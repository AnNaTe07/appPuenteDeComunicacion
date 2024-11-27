package com.softannate.apppuentedecomunicacion;

import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.softannate.apppuentedecomunicacion.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private LoginActivityViewModel vmLogin;
    private ActivityLoginBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding= ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        vmLogin= ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(LoginActivityViewModel.class);

        //evento Olvidé pass
        binding.tvOlvidePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=binding.etEmail.getText().toString().trim();
                vmLogin.validarRestablecerPass(email);//método de validación en el ViewModel
            }
        });

        //observer para el mensaje
        vmLogin.getMensaje().observe(this, new Observer<String>(){
            @Override
            public void onChanged(String mensaje) {
                Toast.makeText(LoginActivity.this, mensaje, Toast.LENGTH_SHORT).show();
            }
        });


        //evento para ingresar
        binding.btIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email= binding.etEmail.getText().toString().trim();
                String password= binding.etPass.getText().toString().trim();
                //llamo metodo para validar password
                vmLogin.validarLogin(email, password);
            }
        });

        //agrego subrayado al texto"Olvidé password"
        binding.tvOlvidePass.setPaintFlags(binding.tvOlvidePass.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.nav_login), (v, insets) -> {
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