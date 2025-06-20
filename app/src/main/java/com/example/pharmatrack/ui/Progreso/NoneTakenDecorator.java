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
 * Pinta de rojo los días en los que el usuario NO se tomó ningún medicamento.
 */
public class NoneTakenDecorator implements DayViewDecorator {

    private final Set<CalendarDay> datesToDecorate = new HashSet<>();
    private final Drawable redCircle;

    public NoneTakenDecorator(Context context) {
        redCircle = ContextCompat.getDrawable(context, R.drawable.red_circle);
    }

    @Override
    public boolean shouldDecorate(@NonNull CalendarDay day) {
        return datesToDecorate.contains(day);
    }

    @Override
    public void decorate(@NonNull DayViewFacade view) {
        view.setSelectionDrawable(redCircle);
    }

    public void addDate(CalendarDay day) {
        datesToDecorate.add(day);
    }

    public void clearDates() {
        datesToDecorate.clear();
    }
}
