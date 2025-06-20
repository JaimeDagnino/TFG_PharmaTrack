package com.example.pharmatrack.ui.Progreso;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class HeartDayDecorator implements DayViewDecorator {
    public final Context context;
    private final Set<CalendarDay> dates;
    private final @DrawableRes int drawableRes;

    public HeartDayDecorator(Context ctx,
                             Collection<CalendarDay> dates,
                             @DrawableRes int drawableRes) {
        this.context    = ctx;
        this.dates      = new HashSet<>(dates);
        this.drawableRes = drawableRes;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        Drawable bg = ContextCompat.getDrawable(context, drawableRes);
        view.setBackgroundDrawable(bg);
    }
}

