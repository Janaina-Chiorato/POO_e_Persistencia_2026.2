package exceptions;

public class EquipamentoNaoEncontradoException extends Exception {

    // Construtor com mensagem personalizada
    public EquipamentoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }

    // Construtor com identificador do equipamento
    public EquipamentoNaoEncontradoException(int id) {
        super("Equipamento com ID " + id + " nao encontrado.");
    }
}
