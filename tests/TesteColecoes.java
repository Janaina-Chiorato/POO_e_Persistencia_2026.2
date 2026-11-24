package tests;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import model.AtivoTI;
import model.Servidor;
import model.EstacaoTrabalho;
import model.DispositivoRede;
import model.ChamadoSuporte;

import service.GerenciadorColecao;

public class TesteColecoes {

    public static void main(String[] args) {

        System.out.println("====================================");
        System.out.println(" TECHLAB - COLECOES E GENERICS");
        System.out.println("====================================");

        GerenciadorColecao<AtivoTI> inventario =
                new GerenciadorColecao<>();

        // Criacao dos ativos

        AtivoTI servidor = new Servidor(
                1,
                "PAT-001",
                "Servidor Principal",
                "Dell PowerEdge",
                LocalDate.of(2025, 1, 10),
                "ATIVO",
                15000.00,
                "192.168.1.10",
                "Linux",
                32
        );

        AtivoTI estacao = new EstacaoTrabalho(
                2,
                "PAT-002",
                "Computador Administrativo",
                "Dell OptiPlex",
                LocalDate.of(2025, 3, 15),
                "ATIVO",
                4500.00,
                "Intel Core i5",
                16,
                "Administrativo"
        );

        AtivoTI dispositivo = new DispositivoRede(
                3,
                "PAT-003",
                "Switch Principal",
                "Cisco Catalyst",
                LocalDate.of(2025, 5, 20),
                "INATIVO",
                2800.00,
                "Switch",
                "192.168.1.1",
                24
        );

        System.out.println("\n=== TESTE DE CADASTRO ===");

        verificar(
                inventario.estaVazia(),
                "Colecao inicialmente vazia"
        );

        inventario.adicionar(servidor);
        inventario.adicionar(estacao);
        inventario.adicionar(dispositivo);

        verificar(
                inventario.quantidade() == 3,
                "Cadastro de tres ativos"
        );

        System.out.println("\n=== TESTE DE BUSCA ===");

        verificar(
                inventario.buscar(a -> a.getId() == 2)
                        .orElse(null) == estacao,
                "Busca de ativo pelo ID"
        );

        verificar(
                inventario.buscar(a -> a.getId() == 999)
                        .isEmpty(),
                "Busca de ativo inexistente"
        );

        verificar(
                inventario.existe(
                    a -> a.getCodigoPatrimonio()
                          .equals("PAT-001")
                ),
                "Busca pelo codigo de patrimonio"
        );

        System.out.println("\n=== TESTE DE FILTRAGEM ===");

        List<AtivoTI> ativos = inventario.filtrar(
                a -> a.getStatus().equals("ATIVO")
        );

        verificar(
                ativos.size() == 2,
                "Filtragem de equipamentos ativos"
        );

        verificar(
                inventario.contar(
                    a -> a.getStatus().equals("INATIVO")
                ) == 1,
                "Contagem de equipamentos inativos"
        );

        System.out.println("\n=== TESTE DE ORDENACAO ===");

        List<AtivoTI> crescente = inventario.ordenar(
                Comparator.comparingDouble(AtivoTI::getValor)
        );

        verificar(
                crescente.get(0) == dispositivo
                && crescente.get(1) == estacao
                && crescente.get(2) == servidor,
                "Ordenacao crescente por valor"
        );

        List<AtivoTI> decrescente = inventario.ordenar(
                Comparator.comparingDouble(AtivoTI::getValor)
                          .reversed()
        );

        verificar(
                decrescente.get(0) == servidor
                && decrescente.get(1) == estacao
                && decrescente.get(2) == dispositivo,
                "Ordenacao decrescente por valor"
        );

        verificar(
                inventario.listarTodos().get(0) == servidor
                && inventario.listarTodos().get(1) == estacao
                && inventario.listarTodos().get(2) == dispositivo,
                "Preservacao da ordem original"
        );

        System.out.println("\n=== TESTE DE ENCAPSULAMENTO ===");

        List<AtivoTI> copia = inventario.listarTodos();

        copia.clear();

        verificar(
                inventario.quantidade() == 3,
                "Protecao da estrutura da colecao original"
        );

        System.out.println("\n=== TESTE DE REMOCAO ===");

        verificar(
                inventario.remover(dispositivo)
                && inventario.quantidade() == 2,
                "Remocao de um equipamento"
        );

        verificar(
                inventario.removerSe(a -> a.getId() == 2)
                && inventario.quantidade() == 1,
                "Remocao por criterio"
        );

        System.out.println("\n=== TESTE DE GENERICS ===");

        GerenciadorColecao<ChamadoSuporte> chamados =
                new GerenciadorColecao<>();

        ChamadoSuporte chamado = new ChamadoSuporte(
                1,
                "Falha no servidor",
                "Servidor apresenta instabilidade.",
                "ALTA",
                LocalDateTime.now(),
                servidor,
                "ABERTO"
        );

        chamados.adicionar(chamado);

        verificar(
                chamados.quantidade() == 1,
                "Colecao generica de chamados"
        );

        verificar(
                chamados.contar(
                    c -> c.getStatus().equals("ABERTO")
                ) == 1,
                "Contagem de chamados abertos"
        );

        System.out.println("\n=== TESTE DE LIMPEZA ===");

        inventario.limpar();

        verificar(
                inventario.estaVazia(),
                "Limpeza da colecao"
        );

        System.out.println("\n====================================");
        System.out.println(" TODOS OS TESTES FORAM APROVADOS");
        System.out.println("====================================");
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