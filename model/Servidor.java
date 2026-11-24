package model;

import java.time.LocalDate;

public class Servidor extends AtivoTI {

    // Atributos especificos do servidor
    private String ipEstatico;
    private String sistemaOperacional;
    private int memoriaRam;

    // Construtor
    public Servidor(
            int id,
            String codigoPatrimonio,
            String nome,
            String modelo,
            LocalDate dataAquisicao,
            String status,
            double valor,
            String ipEstatico,
            String sistemaOperacional,
            int memoriaRam) {

        super(
                id,
                codigoPatrimonio,
                nome,
                modelo,
                dataAquisicao,
                status,
                valor
        );

        setIpEstatico(ipEstatico);
        setSistemaOperacional(sistemaOperacional);
        setMemoriaRam(memoriaRam);
    }

    // Getters
    public String getIpEstatico() {
        return ipEstatico;
    }

    public String getSistemaOperacional() {
        return sistemaOperacional;
    }

    public int getMemoriaRam() {
        return memoriaRam;
    }

    // Setters com validacao
    public void setIpEstatico(String ipEstatico) {
        if (ipEstatico == null || ipEstatico.isBlank()) {
            throw new IllegalArgumentException(
                    "O endereco IP e obrigatorio.");
        }

        this.ipEstatico = ipEstatico.trim();
    }

    public void setSistemaOperacional(String sistemaOperacional) {
        if (sistemaOperacional == null
                || sistemaOperacional.isBlank()) {

            throw new IllegalArgumentException(
                    "O sistema operacional e obrigatorio.");
        }

        this.sistemaOperacional = sistemaOperacional.trim();
    }

    public void setMemoriaRam(int memoriaRam) {
        if (memoriaRam <= 0) {
            throw new IllegalArgumentException(
                    "A memoria RAM deve ser maior que zero.");
        }

        this.memoriaRam = memoriaRam;
    }

    // Implementacao do metodo abstrato
    @Override
    public double calcularCustoManutencaoMensal() {
        return 200.0 + (memoriaRam * 50.0);
    }

    // Representacao textual
    @Override
    public String toString() {
        return "[Servidor] " + super.toString()
                + " | IP: " + ipEstatico
                + " | SO: " + sistemaOperacional
                + " | RAM: " + memoriaRam + " GB"
                + " | Custo mensal: R$ "
                + calcularCustoManutencaoMensal();
    }
}