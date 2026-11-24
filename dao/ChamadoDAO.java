
package dao;

import interfaces.IConexaoBD;
import interfaces.IPersistencia;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import model.AtivoTI;
import model.ChamadoSuporte;

public class ChamadoDAO implements IPersistencia<ChamadoSuporte> {

    private final IConexaoBD conexaoBD;

    // Consulta base com relacionamento entre chamados e ativos

    private static final String SELECT_BASE = """
        SELECT
            c.id AS chamado_id,
            c.titulo AS chamado_titulo,
            c.descricao AS chamado_descricao,
            c.prioridade AS chamado_prioridade,
            c.data_abertura AS chamado_data_abertura,
            c.status AS chamado_status,
            a.*
        FROM chamados c
        INNER JOIN ativos a ON c.ativo_id = a.id
        """;

    // Construtor padrao

    public ChamadoDAO() {

        this(new ConexaoBanco());
    }

    // Construtor com injecao de dependencia

    public ChamadoDAO(IConexaoBD conexaoBD) {

        this.conexaoBD = Objects.requireNonNull(conexaoBD);
    }

    // ==========================================
    // CREATE - CADASTRAR CHAMADO
    // ==========================================

    @Override
    public void inserir(ChamadoSuporte chamado)
            throws SQLException {

        String sql = """
            INSERT INTO chamados (
                titulo,
                descricao,
                prioridade,
                data_abertura,
                status,
                ativo_id,
                id
            ) VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (
            Connection conexao = conexaoBD.obterConexao();
            PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {

            preencherParametros(stmt, chamado);

            stmt.executeUpdate();
        }
    }

    // ==========================================
    // READ - LISTAR TODOS OS CHAMADOS
    // ==========================================

    @Override
    public List<ChamadoSuporte> listar()
            throws SQLException {

        List<ChamadoSuporte> chamados = new ArrayList<>();

        String sql = SELECT_BASE + " ORDER BY c.id";

        try (
            Connection conexao = conexaoBD.obterConexao();
            PreparedStatement stmt = conexao.prepareStatement(sql);
            ResultSet resultado = stmt.executeQuery()
        ) {

            while (resultado.next()) {

                chamados.add(mapearChamado(resultado));
            }
        }

        return chamados;
    }

    // ==========================================
    // READ - BUSCAR CHAMADO POR ID
    // ==========================================

    @Override
    public ChamadoSuporte buscarPorId(int id)
            throws SQLException {

        String sql = SELECT_BASE + " WHERE c.id = ?";

        try (
            Connection conexao = conexaoBD.obterConexao();
            PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {

            stmt.setInt(1, id);

            try (ResultSet resultado = stmt.executeQuery()) {

                if (resultado.next()) {

                    return mapearChamado(resultado);
                }
            }
        }

        return null;
    }

    // ==========================================
    // READ - LISTAR CHAMADOS POR ATIVO
    // ==========================================

    public List<ChamadoSuporte> listarPorAtivo(int ativoId)
            throws SQLException {

        List<ChamadoSuporte> chamados = new ArrayList<>();

        String sql = SELECT_BASE
            + " WHERE c.ativo_id = ? ORDER BY c.id";

        try (
            Connection conexao = conexaoBD.obterConexao();
            PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {

            stmt.setInt(1, ativoId);

            try (ResultSet resultado = stmt.executeQuery()) {

                while (resultado.next()) {

                    chamados.add(mapearChamado(resultado));
                }
            }
        }

        return chamados;
    }

    // ==========================================
    // READ - LISTAR CHAMADOS POR STATUS
    // ==========================================

    public List<ChamadoSuporte> listarPorStatus(String status)
            throws SQLException {

        List<ChamadoSuporte> chamados = new ArrayList<>();

        String sql = SELECT_BASE
            + " WHERE c.status = ? ORDER BY c.id";

        try (
            Connection conexao = conexaoBD.obterConexao();
            PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {

            stmt.setString(1, status);

            try (ResultSet resultado = stmt.executeQuery()) {

                while (resultado.next()) {

                    chamados.add(mapearChamado(resultado));
                }
            }
        }

        return chamados;
    }

    // ==========================================
    // READ - LISTAR CHAMADOS POR PRIORIDADE
    // ==========================================

    public List<ChamadoSuporte> listarPorPrioridade(
            String prioridade) throws SQLException {

        List<ChamadoSuporte> chamados = new ArrayList<>();

        String sql = SELECT_BASE
            + " WHERE c.prioridade = ? ORDER BY c.id";

        try (
            Connection conexao = conexaoBD.obterConexao();
            PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {

            stmt.setString(1, prioridade);

            try (ResultSet resultado = stmt.executeQuery()) {

                while (resultado.next()) {

                    chamados.add(mapearChamado(resultado));
                }
            }
        }

        return chamados;
    }

    // ==========================================
    // UPDATE - ATUALIZAR CHAMADO
    // ==========================================

    @Override
    public boolean atualizar(ChamadoSuporte chamado)
            throws SQLException {

        String sql = """
            UPDATE chamados SET
                titulo = ?,
                descricao = ?,
                prioridade = ?,
                data_abertura = ?,
                status = ?,
                ativo_id = ?
            WHERE id = ?
            """;

        try (
            Connection conexao = conexaoBD.obterConexao();
            PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {

            preencherParametros(stmt, chamado);

            return stmt.executeUpdate() > 0;
        }
    }

    // ==========================================
    // DELETE - EXCLUIR CHAMADO
    // ==========================================

    @Override
    public boolean excluir(int id) throws SQLException {

        String sql = """
            DELETE FROM chamados
            WHERE id = ?
            """;

        try (
            Connection conexao = conexaoBD.obterConexao();
            PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {

            stmt.setInt(1, id);

            return stmt.executeUpdate() > 0;
        }
    }

    // ==========================================
    // PREENCHER PARAMETROS SQL
    // ==========================================

    private void preencherParametros(
            PreparedStatement stmt,
            ChamadoSuporte chamado) throws SQLException {

        stmt.setString(1, chamado.getTitulo());

        stmt.setString(2, chamado.getDescricao());

        stmt.setString(3, chamado.getPrioridade());

        stmt.setString(
            4,
            chamado.getDataAbertura().toString()
        );

        stmt.setString(5, chamado.getStatus());

        stmt.setInt(
            6,
            chamado.getAtivoRelacionado().getId()
        );

        stmt.setInt(7, chamado.getId());
    }

    // ==========================================
    // MAPEAR RESULTSET PARA OBJETO JAVA
    // ==========================================

    private ChamadoSuporte mapearChamado(
            ResultSet resultado) throws SQLException {

        int id = resultado.getInt("chamado_id");

        String titulo =
            resultado.getString("chamado_titulo");

        String descricao =
            resultado.getString("chamado_descricao");

        String prioridade =
            resultado.getString("chamado_prioridade");

        LocalDateTime dataAbertura = LocalDateTime.parse(
            resultado.getString("chamado_data_abertura")
        );

        String status =
            resultado.getString("chamado_status");

        // Reutilizar o mapeamento implementado no AtivoDAO

        AtivoTI ativoRelacionado =
            AtivoDAO.mapearAtivo(resultado);

        return new ChamadoSuporte(
            id,
            titulo,
            descricao,
            prioridade,
            dataAbertura,
            ativoRelacionado,
            status
        );
    }
}