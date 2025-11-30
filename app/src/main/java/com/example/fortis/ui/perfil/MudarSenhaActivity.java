package com.example.fortis.ui.perfil;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.fortis.R;
import com.example.fortis.viewmodel.perfil.MudarSenhaViewModel;
import com.google.android.material.textfield.TextInputEditText;

public class MudarSenhaActivity extends AppCompatActivity {

    private MudarSenhaViewModel viewModel;

    // Componentes da UI (IDs do activity_mudar_senha.xml)
    private ImageButton ibVoltar;
    private Button btnCancelar;
    private Button btnSalvarSenha;
    private ProgressBar progressBarMudarSenha;
    private TextInputEditText etSenhaAtual;
    private TextInputEditText etNovaSenha;
    private TextInputEditText etConfirmarSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Link ao layout XML da Ação 4
        setContentView(R.layout.activity_mudar_senha);

        // Link ao ViewModel da Ação 5
        viewModel = new ViewModelProvider(this).get(MudarSenhaViewModel.class);

        associarViews();
        configurarBotoes();
        configurarObservadores();
    }

    private void associarViews() {
        ibVoltar = findViewById(R.id.ibVoltar);
        btnCancelar = findViewById(R.id.btnCancelar);
        btnSalvarSenha = findViewById(R.id.btnSalvarSenha);
        progressBarMudarSenha = findViewById(R.id.progressBarMudarSenha);
        etSenhaAtual = findViewById(R.id.etSenhaAtual);
        etNovaSenha = findViewById(R.id.etNovaSenha);
        etConfirmarSenha = findViewById(R.id.etConfirmarSenha);
    }

    private void configurarBotoes() {
        // Ações de 'Voltar' ou 'Cancelar' fecham a tela
        ibVoltar.setOnClickListener(v -> finish());
        btnCancelar.setOnClickListener(v -> finish());

        // Ação de 'Salvar'
        btnSalvarSenha.setOnClickListener(v -> {
            tentarSalvarSenha();
        });
    }

    /**
     * Recolhe os dados dos campos e envia para o ViewModel.
     */
    private void tentarSalvarSenha() {
        String atual = etSenhaAtual.getText().toString();
        String nova = etNovaSenha.getText().toString();
        String confirmacao = etConfirmarSenha.getText().toString();

        // O ViewModel faz toda a validação (campos vazios, senhas não batem, etc.)
        viewModel.salvarNovaSenha(atual, nova, confirmacao);
    }

    /**
     * Observa as respostas do ViewModel.
     */
    private void configurarObservadores() {
        // Observa o estado de carregamento
        viewModel.getEstaCarregando().observe(this, isLoading -> {
            progressBarMudarSenha.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            // Desativa os botões enquanto salva
            btnSalvarSenha.setEnabled(!isLoading);
            btnCancelar.setEnabled(!isLoading);
        });

        // Observa o sucesso
        viewModel.getSucesso().observe(this, sucesso -> {
            if (sucesso) {
                Toast.makeText(this, "Senha alterada com sucesso!", Toast.LENGTH_LONG).show();
                finish(); // Fecha a tela
            }
        });

        // Observa erros (Validação ou API)
        viewModel.getErro().observe(this, erro -> {
            if (erro != null) {
                // O ViewModel envia a mensagem de erro (ex: "As novas senhas não correspondem.")
                Toast.makeText(this, erro, Toast.LENGTH_LONG).show();
            }
        });
    }
}