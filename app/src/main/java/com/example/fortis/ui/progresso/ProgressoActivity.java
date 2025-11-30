package com.example.fortis.ui.progresso;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.fortis.R;
import com.example.fortis.data.SessionManager;
import com.example.fortis.data.api.ApiService;
import com.example.fortis.data.api.RetrofitClient;
import com.example.fortis.data.model.HistoricoTreino;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProgressoActivity extends AppCompatActivity {

    private LineChart chart;
    private TextView tvTotalKg, tvTotalTreinos;
    private ChipGroup chipGroup;
    private List<HistoricoTreino> dadosCompletos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progresso);

        inicializarViews();
        configurarGraficoDesign();
        carregarDadosDaAPI();

        // Listener dos filtros
        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chip7dias) {
                filtrarDados(7);
            } else {
                filtrarDados(30);
            }
        });

        // Botão Voltar
        findViewById(R.id.ibVoltar).setOnClickListener(v -> finish());
    }

    private void inicializarViews() {
        chart = findViewById(R.id.chartEvolucao);
        tvTotalKg = findViewById(R.id.tvTotalKg);
        tvTotalTreinos = findViewById(R.id.tvTotalTreinos);
        chipGroup = findViewById(R.id.chipGroupFiltro);
    }

    private void configurarGraficoDesign() {
        // Estilo "Dark Mode" Clean
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.getLegend().setEnabled(false); // Remove legenda padrão
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(false); // Bloqueia zoom para manter design limpo

        // Eixo X (Datas)
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);

        // Eixo Y (Esquerda)
        chart.getAxisLeft().setTextColor(Color.WHITE);
        chart.getAxisLeft().setDrawGridLines(true);
        chart.getAxisLeft().setGridColor(Color.parseColor("#33FFFFFF")); // Grade suave

        // Desabilita eixo direito
        chart.getAxisRight().setEnabled(false);

        // Animação ao abrir
        chart.animateXY(1500, 1500);
    }

    private void carregarDadosDaAPI() {
        SessionManager session = new SessionManager(this);
        ApiService api = RetrofitClient.getApiService();

        api.getHistoricoProgresso("Bearer " + session.getAuthToken()).enqueue(new Callback<List<HistoricoTreino>>() {
            @Override
            public void onResponse(Call<List<HistoricoTreino>> call, Response<List<HistoricoTreino>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    dadosCompletos = response.body();
                    filtrarDados(7); // Padrão: últimos 7 registros
                } else {
                    Toast.makeText(ProgressoActivity.this, "Sem dados de progresso.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<HistoricoTreino>> call, Throwable t) {
                Toast.makeText(ProgressoActivity.this, "Erro ao carregar gráfico.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filtrarDados(int limite) {
        if (dadosCompletos.isEmpty()) return;

        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        double cargaTotalAcumulada = 0;

        // Pega os últimos X itens (ou todos se for menor que o limite)
        int inicio = Math.max(0, dadosCompletos.size() - limite);
        List<HistoricoTreino> filtrados = dadosCompletos.subList(inicio, dadosCompletos.size());

        for (int i = 0; i < filtrados.size(); i++) {
            HistoricoTreino h = filtrados.get(i);
            // Eixo Y: Carga Total levantada no treino
            entries.add(new Entry(i, h.getCargaTotal().floatValue()));

            // Eixo X: Data (formatar dd/MM se necessário)
            String[] partesData = h.getData().split("-");
            if (partesData.length >= 3) {
                labels.add(partesData[2] + "/" + partesData[1]);
            } else {
                labels.add(h.getData());
            }

            cargaTotalAcumulada += h.getCargaTotal();
        }

        // Atualiza os Cards de Resumo
        // USANDO LOCALE PARA EVITAR ERROS DE FORMATAÇÃO
        tvTotalKg.setText(String.format(Locale.getDefault(), "%.0f kg", cargaTotalAcumulada));
        tvTotalTreinos.setText(String.valueOf(filtrados.size()));

        atualizarChart(entries, labels);
    }

    private void atualizarChart(List<Entry> entries, List<String> labels) {
        LineDataSet dataSet = new LineDataSet(entries, "Carga");

        // Estilo da Linha (Neon Style)
        int corPrimaria = ContextCompat.getColor(this, R.color.Ford_Blue);
        dataSet.setColor(corPrimaria);
        dataSet.setLineWidth(3f);
        dataSet.setCircleColor(Color.WHITE);
        dataSet.setCircleRadius(5f);
        dataSet.setDrawCircleHole(false);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(10f);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER); // Linha curva suave
        dataSet.setDrawFilled(true);
        dataSet.setFillDrawable(ContextCompat.getDrawable(this, R.drawable.fade_blue));
        dataSet.setFillAlpha(50);
        dataSet.setDrawValues(false); // Esconde valores poluídos

        LineData lineData = new LineData(dataSet);

        // Formata as datas no eixo X
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        chart.getXAxis().setLabelCount(Math.min(labels.size(), 5), true);

        // --- ADICIONA O MARKER (INTERATIVIDADE) ---
        try {
            CustomMarkerView mv = new CustomMarkerView(this, R.layout.custom_marker_view, labels);
            mv.setChartView(chart);
            chart.setMarker(mv);
        } catch (Exception e) {
            // Caso o layout do marker não exista ainda, evita crash
        }
        // -----------------------------------------

        chart.setData(lineData);
        chart.animateY(1000);
        chart.invalidate(); // Refresh visual
    }
}