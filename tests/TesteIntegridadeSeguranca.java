package tests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.nio.file.Files;
import java.nio.file.Path;

import java.time.LocalDate;
import java.time.LocalDateTime;

import dao.AtivoDAO;
import dao.ChamadoDAO;

import interfaces.IConexaoBD;

import model.Servidor;
import model.ChamadoSuporte;

public class TesteIntegridadeSeguranca {

    private static int testesAprovados = 0;

    public static void main(String[] args) throws Exception {

        System.out.println("========================================");
        System.out.println(" TECHLAB - INTEGRIDADE E SEGURANCA");
        System.out.println("========================================");

        Path bancoTeste = Files.createTempFile(
            "techlab-teste-integridade-", ".db"
        );

        try {

            executarTestes(bancoTeste);

        } finally {

            Files.deleteIfExists(bancoTeste);
        }
    }

    private static void executarTestes(Path bancoTeste)
            throws Exception {

        String url = "jdbc:sqlite:"
            + bancoTeste.toAbsolutePath();

        // Inicializar banco temporario

        Process processo = new ProcessBuilder(
            "sqlite3",
            "-bail",
            bancoTeste.toAbsolutePath().toString()
        )
        .redirectInput(Path.of("banco.sql").toFile())
        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
        .redirectError(ProcessBuilder.Redirect.INHERIT)
        .start();

        if (processo.waitFor() != 0) {

            throw new IllegalStateException(
                "Falha ao inicializar o banco de teste."
            );
        }

        // Configurar conexao

        IConexaoBD conexaoTeste = () -> {

            Connection conexao =
                DriverManager.getConnection(url);

            try (Statement stmt = conexao.createStatement()) {

                stmt.execute("PRAGMA foreign_keys = ON");

            } catch (SQLException erro) {

                conexao.close();

                throw erro;
            }

            return conexao;
        };

        AtivoDAO ativoDAO = new AtivoDAO(conexaoTeste);

        ChamadoDAO chamadoDAO =
            new ChamadoDAO(conexaoTeste);

        // Preparar equipamentos

        Servidor servidor = new Servidor(
            1,
            "PAT-001",
            "Servidor Principal",
            "Dell PowerEdge",
            LocalDate.of(2025, 1, 10),
            "ATIVO",
            15000.00,
            "192.168.1.10",
            "Linux",
            32
        );

        Servidor servidorInexistente = new Servidor(
            999,
            "PAT-999",
            "Servidor Inexistente",
            "Dell PowerEdge",
            LocalDate.of(2025, 1, 10),
            "ATIVO",
            5000.00,
            "192.168.1.99",
            "Linux",
            16
        );

        ativoDAO.inserir(servidor);

        ChamadoSuporte chamado = new ChamadoSuporte(
            1,
            "Falha no servidor",
            "Servidor apresenta instabilidade.",
            "ALTA",
            LocalDateTime.now(),
            servidor,
            "ABERTO"
        );

        chamadoDAO.inserir(chamado);

        // ==========================================
        // INTEGRIDADE REFERENCIAL
        // ==========================================

        System.out.println(
            "\n=== TESTES DE INTEGRIDADE REFERENCIAL ==="
        );

        ChamadoSuporte chamadoInvalido =
            new ChamadoSuporte(
                2,
                "Chamado invalido",
                "Equipamento nao cadastrado.",
                "ALTA",
                LocalDateTime.now(),
                servidorInexistente,
                "ABERTO"
            );

        esperarErroSQL(
            () -> chamadoDAO.inserir(chamadoInvalido),
            "FOREIGN KEY",
            "Bloqueio de chamado com ativo inexistente"
        );

        chamado.setAtivoRelacionado(servidorInexistente);

        esperarErroSQL(
            () -> chamadoDAO.atualizar(chamado),
            "FOREIGN KEY",
            "Bloqueio de atualizacao para ativo inexistente"
        );

        chamado.setAtivoRelacionado(servidor);

        esperarErroSQL(
            () -> ativoDAO.excluir(1),
            "vinculados",
            "Bloqueio de exclusao de ativo pelo DAO"
        );

        esperarErroSQL(
            () -> executarSQL(
                conexaoTeste,
                "DELETE FROM ativos WHERE id = 1"
            ),
            "FOREIGN KEY",
            "Bloqueio de exclusao direta pelo banco"
        );

        esperarErroSQL(
            () -> executarSQL(
                conexaoTeste,
                "UPDATE ativos SET id = 10 WHERE id = 1"
            ),
            "FOREIGN KEY",
            "Bloqueio de alteracao de ID referenciado"
        );

        // ==========================================
        // RESTRICOES DO BANCO DE DADOS
        // ==========================================

        System.out.println(
            "\n=== TESTES DE RESTRICOES DO BANCO ==="
        );

        esperarErroSQL(
            () -> executarSQL(
                conexaoTeste,
                """
                INSERT INTO chamados (
                    id, titulo, descricao, prioridade,
                    data_abertura, status, ativo_id
                ) VALUES (
                    2, 'Teste', 'Prioridade invalida',
                    'INVALIDA', '2025-01-10T10:00:00',
                    'ABERTO', 1
                )
                """
            ),
            "CHECK",
            "Bloqueio de prioridade invalida"
        );

        esperarErroSQL(
            () -> executarSQL(
                conexaoTeste,
                """
                INSERT INTO chamados (
                    id, titulo, descricao, prioridade,
                    data_abertura, status, ativo_id
                ) VALUES (
                    2, 'Teste', 'Status invalido',
                    'ALTA', '2025-01-10T10:00:00',
                    'INVALIDO', 1
                )
                """
            ),
            "CHECK",
            "Bloqueio de status invalido"
        );

        esperarErroSQL(
            () -> executarSQL(
                conexaoTeste,
                """
                INSERT INTO ativos (
                    id, codigo_patrimonio, nome, modelo,
                    data_aquisicao, status, valor, tipo,
                    ip_estatico, sistema_operacional,
                    memoria_ram
                ) VALUES (
                    2, 'PAT-002', 'Servidor Teste', 'Dell',
                    '2025-01-10', 'ATIVO', -500,
                    'SERVIDOR', '192.168.1.20', 'Linux', 16
                )
                """
            ),
            "CHECK",
            "Bloqueio de cadastro com valor negativo"
        );

        esperarErroSQL(
            () -> executarSQL(
                conexaoTeste,
                """
                INSERT INTO ativos (
                    id, codigo_patrimonio, nome, modelo,
                    data_aquisicao, status, valor, tipo
                ) VALUES (
                    2, 'PAT-002', 'Ativo Teste', 'Dell',
                    '2025-01-10', 'ATIVO', 5000,
                    'TIPO_INVALIDO'
                )
                """
            ),
            "CHECK",
            "Bloqueio de cadastro com tipo invalido"
        );

        esperarErroSQL(
            () -> executarSQL(
                conexaoTeste,
                """
                INSERT INTO ativos (
                    id, codigo_patrimonio, nome, modelo,
                    data_aquisicao, status, valor, tipo,
                    ip_estatico, sistema_operacional,
                    memoria_ram, quantidade_portas
                ) VALUES (
                    2, 'PAT-002', 'Servidor Teste', 'Dell',
                    '2025-01-10', 'ATIVO', 5000,
                    'SERVIDOR', '192.168.1.20', 'Linux',
                    16, 24
                )
                """
            ),
            "CHECK",
            "Bloqueio de atributos incompativeis"
        );

        // ==========================================
        // SEGURANCA E CARACTERES ESPECIAIS
        // ==========================================

        System.out.println(
            "\n=== TESTES DE SEGURANCA ==="
        );

        String descricaoEspecial =
            "Falha no servidor; mensagem \"Erro 500\". "
            + "Equipamento do setor D'Avila.\n"
            + "Necessaria avaliacao tecnica.";

        ChamadoSuporte chamadoEspecial =
            new ChamadoSuporte(
                2,
                "Falha de conexao",
                descricaoEspecial,
                "MEDIA",
                LocalDateTime.now(),
                servidor,
                "ABERTO"
            );

        chamadoDAO.inserir(chamadoEspecial);

        verificar(
            chamadoDAO.buscarPorId(2)
                .getDescricao().equals(descricaoEspecial),
            "Persistencia segura de caracteres especiais"
        );

        // ==========================================
        // EXCLUSAO CONTROLADA
        // ==========================================

        System.out.println(
            "\n=== TESTES DE EXCLUSAO CONTROLADA ==="
        );

        verificar(
            chamadoDAO.excluir(1)
            && chamadoDAO.excluir(2)
            && ativoDAO.excluir(1)
            && ativoDAO.buscarPorId(1) == null
            && chamadoDAO.listar().isEmpty()
            && ativoDAO.listar().isEmpty(),
            "Exclusao permitida apos remover os chamados"
        );

        // ==========================================
        // AUDITORIA FINAL DO BANCO
        // ==========================================

        System.out.println(
            "\n=== AUDITORIA FINAL DO BANCO ==="
        );

        try (
            Connection conexao = conexaoTeste.obterConexao();
            Statement stmt = conexao.createStatement()
        ) {

            try (
                ResultSet resultado = stmt.executeQuery(
                    "PRAGMA foreign_key_check"
                )
            ) {

                verificar(
                    !resultado.next(),
                    "Nenhuma violacao de chave estrangeira"
                );
            }

            try (
                ResultSet resultado = stmt.executeQuery(
                    "PRAGMA integrity_check"
                )
            ) {

                verificar(
                    resultado.next()
                    && resultado.getString(1).equals("ok"),
                    "Integridade estrutural do banco"
                );
            }
        }

        System.out.println(
            "\n========================================"
        );

        System.out.println(
            " TESTES APROVADOS: " + testesAprovados + "/14"
        );

        System.out.println(
            " TODOS OS TESTES FORAM APROVADOS"
        );

        System.out.println(
            "========================================"
        );
    }

    // Executar comandos SQL diretamente no banco

    private static void executarSQL(
            IConexaoBD conexaoBD,
            String sql) throws SQLException {

        try (
            Connection conexao = conexaoBD.obterConexao();
            PreparedStatement stmt =
                conexao.prepareStatement(sql)
        ) {

            stmt.executeUpdate();
        }
    }

    // Contrato para operacoes que podem gerar SQLException

    @FunctionalInterface
    private interface OperacaoSQL {

        void executar() throws SQLException;
    }

    // Verificar se uma operacao invalida foi bloqueada

    private static void esperarErroSQL(
            OperacaoSQL operacao,
            String mensagemEsperada,
            String descricao) {

        try {

            operacao.executar();

            throw new AssertionError(
                "[FALHA] Operacao deveria ser bloqueada: "
                + descricao
            );

        } catch (SQLException erro) {

            verificar(
                erro.getMessage() != null
                && erro.getMessage().contains(mensagemEsperada),
                descricao
            );
        }
    }

    private static void verificar(
            boolean condicao,
            String descricao) {

        if (!condicao) {

            throw new AssertionError(
                "[FALHA] " + descricao
            );
        }

        testesAprovados++;

        System.out.println("[OK] " + descricao);
    }
}