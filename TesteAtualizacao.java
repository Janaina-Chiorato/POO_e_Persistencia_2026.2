
import dao.AtivoDAO;
import model.AtivoTI;

public class TesteAtualizacao {

    public static void main(String[] args) {

        try {
            AtivoDAO dao = new AtivoDAO();

            // Mesmo ID do notebook cadastrado
            AtivoTI ativo = new AtivoTI(
                101,
                "Notebook Dell Latitude",
                "PAT-AULA09-101",
                4200.00
            );

            boolean atualizado = dao.atualizar(ativo);

            if (atualizado) {
                System.out.println("Ativo atualizado com sucesso!");
            } else {
                System.out.println("Ativo nao encontrado.");
            }

        } catch (Exception erro) {
            System.out.println("Erro: " + erro.getMessage());
        }
    }
}
