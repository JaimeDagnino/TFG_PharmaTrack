package com.example.pharmatrack.HashMaps;

import com.example.pharmatrack.R;

import java.util.HashMap;

public class EspecialidadMap {

    private static final HashMap<String, Integer> ESPECIALIDAD_ICONO_MAP = new HashMap<>();

    static {
        ESPECIALIDAD_ICONO_MAP.put("Medicina General / Medicina Familiar", R.drawable.icon1);
        ESPECIALIDAD_ICONO_MAP.put("Pediatría", R.drawable.icon2);
        ESPECIALIDAD_ICONO_MAP.put("Ginecología", R.drawable.icon3);
        ESPECIALIDAD_ICONO_MAP.put("Cardiología", R.drawable.icon4);
        ESPECIALIDAD_ICONO_MAP.put("Dermatología", R.drawable.icon5);
        ESPECIALIDAD_ICONO_MAP.put("Neurología", R.drawable.icon6);
        ESPECIALIDAD_ICONO_MAP.put("Psiquiatría", R.drawable.icon7);
        ESPECIALIDAD_ICONO_MAP.put("Oftalmología", R.drawable.icon8);
        ESPECIALIDAD_ICONO_MAP.put("Otorrinolaringología", R.drawable.icon9);
        ESPECIALIDAD_ICONO_MAP.put("Traumatología", R.drawable.icon10);
        ESPECIALIDAD_ICONO_MAP.put("Endocrinología", R.drawable.icon11);
        ESPECIALIDAD_ICONO_MAP.put("Gastroenterología", R.drawable.icon12);
        ESPECIALIDAD_ICONO_MAP.put("Neumología", R.drawable.icon13);
        ESPECIALIDAD_ICONO_MAP.put("Urología", R.drawable.icon14);
        ESPECIALIDAD_ICONO_MAP.put("Oncología", R.drawable.icon15);
        ESPECIALIDAD_ICONO_MAP.put("Nefrología", R.drawable.icon16);
        ESPECIALIDAD_ICONO_MAP.put("Reumatología", R.drawable.icon17);
        ESPECIALIDAD_ICONO_MAP.put("Anestesiología", R.drawable.icon1);
        ESPECIALIDAD_ICONO_MAP.put("Radiología", R.drawable.icon18);
        ESPECIALIDAD_ICONO_MAP.put("Hematología", R.drawable.icon19);
        ESPECIALIDAD_ICONO_MAP.put("Alergología", R.drawable.icon20);
        ESPECIALIDAD_ICONO_MAP.put("Geriatría", R.drawable.icon1);
        ESPECIALIDAD_ICONO_MAP.put("Cirugía General", R.drawable.icon21);
        ESPECIALIDAD_ICONO_MAP.put("Neurocirugía", R.drawable.icon21);
        ESPECIALIDAD_ICONO_MAP.put("Angiología y Cirugía Vascular", R.drawable.icon21);
        ESPECIALIDAD_ICONO_MAP.put("Infectología", R.drawable.icon22);
        ESPECIALIDAD_ICONO_MAP.put("Patología", R.drawable.icon22);
    }

    // Método para obtener el icono asociado a una especialidad
    public static int getEspecialidadIcon(String especialidad) {
        return ESPECIALIDAD_ICONO_MAP.getOrDefault(especialidad, R.drawable.ic_citas);
    }
}