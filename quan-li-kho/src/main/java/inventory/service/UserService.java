package inventory.service;


import inventory.dao.RoleDAO;
import inventory.dao.UserDAO;
import inventory.dao.UserDAOimpl;
import inventory.dao.UserRoleDAO;
import inventory.dao.entity.Role;
import inventory.dao.entity.User;
import inventory.dao.entity.UserRole;
import inventory.model.paging;
import inventory.utils.HashingPassword;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional
@Service
public class UserService {
    final static Logger logger = Logger.getLogger(UserDAOimpl.class);
@Autowired
private RoleDAO roleDAO;
    @Autowired
private UserDAO userDAO;
    @Autowired
    private UserRoleDAO userRoleDAO;
    public List<User> FindByProperty(String name, Object value) {;
        logger.info("Finding users by property: " + name + " = " + value);
        return userDAO.findByProperty(name, value);
    }
public void SaveUser(User user) {
    logger.info("Saving user: " + user);
       user.setActiveFlag(1);
       user.setCreateDate(new java.util.Date().toInstant());
         user.setUpdateDate(new java.util.Date().toInstant());
         user.setPassword(HashingPassword.hashPassword(user.getPassword()));
    userDAO.save(user);
    UserRole userRole = new UserRole();
    userRole.setUser(user);
    if (user.getRoleID() != null) {
        userRole.setRole(roleDAO.findById(Role.class, user.getRoleID()));
    } else if (user.getUserRoles() != null && !user.getUserRoles().isEmpty()) {
        userRole.setRole(user.getUserRoles().iterator().next().getRole());
    }
    userRole.setActiveFlag(1);
    userRole.setCreateDate(new java.util.Date().toInstant());
    userRole.setUpdateDate(new java.util.Date().toInstant());
    userRoleDAO.save(userRole);

}
public void UpdateUser(User user) {
    logger.info("Updating user: " + user);
    User user1=userDAO.findById(User.class,user.getId());
    if(user1!=null){
        if (user.getRoleID() != null && user1.getUserRoles() != null && !user1.getUserRoles().isEmpty()) {
            UserRole userRole = user1.getUserRoles().iterator().next();
            userRole.setRole(roleDAO.findById(Role.class, user.getRoleID()));
            userRole.setUpdateDate(new java.util.Date().toInstant());
            userRoleDAO.update(userRole);
        }
        user1.setUserName(user.getUserName());
        user1.setEmail(user.getEmail());
        user1.setName(user.getName());
        user1.setUpdateDate(new java.util.Date().toInstant());
        userDAO.update(user1);
    }

}
public void DeleteUser(User user) {
        logger.info("Deleting user: " + user);
        user.setActiveFlag(0);
        user.setUpdateDate(new java.util.Date().toInstant());

        userDAO.update(user);
}
public User ChangeStatus(User user) {
        logger.info("Changing user status: " + user);
        User existing = userDAO.findById(User.class, user.getId());
        if (existing == null) {
            return null;
        }
        existing.setActiveFlag(Integer.valueOf(1).equals(existing.getActiveFlag()) ? 0 : 1);
        existing.setUpdateDate(new java.util.Date().toInstant());
        userDAO.update(existing);
        return existing;
}
public List<User> findAll(User user, paging paging) {
        logger.info("Finding all users");
        StringBuffer sb = new StringBuffer();
        Map<String,Object> map = new HashMap<>();
        if(user!=null){
            if(user.getUserName()!=null && !user.getUserName().isEmpty()){
                sb.append(" and model.userName like :userName");
                map.put("userName","%"+user.getUserName()+"%");
            }
            if(user.getEmail()!=null && !user.getEmail().isEmpty()){
                sb.append(" and model.email like :email");
                map.put("email","%"+user.getEmail()+"%");
        } if(user.getName()!=null && !user.getName().isEmpty()){
            sb.append(" and model.name=:name");
            map.put("name","%"+user.getName()+"%");
            }
        }  return userDAO.findAll(sb.toString(),map,paging);
}
public Role findRoleById(int id) {
        logger.info("Finding role by id: " + id);
        return roleDAO.findById(Role.class,id);

}
}
