
package dao;

import interfaces.IConexaoBD;
import interfaces.IPersistencia;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import model.AtivoTI;
import model.DispositivoRede;
import model.EstacaoTrabalho;
import model.Servidor;

public class AtivoDAO implements IPersistencia<AtivoTI> {

    private final IConexaoBD conexaoBD;

    // Construtor padrao
    public AtivoDAO() {

        this(new ConexaoBanco());
    }

    // Construtor com injecao de dependencia
    public AtivoDAO(IConexaoBD conexaoBD) {

        this.conexaoBD = Objects.requireNonNull(conexaoBD);
    }

    // ==========================================
    // CREATE - INSERIR ATIVO
    // ==========================================

    @Override
    public void inserir(AtivoTI ativo) throws SQLException {

        String sql = """
            INSERT INTO ativos (
                codigo_patrimonio,
                nome,
                modelo,
                data_aquisicao,
                status,
                valor,
                tipo,
                ip_estatico,
                sistema_operacional,
                memoria_ram,
                processador,
                departamento,
                tipo_dispositivo,
                endereco_ip,
                quantidade_portas,
                id
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (
            Connection conexao = conexaoBD.obterConexao();
            PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {

            preencherParametros(stmt, ativo);

            stmt.executeUpdate();
        }
    }

    // ==========================================
    // READ - LISTAR TODOS OS ATIVOS
    // ==========================================

    @Override
    public List<AtivoTI> listar() throws SQLException {

        List<AtivoTI> ativos = new ArrayList<>();

        String sql = """
            SELECT *
            FROM ativos
            ORDER BY id
            """;

        try (
            Connection conexao = conexaoBD.obterConexao();
            PreparedStatement stmt = conexao.prepareStatement(sql);
            ResultSet resultado = stmt.executeQuery()
        ) {

            while (resultado.next()) {

                ativos.add(mapearAtivo(resultado));
            }
        }

        return ativos;
    }

    // ==========================================
    // READ - BUSCAR ATIVO POR ID
    // ==========================================

    @Override
    public AtivoTI buscarPorId(int id) throws SQLException {

        String sql = """
            SELECT *
            FROM ativos
            WHERE id = ?
            """;

        try (
            Connection conexao = conexaoBD.obterConexao();
            PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {

            stmt.setInt(1, id);

            try (ResultSet resultado = stmt.executeQuery()) {

                if (resultado.next()) {

                    return mapearAtivo(resultado);
                }
            }
        }

        return null;
    }

    // ==========================================
    // READ - BUSCAR POR PATRIMONIO
    // ==========================================

    public AtivoTI buscarPorPatrimonio(String patrimonio)
            throws SQLException {

        String sql = """
            SELECT *
            FROM ativos
            WHERE codigo_patrimonio = ?
            """;

        try (
            Connection conexao = conexaoBD.obterConexao();
            PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {

            stmt.setString(1, patrimonio);

            try (ResultSet resultado = stmt.executeQuery()) {

                if (resultado.next()) {

                    return mapearAtivo(resultado);
                }
            }
        }

        return null;
    }

    // ==========================================
    // UPDATE - ATUALIZAR ATIVO
    // ==========================================

    @Override
    public boolean atualizar(AtivoTI ativo)
            throws SQLException {

        String sql = """
            UPDATE ativos SET
                codigo_patrimonio = ?,
                nome = ?,
                modelo = ?,
                data_aquisicao = ?,
                status = ?,
                valor = ?,
                tipo = ?,
                ip_estatico = ?,
                sistema_operacional = ?,
                memoria_ram = ?,
                processador = ?,
                departamento = ?,
                tipo_dispositivo = ?,
                endereco_ip = ?,
                quantidade_portas = ?
            WHERE id = ?
            """;

        try (
            Connection conexao = conexaoBD.obterConexao();
            PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {

            preencherParametros(stmt, ativo);

            return stmt.executeUpdate() > 0;
        }
    }

    // ==========================================
    // DELETE - EXCLUIR ATIVO
    // ==========================================

    @Override
    public boolean excluir(int id) throws SQLException {

        String sqlVerificacao = """
            SELECT COUNT(*) AS total
            FROM chamados
            WHERE ativo_id = ?
            """;

        String sqlExclusao = """
            DELETE FROM ativos
            WHERE id = ?
            """;

        try (
            Connection conexao = conexaoBD.obterConexao()
        ) {

            // Verificar chamados vinculados

            try (
                PreparedStatement stmt =
                    conexao.prepareStatement(sqlVerificacao)
            ) {

                stmt.setInt(1, id);

                try (
                    ResultSet resultado = stmt.executeQuery()
                ) {

                    if (resultado.next()
                            && resultado.getInt("total") > 0) {

                        throw new SQLException(
                            "Operacao negada: o ativo possui "
                            + "chamados de suporte vinculados."
                        );
                    }
                }
            }

            // Excluir somente se nao houver impedimento

            try (
                PreparedStatement stmt =
                    conexao.prepareStatement(sqlExclusao)
            ) {

                stmt.setInt(1, id);

                return stmt.executeUpdate() > 0;
            }
        }
    }

    // ==========================================
    // PREENCHER PARAMETROS SQL
    // ==========================================

    private void preencherParametros(
            PreparedStatement stmt,
            AtivoTI ativo) throws SQLException {

        // Atributos gerais

        stmt.setString(1, ativo.getCodigoPatrimonio());
        stmt.setString(2, ativo.getNome());
        stmt.setString(3, ativo.getModelo());

        stmt.setString(
            4,
            ativo.getDataAquisicao().toString()
        );

        stmt.setString(5, ativo.getStatus());
        stmt.setDouble(6, ativo.getValor());

        // Inicializar atributos especificos como NULL

        stmt.setNull(8, Types.VARCHAR);
        stmt.setNull(9, Types.VARCHAR);
        stmt.setNull(10, Types.INTEGER);
        stmt.setNull(11, Types.VARCHAR);
        stmt.setNull(12, Types.VARCHAR);
        stmt.setNull(13, Types.VARCHAR);
        stmt.setNull(14, Types.VARCHAR);
        stmt.setNull(15, Types.INTEGER);

        // Identificar a subclasse

        if (ativo instanceof Servidor) {

            Servidor servidor = (Servidor) ativo;

            stmt.setString(7, "SERVIDOR");

            stmt.setString(
                8,
                servidor.getIpEstatico()
            );

            stmt.setString(
                9,
                servidor.getSistemaOperacional()
            );

            stmt.setInt(
                10,
                servidor.getMemoriaRam()
            );

        } else if (ativo instanceof EstacaoTrabalho) {

            EstacaoTrabalho estacao =
                (EstacaoTrabalho) ativo;

            stmt.setString(7, "ESTACAO_TRABALHO");

            stmt.setInt(
                10,
                estacao.getMemoriaRam()
            );

            stmt.setString(
                11,
                estacao.getProcessador()
            );

            stmt.setString(
                12,
                estacao.getDepartamento()
            );

        } else if (ativo instanceof DispositivoRede) {

            DispositivoRede dispositivo =
                (DispositivoRede) ativo;

            stmt.setString(7, "DISPOSITIVO_REDE");

            stmt.setString(
                13,
                dispositivo.getTipoDispositivo()
            );

            stmt.setString(
                14,
                dispositivo.getEnderecoIp()
            );

            stmt.setInt(
                15,
                dispositivo.getQuantidadePortas()
            );

        } else {

            throw new SQLException(
                "Tipo de ativo nao suportado."
            );
        }

        stmt.setInt(16, ativo.getId());
    }

    // ==========================================
    // MAPEAR RESULTSET PARA OBJETO JAVA
    // ==========================================

    static AtivoTI mapearAtivo(ResultSet resultado)
            throws SQLException {

        int id = resultado.getInt("id");

        String patrimonio =
            resultado.getString("codigo_patrimonio");

        String nome =
            resultado.getString("nome");

        String modelo =
            resultado.getString("modelo");

        LocalDate dataAquisicao = LocalDate.parse(
            resultado.getString("data_aquisicao")
        );

        String status =
            resultado.getString("status");

        double valor =
            resultado.getDouble("valor");

        String tipo =
            resultado.getString("tipo");

        switch (tipo) {

            case "SERVIDOR":

                return new Servidor(
                    id,
                    patrimonio,
                    nome,
                    modelo,
                    dataAquisicao,
                    status,
                    valor,
                    resultado.getString("ip_estatico"),
                    resultado.getString("sistema_operacional"),
                    resultado.getInt("memoria_ram")
                );

            case "ESTACAO_TRABALHO":

                return new EstacaoTrabalho(
                    id,
                    patrimonio,
                    nome,
                    modelo,
                    dataAquisicao,
                    status,
                    valor,
                    resultado.getString("processador"),
                    resultado.getInt("memoria_ram"),
                    resultado.getString("departamento")
                );

            case "DISPOSITIVO_REDE":

                return new DispositivoRede(
                    id,
                    patrimonio,
                    nome,
                    modelo,
                    dataAquisicao,
                    status,
                    valor,
                    resultado.getString("tipo_dispositivo"),
                    resultado.getString("endereco_ip"),
                    resultado.getInt("quantidade_portas")
                );

            default:

                throw new SQLException(
                    "Tipo de ativo desconhecido: " + tipo
                );
        }
    }
}