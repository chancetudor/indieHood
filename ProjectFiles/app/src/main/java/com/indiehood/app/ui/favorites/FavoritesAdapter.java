package com.indiehood.app.ui.favorites;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.indiehood.app.MainActivity;
import com.indiehood.app.R;
import com.indiehood.app.User;
import com.indiehood.app.ui.GlideApp;
import com.indiehood.app.ui.artist_view.Artist;

// implements a recycler view using data pulled directly from firestore
public class FavoritesAdapter extends FirestoreRecyclerAdapter<Artist, FavoritesAdapter.FavoritesHolder> {
    private OnFavoriteClickListener favoriteClickListener;
    private OnArtistClickListener artistClickListener;
    private Context context;
    private FavoritesFragment fragment;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private User currentUser;
    // this interface and its public function are callbacks for favorite click listener
    public interface OnFavoriteClickListener {
        void onFavoriteClick(DocumentSnapshot snapshot, int position);
    }
    void setOnFavoriteClickListener(OnFavoriteClickListener listener) {
        this.favoriteClickListener = listener;
    }
    // this interface and pub function are callbacks for whole card click
    public interface OnArtistClickListener {
        void onArtistClick(DocumentSnapshot snapshot, int position);
    }
    void setOnArtistClickListener(OnArtistClickListener listener) {
        this.artistClickListener = listener;
    }

    FavoritesAdapter(@NonNull FirestoreRecyclerOptions<Artist> options, FavoritesFragment fragment) {
        super(options);
        MainActivity activity = (MainActivity)fragment.getActivity();
        assert activity != null;
        this.fragment = fragment;
        this.currentUser = activity.currentUser;
     }

    class FavoritesHolder extends RecyclerView.ViewHolder {
        TextView artistName;
        TextView artistBio;
        ImageView artistIcon;
        CheckBox favorite;
        RelativeLayout artistCard;

        FavoritesHolder(final View itemView) {
            super(itemView);
            // initialize the items to appear in each card in recycler view
            artistCard = itemView.findViewById(R.id.fav_card);
            artistIcon = itemView.findViewById(R.id.band_venue_icon);
            artistName = itemView.findViewById(R.id.band_venue_name);
            artistBio = itemView.findViewById(R.id.band_venue_description);
            favorite = itemView.findViewById(R.id.favorite_button);
            // sets on click listener for the favorite button
            // if clicked, pass document snapshot & its position to function in FavoritesFragment
            favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Artist selected = FavoritesAdapter.super.getSnapshots().get(position);

                    final String UID = currentUser.getUID();
                    final DocumentReference userRef = db.collection("UserCol").document(UID);
                    currentUser.removeFavoritedBand(selected.getArtistName());
                    db.runTransaction(new Transaction.Function<Void>() {
                        @Nullable
                        @Override
                        public Void apply(@NonNull Transaction transaction) {
                            transaction.update(userRef, "favoritedBands", currentUser.getFavoritedBands());

                            return null;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //  Log.w(TAG, "Transaction failure.", e);
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            refreshFavorites();
                        }
                    });
                    Toast.makeText(fragment.getContext(), "Artist unfavorited", Toast.LENGTH_SHORT).show();

                }
            });
                    /*
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && favoriteClickListener != null) {
                      //  favoriteClickListener.onFavoriteClick(getSnapshots().getSnapshot(position), position, adapter);

                    } */

            // sets on click listener for when a card is clicked
            artistCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && artistClickListener != null) {
                        artistClickListener.onArtistClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public FavoritesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View favoriteView = inflater.inflate(R.layout.favorited_band_row, parent, false);
        context = parent.getContext();

        return new FavoritesHolder(favoriteView);
    }

    @Override
    protected void onBindViewHolder(@NonNull final FavoritesHolder viewHolder, final int position,
                                    @NonNull Artist currArtist) {
        String fileName = currArtist.getArtistName().toLowerCase() + ".jpg";
        StorageReference proPicRef = storage.getReference().child("bandProfilePictures/" + fileName);
        if (currArtist.getArtistName() != null) {
            viewHolder.artistName.setText(currArtist.getArtistName());
        }
        if (currArtist.getBio() != null) { viewHolder.artistBio.setText(currArtist.getBio()); }
        // set artist icon
        GlideApp.with(context)
                .load(proPicRef)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.band_venue_icon)
                        .error(R.drawable.band_venue_icon)
                        .fallback(R.drawable.band_venue_icon)
                        .fitCenter())
                .into(viewHolder.artistIcon);
        if (currArtist.getFavorited()) {
            viewHolder.favorite.setEnabled(currArtist.getFavorited());
        }
    }

    @Override
    public void onDataChanged() {
       // emptyList.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    private void refreshFavorites() {
        if (currentUser.getFavoritedBands().size() == 0) {
            fragment.emptyList.setVisibility(View.VISIBLE);
            fragment.favoritesList.setVisibility(View.GONE);
        } else {
            fragment.favoritesList.setVisibility(View.VISIBLE);
            final Query query = db.collection("ArtistCollection").whereIn("artistName", currentUser.getFavoritedBands());
            // add listener to see if there are no favorites to show
            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                    @Nullable FirebaseFirestoreException e) {
                    if (queryDocumentSnapshots != null && queryDocumentSnapshots.isEmpty()) {
                        fragment.emptyList.setVisibility(View.VISIBLE);
                        fragment.favoritesList.setVisibility(View.GONE);
                    }
                }
            });

            FirestoreRecyclerOptions<Artist> options = new FirestoreRecyclerOptions.Builder<Artist>()
                    .setQuery(query, Artist.class)
                    .build();
            FavoritesAdapter.super.updateOptions(options);
        }
    }
}
