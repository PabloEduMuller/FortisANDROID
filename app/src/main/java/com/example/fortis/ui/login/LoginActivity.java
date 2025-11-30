package com.example.fortis.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.fortis.R; // Certifique-se de que este import está correto
import com.example.fortis.data.SessionManager;
import com.example.fortis.data.model.LoginResponse;
import com.example.fortis.ui.home.HomeActivity;
import com.example.fortis.viewmodel.LoginViewModel;

public class LoginActivity extends AppCompatActivity {

    private EditText etCpf, etSenha;
    private Button btnEntrar;
    private LoginViewModel loginViewModel; // 1. Declaração do ViewModel

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etCpf = findViewById(R.id.edCPF);
        etSenha = findViewById(R.id.edSenha);
        btnEntrar = findViewById(R.id.btnEntrar);

        // 2. Inicialização do ViewModel
        // O ViewModelProvider garante que o ViewModel sobreviva a rotações de tela
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // 3. Configura os "observadores" para os LiveData do ViewModel
        setupObservers();

        // 4. Ação de clique agora é muito mais simples
        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cpf = etCpf.getText().toString().trim();
                String senha = etSenha.getText().toString().trim();

                if (cpf.isEmpty() || senha.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                // A Activity agora apenas DELEGA a tarefa para o ViewModel
                loginViewModel.login(cpf, senha);
            }
        });
    }

    private void setupObservers() {
        // Observador para o resultado de sucesso do login
        loginViewModel.getLoginResult().observe(this, new Observer<LoginResponse>() {
            @Override
            public void onChanged(LoginResponse loginResponse) {
                Toast.makeText(LoginActivity.this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show();

                SessionManager sessionManager = new SessionManager(getApplicationContext());
                sessionManager.saveAuthToken(loginResponse.getToken());

                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
                finish(); // Isso fecha a tela de login para o usuário не poder voltar
            }
        });

        // Observador para as mensagens de erro
        loginViewModel.getError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String errorMsg) {
                // Este código SÓ executa quando o error no ViewModel é atualizado
                Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });

        // Observador para o estado de "carregando" (opcional, mas bom para UX)
        loginViewModel.getIsLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                // Se estiver carregando, desabilita o botão para evitar cliques duplos
                // Se não estiver, habilita o botão novamente.
                btnEntrar.setEnabled(!isLoading);
            }
        });
    }
}