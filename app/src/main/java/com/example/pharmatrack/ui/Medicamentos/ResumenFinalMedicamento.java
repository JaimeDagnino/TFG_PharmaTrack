package com.example.pharmatrack.ui.Medicamentos;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;


import androidx.appcompat.app.AppCompatActivity;

import com.example.pharmatrack.MainActivity;
import com.example.pharmatrack.Notifications.NotificacionMedicamentos;
import com.example.pharmatrack.R;
import com.example.pharmatrack.db.DbMedicamentos;
import com.example.pharmatrack.entidades.Medicamentos;
import com.example.pharmatrack.Notifications.NotificacionMedicamentos;

public class ResumenFinalMedicamento extends AppCompatActivity {

    private TextView resumenNombre,
            resumenVeces,
            resumenHoras,
            resumenDias,
            resumenCapsulas,
            resumenDosis,
            resumenUmbral;
    private Button btnGuardar;
    private com.example.pharmatrack.Notifications.NotificacionMedicamentos NotificacionMedicamentos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen_final_medicamento);

        Toolbar toolbar = findViewById(R.id.toolbarResumenMedicamento);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // ① Referencias a vistas
        resumenNombre  = findViewById(R.id.resumenNombre);
        resumenVeces   = findViewById(R.id.resumenVeces);
        resumenHoras   = findViewById(R.id.resumenHoras);
        resumenDias    = findViewById(R.id.txtDiasMedicamento);
        resumenCapsulas = findViewById(R.id.resumenCapsulas);
        resumenDosis   = findViewById(R.id.resumenDosis);
        resumenUmbral  = findViewById(R.id.resumenUmbral);
        btnGuardar     = findViewById(R.id.btnGuardarFinal);

        // ② Obtener datos del Intent
        Intent intent = getIntent();
        String nombre         = intent.getStringExtra("nombre");
        int vecesPorDia       = intent.getIntExtra("vecesPorDia", 1);
        String horas          = intent.getStringExtra("horas");   // ej. "08:00,14:00"
        int dias              = intent.getIntExtra("dias", -1);   // -1 si crónico
        boolean seguimiento   = intent.getBooleanExtra("seguimientoActivado", false);
        int capsulas          = seguimiento ? intent.getIntExtra("capsulas", -1) : -1;
        int dosis             = intent.getIntExtra("dosis", -1);
        int umbral            = intent.getIntExtra("umbral", -1);

        // ③ Mostrar resumen en pantalla
        resumenNombre.setText("• Nombre: " + nombre);
        resumenVeces.setText("• Frecuencia: " + vecesPorDia
                + (vecesPorDia == 1 ? " vez" : " veces") + " al día");
        resumenHoras.setText("• Horas: " + horas);
        resumenDias.setText(dias == -1
                ? "• Tratamiento crónico"
                : "• Días de tratamiento: " + dias);

        if (capsulas >= 0) {
            resumenCapsulas.setText("• Cápsulas restantes: " + capsulas);
            resumenDosis.setText("• Dosis por toma: " + (dosis > 0 ? dosis : "No especificado"));
            resumenUmbral.setText("• Umbral de aviso: " + (umbral >= 0 ? umbral : "No configurado"));
        } else {
            resumenCapsulas.setText("• No se realiza seguimiento de cápsulas");
            resumenDosis.setText("");
            resumenUmbral.setText("");
        }

        // ④ Listener para “Guardar”
        btnGuardar.setOnClickListener(v -> {
            // a) Crear el objeto Medicamentos a insertar
            Medicamentos m = new Medicamentos();
            m.setIcono(R.drawable.ic_medicamento);
            m.setNombre(nombre);
            m.setVecesPorDia(vecesPorDia);
            m.setHoras(horas);
            m.setDiasDeToma(dias);
            m.setCapsulasRestantes(capsulas);
            m.setDosisPorToma(dosis);
            m.setUmbralAviso(umbral);
            m.setDosis(dosis > 0 ? dosis + " cápsula(s)" : "");
            m.setComprimidosPorCaja(0);

            // b) Insertar en BD y obtener el ID generado
            DbMedicamentos db = new DbMedicamentos(this);
            long resultado = db.insertarMedicamento(
                    m.getNombre(),
                    m.getDosis(),
                    m.getVecesPorDia(),
                    m.getHoras(),
                    m.getCapsulasRestantes(),
                    m.getDosisPorToma(),
                    m.getComprimidosPorCaja(),
                    m.getUmbralAviso(),
                    m.getDiasDeToma()
            );
            Log.d("DEBUG_DB", "✅ Medicamento insertado con ID: " + resultado);

            if (resultado != -1) {
                int medId = (int) resultado;

                // c) Depuración: verificar que se llama a scheduling
                Log.d("DEBUG_NOTIF_MED", "↴↴ Llamando a scheduleMedicamentoNotifications con: "
                        + "medId=" + medId
                        + ", horas=" + horas
                        + ", vecesPorDia=" + vecesPorDia
                        + ", dosisPorToma=" + dosis
                        + ", capsulasRestantes=" + capsulas
                        + ", umbralAviso=" + umbral
                        + ", diasDeToma=" + dias);

                // d) Programar notificaciones (diarias + umbral)
                NotificacionMedicamentos.scheduleMedicamentoNotifications(
                        this,
                        medId,
                        horas,
                        vecesPorDia,
                        dosis,
                        capsulas,
                        umbral,
                        dias
                );
            } else {
                Log.d("DEBUG_DB", "⋮ medId == -1, no se programan notificaciones");
            }

            // e) Volver a MainActivity mostrando el fragmento “medicamentos”
            Intent volver = new Intent(this, MainActivity.class);
            volver.putExtra("openFragment", "medicamentos");
            volver.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(volver);
            finish();
        });
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}




