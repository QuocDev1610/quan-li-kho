package inventory.service;

import inventory.dao.MenuDAO;
import inventory.dao.entity.Menu;
import inventory.model.paging;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional
@Service
public class MenuService {
    @Autowired
    private MenuDAO menuDAO;
    private static final Logger log = Logger.getLogger(MenuService.class);
    public List<Menu> getListMenu(paging paging , Menu menu){
        log.info("show all menu");
        StringBuilder queryStr = new StringBuilder();
        Map<String, Object> mapParams = new HashMap<>();
        if(menu!=null) {
            if(!StringUtils.isEmpty(menu.getUrl())) {
                queryStr.append(" and model.url like :url");
                mapParams.put("url", "%"+menu.getUrl()+"%");
            }
        }
        return menuDAO.findAll(queryStr.toString(), mapParams, paging);
    }
    public void change(Menu menu){

    }
    public Menu findById(int id){
return menuDAO.findById(Menu.class,id);
    }
    public void changeMenu(Menu menu){
        Menu menu1=menuDAO.findById(Menu.class,menu.getId());
        if(menu1!=null){
            menu1.setName(menu.getName());
            menu1.setUrl(menu.getUrl());
            menu1.setUpdateDate(new java.util.Date().toInstant());
            menuDAO.update(menu1);
        }
    }
}
