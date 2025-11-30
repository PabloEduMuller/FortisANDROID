package com.example.fortis.viewmodel.perfil;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.fortis.data.SessionManager;
import com.example.fortis.data.api.ApiService;
import com.example.fortis.data.api.RetrofitClient;
import com.example.fortis.data.model.MudarSenhaDTO;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MudarSenhaViewModel extends AndroidViewModel {

    private ApiService apiService;
    private SessionManager sessionManager;

    private MutableLiveData<Boolean> _estaCarregando = new MutableLiveData<>(false);
    private MutableLiveData<String> _erro = new MutableLiveData<>(null);
    private MutableLiveData<Boolean> _sucesso = new MutableLiveData<>(false);

    // Getters
    public LiveData<Boolean> getEstaCarregando() { return _estaCarregando; }
    public LiveData<String> getErro() { return _erro; }
    public LiveData<Boolean> getSucesso() { return _sucesso; }

    public MudarSenhaViewModel(@NonNull Application application) {
        super(application);
        sessionManager = new SessionManager(application.getApplicationContext());
        apiService = RetrofitClient.getApiService();
    }

    public void salvarNovaSenha(String senhaAtual, String novaSenha, String confirmarSenha) {
        // 1. Validação local (frontend)
        if (senhaAtual.isEmpty() || novaSenha.isEmpty() || confirmarSenha.isEmpty()) {
            _erro.setValue("Todos os campos são obrigatórios.");
            return;
        }
        if (!novaSenha.equals(confirmarSenha)) {
            _erro.setValue("As novas senhas não correspondem.");
            return;
        }
        if (novaSenha.length() < 6) { // (Exemplo de regra)
            _erro.setValue("A nova senha deve ter pelo menos 6 caracteres.");
            return;
        }
        if (novaSenha.equals(senhaAtual)) {
            _erro.setValue("A nova senha não pode ser igual à senha atual.");
            return;
        }

        // 2. Preparar para chamada de API
        _estaCarregando.setValue(true);
        _erro.setValue(null); // Limpa erros antigos
        String token = sessionManager.getAuthToken();

        if (token == null) {
            _erro.setValue("Sessão inválida. Faça login novamente.");
            _estaCarregando.setValue(false);
            return;
        }

        MudarSenhaDTO dto = new MudarSenhaDTO(senhaAtual, novaSenha);

        // 3. Chamar API
        apiService.mudarSenha("Bearer " + token, dto).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                _estaCarregando.setValue(false);

                if (response.isSuccessful()) {
                    // 200 OK
                    _sucesso.setValue(true);
                } else {
                    // Erros 400 (Senha atual incorreta) ou 404 (User não encontrado)
                    try {
                        // Tenta ler a mensagem de erro do corpo (ex: "A senha atual está incorreta.")
                        _erro.setValue(response.errorBody().string());
                    } catch (IOException e) {
                        _erro.setValue("Erro ao alterar a senha. Código: " + response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                _estaCarregando.setValue(false);
                _erro.setValue("Falha de rede: " + t.getMessage());
            }
        });
    }
}