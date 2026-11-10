
import dao.ChamadoDAO;
import model.AtivoTI;
import model.ChamadoSuporte;

public class TesteChamado {

    public static void main(String[] args) {

        try {
            // Equipamento ja cadastrado no SQLite
            AtivoTI ativo = new AtivoTI(
                101,
                "Notebook Dell Latitude",
                "PAT-AULA09-101",
                4200.00
            );

            // Chamado relacionado ao equipamento
            ChamadoSuporte chamado = new ChamadoSuporte(
                1,
                "Notebook nao liga",
                "ABERTO",
                ativo
            );

            ChamadoDAO dao = new ChamadoDAO();

            dao.salvar(chamado);

            System.out.println("Teste de chamado concluido!");

        } catch (Exception erro) {
            System.out.println("Erro: " + erro.getMessage());
        }
    }
}
