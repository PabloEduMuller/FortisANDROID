package com.example.fortis.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class HistoricoTreino {
    @SerializedName("data")
    private String data; // Formato "yyyy-MM-dd"

    @SerializedName("cargaTotal")
    private Double cargaTotal;

    @SerializedName("duracaoSegundos")
    private Integer duracaoSegundos;

    // Getters
    public String getData() { return data; }
    public Double getCargaTotal() { return cargaTotal; }
    public Integer getDuracaoSegundos() { return duracaoSegundos; }
}