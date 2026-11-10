
import dao.ChamadoDAO;
import java.util.List;
import model.ChamadoSuporte;

public class TesteListagemChamados {

    public static void main(String[] args) {

        try {
            ChamadoDAO dao = new ChamadoDAO();

            // Consulta os chamados do notebook ID 101
            List<ChamadoSuporte> chamados =
                    dao.listarPorAtivo(101);

            System.out.println("===== CHAMADOS DO ATIVO 101 =====");

            for (ChamadoSuporte chamado : chamados) {
                System.out.println(chamado);
            }

            System.out.println(
                "Total de chamados: " + chamados.size()
            );

        } catch (Exception erro) {
            System.out.println("Erro: " + erro.getMessage());
        }
    }
}
