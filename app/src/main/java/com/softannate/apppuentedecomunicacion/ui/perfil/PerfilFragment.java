package com.softannate.apppuentedecomunicacion.ui.perfil;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.softannate.apppuentedecomunicacion.databinding.FragmentPerfilBinding;
import com.softannate.apppuentedecomunicacion.modelos.Usuario;

public class PerfilFragment extends Fragment {

    private EditText etNombre, etApellido, etDni, etTelefono, etFechaNacimiento, etDomicilio;
    private TextView tvEmail, tvRol;
    private ImageView fotoPerfil;
    private  PerfilViewModel vmPerfil;
    private FragmentPerfilBinding bindingPerfil;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        bindingPerfil= FragmentPerfilBinding.inflate(getLayoutInflater());
       vmPerfil =  new ViewModelProvider(this).get(PerfilViewModel.class);


        //inicializo campos
        etApellido = bindingPerfil.etApellido;
        etNombre = bindingPerfil.etNombre;
        etDni= bindingPerfil.etDni;
        etDomicilio= bindingPerfil.etDireccion;
        etTelefono= bindingPerfil.etTelefono;
        etFechaNacimiento=bindingPerfil.etFecha;
        tvEmail=bindingPerfil.tvEmail;
        tvRol=bindingPerfil.tvRol;


        vmPerfil.obtenerUsuario();

        //observer p/Usuario
        vmPerfil.getMUsuario().observe(getViewLifecycleOwner(), new Observer<Usuario>() {
            @Override
            public void onChanged(Usuario usuario) {
                etNombre.setText(usuario.getNombre());
                etApellido.setText(usuario.getApellido());
                etDni.setText(String.valueOf(usuario.getDni()));
                etDomicilio.setText(usuario.getDomicilio());
                etTelefono.setText(String.valueOf(usuario.getTelefono()));
                etFechaNacimiento.setText(String.valueOf(usuario.getFechaNacimiento()));
                tvEmail.setText(String.valueOf(usuario.getEmail()));
                tvRol.setText(String.valueOf(usuario.getRol().getNombre()));
            }
        });

        return  bindingPerfil.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bindingPerfil = null;
    }
}