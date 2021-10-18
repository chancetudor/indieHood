package com.indiehood.app.ui.report;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ReportViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ReportViewModel() {

        //LiveData<Classname> liveData = new MutableLiveData<Classname>();
        mText = new MutableLiveData<>();
        //mText.setValue("Have an issue with our app, a venue, or an artist? Tell us about it!");
    }

    public LiveData<String> getText() {
        return mText;
    }
}