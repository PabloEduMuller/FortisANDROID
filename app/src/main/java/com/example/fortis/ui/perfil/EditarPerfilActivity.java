package com.example.fortis.ui.perfil;

import android.app.Activity;
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
import com.example.fortis.viewmodel.perfil.EditarPerfilViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Activity para gerir a edição dos dados do perfil do utilizador.
 */
public class EditarPerfilActivity extends AppCompatActivity {

    private EditarPerfilViewModel viewModel;

    // Objeto para guardar o DTO carregado, que será modificado e salvo.
    private AlunoDTO alunoDTOAtual;

    // Componentes da UI
    private ImageButton ibVoltar;
    private Button btnCancelarEdicao, btnSalvarEdicao;
    private ProgressBar progressBarEditarPerfil;

    // Campos Não Editáveis (Labels)
    private TextView tvCpfLabel, tvNascimentoLabel;

    // Campos Editáveis
    private TextInputEditText etNome, etEmail, etTelefone;
    private TextInputEditText etCep, etRua, etCidade, etEstado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil); // Layout da Ação 3.3

        viewModel = new ViewModelProvider(this).get(EditarPerfilViewModel.class);

        associarViews();
        configurarBotoes();
        configurarObservadores();

        // Pede ao ViewModel para carregar os dados atuais
        viewModel.buscarPerfil();
    }

    private void associarViews() {
        // Botões
        ibVoltar = findViewById(R.id.ibVoltar);
        btnCancelarEdicao = findViewById(R.id.btnCancelarEdicao);
        btnSalvarEdicao = findViewById(R.id.btnSalvarEdicao);
        progressBarEditarPerfil = findViewById(R.id.progressBarEditarPerfil);

        // Labels (não editáveis)
        tvCpfLabel = findViewById(R.id.tvCpfLabel);
        tvNascimentoLabel = findViewById(R.id.tvNascimentoLabel);

        // Campos Editáveis
        etNome = findViewById(R.id.etNome);
        etEmail = findViewById(R.id.etEmail);
        etTelefone = findViewById(R.id.etTelefone);
        etCep = findViewById(R.id.etCep);
        etRua = findViewById(R.id.etRua);
        etCidade = findViewById(R.id.etCidade);
        etEstado = findViewById(R.id.etEstado);
    }

    private void configurarBotoes() {
        ibVoltar.setOnClickListener(v -> finish());
        btnCancelarEdicao.setOnClickListener(v -> finish());

        btnSalvarEdicao.setOnClickListener(v -> {
            // 1. Obter o DTO atual (carregado da tela anterior ou da API)
            AlunoDTO alunoParaSalvar = viewModel.getAlunoDTO().getValue();

            if (alunoParaSalvar == null) {
                alunoParaSalvar = new AlunoDTO(); // Previne crash se for nulo
            }

            // 2. CAPTURAR DADOS DOS CAMPOS (Isto é o que estava faltando ou incorreto)
            // Dados Pessoais
            alunoParaSalvar.setNome(etNome.getText().toString().trim());
            alunoParaSalvar.setEmail(etEmail.getText().toString().trim());
            alunoParaSalvar.setTelefone(etTelefone.getText().toString().trim());

            // OBS: Data de nascimento geralmente precisa de tratamento especial (conversão String -> Date)
            // Se estiver usando um DatePicker, garanta que a data está sendo setada no DTO.

            // 3. CAPTURAR DADOS DE ENDEREÇO
            // Verifique se os IDs dos EditTexts batem com o seu XML (activity_editar_perfil.xml)
            if (etRua != null) alunoParaSalvar.setRua(etRua.getText().toString().trim());
            if (etCidade != null) alunoParaSalvar.setCidade(etCidade.getText().toString().trim());
            if (etEstado != null) alunoParaSalvar.setEstado(etEstado.getText().toString().trim());
            if (etCep != null) alunoParaSalvar.setCep(etCep.getText().toString().trim());

            // 4. Enviar para o ViewModel
            viewModel.salvarAlteracoes(alunoParaSalvar);
        });
    }

    private void configurarObservadores() {
        // Observa o carregamento
        viewModel.getEstaCarregando().observe(this, isLoading -> {
            progressBarEditarPerfil.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            // Desativa os botões enquanto carrega/salva
            btnSalvarEdicao.setEnabled(!isLoading);
            btnCancelarEdicao.setEnabled(!isLoading);
        });

        // Observa erros
        viewModel.getErro().observe(this, erro -> {
            if (erro != null) {
                Toast.makeText(this, erro, Toast.LENGTH_LONG).show();
            }
        });

        // Observa o DTO (quando os dados chegam da API)
        viewModel.getAlunoDTO().observe(this, alunoDTO -> {
            if (alunoDTO != null) {
                this.alunoDTOAtual = alunoDTO; // Guarda o DTO original
                preencherFormulario(alunoDTO);
            }
        });

        // Observa o sucesso ao Salvar
        viewModel.getSalvoComSucesso().observe(this, salvo -> {
            if (salvo) {
                Toast.makeText(this, "Perfil atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                // Avisa a Activity anterior (Configuracoes) que algo mudou (opcional)
                setResult(Activity.RESULT_OK);
                finish(); // Fecha a tela de edição
            }
        });
    }

    /**
     * Preenche os campos do formulário com os dados do DTO vindos da API.
     */
    private void preencherFormulario(AlunoDTO dto) {
        // Campos editáveis
        etNome.setText(dto.getNome());
        etEmail.setText(dto.getEmail());
        etTelefone.setText(dto.getTelefone());
        etCep.setText(dto.getCep());
        etRua.setText(dto.getRua());
        etCidade.setText(dto.getCidade());
        etEstado.setText(dto.getEstado());

        // Campos não editáveis
        tvCpfLabel.setText("CPF: " + (dto.getCpf() != null ? dto.getCpf() : "N/A"));

        // Formatar Data (mesma lógica da PerfilActivity)
        String dataNascFormatada = "N/A";
        if (dto.getDataNascimento() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            dataNascFormatada = sdf.format(dto.getDataNascimento());
        }
        tvNascimentoLabel.setText("Nascimento: " + dataNascFormatada);
    }

    /**
     * Lê os dados dos EditTexts, atualiza o DTO e envia para o ViewModel salvar.
     */
    private void recolherDadosEsalvar() {
        // Validação de segurança: não tenta salvar se os dados originais não carregaram
        if (this.alunoDTOAtual == null) {
            Toast.makeText(this, "Erro: Dados do perfil ainda não carregados.", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- Recolhe os dados dos campos ---
        String nome = etNome.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String telefone = etTelefone.getText().toString().trim();
        String cep = etCep.getText().toString().trim();
        String rua = etRua.getText().toString().trim();
        String cidade = etCidade.getText().toString().trim();
        String estado = etEstado.getText().toString().trim();

        // (Validação básica de campos)
        if (nome.isEmpty() || email.isEmpty() || telefone.isEmpty()) {
            Toast.makeText(this, "Nome, E-mail e Telefone são obrigatórios.", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- Atualiza o DTO que já temos ---
        // (Não criamos um DTO novo, pois perderíamos IDs e dados do plano)
        alunoDTOAtual.setNome(nome);
        alunoDTOAtual.setEmail(email);
        alunoDTOAtual.setTelefone(telefone);
        alunoDTOAtual.setCep(cep);
        alunoDTOAtual.setRua(rua);
        alunoDTOAtual.setCidade(cidade);
        alunoDTOAtual.setEstado(estado);

        // --- Envia para o ViewModel ---
        viewModel.salvarAlteracoes(alunoDTOAtual);
    }
}