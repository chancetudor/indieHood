package com.indiehood.app.ui.favorites;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.indiehood.app.MainActivity;
import com.indiehood.app.R;
import com.indiehood.app.User;
import com.indiehood.app.ui.SharedArtistViewModel;
import com.indiehood.app.ui.artist_view.Artist;

public class FavoritesFragment extends Fragment {
    // to communicate with artist view
    private SharedArtistViewModel viewModel;
    // firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference ArtistCollection = db.collection("ArtistCollection");
    private CollectionReference UserCollection = db.collection("UserCol");
    private FavoritesAdapter adapter;
    private User currUser;
    public TextView emptyList;
    public RecyclerView favoritesList;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_favorites, container, false);
        this.currUser = ((MainActivity) requireActivity()).currentUser;
        emptyList = root.findViewById(R.id.empty_rv);
        favoritesList = root.findViewById(R.id.favorites_rv);
        viewModel = new ViewModelProvider(requireActivity()).get(SharedArtistViewModel.class);
        setUpRecyclerView(root);

        return root;
    }

    private void setUpRecyclerView(View r) {
        if (currUser.getFavoritedBands().size() == 0) {
            emptyList.setVisibility(View.VISIBLE);
        }
        else {
            final Query query = ArtistCollection.whereIn("artistName", currUser.getFavoritedBands());
            // add listener to see if there are no favorites to show
            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                    @Nullable FirebaseFirestoreException e) {
                    if (queryDocumentSnapshots != null && queryDocumentSnapshots.isEmpty()) {
                        emptyList.setVisibility(View.VISIBLE);
                    }
                }
            });

            FirestoreRecyclerOptions<Artist> options = new FirestoreRecyclerOptions.Builder<Artist>()
                    .setQuery(query, Artist.class)
                    .build();
            // pass empty list for OnDataChanged() method to use if no favorites populated
            adapter = new FavoritesAdapter(options, this);
            final RecyclerView favorites_rv = r.findViewById(R.id.favorites_rv);
            favorites_rv.setHasFixedSize(true);
            favorites_rv.setLayoutManager(new LinearLayoutManager(this.getContext()));
            favorites_rv.setAdapter(adapter);

            // for when the favorite button is clicked to unfavorite artist
            /*
            adapter.setOnFavoriteClickListener(new FavoritesAdapter.OnFavoriteClickListener() {
                final String TAG = "onFavClick";
                @Override
                public void onFavoriteClick(DocumentSnapshot snapshot, int position) {
                    final User currUser = ((MainActivity) requireActivity()).currentUser;
                    final String UID = currUser.getUID();
                    final DocumentReference userRef = UserCollection.document(UID);
                    Artist artist = snapshot.toObject(Artist.class);
                    assert artist != null;
                    currUser.removeFavoritedBand(artist.getArtistName());
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
                    Toast.makeText(getContext(), "Artist unfavorited", Toast.LENGTH_SHORT).show();
                }
            }); */

            // for when the user clicks an artist entirely
             adapter.setOnArtistClickListener(new FavoritesAdapter.OnArtistClickListener() {
                @Override
                public void onArtistClick(DocumentSnapshot snapshot, int position) {
                    assert snapshot != null;
                    String path = snapshot.getReference().getPath();
                    String name = snapshot.getId();
                    viewModel.setArtistPath(path);
                    viewModel.setArtistName(name);
                    Bundle bundle = new Bundle();
                    bundle.putString("docID", name);
                    Navigation.findNavController(requireView()).navigate(R.id.nav_artist_view, bundle);
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (currUser.getFavoritedBands().size() != 0) adapter.startListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (currUser.getFavoritedBands().size() != 0) adapter.stopListening();
    }
}
