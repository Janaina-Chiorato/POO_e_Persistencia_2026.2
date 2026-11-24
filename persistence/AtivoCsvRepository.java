package persistence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

import model.AtivoTI;
import model.Servidor;
import model.EstacaoTrabalho;
import model.DispositivoRede;

public class AtivoCsvRepository {

    private final Path arquivo;

    private static final String CABECALHO =
            "id;patrimonio;nome;modelo;data_aquisicao;"
            + "status;valor;tipo;atributo1;atributo2;atributo3";

    public AtivoCsvRepository(String caminho) {

        this.arquivo = Path.of(caminho);
    }

    // Salvar todos os ativos no arquivo CSV
    public void salvarTodos(List<AtivoTI> ativos)
            throws IOException {

        List<String> linhas = new ArrayList<>();

        linhas.add(CABECALHO);

        Set<Integer> ids = new HashSet<>();
        Set<String> patrimonios = new HashSet<>();

        for (AtivoTI ativo : ativos) {

            if (!ids.add(ativo.getId())) {

                throw new IllegalArgumentException(
                    "ID duplicado: " + ativo.getId()
                );
            }

            if (!patrimonios.add(
                    ativo.getCodigoPatrimonio())) {

                throw new IllegalArgumentException(
                    "Patrimonio duplicado: "
                    + ativo.getCodigoPatrimonio()
                );
            }

            linhas.add(converterParaCsv(ativo));
        }

        Path pasta = arquivo.getParent();

        if (pasta != null) {

            Files.createDirectories(pasta);
        }

        Files.write(
                arquivo,
                linhas,
                StandardCharsets.UTF_8
        );
    }

    // Converter um ativo em registro CSV
    private String converterParaCsv(AtivoTI ativo) {

        String tipo;
        String atributo1;
        String atributo2;
        String atributo3;

        if (ativo instanceof Servidor) {

            Servidor servidor = (Servidor) ativo;

            tipo = "SERVIDOR";

            atributo1 = servidor.getIpEstatico();
            atributo2 = servidor.getSistemaOperacional();
            atributo3 = String.valueOf(
                    servidor.getMemoriaRam()
            );

        } else if (ativo instanceof EstacaoTrabalho) {

            EstacaoTrabalho estacao =
                    (EstacaoTrabalho) ativo;

            tipo = "ESTACAO_TRABALHO";

            atributo1 = estacao.getProcessador();
            atributo2 = String.valueOf(
                    estacao.getMemoriaRam()
            );
            atributo3 = estacao.getDepartamento();

        } else if (ativo instanceof DispositivoRede) {

            DispositivoRede dispositivo =
                    (DispositivoRede) ativo;

            tipo = "DISPOSITIVO_REDE";

            atributo1 = dispositivo.getTipoDispositivo();
            atributo2 = dispositivo.getEnderecoIp();
            atributo3 = String.valueOf(
                    dispositivo.getQuantidadePortas()
            );

        } else {

            throw new IllegalArgumentException(
                "Tipo de ativo nao suportado."
            );
        }

        return CsvUtil.criarLinha(
                String.valueOf(ativo.getId()),
                ativo.getCodigoPatrimonio(),
                ativo.getNome(),
                ativo.getModelo(),
                ativo.getDataAquisicao().toString(),
                ativo.getStatus(),
                String.valueOf(ativo.getValor()),
                tipo,
                atributo1,
                atributo2,
                atributo3
        );
    }

    // Recuperar todos os ativos armazenados
    public List<AtivoTI> carregarTodos()
            throws IOException {

        List<AtivoTI> ativos = new ArrayList<>();

        if (!Files.exists(arquivo)) {

            return ativos;
        }

        List<String> linhas = Files.readAllLines(
                arquivo,
                StandardCharsets.UTF_8
        );

        if (linhas.isEmpty()) {

            return ativos;
        }

        if (!CABECALHO.equals(linhas.get(0))) {

            throw new IOException(
                "Cabecalho CSV invalido."
            );
        }

        StringBuilder registro = new StringBuilder();

        Set<Integer> ids = new HashSet<>();
        Set<String> patrimonios = new HashSet<>();

        for (int i = 1; i < linhas.size(); i++) {

            if (registro.length() > 0) {

                registro.append("\n");
            }

            registro.append(linhas.get(i));

            // Aguarda o fechamento das aspas
            // em registros com varias linhas
            if (!registroCompleto(registro.toString())) {

                continue;
            }

            if (!registro.toString().isBlank()) {

                try {

                    AtivoTI ativo = converterParaAtivo(
                        CsvUtil.lerLinha(registro.toString())
                    );

                    if (!ids.add(ativo.getId())
                            || !patrimonios.add(
                                ativo.getCodigoPatrimonio())) {

                        throw new IllegalArgumentException(
                            "Registro duplicado."
                        );
                    }

                    ativos.add(ativo);

                } catch (RuntimeException erro) {

                    throw new IOException(
                        "Registro CSV invalido na linha "
                        + (i + 1),
                        erro
                    );
                }
            }

            registro.setLength(0);
        }

        if (registro.length() > 0) {

            throw new IOException(
                "Arquivo CSV possui registro incompleto."
            );
        }

        return ativos;
    }

    // Verificar se todas as aspas foram fechadas
    private boolean registroCompleto(String registro) {

        boolean entreAspas = false;

        for (int i = 0; i < registro.length(); i++) {

            if (registro.charAt(i) == '"') {

                if (entreAspas
                        && i + 1 < registro.length()
                        && registro.charAt(i + 1) == '"') {

                    i++;

                } else {

                    entreAspas = !entreAspas;
                }
            }
        }

        return !entreAspas;
    }

    // Reconstruir o objeto a partir do CSV
    private AtivoTI converterParaAtivo(
            List<String> campos) {

        if (campos.size() != 11) {

            throw new IllegalArgumentException(
                "Quantidade de campos invalida."
            );
        }

        int id = Integer.parseInt(campos.get(0));

        String patrimonio = campos.get(1);
        String nome = campos.get(2);
        String modelo = campos.get(3);

        LocalDate dataAquisicao =
                LocalDate.parse(campos.get(4));

        String status = campos.get(5);

        double valor =
                Double.parseDouble(campos.get(6));

        String tipo = campos.get(7);

        switch (tipo) {

            case "SERVIDOR":

                return new Servidor(
                    id,
                    patrimonio,
                    nome,
                    modelo,
                    dataAquisicao,
                    status,
                    valor,
                    campos.get(8),
                    campos.get(9),
                    Integer.parseInt(campos.get(10))
                );

            case "ESTACAO_TRABALHO":

                return new EstacaoTrabalho(
                    id,
                    patrimonio,
                    nome,
                    modelo,
                    dataAquisicao,
                    status,
                    valor,
                    campos.get(8),
                    Integer.parseInt(campos.get(9)),
                    campos.get(10)
                );

            case "DISPOSITIVO_REDE":

                return new DispositivoRede(
                    id,
                    patrimonio,
                    nome,
                    modelo,
                    dataAquisicao,
                    status,
                    valor,
                    campos.get(8),
                    campos.get(9),
                    Integer.parseInt(campos.get(10))
                );

            default:

                throw new IllegalArgumentException(
                    "Tipo de ativo desconhecido: " + tipo
                );
        }
    }
}