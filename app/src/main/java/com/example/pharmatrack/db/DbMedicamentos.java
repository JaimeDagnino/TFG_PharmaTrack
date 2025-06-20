package com.example.pharmatrack.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.Nullable;
import com.example.pharmatrack.R;
import com.example.pharmatrack.entidades.Medicamentos;
import com.example.pharmatrack.ui.Hoy.Toma;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Gestión de medicamentos y sus tomas, con histórico y umbral.
 */
public class DbMedicamentos extends DbHelper {

    private static final DateTimeFormatter DTF =
            DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault());
    private static final String TABLE = "t_medicamentos";
    private static final String TABLE_TOMAS = "t_tomas_tomadas";

    public DbMedicamentos(@Nullable Context context) {
        super(context);
    }

    /** Inserta un nuevo medicamento y guarda fecha_inicio=hoy. */
    public long insertarMedicamento(String nombre,
                                    String dosis,
                                    int vecesPorDia,
                                    String horas,
                                    int capsRest,
                                    int dosisPorToma,
                                    int comprimidosCaja,
                                    int umbralAviso,
                                    int diasDeToma) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("dosis", dosis);
        values.put("veces_por_dia", vecesPorDia);
        values.put("horas", horas);
        values.put("dias_de_toma", diasDeToma);
        values.put("capsulas_restantes", capsRest);
        values.put("dosis_por_toma", dosisPorToma);
        values.put("comprimidos_caja", comprimidosCaja);
        values.put("umbral_aviso", umbralAviso);
        values.put("dosis_tomadas_hoy", 0);
        String hoy = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        values.put("fecha_inicio", hoy);
        return db.insert(TABLE, null, values);
    }

    /** Recupera un medicamento por ID. */
    public Medicamentos verMedicamento(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE + " WHERE id = ?", new String[]{String.valueOf(id)});
        Medicamentos m = null;
        if (c.moveToFirst()) {
            m = new Medicamentos();
            m.setId(c.getInt(c.getColumnIndexOrThrow("id")));
            m.setNombre(c.getString(c.getColumnIndexOrThrow("nombre")));
            m.setDosis(c.getString(c.getColumnIndexOrThrow("dosis")));
            m.setVecesPorDia(c.getInt(c.getColumnIndexOrThrow("veces_por_dia")));
            m.setHoras(c.getString(c.getColumnIndexOrThrow("horas")));
            m.setDiasDeToma(c.getInt(c.getColumnIndexOrThrow("dias_de_toma")));
            m.setCapsulasRestantes(c.getInt(c.getColumnIndexOrThrow("capsulas_restantes")));
            m.setDosisPorToma(c.getInt(c.getColumnIndexOrThrow("dosis_por_toma")));
            m.setComprimidosPorCaja(c.getInt(c.getColumnIndexOrThrow("comprimidos_caja")));
            m.setUmbralAviso(c.getInt(c.getColumnIndexOrThrow("umbral_aviso")));
            m.setDosisTomadasHoy(c.getInt(c.getColumnIndexOrThrow("dosis_tomadas_hoy")));
            m.setFechaInicio(c.getString(c.getColumnIndexOrThrow("fecha_inicio")));
        }
        c.close();
        return m;
    }

    /** Actualiza un medicamento (sin modificar fecha_inicio). */
    public boolean editarMedicamento(int id,
                                     String nombre,
                                     String dosis,
                                     int vecesPorDia,
                                     String horas,
                                     int diasDeToma,
                                     int capsRest,
                                     int dosisPorToma,
                                     int comprimidosCaja,
                                     int umbralAviso,
                                     int dosisHoy) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("nombre", nombre);
        v.put("dosis", dosis);
        v.put("veces_por_dia", vecesPorDia);
        v.put("horas", horas);
        v.put("dias_de_toma", diasDeToma);
        v.put("capsulas_restantes", capsRest);
        v.put("dosis_por_toma", dosisPorToma);
        v.put("comprimidos_caja", comprimidosCaja);
        v.put("umbral_aviso", umbralAviso);
        v.put("dosis_tomadas_hoy", dosisHoy);
        return db.update(TABLE, v, "id = ?", new String[]{String.valueOf(id)}) > 0;
    }

    /** Elimina un medicamento y su histórico de tomas. */
    public boolean eliminarMedicamento(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            int filas = db.delete(TABLE, "id = ?", new String[]{String.valueOf(id)});
            db.delete(TABLE_TOMAS, "med_id = ?", new String[]{String.valueOf(id)});
            db.setTransactionSuccessful();
            return filas > 0;
        } finally {
            db.endTransaction();
        }
    }

    /** Lista todos los medicamentos. */
    public List<Medicamentos> mostrarMedicamentos() {
        ArrayList<Medicamentos> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE, null);
        if (c.moveToFirst()) {
            do {
                list.add(verMedicamento(c.getInt(c.getColumnIndexOrThrow("id"))));
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    /** Registra una toma: resta cápsulas, incrementa dosis hoy, guarda y registra histórico. */
    /**
     * Registra una toma únicamente si no existe ya hoy:
     *  - Evita restas dobles
     *  - Resta capsulas, incrementa dosisHoy y guarda histórico
     */
    /**
     * Registra una toma únicamente si el seguimiento está activo:
     *  - Evita restas dobles
     *  - No registra nada si capsulas_restantes < 0 (seguimiento desactivado)
     */
    public void registrarToma(int medId, String fecha, String hora) {
        // 0) Leemos estado actual
        Medicamentos m = verMedicamento(medId);
        if (m == null) return;

        // 1) Si capsulas_restantes es negativo, el seguimiento está desactivado: no hacemos nada
        if (m.getCapsulasRestantes() < 0) {
            Log.d("DEBUG_REG_TOMA", "Seguimiento desactivado, no registrar toma medId=" + medId);
            marcarTomaHistorico(medId, fecha, hora);
            return;
        }

        // 2) Protección contra duplicados: si ya hay registro hoy, salimos
        Set<String> yaTomadas = getHorasTomadasParaMedEnFecha(medId, fecha);
        if (yaTomadas.contains(hora)) {
            Log.d("DEBUG_REG_TOMA", "Toma duplicada ignorada medId=" + medId + " hora=" + hora);
            return;
        }

        // 3) Calculamos cuántas cápsulas quedan
        int actuales    = m.getCapsulasRestantes();
        int dosisPorToma= m.getDosisPorToma();
        int nuevas      = Math.max(0, actuales - dosisPorToma);

        // 4) Log para depurar
        Log.d("DEBUG_REG_TOMA", "medId=" + medId
                + " cápsulas antes=" + actuales
                + " dosisPorToma=" + dosisPorToma
                + " cápsulas nuevas=" + nuevas);

        // 5) Actualizamos t_medicamentos
        m.setCapsulasRestantes(nuevas);
        m.setDosisTomadasHoy(m.getDosisTomadasHoy() + 1);
        editarMedicamento(
                medId,
                m.getNombre(),
                m.getDosis(),
                m.getVecesPorDia(),
                m.getHoras(),
                m.getDiasDeToma(),
                nuevas,
                m.getDosisPorToma(),
                m.getComprimidosPorCaja(),
                m.getUmbralAviso(),
                m.getDosisTomadasHoy()
        );

        // 6) Insertamos en t_tomas_tomadas
        marcarTomaHistorico(medId, fecha, hora);
    }

    /** Desmarca una toma: borra del histórico y suma cápsulas. */
    public boolean desmarcarToma(int medId, String fecha, String hora) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            int del = db.delete(TABLE_TOMAS, "med_id = ? AND fecha = ? AND hora = ?",
                    new String[]{String.valueOf(medId), fecha, hora});
            if (del <= 0) return false;
            db.execSQL("UPDATE " + TABLE +
                    " SET capsulas_restantes = capsulas_restantes + dosis_por_toma" +
                    " WHERE id = ? AND capsulas_restantes >= 0", new Object[]{medId});
            db.setTransactionSuccessful();
            return true;
        } finally {
            db.endTransaction();
        }
    }

    /** Inserta una toma en histórico sin modificar cápsulas. */
    private long marcarTomaHistorico(int medId, String fecha, String hora) {
        ContentValues v = new ContentValues();
        v.put("med_id", medId);
        v.put("fecha", fecha);
        v.put("hora", hora);
        return getWritableDatabase().insert(TABLE_TOMAS, null, v);
    }

    /** Devuelve las horas tomadas para una fecha dada. */
    public Set<String> getHorasTomadasParaMedEnFecha(int medId, String fecha) {
        Set<String> horas = new HashSet<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT hora FROM " + TABLE_TOMAS + " WHERE med_id = ? AND fecha = ?",
                new String[]{String.valueOf(medId), fecha}
        );
        if (c.moveToFirst()) {
            do { horas.add(c.getString(c.getColumnIndexOrThrow("hora"))); } while (c.moveToNext());
        }
        c.close();
        return horas;
    }

    /** Cuenta cuántas tomas se han registrado en una fecha. */
    public int getTomasTomadasParaFecha(String fecha) {
        int count = 0;
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_TOMAS + " WHERE fecha = ?",
                new String[]{fecha}
        );
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        return count;
    }

    /** Total de tomas previstas para la fecha (según días de toma). */
    public int getTotalTomasParaFecha(String fecha) {
        LocalDate consulta;
        try { consulta = LocalDate.parse(fecha, DTF); }
        catch (DateTimeParseException e) { return 0; }
        int total = 0;
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT veces_por_dia, dias_de_toma, fecha_inicio FROM " + TABLE, null
        );
        if (c.moveToFirst()) {
            do {
                int veces = c.getInt(c.getColumnIndexOrThrow("veces_por_dia"));
                int dias = c.getInt(c.getColumnIndexOrThrow("dias_de_toma"));
                String ini = c.getString(c.getColumnIndexOrThrow("fecha_inicio"));
                LocalDate inicio;
                try { inicio = LocalDate.parse(ini, DTF); }
                catch (DateTimeParseException ex) { continue; }
                boolean activo;
                if (dias == -1) activo = !consulta.isBefore(inicio);
                else activo = !consulta.isBefore(inicio) && !consulta.isAfter(inicio.plusDays(dias - 1));
                if (activo) total += veces;
            } while (c.moveToNext());
        }
        c.close();
        return total;
    }

    /** Genera lista de tomas (Toma) para la fecha indicada. */
    public ArrayList<Toma> getListaTomasParaFecha(String fecha) {
        ArrayList<Toma> resultado = new ArrayList<>();
        LocalDate consulta;
        try { consulta = LocalDate.parse(fecha, DTF); }
        catch (DateTimeParseException e) { return resultado; }
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT id,nombre,veces_por_dia,horas,dias_de_toma,fecha_inicio,dosis_por_toma FROM " + TABLE,
                null
        );
        if (c.moveToFirst()) {
            do {
                int medId = c.getInt(c.getColumnIndexOrThrow("id"));
                String nombre = c.getString(c.getColumnIndexOrThrow("nombre"));
                int veces = c.getInt(c.getColumnIndexOrThrow("veces_por_dia"));
                String horas = c.getString(c.getColumnIndexOrThrow("horas"));
                int dias = c.getInt(c.getColumnIndexOrThrow("dias_de_toma"));
                String ini = c.getString(c.getColumnIndexOrThrow("fecha_inicio"));
                int dosisPorToma = c.getInt(c.getColumnIndexOrThrow("dosis_por_toma"));
                LocalDate inicio;
                try { inicio = LocalDate.parse(ini, DTF); }
                catch (DateTimeParseException ex) { continue; }
                boolean activo;
                if (dias == -1) activo = !consulta.isBefore(inicio);
                else activo = !consulta.isBefore(inicio) && !consulta.isAfter(inicio.plusDays(dias - 1));
                if (!activo) continue;
                if (horas == null || horas.isEmpty()) continue;
                String[] arrHoras = horas.split(",");
                Set<String> tomadas = getHorasTomadasParaMedEnFecha(medId, fecha);
                for (String hs : arrHoras) {
                    String hora = hs.trim();
                    if (hora.isEmpty()) continue;
                    Toma t = new Toma();
                    t.medId = medId;
                    t.nombre = nombre;
                    t.hora = hora;
                    t.dosisPorToma = dosisPorToma + " cápsula(s)";
                    t.vecesPorDia = veces;
                    t.iconoResId = R.drawable.ic_medicamento;
                    t.tomado = tomadas.contains(hora);
                    resultado.add(t);
                }
            } while (c.moveToNext());
        }
        c.close();
        Collections.sort(resultado, Comparator.comparing(o -> o.hora));
        return resultado;
    }

    /** Cuenta medicamentos activos en fecha dada. */
    public int getTotalMedicamentosParaFecha(String fecha) {
        LocalDate consulta;
        try { consulta = LocalDate.parse(fecha, DTF); }
        catch (DateTimeParseException e) { return 0; }
        int count = 0;
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT dias_de_toma, fecha_inicio FROM " + TABLE, null);
        if (c.moveToFirst()) {
            do {
                int dias = c.getInt(c.getColumnIndexOrThrow("dias_de_toma"));
                String ini = c.getString(c.getColumnIndexOrThrow("fecha_inicio"));
                LocalDate inicio;
                try { inicio = LocalDate.parse(ini, DTF); }
                catch (DateTimeParseException ex) { continue; }
                boolean activo = (dias == -1)
                        ? !consulta.isBefore(inicio)
                        : (!consulta.isBefore(inicio) && !consulta.isAfter(inicio.plusDays(dias - 1)));
                if (activo) count++;
            } while (c.moveToNext());
        }
        c.close();
        return count;
    }

    /** Cuenta cuántos medicamentos distintos han sido tomados en la fecha dada. */
    public int getMedicamentosTomadosParaFecha(String fecha) {
        int taken = 0;
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT COUNT(DISTINCT med_id) FROM " + TABLE_TOMAS + " WHERE fecha = ?",
                new String[]{fecha}
        );
        if (c.moveToFirst()) taken = c.getInt(0);
        c.close();
        return taken;
    }
}






