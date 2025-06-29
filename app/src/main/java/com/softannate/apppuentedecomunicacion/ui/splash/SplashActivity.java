package com.softannate.apppuentedecomunicacion.ui.splash;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.softannate.apppuentedecomunicacion.R;
import com.softannate.apppuentedecomunicacion.databinding.ActivitySplashBinding;
import com.softannate.apppuentedecomunicacion.ui.login.LoginActivity;
import com.softannate.apppuentedecomunicacion.ui.main.MainActivity;
import com.softannate.apppuentedecomunicacion.utils.NavigationBarColor;

public class SplashActivity extends AppCompatActivity {

    private SplashActivityViewModel vm;
    private ImageView splashImage;
    private ActivitySplashBinding binding;
    private static final long SPLASH_DELAY = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
        vm=new ViewModelProvider(this).get(SplashActivityViewModel.class);

        splashImage = binding.imageView;
        splashImage.setVisibility(View.VISIBLE);

        // INICIO: Imagen muy pequeña y transparente
        splashImage.setScaleX(0f);
        splashImage.setScaleY(0f);
        splashImage.setAlpha(0f);

        // ANIMACIÓN DE ESCALA (ACERCAMIENTO)
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(splashImage, "scaleX", 0f, 1f);
        scaleXAnimator.setDuration(1500);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(splashImage, "scaleY", 0f, 1f);
        scaleYAnimator.setDuration(1500);

        // ANIMACIÓN DE TRANSPARENCIA (VOLVIÉNDOSE VISIBLE)
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(splashImage, "alpha", 0f, 1f);
        alphaAnimator.setDuration(1500);

        // EJECUTAR LAS ANIMACIONES SIMULTÁNEAMENTE
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator, alphaAnimator);
        animatorSet.setInterpolator(new OvershootInterpolator(1.2f));
        animatorSet.start();

        vm.checkToken();
        vm.getMostrarMain().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean mostrar) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, SPLASH_DELAY);
            }
        });
        vm.getMostrarLogin().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean mostrar) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, SPLASH_DELAY);
            }
        });

        NavigationBarColor.setNavigationBarColor(this, R.color.white);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.splash), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}