package com.example.pharmatrack.Notifications;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.pharmatrack.MainActivity;
import com.example.pharmatrack.R;
import com.example.pharmatrack.db.DbMedicamentos;
import com.example.pharmatrack.entidades.Medicamentos;
import com.example.pharmatrack.Notifications.NotificacionCita;
import com.example.pharmatrack.ui.Medicamentos.ReponerMedicamento;
import com.example.pharmatrack.ui.Citas.VerCitas;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Receiver encargado de gestionar todas las notificaciones de la app:
 * 1) Medicamentos: notificación diaria y acciones de tomar/posponer/umbral
 * 2) Citas: recordatorios 1 día antes y 2 horas antes
 */
public class NotificationReceiver extends BroadcastReceiver {

    // Medicamentos
    public static final String ACTION_MED_TAKEN     = "com.example.pharmatrack.ACTION_MED_TAKEN";
    public static final String ACTION_MED_SKIPPED   = "com.example.pharmatrack.ACTION_MED_SKIPPED";
    public static final String ACTION_DISABLE_ALERT = "com.example.pharmatrack.ACTION_DISABLE_ALERT";
    public static final String ACTION_UPDATE_TOMA   = "com.example.pharmatrack.ACTION_UPDATE_TOMA";

    // Citas
    public static final String ACTION_CITA_1D = NotificacionCita.ACTION_CITA_1D;
    public static final String ACTION_CITA_2H = NotificacionCita.ACTION_CITA_2H;

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelper.createNotificationChannel(context);
        String action = intent.getAction();
        Log.d("NotificationReceiver", "onReceive action=" + action);
        String rawHora = intent.getStringExtra("rawHora");
        Log.d("NotificationReceiver", "onReceive action=" + action + " rawHora=" + rawHora);

        // 1) "No más avisos" de umbral
        if (ACTION_MED_TAKEN.equals(action)) {
            int medId = intent.getIntExtra("medId", -1);
            int req   = intent.getIntExtra("requestCode", -1);
            if (medId < 0) return;

            // Cancelamos la notificación original
            NotificationManagerCompat.from(context).cancel(req);

            // Construimos la hora de la toma: preferimos rawHora
            String horaStr = rawHora != null
                    ? rawHora
                    : String.format(Locale.getDefault(), "%02d:%02d",
                    intent.getIntExtra("hourOfDay", -1),
                    intent.getIntExtra("minute", -1));

            String fechaHoy = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    .format(new Date());

            // Registro atómico de la toma
            new DbMedicamentos(context).registrarToma(medId, fechaHoy, horaStr);

            // Broadcast para actualizar HoyFragment
            Intent upd = new Intent(ACTION_UPDATE_TOMA);
            upd.putExtra("medId", medId);
            upd.putExtra("hora", horaStr);
            context.sendBroadcast(upd);

            // (Umbral, etc…)
            Medicamentos m2 = new DbMedicamentos(context).verMedicamento(medId);
            if (m2.getUmbralAviso() >= 0 && m2.getCapsulasRestantes() <= m2.getUmbralAviso()) {
                sendImmediateUmbralNotification(context, medId);
            }
        }

