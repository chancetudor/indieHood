package com.indiehood.app.ui.listings;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.indiehood.app.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ListingsFragment extends Fragment {

    private ListingsViewModel listingsViewModel;
    private View root;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ListingAdapter mAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        listingsViewModel = new ViewModelProvider(this).get(ListingsViewModel.class);
        root = inflater.inflate(R.layout.fragment_listings, container, false);
        setupRecycler();
        setUpMultiSpinner();
        setupSingleSpinner();
        return root;
    }
    static class DummyVenue {
        public String venueName;
        public String address1;
        public String address2;
        public Double Lat;
        public Double Long;

        DummyVenue(String venueName, String address1, String address2, Double Lat, Double Long) {
            this.venueName = venueName;
            this.address1 = address1;
            this.address2 = address2;
            this.Lat = Lat;
            this.Long = Long;
        }
    }
    private Void setupRecycler() {

        CollectionReference ShowListing = db.collection("ShowListingCol");
        /*
        DummyVenue[] venue = {
                new DummyVenue("Alcove", "730 22nd Ave", "Tuscaloosa, AL 35401", 33.207860, -87.564760),
                new DummyVenue("Copper Top", "2300 4th St", "Tuscaloosa, AL 35401", 33.211418, -87.56800),
                new DummyVenue("Druid City Brewing Company", "607 14th St", "Tuscaloosa, AL 35401",  33.1999747,-87.5437361),
                new DummyVenue("Egan's Bar", "1229 University Blvd", "Tuscaloosa, AL 35401", 33.2109007, -87.554102),
                new DummyVenue("Gallettes", "1021 University Blvd", "Tuscaloosa, AL 35401", 33.2104992,  -87.551923),
                new DummyVenue("Red Shed", "509 Red Drew Ave", "Tuscaloosa, AL 35401", 33.2102821, -87.5544442),
                new DummyVenue("Wheelhouse", "2326 4th St", "Tuscaloosa, AL 35401", 33.2111453,  -87.5677848)
        };
        String[] stuff = {"Banana Rays", "Bonnie Prince Billy", "Four Tet",  "Gears", "Hollow Hand", "Julia Jacklin", "Mapache", "Prince Fatty"};
        Double[] prices = {0.0, 6.50, 9.0, 10.00, 5.00};
        String[][] times = { {"18:00", "20:00"}, {"18:30", "20:30"}, {"22:15", "1:30"}};
        String[][] dates = { {"2020-04-24", "2020-04-24"}, {"2020-04-24", "2020-04-24"}, {"2020-04-25", "2020-04-25"}, {"2020-04-27", "2020-04-28"}, {"2020-07-01", "2020-07-02"}};
        //CollectionReference ShowListing = db.collection("ShowListingCol");
        for (int i = 0;  i < 20; i++) {
            Map<String, Object> data = new HashMap<>();
            data.put("bandName", stuff[ThreadLocalRandom.current().nextInt(stuff.length)]);
            String[] time = times[ThreadLocalRandom.current().nextInt(times.length)];
            data.put("startTime", time[0]);
            data.put("endTime", time[1]);
            String[] date = dates[ThreadLocalRandom.current().nextInt(dates.length)];
            if (Integer.parseInt(time[1].split(":")[0]) < Integer.parseInt(time[0].split(":")[0])) {
                data.put("startDay", date[0]);
                data.put("endDay", date[1]);
             }
             else {
                data.put("startDay", date[0]);
                data.put("endDay", date[0]);
             }
             DummyVenue thatVenue = venue[ThreadLocalRandom.current().nextInt(venue.length)];
            data.put("venueName", thatVenue.venueName);
            data.put("numberInterested", ThreadLocalRandom.current().nextInt(4));
            data.put("price", prices[ThreadLocalRandom.current().nextInt(prices.length)]);
            data.put("address1", thatVenue.address1);
            data.put("address2", thatVenue.address2);
            data.put("addressLat", thatVenue.Lat);
            data.put("addressLong", thatVenue.Long);
            data.put("description", "NOT canceled due to COVID-19 (yet)");
            data.put("showID", "");
            ShowListing.add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    String showID = documentReference.getId();
                    documentReference.update("showID", showID);
                }
            });

        }
        */
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String currentDate = dateFormat.format(date);
        Query query = ShowListing.whereGreaterThan("startDay", currentDate).orderBy("startDay", Query.Direction.ASCENDING);   //change this to a more intelligent retrieval method later
        final FirestoreRecyclerOptions<ShowListing> options = new FirestoreRecyclerOptions.Builder<ShowListing>()
                .setQuery(query, ShowListing.class).build();
        mAdapter = new ListingAdapter(options, this);
        final RecyclerView allListings = root.findViewById(R.id.listings);
        allListings.setHasFixedSize(true);
        allListings.setLayoutManager(new LinearLayoutManager(this.getContext()));
        allListings.setAdapter(mAdapter);
        /*
        mAdapter.setOnItemClickListener(new ListingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //showLists.get(position) resolve clicking on a card in that position
            }
        }); */

        return null;
    }

    private void setUpMultiSpinner() {
        MultiSpinner filter_options = root.findViewById(R.id.filter_options);
        filter_options.addAdapter(mAdapter);
    }

    private void setupSingleSpinner() {
        Spinner sort_options = root.findViewById(R.id.sort_options);
        sort_options.setOnItemSelectedListener(new SortingSelection(mAdapter));
    }


    @Override
    public void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }




}

