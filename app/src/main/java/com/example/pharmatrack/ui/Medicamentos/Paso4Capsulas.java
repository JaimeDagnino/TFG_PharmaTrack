package com.example.pharmatrack.ui.Medicamentos;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pharmatrack.R;

public class Paso4Capsulas extends AppCompatActivity {

    private RadioGroup radioSeguimiento;
    private EditText edtCapsulas;
    private Button btnSiguiente;
    private String nombre;
    private int vecesPorDia;
    private String horas;
    private int dias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paso4_capsulas);

        radioSeguimiento = findViewById(R.id.radioSeguimiento);
        edtCapsulas = findViewById(R.id.edtCapsulas);
        btnSiguiente = findViewById(R.id.btnSiguiente);

        nombre = getIntent().getStringExtra("nombre");
        vecesPorDia = getIntent().getIntExtra("vecesPorDia", 1);
        horas = getIntent().getStringExtra("horas");
        dias = getIntent().getIntExtra("dias", -1);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        edtCapsulas.setEnabled(false);

        radioSeguimiento.setOnCheckedChangeListener((group, checkedId) -> {
            edtCapsulas.setEnabled(checkedId == R.id.rbSi);
        });

        btnSiguiente.setOnClickListener(v -> {
            int seleccion = radioSeguimiento.getCheckedRadioButtonId();
            boolean quiereControl = seleccion == R.id.rbSi;

            int capsulas = -1;

            if (quiereControl) {
                String texto = edtCapsulas.getText().toString().trim();
                if (TextUtils.isEmpty(texto)) {
                    Toast.makeText(this, "Indica cuántas cápsulas te quedan", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    capsulas = Integer.parseInt(texto);
                } catch (Exception e) {
                    Toast.makeText(this, "Número inválido", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(this, Paso5Dosis.class);
                intent.putExtra("nombre", nombre);
                intent.putExtra("vecesPorDia", vecesPorDia);
                intent.putExtra("horas", horas);
                intent.putExtra("dias", dias);
                intent.putExtra("seguimientoActivado", true);
                intent.putExtra("capsulas", capsulas);
                startActivity(intent);

            } else {
                Intent intent = new Intent(this, ResumenFinalMedicamento.class);
                intent.putExtra("nombre", nombre);
                intent.putExtra("vecesPorDia", vecesPorDia);
                intent.putExtra("horas", horas);
                intent.putExtra("dias", dias);
                intent.putExtra("seguimientoActivado", false);
                intent.putExtra("capsulas", -1);
                intent.putExtra("dosis", -1);
                intent.putExtra("umbral", -1);
                startActivity(intent);
            }
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



