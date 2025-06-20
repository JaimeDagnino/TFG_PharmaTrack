package com.example.pharmatrack.adaptadores;

import android.content.Context;
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

public class ListaMedicamentosAdapter
        extends RecyclerView.Adapter<ListaMedicamentosAdapter.MedicamentoViewHolder> {

    /** Listener para manejar clicks en cada ítem */
    public interface OnItemClickListener {
        void onItemClick(Medicamentos medicamento);
    }

    private final ArrayList<Medicamentos> listaOriginal;
    private final ArrayList<Medicamentos> listaMedicamentos;
    private final Context context;
    private final OnItemClickListener listener;

    /**
     * @param lista    lista completa de medicamentos
     * @param context  contexto de la aplicación
     * @param listener callback al hacer click sobre un ítem
     */
    public ListaMedicamentosAdapter(ArrayList<Medicamentos> lista,
                                    Context context,
                                    OnItemClickListener listener) {
        // Copiamos en ambas listas para soportar filtrado
        this.listaOriginal     = new ArrayList<>(lista != null ? lista : new ArrayList<>());
        this.listaMedicamentos = new ArrayList<>(listaOriginal);
        this.context           = context;
        this.listener          = listener;
    }

    @NonNull
    @Override
    public MedicamentoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.lista_item_medicamento_usuario, parent, false);
        return new MedicamentoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicamentoViewHolder holder, int position) {
        Medicamentos m = listaMedicamentos.get(position);

        holder.txtNombre.setText(m.getNombre());
        // Frecuencia textual
        int veces = m.getVecesPorDia();
        holder.txtFrecuencia.setText(
                veces + (veces == 1 ? " vez al día" : " veces al día")
        );

        // Horas
        String[] horaArray = m.getHoras().split(",");
        StringBuilder horasTexto = new StringBuilder("Horas:\n");
        for (String hora : horaArray) {
            horasTexto.append("• ").append(hora.trim()).append("\n");
        }
        holder.txtHoras.setText(horasTexto.toString().trim());

        // Días de tratamiento
        if (m.getDiasDeToma() == -1) {
            holder.txtDias.setText("Tratamiento crónico");
        } else {
            holder.txtDias.setText("Durante " + m.getDiasDeToma() + " días");
        }

        // Seguimiento cápsulas
        if (m.getCapsulasRestantes() >= 0) {
            holder.txtCapsulas.setText("Te quedan " + m.getCapsulasRestantes() + " cápsulas");
        } else {
            holder.txtCapsulas.setText("Seguimiento de cápsulas no activado");
        }

        holder.imgIcono.setImageResource(R.drawable.ic_medicamento);

        // Click sobre la CardView
        holder.itemView.setOnClickListener(v -> listener.onItemClick(m));
    }

    @Override
    public int getItemCount() {
        return listaMedicamentos.size();
    }

    /**
     * Filtra la lista por nombre (buscando coincidencias que contengan 'texto').
     */
    public void filtrar(String texto) {
        listaMedicamentos.clear();
        if (texto == null || texto.isEmpty()) {
            listaMedicamentos.addAll(listaOriginal);
        } else {
            String lower = texto.toLowerCase();
            for (Medicamentos m : listaOriginal) {
                if (m.getNombre().toLowerCase().contains(lower)) {
                    listaMedicamentos.add(m);
                }
            }
        }
        notifyDataSetChanged();
    }

    /** ViewHolder interna */
    static class MedicamentoViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtFrecuencia, txtHoras, txtCapsulas, txtDias;
        ImageView imgIcono;

        MedicamentoViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre     = itemView.findViewById(R.id.txtNombreMedicamento);
            txtFrecuencia = itemView.findViewById(R.id.txtFrecuenciaMedicamento);
            txtHoras      = itemView.findViewById(R.id.txtHorasMedicamento);
            txtDias       = itemView.findViewById(R.id.txtDiasMedicamento);
            txtCapsulas   = itemView.findViewById(R.id.txtCapsulasRestantes);
            imgIcono      = itemView.findViewById(R.id.imgIconoMedicamento);
        }
    }
}




