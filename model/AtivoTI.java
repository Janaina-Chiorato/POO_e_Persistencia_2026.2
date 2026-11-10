
package model;

public class AtivoTI {

    // Atributos privados
    private int id;
    private String nome;
    private String patrimonio;
    private double valor;

    // Construtor
    public AtivoTI(int id, String nome, String patrimonio, double valor) {
        this.id = id;
        this.nome = nome;
        this.patrimonio = patrimonio;
        this.valor = valor;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getPatrimonio() {
        return patrimonio;
    }

    public double getValor() {
        return valor;
    }

    // Setters
    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setPatrimonio(String patrimonio) {
        this.patrimonio = patrimonio;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return "AtivoTI [ID=" + id
                + ", Nome=" + nome
                + ", Patrimonio=" + patrimonio
                + ", Valor=" + valor + "]";
    }
}
