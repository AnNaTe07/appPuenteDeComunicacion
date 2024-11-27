package com.softannate.apppuentedecomunicacion.ui.comunicados;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.softannate.apppuentedecomunicacion.databinding.FragmentComunicadosBinding;

public class ComunicadosFragment extends Fragment {

    private FragmentComunicadosBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ComunicadosViewModel comunicadosvm =
                new ViewModelProvider(this).get(ComunicadosViewModel.class);

        binding = FragmentComunicadosBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}