package com.example.pharmatrack.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pharmatrack.R;
import com.example.pharmatrack.entidades.Medicamentos;

import java.util.ArrayList;

public class ListaMedicamentosPredefinidosAdapter extends RecyclerView.Adapter<ListaMedicamentosPredefinidosAdapter.ViewHolder> {

    private final ArrayList<Medicamentos> listaOriginal;
    private final ArrayList<Medicamentos> listaFiltrada;
    private final OnMedicamentoClickListener listener;

    public interface OnMedicamentoClickListener {
        void onMedicamentoClick(Medicamentos medicamento);
    }

    public ListaMedicamentosPredefinidosAdapter(ArrayList<Medicamentos> lista, OnMedicamentoClickListener listener) {
        this.listaOriginal = lista;
        this.listaFiltrada = new ArrayList<>(lista);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_item_medicamento, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Medicamentos m = listaFiltrada.get(position);
        holder.nombre.setText(m.getNombre());
        holder.icono.setImageResource(m.getIcono());

        holder.itemView.setOnClickListener(v -> listener.onMedicamentoClick(m));
    }

    @Override
    public int getItemCount() {
        return listaFiltrada.size();
    }

    public void filtrado(String texto) {
        listaFiltrada.clear();
        if (texto.isEmpty()) {
            listaFiltrada.addAll(listaOriginal);
        } else {
            for (Medicamentos m : listaOriginal) {
                if (m.getNombre().toLowerCase().contains(texto.toLowerCase())) {
                    listaFiltrada.add(m);
                }
            }
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombre;
        ImageView icono;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.txtNombreMedicamento);
            icono = itemView.findViewById(R.id.imgIconoMedicamento);
        }
    }

}


