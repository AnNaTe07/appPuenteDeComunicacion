package com.softannate.apppuentedecomunicacion.ui.splash;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.softannate.apppuentedecomunicacion.data.local.SpManager;
import com.softannate.apppuentedecomunicacion.data.local.TokenExpirationChecker;

public class SplashActivityViewModel extends AndroidViewModel {

    private String accessToken, refreshToken;
    private MutableLiveData<Boolean> mostrarMain=new MutableLiveData<>();;
    private MutableLiveData<Boolean> mostrarLogin=new MutableLiveData<>();;

    public SplashActivityViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData getMostrarMain() {
        if(mostrarMain==null){
            mostrarMain= new MutableLiveData<>();
        }
        return mostrarMain;
    }

    public LiveData getMostrarLogin() {
        if(mostrarLogin==null){
            mostrarLogin= new MutableLiveData<>();
        }
        return mostrarLogin;
    }
    public void checkToken() {
        refreshToken = SpManager.getRefreshToken(getApplication().getApplicationContext());
        accessToken = SpManager.getAccessToken(getApplication().getApplicationContext());
        if(accessToken!=null && !accessToken.isEmpty() && !TokenExpirationChecker.isTokenExpired(getApplication())){
            mostrarMain.postValue(true);
        }else if(refreshToken!=null && !refreshToken.isEmpty() && !TokenExpirationChecker.isRefreshTokenExpired(getApplication())){
            mostrarMain.postValue(true);
        }else
            mostrarLogin.postValue(true);
    }
}
