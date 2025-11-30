// Em: Fortis/app/src/main/java/com/example/fortis/data/model/TreinoDTO.java
package com.example.fortis.data.model;

import java.util.List;

// Esta classe é um espelho do seu TreinoDTO do backend
//
public class TreinoDTO {
    private Long id;
    private String nome;
    private String diaSemana;
    private List<ExercicioDTO> exercicios; // <-- Note que usa ExercicioDTO

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDiaSemana() { return diaSemana; }
    public void setDiaSemana(String diaSemana) { this.diaSemana = diaSemana; }
    public List<ExercicioDTO> getExercicios() { return exercicios; }
    public void setExercicios(List<ExercicioDTO> exercicios) { this.exercicios = exercicios; }
}