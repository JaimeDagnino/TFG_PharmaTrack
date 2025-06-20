package com.example.pharmatrack.ui.Citas;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pharmatrack.Notifications.NotificacionCita;
import com.example.pharmatrack.Notifications.NotificationHelper;
import com.example.pharmatrack.R;
import com.example.pharmatrack.adaptadores.IconAdapter;
import com.example.pharmatrack.db.DbCitas;
import com.example.pharmatrack.HashMaps.EspecialidadMap;

import java.util.Calendar;

public class Add_Cita extends AppCompatActivity {

    AutoCompleteTextView txtNombre;
    EditText txtFecha, txtHora;
    Button btnGuarda, btnVolver;
    private ImageView icon_select;
    int imagen_id = R.drawable.ic_citas;

    // Lista de especialidades médicas
    private static final String[] ESPECIALIDADES = {
            "Medicina General / Medicina Familiar", "Pediatría", "Ginecología",
            "Cardiología", "Dermatología", "Neurología", "Psiquiatría", "Oftalmología",
            "Otorrinolaringología (ORL)", "Traumatología", "Endocrinología",
            "Gastroenterología", "Neumología", "Urología", "Oncología", "Nefrología",
            "Reumatología", "Anestesiología", "Radiología", "Hematología", "Alergología",
            "Genética Médica", "Geriatría", "Cirugía General","Neurocirugía",
            "Angiología y Cirugía Vascular", "Infectología", "Patología"

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cita);

        NotificationHelper.createNotificationChannel(this);

        txtNombre = findViewById(R.id.txtNombre);
        txtFecha = findViewById(R.id.txtFecha);
        txtHora = findViewById(R.id.txtHora);
        btnGuarda = findViewById(R.id.btnGuarda);
        icon_select = findViewById(R.id.selecIcon);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        // Configurar AutoCompleteTextView con las especialidades
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, ESPECIALIDADES);
        txtNombre.setAdapter(adapter);

        // Evento para cambiar el icono automáticamente al seleccionar una especialidad
        txtNombre.setOnItemClickListener((parent, view, position, id) -> {
            String especialidadSeleccionada = (String) parent.getItemAtPosition(position);
            imagen_id = EspecialidadMap.getEspecialidadIcon(especialidadSeleccionada);
            icon_select.setImageResource(imagen_id);
        });

        // Mostrar sugerencias al hacer clic
        txtNombre.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) txtNombre.showDropDown();
        });

        txtNombre.setOnClickListener(v -> txtNombre.showDropDown());

        // Seleccionar fecha
        txtFecha.setOnClickListener(v -> mostrarSelectorFecha());

        // Seleccionar hora
        txtHora.setOnClickListener(v -> mostrarSelectorHora());

        // Seleccionar icono manualmente
        icon_select.setOnClickListener(v -> showIconDialog());

        // Guardar cita
        btnGuarda.setOnClickListener(v -> {
            String nombre = txtNombre.getText().toString().trim();
            String fecha = txtFecha.getText().toString().trim();
            String hora = txtHora.getText().toString().trim();

            // Verificar si los campos están completos

            if (nombre.isEmpty()) {
                Toast.makeText(Add_Cita.this, "Por favor, ingrese una especialidad", Toast.LENGTH_LONG).show();
                return;
            }

            if (fecha.isEmpty()) {
                Toast.makeText(Add_Cita.this, "Por favor, seleccione una fecha", Toast.LENGTH_LONG).show();
                return;
            }

            if (hora.isEmpty()) {
                Toast.makeText(Add_Cita.this, "Por favor, seleccione una hora", Toast.LENGTH_LONG).show();
                return;
            }

            // Guardar la cita si todos los datos son correctos
            DbCitas dbCitas = new DbCitas(Add_Cita.this);
            long id = dbCitas.insertarCita(nombre, fecha, hora, imagen_id);

            if (id > 0) {
                Toast.makeText(Add_Cita.this, "Cita registrada correctamente", Toast.LENGTH_LONG).show();



                // ✅ Programar notificaciones desde clase util
                NotificacionCita.programarNotificaciones(this, nombre, hora, fecha, (int) id,imagen_id);

                limpiar();
                finish();
            } else {
                Toast.makeText(Add_Cita.this, "Error al registrar la cita", Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("PERMISOS", "Requiriendo permiso de notificaciones...");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso de notificaciones concedido", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No podrás recibir recordatorios si no activas las notificaciones", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Método para mostrar el selector de fecha
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

    // Método para mostrar el selector de hora
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

    // Método para limpiar los campos después de guardar
    private void limpiar() {
        txtNombre.setText("");
        txtFecha.setText("");
        txtHora.setText("");
    }

    // Método para mostrar el diálogo de selección de ícono manualmente
    public void showIconDialog() {
        Dialog dialog = new Dialog(this, R.style.TransparentDialog);
        dialog.setContentView(R.layout.icon_grid);

        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.y = -150;
            window.setAttributes(layoutParams);
        }

        GridView gridView = dialog.findViewById(R.id.grid_icon);

        int[] icons = {
                R.drawable.icon1, R.drawable.icon2, R.drawable.icon3, R.drawable.icon4,
                R.drawable.icon5, R.drawable.icon6, R.drawable.icon7, R.drawable.icon8,
                R.drawable.icon9, R.drawable.icon10, R.drawable.icon11, R.drawable.icon12,
                R.drawable.icon13, R.drawable.icon14, R.drawable.icon15, R.drawable.icon16,
                R.drawable.icon17, R.drawable.icon18,R.drawable.icon19, R.drawable.icon20,
                R.drawable.icon21, R.drawable.icon22

        };

        IconAdapter emojiAdapter = new IconAdapter(this, icons);
        gridView.setAdapter(emojiAdapter);

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            imagen_id = icons[position];
            icon_select.setImageResource(imagen_id);
            dialog.dismiss();
        });

        dialog.show();
    }
}




