package com.example.fortis.data.model;

public class Exercicio {
    private long id;
    private String nome;
    private int series;
    private int repeticoes;

    // --- CORREÇÃO 1: Adicionar construtor vazio ---
    // O ViewModel (e o GSON/Retrofit) precisa disso para
    // converter DTOs para este modelo.
    public Exercicio() {}

    // Construtor que você já tinha (útil para criar novos)
    public Exercicio(String nome, int series, int repeticoes) {
        this.nome = nome;
        this.series = series;
        this.repeticoes = repeticoes;
    }

    // Getters
    public long getId() { return id; }
    public String getNome() { return nome; }
    public int getSeries() { return series; }
    public int getRepeticoes() { return repeticoes; }

    // --- CORREÇÃO 2: Adicionar Setters ---
    // O ViewModel precisa setar o ID após a conversão
    public void setId(long id) {
        this.id = id;
    }

    // Setters para os outros campos (Boas práticas)
    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setSeries(int series) {
        this.series = series;
    }

    public void setRepeticoes(int repeticoes) {
        this.repeticoes = repeticoes;
    }
}