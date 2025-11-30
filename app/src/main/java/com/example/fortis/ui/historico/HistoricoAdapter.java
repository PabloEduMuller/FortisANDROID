package com.example.fortis.ui.historico;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fortis.R;
import com.example.fortis.data.model.HistoricoTreino;

import java.util.List;
import java.util.Locale;

public class HistoricoAdapter extends RecyclerView.Adapter<HistoricoAdapter.ViewHolder> {

    private List<HistoricoTreino> lista;

    public HistoricoAdapter(List<HistoricoTreino> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // AQUI ESTÁ A MUDANÇA: Usamos o novo layout item_historico_card
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_historico_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoricoTreino item = lista.get(position);

        // Formata Data
        String dataFormatada = item.getData();
        try {
            String[] partes = item.getData().split("-");
            if (partes.length == 3) {
                dataFormatada = partes[2] + "/" + partes[1]; // Apenas dia/mês fica mais limpo
            }
        } catch (Exception e) {}

        // Popula os campos do Card
        holder.tvData.setText(dataFormatada);
        holder.tvCarga.setText(String.format(Locale.getDefault(), "%.0f kg", item.getCargaTotal()));

        long minutos = item.getDuracaoSegundos() != null ? item.getDuracaoSegundos() / 60 : 0;
        holder.tvDuracao.setText(minutos + " min");
    }

    @Override
    public int getItemCount() {
        return lista != null ? lista.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvData, tvCarga, tvDuracao;

        public ViewHolder(View v) {
            super(v);
            // Vincula com os IDs do novo layout item_historico_card.xml
            tvData = v.findViewById(R.id.tvData);
            tvCarga = v.findViewById(R.id.tvCargaTotal);
            tvDuracao = v.findViewById(R.id.tvDuracao);
        }
    }
}