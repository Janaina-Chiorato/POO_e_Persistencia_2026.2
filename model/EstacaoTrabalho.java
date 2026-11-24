package model;

import java.time.LocalDate;

public class EstacaoTrabalho extends AtivoTI {

    // Atributos especificos
    private String processador;
    private int memoriaRam;
    private String departamento;

    // Construtor
    public EstacaoTrabalho(
            int id,
            String codigoPatrimonio,
            String nome,
            String modelo,
            LocalDate dataAquisicao,
            String status,
            double valor,
            String processador,
            int memoriaRam,
            String departamento) {

        super(
                id,
                codigoPatrimonio,
                nome,
                modelo,
                dataAquisicao,
                status,
                valor
        );

        setProcessador(processador);
        setMemoriaRam(memoriaRam);
        setDepartamento(departamento);
    }

    // Getters
    public String getProcessador() {
        return processador;
    }

    public int getMemoriaRam() {
        return memoriaRam;
    }

    public String getDepartamento() {
        return departamento;
    }

    // Setters com validacao
    public void setProcessador(String processador) {
        if (processador == null || processador.isBlank()) {
            throw new IllegalArgumentException(
                    "O processador e obrigatorio.");
        }

        this.processador = processador.trim();
    }

    public void setMemoriaRam(int memoriaRam) {
        if (memoriaRam <= 0) {
            throw new IllegalArgumentException(
                    "A memoria RAM deve ser maior que zero.");
        }

        this.memoriaRam = memoriaRam;
    }

    public void setDepartamento(String departamento) {
        if (departamento == null || departamento.isBlank()) {
            throw new IllegalArgumentException(
                    "O departamento e obrigatorio.");
        }

        this.departamento = departamento.trim();
    }

    // Implementacao do metodo abstrato
    @Override
    public double calcularCustoManutencaoMensal() {
        return 80.0 + (memoriaRam * 5.0);
    }

    // Representacao textual
    @Override
    public String toString() {
        return "[Estacao de Trabalho] " + super.toString()
                + " | Processador: " + processador
                + " | RAM: " + memoriaRam + " GB"
                + " | Departamento: " + departamento
                + " | Custo mensal: R$ "
                + calcularCustoManutencaoMensal();
    }
}