
import dao.AtivoDAO;
import java.sql.SQLException;

public class TesteChaveEstrangeira {

    public static void main(String[] args) {

        AtivoDAO dao = new AtivoDAO();

        try {
            boolean excluido = dao.excluir(101);

            if (excluido) {
                System.out.println("Ativo excluido!");
            } else {
                System.out.println("Ativo nao encontrado.");
            }

        } catch (SQLException erro) {
            System.out.println(
                "Exclusao bloqueada pelo banco: "
                + erro.getMessage()
            );
        }
    }
}
