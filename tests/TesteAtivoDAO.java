package tests;

import dao.AtivoDAO;
import interfaces.IConexaoBD;
import interfaces.IPersistencia;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import model.AtivoTI;
import model.DispositivoRede;
import model.EstacaoTrabalho;
import model.Servidor;

public class TesteAtivoDAO {

    public static void main(String[] args) throws Exception {

        System.out.println("====================================");
        System.out.println(" TECHLAB - TESTE CRUD DE ATIVOS");
        System.out.println("====================================");

        // Banco temporario exclusivo para os testes

        Path bancoTeste = Files.createTempFile(
            "techlab-teste-ativos-", ".db"
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

        // Inicializar o banco de teste

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

        // Injecao de dependencia

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

        AtivoDAO dao = new AtivoDAO(conexaoTeste);

        IPersistencia<AtivoTI> repositorio = dao;

        // Criacao dos equipamentos

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
            "INATIVO",
            2800.00,
            "Switch",
            "192.168.1.1",
            24
        );

        // ==========================================
        // CREATE
        // ==========================================

        System.out.println("\n=== TESTES DE CADASTRO ===");

        verificar(
            repositorio.listar().isEmpty(),
            "Banco de teste inicialmente vazio"
        );

        repositorio.inserir(servidor);
        repositorio.inserir(estacao);
        repositorio.inserir(dispositivo);

        verificar(
            repositorio.listar().size() == 3,
            "Cadastro dos tres tipos de ativos"
        );

        // ==========================================
        // READ
        // ==========================================

        System.out.println("\n=== TESTES DE CONSULTA ===");

        List<AtivoTI> ativos = repositorio.listar();

        verificar(
            ativos.get(0) instanceof Servidor
            && ativos.get(1) instanceof EstacaoTrabalho
            && ativos.get(2) instanceof DispositivoRede,
            "Reconstrucao correta das subclasses"
        );

        AtivoTI ativoEncontrado =
            repositorio.buscarPorId(1);

        verificar(
            ativoEncontrado != null
            && ativoEncontrado.getNome()
                .equals("Servidor Principal"),
            "Busca de ativo por ID"
        );

        verificar(
            repositorio.buscarPorId(999) == null,
            "Busca de ID inexistente"
        );

        AtivoTI ativoPorPatrimonio =
            dao.buscarPorPatrimonio("PAT-002");

        verificar(
            ativoPorPatrimonio != null
            && ativoPorPatrimonio.getId() == 2,
            "Busca por codigo de patrimonio"
        );

        verificar(
            dao.buscarPorPatrimonio("PAT-999") == null,
            "Busca de patrimonio inexistente"
        );

        // ==========================================
        // ATRIBUTOS GERAIS E ESPECIFICOS
        // ==========================================

        System.out.println("\n=== TESTES DE MAPEAMENTO ===");

        Servidor servidorRecuperado =
            (Servidor) repositorio.buscarPorId(1);

        EstacaoTrabalho estacaoRecuperada =
            (EstacaoTrabalho) repositorio.buscarPorId(2);

        DispositivoRede dispositivoRecuperado =
            (DispositivoRede) repositorio.buscarPorId(3);

        verificar(
            servidorRecuperado.getId() == servidor.getId()
            && servidorRecuperado.getCodigoPatrimonio()
                .equals(servidor.getCodigoPatrimonio())
            && servidorRecuperado.getNome()
                .equals(servidor.getNome())
            && servidorRecuperado.getModelo()
                .equals(servidor.getModelo())
            && servidorRecuperado.getDataAquisicao()
                .equals(servidor.getDataAquisicao())
            && servidorRecuperado.getStatus()
                .equals(servidor.getStatus())
            && servidorRecuperado.getValor()
                == servidor.getValor(),
            "Preservacao dos atributos gerais"
        );

        verificar(
            servidorRecuperado.getMemoriaRam() == 32
            && servidorRecuperado.getIpEstatico()
                .equals("192.168.1.10")
            && servidorRecuperado.getSistemaOperacional()
                .equals("Linux"),
            "Preservacao dos atributos do servidor"
        );

        verificar(
            estacaoRecuperada.getMemoriaRam() == 16
            && estacaoRecuperada.getProcessador()
                .equals("Intel Core i5")
            && estacaoRecuperada.getDepartamento()
                .equals("Administrativo"),
            "Preservacao dos atributos da estacao"
        );

        verificar(
            dispositivoRecuperado.getQuantidadePortas() == 24
            && dispositivoRecuperado.getEnderecoIp()
                .equals("192.168.1.1")
            && dispositivoRecuperado.getTipoDispositivo()
                .equals("Switch"),
            "Preservacao dos atributos do dispositivo"
        );

        verificar(
            servidorRecuperado
                .calcularCustoManutencaoMensal() == 1800.00
            && estacaoRecuperada
                .calcularCustoManutencaoMensal() == 160.00
            && dispositivoRecuperado
                .calcularCustoManutencaoMensal() == 290.00,
            "Preservacao do comportamento polimorfico"
        );

        // ==========================================
        // UPDATE
        // ==========================================

        System.out.println("\n=== TESTES DE ATUALIZACAO ===");

        servidor.setNome("Servidor Atualizado");
        servidor.setValor(18000.00);
        servidor.setMemoriaRam(64);

        verificar(
            repositorio.atualizar(servidor),
            "Atualizacao de ativo existente"
        );

        Servidor servidorAtualizado =
            (Servidor) repositorio.buscarPorId(1);

        verificar(
            servidorAtualizado.getNome()
                .equals("Servidor Atualizado")
            && servidorAtualizado.getValor() == 18000.00
            && servidorAtualizado.getMemoriaRam() == 64,
            "Persistencia dos dados atualizados"
        );

        verificar(
            servidorAtualizado
                .calcularCustoManutencaoMensal() == 3400.00,
            "Recalculo do custo de manutencao"
        );

        Servidor inexistente = new Servidor(
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

        verificar(
            !repositorio.atualizar(inexistente),
            "Atualizacao de ativo inexistente"
        );

        // ==========================================
        // DUPLICIDADE
        // ==========================================

        System.out.println("\n=== TESTES DE DUPLICIDADE ===");

        try {

            repositorio.inserir(servidor);

            throw new AssertionError(
                "ID duplicado nao foi bloqueado."
            );

        } catch (SQLException erro) {

            verificar(
                erro.getMessage().contains("UNIQUE"),
                "Bloqueio de ID duplicado"
            );
        }

        Servidor patrimonioDuplicado = new Servidor(
            4,
            "PAT-001",
            "Servidor Secundario",
            "Dell PowerEdge",
            LocalDate.of(2025, 1, 10),
            "ATIVO",
            10000.00,
            "192.168.1.20",
            "Linux",
            16
        );

        try {

            repositorio.inserir(patrimonioDuplicado);

            throw new AssertionError(
                "Patrimonio duplicado nao foi bloqueado."
            );

        } catch (SQLException erro) {

            verificar(
                erro.getMessage().contains(
                    "ativos.codigo_patrimonio"
                ),
                "Bloqueio de patrimonio duplicado"
            );
        }

        // ==========================================
        // SEGURANCA
        // ==========================================

        System.out.println("\n=== TESTES DE SEGURANCA ===");

        String entradaMaliciosa = "' OR '1'='1";

        verificar(
            dao.buscarPorPatrimonio(entradaMaliciosa) == null,
            "Protecao contra SQL Injection na busca"
        );

        verificar(
            repositorio.listar().size() == 3,
            "Preservacao dos registros apos testes de seguranca"
        );

        // ==========================================
        // PERSISTENCIA
        // ==========================================

        System.out.println("\n=== TESTE DE PERSISTENCIA ===");

        AtivoDAO novoDAO = new AtivoDAO(conexaoTeste);

        verificar(
            novoDAO.listar().size() == 3
            && novoDAO.buscarPorId(1).getNome()
                .equals("Servidor Atualizado"),
            "Recuperacao dos dados por uma nova instancia"
        );

        // ==========================================
        // DELETE
        // ==========================================

        System.out.println("\n=== TESTES DE EXCLUSAO ===");

        verificar(
            repositorio.excluir(3),
            "Exclusao de ativo existente"
        );

        verificar(
            repositorio.buscarPorId(3) == null,
            "Confirmacao da exclusao"
        );

        verificar(
            !repositorio.excluir(999),
            "Exclusao de ativo inexistente"
        );

        verificar(
            repositorio.listar().size() == 2,
            "Quantidade final de ativos"
        );

        System.out.println("\n=== ATIVOS RESTANTES ===");

        for (AtivoTI ativo : repositorio.listar()) {

            System.out.println(ativo);
        }

        System.out.println("\n====================================");
        System.out.println(" TODOS OS TESTES FORAM APROVADOS");
        System.out.println("====================================");
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