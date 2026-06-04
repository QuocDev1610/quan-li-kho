package inventory.service;

import inventory.dao.HistoryDAO;
import inventory.dao.entity.History;
import inventory.dao.entity.Invoice;
import inventory.dao.entity.ProductInStock;
import inventory.dao.entity.ProductInfo;
import inventory.model.paging;
import inventory.utils.configLoader;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Transactional
@Service
public class HistoryService {
    @Autowired
    private HistoryDAO historyDAO;
    final static Logger logger = Logger.getLogger(HistoryService.class);




    public List<History> findAll(History searchForm, paging paging) {
        logger.info("Finding all Historys");

        StringBuilder query = new StringBuilder();
        Map<String, Object> params = new java.util.HashMap<>();

        if (searchForm != null ) {
            if(searchForm.getProduct() != null){

            ProductInfo product = searchForm.getProduct();


            if (product.getCate() != null && product.getCate().getName() != null && !product.getCate().getName().isEmpty()) {
                query.append(" and model.product.cate.name like :namecate");
                params.put("namecate", "%" + product.getCate().getName() + "%");
            }

            // 2. Tìm theo Code sản phẩm
            if (product.getCode() != null && !product.getCode().isEmpty()) {
                query.append(" and model.product.code = :code");
                params.put("code", product.getCode());
            }


            if (searchForm.getActionName() != null && !searchForm.getActionName().isEmpty()) {
                query.append(" and model.actionName like :actionName");
             params.put("actionName", "%" + searchForm.getActionName() + "%");

            }
            if(searchForm.getType()!=null && searchForm.getType()!=0){
                query.append(" and model.type = :type");
                params.put("type", searchForm.getType());
            }
            if(product.getName()!=null && !product.getName().isEmpty()) {
                query.append(" and model.product.name like :name");
                params.put("name", "%" + searchForm.getActionName() + "%");
            }
        }
}
        return historyDAO.findAll(query.toString(), params, paging);
    }


    public void Save (Invoice invoice, String actionName) throws Exception {
        logger.info("Insert history: " + invoice);
        History history = new History();
        history.setProduct(invoice.getProduct());
        history.setActionName(actionName);
        history.setActiveFlag(1);
        history.setQty(invoice.getQty());
        history.setPrice(invoice.getPrice());
        history.setCreateDate(new Date().toInstant());
        history.setType(invoice.getType());
        history.setUpdateDate(new Date().toInstant());
        historyDAO.save(history);

        }


    }

