package view;

import java.util.Scanner;
import java.util.List;
import java.util.Comparator;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.sql.SQLException;

import dao.AtivoDAO;
import model.AtivoTI;
import dto.AtivoDTO;
import service.GerenciadorColecao;
import exceptions.FalhaConexaoBDException;

public class MenuConsultas {

    private final Scanner scanner;
    private final AtivoDAO ativoDAO;

    public MenuConsultas(Scanner scanner) {

        this.scanner = scanner;
        this.ativoDAO = new AtivoDAO();
    }

    // ==========================================
    // MENU DE CONSULTAS
    // ==========================================

    public void iniciar() {

        while (true) {

            exibirMenu();

            try {

                int opcao = lerInteiro("Escolha uma opcao: ");

                switch (opcao) {

                    case 1:
                        filtrarPorTipo();
                        break;

                    case 2:
                        filtrarPorStatus();
                        break;

                    case 3:
                        buscarPorNome();
                        break;

                    case 4:
                        ordenarPorNome();
                        break;

                    case 5:
                        ordenarPorValor();
                        break;

                    case 0:
                        return;

                    default:
                        System.out.println("Opcao invalida.");
                }

            } catch (FalhaConexaoBDException erro) {

                System.out.println(
                    "Falha de conexao: " + erro.getMessage()
                );

            } catch (SQLException erro) {

                System.out.println(
                    "Erro no banco: " + erro.getMessage()
                );

            } catch (NoSuchElementException erro) {

                return;
            }
        }
    }

    private void exibirMenu() {

        System.out.println();
        System.out.println("============================================");
        System.out.println("         CONSULTAS E ORDENACAO");
        System.out.println("============================================");
        System.out.println("  1. Filtrar ativos por tipo");
        System.out.println("  2. Filtrar ativos por status");
        System.out.println("  3. Buscar ativos por nome");
        System.out.println("  4. Ordenar ativos por nome");
        System.out.println("  5. Ordenar ativos por valor");
        System.out.println("  0. Voltar ao menu principal");
        System.out.println("============================================");
    }

    // ==========================================
    // CARREGAR DADOS E CONVERTER PARA DTO
    // ==========================================

    private GerenciadorColecao<AtivoDTO> carregarAtivos()
            throws SQLException {

        GerenciadorColecao<AtivoDTO> colecao =
            new GerenciadorColecao<>();

        List<AtivoTI> ativos = ativoDAO.listar();

        for (AtivoTI ativo : ativos) {

            colecao.adicionar(new AtivoDTO(ativo));
        }

        return colecao;
    }

    // ==========================================
    // FILTRAR POR TIPO
    // ==========================================

    private void filtrarPorTipo() throws SQLException {

        System.out.println("\n=== FILTRO POR TIPO ===");
        System.out.println("1. Servidor");
        System.out.println("2. Estacao de Trabalho");
        System.out.println("3. Dispositivo de Rede");

        int opcao = lerInteiro("Selecione o tipo: ");

        String tipo;

        switch (opcao) {

            case 1:
                tipo = "Servidor";
                break;

            case 2:
                tipo = "EstacaoTrabalho";
                break;

            case 3:
                tipo = "DispositivoRede";
                break;

            default:
                System.out.println("Tipo invalido.");
                return;
        }

        GerenciadorColecao<AtivoDTO> colecao =
            carregarAtivos();

        List<AtivoDTO> resultado = colecao.filtrar(
            ativo -> ativo.getTipo().equalsIgnoreCase(tipo)
        );

        exibirResultados(resultado);
    }

    // ==========================================
    // FILTRAR POR STATUS
    // ==========================================

    private void filtrarPorStatus() throws SQLException {

        String status = selecionarStatus();

        GerenciadorColecao<AtivoDTO> colecao =
            carregarAtivos();

        List<AtivoDTO> resultado = colecao.filtrar(
            ativo -> ativo.getStatus().equalsIgnoreCase(status)
        );

        System.out.println("\nStatus selecionado: " + status);

        exibirResultados(resultado);
    }

