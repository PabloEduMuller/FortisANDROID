package com.example.fortis.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.fortis.R;
import com.example.fortis.data.SessionManager;
import com.example.fortis.data.model.Treino;
import com.example.fortis.ui.configuracoes.ConfiguracoesActivity;
import com.example.fortis.ui.editar_treino.EditarTreinoActivity;
import com.example.fortis.ui.login.LoginActivity;
import com.example.fortis.ui.perfil.PerfilActivity;
import com.example.fortis.ui.progresso.ProgressoActivity;
import com.example.fortis.ui.suporte.AjudaActivity;
import com.example.fortis.ui.treino.MeusTreinosActivity;
import com.example.fortis.ui.treino.TreinoActivity;
import com.example.fortis.ui.treino.TreinoDoDiaRouterActivity;
import com.example.fortis.viewmodel.home.HomeViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeActivity extends AppCompatActivity {

    private static final int EDITAR_TREINO_REQUEST = 101;
    private static final int CONFIGURACOES_REQUEST = 103;

    private HomeViewModel homeViewModel;
    private SessionManager sessionManager;
    private String token;

    // Componentes do Header
    private ImageButton ibSairSistema;
    private ImageButton ibConfiguracao;

    // Componentes do corpo
    private TextView tvBemVindo;
    private TextView tvTreinoHojeNome;
    private TextView tvTreinoHojeDetalhes;
    private Button btnIniciarTreino;
    private LinearLayout llTreinosSemanaContainer;
    private ProgressBar progressBarHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sessionManager = new SessionManager(this);
        token = sessionManager.getAuthToken();

        if (token == null) {
            Toast.makeText(this, "Sessão inválida, faça login novamente.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        associarViews();
        configurarBottomNavigation();
        configurarObservadores();
        configurarAcoes();

        carregarDados();
        configurarFabProgresso();

    }

    private void configurarFabProgresso() {
        FloatingActionButton fab = findViewById(R.id.fabProgresso);
        if (fab != null) {
            fab.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, ProgressoActivity.class);
                startActivity(intent);
            });
        }
    }

    private void associarViews() {
        ibSairSistema = findViewById(R.id.ibSairSistema);
        ibConfiguracao = findViewById(R.id.ibConfiguracao);
        tvBemVindo = findViewById(R.id.tvBemVindo);
        tvTreinoHojeNome = findViewById(R.id.tvTreinoHojeNome);
        tvTreinoHojeDetalhes = findViewById(R.id.tvTreinoHojeDetalhes);
        btnIniciarTreino = findViewById(R.id.btnIniciarTreino);
        llTreinosSemanaContainer = findViewById(R.id.llTreinosSemanaContainer);
        progressBarHome = findViewById(R.id.progressBarHome);
    }

    private void carregarDados() {
        homeViewModel.fetchHomeData();
    }

    private void configurarAcoes() {
        btnIniciarTreino.setOnClickListener(v -> {
            Treino treinoHoje = homeViewModel.getTreinoDeHoje().getValue();
            if (treinoHoje != null && treinoHoje.getExercicios() != null && !treinoHoje.getExercicios().isEmpty()) {
                Intent intent = new Intent(HomeActivity.this, TreinoActivity.class);
                intent.putExtra("TREINO_ID", treinoHoje.getId());
                startActivity(intent);
            } else {
                Toast.makeText(this, "Nenhum treino para hoje", Toast.LENGTH_SHORT).show();
            }
        });

        ibConfiguracao.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ConfiguracoesActivity.class);
            startActivityForResult(intent, CONFIGURACOES_REQUEST);
        });

        ibSairSistema.setOnClickListener(v -> {
            mostrarDialogoLogout();
        });
    }




    private void mostrarDialogoLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Sair da Conta")
                .setMessage("Tem certeza que deseja se desconectar?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    sessionManager.clearSession();
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Não", null)
                .show();
    }

    private void configurarObservadores() {
        homeViewModel.getAluno().observe(this, aluno -> {
            if (aluno != null && aluno.getNome() != null) {
                tvBemVindo.setText("Olá, " + aluno.getNome().split(" ")[0]);
            } else {
                tvBemVindo.setText("Olá!");
            }
        });

        homeViewModel.getEstaCarregando().observe(this, isLoading -> {
            progressBarHome.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        homeViewModel.getError().observe(this, erro -> {
            if (erro != null) {
                Toast.makeText(this, erro, Toast.LENGTH_LONG).show();
            }
        });

        homeViewModel.getTreinoDeHoje().observe(this, treino -> {
            if (treino != null && treino.getExercicios() != null) {
                tvTreinoHojeNome.setText(treino.getNome());
                String detalhes = treino.getExercicios().size() + " exercícios";
                tvTreinoHojeDetalhes.setText(detalhes);
                btnIniciarTreino.setEnabled(!treino.getExercicios().isEmpty());
            } else {
                tvTreinoHojeNome.setText("Nenhum treino hoje");
                tvTreinoHojeDetalhes.setText("Aproveite para descansar!");
                btnIniciarTreino.setEnabled(false);
            }
        });

        homeViewModel.getTreinos().observe(this, treinos -> {
            llTreinosSemanaContainer.removeAllViews();
            if (treinos != null) {
                LayoutInflater inflater = LayoutInflater.from(this);
                for (Treino treino : treinos) {
                    View itemView = inflater.inflate(R.layout.item_treino_semana, llTreinosSemanaContainer, false);
                    TextView tvDia = itemView.findViewById(R.id.tvDiaSemana);
                    TextView tvNome = itemView.findViewById(R.id.tvNomeTreinoSemana);

                    if (treino != null) {
                        tvDia.setText(treino.getDiaSemana());
                        tvNome.setText(treino.getNome());
                        itemView.setOnClickListener(v -> {
                            iniciarEdicaoTreino(treino);
                        });
                        llTreinosSemanaContainer.addView(itemView);
                    }
                }
            }
        });
    }
    // --- 2. MÉTODO DE NAVEGAÇÃO CORRETO ---
    private void iniciarEdicaoTreino(Treino treino) {
        Intent intent = new Intent(HomeActivity.this, EditarTreinoActivity.class);
        intent.putExtra("TREINO_ID", treino.getId());

        // NÃO passamos o 'MODO_VISUALIZACAO'.
        // Isso abre a tela em MODO DE GERENCIAMENTO (Editar/Excluir/Iniciar).

        startActivityForResult(intent, EDITAR_TREINO_REQUEST);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == EDITAR_TREINO_REQUEST) {
                Toast.makeText(this, "Atualizando treinos...", Toast.LENGTH_SHORT).show();
                carregarDados();
            }
            else if (requestCode == CONFIGURACOES_REQUEST) {
                Toast.makeText(this, "Atualizando perfil...", Toast.LENGTH_SHORT).show();
                carregarDados();
            }
        }
    }


    // --- 2. 'configurarBottomNavigation' ATUALIZADO ---
    private void configurarBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_home) {
                return true;
            } else if (itemId == R.id.nav_ficha_treinos) {
                Intent intent = new Intent(HomeActivity.this, MeusTreinosActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_perfil) {
                Intent intent = new Intent(HomeActivity.this, PerfilActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.navigation_treino_do_dia) {
                Intent intent = new Intent(HomeActivity.this, TreinoDoDiaRouterActivity.class);
                startActivity(intent);
                return true;

                // --- CORREÇÃO APLICADA AQUI ---
            } else if (itemId == R.id.nav_chat_suporte) {
                Intent intent = new Intent(HomeActivity.this, AjudaActivity.class);
                startActivity(intent);
                finish(); // Fecha a Home
                return true;
                // --- FIM DA CORREÇÃO ---
            }

            return false;
        });
    }
}