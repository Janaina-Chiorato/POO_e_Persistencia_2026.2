
package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexaoBanco {

    private static final String URL =
            "jdbc:sqlite:techlab_aula09.db";

    public static Connection obterConexao() throws SQLException {

        Connection conexao = DriverManager.getConnection(URL);

        try (Statement comando = conexao.createStatement()) {

            comando.execute("PRAGMA foreign_keys = ON");

        } catch (SQLException erro) {

            conexao.close();
            throw erro;
        }

        return conexao;
    }
}
