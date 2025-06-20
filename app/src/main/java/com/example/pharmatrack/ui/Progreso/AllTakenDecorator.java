package com.example.pharmatrack.ui.Progreso;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.example.pharmatrack.R;

import java.util.HashSet;
import java.util.Set;

/**
 * Pinta de verde los días en los que el usuario se tomó todas las medicinas previstas.
 */
public class AllTakenDecorator implements DayViewDecorator {

    private final Set<CalendarDay> datesToDecorate = new HashSet<>();
    private final Drawable greenCircle;

    public AllTakenDecorator(Context context) {
        // Cargamos el drawable de outline verde (res/drawable/green_circle.xml)
        greenCircle = ContextCompat.getDrawable(context, R.drawable.green_circle);
    }

    @Override
    public boolean shouldDecorate(@NonNull CalendarDay day) {
        // Devolverá true solo si ese día pertenece a nuestro Set de “todos tomados”
        return datesToDecorate.contains(day);
    }

    @Override
    public void decorate(@NonNull DayViewFacade view) {
        // Pinta el círculo verde (en background o como span)
        view.setSelectionDrawable(greenCircle);
        // Si quieres que sea exclusivamente un outline (sin “foco de selección”)
        // en lugar de setSelectionDrawable, podrías usar view.setBackgroundDrawable(greenCircle);
    }

    /** Llamar desde tu Fragment para agregar fechas a pintar de verde */
    public void addDate(CalendarDay day) {
        datesToDecorate.add(day);
    }

    /** Si quieres borrar todas las fechas (al recargar mes), puedes: */
    public void clearDates() {
        datesToDecorate.clear();
    }
}

