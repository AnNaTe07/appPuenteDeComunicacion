package com.softannate.apppuentedecomunicacion.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import androidx.core.content.ContextCompat;
import com.softannate.apppuentedecomunicacion.R;
import com.softannate.apppuentedecomunicacion.modelos.Categoria_Mensaje;
import java.util.ArrayList;
import java.util.List;

public class CategoriaPopUpWindow {

    private final Context context;
    private final List<Categoria_Mensaje> categorias;
    private final OnCategoriaSeleccionadaListener listener;

    public CategoriaPopUpWindow(Context context, List<Categoria_Mensaje> categorias, OnCategoriaSeleccionadaListener listener) {
        this.context = context;
        this.categorias = categorias;
        this.listener = listener;
    }

    public interface OnCategoriaSeleccionadaListener {
        void onSeleccionada(Categoria_Mensaje categoria);
    }

    public void mostrar(View anchorView) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popup, null);
        Log.d("CategoriaPopUp", "Popup view inflado: " + (view != null));


        ListView listView = view.findViewById(R.id.listViewItems);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.item_menu, obtenerNombres());
        listView.setAdapter(adapter);
        listView.setDivider(new ColorDrawable(ContextCompat.getColor(context, R.color.colorBase)));
        listView.setDividerHeight(1);

        int anchoFijoPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 220, anchorView.getResources().getDisplayMetrics());

        PopupWindow popupWindow = new PopupWindow(view, anchoFijoPx, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        int[] location = new int[2];
        anchorView.getLocationOnScreen(location);
        int x = location[0];

        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popupHeight = view.getMeasuredHeight();// altura real del popup

        int y = location[1] - popupHeight; // muestra arriba del anchor

        popupWindow.showAtLocation(anchorView, Gravity.NO_GRAVITY, x, y);

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            Categoria_Mensaje seleccionada = categorias.get(position);
            listener.onSeleccionada(seleccionada);
            popupWindow.dismiss();
        });
    }

    private List<String> obtenerNombres() {
        List<String> nombres = new ArrayList<>();
        for (Categoria_Mensaje c : categorias) {
            nombres.add(c.getNombre());
        }
        return nombres;
    }
}