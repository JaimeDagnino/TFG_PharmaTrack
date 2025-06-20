package com.example.pharmatrack.ui.Medicamentos;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pharmatrack.R;
import com.example.pharmatrack.db.DbMedicamentos;
import com.example.pharmatrack.entidades.Medicamentos;
import com.example.pharmatrack.Notifications.NotificacionMedicamentos;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Calendar;

public class EditarMedicamento extends AppCompatActivity {

    private EditText edtNombre, edtFrecuencia, edtDias,
            edtCapsulas, edtDosisPorToma, edtUmbralAviso;
    private CheckBox chkCronico;
    private LinearLayout layoutHoras;
    private MaterialButton btnGuarda;
    private int medId;
    private DbMedicamentos db;
    private Medicamentos m;

    // Lista para almacenar las horas dinámicas
    private ArrayList<String> horasList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_medicamento);

        // ① Activar flecha “Up” en ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // ② Referencias a las vistas
        edtNombre       = findViewById(R.id.edtNombre);
        edtFrecuencia   = findViewById(R.id.edtFrecuencia);
        layoutHoras     = findViewById(R.id.layoutHoras);
        chkCronico      = findViewById(R.id.chkCronico);
        edtDias         = findViewById(R.id.edtDias);
        edtCapsulas     = findViewById(R.id.edtCapsulas);
        edtDosisPorToma = findViewById(R.id.edtDosisPorToma);
        edtUmbralAviso  = findViewById(R.id.edtUmbralAviso);
        btnGuarda       = findViewById(R.id.btnGuarda);

        // ③ Obtener ID del medicamento y cargar datos
        medId = getIntent().getIntExtra("MED_ID", -1);
        db    = new DbMedicamentos(this);

        m = db.verMedicamento(medId);
        if (m == null) {
            Toast.makeText(this, "Medicamento no encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // ④ Cargar datos existentes en los campos
        edtNombre.setText(m.getNombre());
        edtFrecuencia.setText(String.valueOf(m.getVecesPorDia()));

        // Días: si m.getDiasDeToma() == -1 → crónico
        if (m.getDiasDeToma() == -1) {
            chkCronico.setChecked(true);
            edtDias.setText("");
            edtDias.setEnabled(false);
        } else {
            chkCronico.setChecked(false);
            edtDias.setText(String.valueOf(m.getDiasDeToma()));
            edtDias.setEnabled(true);
        }

        edtCapsulas.setText(m.getCapsulasRestantes() >= 0
                ? String.valueOf(m.getCapsulasRestantes()) : "");
        edtDosisPorToma.setText(m.getDosisPorToma() > 0
                ? String.valueOf(m.getDosisPorToma()) : "");
        edtUmbralAviso.setText(m.getUmbralAviso() >= 0
                ? String.valueOf(m.getUmbralAviso()) : "");

        // ⑤ Inicializar horasList a partir del String (o vacíos)
        horasList = new ArrayList<>();
        if (m.getHoras() != null && !m.getHoras().isEmpty()) {
            String[] splitHoras = m.getHoras().split(",");
            for (String h : splitHoras) {
                horasList.add(h.trim());
            }
        }
        // Rellenar hasta que horasList.size() == frecuencia original
        ajustarListaHoras(m.getVecesPorDia());

        // ⑥ Mostrar botones de hora
        construirBotonesHoras();

        // ⑦ Lógica para el CheckBox “Crónico”
        chkCronico.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                edtDias.setText("");
                edtDias.setEnabled(false);
            } else {
                edtDias.setEnabled(true);
            }
        });

        // ⑧ Listener para frecuencia: sólo entre 1 y 3
        TextWatcher freqWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String texto = s.toString().trim();
                if (texto.isEmpty()) return;

                int nuevaFreq;
                try {
                    nuevaFreq = Integer.parseInt(texto);
                } catch (NumberFormatException ex) {
                    nuevaFreq = 1;
                }
                // Forzar rango [1..3]
                if (nuevaFreq < 1) {
                    nuevaFreq = 1;
                    Toast.makeText(EditarMedicamento.this,
                            "La frecuencia mínima es 1", Toast.LENGTH_SHORT).show();
                }
                if (nuevaFreq > 3) {
                    nuevaFreq = 3;
                    Toast.makeText(EditarMedicamento.this,
                            "La frecuencia máxima es 3", Toast.LENGTH_SHORT).show();
                }

                String actualText = edtFrecuencia.getText().toString().trim();
                if (!actualText.equals(String.valueOf(nuevaFreq))) {
                    edtFrecuencia.removeTextChangedListener(this);
                    edtFrecuencia.setText(String.valueOf(nuevaFreq));
                    edtFrecuencia.setSelection(String.valueOf(nuevaFreq).length());
                    edtFrecuencia.addTextChangedListener(this);
                }

                if (nuevaFreq != horasList.size()) {
                    ajustarListaHoras(nuevaFreq);
                    construirBotonesHoras();
                }
            }
        };
        edtFrecuencia.addTextChangedListener(freqWatcher);

        // ⑨ Botón “Guardar cambios”
        btnGuarda.setOnClickListener(v -> {
            // ⑨.1) Validar y actualizar campos del objeto m
            m.setNombre(edtNombre.getText().toString().trim());
            m.setVecesPorDia(parseIntOrZero(edtFrecuencia));

            if (chkCronico.isChecked()) {
                m.setDiasDeToma(-1);
            } else {
                int diasValor = parseIntOrMinusOne(edtDias);
                if (diasValor <= 0) {
                    Toast.makeText(this,
                            "Introduce un número de días válido (≥1) o marca crónico.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                m.setDiasDeToma(diasValor);
            }

            m.setHoras(String.join(",", horasList));
            m.setCapsulasRestantes(parseIntOrMinusOne(edtCapsulas));
            m.setDosisPorToma(parseIntOrMinusOne(edtDosisPorToma));
            m.setUmbralAviso(parseIntOrMinusOne(edtUmbralAviso));

            // ⑨.2) Cancelar alarmas antiguas antes de editar
            NotificacionMedicamentos.cancelMedicamentoNotifications(
                    this,
                    m.getId(),
                    m.getHoras()
            );

            // ⑨.3) Editar en BD (solo asignar a “ok”, sin redeclarar)
            boolean ok = db.editarMedicamento(
                    m.getId(),
                    m.getNombre(),
                    m.getDosis(),
                    m.getVecesPorDia(),
                    m.getHoras(),
                    m.getDiasDeToma(),
                    m.getCapsulasRestantes(),
                    m.getDosisPorToma(),
                    m.getComprimidosPorCaja(),
                    m.getUmbralAviso(),
                    m.getDosisTomadasHoy()
            );

            if (ok) {
                // ⑨.4) Volver a programar notificaciones con valores actualizados
                NotificacionMedicamentos.scheduleMedicamentoNotifications(
                        this,
                        m.getId(),
                        m.getHoras(),
                        m.getVecesPorDia(),
                        m.getDosisPorToma(),
                        m.getCapsulasRestantes(),
                        m.getUmbralAviso(),
                        m.getDiasDeToma()
                );

                Toast.makeText(this, "Medicamento actualizado", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Ajusta horasList para que tenga exactamente 'nuevaFrecuencia' elementos.
     * - Si faltan, añade "".
     * - Si sobran, elimina del final.
     */
    private void ajustarListaHoras(int nuevaFrecuencia) {
        int actual = horasList.size();
        if (nuevaFrecuencia > actual) {
            for (int i = actual; i < nuevaFrecuencia; i++) {
                horasList.add("");
            }
        } else if (nuevaFrecuencia < actual) {
            for (int i = actual - 1; i >= nuevaFrecuencia; i--) {
                horasList.remove(i);
            }
        }
    }

    /**
     * Genera un botón por cada posición en horasList.
     * Cada clic abre un TimePicker con validación de orden cronológico.
     */
    private void construirBotonesHoras() {
        layoutHoras.removeAllViews();
        int frecuencias = horasList.size();

        for (int i = 0; i < frecuencias; i++) {
            final int index = i;
            Button btnHora = new Button(this);
            String texto = horasList.get(i).isEmpty()
                    ? "Seleccionar hora " + (i + 1)
                    : "Hora: " + horasList.get(i);
            btnHora.setText(texto);
            btnHora.setOnClickListener(v ->
                    mostrarTimePickerConValidacion(btnHora, index));
            layoutHoras.addView(btnHora);
        }
    }

    /**
     * Abre un TimePickerDialog y comprueba:
     * - La hora nueva debe ser > hora anterior (si existe).
     * - La hora nueva debe ser < hora siguiente (si existe).
     */
    private void mostrarTimePickerConValidacion(Button boton, int index) {
        Calendar c = Calendar.getInstance();
        int horaActual = c.get(Calendar.HOUR_OF_DAY);
        int minutoActual = c.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    String nuevaHora = String.format("%02d:%02d", hourOfDay, minute);
                    int minNueva = hourOfDay * 60 + minute;

                    // Validar contra hora anterior
                    if (index > 0) {
                        String anterior = horasList.get(index - 1);
                        if (!anterior.isEmpty()) {
                            String[] partA = anterior.split(":");
                            int minAnterior = Integer.parseInt(partA[0]) * 60
                                    + Integer.parseInt(partA[1]);
                            if (minNueva <= minAnterior) {
                                Toast.makeText(
                                        EditarMedicamento.this,
                                        "La hora debe ser posterior a la anterior",
                                        Toast.LENGTH_SHORT
                                ).show();
                                return;
                            }
                        }
                    }

                    // Validar contra hora siguiente
                    if (index < horasList.size() - 1) {
                        String siguiente = horasList.get(index + 1);
                        if (!siguiente.isEmpty()) {
                            String[] partS = siguiente.split(":");
                            int minSiguiente = Integer.parseInt(partS[0]) * 60
                                    + Integer.parseInt(partS[1]);
                            if (minNueva >= minSiguiente) {
                                Toast.makeText(
                                        EditarMedicamento.this,
                                        "La hora debe ser anterior a la siguiente",
                                        Toast.LENGTH_SHORT
                                ).show();
                                return;
                            }
                        }
                    }

                    // Si pasa ambas validaciones, guardamos:
                    horasList.set(index, nuevaHora);
                    boton.setText("Hora: " + nuevaHora);
                },
                horaActual,
                minutoActual,
                true
        );
        dialog.show();
    }

    private int parseIntOrZero(EditText e) {
        try {
            return Integer.parseInt(e.getText().toString().trim());
        } catch (Exception ex) {
            return 0;
        }
    }

    private int parseIntOrMinusOne(EditText e) {
        try {
            return Integer.parseInt(e.getText().toString().trim());
        } catch (Exception ex) {
            return -1;
        }
    }

    // ⑩ Manejo del botón “Up” en ActionBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}









