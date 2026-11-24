package persistence;

import java.util.ArrayList;
import java.util.List;

public final class CsvUtil {

    private static final char SEPARADOR = ';';

    // Impede a criacao de objetos desta classe utilitaria
    private CsvUtil() {
    }

    // Formata um campo para armazenamento no CSV
    public static String escapar(String valor) {

        if (valor == null) {
            return "";
        }

        if (valor.contains(";")
                || valor.contains("\"")
                || valor.contains("\n")
                || valor.contains("\r")) {

            return "\"" + valor.replace("\"", "\"\"") + "\"";
        }

        return valor;
    }

    // Transforma varios campos em um registro CSV
    public static String criarLinha(String... campos) {

        StringBuilder linha = new StringBuilder();

        for (int i = 0; i < campos.length; i++) {

            if (i > 0) {
                linha.append(SEPARADOR);
            }

            linha.append(escapar(campos[i]));
        }

        return linha.toString();
    }

    // Separa os campos de um registro CSV completo
    public static List<String> lerLinha(String linha) {

        List<String> campos = new ArrayList<>();

        StringBuilder campo = new StringBuilder();

        boolean entreAspas = false;

        for (int i = 0; i < linha.length(); i++) {

            char caractere = linha.charAt(i);

            if (caractere == '"') {

                if (entreAspas
                        && i + 1 < linha.length()
                        && linha.charAt(i + 1) == '"') {

                    campo.append('"');
                    i++;

                } else {

                    entreAspas = !entreAspas;
                }

            } else if (caractere == SEPARADOR
                    && !entreAspas) {

                campos.add(campo.toString());

                campo.setLength(0);

            } else {

                campo.append(caractere);
            }
        }

        if (entreAspas) {

            throw new IllegalArgumentException(
                "Registro CSV invalido: aspas nao fechadas."
            );
        }

        campos.add(campo.toString());

        return campos;
    }
}