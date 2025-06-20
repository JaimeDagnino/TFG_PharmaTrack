package com.example.pharmatrack.ui.Medicamentos;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.SearchView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pharmatrack.R;
import com.example.pharmatrack.adaptadores.ListaMedicamentosAdapter;
import com.example.pharmatrack.db.DbMedicamentos;
import com.example.pharmatrack.entidades.Medicamentos;
import com.google.android.material.button.MaterialButton;

import org.threeten.bp.LocalDate;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import java.text.SimpleDateFormat;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeParseException;

public class MedicamentosFragment extends Fragment {

    private RecyclerView recyclerView;
    private ListaMedicamentosAdapter adapter;
    private ArrayList<Medicamentos> listaMedicamentos;
    private static final DateTimeFormatter DTF =
            DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medicamentos, container, false);

        recyclerView = view.findViewById(R.id.listaMedicamentos);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cargarMedicamentos();

        SearchView searchView = view.findViewById(R.id.txtBuscarMedicamento);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filtrar(newText.trim());
                return true;
            }
        });


        MaterialButton btnAdd = view.findViewById(R.id.btnAddMedicamento);
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), BuscarMedicamentos.class);
            startActivity(intent);
        });


        recyclerView = view.findViewById(R.id.listaMedicamentos); // usa el mismo ID que ya usabas
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        cargarMedicamentos();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarMedicamentos();
    }

    private void cargarMedicamentos() {
        DbMedicamentos db = new DbMedicamentos(getContext());
        List<Medicamentos> todos = db.mostrarMedicamentos();
        ArrayList<Medicamentos> filtrados = new ArrayList<>();

        // 1) Obtenemos la fecha de hoy
        String hoyStr = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                .format(new Date());
        LocalDate hoy;
        try {
            hoy = LocalDate.parse(hoyStr, DTF);
        } catch (DateTimeParseException e) {
            hoy = LocalDate.now();
        }

        // 2) Filtramos Solo los tratamientos cuyo periodo incluya hoy
        for (Medicamentos m : todos) {
            LocalDate inicio;
            try {
                inicio = LocalDate.parse(m.getFechaInicio(), DTF);
            } catch (DateTimeParseException ex) {
                continue;
            }
            boolean activo;
            if (m.getDiasDeToma() == -1) {
                // crónico: siempre activo a partir de la fecha de inicio
                activo = !hoy.isBefore(inicio);
            } else {
                // finito: activo entre inicio y fin (inclusive)
                LocalDate fin = inicio.plusDays(m.getDiasDeToma() - 1);
                activo = !hoy.isBefore(inicio) && !hoy.isAfter(fin);
            }
            if (activo) {
                filtrados.add(m);
            }
        }

        // 3) Cargamos el adapter solo con el subconjunto activo
        adapter = new ListaMedicamentosAdapter(
                filtrados,
                getContext(),
                medicamento -> {
                    Intent it = new Intent(getContext(), VerMedicamento.class);
                    it.putExtra("MED_ID", medicamento.getId());
                    startActivity(it);
                }
        );
        recyclerView.setAdapter(adapter);
    }
}
