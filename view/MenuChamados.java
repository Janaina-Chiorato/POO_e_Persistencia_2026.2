package view;

import java.util.Scanner;
import java.util.List;
import java.util.NoSuchElementException;

import java.time.LocalDateTime;
import java.sql.SQLException;

import dao.AtivoDAO;
import dao.ChamadoDAO;

import model.AtivoTI;
import model.ChamadoSuporte;

import exceptions.EquipamentoNaoEncontradoException;
import exceptions.FalhaConexaoBDException;

public class MenuChamados {

    private final Scanner scanner;
    private final AtivoDAO ativoDAO;
    private final ChamadoDAO chamadoDAO;

    public MenuChamados(Scanner scanner) {

        this.scanner = scanner;
        this.ativoDAO = new AtivoDAO();
        this.chamadoDAO = new ChamadoDAO();
    }

    // ==========================================
    // MENU DE CHAMADOS
    // ==========================================

    public void iniciar() {

        while (true) {

            exibirMenu();

            try {

                int opcao = lerInteiro("Escolha uma opcao: ");

                switch (opcao) {

                    case 1:
                        abrirChamado();
                        break;

                    case 2:
                        listarChamados();
                        break;

                    case 3:
                        buscarPorId();
                        break;

                    case 4:
                        listarPorAtivo();
                        break;

                    case 5:
                        filtrarPorStatus();
                        break;

                    case 6:
                        filtrarPorPrioridade();
                        break;

                    case 7:
                        atualizarChamado();
                        break;

                    case 8:
                        encerrarChamado();
                        break;

                    case 9:
                        excluirChamado();
                        break;

                    case 0:
                        return;

                    default:
                        System.out.println("Opcao invalida.");
                }

            } catch (EquipamentoNaoEncontradoException erro) {

                System.out.println(
                    "\nAviso: " + erro.getMessage()
                );

            } catch (FalhaConexaoBDException erro) {

                System.out.println(
                    "\nFalha de conexao: " + erro.getMessage()
                );

            } catch (SQLException erro) {

                System.out.println(
                    "\nErro no banco: " + erro.getMessage()
                );

            } catch (IllegalArgumentException erro) {

                System.out.println(
                    "\nDados invalidos: " + erro.getMessage()
                );

            } catch (NoSuchElementException erro) {

                return;
            }
        }
    }

    private void exibirMenu() {

        System.out.println();
        System.out.println("============================================");
        System.out.println("       GERENCIAMENTO DE CHAMADOS");
        System.out.println("============================================");
        System.out.println("  1. Abrir chamado");
        System.out.println("  2. Listar todos os chamados");
        System.out.println("  3. Buscar chamado por ID");
        System.out.println("  4. Listar chamados por equipamento");
        System.out.println("  5. Filtrar chamados por status");
        System.out.println("  6. Filtrar chamados por prioridade");
        System.out.println("  7. Atualizar chamado");
        System.out.println("  8. Encerrar chamado");
        System.out.println("  9. Excluir chamado");
        System.out.println("  0. Voltar ao menu principal");
        System.out.println("============================================");
    }

    // ==========================================
    // ABRIR CHAMADO
    // ==========================================

    private void abrirChamado()
            throws SQLException,
                   EquipamentoNaoEncontradoException {

        System.out.println("\n=== ABERTURA DE CHAMADO ===");

        int id = lerInteiro("ID do chamado: ");

        if (chamadoDAO.buscarPorId(id) != null) {

            System.out.println("ID de chamado ja cadastrado.");
            return;
        }

        exibirEquipamentos();

        int ativoId = lerInteiro("ID do equipamento: ");

        AtivoTI ativo = localizarAtivo(ativoId);

        String titulo = lerTexto("Titulo: ");

        String descricao = lerTexto("Descricao: ");

        String prioridade = selecionarPrioridade();

        ChamadoSuporte chamado = new ChamadoSuporte(
            id,
            titulo,
            descricao,
            prioridade,
            LocalDateTime.now(),
            ativo,
            "ABERTO"
        );

        chamadoDAO.inserir(chamado);

        chamado.enviarAlerta(
            "Novo chamado #" + id + " registrado."
        );

        System.out.println("\nChamado aberto com sucesso!");
    }

    // ==========================================
    // LISTAR CHAMADOS
    // ==========================================

