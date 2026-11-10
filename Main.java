

import dao.AtivoDAO;
import dao.ChamadoDAO;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import model.ChamadoSuporte;

public class Main {

    public static void main(String[] args) {

        Scanner entrada = new Scanner(System.in);

        AtivoDAO ativoDAO = new AtivoDAO();
        ChamadoDAO chamadoDAO = new ChamadoDAO();

        System.out.println("====================================");
        System.out.println("   TECHLAB - SERVICE DESK AULA 09");
        System.out.println("====================================");

        // ==========================================
        // EXERCICIO 1 - VALIDACAO DE EXCLUSAO
        // ==========================================

        System.out.println("\n=== EXCLUSAO DE ATIVO ===");
        System.out.print("Digite o ID do ativo que deseja excluir: ");

        if (!entrada.hasNextInt()) {
            System.out.println(
                "Aviso: Informe um ID numerico valido."
            );
            entrada.close();
            return;
        }

        int id = entrada.nextInt();

        try {

            boolean excluido = ativoDAO.excluir(id);

            if (excluido) {
                System.out.println(
                    "Ativo excluido com sucesso."
                );
            } else {
                System.out.println(
                    "Aviso: Nenhum ativo foi localizado com o ID informado. Exclusão não efetuada."
                );
            }

        } catch (SQLException erro) {
            System.out.println(
                "Erro ao excluir ativo: " + erro.getMessage()
            );
        }

        // ==========================================
        // EXERCICIO 2 - CONSULTA COMPLETA COM JOIN
        // ==========================================

        System.out.println("\n====================================");
        System.out.println("  RELATORIO GERAL DE CHAMADOS");
        System.out.println("====================================");

        try {

            List<ChamadoSuporte> chamados =
                    chamadoDAO.listarTodosComAtivo();

            if (chamados.isEmpty()) {

                System.out.println(
                    "Nenhum chamado cadastrado."
                );

            } else {

                for (ChamadoSuporte chamado : chamados) {

                    System.out.println(
                        "\nChamado ID: " + chamado.getId()
                    );

                    System.out.println(
                        "Descricao: " + chamado.getDescricao()
                    );

                    System.out.println(
                        "Status: " + chamado.getStatus()
                    );

                    System.out.println(
                        "Ativo ID: " + chamado.getAtivo().getId()
                    );

                    System.out.println(
                        "Ativo: " + chamado.getAtivo().getNome()
                    );

                    System.out.println(
                        "Patrimonio: " +
                        chamado.getAtivo().getPatrimonio()
                    );

                    System.out.printf(
                        "Valor: R$ %.2f%n",
                        chamado.getAtivo().getValor()
                    );

                    System.out.println(
                        "------------------------------------"
                    );
                }
            }

        } catch (SQLException erro) {

            System.out.println(
                "Erro ao consultar chamados: " +
                erro.getMessage()
            );
        }

        entrada.close();

        System.out.println("\n====================================");
        System.out.println("      FIM DA EXECUCAO");
        System.out.println("====================================");
    }
}
