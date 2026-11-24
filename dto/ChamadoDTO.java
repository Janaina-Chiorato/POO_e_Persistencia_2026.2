package dto;

import java.time.LocalDateTime;
import model.ChamadoSuporte;
import model.AtivoTI;

public class ChamadoDTO {

    private final int id;
    private final String titulo;
    private final String descricao;
    private final String prioridade;
    private final LocalDateTime dataAbertura;
    private final String status;

    // Dados do ativo relacionado
    private final int ativoId;
    private final String ativoNome;
    private final String ativoPatrimonio;
    private final String ativoTipo;

    public ChamadoDTO(ChamadoSuporte chamado) {

        this.id = chamado.getId();
        this.titulo = chamado.getTitulo();
        this.descricao = chamado.getDescricao();
        this.prioridade = chamado.getPrioridade();
        this.dataAbertura = chamado.getDataAbertura();
        this.status = chamado.getStatus();

        AtivoTI ativo = chamado.getAtivoRelacionado();

        this.ativoId = ativo.getId();
        this.ativoNome = ativo.getNome();
        this.ativoPatrimonio = ativo.getCodigoPatrimonio();
        this.ativoTipo = ativo.getClass().getSimpleName();
    }

    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getPrioridade() {
        return prioridade;
    }

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }

    public String getStatus() {
        return status;
    }

    public int getAtivoId() {
        return ativoId;
    }

    public String getAtivoNome() {
        return ativoNome;
    }

    public String getAtivoPatrimonio() {
        return ativoPatrimonio;
    }

    public String getAtivoTipo() {
        return ativoTipo;
    }

    @Override
    public String toString() {

        return String.format(
            "Chamado #%d%n"
            + "Titulo: %s%n"
            + "Descricao: %s%n"
            + "Prioridade: %s%n"
            + "Data de abertura: %s%n"
            + "Status: %s%n"
            + "Ativo ID: %d%n"
            + "Equipamento: %s%n"
            + "Patrimonio: %s%n"
            + "Tipo de equipamento: %s",
            id,
            titulo,
            descricao,
            prioridade,
            dataAbertura,
            status,
            ativoId,
            ativoNome,
            ativoPatrimonio,
            ativoTipo
        );
    }
}