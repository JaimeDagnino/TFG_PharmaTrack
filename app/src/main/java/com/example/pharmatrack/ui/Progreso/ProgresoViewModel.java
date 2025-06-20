package com.example.pharmatrack.ui.Progreso;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProgresoViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ProgresoViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}