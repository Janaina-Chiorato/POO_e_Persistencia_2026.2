package view;

import java.util.Scanner;

public class MenuPrincipal {

    private final Scanner scanner;

    public MenuPrincipal(Scanner scanner) {

        this.scanner = scanner;
    }

    // ==========================================
    // INICIAR APLICACAO
    // ==========================================

    public void iniciar() {

        boolean executando = true;

        while (executando) {

            exibirMenu();

            int opcao = lerInteiro("Escolha uma opcao: ");

            switch (opcao) {

                case 1:

                    abrirMenuAtivos();
                    break;

                case 2:

                    abrirMenuChamados();
                    break;

                case 3:

                    abrirMenuRelatorios();
                    break;

                case 4:

                    abrirMenuPersistencia();
                    break;

                case 0:

                    executando = false;

                    System.out.println(
                        "\nTechLab encerrado com sucesso."
                    );

                    System.out.println(
                        "Obrigado por utilizar o sistema!"
                    );

                    break;

                default:

                    System.out.println(
                        "\nOpcao invalida. Tente novamente."
                    );
            }
        }
    }

    // ==========================================
    // EXIBIR MENU PRINCIPAL
    // ==========================================

    private void exibirMenu() {

        System.out.println();
        System.out.println("============================================");
        System.out.println("   TECHLAB INVENTORY & SERVICE DESK");
        System.out.println("============================================");
        System.out.println("   Sistema de Gestao de Ativos de TI");
        System.out.println("--------------------------------------------");
        System.out.println("  1. Gerenciamento de Ativos");
        System.out.println("  2. Gerenciamento de Chamados");
        System.out.println("  3. Consultas e Ordenacao");
        System.out.println("  4. Persistencia e Backup CSV");
        System.out.println("  0. Sair");
        System.out.println("============================================");
    }

    // ==========================================
    // MODULOS DA APLICACAO
    // ==========================================

    private void abrirMenuAtivos() {

    MenuAtivos menuAtivos = new MenuAtivos(scanner);

    menuAtivos.iniciar();
    }

    private void abrirMenuChamados() {

    MenuChamados menuChamados = new MenuChamados(scanner);

    menuChamados.iniciar();
    }

    private void abrirMenuRelatorios() {

    MenuConsultas menuConsultas = new MenuConsultas(scanner);

    menuConsultas.iniciar();
    }

   private void abrirMenuPersistencia() {

    MenuPersistencia menuPersistencia =
        new MenuPersistencia(scanner);

    menuPersistencia.iniciar();
    }

    // ==========================================
    // VALIDACAO DE ENTRADA
    // ==========================================

    private int lerInteiro(String mensagem) {

        while (true) {

            System.out.print(mensagem);

            if (!scanner.hasNextLine()) {

                return 0;
            }

            String entrada = scanner.nextLine().trim();

            try {

                return Integer.parseInt(entrada);

            } catch (NumberFormatException erro) {

                System.out.println(
                    "Entrada invalida. Digite um numero inteiro."
                );
            }
        }
    }
}