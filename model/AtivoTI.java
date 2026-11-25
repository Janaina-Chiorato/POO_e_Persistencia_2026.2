package model;

import java.time.LocalDate;
import java.util.Locale;

public abstract class AtivoTI {

    // Atributos privados - Encapsulamento
    private int id;
    private String codigoPatrimonio;
    private String nome;
    private String modelo;
    private LocalDate dataAquisicao;
    private String status;
    private double valor;

    // Construtor
    public AtivoTI(
            int id,
            String codigoPatrimonio,
            String nome,
            String modelo,
            LocalDate dataAquisicao,
            String status,
            double valor) {

        setId(id);
        setCodigoPatrimonio(codigoPatrimonio);
        setNome(nome);
        setModelo(modelo);
        setDataAquisicao(dataAquisicao);
        setStatus(status);
        setValor(valor);
    }

    // ==========================================
    // GETTERS
    // ==========================================

    public int getId() {
        return id;
    }

    public String getCodigoPatrimonio() {
        return codigoPatrimonio;
    }

    public String getNome() {
        return nome;
    }

    public String getModelo() {
        return modelo;
    }

    public LocalDate getDataAquisicao() {
        return dataAquisicao;
    }

    public String getStatus() {
        return status;
    }

    public double getValor() {
        return valor;
    }

    // ==========================================
    // SETTERS COM VALIDACAO
    // ==========================================

    public void setId(int id) {

        if (id <= 0) {

            throw new IllegalArgumentException(
                    "O ID deve ser maior que zero.");
        }

        this.id = id;
    }

    public void setCodigoPatrimonio(String codigoPatrimonio) {

        if (codigoPatrimonio == null
                || codigoPatrimonio.isBlank()) {

            throw new IllegalArgumentException(
                    "O codigo de patrimonio e obrigatorio.");
        }

        this.codigoPatrimonio = codigoPatrimonio.trim();
    }

    public void setNome(String nome) {

        if (nome == null || nome.isBlank()) {

            throw new IllegalArgumentException(
                    "O nome do ativo e obrigatorio.");
        }

        this.nome = nome.trim();
    }

    public void setModelo(String modelo) {

        if (modelo == null || modelo.isBlank()) {

            throw new IllegalArgumentException(
                    "O modelo do equipamento e obrigatorio.");
        }

        this.modelo = modelo.trim();
    }

    public void setDataAquisicao(LocalDate dataAquisicao) {

        if (dataAquisicao == null
                || dataAquisicao.isAfter(LocalDate.now())) {

            throw new IllegalArgumentException(
                    "Data de aquisicao invalida.");
        }

        this.dataAquisicao = dataAquisicao;
    }

    // Validacao e padronizacao do status
    public void setStatus(String status) {

        if (status == null || status.isBlank()) {

            throw new IllegalArgumentException(
                    "O status do ativo e obrigatorio.");
        }

        String statusNormalizado =
                status.trim().toUpperCase(Locale.ROOT);

        switch (statusNormalizado) {

            case "ATIVO":
            case "INATIVO":
            case "EM_MANUTENCAO":
            case "DESCARTADO":

                this.status = statusNormalizado;
                break;

            default:

                throw new IllegalArgumentException(
                        "Status invalido. Utilize ATIVO, INATIVO, "
                        + "EM_MANUTENCAO ou DESCARTADO.");
        }
    }

    public void setValor(double valor) {

        if (!Double.isFinite(valor) || valor < 0) {

            throw new IllegalArgumentException(
                    "O valor deve ser finito e nao negativo.");
        }

        this.valor = valor;
    }

    // ==========================================
    // METODO ABSTRATO - POLIMORFISMO
    // ==========================================

    public abstract double calcularCustoManutencaoMensal();

    // ==========================================
    // REPRESENTACAO TEXTUAL
    // ==========================================

    @Override
    public String toString() {

        return "AtivoTI {" +
                "id=" + id +
                ", patrimonio='" + codigoPatrimonio + '\'' +
                ", nome='" + nome + '\'' +
                ", modelo='" + modelo + '\'' +
                ", dataAquisicao=" + dataAquisicao +
                ", status='" + status + '\'' +
                ", valor=" + valor +
                '}';
    }
}