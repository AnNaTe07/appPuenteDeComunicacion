package com.softannate.apppuentedecomunicacion.ui.inicio;

import static android.Manifest.permission.CALL_PHONE;
import static androidx.core.content.ContextCompat.checkSelfPermission;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.softannate.apppuentedecomunicacion.R;
import com.softannate.apppuentedecomunicacion.databinding.FragmentInicioBinding;

public class InicioFragment extends Fragment {

    private FragmentInicioBinding binding;
    private TextView facebook;
    private TextView llamada;
    private TextView ubicacion;

    public static InicioFragment newInstance() {
        return new InicioFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentInicioBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //asigno la vista del TextView
        facebook = binding.tvFacebook;

        facebook.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
           //URL de facebook
           String url = "https://www.facebook.com/profile.php?id=100008356088664";
           // Creamos un objeto CustomTabsIntent
           CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();

           // color de la barra
           builder.setToolbarColor(ContextCompat.getColor(requireContext(), R.color.teal_700));
           CustomTabsIntent customTabsIntent = builder.build();

           // Abre la URL en un Custom Tab
           customTabsIntent.launchUrl(requireContext(), Uri.parse(url));
           }

    });

        //asigno la vista del TextView
        llamada = binding.tvLlamada;

        llamada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                solicitarPermiso(); // Solicito el permiso para llamar
            }
        });

        //asigno la vista del TextView
        ubicacion = binding.tvUbicacion;

        ubicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);

                navController.navigate(R.id.map);

            }
        });


    }
        // permiso para realizar llamadas telefónicas
        private void solicitarPermiso () {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && checkSelfPermission(getContext(),CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{CALL_PHONE}, 1000); //permiso de llamada
            } else {
                llamar();
            }
        }

        // Método para realizar la llamada telefónica
        private void llamar () {
            Intent intent = new Intent(Intent.ACTION_CALL); // Creo un intent para llamar
            intent.setData(Uri.parse("tel:2664334839")); // Establezco el número de teléfono
            startActivity(intent); // Inicio la actividad para realizar la llamada

        }

        @Override
        public void onActivityCreated (@Nullable Bundle savedInstanceState){
            super.onActivityCreated(savedInstanceState);
            // TODO: Use the ViewModel
        }

}