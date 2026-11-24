package tests;

import model.AtivoTI;
import model.Servidor;
import model.EstacaoTrabalho;
import model.DispositivoRede;
import model.ChamadoSuporte;
import interfaces.INotificavel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TesteModelagemDominio {

    public static void main(String[] args) {

        System.out.println("====================================");
        System.out.println(" TECHLAB - TESTE DE MODELAGEM");
        System.out.println("====================================");

        // 1. Criacao dos equipamentos

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
                "ATIVO",
                2800.00,
                "Switch",
                "192.168.1.1",
                24
        );

        // 2. Colecao generica e polimorfismo

        List<AtivoTI> ativos = new ArrayList<>();

        ativos.add(servidor);
        ativos.add(estacao);
        ativos.add(dispositivo);

        System.out.println("\n=== INVENTARIO DE ATIVOS ===");

        for (AtivoTI ativo : ativos) {

            System.out.println(ativo);

            System.out.printf(
                    "Custo de manutencao: R$ %.2f%n",
                    ativo.calcularCustoManutencaoMensal()
            );

            System.out.println("------------------------------------");
        }

        // 3. Associacao entre chamado e ativo

        ChamadoSuporte chamado = new ChamadoSuporte(
                1,
                "Falha no servidor",
                "Servidor apresenta instabilidade.",
                "ALTA",
                LocalDateTime.now(),
                servidor,
                "ABERTO"
        );

        System.out.println("\n=== CHAMADO DE SUPORTE ===");
        System.out.println(chamado);

        // 4. Teste da interface

        System.out.println("\n=== TESTE DE NOTIFICACAO ===");

        INotificavel notificavel = chamado;

        notificavel.enviarAlerta(
                "Novo chamado registrado com sucesso."
        );

        // 5. Teste de encerramento do chamado

        chamado.fecharChamado();

        verificar(
                chamado.getStatus().equals("FECHADO"),
                "Encerramento do chamado"
        );

        // 6. Testes de encapsulamento

        System.out.println("\n=== TESTES DE VALIDACAO ===");

        testarValidacao(
                () -> servidor.setValor(-500),
                "Bloqueio de valor negativo"
        );

        testarValidacao(
                () -> servidor.setCodigoPatrimonio(""),
                "Bloqueio de patrimonio vazio"
        );

        testarValidacao(
                () -> chamado.setPrioridade("INVALIDA"),
                "Bloqueio de prioridade invalida"
        );

        testarValidacao(
                () -> chamado.setAtivoRelacionado(null),
                "Bloqueio de chamado sem ativo"
        );

        // 7. Verificacao do polimorfismo

        verificar(
                servidor.calcularCustoManutencaoMensal() == 1800.0,
                "Calculo de manutencao do servidor"
        );

        verificar(
                estacao.calcularCustoManutencaoMensal() == 160.0,
                "Calculo de manutencao da estacao"
        );

        verificar(
                dispositivo.calcularCustoManutencaoMensal() == 290.0,
                "Calculo de manutencao do dispositivo"
        );

        verificar(
                chamado.getAtivoRelacionado() == servidor,
                "Associacao entre chamado e ativo"
        );

        System.out.println("\n====================================");
        System.out.println(" TODOS OS TESTES FORAM APROVADOS");
        System.out.println("====================================");
    }

    private static void verificar(
            boolean condicao, String teste) {

        if (!condicao) {
            throw new AssertionError(
                    "[FALHA] " + teste
            );
        }

        System.out.println("[OK] " + teste);
    }

    private static void testarValidacao(
            Runnable acao, String teste) {

        try {
            acao.run();

        } catch (IllegalArgumentException erro) {

            System.out.println("[OK] " + teste);
            System.out.println("Mensagem: " + erro.getMessage());

            return;
        }

        throw new AssertionError(
                "[FALHA] " + teste
        );
    }
}