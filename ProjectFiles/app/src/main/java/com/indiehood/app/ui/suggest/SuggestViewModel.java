package com.indiehood.app.ui.suggest;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SuggestViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public SuggestViewModel() {
        mText = new MutableLiveData<>();
        //mText.setValue("");
    }

    public LiveData<String> getText() {
        return mText;
    }
}