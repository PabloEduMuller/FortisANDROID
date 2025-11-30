// Em: Fortis/app/src/main/java/com/example/fortis/data/model/ExercicioDTO.java
package com.example.fortis.data.model;

// Esta classe é um espelho do seu ExercicioDTO do backend
//
public class ExercicioDTO {
    private Long id;
    private String nome;
    private int series;
    private int repeticoes;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public int getSeries() { return series; }
    public void setSeries(int series) { this.series = series; }
    public int getRepeticoes() { return repeticoes; }
    public void setRepeticoes(int repeticoes) { this.repeticoes = repeticoes; }
}