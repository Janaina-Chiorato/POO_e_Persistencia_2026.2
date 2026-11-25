package view;

import dao.AtivoDAO;
import exceptions.EquipamentoNaoEncontradoException;
import exceptions.FalhaConexaoBDException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import model.AtivoTI;
import model.DispositivoRede;
import model.EstacaoTrabalho;
import model.Servidor;

public class MenuAtivos {

    private final Scanner scanner;
    private final AtivoDAO ativoDAO;

    public MenuAtivos(Scanner scanner) {

        this.scanner = scanner;
        this.ativoDAO = new AtivoDAO();
    }

    // ==========================================
    // MENU DE ATIVOS
    // ==========================================

    public void iniciar() {

        while (true) {

            exibirMenu();

            try {

                int opcao = lerInteiro("Escolha uma opcao: ");

                switch (opcao) {

                    case 1:
                        cadastrarAtivo();
                        break;

                    case 2:
                        listarAtivos();
                        break;

                    case 3:
                        buscarPorId();
                        break;

                    case 4:
                        buscarPorPatrimonio();
                        break;

                    case 5:
                        atualizarAtivo();
                        break;

                    case 6:
                        excluirAtivo();
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

            } catch (java.util.NoSuchElementException erro) {

                return;
            }
        }
    }

    private void exibirMenu() {

        System.out.println();
        System.out.println("============================================");
        System.out.println("       GERENCIAMENTO DE ATIVOS");
        System.out.println("============================================");
        System.out.println("  1. Cadastrar ativo");
        System.out.println("  2. Listar todos os ativos");
        System.out.println("  3. Buscar ativo por ID");
        System.out.println("  4. Buscar ativo por patrimonio");
        System.out.println("  5. Atualizar ativo");
        System.out.println("  6. Excluir ativo");
        System.out.println("  0. Voltar ao menu principal");
        System.out.println("============================================");
    }

    // ==========================================
    // CADASTRAR ATIVO
    // ==========================================

    private void cadastrarAtivo() throws SQLException {

        System.out.println("\n=== CADASTRO DE ATIVO ===");

        System.out.println("1. Servidor");
        System.out.println("2. Estacao de Trabalho");
        System.out.println("3. Dispositivo de Rede");

        int tipo = lerInteiro("Tipo de equipamento: ");

        if (tipo < 1 || tipo > 3) {

            System.out.println("Tipo invalido.");
            return;
        }

        int id = lerInteiroPositivo("ID: ");

        if (ativoDAO.buscarPorId(id) != null) {

            System.out.println("ID ja cadastrado.");
            return;
        }

        String patrimonio = lerTexto("Codigo de patrimonio: ");

        if (ativoDAO.buscarPorPatrimonio(patrimonio) != null) {

            System.out.println("Patrimonio ja cadastrado.");
            return;
        }

        String nome = lerTexto("Nome: ");
        String modelo = lerTexto("Modelo: ");

        LocalDate data = lerData(
            "Data de aquisicao (AAAA-MM-DD): "
        );

        String status = selecionarStatus();

        double valor = lerDouble("Valor do equipamento: R$ ");

        AtivoTI ativo;

        switch (tipo) {

            case 1:

                String ip = lerTexto("IP estatico: ");
                String sistema = lerTexto("Sistema operacional: ");

                int ramServidor = lerInteiroPositivo(
                    "Memoria RAM (GB): "
                );

                ativo = new Servidor(
                    id,
                    patrimonio,
                    nome,
                    modelo,
                    data,
                    status,
                    valor,
                    ip,
                    sistema,
                    ramServidor
                );

                break;

            case 2:

                String processador = lerTexto("Processador: ");

                int ramEstacao = lerInteiroPositivo(
                    "Memoria RAM (GB): "
                );

                String departamento = lerTexto("Departamento: ");

                ativo = new EstacaoTrabalho(
                    id,
                    patrimonio,
                    nome,
                    modelo,
                    data,
                    status,
                    valor,
                    processador,
                    ramEstacao,
                    departamento
                );

                break;

            case 3:

                String tipoDispositivo = lerTexto(
                    "Tipo de dispositivo: "
                );

                String enderecoIp = lerTexto("Endereco IP: ");

                int portas = lerInteiroPositivo(
                    "Quantidade de portas: "
                );

                ativo = new DispositivoRede(
                    id,
                    patrimonio,
                    nome,
                    modelo,
                    data,
                    status,
                    valor,
                    tipoDispositivo,
                    enderecoIp,
                    portas
                );

                break;

            default:
                return;
        }

        ativoDAO.inserir(ativo);

        System.out.println("\nAtivo cadastrado com sucesso!");
    }

    // ==========================================
    // LISTAR ATIVOS
    // ==========================================

    private void listarAtivos() throws SQLException {

        System.out.println("\n=== ATIVOS CADASTRADOS ===");

        List<AtivoTI> ativos = ativoDAO.listar();

        if (ativos.isEmpty()) {

            System.out.println("Nenhum ativo cadastrado.");
            return;
        }

        for (AtivoTI ativo : ativos) {

            System.out.println(ativo);
            System.out.println("--------------------------------");
        }

        System.out.println(
            "Total de equipamentos: " + ativos.size()
        );
    }

    // ==========================================
    // BUSCAR POR ID
    // ==========================================

    private void buscarPorId()
            throws SQLException,
                   EquipamentoNaoEncontradoException {

        int id = lerInteiroPositivo("Informe o ID: ");

        AtivoTI ativo = localizarAtivo(id);

        System.out.println("\n=== EQUIPAMENTO LOCALIZADO ===");
        System.out.println(ativo);
    }

    // ==========================================
    // BUSCAR POR PATRIMONIO
    // ==========================================

    private void buscarPorPatrimonio()
            throws SQLException,
                   EquipamentoNaoEncontradoException {

        String patrimonio = lerTexto(
            "Informe o codigo de patrimonio: "
        );

        AtivoTI ativo =
            ativoDAO.buscarPorPatrimonio(patrimonio);

        if (ativo == null) {

            throw new EquipamentoNaoEncontradoException(
                "Equipamento com patrimonio "
                + patrimonio + " nao encontrado."
            );
        }

        System.out.println("\n=== EQUIPAMENTO LOCALIZADO ===");
        System.out.println(ativo);
    }

    // ==========================================
    // ATUALIZAR ATIVO
    // ==========================================

    private void atualizarAtivo()
            throws SQLException,
                   EquipamentoNaoEncontradoException {

        System.out.println("\n=== ATUALIZACAO DE ATIVO ===");

        int id = lerInteiroPositivo("ID do equipamento: ");

        AtivoTI ativo = localizarAtivo(id);

        System.out.println("\nDados atuais:");
        System.out.println(ativo);

        System.out.println("\nInforme os novos dados:");

        ativo.setCodigoPatrimonio(
            lerTexto("Novo patrimonio: ")
        );

        ativo.setNome(
            lerTexto("Novo nome: ")
        );

        ativo.setModelo(
            lerTexto("Novo modelo: ")
        );

        ativo.setDataAquisicao(
            lerData("Nova data de aquisicao (AAAA-MM-DD): ")
        );

        ativo.setStatus(selecionarStatus());

        ativo.setValor(
            lerDouble("Novo valor: R$ ")
        );

        // Atualizar atributos especificos

        if (ativo instanceof Servidor) {

            Servidor servidor = (Servidor) ativo;

            servidor.setIpEstatico(
                lerTexto("Novo IP estatico: ")
            );

            servidor.setSistemaOperacional(
                lerTexto("Novo sistema operacional: ")
            );

            servidor.setMemoriaRam(
                lerInteiroPositivo("Nova memoria RAM (GB): ")
            );

        } else if (ativo instanceof EstacaoTrabalho) {

            EstacaoTrabalho estacao =
                (EstacaoTrabalho) ativo;

            estacao.setProcessador(
                lerTexto("Novo processador: ")
            );

            estacao.setMemoriaRam(
                lerInteiroPositivo("Nova memoria RAM (GB): ")
            );

            estacao.setDepartamento(
                lerTexto("Novo departamento: ")
            );

        } else if (ativo instanceof DispositivoRede) {

            DispositivoRede dispositivo =
                (DispositivoRede) ativo;

            dispositivo.setTipoDispositivo(
                lerTexto("Novo tipo de dispositivo: ")
            );

            dispositivo.setEnderecoIp(
                lerTexto("Novo endereco IP: ")
            );

            dispositivo.setQuantidadePortas(
                lerInteiroPositivo("Nova quantidade de portas: ")
            );
        }

        if (ativoDAO.atualizar(ativo)) {

            System.out.println(
                "\nAtivo atualizado com sucesso!"
            );

        } else {

            System.out.println(
                "Nao foi possivel atualizar o ativo."
            );
        }
    }

    // ==========================================
    // EXCLUIR ATIVO
    // ==========================================

    private void excluirAtivo()
            throws SQLException,
                   EquipamentoNaoEncontradoException {

        System.out.println("\n=== EXCLUSAO DE ATIVO ===");

        int id = lerInteiroPositivo("ID do equipamento: ");

        AtivoTI ativo = localizarAtivo(id);

        System.out.println(ativo);

        if (!confirmar("Confirma a exclusao? (S/N): ")) {

            System.out.println("Exclusao cancelada.");
            return;
        }

        if (ativoDAO.excluir(id)) {

            System.out.println(
                "\nAtivo excluido com sucesso!"
            );

        } else {

            System.out.println("Ativo nao localizado.");
        }
    }

    // ==========================================
    // LOCALIZAR ATIVO COM EXCECAO PERSONALIZADA
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
    // SELECIONAR STATUS DO EQUIPAMENTO
    // ==========================================

    private String selecionarStatus() {

        while (true) {

            System.out.println();
            System.out.println("=== STATUS DO EQUIPAMENTO ===");
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

    // ==========================================
    // VALIDAR NUMEROS INTEIROS POSITIVOS
    // ==========================================

    private int lerInteiroPositivo(String mensagem) {

        while (true) {

            int valor = lerInteiro(mensagem);

            if (valor > 0) {

                return valor;
            }

            System.out.println(
                "Informe um numero inteiro maior que zero."
            );
        }
    }

    // ==========================================
    // VALIDAR CONFIRMACAO S/N
    // ==========================================

    private boolean confirmar(String mensagem) {

        while (true) {

            String resposta = lerTexto(mensagem);

            if (resposta.equalsIgnoreCase("S")) {

                return true;
            }

            if (resposta.equalsIgnoreCase("N")) {

                return false;
            }

            System.out.println(
                "Opcao invalida. Digite S ou N."
            );
        }
    }

    // ==========================================
    // VALIDAR VALORES DECIMAIS
    // ==========================================

    private double lerDouble(String mensagem) {

        while (true) {

            try {

                double valor = Double.parseDouble(
                    lerTexto(mensagem).replace(',', '.')
                );

                if (Double.isFinite(valor) && valor >= 0) {

                    return valor;
                }

                System.out.println(
                    "Informe um valor valido e nao negativo."
                );

            } catch (NumberFormatException erro) {

                System.out.println(
                    "Digite um valor numerico valido."
                );
            }
        }
    }

    // ==========================================
    // VALIDAR DATAS
    // ==========================================

    private LocalDate lerData(String mensagem) {

        while (true) {

            try {

                LocalDate data = LocalDate.parse(
                    lerTexto(mensagem)
                );

                if (!data.isAfter(LocalDate.now())) {

                    return data;
                }

                System.out.println(
                    "A data nao pode estar no futuro."
                );

            } catch (
                java.time.format.DateTimeParseException erro
            ) {

                System.out.println(
                    "Data invalida. Utilize AAAA-MM-DD."
                );
            }
        }
    }
}