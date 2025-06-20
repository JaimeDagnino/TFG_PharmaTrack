package com.example.pharmatrack.ui.Progreso;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.ColorInt;
import androidx.core.content.ContextCompat;

import com.example.pharmatrack.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

/**
 * Decora un solo día (CalendarDay) con un círculo de un color específico.
 */
public class ColorCircleDecorator implements DayViewDecorator {

    private final CalendarDay date;
    private final Drawable circleDrawable;

    public ColorCircleDecorator(Context context, CalendarDay date, @ColorInt int color) {
        this.date = date;
        // Cargamos el drawable oval y lo tintamos
        Drawable original = ContextCompat.getDrawable(context, R.drawable.background_circle);
        if (original != null) {
            circleDrawable = original.mutate();
            circleDrawable.setTint(color);
        } else {
            circleDrawable = null;
        }
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        // Solo queremos decorar el día exacto que coincida
        return date != null && day.equals(date);
    }

    @Override
    public void decorate(DayViewFacade view) {
        if (circleDrawable != null) {
            // Ponemos el drawable (círculo tintado) como fondo de la celda
            view.setBackgroundDrawable(circleDrawable);
        }
    }
}

