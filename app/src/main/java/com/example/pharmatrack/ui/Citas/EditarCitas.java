package com.example.pharmatrack.ui.Citas;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pharmatrack.HashMaps.EspecialidadMap;
import com.example.pharmatrack.Notifications.NotificacionCita;
import com.example.pharmatrack.R;
import com.example.pharmatrack.adaptadores.IconAdapter;
import com.example.pharmatrack.db.DbCitas;
import com.example.pharmatrack.entidades.Citas;
import com.google.android.material.button.MaterialButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditarCitas extends AppCompatActivity {

    AutoCompleteTextView txtNombre;
    EditText txtFecha, txtHora;
    Button btnGuarda;
    ImageView viewIcono;
    MaterialButton fabEditar, fabEliminar;
    Citas citas;
    int id = -1;
    int imagen_id = R.drawable.ic_citas;

    private static final String[] ESPECIALIDADES = {
            "Medicina General / Medicina Familiar", "Pediatría", "Ginecología",
            "Cardiología", "Dermatología", "Neurología", "Psiquiatría", "Oftalmología",
            "Otorrinolaringología (ORL)", "Traumatología", "Endocrinología",
            "Gastroenterología", "Neumología", "Urología", "Oncología", "Nefrología",
            "Reumatología", "Anestesiología", "Radiología", "Hematología", "Medicina Interna",
            "Medicina Intensiva / Cuidados Críticos", "Medicina del Deporte",
            "Medicina Física y Rehabilitación", "Medicina del Trabajo", "Alergología",
            "Genética Médica", "Geriatría", "Cirugía General", "Cirugía Plástica",
            "Cirugía Cardiovascular", "Cirugía Torácica", "Cirugía Pediátrica",
            "Cirugía Oncológica", "Cirugía Maxilofacial", "Neurocirugía",
            "Angiología y Cirugía Vascular", "Infectología", "Patología"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_citas);

        txtNombre = findViewById(R.id.txtNombre);
        txtFecha = findViewById(R.id.txtFecha);
        txtHora = findViewById(R.id.txtHora);
        btnGuarda = findViewById(R.id.btnGuarda);
        viewIcono = findViewById(R.id.viewIcono);
        fabEditar = findViewById(R.id.fabEditar);
        fabEditar.setVisibility(View.INVISIBLE);
        fabEliminar = findViewById(R.id.fabEliminar);
        fabEliminar.setVisibility(View.INVISIBLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1001);
            }
        }

        if (getIntent().hasExtra("ID")) {
            id = getIntent().getIntExtra("ID", -1);
        }

        if (getIntent().hasExtra("iconoActual")) {
            imagen_id = getIntent().getIntExtra("iconoActual", R.drawable.ic_citas);
        }

        viewIcono.setImageResource(imagen_id);
        viewIcono.setOnClickListener(v -> abrirSelectorIcono());

        DbCitas dbCitas = new DbCitas(this);
        citas = dbCitas.verCitas(id);

        if (citas != null) {
            txtNombre.setText(citas.getNombre());
            txtFecha.setText(citas.getFecha());
            txtHora.setText(citas.getHora());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, ESPECIALIDADES);
        txtNombre.setAdapter(adapter);

        txtNombre.setOnItemClickListener((parent, view, position, id) -> {
            String especialidadSeleccionada = (String) parent.getItemAtPosition(position);
            imagen_id = EspecialidadMap.getEspecialidadIcon(especialidadSeleccionada);
            viewIcono.setImageResource(imagen_id);
        });

        txtNombre.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) txtNombre.showDropDown();
        });

        txtNombre.setOnClickListener(v -> txtNombre.showDropDown());

        btnGuarda.setOnClickListener(v -> {
            String nombre = txtNombre.getText().toString().trim();
            String fecha = txtFecha.getText().toString().trim();
            String hora = txtHora.getText().toString().trim();

            if (!nombre.isEmpty() && !fecha.isEmpty() && !hora.isEmpty()) {
                boolean correcto = dbCitas.editarCita(id, nombre, fecha, hora, imagen_id);

                if (correcto) {
                    Toast.makeText(this, "Cita cambiada correctamente", Toast.LENGTH_SHORT).show();

                    // ✅ Cancelar notificaciones anteriores
                    NotificacionCita.cancelarNotificaciones(this, id);

                    // ✅ Reprogramar con la lógica centralizada
                    NotificacionCita.programarNotificaciones(this, nombre, hora, fecha, id,imagen_id);

                    finish();
                } else {
                    Toast.makeText(this, "Error al cambiar la cita", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Debe rellenar todos los campos", Toast.LENGTH_SHORT).show();
            }
        });

        txtFecha.setOnClickListener(v -> mostrarSelectorFecha());
        txtHora.setOnClickListener(v -> mostrarSelectorHora());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso de notificaciones concedido", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No podrás recibir recordatorios si no activas las notificaciones", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void abrirSelectorIcono() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.icon_grid);
        GridView gridView = dialog.findViewById(R.id.grid_icon);

        int[] icons = {R.drawable.icon1, R.drawable.icon2, R.drawable.icon13, R.drawable.icon4,
                R.drawable.icon5, R.drawable.icon6, R.drawable.icon7, R.drawable.icon8, R.drawable.icon9,
                R.drawable.icon10, R.drawable.icon11, R.drawable.icon12, R.drawable.icon14, R.drawable.icon15,
                R.drawable.icon16, R.drawable.icon17, R.drawable.icon18};
        IconAdapter iconAdapter = new IconAdapter(this, icons);
        gridView.setAdapter(iconAdapter);

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            imagen_id = icons[position];
            viewIcono.setImageResource(imagen_id);
            dialog.dismiss();
        });

        dialog.show();
    }


    private void mostrarSelectorFecha() {
        final Calendar calendario = Calendar.getInstance();
        int anio = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    String fechaSeleccionada = dayOfMonth + "/" + (month + 1) + "/" + year;
                    txtFecha.setText(fechaSeleccionada);
                }, anio, mes, dia);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void mostrarSelectorHora() {
        final Calendar calendario = Calendar.getInstance();
        int hora = calendario.get(Calendar.HOUR_OF_DAY);
        int minuto = calendario.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, selectedHour, selectedMinute) -> {
                    String horaSeleccionada = String.format("%02d:%02d", selectedHour, selectedMinute);
                    txtHora.setText(horaSeleccionada);
                }, hora, minuto, true);

        timePickerDialog.show();
    }

    private long convertirFechaYHoraAMillis(String fecha, String hora) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        try {
            Date fechaHora = sdf.parse(fecha + " " + hora);
            return fechaHora != null ? fechaHora.getTime() : 0;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }
}




