package com.example.fortis.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class Aluno {

    @SerializedName("id")
    private long id;
    @SerializedName("nome")
    private String nome;
    @SerializedName("cpf")
    private String cpf;
    @SerializedName("email")
    private String email;
    @SerializedName("senha")
    private String senha;
    @SerializedName("dataNascimento")
    private Date dataNascimento;
    @SerializedName("peso")
    private double peso;
    @SerializedName("altura")
    private double altura;
    @SerializedName("sexo")
    private String sexo;
    @SerializedName("telefone")
    private String telefone;

    // Construtor (se precisar)
    public Aluno() {}

    // --- Getters (Já existiam) ---
    public long getId() { return id; }
    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public String getEmail() { return email; }
    public String getSenha() { return senha; }
    public Date getDataNascimento() { return dataNascimento; }
    public double getPeso() { return peso; }
    public double getAltura() { return altura; }
    public String getSexo() { return sexo; }
    public String getTelefone() { return telefone; }

    // --- Setters (Alguns já existiam, outros foram adicionados) ---
    public void setId(long id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public void setEmail(String email) { this.email = email; }
    public void setSenha(String senha) { this.senha = senha; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    // --- CORREÇÃO: SETTERS ADICIONADOS ---
    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public void setAltura(double altura) {
        this.altura = altura;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }
}