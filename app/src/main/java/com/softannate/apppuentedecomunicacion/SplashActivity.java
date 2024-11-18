package com.softannate.apppuentedecomunicacion;

import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.softannate.apppuentedecomunicacion.modelos.Navegacion;
import com.softannate.apppuentedecomunicacion.utils.NavManager;

public class SplashActivity extends AppCompatActivity {

    private SplashActivityViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        vm=new ViewModelProvider(this).get(SplashActivityViewModel.class);


        //observer para el estado de navegacion
        vm.nav.observe(this, new Observer<Navegacion>() {
            @Override
            public void onChanged(Navegacion navegacion) {
                //lógica de navegacion a navManager
                NavManager.navegarAVista(SplashActivity.this, navegacion);
                finish();//finalizo SplashActivity
            }
        });
        //3 segundos para cerrar
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //validación del estado de usuario
                vm.estadoUsuario();
            }

        },3000);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}