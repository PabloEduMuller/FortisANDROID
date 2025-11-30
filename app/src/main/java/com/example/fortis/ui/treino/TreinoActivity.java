package com.example.fortis.ui.treino;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton; // <-- 1. IMPORTAR ImageButton
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
// import androidx.appcompat.widget.Toolbar; // <-- 2. REMOVER Toolbar
import androidx.lifecycle.ViewModelProvider;

import com.example.fortis.R;
import com.example.fortis.data.SessionManager;
import com.example.fortis.data.model.Exercicio;
import com.example.fortis.viewmodel.treino.ExecucaoTreinoViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TreinoActivity extends AppCompatActivity {

    private ExecucaoTreinoViewModel viewModel;
    private long idDoTreino;
    private String tokenDeAutenticacao;
    private long treinoId;

    // --- 3. VIEWS ATUALIZADAS ---
    // private Toolbar toolbar; // (Removido)
    private ImageButton ibVoltar;
    private ImageButton ibConfiguracao;

    private ProgressBar progressBar;
    private LinearLayout llGrupoTreino, llGrupoDescanso;
    private TextView tvContagemExercicio, tvNomeExercicio, tvContagemSeries, tvTimerDescanso;
    private Button btnConcluirSerie, btnProximoExercicio, btnFinalizarTreino, btnIniciarDescanso, btnPularDescanso;
    private TextInputEditText etCarga;
    private TextInputEditText etRepeticoes;

    private CountDownTimer countDownTimer;
    private static final long TEMPO_DESCANSO_MS = 90000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treino); // Layout da Ação 44.1

        SessionManager sessionManager = new SessionManager(this);
        tokenDeAutenticacao = sessionManager.getAuthToken();
        idDoTreino = getIntent().getLongExtra("TREINO_ID", -1);

        if (idDoTreino == -1 || tokenDeAutenticacao == null) {
            Toast.makeText(this, "Erro: Treino ou Autenticação inválida.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        treinoId = getIntent().getLongExtra("TREINO_ID", -1);
        if (treinoId == -1) {
            finish();
            return;
        }
        viewModel = new ViewModelProvider(this).get(ExecucaoTreinoViewModel.class);

        associarViews();
        // configurarToolbar(); // (Removido)
        configurarHeaderBotoes(); // <-- 4. NOVO MÉTODO CHAMADO
        configurarListeners();
        configurarObservadores();

        viewModel.carregarTreino(tokenDeAutenticacao, idDoTreino);
    }

    private void associarViews() {
        // --- 5. ASSOCIAR NOVOS BOTÕES ---
        ibVoltar = findViewById(R.id.ibVoltar);
        ibConfiguracao = findViewById(R.id.ibConfiguracao);

        progressBar = findViewById(R.id.progressBarTreino);
        llGrupoTreino = findViewById(R.id.llGrupoTreino);
        llGrupoDescanso = findViewById(R.id.llGrupoDescanso);
        tvContagemExercicio = findViewById(R.id.tvContagemExercicio);
        tvNomeExercicio = findViewById(R.id.tvNomeExercicio);
        tvContagemSeries = findViewById(R.id.tvContagemSeries);
        tvTimerDescanso = findViewById(R.id.tvTimerDescanso);
        btnConcluirSerie = findViewById(R.id.btnConcluirSerie);
        btnProximoExercicio = findViewById(R.id.btnProximoExercicio);
        btnFinalizarTreino = findViewById(R.id.btnFinalizarTreino);
        btnIniciarDescanso = findViewById(R.id.btnIniciarDescanso);
        btnPularDescanso = findViewById(R.id.btnPularDescanso);
        etCarga = findViewById(R.id.etCarga);
        etRepeticoes = findViewById(R.id.etRepeticoes);
        btnFinalizarTreino = findViewById(R.id.btnFinalizarTreino);
    }

    // --- 6. MÉTODO ANTIGO DA TOOLBAR REMOVIDO ---
    /*
    private void configurarToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            mostrarDialogoSaida();
        });
    }
    */

    // --- 7. NOVO MÉTODO (padrão FAQ/Termos) ---
    private void configurarHeaderBotoes() {
        ibVoltar.setOnClickListener(v -> {
            mostrarDialogoSaida(); // Esta é a função "Cancelar"
        });

        ibConfiguracao.setOnClickListener(v -> {
            mostrarDialogoSaida(); // Ação "Cancelar"
        });
    }

    @Override
    public void onBackPressed() {
        mostrarDialogoSaida();
    }

    private void configurarListeners() {
        btnConcluirSerie.setOnClickListener(v -> {
            String carga = etCarga.getText().toString();
            String repeticoes = etRepeticoes.getText().toString();

            if (carga.isEmpty() || repeticoes.isEmpty()) {
                Toast.makeText(this, "Preencha a Carga e as Repetições", Toast.LENGTH_SHORT).show();
                return;
            }
            viewModel.concluirSerie(carga, repeticoes);

            // Verifica se acabou de concluir a última série do último exercício
            if (viewModel.isTreinoFinalizado()) {
                concluirTreino(); // Chama a tela de sucesso direto
            } else {
                mostrarTelaDescanso(true);
            }
        });

        btnIniciarDescanso.setOnClickListener(v -> iniciarContadorDescanso(TEMPO_DESCANSO_MS));

        btnPularDescanso.setOnClickListener(v -> {
            pararContadorDescanso();
            mostrarTelaDescanso(false);
        });

        // Este botão "Finalizar" (se existir na tela o tempo todo) deve ser para SAIR/CANCELAR
        // Se for o botão de "Concluir com Sucesso", ele deve ser tratado diferente.
        // Assumindo que 'btnFinalizarTreino' é um botão de "Abortar":
        if (btnFinalizarTreino != null) {
            btnFinalizarTreino.setOnClickListener(v -> mostrarDialogoSaida());
        }
    }

    private void configurarObservadores() {
        viewModel.getEstaCarregando().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            if (!isLoading) {
                llGrupoTreino.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getExercicioAtual().observe(this, this::atualizarUIExercicio);

        viewModel.getTreinoConcluido().observe(this, concluido -> {
            if (concluido) {
                Toast.makeText(this, "Treino Concluído!", Toast.LENGTH_LONG).show();
                finish();
            }
        });

        viewModel.getErro().observe(this, erro -> {
            Toast.makeText(this, erro, Toast.LENGTH_LONG).show();
        });
    }


    private void atualizarUIExercicio(Exercicio exercicio) {
        if (exercicio == null) return;

        // --- 8. REMOVIDA A LINHA DO 'getSupportActionBar()' ---
        // getSupportActionBar().setTitle(viewModel.getNomeDoTreino());

        tvNomeExercicio.setText(exercicio.getNome());
        tvContagemExercicio.setText(String.format(Locale.getDefault(), "Exercício %d de %d",
                viewModel.getIndiceExercicioAtual() + 1, viewModel.getTotalExercicios()));

        tvContagemSeries.setText(String.format(Locale.getDefault(), "Série %d de %d",
                viewModel.getSerieAtual() + 1, exercicio.getSeries()));

        etCarga.setText("");
        etRepeticoes.setText(String.valueOf(exercicio.getRepeticoes()));
        etCarga.requestFocus();
    }

    // ... (O restante dos métodos: mostrarTelaDescanso, iniciarContadorDescanso,
    // pararContadorDescanso, mostrarDialogoSaida permanecem OS MESMOS) ...

    private void mostrarTelaDescanso(boolean mostrar) {
        if (mostrar) {
            llGrupoTreino.setVisibility(View.GONE);
            llGrupoDescanso.setVisibility(View.VISIBLE);
            btnIniciarDescanso.setVisibility(View.VISIBLE);
            tvTimerDescanso.setVisibility(View.GONE);
        } else {
            llGrupoDescanso.setVisibility(View.GONE);
            llGrupoTreino.setVisibility(View.VISIBLE);
        }
    }

    private void iniciarContadorDescanso(long duracaoMs) {
        btnIniciarDescanso.setVisibility(View.GONE);
        tvTimerDescanso.setVisibility(View.VISIBLE);

        countDownTimer = new CountDownTimer(duracaoMs, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String tempo = String.format(Locale.getDefault(), "%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60);
                tvTimerDescanso.setText(tempo);
            }
            @Override
            public void onFinish() {
                tvTimerDescanso.setText("00:00");
                mostrarTelaDescanso(false);
            }
        }.start();
    }

    private void pararContadorDescanso() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void mostrarDialogoSaida() {
        new AlertDialog.Builder(this)
                .setTitle("Finalizar Treino")
                .setMessage("Tem certeza que deseja parar o treino agora? Seu progresso não será salvo.")
                .setPositiveButton("Sim", (dialog, which) -> {
                    pararContadorDescanso();
                    finish();
                })
                .setNegativeButton("Não", null)
                .show();
    }

    private void concluirTreino() {
        // 1. (Opcional) Calcular estatísticas reais
        long duracao = 3600; // Exemplo: pegar do cronômetro
        double carga = 500.0; // Exemplo: somar cargas dos exercícios
        int series = 12;     // Exemplo: contar séries feitas

        // 2. Navegar para a tela de Conclusão
        Intent intent = new Intent(TreinoActivity.this, TreinoConcluidoActivity.class);

        // Passar dados para o resumo (se quiser exibir lá)
        intent.putExtra("DURACAO", duracao);
        intent.putExtra("CARGA_TOTAL", carga);
        intent.putExtra("SERIES_TOTAL", series);
        intent.putExtra("TREINO_ID", treinoId); // Para salvar no histórico

        startActivity(intent);
        finish(); // Fecha a tela de execução para não voltar nela com "Voltar"
    }
}