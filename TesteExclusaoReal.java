
import dao.AtivoDAO;
import model.AtivoTI;

public class TesteExclusaoReal {

    public static void main(String[] args) {

        try {
            AtivoDAO dao = new AtivoDAO();

            // Cria um ativo temporario para testar
            AtivoTI ativo = new AtivoTI(
                102,
                "Monitor LG",
                "PAT-AULA09-102",
                850.00
            );

            dao.inserir(ativo);

            // Exclui o ativo temporario
            boolean excluido = dao.excluir(102);

            if (excluido) {
                System.out.println("Ativo excluido com sucesso!");
            } else {
                System.out.println("Ativo nao encontrado.");
            }

        } catch (Exception erro) {
            System.out.println("Erro: " + erro.getMessage());
        }
    }
}

