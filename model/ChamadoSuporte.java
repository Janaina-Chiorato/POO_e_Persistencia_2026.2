
package model;

import interfaces.INotificavel;
import java.time.LocalDateTime;

public class ChamadoSuporte implements INotificavel {

    // Atributos privados
    private int id;
    private String titulo;
    private String descricao;
    private String prioridade;
    private LocalDateTime dataAbertura;
    private AtivoTI ativoRelacionado;
    private String status;

    // Construtor
    public ChamadoSuporte(
            int id,
            String titulo,
            String descricao,
            String prioridade,
            LocalDateTime dataAbertura,
            AtivoTI ativoRelacionado,
            String status) {

        setId(id);
        setTitulo(titulo);
        setDescricao(descricao);
        setPrioridade(prioridade);
        setDataAbertura(dataAbertura);
        setAtivoRelacionado(ativoRelacionado);
        setStatus(status);
    }

    // Getters
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

    public AtivoTI getAtivoRelacionado() {
        return ativoRelacionado;
    }

    public String getStatus() {
        return status;
    }

    // Setters com validacao
    public void setId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException(
                    "O ID deve ser maior que zero.");
        }

        this.id = id;
    }

    public void setTitulo(String titulo) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException(
                    "O titulo do chamado e obrigatorio.");
        }

        this.titulo = titulo.trim();
    }

    public void setDescricao(String descricao) {
        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException(
                    "A descricao do chamado e obrigatoria.");
        }

        this.descricao = descricao.trim();
    }

    public void setPrioridade(String prioridade) {
        if (prioridade == null || prioridade.isBlank()) {
            throw new IllegalArgumentException(
                    "A prioridade e obrigatoria.");
        }

        String valor = prioridade.trim()
                .toUpperCase(java.util.Locale.ROOT);

        if (!valor.equals("BAIXA")
                && !valor.equals("MEDIA")
                && !valor.equals("ALTA")
                && !valor.equals("CRITICA")) {

            throw new IllegalArgumentException(
                    "Prioridade invalida.");
        }

        this.prioridade = valor;
    }

    public void setDataAbertura(LocalDateTime dataAbertura) {
        if (dataAbertura == null
                || dataAbertura.isAfter(LocalDateTime.now())) {

            throw new IllegalArgumentException(
                    "Data de abertura invalida.");
        }

        this.dataAbertura = dataAbertura;
    }

    public void setAtivoRelacionado(AtivoTI ativoRelacionado) {
        if (ativoRelacionado == null) {
            throw new IllegalArgumentException(
                    "O chamado deve possuir um ativo relacionado.");
        }

        this.ativoRelacionado = ativoRelacionado;
    }

    public void setStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException(
                    "O status do chamado e obrigatorio.");
        }

        String valor = status.trim()
                .toUpperCase(java.util.Locale.ROOT);

        if (!valor.equals("ABERTO")
                && !valor.equals("EM_ATENDIMENTO")
                && !valor.equals("RESOLVIDO")
                && !valor.equals("FECHADO")) {

            throw new IllegalArgumentException(
                    "Status do chamado invalido.");
        }

        this.status = valor;
    }

    // Regra de negocio
    public void fecharChamado() {
        setStatus("FECHADO");

        enviarAlerta(
                "Chamado #" + id + " fechado com sucesso."
        );
    }

    // Implementacao da interface INotificavel
    @Override
    public void enviarAlerta(String mensagem) {
        System.out.println(
                "[SISTEMA DE ALERTA]: " + mensagem
        );
    }

    // Representacao textual
    @Override
    public String toString() {
        return "\n------------------------------------"
                + "\nChamado #" + id
                + "\nTitulo: " + titulo
                + "\nDescricao: " + descricao
                + "\nPrioridade: " + prioridade
                + "\nData de abertura: " + dataAbertura
                + "\nStatus: " + status
                + "\nAtivo: " + ativoRelacionado.getNome()
                + "\nPatrimonio: "
                + ativoRelacionado.getCodigoPatrimonio()
                + "\n------------------------------------";
    }
}