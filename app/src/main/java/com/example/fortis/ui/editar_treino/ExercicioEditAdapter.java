package com.example.fortis.ui.editar_treino;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fortis.R;
import com.example.fortis.data.model.Exercicio;

import java.util.List;
import java.util.Locale;

public class ExercicioEditAdapter extends RecyclerView.Adapter<ExercicioEditAdapter.ExercicioViewHolder> {

    // --- 1. Interfaces para os callbacks (ações) ---
    public interface OnExercicioEditListener {
        void onEditClick(Exercicio exercicio);
    }

    public interface OnExercicioDeleteListener {
        void onDeleteClick(Exercicio exercicio);
    }

    private List<Exercicio> exercicios;
    private boolean modoEdicao; // Controla se os botões de edição/deleção aparecem
    private final OnExercicioEditListener editListener;
    private final OnExercicioDeleteListener deleteListener;

    // --- 2. Construtor ---
    public ExercicioEditAdapter(List<Exercicio> exercicios,
                                boolean modoVisualizacao,
                                OnExercicioEditListener editListener,
                                OnExercicioDeleteListener deleteListener) {
        this.exercicios = exercicios;
        this.modoEdicao = !modoVisualizacao; // Invertido: modoVisualizacao=true significa modoEdicao=false
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ExercicioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla o layout do item que está em res/layout/item_exercicio_edit.xml
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercicio_edit, parent, false);
        return new ExercicioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExercicioViewHolder holder, int position) {
        Exercicio exercicio = exercicios.get(position);
        holder.bind(exercicio, modoEdicao, editListener, deleteListener);
    }

    @Override
    public int getItemCount() {
        return exercicios != null ? exercicios.size() : 0;
    }

    // --- 3. ViewHolder (O 'molde' para cada item da lista) ---
    static class ExercicioViewHolder extends RecyclerView.ViewHolder {
        TextView tvNomeExercicio;
        TextView tvSeriesRepeticoes;
        ImageButton btnEditExercicio;
        ImageButton btnDeleteExercicio;

        public ExercicioViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNomeExercicio = itemView.findViewById(R.id.tvExercicioNome);
            tvSeriesRepeticoes = itemView.findViewById(R.id.tvExercicioDetalhes);
            btnEditExercicio = itemView.findViewById(R.id.ibEditExercicio);
            btnDeleteExercicio = itemView.findViewById(R.id.ibDeleteExercicio);
        }

        public void bind(Exercicio exercicio, boolean modoEdicao, OnExercicioEditListener editListener, OnExercicioDeleteListener deleteListener) {
            tvNomeExercicio.setText(exercicio.getNome());
            String seriesRepeticoes = String.format(Locale.getDefault(), "%d séries x %d repetições",
                    exercicio.getSeries(), exercicio.getRepeticoes());
            tvSeriesRepeticoes.setText(seriesRepeticoes);

            // --- 4. Lógica de visibilidade ---
            if (modoEdicao) {
                btnEditExercicio.setVisibility(View.VISIBLE);
                btnDeleteExercicio.setVisibility(View.VISIBLE);

                // Configura os cliques
                btnEditExercicio.setOnClickListener(v -> editListener.onEditClick(exercicio));
                btnDeleteExercicio.setOnClickListener(v -> deleteListener.onDeleteClick(exercicio));
            } else {
                // Modo Visualização: esconde os botões
                btnEditExercicio.setVisibility(View.GONE);
                btnDeleteExercicio.setVisibility(View.GONE);
            }
        }
    }

    // --- 5. Métodos para atualizar o adapter de fora ---

    /**
     * Atualiza a lista de exercícios e notifica o adapter.
     */
    public void atualizarLista(List<Exercicio> novaLista) {
        this.exercicios.clear();
        if (novaLista != null) {
            this.exercicios.addAll(novaLista);
        }
        notifyDataSetChanged(); // Notifica o RecyclerView para redesenhar
    }

    /**
     * Alterna o modo da UI (usado pela Activity).
     */
    public void setModoEdicao(boolean modoEdicao) {
        this.modoEdicao = modoEdicao;
        notifyDataSetChanged(); // Re-binda todas as views com a nova visibilidade
    }
}