package com.softannate.apppuentedecomunicacion.ui.main.nuevoMensaje.tutor;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.softannate.apppuentedecomunicacion.R;

public class NuevoMensajeTutorFragment extends Fragment {

    private NuevoMensajeTutorViewModel mViewModel;

    public static NuevoMensajeTutorFragment newInstance() {
        return new NuevoMensajeTutorFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nuevo_mensaje_tutor, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(NuevoMensajeTutorViewModel.class);
        // TODO: Use the ViewModel
    }

}