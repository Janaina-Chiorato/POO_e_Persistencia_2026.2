
package model;

public class ChamadoSuporte {

    // Atributos
    private int id;
    private String descricao;
    private String status;
    private AtivoTI ativo;

    // Construtor
    public ChamadoSuporte(int id, String descricao,
                          String status, AtivoTI ativo) {
        this.id = id;
        this.descricao = descricao;
        this.status = status;
        this.ativo = ativo;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getStatus() {
        return status;
    }

    public AtivoTI getAtivo() {
        return ativo;
    }

    // Setters
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAtivo(AtivoTI ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return "Chamado #" + id
                + " | Descricao: " + descricao
                + " | Status: " + status
                + " | Ativo: " + ativo.getNome();
    }
}
