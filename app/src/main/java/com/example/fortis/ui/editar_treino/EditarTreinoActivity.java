package com.example.fortis.ui.editar_treino;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fortis.R;
import com.example.fortis.data.SessionManager;
import com.example.fortis.data.model.Exercicio;
import com.example.fortis.data.model.Treino;
import com.example.fortis.ui.treino.TreinoActivity;
import com.example.fortis.viewmodel.treino.EditarTreinoViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class EditarTreinoActivity extends AppCompatActivity {

    private EditarTreinoViewModel viewModel;
    private SessionManager gerenciadorDeSessao;
    private long idDoTreino;
    private String tokenDeAutenticacao;
    private boolean modoVisualizacao = false;
    private ExercicioEditAdapter exercicioAdapter;

    // Views
    private ProgressBar barraDeProgresso;
    private ImageButton ibVoltar;
    private ImageButton ibConfiguracao;
    private TextInputEditText etNomeTreino;
    private AutoCompleteTextView actvDiaSemana;
    private Button btnExcluirTreino;
    private Button btnAdicionarExercicio;
    private RecyclerView rvExerciciosEdit;
    private Button btnSalvarTreino;
    private MaterialButton btnIniciarTreinoPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_treino);

        gerenciadorDeSessao = new SessionManager(this);
        tokenDeAutenticacao = gerenciadorDeSessao.getAuthToken();

        // Recupera dados da Intent
        idDoTreino = getIntent().getLongExtra("TREINO_ID", -1);
        modoVisualizacao = getIntent().getBooleanExtra("MODO_VISUALIZACAO", false);

        if (tokenDeAutenticacao == null) {
            Toast.makeText(this, "Sessão inválida", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(EditarTreinoViewModel.class);

        associarViews();
        configurarHeaderBotoes();
        configurarBotoes();
        configurarRecyclerView();
        configurarObservadores();
        preencherDropdownDias();
        atualizarModoUI();

        // --- LÓGICA CRÍTICA: CRIAÇÃO vs EDIÇÃO ---
        if (idDoTreino == -1) {
            // Novo Treino: Inicializa objeto vazio na memória
            viewModel.inicializarNovoTreino();
        } else {
            // Treino Existente: Busca dados da API
            viewModel.buscarTreinoPorId(tokenDeAutenticacao, idDoTreino);
        }
    }

    private void associarViews() {
        ibVoltar = findViewById(R.id.ibVoltar);
        ibConfiguracao = findViewById(R.id.ibConfiguracao);
        etNomeTreino = findViewById(R.id.etNomeTreino);
        actvDiaSemana = findViewById(R.id.actvDiaSemana);
        rvExerciciosEdit = findViewById(R.id.rvExerciciosEdit);
        btnAdicionarExercicio = findViewById(R.id.btnAdicionarExercicio);
        btnSalvarTreino = findViewById(R.id.btnSalvarTreino);
        btnIniciarTreinoPreview = findViewById(R.id.btnIniciarTreinoPreview);
        barraDeProgresso = findViewById(R.id.progressBarEdit);
        try {
            btnExcluirTreino = findViewById(R.id.btnExcluirTreino);
        } catch (Exception e) { /* Botão pode não existir no layout dependendo da versão */ }
    }

    private void configurarHeaderBotoes() {
        ibVoltar.setOnClickListener(v -> {
            setResult(Activity.RESULT_CANCELED);
            finish();
        });
        ibConfiguracao.setOnClickListener(v -> {
            setResult(Activity.RESULT_CANCELED);
            finish();
        });
    }

    private void preencherDropdownDias() {
        String[] dias = new String[]{"segunda", "terca", "quarta", "quinta", "sexta", "sabado", "domingo"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, dias);
        actvDiaSemana.setAdapter(adapter);
    }

    private void configurarRecyclerView() {
        exercicioAdapter = new ExercicioEditAdapter(new ArrayList<>(), modoVisualizacao,
                this::mostrarDialogoEditarExercicio,
                exercicio -> {
                    new AlertDialog.Builder(this)
                            .setTitle("Deletar Exercício")
                            .setMessage("Tem certeza que deseja deletar '" + exercicio.getNome() + "'?")
                            .setPositiveButton("Sim", (dialog, which) -> {
                                if (idDoTreino == -1) {
                                    // Modo Local: Remove da lista e atualiza adapter
                                    Treino t = viewModel.getTreino().getValue();
                                    if (t != null) {
                                        t.getExercicios().remove(exercicio);
                                        exercicioAdapter.atualizarLista(t.getExercicios());
                                    }
                                } else {
                                    // Modo API: Remove do banco
                                    viewModel.deletarExercicio(tokenDeAutenticacao, exercicio.getId());
                                }
                            })
                            .setNegativeButton("Não", null)
                            .show();
                });

        rvExerciciosEdit.setLayoutManager(new LinearLayoutManager(this));
        rvExerciciosEdit.setAdapter(exercicioAdapter);
    }

    private void atualizarModoUI() {
        btnIniciarTreinoPreview.setVisibility(View.VISIBLE);
        TextInputLayout tilDiaSemana = findViewById(R.id.tilDiaSemana);

        if (modoVisualizacao) {
            // --- MODO LEITURA (Treino do Dia) ---
            btnSalvarTreino.setVisibility(View.GONE);
            btnAdicionarExercicio.setVisibility(View.GONE);
            if (btnExcluirTreino != null) btnExcluirTreino.setVisibility(View.GONE);

            etNomeTreino.setEnabled(false);
            etNomeTreino.setFocusable(false);

            actvDiaSemana.setEnabled(false);
            actvDiaSemana.setAdapter(null); // Remove lista para não abrir dropdown
            actvDiaSemana.setTextColor(getResources().getColor(android.R.color.black));

            if (tilDiaSemana != null) tilDiaSemana.setEndIconMode(TextInputLayout.END_ICON_NONE);
            if (exercicioAdapter != null) exercicioAdapter.setModoEdicao(false);

        } else {
            // --- MODO EDIÇÃO ---
            btnSalvarTreino.setVisibility(View.VISIBLE);
            btnAdicionarExercicio.setVisibility(View.VISIBLE);
            if (btnExcluirTreino != null) btnExcluirTreino.setVisibility(View.VISIBLE);

            etNomeTreino.setEnabled(true);
            actvDiaSemana.setEnabled(true);
            preencherDropdownDias();

            if (tilDiaSemana != null) tilDiaSemana.setEndIconMode(TextInputLayout.END_ICON_DROPDOWN_MENU);
            if (exercicioAdapter != null) exercicioAdapter.setModoEdicao(true);
        }
    }

    private void configurarBotoes() {
        if (btnExcluirTreino != null) {
            btnExcluirTreino.setOnClickListener(v -> {
                if (idDoTreino == -1) {
                    finish(); // Apenas sai se não foi salvo
                } else {
                    new AlertDialog.Builder(this)
                            .setTitle("Excluir Treino")
                            .setMessage("Deseja excluir este treino permanentemente?")
                            .setPositiveButton("Sim", (d, w) -> viewModel.deletarTreino(tokenDeAutenticacao, idDoTreino))
                            .setNegativeButton("Não", null)
                            .show();
                }
            });
        }

        btnAdicionarExercicio.setOnClickListener(v -> mostrarDialogoAdicionarExercicio());

        // --- BOTÃO SALVAR (Atualizado) ---
        btnSalvarTreino.setOnClickListener(v -> {
            Treino treinoAtual = viewModel.getTreino().getValue();
            if (treinoAtual == null) return;

            String nome = etNomeTreino.getText().toString().trim();
            String dia = actvDiaSemana.getText().toString().trim().toLowerCase(); // API espera minúsculo

            if (nome.isEmpty() || dia.isEmpty()) {
                Toast.makeText(this, "Preencha nome e dia da semana", Toast.LENGTH_SHORT).show();
                return;
            }

            treinoAtual.setNome(nome);
            treinoAtual.setDiaSemana(dia);
            // A lista de exercícios já está no objeto treinoAtual

            viewModel.salvarTreino(tokenDeAutenticacao, treinoAtual);
        });

        btnIniciarTreinoPreview.setOnClickListener(v -> {
            Intent intent = new Intent(this, TreinoActivity.class);
            intent.putExtra("TREINO_ID", idDoTreino);
            startActivity(intent);
        });
    }

    private void mostrarDialogoAdicionarExercicio() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_adicionar_exercicio, null);
        EditText etNome = dialogView.findViewById(R.id.etNomeExercicio);
        EditText etSeries = dialogView.findViewById(R.id.etSeries);
        EditText etRepeticoes = dialogView.findViewById(R.id.etRepeticoes);

        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("Adicionar", (dialog, which) -> {
                    String nome = etNome.getText().toString().trim();
                    String seriesStr = etSeries.getText().toString().trim();
                    String repsStr = etRepeticoes.getText().toString().trim();

                    if (nome.isEmpty() || seriesStr.isEmpty() || repsStr.isEmpty()) return;

                    int series = Integer.parseInt(seriesStr);
                    int repeticoes = Integer.parseInt(repsStr);

                    if (idDoTreino == -1) {
                        // --- MODO LOCAL (Novo Treino) ---
                        Treino t = viewModel.getTreino().getValue();
                        if (t != null) {
                            Exercicio novo = new Exercicio();
                            novo.setNome(nome);
                            novo.setSeries(series);
                            novo.setRepeticoes(repeticoes);

                            if (t.getExercicios() == null) t.setExercicios(new ArrayList<>());

                            t.getExercicios().add(novo);
                            exercicioAdapter.atualizarLista(t.getExercicios());

                            // --- AQUI ESTAVA O ERRO. USE O MÉTODO NOVO: ---
                            viewModel.atualizarTreinoLocal(t);
                        }
                    } else {
                        // --- MODO API (Treino Existente) ---
                        viewModel.adicionarExercicioAPI(tokenDeAutenticacao, idDoTreino, nome, series, repeticoes);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogoEditarExercicio(Exercicio exercicio) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_adicionar_exercicio, null);
        TextView tvTitulo = dialogView.findViewById(R.id.tvTituloDialog);
        EditText etNome = dialogView.findViewById(R.id.etNomeExercicio);
        EditText etSeries = dialogView.findViewById(R.id.etSeries);
        EditText etRepeticoes = dialogView.findViewById(R.id.etRepeticoes);

        tvTitulo.setText("Editar Exercício");
        etNome.setText(exercicio.getNome());
        etSeries.setText(String.valueOf(exercicio.getSeries()));
        etRepeticoes.setText(String.valueOf(exercicio.getRepeticoes()));

        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("Salvar", (dialog, which) -> {
                    String nome = etNome.getText().toString().trim();
                    int series = Integer.parseInt(etSeries.getText().toString());
                    int repeticoes = Integer.parseInt(etRepeticoes.getText().toString());

                    if (idDoTreino == -1) {
                        // --- MODO LOCAL ---
                        exercicio.setNome(nome);
                        exercicio.setSeries(series);
                        exercicio.setRepeticoes(repeticoes);
                        exercicioAdapter.notifyDataSetChanged();
                    } else {
                        // --- MODO API ---
                        viewModel.editarExercicioAPI(tokenDeAutenticacao, exercicio.getId(), nome, series, repeticoes);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void configurarObservadores() {
        viewModel.getTreino().observe(this, treino -> {
            if (treino != null) {
                if (!etNomeTreino.getText().toString().equals(treino.getNome())) {
                    etNomeTreino.setText(treino.getNome());
                }
                // Só seta o texto se estiver vazio para não sobrescrever seleção do usuário
                if (actvDiaSemana.getText().toString().isEmpty()) {
                    actvDiaSemana.setText(treino.getDiaSemana(), false);
                }
                exercicioAdapter.atualizarLista(treino.getExercicios());
            }
        });

        viewModel.getEstaCarregando().observe(this, loading ->
                barraDeProgresso.setVisibility(loading ? View.VISIBLE : View.GONE)
        );

        viewModel.getErro().observe(this, erro -> {
            if ("SUCESSO_SALVAR".equals(erro)) {
                Toast.makeText(this, "Treino salvo com sucesso!", Toast.LENGTH_SHORT).show();
                setResult(Activity.RESULT_OK);
                finish();
            } else if (erro != null && !erro.equals("Exercício atualizado com sucesso!")) { // Evita toast se for sucesso de exercício
                Toast.makeText(this, erro, Toast.LENGTH_LONG).show();
            } else if (erro != null) {
                Toast.makeText(this, erro, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getTreinoDeletado().observe(this, deletado -> {
            if (deletado) {
                Toast.makeText(this, "Treino excluído.", Toast.LENGTH_SHORT).show();
                setResult(Activity.RESULT_OK);
                finish();
            }
        });

        // Observadores para atualizar lista em tempo real (Modo API)
        viewModel.getExercicioAdicionado().observe(this, ex ->
                Toast.makeText(this, "Exercício adicionado!", Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }
}