    // ==========================================
    // SELECIONAR STATUS
    // ==========================================

    private String selecionarStatus() {

        while (true) {

            System.out.println("\n=== FILTRO POR STATUS ===");
            System.out.println("1. ATIVO");
            System.out.println("2. INATIVO");
            System.out.println("3. EM MANUTENCAO");
            System.out.println("4. DESCARTADO");

            int opcao = lerInteiro("Selecione o status: ");

            switch (opcao) {

                case 1:
                    return "ATIVO";

                case 2:
                    return "INATIVO";

                case 3:
                    return "EM_MANUTENCAO";

                case 4:
                    return "DESCARTADO";

                default:

                    System.out.println(
                        "Status invalido. Escolha uma opcao de 1 a 4."
                    );
            }
        }
    }

    // ==========================================
    // BUSCAR POR NOME
    // ==========================================

    private void buscarPorNome() throws SQLException {

        System.out.println("\n=== BUSCA POR NOME ===");

        String nome = lerTexto("Informe o nome: ")
            .toLowerCase(Locale.ROOT);

        GerenciadorColecao<AtivoDTO> colecao =
            carregarAtivos();

        List<AtivoDTO> resultado = colecao.filtrar(
            ativo -> ativo.getNome()
                .toLowerCase(Locale.ROOT)
                .contains(nome)
        );

        exibirResultados(resultado);
    }

    // ==========================================
    // ORDENAR POR NOME
    // ==========================================

    private void ordenarPorNome() throws SQLException {

        System.out.println("\n=== ORDENACAO POR NOME ===");

        GerenciadorColecao<AtivoDTO> colecao =
            carregarAtivos();

        List<AtivoDTO> resultado = colecao.ordenar(
            Comparator.comparing(
                AtivoDTO::getNome,
                String.CASE_INSENSITIVE_ORDER
            )
        );

        exibirResultados(resultado);
    }

    // ==========================================
    // ORDENAR POR VALOR
    // ==========================================

    private void ordenarPorValor() throws SQLException {

        System.out.println("\n=== ORDENACAO POR VALOR ===");
        System.out.println("1. Menor para maior");
        System.out.println("2. Maior para menor");

        int opcao = lerInteiro("Escolha a ordenacao: ");

        Comparator<AtivoDTO> comparador =
            Comparator.comparingDouble(AtivoDTO::getValor);

        if (opcao == 2) {

            comparador = comparador.reversed();

        } else if (opcao != 1) {

            System.out.println("Opcao invalida.");
            return;
        }

        GerenciadorColecao<AtivoDTO> colecao =
            carregarAtivos();

        List<AtivoDTO> resultado =
            colecao.ordenar(comparador);

        exibirResultados(resultado);
    }

    // ==========================================
    // EXIBIR RESULTADOS
    // ==========================================

    private void exibirResultados(List<AtivoDTO> ativos) {

        System.out.println("\n=== RESULTADO DA CONSULTA ===");

        if (ativos.isEmpty()) {

            System.out.println("Nenhum equipamento encontrado.");
            return;
        }

        for (AtivoDTO ativo : ativos) {

            System.out.println("------------------------------------");
            System.out.println("ID: " + ativo.getId());
            System.out.println("Patrimonio: " + ativo.getCodigoPatrimonio());
            System.out.println("Nome: " + ativo.getNome());
            System.out.println("Modelo: " + ativo.getModelo());
            System.out.println("Tipo: " + ativo.getTipo());
            System.out.println("Status: " + ativo.getStatus());

            System.out.printf(
                Locale.forLanguageTag("pt-BR"),
                "Valor: R$ %,.2f%n",
                ativo.getValor()
            );
        }

        System.out.println("------------------------------------");

        System.out.println(
            "Total de equipamentos: " + ativos.size()
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

            System.out.println("O campo nao pode ficar vazio.");
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