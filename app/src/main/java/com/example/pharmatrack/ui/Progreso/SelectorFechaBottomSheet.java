package com.example.pharmatrack.ui.Progreso;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.pharmatrack.R;
import com.example.pharmatrack.db.DbMedicamentos;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Calendar;
import java.util.Locale;

/**
 * BottomSheetDialogFragment que muestra un calendario para elegir una fecha.
 * Cuando el usuario selecciona un día, invoca al listener definido por quien lo implementa.
 */
public class SelectorFechaBottomSheet
        extends BottomSheetDialogFragment
        implements OnDateSelectedListener {

    private MaterialCalendarView calendarView;
    private DbMedicamentos dbMed;
    private AllTakenDecorator allTakenDecorator;
    private PartialTakenDecorator partialTakenDecorator;
    private NoneTakenDecorator noneTakenDecorator;

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.bottom_sheet_selector_fecha,
                container, false);

        // ① Referencia al MaterialCalendarView de tu XML
        calendarView = view.findViewById(R.id.calendarView);
        calendarView.setShowOtherDates(MaterialCalendarView.SHOW_NONE);

        // ② Instancia tu helper de BD y tus decoradores
        dbMed    = new DbMedicamentos(requireContext());
        allTakenDecorator     = new AllTakenDecorator(requireContext());
        partialTakenDecorator = new PartialTakenDecorator(requireContext());
        noneTakenDecorator    = new NoneTakenDecorator(requireContext());

        // ③ Pinta los días una vez (mes actual)
        decorateCalendarDays();

        // ④ Añade los decoradores
        calendarView.addDecorators(
                allTakenDecorator,
                partialTakenDecorator,
                noneTakenDecorator);

        // ⑤ Cuando el usuario toque un día, lanzamos el callback al fragment padre
        calendarView.setOnDateChangedListener(this);

        // Opcional: si quieres que empiece con “hoy” seleccionado
        calendarView.setSelectedDate(CalendarDay.today());

        return view;
    }

    private void decorateCalendarDays() {
        allTakenDecorator.clearDates();
        partialTakenDecorator.clearDates();
        noneTakenDecorator.clearDates();

        // pinta TODO el mes visible (o el que prefieras)
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int d = 1; d <= maxDay; d++) {
            String dateStr = String.format(Locale.getDefault(),
                    "%02d/%02d/%04d", d, month + 1, year);
            int total   = dbMed.getTotalTomasParaFecha(dateStr);
            int taken   = dbMed.getTomasTomadasParaFecha(dateStr);
            if (total <= 0) continue;
            CalendarDay day = CalendarDay.from(year, month, d);
            if (taken >= total)      allTakenDecorator.addDate(day);
            else if (taken > 0)      partialTakenDecorator.addDate(day);
            else                     noneTakenDecorator.addDate(day);
        }
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget,
                               @NonNull CalendarDay date,
                               boolean selected) {
        // Convierte CalendarDay a tu formato “dd/MM/yyyy” y pasa al fragment Progreso
        int y = date.getYear();
        int m = date.getMonth();   // ya es 1–12
        int d = date.getDay();
        String fecha = String.format(Locale.getDefault(),
                "%02d/%02d/%04d", d, m, y);

        ((OnFechaSeleccionadaListener)getParentFragment())
                .onFechaSeleccionada(fecha);
        dismiss();
    }

    public interface OnFechaSeleccionadaListener {
        void onFechaSeleccionada(String fecha);
    }
}
