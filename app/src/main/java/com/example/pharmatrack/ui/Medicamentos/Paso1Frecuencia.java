package com.example.pharmatrack.ui.Medicamentos;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pharmatrack.R;

public class Paso1Frecuencia extends AppCompatActivity {

    private RadioGroup radioFrecuencia;
    private Button btnSiguiente;
    private String nombreMedicamento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paso1_frecuencia);

        radioFrecuencia = findViewById(R.id.radioFrecuencia);
        btnSiguiente = findViewById(R.id.btnSiguiente);

        nombreMedicamento = getIntent().getStringExtra("nombre");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        btnSiguiente.setOnClickListener(v -> {
            int idSeleccionado = radioFrecuencia.getCheckedRadioButtonId();

            if (idSeleccionado == -1) {
                Toast.makeText(this, "Por favor, selecciona una opción", Toast.LENGTH_SHORT).show();
                return;
            }

            int vecesPorDia = 0;
            if (idSeleccionado == R.id.rb1) vecesPorDia = 1;
            else if (idSeleccionado == R.id.rb2) vecesPorDia = 2;
            else if (idSeleccionado == R.id.rb3) vecesPorDia = 3;

            Intent intent = new Intent(this, Paso2Horas.class);
            intent.putExtra("nombre", nombreMedicamento);
            intent.putExtra("vecesPorDia", vecesPorDia);
            startActivity(intent);
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
