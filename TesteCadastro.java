
import dao.AtivoDAO;
import model.AtivoTI;

public class TesteCadastro {

    public static void main(String[] args) {

        try {
            AtivoTI ativo = new AtivoTI(
                101,
                "Notebook Dell",
                "PAT-AULA09-101",
                3500.00
            );

            AtivoDAO dao = new AtivoDAO();

            dao.inserir(ativo);

            System.out.println("Teste de cadastro concluido!");

        } catch (Exception erro) {
            System.out.println("Erro: " + erro.getMessage());
        }
    }
}
