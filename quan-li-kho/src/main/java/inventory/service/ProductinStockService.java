package inventory.service;

import inventory.dao.CategoryDAO;
import inventory.dao.ProductinStockDAO;
import inventory.dao.UserDAO;
import inventory.dao.entity.Category;
import inventory.dao.entity.Invoice;
import inventory.dao.entity.ProductInStock;
import inventory.dao.entity.ProductInfo;
import inventory.dao.productInfoDA0;
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
@Service
@Transactional
public class ProductinStockService {
    @Autowired
    private ProductinStockDAO ProductInStockDAO;

    final static  Logger logger = Logger.getLogger(ProductinStockService.class);




    public List<ProductInStock> findAllProduct(ProductInStock searchForm, paging paging) {
        logger.info("Finding all ProductInStocks");

        StringBuilder query = new StringBuilder(); // Đổi sang StringBuilder cho tối ưu hiệu năng
        Map<String, Object> params = new java.util.HashMap<>();

        if (searchForm != null && searchForm.getProduct() != null) {

            ProductInfo product = searchForm.getProduct();

            // 1. Tìm theo tên Category (Đã bổ sung check null cho Cate)
            if (product.getCate() != null && product.getCate().getName() != null && !product.getCate().getName().isEmpty()) {
                query.append(" and model.product.cate.name like :namecate");
                params.put("namecate", "%" + product.getCate().getName() + "%");
            }

            // 2. Tìm theo Code sản phẩm
            if (product.getCode() != null && !product.getCode().isEmpty()) {
                query.append(" and model.product.code = :code");
                params.put("code", product.getCode());
            }

            // 3. Tìm theo Tên sản phẩm
            if (product.getName() != null && !product.getName().isEmpty()) {
                query.append(" and model.product.name like :name");
                params.put("name", "%" + product.getName() + "%");
            }
        }

        return ProductInStockDAO.findAll(query.toString(), params, paging);
    }

    private void processUploadFile(MultipartFile file, String filename) throws IOException {

        String path = configLoader.getInstance().getProperty("upload.location");

        if (file != null && !file.isEmpty()) {



            File dest= new File(path);

            if (!dest.exists()) {

                dest.mkdirs();

            }

            File destFile = new File(path, filename);

            file.transferTo(destFile);}

    }
    public void SaveorUpdate (Invoice invoice){
        if(invoice.getProduct()!=null){
            if(invoice.getId()==null || invoice.getId()==0){
               String code=invoice.getProduct().getCode();
               ProductInStock product=ProductInStockDAO.findByProperty("product.code", code).get(0);
               if(product!=null){
                   logger.info("update qty"+invoice.getQty()+" and Price"+invoice.getPrice());
                  if(invoice.getType()==2){
                      product.setQty(product.getQty()-invoice.getQty());
                  }  else {product.setQty(product.getQty()+invoice.getQty());   }               //type=1: nhập hàng, type=2: xuất hàng
                   if(invoice.getType()==1){
                       product.setPrice(invoice.getPrice());
                   }
        product.setUpdateDate(new Date().toInstant());
                   ProductInStockDAO.update(product);
               }

            }else {
                logger.info("Insert qty"+invoice.getQty()+" and Price"+invoice.getPrice());
                ProductInStock product=new ProductInStock();
                product.setId(invoice.getProduct().getId());
                product.setPrice(invoice.getPrice());
                product.setQty(invoice.getQty());
                product.setProduct(invoice.getProduct());
                product.setActiveFlag(1);
                product.setCreateDate(new Date().toInstant());
                product.setUpdateDate(new Date().toInstant());
                ProductInStockDAO.save(product);
            }
        }


    }

}
