package com.gallegos.seesavi;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CalendarioAdapter extends RecyclerView.Adapter<CalendarioAdapter.ViewHolder> {

    private List<DiaCalendario> listaDias;
    private OnDiaClickListener listener;

    public interface OnDiaClickListener {
        void onDiaClick(String fechaFomateada);
    }

    public CalendarioAdapter(List<DiaCalendario> listaDias, OnDiaClickListener listener) {
        this.listaDias = listaDias;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dia, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DiaCalendario dia = listaDias.get(position);
        holder.txtNumero.setText(dia.numeroDia);

        if (dia.numeroDia.isEmpty()) {
            holder.card.setCardBackgroundColor(Color.TRANSPARENT);
            holder.card.setOnClickListener(null);
        } else {
            holder.card.setCardBackgroundColor(dia.colorFondo);

            if (dia.colorFondo != Color.TRANSPARENT) {
                holder.txtNumero.setTextColor(Color.WHITE);
            } else {
                holder.txtNumero.setTextColor(Color.BLACK);
            }

            // PERMITIR CLIC EN CUALQUIER DÍA EXCEPTO FINES DE SEMANA (ROJO)
            holder.card.setOnClickListener(v -> {
                if (dia.colorFondo == Color.parseColor("#F44336")) { // Rojo
                    Toast.makeText(v.getContext(), "Inactivo: Fin de semana", Toast.LENGTH_SHORT).show();
                } else {
                    // Envía la fecha sin importar si es verde, azul, gris o naranja
                    listener.onDiaClick(dia.fechaCompleta);
                }
            });
        }
    }

    @Override
    public int getItemCount() { return listaDias.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNumero;
        CardView card;
        public ViewHolder(View itemView) {
            super(itemView);
            txtNumero = itemView.findViewById(R.id.txtNumeroDia);
            card = itemView.findViewById(R.id.cardFondoDia);
        }
    }
}