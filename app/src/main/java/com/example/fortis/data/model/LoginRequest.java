package com.example.fortis.data.model;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {

    @SerializedName("cpf")
    private String cpf;

    @SerializedName("senha")
    private String senha;

    // ADICIONE ESTE BLOCO DE CÓDIGO
    public LoginRequest(String cpf, String senha) {
        this.cpf = cpf;
        this.senha = senha;
    }

    // Getters e Setters...
    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}