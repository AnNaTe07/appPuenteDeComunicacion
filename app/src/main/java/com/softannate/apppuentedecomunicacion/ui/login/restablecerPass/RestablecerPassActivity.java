package com.softannate.apppuentedecomunicacion.ui.login.restablecerPass;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.softannate.apppuentedecomunicacion.databinding.ActivityRestablecerPassBinding;
import com.softannate.apppuentedecomunicacion.ui.login.LoginActivity;

public class RestablecerPassActivity extends AppCompatActivity {

    private  RestablecerPassActivityViewModel vmRestablecerPass;
    private ActivityRestablecerPassBinding binding;
    private String etPass1;
    private String etPass2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRestablecerPassBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        vmRestablecerPass = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(RestablecerPassActivityViewModel.class);

        //observo el mensje desde el viewModel para mostrar un toast cuando cambie
        vmRestablecerPass.getMensaje().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String mensaje) {
                //muestro el mensaje en un toast
                Toast.makeText(RestablecerPassActivity.this, mensaje, Toast.LENGTH_SHORT).show();
            }
        });

        Uri uri= getIntent().getData();//obtengo la URI que fue pasada a la actividad
        binding.navRestablecer.setVisibility(View.VISIBLE);//Hago visible la vista para restablecer el pass
        Log.d("RestablecerPassActivity", "URI received: " + uri.toString());

        //obtengo el token y el email de la URI
        String token = uri.getQueryParameter("token");
        Log.d("RestablecerPassActivity", "Token: " + token);
        String email = uri.getQueryParameter("email");
        Log.d("RestablecerPassActivity", "Email: " + email);

        //paso el email y el token al viewModel para que los maneje
        vmRestablecerPass.setEmail(email);
        vmRestablecerPass.setToken(token);

        vmRestablecerPass.getRedirigirLogin().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                redirigirLogin();
            }
        });

        //listener para el boton para guardar el pass
        binding.btGuardarPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etPass1 = binding.etPassR1.getText().toString().trim();
                etPass2 = binding.etPassR2.getText().toString();
                vmRestablecerPass.validarYRestablecer(etPass1, etPass2);
            }
        });
    }

    private void redirigirLogin() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//se eliminan las activity, sólo la nueva permanece, asi el usuario no puede volver atrás
        startActivity(intent);
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("RestablecerPassActivity", "onNewIntent called");
    }
}