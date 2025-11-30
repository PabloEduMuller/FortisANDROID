package com.example.fortis.viewmodel.treino;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.fortis.data.SessionManager;
import com.example.fortis.data.api.ApiService;
import com.example.fortis.data.api.RetrofitClient;
import com.example.fortis.data.model.Exercicio;
import com.example.fortis.data.model.ExercicioDTO;
import com.example.fortis.data.model.Treino;
import com.example.fortis.data.model.TreinoDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViewModel dedicado a buscar a lista completa de treinos
 * para a tela "Meus Treinos".
 */
public class MeusTreinosViewModel extends AndroidViewModel {

    private ApiService apiService;
    private SessionManager sessionManager;

    private MutableLiveData<Boolean> _estaCarregando = new MutableLiveData<>();
    private MutableLiveData<String> _erro = new MutableLiveData<>();
    // LiveData que a Activity irá observar
    private MutableLiveData<List<Treino>> _treinos = new MutableLiveData<>();

    // Getters
    public LiveData<Boolean> getEstaCarregando() { return _estaCarregando; }
    public LiveData<String> getErro() { return _erro; }
    public LiveData<List<Treino>> getTreinos() { return _treinos; }

    public MeusTreinosViewModel(@NonNull Application application) {
        super(application);
        sessionManager = new SessionManager(application.getApplicationContext());
        apiService = RetrofitClient.getApiService();
    }

    /**
     * Busca a lista de todos os treinos do utilizador logado.
     */
    public void buscarTreinos() {
        _estaCarregando.setValue(true);
        String token = sessionManager.getAuthToken();
        if (token == null) {
            _erro.setValue("Sessão inválida.");
            _estaCarregando.setValue(false);
            return;
        }

        // Usa o endpoint 'listar'
        apiService.getTreinos("Bearer " + token).enqueue(new Callback<List<TreinoDTO>>() {
            @Override
            public void onResponse(Call<List<TreinoDTO>> call, Response<List<TreinoDTO>> response) {
                _estaCarregando.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    // Converte a lista de DTOs para o modelo local 'Treino'
                    List<Treino> treinosConvertidos = response.body().stream()
                            .map(dto -> converterParaTreino(dto))
                            .collect(Collectors.toList());
                    _treinos.setValue(treinosConvertidos);
                } else {
                    _erro.setValue("Falha ao buscar lista de treinos.");
                }
            }

            @Override
            public void onFailure(Call<List<TreinoDTO>> call, Throwable t) {
                _estaCarregando.setValue(false);
                _erro.setValue("Erro de rede: " + t.getMessage());
            }
        });
    }

    // --- Métodos de Conversão (Reutilizados do HomeViewModel) ---

    private Treino converterParaTreino(TreinoDTO dto) {
        if (dto == null) return null;
        Treino treino = new Treino();
        treino.setId(dto.getId());
        treino.setNome(dto.getNome());
        treino.setDiaSemana(dto.getDiaSemana());

        if (dto.getExercicios() != null) {
            List<Exercicio> exercicios = dto.getExercicios().stream()
                    .map(this::converterParaExercicio)
                    .collect(Collectors.toList());
            treino.setExercicios(exercicios);
        } else {
            treino.setExercicios(new ArrayList<>());
        }
        return treino;
    }

    private Exercicio converterParaExercicio(ExercicioDTO dto) {
        if (dto == null) return null;
        Exercicio exercicio = new Exercicio();
        exercicio.setId(dto.getId());
        exercicio.setNome(dto.getNome());
        exercicio.setSeries(dto.getSeries());
        exercicio.setRepeticoes(dto.getRepeticoes());
        return exercicio;
    }
}