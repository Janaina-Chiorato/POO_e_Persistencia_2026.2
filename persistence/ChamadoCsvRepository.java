package persistence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;

import model.AtivoTI;
import model.ChamadoSuporte;

import exceptions.EquipamentoNaoEncontradoException;

public class ChamadoCsvRepository {

    private final Path arquivo;

    private static final String CABECALHO =
            "id;titulo;descricao;prioridade;"
            + "data_abertura;status;ativo_id";

    public ChamadoCsvRepository(String caminho) {

        this.arquivo = Path.of(caminho);
    }

    // Salvar todos os chamados no arquivo CSV
    public void salvarTodos(
            List<ChamadoSuporte> chamados)
            throws IOException {

        List<String> linhas = new ArrayList<>();

        linhas.add(CABECALHO);

        Set<Integer> ids = new HashSet<>();

        for (ChamadoSuporte chamado : chamados) {

            if (!ids.add(chamado.getId())) {

                throw new IllegalArgumentException(
                    "ID de chamado duplicado: "
                    + chamado.getId()
                );
            }

            linhas.add(converterParaCsv(chamado));
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

    // Converter um chamado em registro CSV
    private String converterParaCsv(
            ChamadoSuporte chamado) {

        return CsvUtil.criarLinha(
                String.valueOf(chamado.getId()),
                chamado.getTitulo(),
                chamado.getDescricao(),
                chamado.getPrioridade(),
                chamado.getDataAbertura().toString(),
                chamado.getStatus(),
                String.valueOf(
                    chamado.getAtivoRelacionado().getId()
                )
        );
    }

    // Recuperar todos os chamados armazenados
    public List<ChamadoSuporte> carregarTodos(
            List<AtivoTI> ativos)
            throws IOException,
                   EquipamentoNaoEncontradoException {

        List<ChamadoSuporte> chamados =
                new ArrayList<>();

        if (!Files.exists(arquivo)) {

            return chamados;
        }

        // Indexar os ativos pelo ID
        Map<Integer, AtivoTI> ativosPorId =
                new HashMap<>();

        for (AtivoTI ativo : ativos) {

            if (ativosPorId.putIfAbsent(
                    ativo.getId(), ativo) != null) {

                throw new IllegalArgumentException(
                    "ID de ativo duplicado: "
                    + ativo.getId()
                );
            }
        }

        List<String> linhas = Files.readAllLines(
                arquivo,
                StandardCharsets.UTF_8
        );

        if (linhas.isEmpty()) {

            return chamados;
        }

        if (!CABECALHO.equals(linhas.get(0))) {

            throw new IOException(
                "Cabecalho CSV de chamados invalido."
            );
        }

        Set<Integer> ids = new HashSet<>();

        StringBuilder registro = new StringBuilder();

        for (int i = 1; i < linhas.size(); i++) {

            if (registro.length() > 0) {

                registro.append("\n");
            }

            registro.append(linhas.get(i));

            if (!registroCompleto(registro.toString())) {

                continue;
            }

            if (!registro.toString().isBlank()) {

                try {

                    ChamadoSuporte chamado =
                        converterParaChamado(
                            CsvUtil.lerLinha(
                                registro.toString()
                            ),
                            ativosPorId
                        );

                    if (!ids.add(chamado.getId())) {

                        throw new IllegalArgumentException(
                            "ID de chamado duplicado."
                        );
                    }

                    chamados.add(chamado);

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

        return chamados;
    }

    // Verificar o fechamento das aspas
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

    // Reconstruir o chamado a partir do CSV
    private ChamadoSuporte converterParaChamado(
            List<String> campos,
            Map<Integer, AtivoTI> ativosPorId)
            throws EquipamentoNaoEncontradoException {

        if (campos.size() != 7) {

            throw new IllegalArgumentException(
                "Quantidade de campos invalida."
            );
        }

        int id = Integer.parseInt(campos.get(0));

        String titulo = campos.get(1);
        String descricao = campos.get(2);
        String prioridade = campos.get(3);

        LocalDateTime dataAbertura =
                LocalDateTime.parse(campos.get(4));

        String status = campos.get(5);

        int ativoId =
                Integer.parseInt(campos.get(6));

        AtivoTI ativo = ativosPorId.get(ativoId);

        if (ativo == null) {

            throw new EquipamentoNaoEncontradoException(
                ativoId
            );
        }

        return new ChamadoSuporte(
                id,
                titulo,
                descricao,
                prioridade,
                dataAbertura,
                ativo,
                status
        );
    }
}