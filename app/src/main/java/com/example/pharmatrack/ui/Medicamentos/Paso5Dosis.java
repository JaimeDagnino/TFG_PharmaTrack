package com.example.pharmatrack.ui.Medicamentos;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pharmatrack.R;

public class Paso5Dosis extends AppCompatActivity {

    private EditText edtDosis;
    private Button btnSiguiente;
    private String nombre;
    private int vecesPorDia;
    private String horas;
    private int dias;
    private int capsulas;
    private boolean seguimientoActivado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paso5_dosis);

        edtDosis = findViewById(R.id.edtDosisPorToma);
        btnSiguiente = findViewById(R.id.btnSiguiente);

        // Obtener valores del Intent anterior
        nombre = getIntent().getStringExtra("nombre");
        vecesPorDia = getIntent().getIntExtra("vecesPorDia", 1);
        horas = getIntent().getStringExtra("horas");
        dias = getIntent().getIntExtra("dias", -1);
        capsulas = getIntent().getIntExtra("capsulas", -1);
        seguimientoActivado = getIntent().getBooleanExtra("seguimientoActivado", false);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        btnSiguiente.setOnClickListener(v -> {
            String dosisTexto = edtDosis.getText().toString().trim();
            if (TextUtils.isEmpty(dosisTexto)) {
                Toast.makeText(this, "Introduce cuántas cápsulas tomas por vez", Toast.LENGTH_SHORT).show();
                return;
            }

            int dosis;
            try {
                dosis = Integer.parseInt(dosisTexto);
                if (dosis <= 0) throw new NumberFormatException();
            } catch (Exception e) {
                Toast.makeText(this, "Número inválido", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, Paso6Aviso.class);
            intent.putExtra("nombre", nombre);
            intent.putExtra("vecesPorDia", vecesPorDia);
            intent.putExtra("horas", horas);
            intent.putExtra("dias", dias);
            intent.putExtra("seguimientoActivado", seguimientoActivado);
            intent.putExtra("capsulas", capsulas);
            intent.putExtra("dosis", dosis);
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