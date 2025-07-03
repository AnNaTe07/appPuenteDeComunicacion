package com.softannate.apppuentedecomunicacion.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import com.softannate.apppuentedecomunicacion.R;
import com.softannate.apppuentedecomunicacion.data.local.SpManager;
import java.util.ArrayList;
import java.util.List;

public class MenuUtils {

    public static void configurarPopupMenu(Activity activity, View anchorView) {
        Context context = activity;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup, null);

        ListView listView = popupView.findViewById(R.id.listViewItems);
        List<String> opcionesMenu = generarOpcionesMenu(context);

        configurarAdapter(context, listView, opcionesMenu);

        PopupWindow popupWindow = new PopupWindow(context);
        configurarPopupWindow(popupWindow, popupView, anchorView);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            manejarSeleccion(activity, opcionesMenu.get(position), popupWindow);
        });
    }

    private static void manejarSeleccion(Activity activity, String opcionSeleccionada, PopupWindow popupWindow) {
        popupWindow.dismiss();
        switch (opcionSeleccionada) {
            case "Nuevo Mensaje":
                String rol=SpManager.getRol(activity);
                Log.d("MenuUtils", "rol: " + rol);
                if(rol.equals("5")) {
                    navegar(activity, R.id.nav_nuevoT);
                }else {
                    navegar(activity, R.id.nav_nuevoE);
                }
                break;
            case "Contacto Institucional":
                navegar(activity, R.id.nav_inicio);
                break;
            case "Cerrar Sesión":
                navegar(activity, R.id.nav_logout);
                break;
        }
    }
    public static void navegar(Activity activity, int destino) {
        NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment_content_main);

        if (navController.getCurrentDestination() != null
                && navController.getCurrentDestination().getId() == destino) {
            return;
        }

        NavOptions navOptions = new NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setPopUpTo(R.id.mobile_navigation, false)
                .build();

        try {
            navController.navigate(destino, null, navOptions);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private static List<String> generarOpcionesMenu(Context context) {
        List<String> opcionesMenu = new ArrayList<>();
        opcionesMenu.add("Nuevo Mensaje");
        opcionesMenu.add("Contacto Institucional");
        opcionesMenu.add("Cerrar Sesión");
        return opcionesMenu;
    }

    private static void configurarAdapter(Context context, ListView listViewPrincipal, List<String> opcionesMenu) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, opcionesMenu) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setTextColor(ContextCompat.getColor(context, R.color.colorBase));
                Typeface typeface = ResourcesCompat.getFont(context, R.font.adamina);
                textView.setTypeface(typeface);

                return view;
            }
        };
        listViewPrincipal.setAdapter(adapter);
        listViewPrincipal.setDivider(new ColorDrawable(ContextCompat.getColor(context, R.color.colorBase)));
        listViewPrincipal.setDividerHeight(1);
    }

    private static void configurarPopupWindow(PopupWindow popupWindow, View popupView, View anchorView) {
        popupWindow.setContentView(popupView);
        popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popupWidth = popupView.getMeasuredWidth();

        popupWindow.setWidth(popupWidth);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        int[] location = new int[2];
        anchorView.getLocationOnScreen(location);
        popupWindow.showAtLocation(anchorView, Gravity.NO_GRAVITY, location[0], location[1] + anchorView.getHeight());
    }
}
