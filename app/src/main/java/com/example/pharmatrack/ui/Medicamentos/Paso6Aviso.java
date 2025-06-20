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

public class Paso6Aviso extends AppCompatActivity {

    private EditText edtUmbralAviso;
    private Button btnFinalizar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paso6_aviso);

        edtUmbralAviso = findViewById(R.id.edtUmbralAviso);
        btnFinalizar = findViewById(R.id.btnFinalizar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        btnFinalizar.setOnClickListener(v -> {
            String textoUmbral = edtUmbralAviso.getText().toString().trim();

            if (TextUtils.isEmpty(textoUmbral)) {
                Toast.makeText(this, "Por favor, introduce el umbral de aviso", Toast.LENGTH_SHORT).show();
                return;
            }

            int umbral;
            try {
                umbral = Integer.parseInt(textoUmbral);
                if (umbral < 0) throw new NumberFormatException();
            } catch (Exception e) {
                Toast.makeText(this, "Número inválido", Toast.LENGTH_SHORT).show();
                return;
            }

            // Recuperar todos los extras previos y añadir el umbral
            Intent prevIntent = getIntent();
            Intent intent = new Intent(this, ResumenFinalMedicamento.class);
            intent.putExtras(prevIntent); // copia todo lo anterior
            intent.putExtra("umbral", umbral); // añade umbral actual
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

