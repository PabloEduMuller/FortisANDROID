package com.example.fortis.data.api;

import com.example.fortis.data.model.AlunoDTO;
import com.example.fortis.data.model.ExercicioDTO;
import com.example.fortis.data.model.HistoricoTreino;
import com.example.fortis.data.model.LoginRequest;
import com.example.fortis.data.model.LoginResponse;
import com.example.fortis.data.model.MudarSenhaDTO;
import com.example.fortis.data.model.Treino;
import com.example.fortis.data.model.TreinoDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE; // <-- 1. IMPORTAR DELETE
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path; // <-- 2. IMPORTAR PATH

public interface ApiService {

    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @GET("aluno/perfil")
    Call<AlunoDTO> getPerfil(@Header("Authorization") String token);

    @PUT("aluno/perfil")
    Call<AlunoDTO> updatePerfil(@Header("Authorization") String token, @Body AlunoDTO alunoDTO);

    @PUT("aluno/mudar-senha")
    Call<Void> mudarSenha(@Header("Authorization") String token, @Body MudarSenhaDTO mudarSenhaDTO);

    @GET("treino/hoje")
    Call<TreinoDTO> getTreinoDeHoje(@Header("Authorization") String token);

    @GET("treino/listar")
    Call<List<TreinoDTO>> getTreinos(@Header("Authorization") String token);

    /*
     --- 3. ENDPOINTS DE GERENCIAMENTO DE TREINO/EXERCÍCIO ---
     (Estes são os métodos que estavam faltando)
    */

    @GET("treino/{id}")
    Call<TreinoDTO> getTreinoCompleto(
            @Header("Authorization") String token,
            @Path("id") long treinoId
    );

    @DELETE("treino/{treinoId}")
    Call<Void> deletarTreino(
            @Header("Authorization") String token,
            @Path("treinoId") long treinoId
    );

    @POST("exercicio/treino/{treinoId}")
    Call<ExercicioDTO> adicionarExercicio(
            @Header("Authorization") String token,
            @Path("treinoId") long treinoId,
            @Body ExercicioDTO exercicioDTO
    );

    @PUT("exercicio/{exercicioId}")
    Call<ExercicioDTO> atualizarExercicio(
            @Header("Authorization") String token,
            @Path("exercicioId") long exercicioId,
            @Body ExercicioDTO exercicioDTO
    );

    @DELETE("exercicio/{exercicioId}")
    Call<Void> deletarExercicio(
            @Header("Authorization") String token,
            @Path("exercicioId") long exercicioId
    );

    @POST("treino/salvar")
    Call<Treino> salvarTreino(@Header("Authorization") String token, @Body Treino treino);


    @POST("historico/salvar")
    Call<Void> salvarHistorico(
            @Header("Authorization") String token,
            @Body com.example.fortis.data.model.HistoricoTreinoDTO dto
    );

    @GET("historico/meu-progresso")
    Call<java.util.List<com.example.fortis.data.model.HistoricoTreino>> getHistoricoProgresso(
            @Header("Authorization") String token
    );

    @GET("historico/realizados")
    Call<List<com.example.fortis.data.model.HistoricoTreino>> getExerciciosRealizados(@Header("Authorization") String token);

}