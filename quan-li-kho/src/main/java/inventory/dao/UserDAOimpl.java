package inventory.dao;


import inventory.dao.entity.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class UserDAOimpl extends BaseDAOimpl<User> implements UserDAO {

}
