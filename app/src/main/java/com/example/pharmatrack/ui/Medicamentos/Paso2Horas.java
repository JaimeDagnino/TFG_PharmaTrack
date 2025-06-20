package com.example.pharmatrack.ui.Medicamentos;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pharmatrack.R;

import java.util.ArrayList;
import java.util.Calendar;

public class Paso2Horas extends AppCompatActivity {

    private LinearLayout layoutHoras;
    private Button btnSiguiente;
    private String nombre;
    private int vecesPorDia;
    private ArrayList<String> horas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paso2_horas);

        layoutHoras = findViewById(R.id.layoutHoras);
        btnSiguiente = findViewById(R.id.btnContinuarHoras);

        nombre = getIntent().getStringExtra("nombre");
        vecesPorDia = getIntent().getIntExtra("vecesPorDia", 1);
        horas = new ArrayList<>();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        for (int i = 0; i < vecesPorDia; i++) {
            int index = i;
            Button btnHora = new Button(this);
            btnHora.setText("Seleccionar hora " + (i + 1));
            btnHora.setOnClickListener(v -> mostrarTimePicker(btnHora, index));
            layoutHoras.addView(btnHora);
            horas.add("");
        }

        btnSiguiente.setOnClickListener(v -> {
            if (horas.contains("")) {
                Toast.makeText(this, "Selecciona todas las horas", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, com.example.pharmatrack.ui.Medicamentos.Paso3Dias.class);
            intent.putExtra("nombre", nombre);
            intent.putExtra("vecesPorDia", vecesPorDia);
            intent.putExtra("horas", String.join(",", horas));
            startActivity(intent);
        });
    }

    private void mostrarTimePicker(Button boton, int index) {
        Calendar c = Calendar.getInstance();
        int horaActual = c.get(Calendar.HOUR_OF_DAY);
        int minutoActual = c.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            String horaStr = String.format("%02d:%02d", hourOfDay, minute);
            int totalMinutosActual = hourOfDay * 60 + minute;

            // Validar contra hora anterior
            if (index > 0) {
                String anterior = horas.get(index - 1);
                if (!anterior.isEmpty()) {
                    String[] partes = anterior.split(":");
                    int minAnterior = Integer.parseInt(partes[0]) * 60 + Integer.parseInt(partes[1]);
                    if (totalMinutosActual < minAnterior) {
                        Toast.makeText(this, "La hora debe ser posterior a la anterior", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }

            boton.setText("Hora: " + horaStr);
            horas.set(index, horaStr);

        }, horaActual, minutoActual, true);

        dialog.show();
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