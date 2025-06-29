package com.softannate.apppuentedecomunicacion.ui.login;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.softannate.apppuentedecomunicacion.R;
import com.softannate.apppuentedecomunicacion.databinding.ActivityLoginBinding;
import com.softannate.apppuentedecomunicacion.utils.NavigationBarColor;
import com.softannate.apppuentedecomunicacion.utils.TecladoUtils;

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

        vmLogin.getMensaje().observe(this, new Observer<String>(){
            @Override
            public void onChanged(String mensaje) {
                Toast.makeText(LoginActivity.this, mensaje, Toast.LENGTH_SHORT).show();
            }
        });
        vmLogin.getEnviandoMensaje().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                binding.tvOlvidePass.setEnabled(!aBoolean);
            }
        });

        vmLogin.getLimpiar().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                binding.etEmail.setText("");
            }
        });

        binding.btIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TecladoUtils.ocultarTeclado(LoginActivity.this);
                String email= binding.etEmail.getText().toString().trim();
                String password= binding.etPass.getText().toString().trim();
                vmLogin.validarLogin(email, password);
            }
        });

        binding.tvOlvidePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=binding.etEmail.getText().toString().trim();
                vmLogin.validarRestablecerPass(email);
            }
        });

        //agrego subrayado al texto"Olvidé password"
        binding.tvOlvidePass.setPaintFlags(binding.tvOlvidePass.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // Cambio el color de la barra de navegación
        NavigationBarColor.setNavigationBarColor(this, R.color.white);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.nav_login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}