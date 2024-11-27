package com.softannate.apppuentedecomunicacion;

import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.core.view.WindowCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.softannate.apppuentedecomunicacion.databinding.ActivityMainBinding;
import com.softannate.apppuentedecomunicacion.modelos.Usuario;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private MainActivityViewModel vmMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        vmMain = new ViewModelProvider(this).get(MainActivityViewModel.class);

        //obtener token????



        setSupportActionBar(binding.appBarMain.toolbar);

        View headerView = binding.navView.getHeaderView(0);
        TextView hNombre = headerView.findViewById(R.id.headerNombre);
        TextView hEmail= headerView.findViewById(R.id.headerEmail);
        ImageView hAvatar = headerView.findViewById(R.id.headerAvatar);

        //seteo datos de usuario en el header
        vmMain.getUsuario().observe(this, new Observer<Usuario>() {
            @Override
            public void onChanged(Usuario usuario) {
                hNombre.setText(usuario.getNombre()+ " "+ usuario.getApellido());
                hEmail.setText(usuario.getEmail());
                vmMain.getAvatar().observe(MainActivity.this, new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        Glide.with(getApplication().getApplicationContext()).
                                load(usuario.getAvatar()).
                                placeholder(R.drawable.cargando)//imagen temporal mientras se carga
                                .diskCacheStrategy(DiskCacheStrategy.NONE)//desactivo la cache
                                .skipMemoryCache(true)
                                .signature(new ObjectKey(System.currentTimeMillis()))//forzar actualizacion
                                .error(R.drawable.perfil_user)
                                .into(hAvatar);
                    }
                });
            }
        });

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;


        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_comunicados, R.id.nav_perfil, R.id.nav_slideshow, R.id.nav_inicio, R.id.nav_asistencias)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Si el extra 'navegar_a_inicio' es true, navegamos directamente al fragmento INICIO
        if (getIntent().getBooleanExtra("navegar_a_inicio", false)) {
            navController.navigate(R.id.nav_inicio);  // Navegamos al fragmento INICIO
        }

        //listener para cuando se abre el menu
        drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                vmMain.obtenerUsuario();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
            }
        });

//onResume????

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}