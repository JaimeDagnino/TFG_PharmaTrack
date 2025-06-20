package com.example.pharmatrack.ui.Hoy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pharmatrack.Notifications.NotificationReceiver;
import com.example.pharmatrack.R;
import com.example.pharmatrack.adaptadores.HoyAdapter;
import com.example.pharmatrack.adaptadores.ListaCitasAdapter;
import com.example.pharmatrack.db.DbCitas;
import com.example.pharmatrack.db.DbMedicamentos;
import com.example.pharmatrack.entidades.Citas;
import com.example.pharmatrack.entidades.Medicamentos;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.HashSet;
import java.util.Set;

public class HoyFragment extends Fragment {

    private static final String TAG = "HoyFragment";

    private RecyclerView recyclerCitasHoy;
    private RecyclerView recyclerMedicamentosHoy;
    private TextView tvContadorMedicamentos;

    private ListaCitasAdapter citasAdapter;
    private HoyAdapter medicamentosAdapter;

    // Lista de Citas y Lista de Tomas que usará el adapter
    private ArrayList<Citas> listaCitasHoy;
    private ArrayList<Toma> listaTomasHoy;

    /** Receptor para escuchar cuando llegue ACTION_UPDATE_TOMA y marcar la toma en pantalla */
    private final BroadcastReceiver tomaReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!NotificationReceiver.ACTION_UPDATE_TOMA.equals(intent.getAction())) {
                return;
            }
            int medId = intent.getIntExtra("medId", -1);
            String hora = intent.getStringExtra("hora");
            if (medId < 0 || hora == null) return;

            // Buscar la Toma correspondiente en listaTomasHoy y marcarla
            for (int i = 0; i < listaTomasHoy.size(); i++) {
                Toma t = listaTomasHoy.get(i);
                if (t.medId == medId && t.hora.equals(hora)) {
                    t.tomado = true;
                    medicamentosAdapter.notifyItemChanged(i);
                    break;
                }
            }
            // Actualizar contador “X de Y”
            medicamentosAdapter.actualizarContadorInterno();
        }
    };


    public HoyFragment() {
        // Constructor vacío requerido
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // 1) Inflamos el layout
        View root = inflater.inflate(R.layout.fragment_hoy, container, false);

        // 2) Referenciamos las vistas
        recyclerCitasHoy        = root.findViewById(R.id.recyclerCitasHoy);
        recyclerMedicamentosHoy = root.findViewById(R.id.recyclerMedicamentosHoy);
        tvContadorMedicamentos  = root.findViewById(R.id.tvContadorMedicamentos);

        // 3) Asignamos LayoutManagers a ambos RecyclerViews
        recyclerCitasHoy.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerMedicamentosHoy.setLayoutManager(new LinearLayoutManager(getContext()));

        // 4) Cargamos datos iniciales (antes de crear cualquier adapter)
        cargarCitasDeHoy();
        cargarTomasDeHoy();

        // 5) Creamos y asignamos adapter de Citas
        if (listaCitasHoy == null) {
            listaCitasHoy = new ArrayList<>();
        }
        citasAdapter = new ListaCitasAdapter(listaCitasHoy);
        recyclerCitasHoy.setAdapter(citasAdapter);

        // 6) Creamos y asignamos adapter de Medicamentos DE HOY
        //    IMPORTANTE: usamos la listaTomasHoy YA cargada en el paso (4).
        if (listaTomasHoy == null) {
            listaTomasHoy = new ArrayList<>();
        }
        medicamentosAdapter = new HoyAdapter(
                getContext(),
                listaTomasHoy,
                tvContadorMedicamentos
        );
        recyclerMedicamentosHoy.setAdapter(medicamentosAdapter);

        // 7) Inicializamos el contador “X / Y” manualmente la primera vez
        medicamentosAdapter.updateList(listaTomasHoy);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        // 1) Registrar el BroadcastReceiver para ACTION_UPDATE_TOMA
        IntentFilter filter = new IntentFilter(NotificationReceiver.ACTION_UPDATE_TOMA);
        requireContext().registerReceiver(
                tomaReceiver,
                filter,
                Context.RECEIVER_NOT_EXPORTED
        );

        // 2) Volvemos a cargar citas de hoy (puede permanecer igual)
        cargarCitasDeHoy();
        if (citasAdapter != null) {
            citasAdapter.listaCitas.clear();
            citasAdapter.listaCitas.addAll(listaCitasHoy);
            citasAdapter.listaOriginal.clear();
            citasAdapter.listaOriginal.addAll(listaCitasHoy);
            citasAdapter.notifyDataSetChanged();
        }

        // 3) Volvemos a cargar tomas de hoy
        cargarTomasDeHoy();
        if (medicamentosAdapter != null) {
            medicamentosAdapter.updateList(listaTomasHoy);
            medicamentosAdapter.actualizarContadorInterno();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Desregistrar el receptor para evitar fugas de memoria
        requireContext().unregisterReceiver(tomaReceiver);
    }



    /**
     * Lee de la BD todas las citas cuya fecha coincide con hoy.
     * (Parseo flexible para aceptar d/M/yyyy o dd/MM/yyyy).
     */
    private void cargarCitasDeHoy() {
        listaCitasHoy = new ArrayList<>();

        // 1) Obtiene Date de “hoy” (formato dd/MM/yyyy)
        Date hoyDate;
        try {
            String hoyStr = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    .format(new Date());
            hoyDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(hoyStr);
        } catch (Exception e) {
            Log.e(TAG, "Error parseando fecha de hoy", e);
            return;
        }

        // 2) Trae todas las citas sin filtrar
        DbCitas dbCitas = new DbCitas(getContext());
        ArrayList<Citas> todas = dbCitas.mostrarTodasCitas();
        Log.d(TAG, "mostrarTodasCitas() devolvió " + todas.size() + " citas.");

        // 3) Compara cada c.getFecha() con hoyDate
        SimpleDateFormat parserFlexible = new SimpleDateFormat("d/M/yyyy", Locale.getDefault());
        SimpleDateFormat comparador = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String hoyYmd = comparador.format(hoyDate);

        for (Citas c : todas) {
            Date citaDate;
            try {
                citaDate = parserFlexible.parse(c.getFecha());
            } catch (Exception ex) {
                Log.v(TAG, "No se pudo parsear fecha '" + c.getFecha() +
                        "' para cita id=" + c.getId());
                continue;
            }
            String citaYmd = comparador.format(citaDate);
            if (hoyYmd.equals(citaYmd)) {
                listaCitasHoy.add(c);
                Log.v(TAG, "Añadida a listaCitasHoy: id=" + c.getId() +
                        " fecha=" + c.getFecha());
            }
        }

        Log.d(TAG, "Citas filtradas para hoy: " + listaCitasHoy.size());

        // 4) Ordenar por hora (“HH:mm”)
        Collections.sort(listaCitasHoy, new Comparator<Citas>() {
            @Override
            public int compare(Citas c1, Citas c2) {
                return c1.getHora().compareTo(c2.getHora());
            }
        });
        Log.d(TAG, "Citas de hoy ordenadas por hora.");
    }

    /**
     * Lee todos los medicamentos, genera un “Toma” por cada hora y carga listaTomasHoy.
     */
    private void cargarTomasDeHoy() {
        listaTomasHoy = new ArrayList<>();
        DbMedicamentos dbMed = new DbMedicamentos(getContext());
        ArrayList<Medicamentos> meds = (ArrayList<Medicamentos>) dbMed.mostrarMedicamentos();

        // Obtenemos la fecha de hoy una sola vez:
        String fechaHoy = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                .format(new Date());

        for (Medicamentos m : meds) {
            String horasRaw = m.getHoras();
            if (TextUtils.isEmpty(horasRaw)) continue;

            String[] arrHoras = horasRaw.split(",");
            for (String hs : arrHoras) {
                String hora = hs.trim();
                if (hora.isEmpty()) continue;

                Toma t = new Toma();
                t.medId = m.getId();
                t.hora  = hora;
                t.nombre = m.getNombre();
                t.dosisPorToma = m.getDosisPorToma() > 0
                        ? (m.getDosisPorToma() + " cápsula(s)")
                        : "";
                t.vecesPorDia     = m.getVecesPorDia();
                t.iconoResId      = R.drawable.ic_medicamento;

                // Nueva forma de saber si ya se ha tomado esa hora:
                Set<String> horasTomadas = dbMed.getHorasTomadasParaMedEnFecha(
                        t.medId,
                        fechaHoy
                );
                t.tomado = horasTomadas.contains(t.hora);

                listaTomasHoy.add(t);
            }
        }

        Collections.sort(listaTomasHoy, Comparator.comparing(o -> o.hora));
    }

}






