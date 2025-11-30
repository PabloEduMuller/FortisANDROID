package com.example.fortis.ui.treino;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fortis.R;
import com.example.fortis.data.model.Treino;

import java.util.List;
import java.util.Locale;

/**
 * Adapter para preencher o RecyclerView da tela "Meus Treinos"
 * com base no layout 'item_meu_treino.xml'.
 */
public class TreinoAdapter extends RecyclerView.Adapter<TreinoAdapter.TreinoViewHolder> {

    private List<Treino> treinos;
    private final OnTreinoClickListener listener;

    /**
     * Interface para comunicar os cliques de volta para a Activity.
     * A Activity irá decidir o que fazer (Editar ou Iniciar).
     */
    public interface OnTreinoClickListener {
        void onVerTreinoClick(Treino treino); // Clique no botão "Play"
        void onItemClick(Treino treino);      // Clique no corpo do item (para editar)
    }

    // Construtor que recebe a lista de dados e o listener
    public TreinoAdapter(List<Treino> treinos, OnTreinoClickListener listener) {
        this.treinos = treinos;
        this.listener = listener;
    }

    /**
     * Chamado quando o RecyclerView precisa de um novo ViewHolder (um novo item).
     * Ele infla (cria) o layout 'item_meu_treino.xml'.
     */
    @NonNull
    @Override
    public TreinoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meu_treino, parent, false);
        return new TreinoViewHolder(view);
    }

    /**
     * Chamado para preencher os dados de um item específico (ViewHolder)
     * na posição 'position' da lista.
     */
    @Override
    public void onBindViewHolder(@NonNull TreinoViewHolder holder, int position) {
        Treino treino = treinos.get(position);
        holder.bind(treino, listener);
    }

    /**
     * Retorna o número total de itens na lista.
     */
    @Override
    public int getItemCount() {
        return treinos != null ? treinos.size() : 0;
    }

    /**
     * Método para a Activity atualizar a lista de treinos quando
     * os dados chegarem do ViewModel.
     */
    public void setTreinos(List<Treino> novosTreinos) {
        this.treinos = novosTreinos;
        notifyDataSetChanged(); // Atualiza o RecyclerView
    }


    // --- ViewHolder Inner Class ---

    /**
     * A classe ViewHolder armazena as referências dos componentes visuais
     * (os TextViews) de um único 'item_meu_treino.xml'.
     */
    static class TreinoViewHolder extends RecyclerView.ViewHolder {

        // Componentes do item_meu_treino.xml
        TextView tvTreinoTitulo;
        TextView tvTreinoDetalhes;
        TextView tvTreinoDia;
        ImageButton ibVerTreino;
        ImageView ivIconeTreino; // O ícone de lápis

        public TreinoViewHolder(@NonNull View itemView) {
            super(itemView);
            // Associa os IDs do XML
            tvTreinoTitulo = itemView.findViewById(R.id.tvTreinoTitulo);
            tvTreinoDetalhes = itemView.findViewById(R.id.tvTreinoDetalhes);
            tvTreinoDia = itemView.findViewById(R.id.tvTreinoDia);
            ibVerTreino = itemView.findViewById(R.id.ibVerTreino);
            ivIconeTreino = itemView.findViewById(R.id.ivIconeTreino);
        }

        /**
         * Preenche os componentes visuais com os dados do objeto Treino.
         */
        public void bind(final Treino treino, final OnTreinoClickListener listener) {
            // Preenche os textos
            tvTreinoTitulo.setText(treino.getNome());

            // Converte o dia da semana para maiúsculas (como no protótipo)
            if (treino.getDiaSemana() != null) {
                tvTreinoDia.setText(treino.getDiaSemana().toUpperCase(Locale.getDefault()));
            } else {
                tvTreinoDia.setText("DIA NÃO DEFINIDO");
            }

            // Calcula o número de exercícios (com segurança)
            int totalExercicios = 0;
            if (treino.getExercicios() != null) {
                totalExercicios = treino.getExercicios().size();
            }
            String detalhes = String.format(Locale.getDefault(), "%d exercícios", totalExercicios);
            tvTreinoDetalhes.setText(detalhes);

            // --- Configura os Cliques ---

            // 1. Clique no botão "VER TREINO" (Play)
            ibVerTreino.setOnClickListener(v -> listener.onVerTreinoClick(treino));

            // 2. Clique no resto do item (para Editar)
            itemView.setOnClickListener(v -> listener.onItemClick(treino));
            // (Também ligamos o ícone, caso o utilizador clique nele)
            ivIconeTreino.setOnClickListener(v -> listener.onItemClick(treino));
        }
    }
}