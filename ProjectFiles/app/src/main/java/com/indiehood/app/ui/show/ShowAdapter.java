package com.indiehood.app.ui.show;

import android.content.Context;
import android.view.View;

import android.widget.Adapter;
import android.widget.AdapterView;

import com.indiehood.app.ui.listings.ShowListing;

public class ShowAdapter extends AdapterView {
    ShowListing show;
    public ShowAdapter(Context context, ShowListing show) {
        super(context);
        this.show = show;
        //might be able to do all of this in the fragment??? we'll see
    }


    @Override
    public Adapter getAdapter() {
        return null;
    }

    @Override
    public void setAdapter(Adapter adapter) {

    }

    @Override
    public View getSelectedView() {
        return null;
    }

    @Override
    public void setSelection(int position) {

    }
}