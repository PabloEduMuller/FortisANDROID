package com.example.fortis.viewmodel.treino;

import android.app.Application;
import android.util.Log; // <-- 1. IMPORTAR LOG

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

public class ExecucaoTreinoViewModel extends AndroidViewModel {

    private final ApiService apiService;
    private final SessionManager sessionManager;

    // Lógica de estado do treino
    private Treino treinoCompleto;
    private int indiceExercicioAtual = 0;
    private int serieAtual = 0;

    // LiveData para a UI
    private final MutableLiveData<Boolean> _estaCarregando = new MutableLiveData<>(true);
    public LiveData<Boolean> getEstaCarregando() { return _estaCarregando; }

    private final MutableLiveData<String> _erro = new MutableLiveData<>();
    public LiveData<String> getErro() { return _erro; }

    private final MutableLiveData<Exercicio> _exercicioAtual = new MutableLiveData<>();
    public LiveData<Exercicio> getExercicioAtual() { return _exercicioAtual; }

    private final MutableLiveData<Boolean> _treinoConcluido = new MutableLiveData<>(false);
    public LiveData<Boolean> getTreinoConcluido() { return _treinoConcluido; }


    public ExecucaoTreinoViewModel(@NonNull Application application) {
        super(application);
        sessionManager = new SessionManager(application.getApplicationContext());
        apiService = RetrofitClient.getApiService();
    }

    // ... (carregarTreino permanece o mesmo) ...
    public void carregarTreino(String token, long treinoId) {
        _estaCarregando.setValue(true);
        indiceExercicioAtual = 0;
        serieAtual = 0;

        apiService.getTreinoCompleto("Bearer " + token, treinoId).enqueue(new Callback<TreinoDTO>() {
            @Override
            public void onResponse(Call<TreinoDTO> call, Response<TreinoDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    treinoCompleto = converterParaTreino(response.body());
                    if (treinoCompleto != null && !treinoCompleto.getExercicios().isEmpty()) {
                        _exercicioAtual.setValue(treinoCompleto.getExercicios().get(indiceExercicioAtual));
                    } else {
                        _erro.setValue("Este treino não possui exercícios.");
                    }
                } else {
                    _erro.setValue("Falha ao carregar treino: " + response.message());
                }
                _estaCarregando.setValue(false);
            }

            @Override
            public void onFailure(Call<TreinoDTO> call, Throwable t) {
                _erro.setValue("Erro de rede: " + t.getMessage());
                _estaCarregando.setValue(false);
            }
        });
    }

    // --- 2. MÉTODO ANTIGO RENOMEADO (AGORA PRIVADO) ---
    /**
     * Apenas avança o estado do treino (próxima série ou exercício).
     */
    private void avancarEstadoTreino() {
        if (treinoCompleto == null) return;
        Exercicio exercicio = _exercicioAtual.getValue();
        if (exercicio == null) return;

        serieAtual++;

        if (serieAtual >= exercicio.getSeries()) {
            proximoExercicio();
        } else {
            _exercicioAtual.setValue(exercicio);
        }
    }

    public boolean isTreinoFinalizado() {
        // Verifica se o objeto treino existe para evitar NullPointerException
        if (treinoCompleto == null || treinoCompleto.getExercicios() == null) {
            return false;
        }

        // Verifica se estamos no último exercício
        boolean ultimoExercicio = indiceExercicioAtual >= treinoCompleto.getExercicios().size() - 1;

        // Verifica se estamos na última série (ou além dela)
        // Recupera o exercício atual do LiveData
        Exercicio exercicio = _exercicioAtual.getValue();
        boolean ultimaSerie = false;

        if (exercicio != null) {
            // Se a série atual (índice 0-based) + 1 for maior ou igual ao total de séries
            ultimaSerie = (serieAtual + 1) >= exercicio.getSeries();
        }

        return ultimoExercicio && ultimaSerie;
    }

    // --- 3. NOVO MÉTODO PÚBLICO (Chamado pela Activity) ---
    /**
     * Salva os dados da série (Carga/Reps) e avança o estado do treino.
     */
    public void concluirSerie(String cargaStr, String repsStr) {
        if (treinoCompleto == null) return;

        try {
            double carga = Double.parseDouble(cargaStr);
            int repeticoes = Integer.parseInt(repsStr);

            // --- PONTO DE FUTURA INTEGRAÇÃO ---
            // Aqui é onde salvaríamos os dados no banco de dados (via API)
            // ex: apiService.salvarHistoricoSerie(token, idExercicio, serieAtual, carga, repeticoes)...
            Log.d("ExecucaoTreinoViewModel", "SÉRIE CONCLUÍDA: " +
                    "Exercicio: " + _exercicioAtual.getValue().getNome() +
                    ", Série: " + (serieAtual + 1) +
                    ", Carga: " + carga + "kg" +
                    ", Reps: " + repeticoes);
            // --- FIM DA INTEGRAÇÃO ---

            // Avança para a próxima série/exercício
            avancarEstadoTreino();

        } catch (NumberFormatException e) {
            _erro.setValue("Valores de carga ou repetições inválidos.");
        }
    }


    // --- 4. MÉTODO "PULAR" (Da Etapa 17 - permanece o mesmo) ---
    public void proximoExercicio() {
        if (treinoCompleto == null) return;

        indiceExercicioAtual++;
        serieAtual = 0;

        if (indiceExercicioAtual >= treinoCompleto.getExercicios().size()) {
            _treinoConcluido.setValue(true);
        } else {
            _exercicioAtual.setValue(treinoCompleto.getExercicios().get(indiceExercicioAtual));
        }
    }

    // ... (Getters e Conversores DTO permanecem os mesmos) ...
    public int getSerieAtual() {
        return serieAtual;
    }
    public int getIndiceExercicioAtual() {
        return indiceExercicioAtual;
    }
    public int getTotalExercicios() {
        return (treinoCompleto != null && treinoCompleto.getExercicios() != null) ? treinoCompleto.getExercicios().size() : 0;
    }
    public String getNomeDoTreino() {
        return (treinoCompleto != null) ? treinoCompleto.getNome() : "Treino";
    }
    private Treino converterParaTreino(TreinoDTO dto) {
        if (dto == null) return null;
        Treino treino = new Treino();
        treino.setId(dto.getId());
        treino.setNome(dto.getNome());
        treino.setDiaSemana(dto.getDiaSemana());
        if (dto.getExercicios() != null) {
            treino.setExercicios(dto.getExercicios().stream()
                    .map(this::converterParaExercicio)
                    .collect(Collectors.toList()));
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