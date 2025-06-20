package com.example.pharmatrack.ui.Medicamentos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pharmatrack.R;
import com.example.pharmatrack.adaptadores.ListaMedicamentosPredefinidosAdapter;
import com.example.pharmatrack.db.DbMedicamentosPredefinidos;
import com.example.pharmatrack.entidades.Medicamentos;

import java.util.ArrayList;

public class BuscarMedicamentos extends AppCompatActivity {

    private SearchView txtBuscar;
    private RecyclerView recyclerMedicamentos;
    private ListaMedicamentosPredefinidosAdapter adapter;
    private ArrayList<Medicamentos> listaMedicamentos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_medicamentos);


        recyclerMedicamentos = findViewById(R.id.recyclerMedicamentos);

        SearchView txtBuscar = findViewById(R.id.searchMedicamento);
        txtBuscar.setIconifiedByDefault(false);
        txtBuscar.setQueryHint(" "); // Evita que el queryHint nativo bloquee el hint real

        int id = txtBuscar.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView searchText = txtBuscar.findViewById(id);
        if (searchText != null) {
            searchText.setHint("Busca aquí tu medicamento");
            searchText.setHintTextColor(ContextCompat.getColor(this, R.color.darkblue));
            searchText.setTextColor(ContextCompat.getColor(this, R.color.darkblue));
            searchText.setTextSize(16);
        }


        recyclerMedicamentos.setLayoutManager(new LinearLayoutManager(this));

        // Obtener la lista desde base de datos predefinida
        DbMedicamentosPredefinidos db = new DbMedicamentosPredefinidos(this);
        listaMedicamentos = db.obtenerMedicamentos();

        if (listaMedicamentos.isEmpty()) {
            Toast.makeText(this, "No hay medicamentos precargados", Toast.LENGTH_SHORT).show();
        }

        adapter = new ListaMedicamentosPredefinidosAdapter(listaMedicamentos, new ListaMedicamentosPredefinidosAdapter.OnMedicamentoClickListener() {
            @Override
            public void onMedicamentoClick(Medicamentos medicamento) {
                abrirPantallaAgregar(medicamento);
            }
        });

        recyclerMedicamentos.setAdapter(adapter);

        txtBuscar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filtrado(newText.trim());
                return true;
            }
        });

        findViewById(R.id.btnAgregarManual).setOnClickListener(v -> {
            EditText input = new EditText(this);
            input.setHint("Nombre del medicamento");

            new AlertDialog.Builder(this)
                    .setTitle("Nuevo medicamento")
                    .setMessage("¿Cómo se llama el medicamento que quieres añadir?")
                    .setView(input)
                    .setPositiveButton("Continuar", (dialog, which) -> {
                        String nombre = input.getText().toString().trim();
                        if (!nombre.isEmpty()) {
                            Intent intent = new Intent(this, Paso1Frecuencia.class);
                            intent.putExtra("nombre", nombre);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    private void abrirPantallaAgregar(Medicamentos medicamento) {
        Intent intent = new Intent(this, Paso1Frecuencia.class);
        intent.putExtra("nombre", medicamento.getNombre());
        intent.putExtra("icono", medicamento.getIcono());
        startActivity(intent);
    }

}
