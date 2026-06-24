package inventory.dao;

import inventory.model.paging;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

// IMPORT THÊM THƯ VIỆN NÀY CỦA JPA
import javax.persistence.EntityManager;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;

@Transactional
@Repository
public abstract class BaseDAOimpl<E> implements baseDAO<E> {

    // 1. THAY THẾ SessionFactory BẰNG EntityManager CỦA SPRING BOOT
    @Autowired
    private EntityManager entityManager;

    private Class<E> persistentClass;

    @SuppressWarnings("unchecked")
    public BaseDAOimpl() {
        // Đoạn code ma thuật dùng Reflection để lấy Class của E tại Runtime
        this.persistentClass = (Class<E>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }

    // 2. SỬA LẠI HÀM LẤY SESSION ĐỂ ĐỒNG BỘ VỚI @Transactional
    protected Session getSession() {
        // Rút ruột Session của Hibernate ra từ bên trong EntityManager của Spring
        return entityManager.unwrap(Session.class);
    }

    @Override
    public List<E> findAll(String query, Map<String, Object> params, paging paging) {
       StringBuffer hql = new StringBuffer("from "+persistentClass.getName()+" as model  where 1=1 ");
       StringBuffer countHql = new StringBuffer();
       countHql.append("select count(*) from "+persistentClass.getName()+" as model  where 1=1 ");
        if(query!=null&&query!=""){
            hql.append(query);
            countHql.append(query);
        }
        Query<E> queryObj = getSession().createQuery(hql.toString());
        Query<E> queryCount = getSession().createQuery(countHql.toString());
        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
                queryObj.setParameter(key, params.get(key));
                queryCount.setParameter(key, params.get(key));
            }
        }
        if(paging!=null){
            queryObj.setFirstResult(paging.getOffset());
            queryObj.setMaxResults(paging.getRecordPerPage());
            Long total = (Long) queryCount.uniqueResult();
            paging.setTotalRows(total);
        }
        return queryObj.list();
    }

    @Override
    public E findById(Class<E> e, int id) {
        return getSession().get(persistentClass, id); // hàm tìm kiếm theo id
    }

    @Override
    public List<E> findByProperty(String name, Object value) {
        return getSession().createQuery("from " + persistentClass.getName() + " where " + name + " = :value")
                .setParameter("value", value)
                .list();
    }

    @Override
    public void save(E e) {
        getSession().save(e);
    }

    @Override
    public void update(E e) {
        getSession().merge(e);
    }
}