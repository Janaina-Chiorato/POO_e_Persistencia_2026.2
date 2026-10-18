

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexaoBanco {

    // Endereço do banco de dados
    private static final String URL = "jdbc:sqlite:techlab.db";

    // Método responsável por estabelecer a conexão
    public static Connection obterConexao() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    // Método responsável por criar as tabelas
    public static void inicializarBanco() {

        String sqlAtivos = """
            CREATE TABLE IF NOT EXISTS ativos (
                id INTEGER PRIMARY KEY,
                patrimonio TEXT NOT NULL UNIQUE,
                modelo TEXT NOT NULL,
                status TEXT NOT NULL,
                categoria TEXT NOT NULL,
                ip_estatico TEXT,
                sistema_operacional TEXT,
                memoria_ram INTEGER
            );
            """;

        String sqlChamados = """
            CREATE TABLE IF NOT EXISTS chamados (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                descricao TEXT NOT NULL,
                ativo_id INTEGER NOT NULL,
                FOREIGN KEY (ativo_id) REFERENCES ativos(id)
            );
            """;

        try (Connection conexao = obterConexao();
             Statement comando = conexao.createStatement()) {

            comando.execute("PRAGMA foreign_keys = ON");
            comando.execute(sqlAtivos);
            comando.execute(sqlChamados);

            System.out.println("Banco de dados inicializado com sucesso!");

        } catch (SQLException erro) {

            System.out.println("Erro ao inicializar banco: "
                    + erro.getMessage());

            throw new RuntimeException(erro);
        }
    }
}