        // 2) Confirmar o posponer toma de medicamento
        if (ACTION_MED_TAKEN.equals(action) || ACTION_MED_SKIPPED.equals(action)) {
            int medId        = intent.getIntExtra("medId", -1);
            int dosisPorToma = intent.getIntExtra("dosisPorToma", 1);
            int requestCode  = intent.getIntExtra("requestCode", -1);
            int hourOfDay    = intent.getIntExtra("hourOfDay", -1);
            int minute       = intent.getIntExtra("minute", -1);
            if (medId < 0) return;

            NotificationManagerCompat.from(context).cancel(requestCode);
            DbMedicamentos db = new DbMedicamentos(context);

            if (ACTION_MED_TAKEN.equals(action)) {
                // Registro atómico de la toma
                String horaStr  = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                String fechaHoy = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
                db.registrarToma(medId, fechaHoy, horaStr);

                // Notificar UI (HoyFragment)
                Intent update = new Intent(ACTION_UPDATE_TOMA);
                update.putExtra("medId", medId);
                update.putExtra("hora", horaStr);
                context.sendBroadcast(update);

                // Umbral
                Medicamentos m2 = db.verMedicamento(medId);
                if (m2.getUmbralAviso() >= 0 && m2.getCapsulasRestantes() <= m2.getUmbralAviso()) {
                    sendImmediateUmbralNotification(context, medId);
                }
            } else {
                // Posponer: reprogramar en 1 hora
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.HOUR_OF_DAY, 1);
                Intent retry = new Intent(context, NotificationReceiver.class)
                        .setAction(ACTION_MED_SKIPPED)
                        .putExtra("medId", medId)
                        .putExtra("dosisPorToma", dosisPorToma)
                        .putExtra("requestCode", requestCode)
                        .putExtra("hourOfDay", hourOfDay)
                        .putExtra("minute", minute);
                PendingIntent piRetry = PendingIntent.getBroadcast(
                        context, requestCode, retry,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );
                AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                if (am != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), piRetry);
                    } else {
                        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), piRetry);
                    }
                }
            }
            return;
        }

        // 3) Recordatorio de cita
        if (ACTION_CITA_1D.equals(action) || ACTION_CITA_2H.equals(action)) {
            int citaId = intent.getIntExtra("citaId", -1);
            String titulo = intent.getStringExtra("titulo");
            String mensaje = intent.getStringExtra("mensaje");
            int icono = intent.getIntExtra("icono", R.drawable.ic_citas);
            if (citaId < 0 || titulo == null || mensaje == null) return;
            Intent view = new Intent(context, VerCitas.class).putExtra("citaId", citaId);
            PendingIntent piView = PendingIntent.getActivity(
                    context, citaId, view,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            NotificationCompat.Builder nb = new NotificationCompat.Builder(
                    context, NotificationHelper.CHANNEL_ID)
                    .setSmallIcon(icono)
                    .setContentTitle(titulo)
                    .setContentText(mensaje)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setContentIntent(piView);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED) {
                NotificationManagerCompat.from(context).notify(citaId, nb.build());
            }
            return;
        }

        // 4) Alarma diaria de dosis (action == null)
        //    Mostrar notificación de toma diaria con acciones Sí/No y reprogramación
        if (action == null) {
            int medId        = intent.getIntExtra("medId", -1);
            int dosisPorToma = intent.getIntExtra("dosisPorToma", 1);
            int hourOfDay    = intent.getIntExtra("hourOfDay", -1);
            int minute       = intent.getIntExtra("minute", -1);
            if (medId < 0) return;

            DbMedicamentos db = new DbMedicamentos(context);
            Medicamentos m = db.verMedicamento(medId);
            if (m == null || m.getDosisTomadasHoy() >= m.getVecesPorDia()) return;

            String nombre = m.getNombre();
            String titulo = "Tomar: " + nombre + " 💊";
            String mensaje = "¿Has tomado " + nombre + "?";

            Intent main = new Intent(context, MainActivity.class)
                    .putExtra("openFragment", "medicamentos");
            main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent piMain = PendingIntent.getActivity(
                    context, medId + 200000, main,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            // Acción Sí
            Intent yesIntent = new Intent(context, NotificationReceiver.class)
                    .setAction(ACTION_MED_TAKEN)
                    .putExtra("medId", medId)
                    .putExtra("dosisPorToma", dosisPorToma)
                    .putExtra("requestCode", medId)
                    .putExtra("hourOfDay", hourOfDay)
                    .putExtra("minute", minute);
            PendingIntent piYes = PendingIntent.getBroadcast(
                    context, medId, yesIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            // Acción No
            Intent noIntent = new Intent(context, NotificationReceiver.class)
                    .setAction(ACTION_MED_SKIPPED)
                    .putExtra("medId", medId)
                    .putExtra("dosisPorToma", dosisPorToma)
                    .putExtra("requestCode", medId)
                    .putExtra("hourOfDay", hourOfDay)
                    .putExtra("minute", minute);
            PendingIntent piNo = PendingIntent.getBroadcast(
                    context, medId, noIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            NotificationCompat.Builder builder = new NotificationCompat.Builder(
                    context, NotificationHelper.CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_citas)
                    .setContentTitle(titulo)
                    .setContentText(mensaje)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setContentIntent(piMain)
                    .addAction(R.drawable.ic_check, "Sí", piYes)
                    .addAction(R.drawable.ic_close, "No", piNo);

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED) {
                NotificationManagerCompat.from(context).notify(medId, builder.build());
            }

            // Reprogramar para mañana misma hora
            if (hourOfDay >= 0 && minute >= 0) {
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                cal.set(Calendar.MINUTE, minute);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                cal.add(Calendar.DAY_OF_MONTH, 1);
                AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                if (am != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        am.setAndAllowWhileIdle(
                                AlarmManager.RTC_WAKEUP,
                                cal.getTimeInMillis(),
                                piMain // reuse or create new PendingIntent as above
                        );
                    } else {
                        am.set(
                                AlarmManager.RTC_WAKEUP,
                                cal.getTimeInMillis(),
                                piMain
                        );
                    }
                }
            }
        }
    }

    /** Envía notificación de umbral alcanzado. */
    public static void sendImmediateUmbralNotification(Context context, int medId) {
        DbMedicamentos db = new DbMedicamentos(context);
        Medicamentos m = db.verMedicamento(medId);
        if (m == null) return;
        int actuales = m.getCapsulasRestantes();
        String nombre = m.getNombre();
        String titulo = "Umbral alcanzado: " + nombre;
        String mensaje = "Solo quedan " + actuales + " cápsulas de " + nombre;

        Intent main = new Intent(context, MainActivity.class)
                .putExtra("openFragment", "medicamentos");
        PendingIntent piMain = PendingIntent.getActivity(
                context, medId + 300000, main,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder nb = new NotificationCompat.Builder(
                context, NotificationHelper.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_citas)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(piMain);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(context).notify(medId + 100000, nb.build());
        }
    }
}