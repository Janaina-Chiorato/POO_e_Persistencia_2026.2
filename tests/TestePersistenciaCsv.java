package tests;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import model.AtivoTI;
import model.Servidor;
import model.EstacaoTrabalho;
import model.DispositivoRede;
import model.ChamadoSuporte;

import persistence.AtivoCsvRepository;
import persistence.ChamadoCsvRepository;

import exceptions.EquipamentoNaoEncontradoException;

public class TestePersistenciaCsv {

    public static void main(String[] args) throws Exception {

        System.out.println("====================================");
        System.out.println(" TECHLAB - PERSISTENCIA CSV");
        System.out.println("====================================");

        // Diretorio exclusivo para os testes

        Path pasta = Path.of("dados", "testes");

        Files.createDirectories(pasta);

        // Repositorios

        AtivoCsvRepository ativoRepository =
            new AtivoCsvRepository(
                pasta.resolve("ativos.csv").toString()
            );

        ChamadoCsvRepository chamadoRepository =
            new ChamadoCsvRepository(
                pasta.resolve("chamados.csv").toString()
            );

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

        List<AtivoTI> ativos = List.of(
            servidor,
            estacao,
            dispositivo
        );

        System.out.println("\n=== GRAVACAO DOS ATIVOS ===");

        ativoRepository.salvarTodos(ativos);

        verificar(
            Files.exists(pasta.resolve("ativos.csv")),
            "Arquivo de ativos criado"
        );

        System.out.println("\n=== RECUPERACAO DOS ATIVOS ===");

        List<AtivoTI> recuperados =
            ativoRepository.carregarTodos();

        verificar(
            recuperados.size() == 3,
            "Recuperacao dos tres ativos"
        );

        verificar(
            recuperados.get(0) instanceof Servidor
            && recuperados.get(1) instanceof EstacaoTrabalho
            && recuperados.get(2) instanceof DispositivoRede,
            "Reconstrucao correta das subclasses"
        );

        Servidor servidorRecuperado =
            (Servidor) recuperados.get(0);

        EstacaoTrabalho estacaoRecuperada =
            (EstacaoTrabalho) recuperados.get(1);

        DispositivoRede dispositivoRecuperado =
            (DispositivoRede) recuperados.get(2);

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

        // Criacao dos chamados

        String descricaoEspecial =
            "Falha de conexão; mensagem \"Erro 500\".\n"
            + "Necessária uma avaliação técnica.";

        ChamadoSuporte chamado1 = new ChamadoSuporte(
            1,
            "Falha no servidor",
            descricaoEspecial,
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

        List<ChamadoSuporte> chamados =
            List.of(chamado1, chamado2);

        System.out.println("\n=== GRAVACAO DOS CHAMADOS ===");

        chamadoRepository.salvarTodos(chamados);

        verificar(
            Files.exists(pasta.resolve("chamados.csv")),
            "Arquivo de chamados criado"
        );

        System.out.println("\n=== RECUPERACAO DOS CHAMADOS ===");

        List<ChamadoSuporte> chamadosRecuperados =
            chamadoRepository.carregarTodos(recuperados);

        verificar(
            chamadosRecuperados.size() == 2,
            "Recuperacao dos dois chamados"
        );

        ChamadoSuporte chamadoRecuperado =
            chamadosRecuperados.get(0);

        verificar(
            chamadoRecuperado.getAtivoRelacionado()
                == servidorRecuperado
            && chamadosRecuperados.get(1)
                .getAtivoRelacionado() == estacaoRecuperada,
            "Preservacao do relacionamento entre entidades"
        );

        verificar(
            chamadoRecuperado.getDescricao()
                .equals(descricaoEspecial),
            "Preservacao de acentos, aspas e separadores"
        );

        verificar(
            chamadoRecuperado.getDescricao()
                .contains("\n"),
            "Preservacao das quebras de linha"
        );

        verificar(
            chamadoRecuperado.getId() == chamado1.getId()
            && chamadoRecuperado.getTitulo()
                .equals(chamado1.getTitulo())
            && chamadoRecuperado.getPrioridade()
                .equals(chamado1.getPrioridade())
            && chamadoRecuperado.getDataAbertura()
                .equals(chamado1.getDataAbertura())
            && chamadoRecuperado.getStatus()
                .equals(chamado1.getStatus()),
            "Preservacao dos dados do chamado"
        );

        System.out.println("\n=== TESTES DE DUPLICIDADE ===");

        try {

            ativoRepository.salvarTodos(
                List.of(servidor, servidor)
            );

            throw new AssertionError(
                "ID duplicado nao foi bloqueado."
            );

        } catch (IllegalArgumentException erro) {

            verificar(
                erro.getMessage().contains("ID duplicado"),
                "Bloqueio de ID de ativo duplicado"
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

            ativoRepository.salvarTodos(
                List.of(servidor, patrimonioDuplicado)
            );

            throw new AssertionError(
                "Patrimonio duplicado nao foi bloqueado."
            );

        } catch (IllegalArgumentException erro) {

            verificar(
                erro.getMessage().contains(
                    "Patrimonio duplicado"
                ),
                "Bloqueio de patrimonio duplicado"
            );
        }

        try {

            chamadoRepository.salvarTodos(
                List.of(chamado1, chamado1)
            );

            throw new AssertionError(
                "Chamado duplicado nao foi bloqueado."
            );

        } catch (IllegalArgumentException erro) {

            verificar(
                erro.getMessage().contains("duplicado"),
                "Bloqueio de ID de chamado duplicado"
            );
        }

        System.out.println("\n=== INTEGRIDADE REFERENCIAL ===");

        try {

            chamadoRepository.carregarTodos(
                List.of(estacaoRecuperada)
            );

            throw new AssertionError(
                "Equipamento inexistente nao foi detectado."
            );

        } catch (EquipamentoNaoEncontradoException erro) {

            verificar(
                erro.getMessage().contains("ID 1"),
                "Excecao de equipamento nao encontrado"
            );

            System.out.println(
                "Mensagem: " + erro.getMessage()
            );
        }

        System.out.println("\n=== PRESERVACAO DOS ARQUIVOS ===");

        verificar(
            ativoRepository.carregarTodos().size() == 3
            && chamadoRepository.carregarTodos(recuperados)
                .size() == 2,
            "Arquivos preservados apos rejeicao de duplicidades"
        );

        System.out.println("\n====================================");
        System.out.println(" TODOS OS TESTES FORAM APROVADOS");
        System.out.println("====================================");

        System.out.println(
            "\nArquivos gerados em: "
            + pasta.toAbsolutePath()
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