
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AtivoDAO {

        // Método responsável por salvar um ativo no banco de dados
    public void salvar(AtivoTI ativo) throws SQLException {

        String sql = """
            INSERT INTO ativos
            (id, patrimonio, modelo, status, categoria,
            ip_estatico, sistema_operacional, memoria_ram)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conexao = ConexaoBanco.obterConexao();
            PreparedStatement comando = conexao.prepareStatement(sql)) {

            // Atributos herdados da classe AtivoTI
            comando.setInt(1, ativo.getId());
            comando.setString(2, ativo.getPatrimonio());
            comando.setString(3, ativo.getModelo());
            comando.setString(4, ativo.getStatus());
            comando.setString(5, ativo.getClass().getSimpleName());

            // Verifica se o ativo é um Servidor
            if (ativo instanceof Servidor servidor) {

                comando.setString(6, servidor.getIpEstatico());
                comando.setString(7, servidor.getSistemaOperacional());
                comando.setInt(8, servidor.getMemoriaRam());

            } else {

                comando.setNull(6, java.sql.Types.VARCHAR);
                comando.setNull(7, java.sql.Types.VARCHAR);
                comando.setNull(8, java.sql.Types.INTEGER);
            }

            // Executa o cadastro no banco
            comando.executeUpdate();

            System.out.println("Ativo cadastrado com sucesso!");

        } catch (SQLException erro) {

            System.err.println("Erro ao salvar ativo: "
                    + erro.getMessage());

            throw erro;
        }
    }
        // Método responsável por listar todos os ativos cadastrados
    public List<AtivoTI> listarTodos() throws SQLException {

        // Lista que armazenará os equipamentos encontrados
        List<AtivoTI> ativos = new ArrayList<>();

        // Consulta SQL
        String sql = "SELECT * FROM ativos";

        try (Connection conexao = ConexaoBanco.obterConexao();
            PreparedStatement comando = conexao.prepareStatement(sql);
            ResultSet resultado = comando.executeQuery()) {

            // Percorre os registros retornados pelo banco
            while (resultado.next()) {

                int id = resultado.getInt("id");
                String patrimonio = resultado.getString("patrimonio");
                String modelo = resultado.getString("modelo");
                String status = resultado.getString("status");
                String categoria = resultado.getString("categoria");

                // Identifica os registros da categoria Servidor
                if ("Servidor".equals(categoria)) {

                    String ipEstatico = resultado.getString("ip_estatico");

                    String sistemaOperacional =
                            resultado.getString("sistema_operacional");

                    int memoriaRam = resultado.getInt("memoria_ram");

                    // Reconstrói o objeto Servidor
                    Servidor servidor = new Servidor(
                            id,
                            patrimonio,
                            modelo,
                            status,
                            ipEstatico,
                            sistemaOperacional,
                            memoriaRam
                    );

                    // Adiciona o servidor à lista
                    ativos.add(servidor);
                }
            }
        }

        return ativos;
    }
}