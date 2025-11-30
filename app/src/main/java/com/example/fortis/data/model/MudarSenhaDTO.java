package com.example.fortis.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * POJO para enviar as informações de mudança de senha para a API.
 * Espelha o MudarSenhaDTO do backend.
 */
public class MudarSenhaDTO {

    @SerializedName("senhaAtual")
    private String senhaAtual;

    @SerializedName("novaSenha")
    private String novaSenha;

    // Construtor
    public MudarSenhaDTO(String senhaAtual, String novaSenha) {
        this.senhaAtual = senhaAtual;
        this.novaSenha = novaSenha;
    }

    // Getters e Setters (necessários para o Gson)
    public String getSenhaAtual() {
        return senhaAtual;
    }

    public void setSenhaAtual(String senhaAtual) {
        this.senhaAtual = senhaAtual;
    }

    public String getNovaSenha() {
        return novaSenha;
    }

    public void setNovaSenha(String novaSenha) {
        this.novaSenha = novaSenha;
    }
}