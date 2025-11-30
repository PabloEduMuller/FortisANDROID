package com.example.fortis.data.model;

import com.google.gson.annotations.SerializedName;

public class HistoricoTreinoDTO {

    @SerializedName("alunoId")
    private Long alunoId;

    @SerializedName("treinoId")
    private Long treinoId;

    @SerializedName("duracaoSegundos")
    private Integer duracaoSegundos;

    @SerializedName("cargaTotalLevantada")
    private Double cargaTotalLevantada;

    @SerializedName("totalSeries")
    private Integer totalSeries;

    // Construtor Vazio
    public HistoricoTreinoDTO() {
    }

    // Getters e Setters
    public Long getAlunoId() {
        return alunoId;
    }

    public void setAlunoId(Long alunoId) {
        this.alunoId = alunoId;
    }

    public Long getTreinoId() {
        return treinoId;
    }

    public void setTreinoId(Long treinoId) {
        this.treinoId = treinoId;
    }

    public Integer getDuracaoSegundos() {
        return duracaoSegundos;
    }

    public void setDuracaoSegundos(Integer duracaoSegundos) {
        this.duracaoSegundos = duracaoSegundos;
    }

    public Double getCargaTotalLevantada() {
        return cargaTotalLevantada;
    }

    public void setCargaTotalLevantada(Double cargaTotalLevantada) {
        this.cargaTotalLevantada = cargaTotalLevantada;
    }

    public Integer getTotalSeries() {
        return totalSeries;
    }

    public void setTotalSeries(Integer totalSeries) {
        this.totalSeries = totalSeries;
    }
}