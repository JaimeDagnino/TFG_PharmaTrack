package com.example.pharmatrack.adaptadores;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pharmatrack.R;
import com.example.pharmatrack.ui.Citas.VerCitas;
import com.example.pharmatrack.entidades.Citas;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ListaCitasAdapter extends RecyclerView.Adapter<ListaCitasAdapter.CitaViewHolder> {

    public ArrayList<Citas> listaCitas;
    public ArrayList<Citas> listaOriginal;

    public ListaCitasAdapter(ArrayList<Citas> listaCitas) {
        this.listaCitas = listaCitas;
        listaOriginal = new ArrayList<>();
        listaOriginal.addAll(listaCitas);
    }

    public void filtrado(String txtBuscar) {
        int longitud = txtBuscar.length();
        listaCitas.clear(); //

        if (longitud == 0) {
            listaCitas.addAll(listaOriginal); //
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                List<Citas> collection = listaOriginal.stream()
                        .filter(i -> i.getNombre().toLowerCase().contains(txtBuscar.toLowerCase()))
                        .collect(Collectors.toList());

                listaCitas.addAll(collection);
            } else {
                for (Citas c : listaOriginal) {
                    if (c.getNombre().toLowerCase().contains(txtBuscar.toLowerCase())) {
                        listaCitas.add(c);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CitaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_item_cita, null, false);
        return new CitaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CitaViewHolder holder, int position) {
        Citas cita = listaCitas.get(position);
        holder.viewNombre.setText(cita.getNombre());
        holder.viewHora.setText(cita.getHora());
        holder.viewFecha.setText(cita.getFecha());

        int iconRes = cita.getIcono();
        if (iconRes != 0) {
            // Comprobamos que iconRes realmente existe y es un drawable
            try {
                // Resources.getResourceTypeName() explota si iconRes no existe
                String tipo = holder.viewIcono.getContext()
                        .getResources()
                        .getResourceTypeName(iconRes);
                if ("drawable".equals(tipo)) {
                    holder.viewIcono.setImageResource(iconRes);
                } else {
                    // Si no es 'drawable', ponemos el icono por defecto
                    holder.viewIcono.setImageResource(R.drawable.ic_citas);
                }
            } catch (Exception e) {
                // Si falla la llamada a getResourceTypeName (recurso inexistente, etc.)
                holder.viewIcono.setImageResource(R.drawable.ic_citas);
            }
        } else {
            // icono 0 ⇒ usamos el icono por defecto
            holder.viewIcono.setImageResource(R.drawable.ic_citas);
        }
    }

    @Override
    public int getItemCount() {
        return listaCitas.size();
    }

    public class CitaViewHolder extends RecyclerView.ViewHolder {

        TextView viewNombre, viewHora, viewFecha;
        ImageView viewIcono; //

        public CitaViewHolder(@NonNull View itemView) {
            super(itemView);

            viewNombre = itemView.findViewById(R.id.viewNombre);
            viewHora = itemView.findViewById(R.id.viewHora);
            viewFecha = itemView.findViewById(R.id.viewFecha);
            viewIcono = itemView.findViewById(R.id.viewIcono); //

            itemView.setOnClickListener(v -> {
                Context context = v.getContext();
                Intent intent = new Intent(context, VerCitas.class);
                intent.putExtra("ID", listaCitas.get(getAdapterPosition()).getId());
                context.startActivity(intent);
            });
        }
    }
}
