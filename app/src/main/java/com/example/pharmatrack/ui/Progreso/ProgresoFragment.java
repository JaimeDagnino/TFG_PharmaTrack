package com.example.pharmatrack.ui.Progreso;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pharmatrack.R;
import com.example.pharmatrack.adaptadores.ProgresoAdapter;
import com.example.pharmatrack.db.DbCitas;
import com.example.pharmatrack.db.DbMedicamentos;
import com.example.pharmatrack.entidades.Citas;
import com.example.pharmatrack.ui.Hoy.Toma;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeParseException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProgresoFragment extends Fragment
        implements SelectorFechaBottomSheet.OnFechaSeleccionadaListener {

    // Vistas
    private LinearLayout llSelectorFecha;
    private TextView tvFechaSeleccionada, tvDetalleDia, tvExplicacionTomas;
    private RecyclerView recyclerTomasFecha;
    private TextView tvSubtituloMedicamentos, tvProgresoMedicamentos;
    private ProgressBar pbMedicamentos;
    private TextView tvRacha, tvPrediccionReposicion;

    // BD
    private DbMedicamentos dbMed;
    private DbCitas dbCitas;

    // Adapter
    private ProgresoAdapter progresoAdapter;
    private ArrayList<Toma> listaTomasEseDia;

    // Fechas
    private final SimpleDateFormat sdfInterno =
            new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private final SimpleDateFormat sdfCompleto =
            new SimpleDateFormat("EEEE, d 'de' MMMM 'de' yyyy", new Locale("es","ES"));
    private static final DateTimeFormatter DTF =
            DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault());

    // Fecha activa
    private String fechaSeleccionada;

    public ProgresoFragment() { }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_progreso, container, false);

        // Referencias
        tvRacha                = root.findViewById(R.id.tvRacha);
        llSelectorFecha        = root.findViewById(R.id.llSelectorFecha);
        tvFechaSeleccionada    = root.findViewById(R.id.tvFechaSeleccionada);
        ImageView ivAbrir      = root.findViewById(R.id.ivAbrirCalendario);
        tvDetalleDia           = root.findViewById(R.id.tvDetalleDia);
        tvExplicacionTomas     = root.findViewById(R.id.tvExplicacionTomas);
        recyclerTomasFecha     = root.findViewById(R.id.recyclerTomasFecha);
        tvSubtituloMedicamentos= root.findViewById(R.id.tvSubtituloMedicamentos);
        tvProgresoMedicamentos = root.findViewById(R.id.tvProgresoMedicamentos);
        pbMedicamentos         = root.findViewById(R.id.pbMedicamentos);
        tvPrediccionReposicion = root.findViewById(R.id.tvPrediccionReposicion);

        // BD
        dbMed   = new DbMedicamentos(requireContext());
        dbCitas = new DbCitas(requireContext());

        // RecyclerView
        listaTomasEseDia = new ArrayList<>();
        recyclerTomasFecha.setLayoutManager(new LinearLayoutManager(requireContext()));
        progresoAdapter = new ProgresoAdapter(requireContext(), listaTomasEseDia);
        recyclerTomasFecha.setAdapter(progresoAdapter);

        // Inicial: hoy
        Date hoy = new Date();
        fechaSeleccionada = sdfInterno.format(hoy);
        String headerHoy = "Hoy, " + capitalizeFirstLetter(sdfCompleto.format(hoy));
        tvFechaSeleccionada.setText(headerHoy);

        // Carga inicial
        cargarDatosParaFecha(fechaSeleccionada);
        actualizarRachaConAnimacion();
        syncMedicamentosSection();
        actualizarPrediccionReposicion();

        // Abrir BottomSheet
        View.OnClickListener openBS = v -> {
            SelectorFechaBottomSheet bs = new SelectorFechaBottomSheet();
            bs.show(getChildFragmentManager(), "selector_fecha");
        };
        llSelectorFecha.setOnClickListener(openBS);
        ivAbrir.setOnClickListener(openBS);

        return root;
    }

    @Override public void onResume() {
        super.onResume();
        actualizarRachaConAnimacion();
        actualizarPrediccionReposicion();
        // Si sigue siendo hoy, recalcular sección
        if (fechaSeleccionada.equals(sdfInterno.format(new Date()))) {
            syncMedicamentosSection();
        }
    }

    // Helper unificado
    private void syncMedicamentosSection() {
        String fecha = fechaSeleccionada;
        LocalDate sel;
        try {
            sel = LocalDate.parse(fecha, DTF);
        } catch (DateTimeParseException e) {
            sel = LocalDate.now();
        }
        boolean isPastOrToday = !sel.isAfter(LocalDate.now());

        int totalTomas   = dbMed.getTotalTomasParaFecha(fecha);
        int tomasTomadas = dbMed.getTomasTomadasParaFecha(fecha);

        boolean show = isPastOrToday && totalTomas > 0;
        tvSubtituloMedicamentos.setVisibility(show ? View.VISIBLE : View.GONE);
        tvProgresoMedicamentos .setVisibility(show ? View.VISIBLE : View.GONE);
        pbMedicamentos         .setVisibility(show ? View.VISIBLE : View.GONE);

        if (show) {
            pbMedicamentos.setMax(totalTomas);
            pbMedicamentos.setProgress(tomasTomadas);
            tvProgresoMedicamentos.setText(tomasTomadas + " / " + totalTomas);
        }
    }

    @Override public void onFechaSeleccionada(String fechaFormateada) {
        fechaSeleccionada = fechaFormateada;
        // Header
        try {
            Date d = sdfInterno.parse(fechaFormateada);
            String txt = sdfCompleto.format(d);
            if (fechaFormateada.equals(sdfInterno.format(new Date()))) {
                txt = "Hoy, " + txt;
            }
            tvFechaSeleccionada.setText(capitalizeFirstLetter(txt));
        } catch (Exception ignored) {}

        // detalle diario
        cargarDatosParaFecha(fechaFormateada);
        // sección medicamentos
        syncMedicamentosSection();
    }

    private void cargarDatosParaFecha(String diaStr) {
        List<Citas> citas = dbCitas.mostrarCitasPorFecha(diaStr);
        if (!citas.isEmpty()) {
            StringBuilder sb = new StringBuilder("Citas:\n");
            for (Citas c : citas) sb.append("• ").append(c.getHora())
                    .append(" - ").append(c.getNombre()).append("\n");
            tvDetalleDia.setText(sb.toString().trim());
            tvDetalleDia.setVisibility(View.VISIBLE);
            tvExplicacionTomas.setVisibility(View.GONE);
            recyclerTomasFecha.setVisibility(View.GONE);
        } else {
            tvDetalleDia.setVisibility(View.GONE);
            List<Toma> tomas = dbMed.getListaTomasParaFecha(diaStr);
            if (tomas.isEmpty()) {
                tvExplicacionTomas.setVisibility(View.GONE);
                recyclerTomasFecha.setVisibility(View.GONE);
                tvDetalleDia.setText("No hay citas ni tomas programadas");
                tvDetalleDia.setVisibility(View.VISIBLE);
            } else {
                tvExplicacionTomas.setVisibility(View.VISIBLE);
                listaTomasEseDia.clear();
                listaTomasEseDia.addAll(tomas);
                progresoAdapter.notifyDataSetChanged();
                recyclerTomasFecha.setVisibility(View.VISIBLE);
            }
        }

        float pct;
        int tot = dbMed.getTotalTomasParaFecha(diaStr);
        int cum = dbMed.getTomasTomadasParaFecha(diaStr);
        pct = tot == 0 ? 1f : (float) cum / tot;
        int color = pct >= 1f ? Color.parseColor("#4CAF50")
                : pct > 0f ? Color.parseColor("#FFEB3B")
                : Color.parseColor("#F44336");
        llSelectorFecha.setBackgroundColor(color);
    }

    private void actualizarRachaConAnimacion() {
        int racha = 0; Calendar cal = Calendar.getInstance();
        while (true) {
            String f = sdfInterno.format(cal.getTime());
            int tot = dbMed.getTotalTomasParaFecha(f);
            if (tot <= 0) break;
            int cum = dbMed.getTomasTomadasParaFecha(f);
            if (cum >= tot) { racha++; cal.add(Calendar.DAY_OF_MONTH, -1); }
            else break;
        }
        tvRacha.setText(racha + " día" + (racha==1?"":"s") + " seguidos");
        AlphaAnimation anim = new AlphaAnimation(0f,1f);
        anim.setDuration(500); anim.setFillAfter(true);
        tvRacha.startAnimation(anim);
    }

    private void actualizarPrediccionReposicion() {
        var meds = dbMed.mostrarMedicamentos();
        StringBuilder sb = new StringBuilder();
        for (var m : meds) {
            int caps = m.getCapsulasRestantes(), dosis = m.getDosisPorToma();
            if (caps<0||dosis<=0) continue;
            int faltan = (int)Math.ceil((double)caps / dosis);
            if (faltan<=7) sb.append(m.getNombre())
                    .append(": quedan ").append(faltan)
                    .append(" día").append(faltan==1?"":"s")
                    .append("\n");
        }
        tvPrediccionReposicion.setText(sb.length()==0
                ? "No hay medicamentos cercanos al umbral."
                : sb.toString().trim());
    }

    private String capitalizeFirstLetter(String s) {
        if (s==null||s.isEmpty()) return s;
        return s.substring(0,1).toUpperCase(new Locale("es","ES"))
                + s.substring(1);
    }
}












