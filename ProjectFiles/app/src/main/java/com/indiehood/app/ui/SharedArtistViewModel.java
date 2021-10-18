package com.indiehood.app.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/*
this view model is used for communication between fragments that use the artist profile view
ex: favorites fragment --> artist profile
ex: show listing --> artist profile
*/

public class SharedArtistViewModel extends ViewModel {
    private MutableLiveData<String> artistPath = new MutableLiveData<>();
    public void setArtistPath(String input) {
        artistPath.setValue(input);
    }
    public LiveData<String> getArtistPath() {
        return artistPath;
    }
    private MutableLiveData<String> artistName = new MutableLiveData<>();
    public void setArtistName(String input) { artistName.setValue(input); }
    public LiveData<String> getArtistName() { return artistName; }
}
