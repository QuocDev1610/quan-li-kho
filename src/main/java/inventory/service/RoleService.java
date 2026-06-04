package inventory.service;

import inventory.dao.RoleDAO;
import inventory.dao.UserDAOimpl;
import inventory.dao.entity.Role;
import inventory.model.paging;
import inventory.utils.HashingPassword;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class RoleService {
    @Autowired
    private RoleDAO roleDAO;
   final static Logger logger = Logger.getLogger(RoleService.class);
    public List<Role> FindByProperty(String name, Object value) {;
        logger.info("Finding Roles by property: " + name + " = " + value);
        return roleDAO.findByProperty(name, value);
    }
    public void SaveRole(Role Role) {
        logger.info("Saving Role: " + Role);
        Role.setActiveFlag(1);
        Role.setCreateDate(new java.util.Date().toInstant());
        Role.setUpdateDate(new java.util.Date().toInstant());
        roleDAO.save(Role);


    }
    public void UpdateRole(Role Role) {
        logger.info("Updating Role: " + Role);
        Role Role1=roleDAO.findById(Role.class,Role.getId());
        if(Role1!=null){
            Role1.setRoleName(Role.getRoleName());
            Role1.setDescription(Role.getDescription());
            Role1.setUpdateDate(new java.util.Date().toInstant());



        } roleDAO.update(Role);

    }
    public void DeleteRole(Role Role) {
        logger.info("Deleting Role: " + Role);
        Role.setActiveFlag(0);
        Role.setUpdateDate(new java.util.Date().toInstant());

        roleDAO.update(Role);
    }
    public List<Role> findAll(Role Role, paging paging) {
        logger.info("Finding all Roles");
        StringBuffer sb = new StringBuffer();
        Map<String,Object> map = new HashMap<>();
        if(Role!=null){
            if(Role.getRoleName()!=null && !Role.getRoleName().isEmpty()){
                sb.append(" and model.RoleName like :RoleName");
                map.put("RoleName","%"+Role.getRoleName()+"%");
            }
             if(Role.getDescription()!=null && !Role.getDescription().isEmpty()){
                sb.append(" and model.Description like :Description");
                map.put("Description","%"+Role.getDescription()+"%");
            }
        }  return roleDAO.findAll(sb.toString(),map,paging);
    }
    public Role findRoleById(int id) {
        logger.info("Finding role by id: " + id);
        return roleDAO.findById(Role.class,id);

    }
}
