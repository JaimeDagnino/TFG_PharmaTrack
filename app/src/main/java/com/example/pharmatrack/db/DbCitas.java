package com.example.pharmatrack.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.example.pharmatrack.entidades.Citas;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DbCitas extends DbHelper{

    Context context;
    public DbCitas(@Nullable Context context) {
        super(context);
        this.context = context;
    }

    public long insertarCita(String nombre, String fecha, String hora, int icono) {

        long id = 0;

        try {

            DbHelper dbHelper = new DbHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("nombre", nombre);
            values.put("fecha", fecha);
            values.put("hora", hora);
            values.put("icono", icono);

            id = db.insert("t_citas", null, values);
        } catch (Exception ex){
            ex.toString();
        }
        return id;

    }

    public ArrayList<Citas> mostrarCitas() {
        ArrayList<Citas> listaCitas = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CITAS, null);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        long ahora = System.currentTimeMillis();

        if (cursor.moveToFirst()) {
            do {
                String fecha = cursor.getString(2);
                String hora = cursor.getString(3);
                try {
                    Date citaDate = sdf.parse(fecha + " " + hora);
                    if (citaDate != null && citaDate.getTime() > ahora) {
                        Citas cita = new Citas();
                        cita.setId(cursor.getInt(0));
                        cita.setNombre(cursor.getString(1));
                        cita.setFecha(fecha);
                        cita.setHora(hora);
                        cita.setIcono(cursor.getInt(4));
                        listaCitas.add(cita);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        // ✅ Ordenar por fecha y hora
        listaCitas.sort((c1, c2) -> {
            try {
                Date d1 = sdf.parse(c1.getFecha() + " " + c1.getHora());
                Date d2 = sdf.parse(c2.getFecha() + " " + c2.getHora());
                return d1.compareTo(d2);
            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
            }
        });

        return listaCitas;
    }

    public Citas verCitas(int id) {

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Citas citas = null;
        Cursor cursorCitas = null;

        cursorCitas = db.rawQuery("SELECT * FROM "+ TABLE_CITAS + " WHERE id = " + id + " LIMIT 1", null);

        if (cursorCitas.moveToFirst()) {

            citas = new Citas();
            citas.setId(cursorCitas.getInt(0));
            citas.setNombre(cursorCitas.getString(1));
            citas.setFecha(cursorCitas.getString(2));
            citas.setHora(cursorCitas.getString(3));
            citas.setIcono(cursorCitas.getInt(4));
        }

        cursorCitas.close();
        return citas;
    }

    public boolean editarCita(int id, String nombre, String fecha, String hora, int icono) {
        boolean correcto = false;
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put("nombre", nombre);
            values.put("fecha", fecha);
            values.put("hora", hora);
            values.put("icono", icono);

            int rows = db.update("t_citas", values, "id=?", new String[]{String.valueOf(id)});
            correcto = rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            correcto = false;
        } finally {
            db.close();
        }
        return correcto;
    }
    public boolean eliminarCita(int id) {

        boolean correcto=false;

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.execSQL("DELETE FROM " + TABLE_CITAS + " WHERE id = '" + id + "'");
            correcto=true;
        } catch (Exception ex){
            ex.toString();
            correcto=false;
        }finally {
            db.close();
        }
        return correcto;

    }
    // En DbCitas.java
    public ArrayList<Citas> mostrarTodasCitas() {
        ArrayList<Citas> lista = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        // Seleccionamos *todas* las filas de t_citas, SIN FILTRAR POR FECHA/HORA.
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CITAS, null);
        if (cursor.moveToFirst()) {
            do {
                Citas c = new Citas();
                // Asegúrate de usar getColumnIndex(...) para no romper si cambian columnas.
                c.setId(cursor.getInt(cursor.getColumnIndex("id")));
                c.setNombre(cursor.getString(cursor.getColumnIndex("nombre")));
                c.setFecha(cursor.getString(cursor.getColumnIndex("fecha")));
                c.setHora(cursor.getString(cursor.getColumnIndex("hora")));
                c.setIcono(cursor.getInt(cursor.getColumnIndex("icono")));
                lista.add(c);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }
    /**
     * Devuelve todas las citas cuya fecha coincide exactamente con el String recibido.
     * (Formato "dd/MM/yyyy").
     */
    public ArrayList<Citas> mostrarCitasPorFecha(String fecha) {
        ArrayList<Citas> resultado = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_CITAS + " WHERE fecha = ?",
                new String[]{ fecha }
        );
        if (cursor.moveToFirst()) {
            do {
                Citas c = new Citas();
                c.setId(cursor.getInt(cursor.getColumnIndex("id")));
                c.setNombre(cursor.getString(cursor.getColumnIndex("nombre")));
                c.setFecha(cursor.getString(cursor.getColumnIndex("fecha")));
                c.setHora(cursor.getString(cursor.getColumnIndex("hora")));
                c.setIcono(cursor.getInt(cursor.getColumnIndex("icono")));
                resultado.add(c);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return resultado;
    }


}

