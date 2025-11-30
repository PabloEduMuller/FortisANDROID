package com.example.fortis.ui.treino;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

// --- 1. IMPORTAR A ACTIVITY CORRETA ---
import com.example.fortis.ui.editar_treino.EditarTreinoActivity;
import com.example.fortis.viewmodel.treino.TreinoDoDiaViewModel;

/**
 * Esta Activity age como um "roteador". Ela não tem UI.
 * Ela busca o treino do dia e decide para qual tela enviar o usuário.
 */
public class TreinoDoDiaRouterActivity extends AppCompatActivity {

    private TreinoDoDiaViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(TreinoDoDiaViewModel.class);

        viewModel.getTreinoDeHoje().observe(this, treino -> {
            if (treino != null && treino.getId() != 0) {

                // --- 2. CORREÇÃO DO FLUXO ---
                // Manda para a tela de "Detalhes/Edição" em vez de iniciar o treino direto.
                Intent intent = new Intent(this, EditarTreinoActivity.class);
                intent.putExtra("TREINO_ID", treino.getId());
                startActivity(intent);

            } else {
                // Se não houver treino (404 da API ou erro)
                Toast.makeText(this, "Nenhum treino para hoje!", Toast.LENGTH_LONG).show();
            }

            // Fecha este roteador, pois ele já cumpriu sua função
            finish();
        });

        // Inicia a busca pelo treino
        viewModel.buscarTreinoDeHoje();
    }
}