    private void listarChamados() throws SQLException {

        System.out.println("\n=== CHAMADOS CADASTRADOS ===");

        exibirChamados(chamadoDAO.listar());
    }

    // ==========================================
    // BUSCAR CHAMADO POR ID
    // ==========================================

    private void buscarPorId() throws SQLException {

        int id = lerInteiro("ID do chamado: ");

        ChamadoSuporte chamado = chamadoDAO.buscarPorId(id);

        if (chamado == null) {

            System.out.println("Chamado nao encontrado.");
            return;
        }

        System.out.println(chamado);
    }

    // ==========================================
    // LISTAR POR EQUIPAMENTO
    // ==========================================

    private void listarPorAtivo()
            throws SQLException,
                   EquipamentoNaoEncontradoException {

        exibirEquipamentos();

        int id = lerInteiro("ID do equipamento: ");

        AtivoTI ativo = localizarAtivo(id);

        System.out.println(
            "\nChamados do equipamento: " + ativo.getNome()
        );

        exibirChamados(chamadoDAO.listarPorAtivo(id));
    }

    // ==========================================
    // FILTRAR POR STATUS
    // ==========================================

    private void filtrarPorStatus() throws SQLException {

        String status = selecionarStatus();

        System.out.println("\nChamados com status: " + status);

        exibirChamados(
            chamadoDAO.listarPorStatus(status)
        );
    }

    // ==========================================
    // FILTRAR POR PRIORIDADE
    // ==========================================

    private void filtrarPorPrioridade() throws SQLException {

        String prioridade = selecionarPrioridade();

        System.out.println(
            "\nChamados com prioridade: " + prioridade
        );

        exibirChamados(
            chamadoDAO.listarPorPrioridade(prioridade)
        );
    }

    // ==========================================
    // ATUALIZAR CHAMADO
    // ==========================================

    private void atualizarChamado()
            throws SQLException,
                   EquipamentoNaoEncontradoException {

        System.out.println("\n=== ATUALIZACAO DE CHAMADO ===");

        int id = lerInteiro("ID do chamado: ");

        ChamadoSuporte chamado = chamadoDAO.buscarPorId(id);

        if (chamado == null) {

            System.out.println("Chamado nao encontrado.");
            return;
        }

        System.out.println("\nDados atuais:");
        System.out.println(chamado);

        chamado.setTitulo(
            lerTexto("Novo titulo: ")
        );

        chamado.setDescricao(
            lerTexto("Nova descricao: ")
        );

        chamado.setPrioridade(
            selecionarPrioridade()
        );

        chamado.setStatus(
            selecionarStatus()
        );

        exibirEquipamentos();

        int ativoId = lerInteiro("ID do equipamento: ");

        chamado.setAtivoRelacionado(
            localizarAtivo(ativoId)
        );

        if (chamadoDAO.atualizar(chamado)) {

            System.out.println(
                "\nChamado atualizado com sucesso!"
            );

        } else {

            System.out.println(
                "Nao foi possivel atualizar o chamado."
            );
        }
    }

    // ==========================================
    // ENCERRAR CHAMADO
    // ==========================================

    private void encerrarChamado() throws SQLException {

        System.out.println("\n=== ENCERRAMENTO DE CHAMADO ===");

        int id = lerInteiro("ID do chamado: ");

        ChamadoSuporte chamado = chamadoDAO.buscarPorId(id);

        if (chamado == null) {

            System.out.println("Chamado nao encontrado.");
            return;
        }

        if (chamado.getStatus().equals("FECHADO")) {

            System.out.println("Este chamado ja esta fechado.");
            return;
        }

        System.out.println(chamado);

        String confirmacao = lerTexto(
            "Confirma o encerramento? (S/N): "
        );

        if (!confirmacao.equalsIgnoreCase("S")) {

            System.out.println("Encerramento cancelado.");
            return;
        }

        chamado.setStatus("FECHADO");

        if (chamadoDAO.atualizar(chamado)) {

            chamado.fecharChamado();

            System.out.println(
                "\nEncerramento registrado no banco de dados."
            );

        } else {

            System.out.println(
                "Nao foi possivel encerrar o chamado."
            );
        }
    }

    // ==========================================
    // EXCLUIR CHAMADO
    // ==========================================

