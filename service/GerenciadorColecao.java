package service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GerenciadorColecao<T> {

    private final List<T> registros;

    public GerenciadorColecao() {

        this.registros = new ArrayList<>();
    }

    // Adicionar um registro
    public void adicionar(T registro) {

        Objects.requireNonNull(
            registro,
            "O registro nao pode ser nulo."
        );

        registros.add(registro);
    }

    // Listar todos os registros
    public List<T> listarTodos() {

        return new ArrayList<>(registros);
    }

    // Remover um registro
    public boolean remover(T registro) {

        return registros.remove(registro);
    }

    // Remover registros por uma condicao
    public boolean removerSe(Predicate<T> criterio) {

        return registros.removeIf(criterio);
    }

    // Retornar a quantidade de registros
    public int quantidade() {

        return registros.size();
    }

    // Verificar se a colecao esta vazia
    public boolean estaVazia() {

        return registros.isEmpty();
    }

    // Limpar todos os registros
    public void limpar() {

        registros.clear();
    }

    // Buscar o primeiro registro que atende ao criterio
    public Optional<T> buscar(Predicate<T> criterio) {

        return registros.stream()
                .filter(criterio)
                .findFirst();
    }

    // Filtrar todos os registros por uma condicao
    public List<T> filtrar(Predicate<T> criterio) {

        return registros.stream()
                .filter(criterio)
                .collect(Collectors.toList());
    }

    // Ordenar registros sem alterar a lista original
    public List<T> ordenar(Comparator<T> comparador) {

        return registros.stream()
                .sorted(comparador)
                .collect(Collectors.toList());
    }

    // Verificar se existe um registro
    public boolean existe(Predicate<T> criterio) {

        return registros.stream()
                .anyMatch(criterio);
    }

    // Contar registros que atendem ao criterio
    public long contar(Predicate<T> criterio) {

        return registros.stream()
                .filter(criterio)
                .count();
    }
}