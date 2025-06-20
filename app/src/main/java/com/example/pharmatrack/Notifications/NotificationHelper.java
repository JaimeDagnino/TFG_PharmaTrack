package com.example.pharmatrack.Notifications;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

import com.example.pharmatrack.R;

import java.util.Date;

public class NotificationHelper {

    public static final String CHANNEL_ID = "citas_channel";

    /**
     * Crea el canal de notificación, necesario en Android O y superiores.
     */
    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Citas y Medicación";
            String description = "Canal para recordatorios de citas y medicamentos";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager =
                    context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Programa una notificación sencilla (sin acciones) para dispararse en triggerAtMillis.
     */
    public static void scheduleNotification(Context context,
                                            String titulo,
                                            String mensaje,
                                            long triggerAtMillis,
                                            int requestCode,
                                            int icono) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("titulo", titulo);
        intent.putExtra("mensaje", mensaje);
        intent.putExtra("citaId", requestCode);
        intent.putExtra("icono", icono);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            Log.d("NOTIF", "Notificación programada para: " + new Date(triggerAtMillis));
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        }
    }

    /**
     * Cancela todas las alarmas/programaciones de notificaciones relacionadas con un medicamento.
     * Debes implementar NotificacionMedicamentos.cancelMedicamentoNotifications().
     */
    public static void cancelNotification(Context context, int requestCode) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    /**
     * Envía inmediatamente la notificación de que se alcanzó el umbral.
     * Esto simplemente delega en el método privado de NotificationReceiver.
     */
    public static void sendImmediateUmbralNotification(Context context, int medId) {
        // Creamos una instancia de NotificationReceiver para reutilizar su lógica
        NotificationReceiver receiver = new NotificationReceiver();
        receiver.sendImmediateUmbralNotification(context, medId);
    }
}

