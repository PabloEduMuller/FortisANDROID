package com.example.fortis.viewmodel.treino;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.fortis.data.api.ApiService;
import com.example.fortis.data.api.RetrofitClient;
import com.example.fortis.data.model.Exercicio;
import com.example.fortis.data.model.ExercicioDTO;
import com.example.fortis.data.model.Treino;
import com.example.fortis.data.model.TreinoDTO;

import java.util.ArrayList;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditarTreinoViewModel extends AndroidViewModel {

    private final ApiService apiService;

    // LiveData
    private final MutableLiveData<Boolean> _estaCarregando = new MutableLiveData<>();
    public LiveData<Boolean> getEstaCarregando() { return _estaCarregando; }

    private final MutableLiveData<String> _erro = new MutableLiveData<>();
    public LiveData<String> getErro() { return _erro; }

    private final MutableLiveData<Treino> _treino = new MutableLiveData<>();
    public LiveData<Treino> getTreino() { return _treino; }

    private final MutableLiveData<Boolean> _treinoDeletado = new MutableLiveData<>(false);
    public LiveData<Boolean> getTreinoDeletado() { return _treinoDeletado; }

    // Events
    private final MutableLiveData<Exercicio> _exercicioAdicionado = new MutableLiveData<>();
    public LiveData<Exercicio> getExercicioAdicionado() { return _exercicioAdicionado; }

    private final MutableLiveData<Exercicio> _exercicioAtualizado = new MutableLiveData<>();
    public LiveData<Exercicio> getExercicioAtualizado() { return _exercicioAtualizado; }

    private final MutableLiveData<Long> _idExercicioDeletado = new MutableLiveData<>();
    public LiveData<Long> getIdExercicioDeletado() { return _idExercicioDeletado; }

    public EditarTreinoViewModel(@NonNull Application application) {
        super(application);
        apiService = RetrofitClient.getApiService();
    }

    // --- Métodos Principais ---

    public void inicializarNovoTreino() {
        Treino novo = new Treino();
        novo.setExercicios(new ArrayList<>());
        _treino.setValue(novo);
    }

    // --- NOVO MÉTODO PARA CORRIGIR O ERRO ---
    public void atualizarTreinoLocal(Treino treino) {
        _treino.setValue(treino);
    }

    public void buscarTreinoPorId(String token, long treinoId) {
        _estaCarregando.setValue(true);
        apiService.getTreinoCompleto("Bearer " + token, treinoId).enqueue(new Callback<TreinoDTO>() {
            @Override
            public void onResponse(Call<TreinoDTO> call, Response<TreinoDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    _treino.setValue(converterParaTreino(response.body()));
                } else {
                    _erro.setValue("Falha ao buscar treino: " + response.code());
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

    public void salvarTreino(String token, Treino treino) {
        _estaCarregando.setValue(true);
        apiService.salvarTreino("Bearer " + token, treino).enqueue(new Callback<Treino>() {
            @Override
            public void onResponse(Call<Treino> call, Response<Treino> response) {
                _estaCarregando.setValue(false);
                if (response.isSuccessful()) {
                    _treino.setValue(response.body());
                    _erro.setValue("SUCESSO_SALVAR");
                } else {
                    _erro.setValue("Falha ao salvar: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Treino> call, Throwable t) {
                _estaCarregando.setValue(false);
                _erro.setValue("Erro de rede ao salvar: " + t.getMessage());
            }
        });
    }

    public void deletarTreino(String token, long treinoId) {
        _estaCarregando.setValue(true);
        apiService.deletarTreino("Bearer " + token, treinoId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) _treinoDeletado.setValue(true);
                else _erro.setValue("Falha ao deletar: " + response.code());
                _estaCarregando.setValue(false);
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                _estaCarregando.setValue(false);
                _erro.setValue("Erro: " + t.getMessage());
            }
        });
    }

    public void adicionarExercicioAPI(String token, long treinoId, String nome, int series, int repeticoes) {
        ExercicioDTO dto = new ExercicioDTO();
        dto.setNome(nome);
        dto.setSeries(series);
        dto.setRepeticoes(repeticoes);

        apiService.adicionarExercicio("Bearer " + token, treinoId, dto).enqueue(new Callback<ExercicioDTO>() {
            @Override
            public void onResponse(Call<ExercicioDTO> call, Response<ExercicioDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Treino t = _treino.getValue();
                    if (t != null) {
                        t.getExercicios().add(converterParaExercicio(response.body()));
                        _treino.setValue(t);
                        _exercicioAdicionado.setValue(converterParaExercicio(response.body()));
                    }
                } else {
                    _erro.setValue("Falha ao adicionar exercício.");
                }
            }
            @Override
            public void onFailure(Call<ExercicioDTO> call, Throwable t) {
                _erro.setValue("Erro: " + t.getMessage());
            }
        });
    }


    public void editarExercicioAPI(String token, long exercicioId, String nome, int series, int repeticoes) {
        ExercicioDTO dto = new ExercicioDTO();
        dto.setId(exercicioId);
        dto.setNome(nome);
        dto.setSeries(series);
        dto.setRepeticoes(repeticoes);

        apiService.atualizarExercicio("Bearer " + token, exercicioId, dto).enqueue(new Callback<ExercicioDTO>() {
            @Override
            public void onResponse(Call<ExercicioDTO> call, Response<ExercicioDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Treino t = _treino.getValue();
                    Exercicio atualizado = converterParaExercicio(response.body());
                    if (t != null) {
                        for (int i = 0; i < t.getExercicios().size(); i++) {
                            if (t.getExercicios().get(i).getId() == exercicioId) {
                                t.getExercicios().set(i, atualizado);
                                break;
                            }
                        }
                        _treino.setValue(t);
                        _exercicioAtualizado.setValue(atualizado);
                    }
                } else {
                    _erro.setValue("Falha ao atualizar: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<ExercicioDTO> call, Throwable t) {
                _erro.setValue("Erro: " + t.getMessage());
            }
        });
    }

    // --- O MÉTODO QUE FALTAVA ---
    public void deletarExercicio(String token, long exercicioId) {
        apiService.deletarExercicio("Bearer " + token, exercicioId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Treino t = _treino.getValue();
                    if (t != null) {
                        t.getExercicios().removeIf(e -> e.getId() == exercicioId);
                        _treino.setValue(t);
                        _idExercicioDeletado.setValue(exercicioId);
                    }
                } else {
                    _erro.setValue("Falha ao deletar exercício: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                _erro.setValue("Erro de rede: " + t.getMessage());
            }
        });
    }

    // --- Conversores ---
    private Treino converterParaTreino(TreinoDTO dto) {
        if (dto == null) return null;
        Treino t = new Treino();
        t.setId(dto.getId());
        t.setNome(dto.getNome());
        t.setDiaSemana(dto.getDiaSemana());
        if(dto.getExercicios() != null) {
            t.setExercicios(dto.getExercicios().stream().map(this::converterParaExercicio).collect(Collectors.toList()));
        } else {
            t.setExercicios(new ArrayList<>());
        }
        return t;
    }

    private Exercicio converterParaExercicio(ExercicioDTO dto) {
        if (dto == null) return null;
        Exercicio e = new Exercicio();
        e.setId(dto.getId());
        e.setNome(dto.getNome());
        e.setSeries(dto.getSeries());
        e.setRepeticoes(dto.getRepeticoes());
        return e;
    }
}