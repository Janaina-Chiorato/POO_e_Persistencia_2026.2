package model;

import java.time.LocalDate;

public class DispositivoRede extends AtivoTI {

    // Atributos especificos
    private String tipoDispositivo;
    private String enderecoIp;
    private int quantidadePortas;

    // Construtor
    public DispositivoRede(
            int id,
            String codigoPatrimonio,
            String nome,
            String modelo,
            LocalDate dataAquisicao,
            String status,
            double valor,
            String tipoDispositivo,
            String enderecoIp,
            int quantidadePortas) {

        super(
                id,
                codigoPatrimonio,
                nome,
                modelo,
                dataAquisicao,
                status,
                valor
        );

        setTipoDispositivo(tipoDispositivo);
        setEnderecoIp(enderecoIp);
        setQuantidadePortas(quantidadePortas);
    }

    // Getters
    public String getTipoDispositivo() {
        return tipoDispositivo;
    }

    public String getEnderecoIp() {
        return enderecoIp;
    }

    public int getQuantidadePortas() {
        return quantidadePortas;
    }

    // Setters com validacao
    public void setTipoDispositivo(String tipoDispositivo) {
        if (tipoDispositivo == null
                || tipoDispositivo.isBlank()) {

            throw new IllegalArgumentException(
                    "O tipo de dispositivo e obrigatorio.");
        }

        this.tipoDispositivo = tipoDispositivo.trim();
    }

    public void setEnderecoIp(String enderecoIp) {
        if (enderecoIp == null || enderecoIp.isBlank()) {
            throw new IllegalArgumentException(
                    "O endereco IP e obrigatorio.");
        }

        this.enderecoIp = enderecoIp.trim();
    }

    public void setQuantidadePortas(int quantidadePortas) {
        if (quantidadePortas <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade de portas deve ser maior que zero.");
        }

        this.quantidadePortas = quantidadePortas;
    }

    // Implementacao do metodo abstrato
    @Override
    public double calcularCustoManutencaoMensal() {
        return 50.0 + (quantidadePortas * 10.0);
    }

    // Representacao textual
    @Override
    public String toString() {
        return "[Dispositivo de Rede] " + super.toString()
                + " | Tipo: " + tipoDispositivo
                + " | IP: " + enderecoIp
                + " | Portas: " + quantidadePortas
                + " | Custo mensal: R$ "
                + calcularCustoManutencaoMensal();
    }
}