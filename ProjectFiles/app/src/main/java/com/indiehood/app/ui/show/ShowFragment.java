package com.indiehood.app.ui.show;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.indiehood.app.MainActivity;
import com.indiehood.app.R;
import com.indiehood.app.User;
import com.indiehood.app.ui.SharedArtistViewModel;
import com.indiehood.app.ui.listings.ShowListing;

import java.util.Calendar;

public class ShowFragment extends Fragment implements OnMapReadyCallback {
    private ShowListing show;
    private GoogleMap mMap;
    private ShowAdapter mAdapter;
    private View root;
    private String docID;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference ArtistCollection = db.collection("ArtistCollection");
    private User currentUser;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        this.currentUser = ((MainActivity) requireActivity()).currentUser;
        assert bundle != null;
        show = (ShowListing) bundle.getSerializable("selected");
        docID = (String) bundle.get("docID");
        root = inflater.inflate(R.layout.fragment_full_show, container, false);
        setupAdapter();
        injectData();
        setupCalendar();
        setupArtistLink();
        setupInterested();
        setupMapIntegration();
        return root;
    }


    private void setupMapIntegration() {
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        mapFragment.getMapAsync(this);
        getChildFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();
        System.out.println("DONE");
    }

    private void setupAdapter() {
        mAdapter = new ShowAdapter(this.getContext(), show);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        double lat = show.getAddressLat();
        double log = show.getAddressLong();
        LatLng venue = new LatLng(lat, log);
        mMap.addMarker(new MarkerOptions().position(venue).title(show.getVenueName()));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(venue, 15));
    }

    @SuppressLint("SetTextI18n")
    private void injectData() {
        TextView bandName = root.findViewById(R.id.bandName);
        TextView interested = root.findViewById(R.id.full_interested_count);
        TextView price = root.findViewById(R.id.full_show_price);
        TextView date = root.findViewById(R.id.full_date);
        TextView time = root.findViewById(R.id.time);
        TextView address1 = root.findViewById(R.id.address1);
        TextView address2 = root.findViewById(R.id.address2);
        TextView description = root.findViewById(R.id.show_description);
        time.setText(show.startTimeFormatted + " to " + show.endTimeFormatted);
        bandName.setText(show.getBandName());
        interested.setText(Integer.toString(show.getNumberInterested()));
        price.setText(show.priceFormatted);
        date.setText(show.dateMonth + " " + show.dateDay + " " + show.dateYear);
        address1.setText(show.getAddress1());
        address2.setText(show.getAddress2());
        description.setText(show.getDescription());
        ImageView favorites = root.findViewById(R.id.bandFavorited);
        if (currentUser.getFavoritedBands().contains(show.getBandName())) {
            favorites.setVisibility(View.VISIBLE);
        }
        else {
            favorites.setVisibility(View.GONE);
        }
    }

    private void setupCalendar() {
        Button addToCalendar = root.findViewById(R.id.addToCalendar);
        addToCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar beginTime = Calendar.getInstance();
                String[] start = show.getStartTime().split(":");
                String starthour = start[0];
                String startminute = start[1].split(" ")[0];
                String month = show.getStartDay().split("-")[1];
                String day = show.getStartDay().split("-")[2];
                beginTime.set(Integer.parseInt(show.dateYear), Integer.parseInt(month), Integer.parseInt(day), Integer.parseInt(starthour), Integer.parseInt(startminute));
                String[] end = show.getEndTime().split(":");
                String endhour = end[0];
                String endminute = end[1].split(" ")[0];
                Calendar endTime = Calendar.getInstance();
                if (!show.getStartDay().equals(show.getEndDay())) {
                    month = show.getEndDay().split("-")[1];
                    day = show.getEndDay().split("-")[2];
                }
                endTime.set(Integer.parseInt(show.dateYear), Integer.parseInt(month), Integer.parseInt(day), Integer.parseInt(endhour), Integer.parseInt(endminute));
                Button interested = root.findViewById(R.id.interested);
                if ("I'm Interested".contains(interested.getText())) {
                    interested.performClick();
                }
                Intent intent = new Intent(Intent.ACTION_INSERT)
                        .setData(CalendarContract.Events.CONTENT_URI)
                        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                        .putExtra(CalendarContract.Events.TITLE,  show.getBandName() + " @ " + show.getVenueName())
                        .putExtra(CalendarContract.Events.DESCRIPTION, show.getDescription() + "\n\n" + "Discovered through indieHood")
                        .putExtra(CalendarContract.Events.EVENT_LOCATION, show.getAddress1() + " " + show.getAddress2())
                        .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
                startActivity(intent);
            }
        });
    }

    private void setupInterested() {
        final Button interested = root.findViewById(R.id.interested);
        if (currentUser.getInterestedShows().contains(show.getShowID())) {
            interested.setText("Not Interested");
        }
        else {
            interested.setText("I'm Interested");
        }
        interested.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {

                if ("I'm Interested".contains(interested.getText())) {       //chose to be interested
                    System.out.println("CHECKED NOW");

                    String docID = currentUser.getUID();
                    currentUser.getInterestedShows().add(show.getShowID());
                    db.collection("UserCol").document(docID).update("interestedShows", currentUser.getInterestedShows());
                    int newInterested = show.getNumberInterested() + 1;
                    updateInterested(newInterested);
                    interested.setText("Not Interested");
                }
                else {  //chose not to be interested
                    System.out.println("NOT CHECKED");
                    String docID = currentUser.getUID();
                    currentUser.getInterestedShows().remove(show.getShowID());
                    db.collection("UserCol").document(docID).update("interestedShows", currentUser.getInterestedShows());
                    int newInterested = show.getNumberInterested() - 1;
                    updateInterested(newInterested);
                    interested.setText("I'm Interested");

                }
            }

        });

    }

    public void updateInterested(int newInterested) {
        db.collection("ShowListingCol").document(docID).update("numberInterested", newInterested);
        TextView interested = root.findViewById(R.id.full_interested_count);
        show.setNumberInterested(newInterested);
        interested.setText(Integer.toString(newInterested));
    }

    private void setupArtistLink() {
        LinearLayout topBar = root.findViewById(R.id.top_bar);
        final String artistPath = "ArtistCollection/" + show.getBandName();
        final SharedArtistViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedArtistViewModel.class);
                topBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.setArtistPath(artistPath);
                viewModel.setArtistName(show.getBandName());
                Bundle bundle = new Bundle();
                bundle.putString("docID", show.getBandName());
                FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
              //  ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
               // ft.replace(R.id.full_show, artistFragment);
                ft.addToBackStack(null);
               // ft.commit();
                Navigation.findNavController(requireView()).navigate(R.id.nav_artist_view, bundle);
            }
        });
    }
}
