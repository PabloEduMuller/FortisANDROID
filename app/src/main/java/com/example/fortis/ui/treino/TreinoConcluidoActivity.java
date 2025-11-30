package com.example.fortis.ui.treino;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fortis.R;
import com.example.fortis.data.SessionManager;
import com.example.fortis.data.api.ApiService;
import com.example.fortis.data.api.RetrofitClient;
import com.example.fortis.data.model.HistoricoTreinoDTO; // Crie este modelo no Android também
import com.example.fortis.ui.home.HomeActivity;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TreinoConcluidoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treino_concluido);

        // 1. Recuperar dados passados pela TreinoActivity
        long duracaoSegundos = getIntent().getLongExtra("DURACAO", 0);
        double cargaTotal = getIntent().getDoubleExtra("CARGA_TOTAL", 0.0);
        int seriesTotal = getIntent().getIntExtra("SERIES_TOTAL", 0);
        long treinoId = getIntent().getLongExtra("TREINO_ID", -1);

        // 2. Popular a Tela (Vincular com seu XML)
        TextView tvDuracao = findViewById(R.id.tvDuracaoValor);
        TextView tvCarga = findViewById(R.id.tvCargaValor);
        TextView tvSeries = findViewById(R.id.tvSeriesValor);
        MaterialButton btnVoltar = findViewById(R.id.btnVoltarHome);

        // Formatar tempo (MM:SS)
        long minutos = duracaoSegundos / 60;
        long segundos = duracaoSegundos % 60;
        tvDuracao.setText(String.format("%02d:%02d", minutos, segundos));

        tvCarga.setText(String.format("%.1f kg", cargaTotal));
        tvSeries.setText(String.valueOf(seriesTotal));

        // 3. Salvar na API (Background)
        salvarNoHistorico(duracaoSegundos, cargaTotal, seriesTotal, treinoId);

        btnVoltar.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void salvarNoHistorico(long duracao, double carga, int series, long treinoId) {
        SessionManager session = new SessionManager(this);
        ApiService api = RetrofitClient.getApiService();

        HistoricoTreinoDTO dto = new HistoricoTreinoDTO();
        dto.setDuracaoSegundos((int) duracao);
        dto.setCargaTotalLevantada(carga);
        dto.setTotalSeries(series);
        if(treinoId != -1) dto.setTreinoId(treinoId);

        api.salvarHistorico("Bearer " + session.getAuthToken(), dto).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // Dados salvos silenciosamente para analytics
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Logar erro se necessário
            }
        });
    }
}