package com.softannate.apppuentedecomunicacion.ui.comunicados;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ComunicadosViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ComunicadosViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}