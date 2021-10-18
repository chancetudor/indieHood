package com.indiehood.app.ui.listings;

import android.annotation.SuppressLint;
import android.content.Intent;

import android.database.Observable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.ObservableSnapshotArray;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.indiehood.app.MainActivity;
import com.indiehood.app.R;
import com.indiehood.app.User;
import com.indiehood.app.ui.show.ShowFragment;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ListingAdapter extends FirestoreRecyclerAdapter<ShowListing, ListingAdapter.ListingHolder> {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<String> filters;
    private String sort;
    private OnItemClickListener mListener;
    private ListingsFragment fragment;
    private User currentUser;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public ListingAdapter(@NonNull FirestoreRecyclerOptions<ShowListing> options, ListingsFragment fragment) {
        super(options);
        this.filters = new ArrayList<>();
        this.sort = "Date: Soonest First";
        this.fragment = fragment;
        this.currentUser = ((MainActivity) fragment.requireActivity()).currentUser;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull final ListingHolder holder, final int position, @NonNull final ShowListing model) {
        model.formatValues();
        holder.mTextBandName.setText(model.getBandName());
        holder.mTextVenue.setText("@ " + model.getVenueName());
        holder.mTextMonth.setText(model.dateMonth);
        holder.mTextDay.setText(model.dateDay);
        holder.mTimeStart.setText(model.startTimeFormatted);
        holder.mInterestedText.setText(model.getInterestedText());
        if (currentUser.getFavoritedBands().contains(model.getBandName())) {
            holder.mBandFavorited.setVisibility(View.VISIBLE);
        }
        else {
            holder.mBandFavorited.setVisibility(View.GONE);
        }
        if (currentUser.getInterestedShows().contains(model.getShowID())) {
            holder.mUserInterested.setChecked(true);
        }
        else {
            holder.mUserInterested.setChecked(false);
        }

        holder.mUserInterested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.mUserInterested.isChecked()) {       //chose to be interested
                    holder.mUserInterested.setClickable(false);     //this prevents the logic from manipulation during processing
                    System.out.println("CHECKED NOW");
                    String docID = currentUser.getUID();
                    currentUser.getInterestedShows().add(model.getShowID());
                    db.collection("UserCol").document(docID).update("interestedShows", currentUser.getInterestedShows());
                    int newInterested = model.getNumberInterested() + 1;
                    updateInterested(position, newInterested);
                    holder.mUserInterested.setClickable(true);
                }
                else {  //chose not to be interested
                    holder.mUserInterested.setClickable(false);
                    String docID = currentUser.getUID();
                    currentUser.getInterestedShows().remove(model.getShowID());
                    db.collection("UserCol").document(docID).update("interestedShows", currentUser.getInterestedShows());
                    int newInterested = model.getNumberInterested() - 1;
                    updateInterested(position, newInterested);
                    System.out.println("NOT CHECKED");
                    holder.mUserInterested.setClickable(true);
                }
            }
            public void updateInterested(int position, int newInterested) {
                String docID = ListingAdapter.super.getSnapshots().getSnapshot(position).getId();
                db.collection("ShowListingCol").document(docID).update("numberInterested", newInterested);
                ListingAdapter.super.bindViewHolder(holder, position);
            }
        });

    }

    @NonNull
    @Override
    public ListingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_listing, parent, false);
        return new ListingHolder(v);
    }

    private void UpdateListings() {
        System.out.println("Starting Reload");
        final CollectionReference ShowListing = db.collection("ShowListingCol");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = new Date();
        String formattedCurrent = dateFormat.format(currentDate);
        Query result = null;
        if (this.filters.contains("Favorited: Bands")) {
            if (result != null) {
                result = result.whereIn("bandName", currentUser.getFavoritedBands());
            }
            else {
                result = ShowListing.whereIn("bandName", currentUser.getFavoritedBands());
            }
        }
        if (this.filters.contains("Price: Free")) {
            if (result != null){
                result = result.whereEqualTo("price", 0);
            }
            else {
                result = ShowListing.whereEqualTo("price", 0);
            }
        }
        if (this.filters.contains("Interested: Yes")) {
            if (result != null) {
                result = result.whereIn("showID", currentUser.getInterestedShows());
            }
            else {
                result = ShowListing.whereIn("showID", currentUser.getInterestedShows());
            }
        }
        if (this.filters.contains("Date: Within 1 Day")) {
            Calendar d = Calendar.getInstance();
            d.setTime(currentDate);
            d.add(Calendar.DATE, 1);
            Date currentDatePlusDay = d.getTime();
            String formattedDay = dateFormat.format(currentDatePlusDay);
            System.out.println(formattedDay);
            if (result != null){
                result = result.whereLessThanOrEqualTo("startDay", formattedDay);
            }
            else {
                result = ShowListing.whereLessThanOrEqualTo("startDay", formattedDay);
            }
        }
        else if (this.filters.contains("Date: Within 1 Week")) {
            Calendar c = Calendar.getInstance();
            c.setTime(currentDate);
            c.add(Calendar.DATE, 7);
            Date currentDatePlusWeek = c.getTime();
            String formattedWeek = dateFormat.format(currentDatePlusWeek);
            if (result != null){
                result = result.whereLessThanOrEqualTo("startDay", formattedWeek);
            }
            else {
                result = ShowListing.whereLessThanOrEqualTo("startDay", formattedWeek);
            }
        }

        if (result == null && sort.contains("Date: Soonest First")) {
            result = ShowListing.whereGreaterThan("startDay", formattedCurrent).orderBy("startDay", Query.Direction.ASCENDING).orderBy("startTime", Query.Direction.ASCENDING);
        }
        else if (result == null && sort.contains("Price: Cheapest First") && !filters.contains("Price: Free")) {
            result = ShowListing.whereGreaterThan("price", -1).orderBy("price", Query.Direction.ASCENDING);
        }
        else if (result == null && sort.contains("Interested: Most First")) {
            result = ShowListing.whereGreaterThan("numberInterested", -1).orderBy("numberInterested", Query.Direction.DESCENDING);
        }
        else if (this.filters.contains("Date: Within 1 Day") || this.filters.contains("Date: Within 1 Week")) {
            System.out.println("HERE");
            result =  result.orderBy("startDay", Query.Direction.ASCENDING).orderBy("startTime", Query.Direction.ASCENDING);
        }
        else if (this.filters.contains("Favorited: Bands")) {
            result = result.whereGreaterThan("startDay", formattedCurrent).orderBy("startDay", Query.Direction.ASCENDING).orderBy("startTime", Query.Direction.ASCENDING);
        }
        else if (!this.filters.contains("Price: Free") && !this.filters.contains("Interested: Yes")){
            result = result.orderBy("price", Query.Direction.ASCENDING);
        }
        else {
            result =  result.whereGreaterThan("startDay", formattedCurrent).orderBy("startDay", Query.Direction.ASCENDING).orderBy("startTime", Query.Direction.ASCENDING);
        }
        ListingAdapter.super.updateOptions(new FirestoreRecyclerOptions.Builder<ShowListing>().setQuery(result, ShowListing.class).build());
        System.out.println("Done Reloading");
    }

    public void FilterListing(ArrayList<String> chosenFilters) {
        this.filters = chosenFilters;
        this.UpdateListings();
    }

    public void SortListing(String sortingChoice) {
        this.sort = sortingChoice;
        this.UpdateListings();
    }

    class ListingHolder extends RecyclerView.ViewHolder {
        public TextView mTextBandName;
        public TextView mTextVenue;
        public TextView mTextDay;
        public TextView mTextMonth;
        public TextView mTimeStart;
        public TextView mInterestedText;
        public ImageView mBandFavorited;
        public CheckBox mUserInterested;

        public ListingHolder(@NonNull final View itemView) {
            super(itemView);
            mTextBandName = itemView.findViewById(R.id.bandName);
            mTextVenue = itemView.findViewById(R.id.venue);
            mTextDay = itemView.findViewById(R.id.day);
            mTextMonth = itemView.findViewById(R.id.month);
            mBandFavorited = itemView.findViewById(R.id.bandFavorited);
            mUserInterested = itemView.findViewById(R.id.interested);
            mInterestedText = itemView.findViewById(R.id.interested_text);
            mTimeStart = itemView.findViewById(R.id.time);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    ObservableSnapshotArray<ShowListing> listing = ListingAdapter.super.getSnapshots();
                    ShowListing selected = listing.get(position);
                    String docID = listing.getSnapshot(position).getId();
                    ShowFragment show = new ShowFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("selected", selected);
                    bundle.putString("docID", docID);
                    Navigation.findNavController(v).navigate(R.id.nav_full_show, bundle);
                }
            });
        }
    }
}

