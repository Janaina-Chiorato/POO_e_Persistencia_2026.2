
import dao.AtivoDAO;

public class TesteExclusao {

    public static void main(String[] args) {

        try {
            AtivoDAO dao = new AtivoDAO();

            // Testando a exclusao de um ID inexistente
            boolean excluido = dao.excluir(999);

            if (excluido) {
                System.out.println("Ativo excluido com sucesso!");
            } else {
                System.out.println(
                    "Aviso: Nenhum ativo foi localizado com o ID informado. Exclusao nao efetuada."
                );
            }

        } catch (Exception erro) {
            System.out.println("Erro: " + erro.getMessage());
        }
    }
}
