package com.softannate.apppuentedecomunicacion.base;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import com.softannate.apppuentedecomunicacion.utils.TecladoUtils;

public abstract class FragmentBase<T extends ViewModelBase> extends Fragment {

    protected T viewModelBase;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TecladoUtils.configurarOcultadoDeTeclado(view, requireActivity());
        if(viewModelBase != null) {
            configurarObservers(viewModelBase);
        }
    }

    protected void configurarObservers(T viewModel) {
        viewModel.getMensaje().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
            }
        });
    }

}