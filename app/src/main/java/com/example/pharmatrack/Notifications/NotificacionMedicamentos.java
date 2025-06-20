package com.example.pharmatrack.Notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.pharmatrack.R;

import java.util.Calendar;

/**
 * Gestiona la programación y cancelación de alarmas para notificaciones de medicamentos:
 *  - Alarmas diarias en cada hora de toma
 *  - Alarma de umbral de cápsulas
 */
public class NotificacionMedicamentos {

    /**
     * Programa las alarmas diarias y de umbral para un medicamento.
     */
    public static void scheduleMedicamentoNotifications(
            Context context,
            int medId,
            String horasString,
            int vecesPorDia,
            int dosisPorToma,
            int capsulasRestantesVal,
            int umbralAvisoVal,
            int diasDeTomaVal
    ) {
        Log.d("DEBUG_NOTIF_MED", "↴↴ scheduleMedicamentoNotifications INVOCADO: medId="
                + medId + ", horas=" + horasString
                + ", vecesPorDia=" + vecesPorDia
                + ", dosisPorToma=" + dosisPorToma
                + ", capsulas=" + capsulasRestantesVal
                + ", umbral=" + umbralAvisoVal
                + ", diasDeToma=" + diasDeTomaVal);

        String[] arrHoras = horasString.split(",");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // 1) Alarmas diarias en cada hora de toma
        for (int i = 0; i < arrHoras.length; i++) {
            String horaTrim = arrHoras[i].trim();
            if (horaTrim.isEmpty()) continue;

            String[] hm = horaTrim.split(":");
            int hourOfDay = Integer.parseInt(hm[0]);
            int minute    = Integer.parseInt(hm[1]);

            Calendar now = Calendar.getInstance();
            Calendar alarmCal = Calendar.getInstance();
            alarmCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
            alarmCal.set(Calendar.MINUTE, minute);
            alarmCal.set(Calendar.SECOND, 0);
            alarmCal.set(Calendar.MILLISECOND, 0);
            if (alarmCal.getTimeInMillis() <= now.getTimeInMillis()) {
                alarmCal.add(Calendar.DAY_OF_MONTH, 1);
            }

            int requestCode = medId * 10 + i;
            Intent intent = new Intent(context, NotificationReceiver.class)
                    .putExtra("medId", medId)
                    .putExtra("dosisPorToma", dosisPorToma)
                    .putExtra("hourOfDay", hourOfDay)
                    .putExtra("minute", minute);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            Log.d("DEBUG_NOTIF_MED", "≻≻ Agendada alarma diaria para medId=" + medId
                    + " hora=" + hourOfDay + ":" + minute
                    + " requestCode=" + requestCode
                    + " millis=" + alarmCal.getTimeInMillis());

            if (alarmManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            alarmCal.getTimeInMillis(),
                            pendingIntent
                    );
                } else {
                    alarmManager.set(
                            AlarmManager.RTC_WAKEUP,
                            alarmCal.getTimeInMillis(),
                            pendingIntent
                    );
                }
            }
        }

        // 2) Alarma de umbral, si procede
        if (umbralAvisoVal >= 0
                && capsulasRestantesVal > umbralAvisoVal
                && dosisPorToma > 0
                && arrHoras.length > 0
                && alarmManager != null) {

            int tomasFaltan = (capsulasRestantesVal - umbralAvisoVal) / dosisPorToma;
            int diasEnteros = (int) Math.ceil((double) tomasFaltan / vecesPorDia);

            String primeraHora = arrHoras[0].trim();
            String[] hm0 = primeraHora.split(":");
            int hourPrimera = Integer.parseInt(hm0[0]);
            int minPrimera  = Integer.parseInt(hm0[1]);

            Calendar calUmbral = Calendar.getInstance();
            calUmbral.set(Calendar.HOUR_OF_DAY, hourPrimera);
            calUmbral.set(Calendar.MINUTE, minPrimera);
            calUmbral.set(Calendar.SECOND, 0);
            calUmbral.set(Calendar.MILLISECOND, 0);
            calUmbral.add(Calendar.DAY_OF_MONTH, diasEnteros);

            int requestCodeUmbral = medId + 100000;
            Intent intentUmbral = new Intent(context, NotificationReceiver.class)
                    .putExtra("medId", medId)
                    .putExtra("umbralAviso", umbralAvisoVal);
            PendingIntent pendingUmbral = PendingIntent.getBroadcast(
                    context,
                    requestCodeUmbral,
                    intentUmbral,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            Log.d("DEBUG_NOTIF_MED", "≻≻ Agendada alarma umbral para medId=" + medId
                    + " en " + calUmbral.getTime());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calUmbral.getTimeInMillis(),
                        pendingUmbral
                );
            } else {
                alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        calUmbral.getTimeInMillis(),
                        pendingUmbral
                );
            }
        }
    }

    /**
     * Cancela todas las alarmas de un medicamento (diarias y de umbral).
     */
    public static void cancelMedicamentoNotifications(
            Context context,
            int medId,
            String horasString
    ) {
        String[] arrHoras = horasString.split(",");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        for (int i = 0; i < arrHoras.length; i++) {
            int requestCode = medId * 10 + i;
            Intent intent = new Intent(context, NotificationReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            if (alarmManager != null) alarmManager.cancel(pi);
        }

        int requestCodeUmbral = medId + 100000;
        Intent intentUmbral = new Intent(context, NotificationReceiver.class);
        PendingIntent piUmbral = PendingIntent.getBroadcast(
                context,
                requestCodeUmbral,
                intentUmbral,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        if (alarmManager != null) alarmManager.cancel(piUmbral);
    }
}