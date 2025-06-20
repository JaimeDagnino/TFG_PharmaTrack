package com.example.pharmatrack.ui.Medicamentos;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pharmatrack.R;

public class Paso3Dias extends AppCompatActivity {

    private EditText edtDias;
    private CheckBox chkCronico;
    private Button btnSiguiente;

    private String nombre;
    private int vecesPorDia;
    private String horas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paso3_dias);

        // Referencias UI
        chkCronico = findViewById(R.id.chkCronico);
        edtDias    = findViewById(R.id.edtDiasDeToma);
        btnSiguiente = findViewById(R.id.btnContinuarDias);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Inicialmente, si está marcada (puede venir de edición), desactivamos el campo
        edtDias.setEnabled(!chkCronico.isChecked());

        // Listener para checkbox: al marcar, deshabilita EditText; al desmarcar, lo habilita
        chkCronico.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                edtDias.setEnabled(!isChecked);
                if (isChecked) {
                    // opcional: limpiar valor anterior
                    edtDias.setText("");
                }
            }
        });

        // Recoger datos anteriores
        nombre     = getIntent().getStringExtra("nombre");
        vecesPorDia= getIntent().getIntExtra("vecesPorDia", 1);
        horas      = getIntent().getStringExtra("horas");

        btnSiguiente.setOnClickListener(v -> {
            boolean esCronico = chkCronico.isChecked();
            int diasTratamiento;

            if (esCronico) {
                diasTratamiento = -1;
            } else {
                String input = edtDias.getText().toString().trim();
                if (TextUtils.isEmpty(input)) {
                    Toast.makeText(this, "Indica los días o marca como crónico", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    diasTratamiento = Integer.parseInt(input);
                    if (diasTratamiento <= 0) {
                        Toast.makeText(this, "Número inválido de días", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Días no válidos", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // Pasamos al siguiente paso
            Intent intent = new Intent(Paso3Dias.this, Paso4Capsulas.class);
            intent.putExtra("nombre", nombre);
            intent.putExtra("vecesPorDia", vecesPorDia);
            intent.putExtra("horas", horas);
            intent.putExtra("dias", diasTratamiento); // valor definitivo
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




