package com.example.fortis.viewmodel.perfil;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.fortis.data.SessionManager;
import com.example.fortis.data.api.ApiService;
import com.example.fortis.data.api.RetrofitClient;
// --- 1. IMPORTAR O AlunoDTO (o novo modelo de rede) ---
import com.example.fortis.data.model.AlunoDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViewModel dedicado a gerenciar os dados da tela "Meu Perfil".
 */
public class PerfilViewModel extends AndroidViewModel {

    private ApiService apiService;
    private SessionManager sessionManager;

    // --- 2. LIVE DATA ATUALIZADO ---
    // O LiveData agora armazena o AlunoDTO completo (com dados do plano)
    private MutableLiveData<AlunoDTO> _alunoDTO = new MutableLiveData<>();
    private MutableLiveData<String> _erro = new MutableLiveData<>();
    private MutableLiveData<Boolean> _estaCarregando = new MutableLiveData<>();

    // --- 3. GETTER ATUALIZADO ---
    // A Activity vai observar o AlunoDTO
    public LiveData<AlunoDTO> getAlunoDTO() { return _alunoDTO; }
    public LiveData<String> getErro() { return _erro; }
    public LiveData<Boolean> getEstaCarregando() { return _estaCarregando; }

    public PerfilViewModel(@NonNull Application application) {
        super(application);
        sessionManager = new SessionManager(application.getApplicationContext());
        apiService = RetrofitClient.getApiService();
    }

    /**
     * Busca os dados do perfil do usuário logado na API.
     */
    public void buscarPerfil() {
        _estaCarregando.setValue(true);
        String token = sessionManager.getAuthToken();

        if (token == null) {
            _erro.setValue("Sessão inválida. Faça login novamente.");
            _estaCarregando.setValue(false);
            return;
        }

        // --- 4. CALLBACK ATUALIZADO ---
        // O ApiService.getPerfil() agora retorna Call<AlunoDTO> (graças à Ação 2)
        apiService.getPerfil("Bearer " + token).enqueue(new Callback<AlunoDTO>() {
            @Override
            public void onResponse(Call<AlunoDTO> call, Response<AlunoDTO> response) {
                _estaCarregando.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    // Sucesso! Salva o DTO recebido diretamente no LiveData
                    _alunoDTO.setValue(response.body());
                } else {
                    _erro.setValue("Falha ao carregar o perfil. Código: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<AlunoDTO> call, Throwable t) {
                _estaCarregando.setValue(false);
                _erro.setValue("Erro de rede ao buscar perfil: " + t.getMessage());
            }
        });
    }
}