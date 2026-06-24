package inventory.dao;

import inventory.model.paging;

import java.util.List;
import java.util.Map;

public interface baseDAO<E> {
    public List<E> findAll(String query, Map<String,Object> params, paging paging );
    public E findById(Class<E> e, int id);
    public List<E> findByProperty(String name,Object value);
    public void save(E e);
    public void update(E e);
}
