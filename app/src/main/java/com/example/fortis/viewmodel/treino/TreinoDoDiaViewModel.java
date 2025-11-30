package com.example.fortis.viewmodel.treino;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.fortis.data.SessionManager;
import com.example.fortis.data.api.ApiService;
import com.example.fortis.data.api.RetrofitClient;
import com.example.fortis.data.model.TreinoDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViewModel dedicado a uma única tarefa:
 * buscar o ID do treino de hoje para o TreinoDoDiaRouterActivity.
 */
public class TreinoDoDiaViewModel extends AndroidViewModel {

    private ApiService apiService;
    private SessionManager sessionManager;

    // LiveData para o DTO (só precisamos do ID)
    private MutableLiveData<TreinoDTO> _treinoDeHoje = new MutableLiveData<>();
    private MutableLiveData<String> _erro = new MutableLiveData<>();
    private MutableLiveData<Boolean> _estaCarregando = new MutableLiveData<>(true);

    public LiveData<TreinoDTO> getTreinoDeHoje() { return _treinoDeHoje; }
    public LiveData<String> getErro() { return _erro; }
    public LiveData<Boolean> getEstaCarregando() { return _estaCarregando; }

    public TreinoDoDiaViewModel(@NonNull Application application) {
        super(application);
        sessionManager = new SessionManager(application.getApplicationContext());
        apiService = RetrofitClient.getApiService();
        buscarTreinoDeHoje();
    }

    /**
     * Busca o treino de hoje no endpoint /treino/hoje.
     */
    public void buscarTreinoDeHoje() {
        _estaCarregando.setValue(true);
        String token = sessionManager.getAuthToken();
        if (token == null) {
            _erro.setValue("Sessão inválida.");
            _estaCarregando.setValue(false);
            return;
        }

        apiService.getTreinoDeHoje("Bearer " + token).enqueue(new Callback<TreinoDTO>() {
            @Override
            public void onResponse(Call<TreinoDTO> call, Response<TreinoDTO> response) {
                _estaCarregando.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    _treinoDeHoje.setValue(response.body());
                } else if (response.code() == 404) {
                    // 404 (Not Found) é uma resposta válida (Dia de descanso)
                    _treinoDeHoje.setValue(null);
                } else {
                    _erro.setValue("Falha ao buscar treino de hoje.");
                }
            }

            @Override
            public void onFailure(Call<TreinoDTO> call, Throwable t) {
                _estaCarregando.setValue(false);
                _erro.setValue("Erro de rede: " + t.getMessage());
            }
        });
    }
}