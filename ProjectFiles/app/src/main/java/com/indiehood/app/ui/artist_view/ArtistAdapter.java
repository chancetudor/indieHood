package com.indiehood.app.ui.artist_view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.indiehood.app.R;
import com.indiehood.app.ui.listings.ShowListing;

// implements a recycler view using data pulled directly from firestore
public class ArtistAdapter extends FirestoreRecyclerAdapter<ShowListing, ArtistAdapter.ShowHolder> {
    ArtistAdapter(@NonNull FirestoreRecyclerOptions<ShowListing> options) {
        super(options);
    }

    static class ShowHolder extends RecyclerView.ViewHolder {
        TextView mTextVenue;
        TextView mTextDay;
        TextView mTextMonth;
        TextView mTimeStart;

        ShowHolder(View itemView) {
            super(itemView);
            mTextVenue = itemView.findViewById(R.id.venue);
            mTextDay = itemView.findViewById(R.id.day);
            mTextMonth = itemView.findViewById(R.id.month);
            mTimeStart = itemView.findViewById(R.id.time);
        }
    }

    @NonNull
    @Override
    public ShowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.artist_show_listing, parent, false);

        return new ShowHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull final ShowHolder viewHolder, int position,
                                    @NonNull final ShowListing listing) {
        listing.formatValues();
        viewHolder.mTextVenue.setText(listing.getVenueName());
        viewHolder.mTextMonth.setText(listing.dateMonth);
        viewHolder.mTextDay.setText(listing.dateDay);
        viewHolder.mTimeStart.setText(listing.startTimeFormatted);
    }
}
