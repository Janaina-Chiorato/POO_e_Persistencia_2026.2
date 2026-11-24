package tests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import java.nio.file.Files;
import java.nio.file.Path;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.List;

import dao.AtivoDAO;
import dao.ChamadoDAO;

import interfaces.IConexaoBD;
import interfaces.IPersistencia;

import model.AtivoTI;
import model.Servidor;
import model.EstacaoTrabalho;
import model.DispositivoRede;
import model.ChamadoSuporte;

public class TesteChamadoDAO {

    public static void main(String[] args) throws Exception {

        System.out.println("====================================");
        System.out.println(" TECHLAB - TESTE CRUD DE CHAMADOS");
        System.out.println("====================================");

        Path bancoTeste = Files.createTempFile(
            "techlab-teste-chamados-", ".db"
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

        // Configurar conexao de teste

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

        IPersistencia<ChamadoSuporte> repositorio =
            chamadoDAO;

        // ==========================================
        // PREPARAR EQUIPAMENTOS
        // ==========================================

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

        EstacaoTrabalho estacao = new EstacaoTrabalho(
            2,
            "PAT-002",
            "Computador Administrativo",
            "Dell OptiPlex",
            LocalDate.of(2025, 3, 15),
            "ATIVO",
            4500.00,
            "Intel Core i5",
            16,
            "Administrativo"
        );

        DispositivoRede dispositivo = new DispositivoRede(
            3,
            "PAT-003",
            "Switch Principal",
            "Cisco Catalyst",
            LocalDate.of(2025, 5, 20),
            "ATIVO",
            2800.00,
            "Switch",
            "192.168.1.1",
            24
        );

        ativoDAO.inserir(servidor);
        ativoDAO.inserir(estacao);
        ativoDAO.inserir(dispositivo);

        // ==========================================
        // PREPARAR CHAMADOS
        // ==========================================

        ChamadoSuporte chamado1 = new ChamadoSuporte(
            1,
            "Falha no servidor",
            "Servidor apresenta instabilidade.",
            "ALTA",
            LocalDateTime.now(),
            servidor,
            "ABERTO"
        );

        ChamadoSuporte chamado2 = new ChamadoSuporte(
            2,
            "Computador nao liga",
            "Verificar fonte de alimentacao.",
            "MEDIA",
            LocalDateTime.now(),
            estacao,
            "EM_ATENDIMENTO"
        );

        ChamadoSuporte chamado3 = new ChamadoSuporte(
            3,
            "Falha na rede",
            "Switch apresenta perda de conexao.",
            "CRITICA",
            LocalDateTime.now(),
            dispositivo,
            "ABERTO"
        );

        ChamadoSuporte chamado4 = new ChamadoSuporte(
            4,
            "Atualizacao do servidor",
            "Atualizar sistema operacional.",
            "BAIXA",
            LocalDateTime.now(),
            servidor,
            "RESOLVIDO"
        );

        // ==========================================
        // CREATE
        // ==========================================

        System.out.println("\n=== TESTES DE CADASTRO ===");

        verificar(
            repositorio.listar().isEmpty(),
            "Banco de chamados inicialmente vazio"
        );

        repositorio.inserir(chamado1);
        repositorio.inserir(chamado2);
        repositorio.inserir(chamado3);
        repositorio.inserir(chamado4);

        verificar(
            repositorio.listar().size() == 4,
            "Cadastro de quatro chamados"
        );

        // ==========================================
        // READ
        // ==========================================

        System.out.println("\n=== TESTES DE CONSULTA ===");

        ChamadoSuporte recuperado =
            repositorio.buscarPorId(1);

        verificar(
            recuperado != null
            && recuperado.getTitulo()
                .equals("Falha no servidor"),
            "Busca de chamado por ID"
        );

        verificar(
            repositorio.buscarPorId(999) == null,
            "Busca de chamado inexistente"
        );

        verificar(
            recuperado.getId() == chamado1.getId()
            && recuperado.getTitulo()
                .equals(chamado1.getTitulo())
            && recuperado.getDescricao()
                .equals(chamado1.getDescricao())
            && recuperado.getPrioridade()
                .equals(chamado1.getPrioridade())
            && recuperado.getDataAbertura()
                .equals(chamado1.getDataAbertura())
            && recuperado.getStatus()
                .equals(chamado1.getStatus()),
            "Preservacao dos atributos do chamado"
        );

        // ==========================================
        // RELACIONAMENTOS E POLIMORFISMO
        // ==========================================

        System.out.println(
            "\n=== TESTES DE RELACIONAMENTO ==="
        );

        verificar(
            recuperado.getAtivoRelacionado().getId() == 1,
            "Relacionamento correto entre chamado e ativo"
        );

        verificar(
            repositorio.buscarPorId(1)
                .getAtivoRelacionado() instanceof Servidor,
            "Reconstrucao do servidor associado"
        );

        verificar(
            repositorio.buscarPorId(2)
                .getAtivoRelacionado()
                instanceof EstacaoTrabalho,
            "Reconstrucao da estacao associada"
        );

        verificar(
            repositorio.buscarPorId(3)
                .getAtivoRelacionado()
                instanceof DispositivoRede,
            "Reconstrucao do dispositivo associado"
        );

        verificar(
            recuperado.getAtivoRelacionado()
                .calcularCustoManutencaoMensal() == 1800.00,
            "Preservacao do comportamento polimorfico"
        );

        // ==========================================
        // FILTROS
        // ==========================================

        System.out.println("\n=== TESTES DE FILTROS ===");

        List<ChamadoSuporte> chamadosServidor =
            chamadoDAO.listarPorAtivo(1);

        verificar(
            chamadosServidor.size() == 2
            && chamadosServidor.stream().allMatch(
                c -> c.getAtivoRelacionado().getId() == 1
            ),
            "Filtro de chamados por equipamento"
        );

        verificar(
            chamadoDAO.listarPorAtivo(999).isEmpty(),
            "Filtro por equipamento inexistente"
        );

        List<ChamadoSuporte> chamadosAbertos =
            chamadoDAO.listarPorStatus("ABERTO");

        verificar(
            chamadosAbertos.size() == 2
            && chamadosAbertos.stream().allMatch(
                c -> c.getStatus().equals("ABERTO")
            ),
            "Filtro de chamados por status"
        );

        List<ChamadoSuporte> chamadosCriticos =
            chamadoDAO.listarPorPrioridade("CRITICA");

        verificar(
            chamadosCriticos.size() == 1
            && chamadosCriticos.get(0).getId() == 3,
            "Filtro de chamados por prioridade"
        );

        verificar(
            chamadoDAO.listarPorStatus("FECHADO").isEmpty(),
            "Filtro sem resultados"
        );

        // ==========================================
        // UPDATE
        // ==========================================

        System.out.println(
            "\n=== TESTES DE ATUALIZACAO ==="
        );

        chamado1.setTitulo("Servidor em manutencao");
        chamado1.setDescricao("Equipe tecnica acionada.");
        chamado1.setPrioridade("CRITICA");
        chamado1.setStatus("EM_ATENDIMENTO");

        verificar(
            repositorio.atualizar(chamado1),
            "Atualizacao de chamado existente"
        );

        ChamadoSuporte atualizado =
            repositorio.buscarPorId(1);

        verificar(
            atualizado.getTitulo()
                .equals("Servidor em manutencao")
            && atualizado.getDescricao()
                .equals("Equipe tecnica acionada.")
            && atualizado.getPrioridade()
                .equals("CRITICA")
            && atualizado.getStatus()
                .equals("EM_ATENDIMENTO"),
            "Persistencia das informacoes atualizadas"
        );

        chamado1.setAtivoRelacionado(estacao);

        verificar(
            repositorio.atualizar(chamado1)
            && repositorio.buscarPorId(1)
                .getAtivoRelacionado().getId() == 2,
            "Atualizacao do equipamento relacionado"
        );

        ChamadoSuporte inexistente = new ChamadoSuporte(
            999,
            "Chamado inexistente",
            "Teste de atualizacao.",
            "BAIXA",
            LocalDateTime.now(),
            servidor,
            "ABERTO"
        );

        verificar(
            !repositorio.atualizar(inexistente),
            "Atualizacao de chamado inexistente"
        );

        // ==========================================
        // ENCERRAMENTO DE CHAMADO
        // ==========================================

        System.out.println(
            "\n=== TESTES DE ENCERRAMENTO ==="
        );

        chamado1.fecharChamado();

        verificar(
            chamado1.getStatus().equals("FECHADO"),
            "Encerramento do chamado no dominio"
        );

        verificar(
            repositorio.atualizar(chamado1)
            && repositorio.buscarPorId(1)
                .getStatus().equals("FECHADO"),
            "Persistencia do encerramento do chamado"
        );

        // ==========================================
        // DUPLICIDADE
        // ==========================================

        System.out.println(
            "\n=== TESTES DE DUPLICIDADE ==="
        );

        try {

            repositorio.inserir(chamado1);

            throw new AssertionError(
                "ID duplicado nao foi bloqueado."
            );

        } catch (SQLException erro) {

            verificar(
                erro.getMessage().contains("UNIQUE"),
                "Bloqueio de chamado com ID duplicado"
            );
        }

        // ==========================================
        // SEGURANCA
        // ==========================================

        System.out.println(
            "\n=== TESTES DE SEGURANCA ==="
        );

        String entradaMaliciosa = "' OR '1'='1";

        verificar(
            chamadoDAO.listarPorStatus(
                entradaMaliciosa
            ).isEmpty(),
            "Protecao contra SQL Injection no status"
        );

        verificar(
            chamadoDAO.listarPorPrioridade(
                entradaMaliciosa
            ).isEmpty(),
            "Protecao contra SQL Injection na prioridade"
        );

        verificar(
            repositorio.listar().size() == 4,
            "Preservacao dos registros apos testes"
        );

        // ==========================================
        // PERSISTENCIA
        // ==========================================

        System.out.println(
            "\n=== TESTE DE PERSISTENCIA ==="
        );

        ChamadoDAO novoDAO =
            new ChamadoDAO(conexaoTeste);

        verificar(
            novoDAO.listar().size() == 4
            && novoDAO.buscarPorId(1)
                .getStatus().equals("FECHADO"),
            "Recuperacao por uma nova instancia do DAO"
        );

        // ==========================================
        // DELETE
        // ==========================================

        System.out.println(
            "\n=== TESTES DE EXCLUSAO ==="
        );

        verificar(
            repositorio.excluir(3),
            "Exclusao de chamado existente"
        );

        verificar(
            repositorio.buscarPorId(3) == null,
            "Confirmacao da exclusao do chamado"
        );

        verificar(
            !repositorio.excluir(999),
            "Exclusao de chamado inexistente"
        );

        verificar(
            repositorio.listar().size() == 3,
            "Quantidade final de chamados"
        );

        verificar(
            ativoDAO.buscarPorId(3) != null
            && ativoDAO.listar().size() == 3,
            "Preservacao dos equipamentos associados"
        );

        // ==========================================
        // RELATORIO FINAL
        // ==========================================

        System.out.println(
            "\n=== CHAMADOS RESTANTES ==="
        );

        for (ChamadoSuporte chamado : repositorio.listar()) {

            System.out.println(chamado);
        }

        System.out.println(
            "\n===================================="
        );

        System.out.println(
            " TODOS OS TESTES FORAM APROVADOS"
        );

        System.out.println(
            "===================================="
        );
    }

    private static void verificar(
            boolean condicao, String descricao) {

        if (!condicao) {

            throw new AssertionError(
                "[FALHA] " + descricao
            );
        }

        System.out.println("[OK] " + descricao);
    }
}