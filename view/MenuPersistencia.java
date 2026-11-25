package view;

import java.util.Scanner;
import java.util.List;
import java.util.NoSuchElementException;
import java.io.IOException;
import java.sql.SQLException;

import dao.AtivoDAO;
import dao.ChamadoDAO;

import model.AtivoTI;
import model.ChamadoSuporte;

import persistence.AtivoCsvRepository;
import persistence.ChamadoCsvRepository;

import exceptions.EquipamentoNaoEncontradoException;
import exceptions.FalhaConexaoBDException;

public class MenuPersistencia {

    private final Scanner scanner;

    private final AtivoDAO ativoDAO;
    private final ChamadoDAO chamadoDAO;

    private final AtivoCsvRepository ativoCsv;
    private final ChamadoCsvRepository chamadoCsv;

    public MenuPersistencia(Scanner scanner) {

        this.scanner = scanner;

        this.ativoDAO = new AtivoDAO();
        this.chamadoDAO = new ChamadoDAO();

        this.ativoCsv = new AtivoCsvRepository(
            "dados/ativos.csv"
        );

        this.chamadoCsv = new ChamadoCsvRepository(
            "dados/chamados.csv"
        );
    }

    // ==========================================
    // MENU DE PERSISTENCIA
    // ==========================================

    public void iniciar() {

        while (true) {

            exibirMenu();

            try {

                int opcao = lerInteiro("Escolha uma opcao: ");

                switch (opcao) {

                    case 1:
                        exportarAtivos();
                        break;

                    case 2:
                        exportarChamados();
                        break;

                    case 3:
                        exportarTodos();
                        break;

                    case 4:
                        consultarAtivosCsv();
                        break;

                    case 5:
                        consultarChamadosCsv();
                        break;

                    case 0:
                        return;

                    default:
                        System.out.println("Opcao invalida.");
                }

            } catch (EquipamentoNaoEncontradoException erro) {

                System.out.println(
                    "Aviso: " + erro.getMessage()
                );

            } catch (FalhaConexaoBDException erro) {

                System.out.println(
                    "Falha de conexao: " + erro.getMessage()
                );

            } catch (SQLException erro) {

                System.out.println(
                    "Erro no banco: " + erro.getMessage()
                );

            } catch (IOException erro) {

                System.out.println(
                    "Erro no arquivo CSV: " + erro.getMessage()
                );

            } catch (IllegalArgumentException erro) {

                System.out.println(
                    "Dados invalidos: " + erro.getMessage()
                );

            } catch (NoSuchElementException erro) {

                return;
            }
        }
    }

    private void exibirMenu() {

        System.out.println();
        System.out.println("============================================");
        System.out.println("         PERSISTENCIA E BACKUP CSV");
        System.out.println("============================================");
        System.out.println("  1. Exportar ativos para CSV");
        System.out.println("  2. Exportar chamados para CSV");
        System.out.println("  3. Exportar ativos e chamados");
        System.out.println("  4. Consultar ativos do arquivo CSV");
        System.out.println("  5. Consultar chamados do arquivo CSV");
        System.out.println("  0. Voltar ao menu principal");
        System.out.println("============================================");
    }

    // ==========================================
    // EXPORTAR ATIVOS
    // ==========================================

    private void exportarAtivos()
            throws SQLException, IOException {

        System.out.println("\n=== EXPORTACAO DE ATIVOS ===");

        if (!confirmarExportacao()) {
            return;
        }

        List<AtivoTI> ativos = ativoDAO.listar();

        ativoCsv.salvarTodos(ativos);

        System.out.println(
            "Ativos exportados com sucesso!"
        );

        System.out.println(
            "Arquivo: dados/ativos.csv"
        );

        System.out.println(
            "Total de ativos exportados: " + ativos.size()
        );
    }

    // ==========================================
    // EXPORTAR CHAMADOS
    // ==========================================

    private void exportarChamados()
            throws SQLException, IOException {

        System.out.println("\n=== EXPORTACAO DE CHAMADOS ===");

        if (!confirmarExportacao()) {
            return;
        }

        List<ChamadoSuporte> chamados = chamadoDAO.listar();

        chamadoCsv.salvarTodos(chamados);

        System.out.println(
            "Chamados exportados com sucesso!"
        );

        System.out.println(
            "Arquivo: dados/chamados.csv"
        );

        System.out.println(
            "Total de chamados exportados: " + chamados.size()
        );

        System.out.println(
            "Mantenha o CSV de ativos atualizado para consultar os chamados."
        );
    }

    // ==========================================
    // EXPORTAR TODOS OS REGISTROS
    // ==========================================

    private void exportarTodos()
            throws SQLException, IOException {

        System.out.println("\n=== EXPORTACAO COMPLETA ===");

        if (!confirmarExportacao()) {
            return;
        }

        List<AtivoTI> ativos = ativoDAO.listar();

        List<ChamadoSuporte> chamados = chamadoDAO.listar();

        ativoCsv.salvarTodos(ativos);

        chamadoCsv.salvarTodos(chamados);

        System.out.println(
            "\nExportacao completa realizada com sucesso!"
        );

        System.out.println(
            "Arquivo de ativos: dados/ativos.csv"
        );

        System.out.println(
            "Arquivo de chamados: dados/chamados.csv"
        );

        System.out.println(
            "Total de ativos: " + ativos.size()
        );

        System.out.println(
            "Total de chamados: " + chamados.size()
        );
    }

    // ==========================================
    // CONSULTAR ATIVOS DO CSV
    // ==========================================

    private void consultarAtivosCsv()
            throws IOException {

        System.out.println("\n=== ATIVOS DO ARQUIVO CSV ===");

        List<AtivoTI> ativos = ativoCsv.carregarTodos();

        if (ativos.isEmpty()) {

            System.out.println(
                "Arquivo inexistente ou sem ativos cadastrados."
            );

            return;
        }

        for (AtivoTI ativo : ativos) {

            System.out.println("------------------------------------");
            System.out.println(ativo);
        }

        System.out.println("------------------------------------");

        System.out.println(
            "Total de ativos recuperados: " + ativos.size()
        );
    }

    // ==========================================
    // CONSULTAR CHAMADOS DO CSV
    // ==========================================

    private void consultarChamadosCsv()
            throws IOException,
                   EquipamentoNaoEncontradoException {

        System.out.println("\n=== CHAMADOS DO ARQUIVO CSV ===");

        List<AtivoTI> ativos = ativoCsv.carregarTodos();

        List<ChamadoSuporte> chamados =
            chamadoCsv.carregarTodos(ativos);

        if (chamados.isEmpty()) {

            System.out.println(
                "Arquivo inexistente ou sem chamados cadastrados."
            );

            return;
        }

        for (ChamadoSuporte chamado : chamados) {

            System.out.println(chamado);
        }

        System.out.println(
            "Total de chamados recuperados: " + chamados.size()
        );
    }

    // ==========================================
    // CONFIRMAR EXPORTACAO
    // ==========================================

    private boolean confirmarExportacao() {

        System.out.println(
            "Arquivos CSV anteriores poderao ser substituidos."
        );

        String confirmacao = lerTexto(
            "Confirma a exportacao? (S/N): "
        );

        if (!confirmacao.equalsIgnoreCase("S")) {

            System.out.println("Exportacao cancelada.");

            return false;
        }

        return true;
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
