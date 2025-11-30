package com.example.fortis.ui.perfil;

import android.content.Intent; // <-- IMPORT
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.fortis.R;
import com.example.fortis.data.model.AlunoDTO;
import com.example.fortis.ui.historico.HistoricoRealizadoActivity;
import com.example.fortis.ui.home.HomeActivity; // <-- IMPORT
import com.example.fortis.ui.suporte.AjudaActivity;
import com.example.fortis.ui.treino.MeusTreinosActivity; // <-- IMPORT
import com.example.fortis.ui.treino.TreinoDoDiaRouterActivity; // <-- IMPORT
import com.example.fortis.viewmodel.perfil.PerfilViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView; // <-- IMPORT

import java.security.cert.CertPath;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PerfilActivity extends AppCompatActivity {

    private PerfilViewModel viewModel;

    // IDs do layout
    private TextView tvNomeHeader, tvNomeDados, tvCpf, tvNascimento, tvTelefone;
    private TextView tvPlanoTipo, tvPlanoStatus, tvPlanoVencimento;
    private ProgressBar progressBarPerfil;
    private ImageButton ibVoltar;
    private BottomNavigationView bottomNavigationView; // <-- 1. ADICIONADO

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        viewModel = new ViewModelProvider(this).get(PerfilViewModel.class);

        associarViews();
        configurarBotoes();
        configurarObservadores();
        configurarBottomNavigation(); // <-- 2. ADICIONADO
        configurarBotaoHistorico();
        viewModel.buscarPerfil();
    }

    private void associarViews() {
        tvNomeHeader = findViewById(R.id.tvNomeHeader);
        tvNomeDados = findViewById(R.id.tvNomeDados);
        tvCpf = findViewById(R.id.tvCpf);
        tvNascimento = findViewById(R.id.tvNascimento);
        tvTelefone = findViewById(R.id.tvTelefone);
        tvPlanoTipo = findViewById(R.id.tvPlanoTipo);
        tvPlanoStatus = findViewById(R.id.tvPlanoStatus);
        tvPlanoVencimento = findViewById(R.id.tvPlanoVencimento);
        progressBarPerfil = findViewById(R.id.progressBarPerfil);
        ibVoltar = findViewById(R.id.ibVoltar);
        bottomNavigationView = findViewById(R.id.bottom_navigation); // <-- 3. ADICIONADO

    }

    private void configurarBotoes() {
        ibVoltar.setOnClickListener(v -> finish()); // (Nota: este botão está sobreposto pelo 'ibSairSistema' no XML, mas deixamos a lógica)
    }

    private void configurarBotaoHistorico() {
        Button btnHistorico = findViewById(R.id.btnVerHistorico);
        if (btnHistorico != null) {
            btnHistorico.setOnClickListener(v -> {
                Intent intent = new Intent(PerfilActivity.this, HistoricoRealizadoActivity.class);
                startActivity(intent);
            });
        }
    }

    private void configurarObservadores() {
        // ... (seu código de observadores existente) ...
        viewModel.getEstaCarregando().observe(this, isLoading -> {
            progressBarPerfil.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getErro().observe(this, erro -> {
            if (erro != null) {
                Toast.makeText(this, erro, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getAlunoDTO().observe(this, alunoDTO -> {
            if (alunoDTO != null) {
                preencherDados(alunoDTO);
            }
        });
    }

    private void preencherDados(AlunoDTO alunoDTO) {
        // ... (seu código 'preencherDados' existente) ...
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String dataNascFormatada = formatarData(alunoDTO.getDataNascimento(), sdf);
        String dataVencFormatada = formatarData(alunoDTO.getDataVencimentoMatricula(), sdf);

        tvNomeHeader.setText(alunoDTO.getNome());
        tvNomeDados.setText("Nome: " + alunoDTO.getNome());
        tvCpf.setText("CPF: " + alunoDTO.getCpf());
        tvNascimento.setText("Nascimento: " + dataNascFormatada);
        tvTelefone.setText("Telefone: " + alunoDTO.getTelefone());

        tvPlanoTipo.setText("Tipo: " + (alunoDTO.getNomePlano() != null ? alunoDTO.getNomePlano() : "N/A"));
        tvPlanoStatus.setText("Status: " + (alunoDTO.getStatusMatricula() != null ? alunoDTO.getStatusMatricula() : "N/A"));
        tvPlanoVencimento.setText("Vencimento: " + dataVencFormatada);
    }

    private String formatarData(Date data, SimpleDateFormat sdf) {
        if (data == null) {
            return "N/A";
        }
        try {
            return sdf.format(data);
        } catch (Exception e) {
            return "Data Inválida";
        }
    }

    // --- 4. MÉTODO DE NAVEGAÇÃO ADICIONADO ---
    private void configurarBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.nav_perfil);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_home) {
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_ficha_treinos) {
                Intent intent = new Intent(this, MeusTreinosActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_perfil) {
                return true;
            } else if (itemId == R.id.navigation_treino_do_dia) {
                Intent intent = new Intent(this, TreinoDoDiaRouterActivity.class);
                startActivity(intent);
                return true;

                // --- CORREÇÃO APLICADA AQUI ---
            } else if (itemId == R.id.nav_chat_suporte) {
                Intent intent = new Intent(this, AjudaActivity.class);
                startActivity(intent);
                finish(); // Fecha o Perfil
                return true;
                // --- FIM DA CORREÇÃO ---
            }

            return false;
        });
    }
}