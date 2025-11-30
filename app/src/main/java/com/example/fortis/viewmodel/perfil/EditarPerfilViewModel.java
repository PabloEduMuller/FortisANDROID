package com.example.fortis.viewmodel.perfil;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.fortis.data.SessionManager;
import com.example.fortis.data.api.ApiService;
import com.example.fortis.data.api.RetrofitClient;
import com.example.fortis.data.model.AlunoDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViewModel para a tela de Edição de Perfil.
 * Responsável por CARREGAR os dados atuais e SALVAR as alterações.
 */
public class EditarPerfilViewModel extends AndroidViewModel {

    private ApiService apiService;
    private SessionManager sessionManager;

    // LiveData para carregar o DTO
    private MutableLiveData<AlunoDTO> _alunoDTO = new MutableLiveData<>();
    // LiveData para o estado de salvamento
    private MutableLiveData<Boolean> _salvoComSucesso = new MutableLiveData<>(false);

    private MutableLiveData<String> _erro = new MutableLiveData<>();
    private MutableLiveData<Boolean> _estaCarregando = new MutableLiveData<>();

    // Getters
    public LiveData<AlunoDTO> getAlunoDTO() { return _alunoDTO; }
    public LiveData<Boolean> getSalvoComSucesso() { return _salvoComSucesso; }
    public LiveData<String> getErro() { return _erro; }
    public LiveData<Boolean> getEstaCarregando() { return _estaCarregando; }

    public EditarPerfilViewModel(@NonNull Application application) {
        super(application);
        sessionManager = new SessionManager(application.getApplicationContext());
        apiService = RetrofitClient.getApiService();
    }

    /**
     * Busca os dados de perfil atuais para preencher os campos de edição.
     * (Usa o mesmo endpoint do PerfilViewModel)
     */
    public void buscarPerfil() {
        _estaCarregando.setValue(true);
        String token = sessionManager.getAuthToken();
        if (token == null) {
            _erro.setValue("Sessão inválida.");
            _estaCarregando.setValue(false);
            return;
        }

        apiService.getPerfil("Bearer " + token).enqueue(new Callback<AlunoDTO>() {
            @Override
            public void onResponse(Call<AlunoDTO> call, Response<AlunoDTO> response) {
                _estaCarregando.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    _alunoDTO.setValue(response.body());
                } else {
                    _erro.setValue("Falha ao carregar dados do perfil.");
                }
            }

            @Override
            public void onFailure(Call<AlunoDTO> call, Throwable t) {
                _estaCarregando.setValue(false);
                _erro.setValue("Erro de rede: " + t.getMessage());
            }
        });
    }

    /**
     * Salva as alterações do perfil.
     * Recebe o DTO modificado pela Activity.
     */
    public void salvarAlteracoes(AlunoDTO dtoParaSalvar) {
        _estaCarregando.setValue(true);
        String token = sessionManager.getAuthToken();
        if (token == null) {
            _erro.setValue("Sessão inválida.");
            _estaCarregando.setValue(false);
            return;
        }

        // Usa o novo método 'updatePerfil' do ApiService
        apiService.updatePerfil("Bearer " + token, dtoParaSalvar).enqueue(new Callback<AlunoDTO>() {
            @Override
            public void onResponse(Call<AlunoDTO> call, Response<AlunoDTO> response) {
                _estaCarregando.setValue(false);
                if (response.isSuccessful()) {
                    // Sucesso! Notifica a Activity.
                    _salvoComSucesso.setValue(true);
                } else {
                    _erro.setValue("Falha ao salvar. Código: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<AlunoDTO> call, Throwable t) {
                _estaCarregando.setValue(false);
                _erro.setValue("Erro de rede ao salvar: " + t.getMessage());
            }
        });
    }
}