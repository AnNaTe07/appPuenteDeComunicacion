package com.softannate.apppuentedecomunicacion.ui.main.mensaje.conversacion;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.softannate.apppuentedecomunicacion.R;
import com.softannate.apppuentedecomunicacion.base.FragmentNuevoConversacion;
import com.softannate.apppuentedecomunicacion.databinding.FragmentConversacionBinding;
import com.softannate.apppuentedecomunicacion.modelos.dto.MensajeDTO;
import java.util.ArrayList;
import java.util.List;

public class ConversacionFragment extends FragmentNuevoConversacion {

    private ConversacionViewModel vmConversacion;
    private ConversacionAdapter adapter;
    private FragmentConversacionBinding binding;
    private RecyclerView rvConversaciones;
    private List<MensajeDTO> listaMensajes;
    private static final int TIEMPO_ESPERA = 4000;
/*
    public static ConversacionFragment newInstance(String avatar,String userNombre, int userId, int alumnoID, String alumnoNombre) {
        ConversacionFragment fragment = new ConversacionFragment();
        Bundle args= new Bundle();
        args.putInt("userId", userId);
        args.putInt("alumnoId", alumnoID);
        args.putString("alumnoNombre", alumnoNombre);
        args.putString("avatar",avatar);
        args.putString("userNombre", userNombre);
        fragment.setArguments(args);
        return  fragment;
    }

 */

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding= FragmentConversacionBinding.inflate(inflater, container, false);
        vmConversacion= new ViewModelProvider(this).get(ConversacionViewModel.class);
/*
        vmConversacion.getEnviandoMensaje().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                binding.layoutEscribir.btnEnviar.setEnabled(!aBoolean);
            }
        });

 */

        //inicializo lista de mensajes
        listaMensajes=new ArrayList<>();
        //adapter = new ConversacionAdapter(listaMensajes, vmConversacion.getIdVisual());

       // adapter= new ConversacionAdapter(listaMensajes);
        LinearLayoutManager llConversaciones= new LinearLayoutManager(requireContext());
        llConversaciones.setStackFromEnd(true);
        llConversaciones.setReverseLayout(false);
        rvConversaciones= binding.recyclerMensajes;
        rvConversaciones.setLayoutManager(llConversaciones);
        rvConversaciones.setAdapter(adapter);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       // this.tvCategoria = binding.layoutEscribir.tvCategoria;
       // this.editMensaje = binding.layoutEscribir.editMensaje;

        Bundle args = getArguments();
        if(args == null) return;

        vmConversacion.getLimpiar().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                Log.d("ConversacionFragment", "getLimpiar activado");
                limpiarCampos();
                vmConversacion.cargarMensajes();
            }
        });

        String nombreUsuario = getArguments().getString("userNombre");
        String alumnoNombre = getArguments().getString("alumnoNombre");

        binding.nombreUsuario.setText(nombreUsuario + " " + alumnoNombre );

        Glide.with(requireContext())
                .load(getArguments().getString("avatar"))
                .placeholder(R.drawable.cargando)//imagen temporal mientras se carga
                .diskCacheStrategy(DiskCacheStrategy.NONE)//desactivo la cache
                .skipMemoryCache(true)
                .error(R.drawable.perfil_user)
                .into(binding.fotoUsuario);


       // int alumnoId= getArguments().getInt("alumnoId");
        //int userId= getArguments().getInt("userId");
        //vmConversacion.setIds( userId,alumnoId);
        //Log.d("ViewModelResponse1", alumnoId + " " + userId);

        vmConversacion.inicializarDesdeBundle(requireContext(), args);
        vmConversacion.cargarMensajes();
        vmConversacion.getListaMensaje().observe(getViewLifecycleOwner(), new Observer<List<MensajeDTO>>() {
          @Override
          public void onChanged(List<MensajeDTO> mensajeDTOS) {
              if (adapter == null) {
                  adapter = new ConversacionAdapter(mensajeDTOS, vmConversacion.getIdVisual());
                  rvConversaciones.setAdapter(adapter);
              } else {
                  adapter.setListaMensajes(mensajeDTOS);
              } listaMensajes.clear();

              listaMensajes.addAll(mensajeDTOS);
              //adapter.setListaMensajes(mensajeDTOS);
              rvConversaciones.scrollToPosition(listaMensajes.size()-1);//para mostrar el ultimo mensaje
          }
      });

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            vmConversacion.marcarMensajesComoLeidos();
            Log.d("ViewModelResponse2", "mensajes marcados como leidos");
        }, TIEMPO_ESPERA);

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        vmConversacion = new ViewModelProvider(this).get(ConversacionViewModel.class);

    }
}