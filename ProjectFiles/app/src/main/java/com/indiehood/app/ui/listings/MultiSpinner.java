package com.indiehood.app.ui.listings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import androidx.appcompat.app.AlertDialog;
import com.indiehood.app.R;

import java.util.ArrayList;
import java.util.Arrays;


class MultiSpinner extends androidx.appcompat.widget.AppCompatSpinner {

    private CharSequence[] entries;
    private boolean[] selected;
    private String defaultText;
    private MultiSpinnerListener listener;
    private ListingAdapter listingAdapter;

    public MultiSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MultiSpinner);
        CharSequence[] allEntries = a.getTextArray(R.styleable.MultiSpinner_android_entries);
        entries = Arrays.copyOfRange(allEntries, 1, allEntries.length);
        if (entries != null) {
            selected = new boolean[entries.length]; // false-filled by default
        }
        a.recycle();
    }


    private DialogInterface.OnMultiChoiceClickListener mOnMultiChoiceClickListener = new DialogInterface.OnMultiChoiceClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
            selected[which] = isChecked;

        }
    };

    private DialogInterface.OnClickListener mOnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            // build new spinner text & delimiter management
            ArrayList<String> chosenFilters = new ArrayList<String>();
            for (int i = 0; i < entries.length; i++) {
                if (selected[i]) {
                    chosenFilters.add(entries[i].toString());
                }
            }

            if (listener != null) {
                listener.onItemsSelected(selected);
            }
            listingAdapter.FilterListing(chosenFilters);
            // hide dialog
            dialog.dismiss();
        }
    };

    @Override
    public boolean performClick() {
        new AlertDialog.Builder(getContext())
                .setMultiChoiceItems(entries, selected, mOnMultiChoiceClickListener)
                .setPositiveButton(R.string.filter, mOnClickListener)
                .show();
        return true;
    }

    //public void setMultiSpinnerListener(MultiSpinnerListener listener) {
   //     this.listener = listener;
   // }

    public interface MultiSpinnerListener {
        public void onItemsSelected(boolean[] selected);
    }

    public void addAdapter(ListingAdapter adapter) {
        this.listingAdapter = adapter;
    }


}
