package com.example.pharmatrack.HashMaps;

import com.example.pharmatrack.R;

import java.util.HashMap;

public class IconMap {
    private final HashMap<Integer, Integer> iconMap;

    // Constructor que inicializa el mapa de íconos
    public IconMap() {
        iconMap = new HashMap<>();
        initializeIconMap();
    }

    // Método para inicializar el mapa de íconos
    private void initializeIconMap() {
        iconMap.put(1, R.drawable.icon1);
        iconMap.put(2, R.drawable.icon2);
        iconMap.put(3, R.drawable.icon3);
        iconMap.put(4, R.drawable.icon4);
        iconMap.put(5, R.drawable.icon5);
        iconMap.put(6, R.drawable.icon6);
        iconMap.put(7, R.drawable.icon7);
        iconMap.put(8, R.drawable.icon8);
        iconMap.put(9, R.drawable.icon9);
        iconMap.put(10, R.drawable.icon10);
        iconMap.put(11, R.drawable.icon11);
        iconMap.put(12, R.drawable.icon12);
        iconMap.put(13, R.drawable.icon13);
        iconMap.put(14, R.drawable.icon14);
        iconMap.put(15, R.drawable.icon15);
        iconMap.put(16, R.drawable.icon16);
        iconMap.put(17, R.drawable.icon17);
        iconMap.put(18, R.drawable.icon18);
        iconMap.put(19, R.drawable.icon19);

    }

    // Método para obtener el ícono correspondiente al ID de imagen
    public int getIcon(int imageId) {
        return iconMap.getOrDefault(imageId, -1);
    }
}
