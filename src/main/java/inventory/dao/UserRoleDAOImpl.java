package inventory.dao;

import inventory.dao.entity.Role;
import inventory.dao.entity.User;
import inventory.dao.entity.UserRole;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class UserRoleDAOImpl extends BaseDAOimpl<UserRole> implements UserRoleDAO {

}
