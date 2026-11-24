package interfaces;

import java.sql.SQLException;
import java.util.List;

public interface IPersistencia<T> {

    // CREATE
    void inserir(T objeto) throws SQLException;

    // READ - Listar todos
    List<T> listar() throws SQLException;

    // READ - Buscar por ID
    T buscarPorId(int id) throws SQLException;

    // UPDATE
    boolean atualizar(T objeto) throws SQLException;

    // DELETE
    boolean excluir(int id) throws SQLException;
}