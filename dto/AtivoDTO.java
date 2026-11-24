package dto;

import java.time.LocalDate;
import model.AtivoTI;

public class AtivoDTO {

    private final int id;
    private final String codigoPatrimonio;
    private final String nome;
    private final String modelo;
    private final String tipo;
    private final LocalDate dataAquisicao;
    private final String status;
    private final double valor;
    private final double custoManutencaoMensal;

    public AtivoDTO(AtivoTI ativo) {

        this.id = ativo.getId();
        this.codigoPatrimonio = ativo.getCodigoPatrimonio();
        this.nome = ativo.getNome();
        this.modelo = ativo.getModelo();
        this.tipo = ativo.getClass().getSimpleName();
        this.dataAquisicao = ativo.getDataAquisicao();
        this.status = ativo.getStatus();
        this.valor = ativo.getValor();
        this.custoManutencaoMensal =
                ativo.calcularCustoManutencaoMensal();
    }

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

    public String getTipo() {
        return tipo;
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

    public double getCustoManutencaoMensal() {
        return custoManutencaoMensal;
    }

    @Override
    public String toString() {

        return String.format(
            "ID: %d | Patrimonio: %s | Nome: %s"
            + " | Modelo: %s | Tipo: %s"
            + " | Aquisicao: %s | Status: %s"
            + " | Valor: R$ %.2f"
            + " | Manutencao mensal: R$ %.2f",
            id,
            codigoPatrimonio,
            nome,
            modelo,
            tipo,
            dataAquisicao,
            status,
            valor,
            custoManutencaoMensal
        );
    }
}