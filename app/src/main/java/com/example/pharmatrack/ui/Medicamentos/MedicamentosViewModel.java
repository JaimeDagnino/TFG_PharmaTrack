package com.example.pharmatrack.ui.Medicamentos;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MedicamentosViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public MedicamentosViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}