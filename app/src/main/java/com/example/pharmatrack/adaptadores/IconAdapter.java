package com.example.pharmatrack.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.pharmatrack.R;


public class IconAdapter extends BaseAdapter {
    private Context context;
    private int[] icon;

    // Constructor que inicializa el adaptador con el contexto y los íconos
    public IconAdapter(Context context, int[] icon) {
        this.context = context;
        this.icon = icon;
    }

    @Override
    public int getCount() {
        return icon.length; // Devolver el número de íconos
    }

    @Override
    public Object getItem(int position) {
        return icon[position]; // Devolver el ícono en la posición especificada
    }

    @Override
    public long getItemId(int position) {
        return position; // Devolver el ID de la posición especificada
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = createImageView(parent);
        } else {
            imageView = (ImageView) convertView;
        }
        bindIconToImageView(imageView, position);
        return imageView;
    }

    // Método auxiliar para crear ImageView
    private ImageView createImageView(ViewGroup parent) {
        return (ImageView) LayoutInflater.from(context).inflate(R.layout.icon_element, parent, false);
    }

    // Método auxiliar para enlazar el ícono al ImageView
    private void bindIconToImageView(ImageView imageView, int position) {
        imageView.setImageResource(icon[position]);
    }
}
