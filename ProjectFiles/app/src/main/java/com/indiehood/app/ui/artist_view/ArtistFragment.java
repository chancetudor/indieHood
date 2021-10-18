package com.indiehood.app.ui.artist_view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.indiehood.app.MainActivity;
import com.indiehood.app.R;
import com.indiehood.app.User;
import com.indiehood.app.ui.GlideApp;
import com.indiehood.app.ui.SharedArtistViewModel;
import com.indiehood.app.ui.listings.ShowListing;

public class ArtistFragment extends Fragment {
    // to communicate with FavoritesFragment
    private SharedArtistViewModel viewModel;
    private Observer<String> artistPathObserver;
    private Observer<String> artistNameObserver;
    private Artist artist;
    private String artistName;
    // elements of the artist profile
    private ImageView coverPhoto;
    private TextView bandName;
    private TextView bandBio;
    // for Firestore read/writes
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference ShowListings = db.collection("ShowListingCol");
    private CollectionReference UserCollection = db.collection("UserCol");
    private DocumentReference artistRef;
    private ArtistAdapter adapter;

    class FavoriteButtonClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            favoriteButtonClicked();
        }
    }

    private void favoriteButtonClicked() {
        final User currUser = ((MainActivity) requireActivity()).currentUser;
        final String UID = currUser.getUID();
        final DocumentReference userRef = UserCollection.document(UID);
        final String TAG = "favButtonClicked";
        if (currUser.isArtistFavorited(artist.getArtistName())) {
            currUser.removeFavoritedBand(artist.getArtistName());
            Toast.makeText(getContext(), "Artist unfavorited", Toast.LENGTH_SHORT).show();
        }
        else {
            currUser.addFavoritedBand(artist.getArtistName());
            Toast.makeText(getContext(), "Artist favorited", Toast.LENGTH_SHORT).show();
        }
        db.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) {
                transaction.update(userRef, "favoritedBands", currUser.getFavoritedBands());
                return null;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Transaction failure.", e);
            }
        });
    }

    class TwitterButtonClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            twitterButtonClicked();
        }
    }

    private void twitterButtonClicked() {
        Uri link = Uri.parse(artist.getSocial1());
        if (!artist.getSocial1().equals("")) {
            Intent intent = new Intent(Intent.ACTION_VIEW, link);
            startActivity(intent);
        }
        else {
            Toast.makeText(getContext(), "Error: artist does not have a link provided", Toast.LENGTH_SHORT).show();
        }
    }

    class InstaButtonClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            instaButtonClicked();
        }
    }

    private void instaButtonClicked() {
        Uri link = Uri.parse(artist.getSocial2());
        if (!artist.getSocial2().equals("")) {
            Intent intent = new Intent(Intent.ACTION_VIEW, link);
            startActivity(intent);
        }
        else {
            Toast.makeText(getContext(), "Error: artist does not have a link provided", Toast.LENGTH_SHORT).show();
        }
    }

    class AMButtonClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            appleMusicButtonClicked();
        }
    }

    private void appleMusicButtonClicked() {
        Uri link = Uri.parse(artist.getMedia1());
        if (!artist.getMedia1().equals("")) {
            Intent intent = new Intent(Intent.ACTION_VIEW, link);
            startActivity(intent);
        }
        else {
            Toast.makeText(getContext(), "Error: artist does not have a link provided", Toast.LENGTH_SHORT).show();
        }
    }

    class SpotifyButtonClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            spotifyButtonClicked();
        }
    }

    private void spotifyButtonClicked() {
        Uri link = Uri.parse(artist.getMedia2());
        if (!artist.getMedia2().equals("")) {
            Intent intent = new Intent(Intent.ACTION_VIEW, link);
            startActivity(intent);
        }
        else {
            Toast.makeText(getContext(), "Error: artist does not have a link provided", Toast.LENGTH_SHORT).show();
        }
    }

    public ArtistFragment() {
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_artist_view, container, false);
        // gets band name from bundle
        Bundle bundle = getArguments();
        if (bundle != null) setName((String) bundle.get("docID"));
        else Log.d("ONCREATEVIEW", "Artistname null");

        setAttributes(root);
        pullArtistPath();
        setCoverPhoto();
        setUpRecyclerView(root);

        return root;
    }

    private void setAttributes(View root) {
        bandName = root.findViewById(R.id.band_name);
        bandBio = root.findViewById(R.id.band_bio);
        coverPhoto = root.findViewById(R.id.cover_photo);
        CheckBox favorited = root.findViewById(R.id.favorite_button);
        ImageButton twitter = root.findViewById(R.id.twitter);
        ImageButton instagram = root.findViewById(R.id.instagram);
        ImageButton appleMusic = root.findViewById(R.id.appleMusic);
        ImageButton spotify = root.findViewById(R.id.spotify);
        favorited.setOnClickListener(new FavoriteButtonClick());
        twitter.setOnClickListener(new TwitterButtonClick());
        instagram.setOnClickListener(new InstaButtonClick());
        appleMusic.setOnClickListener(new AMButtonClick());
        spotify.setOnClickListener(new SpotifyButtonClick());
    }

    private void pullArtistPath() {
        artist = new Artist();
        // to communicate with favorites view
        viewModel = new ViewModelProvider(requireActivity()).get(SharedArtistViewModel.class);
        artistPathObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String s) {
                assert s != null;
                artistRef = db.document(s);
                // get data from artist document reference
                artistRef.get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                assert documentSnapshot != null;
                                artist = documentSnapshot.toObject(Artist.class);
                                assert artist != null;
                                bandName.setText(artist.getArtistName());
                                bandBio.setText(artist.getBio());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                String TAG = "artistRef.get()";
                                Log.d(TAG, "Transaction failure", e);
                            }
                        });
            }
        };
    }

    private void setCoverPhoto() {
        viewModel = new ViewModelProvider(requireActivity()).get(SharedArtistViewModel.class);
        artistNameObserver = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                assert s != null;
                artistName = s;
                String fileName = artistName.toLowerCase() + ".jpg";
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference coverPhotoRef = storage.getReference().child("bandCoverPhotos/" + fileName);
                assert getParentFragment() != null;
                GlideApp.with(getParentFragment())
                        .load(coverPhotoRef)
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.abbey_road_2)
                                .error(R.drawable.abbey_road_2)
                                .fallback(R.drawable.abbey_road_2)
                                .fitCenter())
                        .into(coverPhoto);
            }
        };
    }

    private void setUpRecyclerView(View r) {
        String TAG = "setUpRecyclerView";
        Log.d(TAG, "shows for " + artistName);
        final Query query = ShowListings.whereEqualTo("bandName", artistName)
                                        .orderBy("startDay", Query.Direction.ASCENDING).orderBy("startTime", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<ShowListing> options = new FirestoreRecyclerOptions.Builder<ShowListing>()
                .setQuery(query, ShowListing.class)
                .build();

        adapter = new ArtistAdapter(options);

        final RecyclerView artist_shows_rv = r.findViewById(R.id.artist_shows);
        artist_shows_rv.setHasFixedSize(true);
        artist_shows_rv.setLayoutManager(new LinearLayoutManager(getContext()));
        artist_shows_rv.setAdapter(adapter);
    }

    private void setName(String n) {
        this.artistName = n;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
        viewModel.getArtistPath().observe(getViewLifecycleOwner(), artistPathObserver);
        viewModel.getArtistName().observe(getViewLifecycleOwner(), artistNameObserver);
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
