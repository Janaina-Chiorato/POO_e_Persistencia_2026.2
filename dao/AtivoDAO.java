
package dao;

import model.AtivoTI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AtivoDAO {

    private static final String URL = "jdbc:sqlite:techlab_aula09.db";

    // CREATE - Cadastrar um ativo
    public void inserir(AtivoTI ativo) throws SQLException {

        String sql = """
            INSERT INTO ativos (id, nome, patrimonio, valor)
            VALUES (?, ?, ?, ?)
            """;

        try (Connection conexao = ConexaoBanco.obterConexao();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1, ativo.getId());
            comando.setString(2, ativo.getNome());
            comando.setString(3, ativo.getPatrimonio());
            comando.setDouble(4, ativo.getValor());

            comando.executeUpdate();

            System.out.println("Ativo cadastrado com sucesso!");
        }

    }

    // READ - Consultar todos os ativos cadastrados
    public List<AtivoTI> listar() throws SQLException {

        List<AtivoTI> ativos = new ArrayList<>();

        String sql = "SELECT id, nome, patrimonio, valor FROM ativos";

        try (Connection conexao = ConexaoBanco.obterConexao();
             PreparedStatement comando = conexao.prepareStatement(sql);
             ResultSet resultado = comando.executeQuery()) {

            while (resultado.next()) {

                int id = resultado.getInt("id");
                String nome = resultado.getString("nome");
                String patrimonio = resultado.getString("patrimonio");
                double valor = resultado.getDouble("valor");

                AtivoTI ativo = new AtivoTI(
                    id, nome, patrimonio, valor
                );

                ativos.add(ativo);
            }
        }

        return ativos;
    }

    // UPDATE - Atualizar um ativo cadastrado
    public boolean atualizar(AtivoTI ativo) throws SQLException {

        String sql = """
            UPDATE ativos
            SET nome = ?, patrimonio = ?, valor = ?
            WHERE id = ?
            """;

        try (Connection conexao = ConexaoBanco.obterConexao();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setString(1, ativo.getNome());
            comando.setString(2, ativo.getPatrimonio());
            comando.setDouble(3, ativo.getValor());
            comando.setInt(4, ativo.getId());

            int linhasAfetadas = comando.executeUpdate();

            return linhasAfetadas > 0;
        }
    }

    // DELETE - Excluir um ativo pelo ID
   
    // DELETE - Excluir um ativo pelo ID
    // EXERCICIO 3 - Verificacao previa de integridade
    public boolean excluir(int id) throws SQLException {

        String sqlVerificacao = """
            SELECT COUNT(*) AS total
            FROM chamados
            WHERE ativo_id = ?
            """;

        String sqlExclusao = "DELETE FROM ativos WHERE id = ?";

        try (Connection conexao = ConexaoBanco.obterConexao()) {

            // 1. Verificar se existem chamados vinculados
            try (PreparedStatement verificacao =
                    conexao.prepareStatement(sqlVerificacao)) {

                verificacao.setInt(1, id);

                try (ResultSet resultado = verificacao.executeQuery()) {

                    if (resultado.next()) {

                        int total = resultado.getInt("total");

                        if (total > 0) {
                            throw new SQLException(
                                "Operação Negada: O ativo selecionado possui chamados vinculados e não pode ser removido sem antes tratar os chamados associados."
                            );
                        }
                    }
                }
            }

            // 2. Excluir somente se nao houver chamados
            try (PreparedStatement comando =
                    conexao.prepareStatement(sqlExclusao)) {

                comando.setInt(1, id);

                int linhasAfetadas = comando.executeUpdate();

                return linhasAfetadas > 0;
            }
        }
    }



}
