package tests;

import java.time.LocalDate;
import java.time.LocalDateTime;

import model.AtivoTI;
import model.Servidor;
import model.ChamadoSuporte;

import dto.AtivoDTO;
import dto.ChamadoDTO;

public class TesteDTOs {

    public static void main(String[] args) {

        System.out.println("====================================");
        System.out.println(" TECHLAB - TESTE DE DTOs");
        System.out.println("====================================");

        // Criacao do equipamento

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

        // Criacao do chamado

        ChamadoSuporte chamado = new ChamadoSuporte(
                1,
                "Falha no servidor",
                "Servidor apresenta instabilidade.",
                "ALTA",
                LocalDateTime.now(),
                servidor,
                "ABERTO"
        );

        // Conversao das entidades em DTOs

        AtivoDTO ativoDTO = new AtivoDTO(servidor);

        ChamadoDTO chamadoDTO = new ChamadoDTO(chamado);

        System.out.println("\n=== TESTES DO ATIVO DTO ===");

        verificar(
                ativoDTO.getId() == servidor.getId()
                && ativoDTO.getCodigoPatrimonio()
                           .equals(servidor.getCodigoPatrimonio())
                && ativoDTO.getNome().equals(servidor.getNome())
                && ativoDTO.getModelo().equals(servidor.getModelo())
                && ativoDTO.getDataAquisicao()
                           .equals(servidor.getDataAquisicao())
                && ativoDTO.getStatus().equals(servidor.getStatus())
                && ativoDTO.getValor() == servidor.getValor(),
                "Transferencia dos dados do ativo"
        );

        verificar(
                ativoDTO.getTipo().equals("Servidor"),
                "Identificacao do tipo de equipamento"
        );

        verificar(
                ativoDTO.getCustoManutencaoMensal() == 1800.00,
                "Transferencia do custo de manutencao"
        );

        System.out.println("\n=== TESTES DO CHAMADO DTO ===");

        verificar(
                chamadoDTO.getId() == chamado.getId()
                && chamadoDTO.getTitulo().equals(chamado.getTitulo())
                && chamadoDTO.getDescricao()
                             .equals(chamado.getDescricao())
                && chamadoDTO.getPrioridade()
                             .equals(chamado.getPrioridade())
                && chamadoDTO.getDataAbertura()
                             .equals(chamado.getDataAbertura())
                && chamadoDTO.getStatus().equals(chamado.getStatus()),
                "Transferencia dos dados do chamado"
        );

        verificar(
                chamadoDTO.getAtivoId() == servidor.getId()
                && chamadoDTO.getAtivoNome()
                             .equals(servidor.getNome())
                && chamadoDTO.getAtivoPatrimonio()
                             .equals(servidor.getCodigoPatrimonio())
                && chamadoDTO.getAtivoTipo().equals("Servidor"),
                "Associacao entre chamado e ativo"
        );

        System.out.println("\n=== TESTES DE IMUTABILIDADE ===");

        // Alteracao das entidades originais

        servidor.setNome("Servidor Atualizado");
        servidor.setValor(18000.00);
        chamado.setStatus("RESOLVIDO");
        chamado.setPrioridade("MEDIA");

        verificar(
                ativoDTO.getNome().equals("Servidor Principal")
                && ativoDTO.getValor() == 15000.00,
                "Preservacao dos dados do AtivoDTO"
        );

        verificar(
                chamadoDTO.getStatus().equals("ABERTO")
                && chamadoDTO.getPrioridade().equals("ALTA")
                && chamadoDTO.getAtivoNome()
                             .equals("Servidor Principal"),
                "Preservacao dos dados do ChamadoDTO"
        );

        System.out.println("\n=== TESTE DE RELATORIOS ===");

        verificar(
                ativoDTO.toString().contains("PAT-001")
                && chamadoDTO.toString().contains("Falha no servidor")
                && chamadoDTO.toString().contains("Servidor Principal"),
                "Formatacao dos relatorios"
        );

        System.out.println("\n=== RELATORIO DO ATIVO ===");

        System.out.println(ativoDTO);

        System.out.println("\n=== RELATORIO DO CHAMADO ===");

        System.out.println(chamadoDTO);

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