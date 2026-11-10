
package dao;

import model.ChamadoSuporte;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import model.AtivoTI;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ChamadoDAO {

    // CREATE - Salvar um chamado de suporte
    public void salvar(ChamadoSuporte chamado) throws SQLException {

        String sql = """
            INSERT INTO chamados
            (id, descricao, status, ativo_id)
            VALUES (?, ?, ?, ?)
            """;

        try (Connection conexao = ConexaoBanco.obterConexao();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1, chamado.getId());
            comando.setString(2, chamado.getDescricao());
            comando.setString(3, chamado.getStatus());
            comando.setInt(4, chamado.getAtivo().getId());

            comando.executeUpdate();

            System.out.println("Chamado cadastrado com sucesso!");
        }
    }

    // READ - Listar chamados de um ativo
    public List<ChamadoSuporte> listarPorAtivo(int ativoId)
            throws SQLException {

        List<ChamadoSuporte> chamados = new ArrayList<>();

        String sql = """
            SELECT c.id, c.descricao, c.status,
                   a.id AS ativo_id, a.nome,
                   a.patrimonio, a.valor
            FROM chamados c
            INNER JOIN ativos a ON c.ativo_id = a.id
            WHERE a.id = ?
            """;

        try (Connection conexao = ConexaoBanco.obterConexao();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1, ativoId);

            try (ResultSet resultado = comando.executeQuery()) {

                while (resultado.next()) {

                    AtivoTI ativo = new AtivoTI(
                        resultado.getInt("ativo_id"),
                        resultado.getString("nome"),
                        resultado.getString("patrimonio"),
                        resultado.getDouble("valor")
                    );

                    ChamadoSuporte chamado = new ChamadoSuporte(
                        resultado.getInt("id"),
                        resultado.getString("descricao"),
                        resultado.getString("status"),
                        ativo
                    );

                    chamados.add(chamado);
                }
            }
        }

        return chamados;
    }

}
