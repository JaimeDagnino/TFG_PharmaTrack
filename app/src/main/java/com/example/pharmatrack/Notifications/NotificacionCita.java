package com.example.pharmatrack.Notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.pharmatrack.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Clase para programar recordatorios de cita:
 * 1) 1 día antes
 * 2) 2 horas antes
 *
 * Usa AlarmManager.setAndAllowWhileIdle para evitar necesidad de permisos de alarmas exactas.
 */
public class NotificacionCita {

    public static final String ACTION_CITA_1D = "com.example.pharmatrack.ACTION_CITA_1D";
    public static final String ACTION_CITA_2H = "com.example.pharmatrack.ACTION_CITA_2H";

    /**
     * Programa dos notificaciones diferidas usando AlarmManager inexacto.
     */
    public static void programarNotificaciones(Context context, String titulo, String hora, String fecha, int citaId, int icono) {
        long citaMillis = convertirFechaYHoraAMillis(fecha, hora);
        long ahora = System.currentTimeMillis();
        Log.d("NotificacionCita", "Cita en: " + new Date(citaMillis));

        if (citaMillis <= ahora) {
            Log.w("NotificacionCita", "La cita ya pasó, no se programan notificaciones.");
            return;
        }

        long unDiaAntes    = citaMillis - 24 * 60 * 60 * 1000L;
        long dosHorasAntes = citaMillis -  2 * 60 * 60 * 1000L;

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        // 1 día antes
        if (unDiaAntes > ahora) {
            Intent intent1 = new Intent(context, NotificationReceiver.class)
                    .setAction(ACTION_CITA_1D)
                    .putExtra("citaId", citaId)
                    .putExtra("titulo", titulo)
                    .putExtra("mensaje", "Tienes una cita con " + titulo + " mañana a las " + hora + ".")
                    .putExtra("icono", icono);

            int requestCode1 = ("noti1_" + citaId).hashCode();
            PendingIntent pi1 = PendingIntent.getBroadcast(
                    context, requestCode1, intent1,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP, unDiaAntes, pi1
                );
            } else {
                alarmManager.set(
                        AlarmManager.RTC_WAKEUP, unDiaAntes, pi1
                );
            }
        }

        // 2 horas antes
        if (dosHorasAntes > ahora) {
            Intent intent2 = new Intent(context, NotificationReceiver.class)
                    .setAction(ACTION_CITA_2H)
                    .putExtra("citaId", citaId)
                    .putExtra("titulo", titulo)
                    .putExtra("mensaje", "Hoy tienes una cita con " + titulo + " a las " + hora + ".")
                    .putExtra("icono", icono);

            int requestCode2 = ("noti2_" + citaId).hashCode();
            PendingIntent pi2 = PendingIntent.getBroadcast(
                    context, requestCode2, intent2,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP, dosHorasAntes, pi2
                );
            } else {
                alarmManager.set(
                        AlarmManager.RTC_WAKEUP, dosHorasAntes, pi2
                );
            }
        }
    }

    /**
     * Cancela las dos notificaciones de la cita.
     */
    public static void cancelarNotificaciones(Context context, int citaId) {
        int requestCode1 = ("noti1_" + citaId).hashCode();
        int requestCode2 = ("noti2_" + citaId).hashCode();
        NotificationHelper.cancelNotification(context, requestCode1);
        NotificationHelper.cancelNotification(context, requestCode2);
    }

    private static long convertirFechaYHoraAMillis(String fecha, String hora) {
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        try {
            Date date = formato.parse(fecha + " " + hora);
            return date != null ? date.getTime() : System.currentTimeMillis();
        } catch (ParseException e) {
            e.printStackTrace();
            return System.currentTimeMillis();
        }
    }
}