    private void excluirChamado() throws SQLException {

        System.out.println("\n=== EXCLUSAO DE CHAMADO ===");

        int id = lerInteiro("ID do chamado: ");

        ChamadoSuporte chamado = chamadoDAO.buscarPorId(id);

        if (chamado == null) {

            System.out.println("Chamado nao encontrado.");
            return;
        }

        System.out.println(chamado);

        String confirmacao = lerTexto(
            "Confirma a exclusao definitiva? (S/N): "
        );

        if (!confirmacao.equalsIgnoreCase("S")) {

            System.out.println("Exclusao cancelada.");
            return;
        }

        if (chamadoDAO.excluir(id)) {

            System.out.println(
                "\nChamado excluido com sucesso!"
            );

        } else {

            System.out.println("Chamado nao localizado.");
        }
    }

    // ==========================================
    // SELECIONAR PRIORIDADE
    // ==========================================

    private String selecionarPrioridade() {

        while (true) {

            System.out.println("\n=== PRIORIDADE ===");
            System.out.println("1. BAIXA");
            System.out.println("2. MEDIA");
            System.out.println("3. ALTA");
            System.out.println("4. CRITICA");

            int opcao = lerInteiro("Selecione a prioridade: ");

            switch (opcao) {

                case 1:
                    return "BAIXA";

                case 2:
                    return "MEDIA";

                case 3:
                    return "ALTA";

                case 4:
                    return "CRITICA";

                default:
                    System.out.println("Prioridade invalida.");
            }
        }
    }

    // ==========================================
    // SELECIONAR STATUS
    // ==========================================

    private String selecionarStatus() {

        while (true) {

            System.out.println("\n=== STATUS ===");
            System.out.println("1. ABERTO");
            System.out.println("2. EM_ATENDIMENTO");
            System.out.println("3. RESOLVIDO");
            System.out.println("4. FECHADO");

            int opcao = lerInteiro("Selecione o status: ");

            switch (opcao) {

                case 1:
                    return "ABERTO";

                case 2:
                    return "EM_ATENDIMENTO";

                case 3:
                    return "RESOLVIDO";

                case 4:
                    return "FECHADO";

                default:
                    System.out.println("Status invalido.");
            }
        }
    }

    // ==========================================
    // EXIBIR EQUIPAMENTOS DISPONIVEIS
    // ==========================================

    private void exibirEquipamentos() throws SQLException {

        List<AtivoTI> ativos = ativoDAO.listar();

        System.out.println("\n=== EQUIPAMENTOS CADASTRADOS ===");

        if (ativos.isEmpty()) {

            System.out.println("Nenhum equipamento cadastrado.");
            return;
        }

        for (AtivoTI ativo : ativos) {

            System.out.println(
                "ID: " + ativo.getId()
                + " | " + ativo.getNome()
                + " | Patrimonio: " + ativo.getCodigoPatrimonio()
                + " | Status: " + ativo.getStatus()
            );
        }
    }

    // ==========================================
    // LOCALIZAR EQUIPAMENTO
    // ==========================================

    private AtivoTI localizarAtivo(int id)
            throws SQLException,
                   EquipamentoNaoEncontradoException {

        AtivoTI ativo = ativoDAO.buscarPorId(id);

        if (ativo == null) {

            throw new EquipamentoNaoEncontradoException(id);
        }

        return ativo;
    }

    // ==========================================
    // EXIBIR LISTA DE CHAMADOS
    // ==========================================

    private void exibirChamados(List<ChamadoSuporte> chamados) {

        if (chamados.isEmpty()) {

            System.out.println("Nenhum chamado encontrado.");
            return;
        }

        for (ChamadoSuporte chamado : chamados) {

            System.out.println(chamado);
        }

        System.out.println(
            "Total de chamados: " + chamados.size()
        );
    }

    // ==========================================
    // VALIDACAO DE ENTRADAS
    // ==========================================

    private String lerTexto(String mensagem) {

        while (true) {

            System.out.print(mensagem);

            String entrada = scanner.nextLine().trim();

            if (!entrada.isEmpty()) {

                return entrada;
            }

            System.out.println(
                "O campo nao pode ficar vazio."
            );
        }
    }

    private int lerInteiro(String mensagem) {

        while (true) {

            try {

                return Integer.parseInt(
                    lerTexto(mensagem)
                );

            } catch (NumberFormatException erro) {

                System.out.println(
                    "Digite um numero inteiro valido."
                );
            }
        }
    }
}