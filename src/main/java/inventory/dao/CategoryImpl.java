package inventory.dao;

import inventory.dao.entity.Category;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class CategoryImpl extends BaseDAOimpl<Category> implements CategoryDAO {

}
