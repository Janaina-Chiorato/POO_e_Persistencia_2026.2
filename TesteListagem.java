
import dao.AtivoDAO;
import java.util.List;
import model.AtivoTI;

public class TesteListagem {

    public static void main(String[] args) {

        try {
            AtivoDAO dao = new AtivoDAO();

            List<AtivoTI> ativos = dao.listar();

            System.out.println("===== ATIVOS CADASTRADOS =====");

            for (AtivoTI ativo : ativos) {
                System.out.println(ativo);
            }

            System.out.println("Total de ativos: " + ativos.size());

        } catch (Exception erro) {
            System.out.println("Erro: " + erro.getMessage());
        }
    }
}
