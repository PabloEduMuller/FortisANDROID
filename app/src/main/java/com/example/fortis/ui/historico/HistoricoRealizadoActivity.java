package com.example.fortis.ui.historico;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fortis.R;
import com.example.fortis.data.SessionManager;
import com.example.fortis.data.api.ApiService;
import com.example.fortis.data.api.RetrofitClient;
import com.example.fortis.data.model.HistoricoTreino;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoricoRealizadoActivity extends AppCompatActivity {

    private RecyclerView rvHistorico;
    private ProgressBar progressBar;
    private ImageButton ibVoltar;

    private SessionManager sessionManager;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico_realizado); // Certifique-se de ter criado este XML no passo 2.1

        sessionManager = new SessionManager(this);
        apiService = RetrofitClient.getApiService();

        inicializarViews();
        carregarHistorico();
    }

    private void inicializarViews() {
        rvHistorico = findViewById(R.id.rvHistorico);
        rvHistorico.setLayoutManager(new LinearLayoutManager(this));

        // Se você adicionou uma ProgressBar no layout xml, descomente:
        // progressBar = findViewById(R.id.progressBarHistorico);


        ibVoltar = findViewById(R.id.ibVoltar);
        if (ibVoltar != null) ibVoltar.setOnClickListener(v -> finish());
    }

    private void carregarHistorico() {
        String token = sessionManager.getAuthToken();
        if (token == null) {
            Toast.makeText(this, "Sessão inválida", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        apiService.getExerciciosRealizados("Bearer " + token).enqueue(new Callback<List<HistoricoTreino>>() {
            @Override
            public void onResponse(Call<List<HistoricoTreino>> call, Response<List<HistoricoTreino>> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<HistoricoTreino> lista = response.body();

                    if (lista.isEmpty()) {
                        Toast.makeText(HistoricoRealizadoActivity.this, "Nenhum treino realizado ainda.", Toast.LENGTH_LONG).show();
                    }

                    HistoricoAdapter adapter = new HistoricoAdapter(lista);
                    rvHistorico.setAdapter(adapter);
                } else {
                    Toast.makeText(HistoricoRealizadoActivity.this, "Erro ao carregar histórico.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<HistoricoTreino>> call, Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Toast.makeText(HistoricoRealizadoActivity.this, "Erro de rede: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}