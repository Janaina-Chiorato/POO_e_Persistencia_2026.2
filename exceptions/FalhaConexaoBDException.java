package exceptions;

import java.sql.SQLException;

public class FalhaConexaoBDException extends SQLException {

    // Construtor com mensagem personalizada
    public FalhaConexaoBDException(String mensagem) {
        super(mensagem);
    }

    // Construtor que preserva a causa original do erro
    public FalhaConexaoBDException(
            String mensagem, Throwable causa) {

        super(mensagem, causa);
    }
}