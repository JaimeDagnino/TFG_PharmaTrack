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
 * Pinta de amarillo los días en los que el usuario se tomó solo parte de las dosis.
 */
public class PartialTakenDecorator implements DayViewDecorator {

    private final Set<CalendarDay> datesToDecorate = new HashSet<>();
    private final Drawable yellowCircle;

    public PartialTakenDecorator(Context context) {
        yellowCircle = ContextCompat.getDrawable(context, R.drawable.yellow_circle);
    }

    @Override
    public boolean shouldDecorate(@NonNull CalendarDay day) {
        return datesToDecorate.contains(day);
    }

    @Override
    public void decorate(@NonNull DayViewFacade view) {
        view.setSelectionDrawable(yellowCircle);
    }

    public void addDate(CalendarDay day) {
        datesToDecorate.add(day);
    }

    public void clearDates() {
        datesToDecorate.clear();
    }
}
