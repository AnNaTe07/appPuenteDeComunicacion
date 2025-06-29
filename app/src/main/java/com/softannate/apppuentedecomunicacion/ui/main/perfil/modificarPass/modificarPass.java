package com.softannate.apppuentedecomunicacion.ui.main.perfil.modificarPass;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.softannate.apppuentedecomunicacion.ui.login.LoginActivity;
import com.softannate.apppuentedecomunicacion.databinding.FragmentModificarPassBinding;

public class modificarPass extends Fragment {

    private ModificarPassViewModel vmPass;
    private FragmentModificarPassBinding binding;

    private EditText etPassActual, etPassNueva, etConfirmaPass;

    public static modificarPass newInstance() {
        return new modificarPass();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        vmPass = new ViewModelProvider(this).get(ModificarPassViewModel.class);
        binding = FragmentModificarPassBinding.inflate(inflater, container, false);

        binding.btGuardarPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etPassActual = binding.etPassActual;
                etPassNueva = binding.etNuevaPass;
                etConfirmaPass = binding.etConfirmaPass;

                String passAct= etPassActual.getText().toString();
                String passNueva = etPassNueva.getText().toString();
                String passConfirma = etConfirmaPass.getText().toString();

                vmPass.cambiarPass(passAct, passNueva, passConfirma);

            }
        });
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        vmPass.getRedirigirLogin().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                redirigirLogin();
            }
        });
    }

    private void redirigirLogin() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//elimino las activitys, sólo la nueva permanece, asi el usuario no puede volver atrás
        startActivity(intent);
        getActivity().finish();
    }
}