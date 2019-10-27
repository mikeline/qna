package services.interfaces;

import java.util.List;

public interface Service<T> {
    T find(int id);
    void save(T entity);
    void delete(T entity);
    void update(T entity);
    List<T> findAll();
}
