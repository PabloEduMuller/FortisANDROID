package com.example.fortis.viewmodel.home;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.fortis.data.SessionManager;
import com.example.fortis.data.api.ApiService;
import com.example.fortis.data.api.RetrofitClient;
import com.example.fortis.data.model.Aluno;
import com.example.fortis.data.model.AlunoDTO;
import com.example.fortis.data.model.Exercicio;
import com.example.fortis.data.model.ExercicioDTO;
import com.example.fortis.data.model.Treino;
import com.example.fortis.data.model.TreinoDTO;

// --- 1. IMPORTAR ARRAYLIST ---
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends AndroidViewModel {

    // LiveData
    private MutableLiveData<Aluno> aluno = new MutableLiveData<>();
    private MutableLiveData<List<Treino>> treinos = new MutableLiveData<>();
    private MutableLiveData<Treino> treinoDeHoje = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();
    private MutableLiveData<Boolean> _estaCarregando = new MutableLiveData<>();
    private MutableLiveData<List<TreinoDTO>> _meusTreinos = new MutableLiveData<>();
    private MutableLiveData<Boolean> _estaCarregandoMeusTreinos = new MutableLiveData<>();


    private SessionManager sessionManager;
    private ApiService apiService;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        sessionManager = new SessionManager(application.getApplicationContext());
        apiService = RetrofitClient.getApiService();
    }

    // --- Getters ---
    public LiveData<Aluno> getAluno() { return aluno; }
    public LiveData<List<Treino>> getTreinos() { return treinos; }
    public LiveData<Treino> getTreinoDeHoje() { return treinoDeHoje; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> getEstaCarregando() { return _estaCarregando; }
    public LiveData<List<TreinoDTO>> getMeusTreinos() { return _meusTreinos; }
    public LiveData<Boolean> getEstaCarregandoMeusTreinos() { return _estaCarregandoMeusTreinos; }


    // --- Métodos de Ação ---

    public void fetchHomeData() {
        _estaCarregando.setValue(true);
        String token = sessionManager.getAuthToken();
        if (token == null || token.isEmpty()) {
            error.setValue("Sessão inválida. Por favor, faça o login novamente.");
            _estaCarregando.setValue(false);
            return;
        }
        String authToken = "Bearer " + token;

        buscarPerfil(authToken);
        buscarTreinoDeHoje(authToken);
        buscarMeusTreinos(authToken); // Este já busca os treinos da semana
    }

    public void buscarPerfil(String authToken) {
        apiService.getPerfil(authToken).enqueue(new Callback<AlunoDTO>() {
            @Override
            public void onResponse(Call<AlunoDTO> call, Response<AlunoDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Aluno alunoConvertido = converterParaAluno(response.body());
                    aluno.setValue(alunoConvertido);
                } else {
                    error.setValue("Falha ao buscar perfil do usuário.");
                }
            }
            @Override
            public void onFailure(Call<AlunoDTO> call, Throwable t) {
                error.setValue("Erro de rede (perfil): " + t.getMessage());
            }
        });
    }

    public void buscarMeusTreinos(String authToken) {
        _estaCarregandoMeusTreinos.setValue(true);
        apiService.getTreinos(authToken).enqueue(new Callback<List<TreinoDTO>>() {
            @Override
            public void onResponse(Call<List<TreinoDTO>> call, Response<List<TreinoDTO>> response) {
                _estaCarregandoMeusTreinos.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    _meusTreinos.setValue(response.body());

                    List<Treino> treinosConvertidos = response.body().stream()
                            .map(dto -> converterParaTreino(dto))
                            .collect(Collectors.toList());
                    treinos.setValue(treinosConvertidos);
                } else {
                    error.setValue("Falha ao buscar lista de treinos.");
                }
            }
            @Override
            public void onFailure(Call<List<TreinoDTO>> call, Throwable t) {
                _estaCarregandoMeusTreinos.setValue(false);
                error.setValue("Erro de rede (treinos): " + t.getMessage());
            }
        });
    }

    public void buscarTreinoDeHoje(String authToken) {
        apiService.getTreinoDeHoje(authToken).enqueue(new Callback<TreinoDTO>() {
            @Override
            public void onResponse(Call<TreinoDTO> call, Response<TreinoDTO> response) {
                _estaCarregando.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    Treino treinoConvertido = converterParaTreino(response.body());
                    treinoDeHoje.setValue(treinoConvertido);
                } else if (response.code() == 404) {
                    treinoDeHoje.setValue(null); // Nenhum treino para hoje
                } else {
                    error.setValue("Falha ao buscar treino de hoje.");
                }
            }
            @Override
            public void onFailure(Call<TreinoDTO> call, Throwable t) {
                _estaCarregando.setValue(false);
                error.setValue("Erro de rede (treino hoje): " + t.getMessage());
            }
        });
    }

    // --- MÉTODOS DE CONVERSÃO ---

    /**
     * --- MÉTODO CORRIGIDO (À PROVA DE NULOS / DEFENSIVO) ---
     */
    private Treino converterParaTreino(TreinoDTO dto) {
        if (dto == null) return null;

        Treino treino = new Treino();
        treino.setId(dto.getId());
        treino.setNome(dto.getNome());
        treino.setDiaSemana(dto.getDiaSemana());

        // --- CORREÇÃO DEFENSIVA APLICADA AQUI ---
        // Garante que a lista nunca seja nula, mesmo que o DTO seja.
        if (dto.getExercicios() != null) {
            List<Exercicio> exercicios = dto.getExercicios().stream()
                    .map(this::converterParaExercicio)
                    .collect(Collectors.toList());
            treino.setExercicios(exercicios);
        } else {
            // Garante que a lista exista (vazia) para evitar NPE na Activity
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

    private Aluno converterParaAluno(AlunoDTO dto) {
        if (dto == null) return null;

        Aluno aluno = new Aluno();

        if (dto.getIdAluno() != null) {
            aluno.setId(dto.getIdAluno());
        } else {
            aluno.setId(0);
        }

        aluno.setNome(dto.getNome());
        aluno.setCpf(dto.getCpf());
        aluno.setEmail(dto.getEmail());
        aluno.setDataNascimento(dto.getDataNascimento());
        aluno.setTelefone(dto.getTelefone());

        return aluno;
    }
}