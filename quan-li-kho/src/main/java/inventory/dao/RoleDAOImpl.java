package inventory.dao;

import inventory.dao.entity.Role;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class RoleDAOImpl extends BaseDAOimpl<Role> implements RoleDAO {
}
