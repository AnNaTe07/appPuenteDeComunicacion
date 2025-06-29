package com.softannate.apppuentedecomunicacion.ui.main;

import static com.softannate.apppuentedecomunicacion.utils.NavigationBarColor.setNavigationBarColor;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.softannate.apppuentedecomunicacion.R;
import com.softannate.apppuentedecomunicacion.utils.MenuUtils;


public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //inicializo el NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_content_main);
        navController = navHostFragment.getNavController();

        // conecto el BottomNavigationView con el NavController
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        NavigationUI.setupWithNavController(bottomNav, navController);

        // Listener del destino actual para el botón del menú
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            Fragment fragmentActual = navHostFragment
                    .getChildFragmentManager()
                    .getPrimaryNavigationFragment();

            ImageButton btnMenu = findViewById(R.id.btn_popup_menu);
            btnMenu.setColorFilter(Color.WHITE);
            btnMenu.setOnClickListener(v -> {
                MenuUtils.configurarPopupMenu(MainActivity.this, v);
            });
        });

        // Navegación desde intent externo (si aplica)
        if (getIntent().getBooleanExtra("navegar_a_mensajes", false)) {
            navController.navigate(R.id.nav_mensajes);
        }

        setNavigationBarColor(this, R.color.colorBase);
    }


    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

}
