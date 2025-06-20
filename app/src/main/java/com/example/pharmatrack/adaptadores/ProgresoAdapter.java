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
import com.example.pharmatrack.entidades.Medicamentos;
import com.example.pharmatrack.ui.Hoy.Toma;

import java.util.List;

/**
 * Adaptador para mostrar las CardViews de tomas en el Fragment de Progreso.
 * Igual que HoyAdapter, pero SIN funcionalidad de “marcar”/“desmarcar”.
 */
public class ProgresoAdapter extends RecyclerView.Adapter<ProgresoAdapter.ViewHolder> {

    private final Context context;
    private final DbMedicamentos dbMedicamentos;
    private List<Toma> listaTomas;

    /**
     * @param context    contexto de la Activity/Fragment
     * @param listaTomas lista de tomas (generada con getListaTomasParaFecha)
     */
    public ProgresoAdapter(Context context, List<Toma> listaTomas) {
        this.context = context;
        this.listaTomas = listaTomas;
        this.dbMedicamentos = new DbMedicamentos(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.lista_item_hoy_medicamento, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Toma toma = listaTomas.get(position);

        // 1) Hora y nombre
        holder.tvHoraHoy.setText(toma.hora);
        holder.tvNombreHoy.setText(toma.nombre);

        // 2) Dosis: ocultar si seguimiento desactivado
        Medicamentos m = dbMedicamentos.verMedicamento(toma.medId);
        if (m != null && m.getCapsulasRestantes() < 0) {
            holder.tvDosisHoy.setVisibility(View.GONE);
        } else {
            holder.tvDosisHoy.setVisibility(View.VISIBLE);
            holder.tvDosisHoy.setText(
                    TextUtils.isEmpty(toma.dosisPorToma) ? "" : toma.dosisPorToma
            );
        }

        // 3) Icono fijo de medicamento
        holder.imgIconoMedicamento.setImageResource(toma.iconoResId);

        // 4) Apariencia según estado tomado
        if (toma.tomado) {
            holder.imgIconoMedicamento.setAlpha(0.5f);
            holder.itemView.setBackgroundColor(
                    ContextCompat.getColor(context, R.color.light_gray)
            );
            holder.ivMarcarTomado.setImageResource(R.drawable.ic_circle_check);
            holder.ivMarcarTomado.setColorFilter(
                    ContextCompat.getColor(context, android.R.color.white)
            );
            holder.tvNombreHoy.setAlpha(0.5f);
            holder.tvDosisHoy.setAlpha(0.5f);
            holder.tvHoraHoy.setAlpha(0.5f);
        } else {
            holder.imgIconoMedicamento.setAlpha(1f);
            holder.itemView.setBackgroundColor(
                    ContextCompat.getColor(context, R.color.darkblue)
            );
            holder.ivMarcarTomado.setImageResource(R.drawable.ic_circle_outline);
            holder.ivMarcarTomado.setColorFilter(
                    ContextCompat.getColor(context, android.R.color.white)
            );
            holder.tvNombreHoy.setAlpha(1f);
            holder.tvDosisHoy.setAlpha(1f);
            holder.tvHoraHoy.setAlpha(1f);
            holder.tvNombreHoy.setPaintFlags(
                    holder.tvNombreHoy.getPaintFlags() &
                            (~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG)
            );
        }

        // 5) Desactivar clic en el circulo
        holder.ivMarcarTomado.setClickable(false);
    }

    @Override
    public int getItemCount() {
        return listaTomas.size();
    }

    /** ViewHolder interno */
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgIconoMedicamento;
        TextView tvNombreHoy, tvDosisHoy, tvHoraHoy;
        ImageView ivMarcarTomado;

        ViewHolder(View itemView) {
            super(itemView);
            imgIconoMedicamento = itemView.findViewById(R.id.imgIconoMedicamento);
            tvNombreHoy         = itemView.findViewById(R.id.tvNombreHoy);
            tvDosisHoy          = itemView.findViewById(R.id.tvDosisHoy);
            tvHoraHoy           = itemView.findViewById(R.id.tvHoraHoy);
            ivMarcarTomado      = itemView.findViewById(R.id.ivMarcarTomado);
        }
    }
}