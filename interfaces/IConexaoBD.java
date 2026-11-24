package interfaces;

import java.sql.Connection;
import java.sql.SQLException;

public interface IConexaoBD {

    // Contrato para conexao com o banco de dados
    Connection obterConexao() throws SQLException;

}