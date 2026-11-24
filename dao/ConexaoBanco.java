
package dao;

import exceptions.FalhaConexaoBDException;
import interfaces.IConexaoBD;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexaoBanco implements IConexaoBD {

    private static final String URL =
            "jdbc:sqlite:techlab_final.db";

    @Override
    public Connection obterConexao()
            throws FalhaConexaoBDException {

        Connection conexao = null;

        try {

            conexao = DriverManager.getConnection(URL);

            // Ativa a integridade referencial
            try (Statement stmt = conexao.createStatement()) {

                stmt.execute("PRAGMA foreign_keys = ON");
            }

            return conexao;

        } catch (SQLException erro) {

            // Fecha a conexao em caso de falha
            if (conexao != null) {

                try {
                    conexao.close();

                } catch (SQLException erroFechamento) {

                    erro.addSuppressed(erroFechamento);
                }
            }

            throw new FalhaConexaoBDException(
                    "Falha ao conectar ao banco de dados TechLab.",
                    erro
            );
        }
    }
}