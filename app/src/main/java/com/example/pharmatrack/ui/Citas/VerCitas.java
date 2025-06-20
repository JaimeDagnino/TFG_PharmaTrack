package com.example.pharmatrack.ui.Citas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pharmatrack.Notifications.NotificacionCita;
import com.example.pharmatrack.R;
import com.example.pharmatrack.db.DbCitas;
import com.example.pharmatrack.entidades.Citas;
import com.google.android.material.button.MaterialButton;

public class VerCitas extends AppCompatActivity {

    EditText txtNombre, txtHora, txtFecha;
    Button btnGuarda;
    MaterialButton fabEditar, fabEliminar;
    ImageView viewIcono;
    Citas citas;
    int id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_citas);

        txtNombre = findViewById(R.id.txtNombre);
        txtFecha = findViewById(R.id.txtFecha);
        txtHora = findViewById(R.id.txtHora);
        viewIcono = findViewById(R.id.viewIcono); // ✅ Nuevo ImageView para mostrar el icono
        btnGuarda = findViewById(R.id.btnGuarda);
        btnGuarda.setVisibility(View.INVISIBLE); // Oculta el botón de guardar
        fabEditar = findViewById(R.id.fabEditar);
        fabEliminar = findViewById(R.id.fabEliminar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (getIntent().hasExtra("ID")) {
            id = getIntent().getIntExtra("ID", -1);
        }

        DbCitas dbCitas = new DbCitas(VerCitas.this);
        citas = dbCitas.verCitas(id);

        if (citas != null) {
            txtNombre.setText(citas.getNombre());
            txtFecha.setText(citas.getFecha());
            txtHora.setText(citas.getHora());

            // ✅ Mostrar el ícono de la cita
            if (citas.getIcono() != 0) {
                viewIcono.setImageResource(citas.getIcono());
            } else {
                viewIcono.setImageResource(R.drawable.ic_citas);
            }
        }

        fabEditar.setOnClickListener(v -> {
            Intent intent = new Intent(VerCitas.this, EditarCitas.class);
            intent.putExtra("ID", id);
            intent.putExtra("iconoActual", citas.getIcono());
            startActivity(intent);
        });

        fabEliminar.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(VerCitas.this);
            builder.setMessage("¿Está seguro que desea eliminar esta cita?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        if (dbCitas.eliminarCita(id)) {
                            NotificacionCita.cancelarNotificaciones(this, id);
                            finish();
                        }
                    })
                    .setNegativeButton("No", (dialog, i) -> dialog.dismiss())
                    .show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        actualizarVista(); // ✅ Recargar datos al volver de EditarCitas
    }

    private void actualizarVista() {
        DbCitas dbCitas = new DbCitas(VerCitas.this);
        citas = dbCitas.verCitas(id);

        if (citas != null) {
            txtNombre.setText(citas.getNombre());
            txtFecha.setText(citas.getFecha());
            txtHora.setText(citas.getHora());

            if (citas.getIcono() != 0) {
                viewIcono.setImageResource(citas.getIcono());
            } else {
                viewIcono.setImageResource(R.drawable.ic_citas);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}


