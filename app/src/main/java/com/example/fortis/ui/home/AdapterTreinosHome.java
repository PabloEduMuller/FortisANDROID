package com.example.fortis.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fortis.R;
import com.example.fortis.data.model.TreinoDTO;
import java.util.List;

public class AdapterTreinosHome extends RecyclerView.Adapter<AdapterTreinosHome.TreinoViewHolder> {

    private List<TreinoDTO> listaDeTreinos;
    private OnTreinoCliqueListener mListener;

    // Interface para o clique (que a HomeActivity implementa)
    public interface OnTreinoCliqueListener {
        void onTreinoClick(TreinoDTO treino);
    }

    // Setter para o listener
    public void setOnTreinoCliqueListener(OnTreinoCliqueListener listener) {
        this.mListener = listener;
    }

    // Construtor
    public AdapterTreinosHome(List<TreinoDTO> listaDeTreinos, OnTreinoCliqueListener listener) {
        this.listaDeTreinos = listaDeTreinos;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public TreinoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_treino_semana, parent, false);
        return new TreinoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TreinoViewHolder holder, int position) {
        TreinoDTO treino = listaDeTreinos.get(position);

        // --- CORREÇÃO AQUI ---
        // Agora usa o ID correto do XML
        holder.tvNomeTreino.setText(treino.getNome());
        holder.tvDiaSemana.setText(treino.getDiaSemana());

        // A linha do tvQtdExercicios foi removida pois não existe no XML.

        // Ação de clique
        holder.itemView.setOnClickListener(v -> {
            if (mListener != null && position != RecyclerView.NO_POSITION) {
                mListener.onTreinoClick(treino);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaDeTreinos != null ? listaDeTreinos.size() : 0;
    }

    public void atualizarLista(List<TreinoDTO> novaLista) {
        this.listaDeTreinos = novaLista;
        notifyDataSetChanged();
    }

    // --- CORREÇÃO NO VIEWHOLDER ---
    static class TreinoViewHolder extends RecyclerView.ViewHolder {
        // Removemos o tvQtdExercicios
        TextView tvNomeTreino, tvDiaSemana;

        public TreinoViewHolder(@NonNull View itemView) {
            super(itemView);

            // Corrigimos os IDs para bater 100% com o seu XML
            tvNomeTreino = itemView.findViewById(R.id.tvNomeTreinoSemana);
            tvDiaSemana = itemView.findViewById(R.id.tvDiaSemana);
            // A linha do tvQtdExercicios foi removida
        }
    }
}