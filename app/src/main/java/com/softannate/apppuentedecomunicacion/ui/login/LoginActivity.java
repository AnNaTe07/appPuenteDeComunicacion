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

/**
 * Actividad encargada de gestionar la pantalla de inicio de sesión.
 * <p>
 * Maneja la interacción del usuario con los campos de email y contraseña,
 * se comunica con el ViewModel para validar credenciales y muestra mensajes
 * o actualiza la interfaz según el estado del proceso de login.
 */
public class LoginActivity extends AppCompatActivity {

    private LoginActivityViewModel vmLogin;
    private ActivityLoginBinding binding;

    /**
     * Método llamado al crear la actividad.
     * <p>
     * Inicializa el binding, configura ocultado de teclado, observa LiveData del ViewModel
     * y define el comportamiento de los botones de ingreso y restablecer contraseña.
     *
     * @param savedInstanceState estado anterior de la actividad (si existe)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding= ActivityLoginBinding.inflate(getLayoutInflater());
        TecladoUtils.configurarOcultadoDeTeclado(binding.getRoot(), LoginActivity.this);

        setContentView(binding.getRoot());

        vmLogin= ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(LoginActivityViewModel.class);

        //observer para mostrar mensaje en toast
        vmLogin.getMensaje().observe(this, new Observer<String>(){
            @Override
            public void onChanged(String mensaje) {
                Toast.makeText(LoginActivity.this, mensaje, Toast.LENGTH_SHORT).show();
            }
        });

        // Habilita o deshabilita botón "Olvidé password" según estado
        vmLogin.getEnviandoMensaje().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                binding.tvOlvidePass.setEnabled(!aBoolean);
            }
        });

        // Limpia campo de email si lo indica el ViewModel
        vmLogin.getLimpiar().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                binding.etEmail.setText("");
            }
        });

        // Acción del botón de ingreso
        binding.btIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TecladoUtils.ocultarTeclado(LoginActivity.this);
                String email= binding.etEmail.getText().toString().trim();
                String password= binding.etPass.getText().toString().trim();
                vmLogin.validarLogin(email, password);
            }
        });

        // Acción del botón "Olvidé password"
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
        NavigationBarColor.setNavigationBarColor(this, R.color.white, R.color.white);

        // Ajusta padding según barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.nav_login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}