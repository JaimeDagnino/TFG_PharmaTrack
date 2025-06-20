package com.example.pharmatrack.ui.Medicamentos;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pharmatrack.Notifications.NotificacionMedicamentos;
import com.example.pharmatrack.R;
import com.example.pharmatrack.db.DbMedicamentos;
import com.example.pharmatrack.entidades.Medicamentos;
import com.google.android.material.button.MaterialButton;

public class VerMedicamento extends AppCompatActivity {

    private ImageView ivIcono;
    private TextView tvNombre, tvFrecuencia, tvHoras, tvDias,
            tvCapsulas, tvDosisPorToma, tvUmbralAviso;
    private MaterialButton fabEditar, fabEliminar;
    private int medId;
    private DbMedicamentos db;
    private Medicamentos m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_medicamento);

        // ① Activar flecha “Up” en el ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tvNombre      = findViewById(R.id.edtNombre);
        tvFrecuencia  = findViewById(R.id.edtFrecuencia);
        tvHoras       = findViewById(R.id.edtHoras);
        tvDias        = findViewById(R.id.edtDias);
        tvCapsulas    = findViewById(R.id.edtCapsulas);
        tvDosisPorToma= findViewById(R.id.edtDosisPorToma);
        tvUmbralAviso = findViewById(R.id.edtUmbralAviso);
        fabEditar     = findViewById(R.id.fabEditar);
        fabEliminar   = findViewById(R.id.fabEliminar);

        medId = getIntent().getIntExtra("MED_ID", -1);
        db    = new DbMedicamentos(this);

        cargarMedicamento();

        fabEditar.setOnClickListener(v -> {
            Intent it = new Intent(this, EditarMedicamento.class);
            it.putExtra("MED_ID", medId);
            startActivity(it);
        });

        fabEliminar.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setMessage("¿Eliminar este medicamento?")
                        .setPositiveButton("Sí", (dialog, which) -> {
                            // 1) Cancelar todas las alarmas asociadas a este medId:
                            NotificacionMedicamentos.cancelMedicamentoNotifications(
                                    this,
                                    m.getId(),
                                    m.getHoras()    // la misma cadena “HH:mm,HH:mm,…” que guardaste en BD
                            );

                            // 2) Eliminar de la BD
                            if (db.eliminarMedicamento(medId)) {
                                finish();
                            } else {
                                Toast.makeText(this, "No se pudo eliminar", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show()
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarMedicamento();
    }

    private void cargarMedicamento() {
        m = db.verMedicamento(medId);
        if (m == null) {
            Toast.makeText(this, "Medicamento no encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvNombre.setText(m.getNombre());
        tvFrecuencia.setText(
                m.getVecesPorDia() + (m.getVecesPorDia() == 1 ? " vez al día" : " veces al día")
        );

        // Mostrar siempre las horas con “• ” delante
        String horasRaw = m.getHoras(); // ej. "08:00,14:30,20:00"
        if (horasRaw != null && !horasRaw.isEmpty()) {
            String[] arr = horasRaw.split(",");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < arr.length; i++) {
                if (i > 0) sb.append("\n");
                sb.append("• ").append(arr[i].trim());
            }
            tvHoras.setText(sb.toString());
        } else {
            tvHoras.setText("No hay horas configuradas");
        }

        if (m.getDiasDeToma() == -1) {
            tvDias.setText("Tratamiento crónico");
        } else {
            tvDias.setText("Durante " + m.getDiasDeToma() + " días");
        }

        if (m.getCapsulasRestantes() >= 0) {
            tvCapsulas.setText("Te quedan " + m.getCapsulasRestantes() + " cápsulas");
        } else {
            tvCapsulas.setText("Seguimiento no activado");
        }

        tvDosisPorToma.setText(m.getDosisPorToma() > 0
                ? String.valueOf(m.getDosisPorToma()) : "");
        tvUmbralAviso.setText(m.getUmbralAviso() >= 0
                ? String.valueOf(m.getUmbralAviso()) : "");
    }

    // ② Manejo de pulsar “Up” en ActionBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}





