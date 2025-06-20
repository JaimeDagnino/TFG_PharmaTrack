package com.example.pharmatrack.ui.Citas;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.appcompat.widget.SearchView; // ✅ Usar la versión correcta de SearchView

import com.example.pharmatrack.R;
import com.example.pharmatrack.adaptadores.ListaCitasAdapter;
import com.example.pharmatrack.db.DbCitas;
import com.example.pharmatrack.entidades.Citas;

import java.util.ArrayList;

public class CitasFragment extends Fragment {

    private RecyclerView listaCitas;
    private SearchView txtBuscar;
    private ListaCitasAdapter adapter;
    private ArrayList<Citas> listaArrayCitas;
    private Button btnAdd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_citas, container, false);

        // Inicializar vistas
        listaCitas = view.findViewById(R.id.listaCitas);
        txtBuscar = view.findViewById(R.id.txtBuscar);
        btnAdd = view.findViewById(R.id.btnAdd);

        // Mostrar SearchView expandido por defecto
        txtBuscar.setIconified(false);
        txtBuscar.clearFocus();


        // ✅ Verificar si RecyclerView está presente en el layout
        if (listaCitas == null) {
            Log.e("ERROR", "RecyclerView listaCitas es NULL");
            return view;
        }

        listaCitas.setLayoutManager(new LinearLayoutManager(getContext()));

        // Obtener datos de la base de datos
        DbCitas dbCitas = new DbCitas(getContext());
        listaArrayCitas = dbCitas.mostrarCitas();

        // ✅ Verificar si hay citas en la base de datos
        if (listaArrayCitas.isEmpty()) {
            Log.e("INFO", "No hay citas en la base de datos");
        } else {
            Log.d("DEBUG", "Se encontraron " + listaArrayCitas.size() + " citas.");
        }

        // Configurar el adaptador
        adapter = new ListaCitasAdapter(listaArrayCitas);
        listaCitas.setAdapter(adapter);

        // Configurar la barra de búsqueda
        txtBuscar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapter != null) {
                    adapter.filtrado(newText.trim()); // ✅ Siempre limpiar espacios y actualizar resultados
                }
                return true;
            }
        });

        // Botón para añadir una nueva cita (abre la actividad Add_Cita)
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Add_Cita.class);
            startActivity(intent);
        });

        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        cargarCitas(); // ✅ Recargar citas cada vez que el usuario regrese al fragmento
    }
    private void cargarCitas() {
        DbCitas dbCitas = new DbCitas(getContext());
        listaArrayCitas.clear(); // ✅ Limpiar la lista antes de actualizar
        listaArrayCitas.addAll(dbCitas.mostrarCitas()); // ✅ Agregar las citas nuevamente

        // Notificar al adaptador que los datos han cambiado
        adapter.notifyDataSetChanged();
    }
}




