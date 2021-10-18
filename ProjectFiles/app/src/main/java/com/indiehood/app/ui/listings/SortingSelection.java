package com.indiehood.app.ui.listings;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

public class SortingSelection implements AdapterView.OnItemSelectedListener {
    private ListingAdapter listingAdapter;
    public SortingSelection(ListingAdapter listingAdapter) {
        this.listingAdapter = listingAdapter;
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String sortingChoice = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), "Sorting by " + sortingChoice, Toast.LENGTH_SHORT).show();
        listingAdapter.SortListing(sortingChoice);
        listingAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
