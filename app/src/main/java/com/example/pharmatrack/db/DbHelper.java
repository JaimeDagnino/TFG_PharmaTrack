package com.example.pharmatrack.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * Crea las tablas:
 *  - t_citas
 *  - t_medicamentos (ahora con fecha_inicio)
 *  - t_tomas_tomadas
 */
public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 23;
    private static final String DATABASE_NOMBRE  = "citas.db";

    public static final String TABLE_CITAS       = "t_citas";
    public static final String TABLE_MEDICAMENTOS = "t_medicamentos";
    public static final String TABLE_TOMAS_TOMADAS = "t_tomas_tomadas";

    public DbHelper(@Nullable Context context) {
        super(context, DATABASE_NOMBRE, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createCitasTable(db);
        createMedicamentosTable(db);
        createTomasTomadasTable(db);
    }

    private void createCitasTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_CITAS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT NOT NULL," +
                "fecha TEXT NOT NULL," +
                "hora TEXT NOT NULL," +
                "icono INTEGER NOT NULL" +
                ")");
    }

    /**
     * Ahora incluimos:
     *  - fecha_inicio: TEXT NOT NULL (formato "dd/MM/yyyy")
     *  - dias_de_toma: INTEGER  (–1 si es crónico)
     */
    private void createMedicamentosTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_MEDICAMENTOS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT NOT NULL," +
                "dosis TEXT," +
                "veces_por_dia INTEGER NOT NULL," +
                "horas TEXT NOT NULL," +
                "dias_de_toma INTEGER," +
                "capsulas_restantes INTEGER," +
                "dosis_por_toma INTEGER," +
                "comprimidos_caja INTEGER," +
                "umbral_aviso INTEGER," +
                "dosis_tomadas_hoy INTEGER DEFAULT 0," +
                "fecha_inicio TEXT NOT NULL" +
                ")");
    }

    private void createTomasTomadasTable(SQLiteDatabase db) {
        // Guardaremos: id (autoincrement), med_id, fecha (dd/MM/yyyy), hora (HH:mm)
        db.execSQL("CREATE TABLE " + TABLE_TOMAS_TOMADAS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "med_id INTEGER NOT NULL," +
                "fecha TEXT NOT NULL," +
                "hora TEXT NOT NULL" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Al subir de versión, destruimos todo y volvemos a crear (lo perdemos todo).
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CITAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDICAMENTOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOMAS_TOMADAS);
        onCreate(db);
    }
}

