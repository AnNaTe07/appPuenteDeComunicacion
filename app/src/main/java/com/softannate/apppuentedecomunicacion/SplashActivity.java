package com.softannate.apppuentedecomunicacion;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

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
                // Lógica de navegación a NavManager para que se encargue de la navegación
                NavManager.navegarAVista(SplashActivity.this, navegacion);

                finish();//finalizo SplashActivity
            }
        });

        // Observer para el mensaje de bienvenida
        vm.getMensaje().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String mensaje) {
                // Mostrar el mensaje en un Toast cada vez que el valor de 'mensaje' cambie
                Toast.makeText(SplashActivity.this, mensaje, Toast.LENGTH_SHORT).show();
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

        // Cambiar el color de la barra de navegación desde código
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.white));
        }
    }
}