package com.example.pharmatrack.adaptadores;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pharmatrack.R;
import com.example.pharmatrack.db.DbMedicamentos;
import com.example.pharmatrack.ui.Hoy.Toma;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class HoyAdapter extends RecyclerView.Adapter<HoyAdapter.ViewHolder> {

    private final Context context;
    private final DbMedicamentos dbMedicamentos;
    private List<Toma> listaTomas;
    private final TextView tvContador;

    public HoyAdapter(Context context, List<Toma> listaTomas, TextView tvContador) {
        this.context = context;
        this.listaTomas = listaTomas;
        this.tvContador = tvContador;
        this.dbMedicamentos = new DbMedicamentos(context);
        actualizarContadorInterno();
    }

    public void updateList(List<Toma> nuevaLista) {
        this.listaTomas = nuevaLista;
        actualizarContadorInterno();
        notifyDataSetChanged();
    }

    public void actualizarContadorInterno() {
        int total = listaTomas.size(), tomados = 0;
        for (Toma t : listaTomas) if (t.tomado) tomados++;
        tvContador.setText(tomados + " de " + total);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.lista_item_hoy_medicamento, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Toma toma = listaTomas.get(position);
        holder.tvHoraHoy.setText(toma.hora);
        holder.tvNombreHoy.setText(toma.nombre);
        holder.tvDosisHoy.setText(TextUtils.isEmpty(toma.dosisPorToma) ? "" : toma.dosisPorToma);
        holder.imgIconoMedicamento.setImageResource(toma.iconoResId);

        // Apariencia
        if (toma.tomado) {
            holder.imgIconoMedicamento.setAlpha(0.5f);
            holder.itemView.setBackgroundColor(
                    ContextCompat.getColor(context, R.color.light_gray));
            holder.ivMarcarTomado.setImageResource(R.drawable.ic_circle_check);
            holder.tvNombreHoy.setAlpha(0.5f);
            holder.tvDosisHoy.setAlpha(0.5f);
            holder.tvHoraHoy.setAlpha(0.5f);
        } else {
            holder.imgIconoMedicamento.setAlpha(1f);
            holder.itemView.setBackgroundColor(
                    ContextCompat.getColor(context, R.color.darkblue));
            holder.ivMarcarTomado.setImageResource(R.drawable.ic_circle_outline);
            holder.tvNombreHoy.setAlpha(1f);
            holder.tvDosisHoy.setAlpha(1f);
            holder.tvHoraHoy.setAlpha(1f);
            holder.tvNombreHoy.setPaintFlags(
                    holder.tvNombreHoy.getPaintFlags() &
                            (~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG));
        }

        // Listener: marcar/desmarcar con control de histórico
        holder.ivMarcarTomado.setOnClickListener(v -> {
            String fechaHoy = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    .format(new Date());
            boolean changed = false;

            if (!toma.tomado) {
                // Si aún no está en la BD, restamos;
                // si ya existe, solo marcamos
                Set<String> tomadas = dbMedicamentos
                        .getHorasTomadasParaMedEnFecha(toma.medId, fechaHoy);
                if (!tomadas.contains(toma.hora)) {
                    dbMedicamentos.registrarToma(toma.medId, fechaHoy, toma.hora);
                }
                toma.tomado = true;
                changed = true;

            } else {
                // Deshacer: devolver cápsulas
                boolean undone = dbMedicamentos.desmarcarToma(
                        toma.medId, fechaHoy, toma.hora);
                if (undone) {
                    toma.tomado = false;
                    changed = true;
                }
            }

            if (changed) {
                notifyItemChanged(position);
                actualizarContadorInterno();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaTomas.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgIconoMedicamento, ivMarcarTomado;
        TextView tvNombreHoy, tvDosisHoy, tvHoraHoy;

        ViewHolder(View itemView) {
            super(itemView);
            imgIconoMedicamento = itemView.findViewById(R.id.imgIconoMedicamento);
            tvNombreHoy = itemView.findViewById(R.id.tvNombreHoy);
            tvDosisHoy = itemView.findViewById(R.id.tvDosisHoy);
            tvHoraHoy = itemView.findViewById(R.id.tvHoraHoy);
            ivMarcarTomado = itemView.findViewById(R.id.ivMarcarTomado);
        }
    }
}