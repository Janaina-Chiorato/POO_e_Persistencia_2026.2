package tests;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dao.ConexaoBanco;
import interfaces.IConexaoBD;
import exceptions.EquipamentoNaoEncontradoException;
import exceptions.FalhaConexaoBDException;

public class TesteContratosResiliencia {

    public static void main(String[] args) throws SQLException {

        System.out.println("====================================");
        System.out.println(" TECHLAB - CONTRATOS E RESILIENCIA");
        System.out.println("====================================");

        testarConexao();
        testarEquipamentoNaoEncontrado();
        testarFalhaConexao();

        System.out.println("\n====================================");
        System.out.println(" TODOS OS TESTES FORAM APROVADOS");
        System.out.println("====================================");
    }

    private static void testarConexao() throws SQLException {

        System.out.println("\n=== TESTE DE CONEXAO ===");

        IConexaoBD banco = new ConexaoBanco();

        Connection conexao = banco.obterConexao();

        try (Connection recurso = conexao) {

            verificar(
                !recurso.isClosed(),
                "Conexao com SQLite estabelecida"
            );

            try (Statement stmt = recurso.createStatement();
                 ResultSet rs = stmt.executeQuery(
                     "PRAGMA foreign_keys")) {

                verificar(
                    rs.next() && rs.getInt(1) == 1,
                    "Chaves estrangeiras ativadas"
                );
            }
        }

        verificar(
            conexao.isClosed(),
            "Conexao encerrada corretamente"
        );
    }

    private static void testarEquipamentoNaoEncontrado() {

        System.out.println("\n=== TESTE DE EQUIPAMENTO ===");

        try {

            throw new EquipamentoNaoEncontradoException(999);

        } catch (EquipamentoNaoEncontradoException erro) {

            verificar(
                erro.getMessage().contains("999"),
                "Excecao de equipamento nao encontrado"
            );

            System.out.println("Mensagem: " + erro.getMessage());
        }
    }

    private static void testarFalhaConexao() throws SQLException {

        System.out.println("\n=== TESTE DE FALHA DE CONEXAO ===");

        SQLException causaOriginal =
            new SQLException("Falha simulada no SQLite");

        IConexaoBD bancoComFalha = () -> {

            throw new FalhaConexaoBDException(
                "Falha ao conectar ao banco de dados TechLab.",
                causaOriginal
            );
        };

        try (Connection conexao = bancoComFalha.obterConexao()) {

            throw new AssertionError(
                "A excecao esperada nao foi lancada."
            );

        } catch (FalhaConexaoBDException erro) {

            verificar(
                erro.getCause() == causaOriginal,
                "Falha de conexao capturada e causa preservada"
            );

            System.out.println("Mensagem: " + erro.getMessage());

            System.out.println(
                "Causa original: " + erro.getCause().getMessage()
            );
        }
    }

    private static void verificar(
            boolean condicao, String descricao) {

        if (!condicao) {

            throw new AssertionError(
                "[FALHA] " + descricao
            );
        }

        System.out.println("[OK] " + descricao);
    }
}