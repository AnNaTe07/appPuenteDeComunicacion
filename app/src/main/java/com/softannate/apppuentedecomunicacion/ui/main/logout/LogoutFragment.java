package com.softannate.apppuentedecomunicacion.ui.main.logout;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.softannate.apppuentedecomunicacion.R;
import com.softannate.apppuentedecomunicacion.databinding.FragmentLogoutBinding;

public class LogoutFragment extends Fragment {

    private LogoutViewModel vm;
    private FragmentLogoutBinding bindingL;

    public static LogoutFragment newInstance() {
        return new LogoutFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        bindingL = FragmentLogoutBinding.inflate(inflater, container, false);
        vm=new ViewModelProvider(this).get(LogoutViewModel.class);

        return bindingL.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mostrarDialogo();
    }
    private void mostrarDialogo(){
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        SpannableString titulo = new SpannableString("Confirmar salida");
        titulo.setSpan(new ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.teal_700)), 0, titulo.length(), 0);

        SpannableString mensaje = new SpannableString("¿Está seguro que desea salir?");
        mensaje.setSpan(new ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.teal_700)), 0, mensaje.length(), 0);

        builder.setMessage(mensaje);
        builder.setTitle(titulo);
        builder.setPositiveButton("Salir", (dialogInterface, i) -> {
                    vm.logoutEnBackend();
                    requireActivity().finishAffinity();
                })
                .setNegativeButton("Cancelar", (dialogInterface, i) -> {
                    NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
                    if (navController.getCurrentDestination() != null &&
                            navController.getPreviousBackStackEntry() != null) {
                        navController.popBackStack();
                    }
                    dialogInterface.dismiss();
                });

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(dialog.getContext(), R.color.teal_700));
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(dialog.getContext(), R.color.teal_700));
            dialog.getWindow().setBackgroundDrawableResource(android.R.drawable.dialog_holo_light_frame);
        });
        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bindingL = null;
    }
}