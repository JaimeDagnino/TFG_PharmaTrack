package com.example.pharmatrack.ui.Medicamentos;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pharmatrack.Notifications.NotificacionMedicamentos;
import com.example.pharmatrack.R;
import com.example.pharmatrack.db.DbMedicamentos;
import com.example.pharmatrack.entidades.Medicamentos;

public class ReponerMedicamento extends AppCompatActivity {

    private EditText edtNuevaCantidad;
    private Button btnGuardarRepuesto;
    private int medId;
    private DbMedicamentos db;
    private Medicamentos m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reponer_medicamento);

        edtNuevaCantidad   = findViewById(R.id.edtNuevaCantidad);
        btnGuardarRepuesto = findViewById(R.id.btnGuardarRepuesto);

        medId = getIntent().getIntExtra("medId", -1);
        if (medId < 0) {
            Toast.makeText(this, "Medicamento desconocido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db = new DbMedicamentos(this);
        m = db.verMedicamento(medId);
        if (m == null) {
            Toast.makeText(this, "Medicamento no encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Mostrar hint con cuántas quedan ahora, por ejemplo:
        edtNuevaCantidad.setHint("Cápsulas compradas (restan " + m.getCapsulasRestantes() + ")");

        btnGuardarRepuesto.setOnClickListener(v -> {
            String texto = edtNuevaCantidad.getText().toString().trim();
            if (TextUtils.isEmpty(texto)) {
                Toast.makeText(this, "Introduce una cantidad válida", Toast.LENGTH_SHORT).show();
                return;
            }
            int compradas;
            try {
                compradas = Integer.parseInt(texto);
                if (compradas <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Número inválido", Toast.LENGTH_SHORT).show();
                return;
            }

            // 1) Calculamos el nuevo stock de cápsulas:
            int actuales = m.getCapsulasRestantes();
            int nuevas   = actuales + compradas;
            m.setCapsulasRestantes(nuevas);

            // 2) Restauramos umbral avisos (si estaba -1) al valor original o a 0
            //    (Podrías guardar el umbral previo en otra columna, pero como ejemplo
            //     lo dejamos en el mismo m.getUmbralAviso() si fue != -1).
            int umbralPrevio = m.getUmbralAviso();
            if (umbralPrevio < 0) {
                // Si antes estaba desactivado (=-1), ponemos el umbral a 0 para que no notifique inmediatamente.
                m.setUmbralAviso(0);
            }

            // 3) Guardamos en BD
            boolean ok = db.editarMedicamento(
                    m.getId(),
                    m.getNombre(),
                    m.getDosis(),
                    m.getVecesPorDia(),
                    m.getHoras(),
                    m.getDiasDeToma(),
                    nuevas,
                    m.getDosisPorToma(),
                    m.getComprimidosPorCaja(),
                    m.getUmbralAviso(),
                    m.getDosisTomadasHoy()
            );
            if (!ok) {
                Toast.makeText(this, "Error al actualizar stock", Toast.LENGTH_SHORT).show();
                return;
            }

            // 4) Volvemos a programar alarmas de umbral con el nuevo stock:
            NotificacionMedicamentos.scheduleMedicamentoNotifications(
                    this,
                    m.getId(),
                    m.getHoras(),
                    m.getVecesPorDia(),
                    m.getDosisPorToma(),
                    nuevas,
                    m.getUmbralAviso(),
                    m.getDiasDeToma()
            );
            Toast.makeText(this, "Stock actualizado → " + nuevas + " cápsulas", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}

