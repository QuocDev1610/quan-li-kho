package inventory.service;

import inventory.dao.CategoryDAO;
import inventory.dao.UserDAO;
import inventory.dao.entity.Category;
import inventory.dao.entity.ProductInfo;
import inventory.dao.productInfoDA0;
import inventory.model.paging;
import inventory.utils.configLoader;
import org.apache.log4j.Logger;
import org.hibernate.boot.cfgxml.internal.ConfigLoader;
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

public class ProductService {
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private CategoryDAO categoryDAO;
    @Autowired
    private productInfoDA0 productInfoDAO;
    final static  Logger logger = Logger.getLogger(ProductService.class);

    public void save(Category category) throws Exception {
        logger.info("Insert category: " + category);
        category.setActiveFlag(1);
        category.setCreateDate(new Date().toInstant());
        category.setUpdateDate(new Date().toInstant());
        categoryDAO.save(category);}
    public void Update(Category category) throws Exception {
        logger.info("Update category: " + category);
        category.setUpdateDate(new Date().toInstant());
        categoryDAO.update(category);
    }
    public void delete(Category category) throws Exception {
        logger.info("Delete category: " + category);
        category.setActiveFlag(0);
        category.setUpdateDate(new Date().toInstant());
        categoryDAO.update(category);
    }
    public List<Category> FindByCode(String code) {
        logger.info("Finding category: " + code);
        return categoryDAO.findByProperty("code", code);

    }
    public List<Category> FindAll(Category category, paging paging) {
        logger.info("Finding all categories");
        StringBuffer query = new StringBuffer();
        Map<String, Object> params = new java.util.HashMap<>();
        query.append(" and model.activeFlag = :activeFlag");
        params.put("activeFlag", 1);
        if(category != null){
            if (category.getId()!=null && category.getId()!=0){
                query.append(" and model.id=:id");
                params.put("id",category.getId());
            }
            if (category.getCode() != null && !category.getCode().isEmpty()) {
                query.append(" and model.code = :code");
                params.put("code", category.getCode());
            }

            if (category.getName() != null && !category.getName().isEmpty()) {
                // Tương tự cho trường Name (Có thể dùng 'like' để tìm kiếm gần đúng)
                query.append(" and model.name like :name");
                params.put("name", "%" + category.getName() + "%");
            }
        }
        return categoryDAO.findAll(query.toString(),params,paging);
    }
    public Category FindById(int id) {
        logger.info("Finding category: " + id);
        return categoryDAO.findByProperty("id", id).get(0);
    }

    //PRODUCT

    public void saveProductInfo(ProductInfo productInfo) throws Exception {
        logger.info("Insert Product: " + productInfo);
        productInfo.setActiveFlag(1);
        if(productInfo.getMultipartFile()!=null && !productInfo.getMultipartFile().isEmpty()){
            String filename=System.currentTimeMillis() + "_" + productInfo.getMultipartFile().getOriginalFilename();
            productInfo.setImgUrl("/upload/"+ filename);
            processUploadFile(productInfo.getMultipartFile(),filename);
        } else if(productInfo.getImgUrl()==null || productInfo.getImgUrl().isEmpty()){
            productInfo.setImgUrl("");
        }
        productInfo.setCreateDate(new Date().toInstant());
        productInfo.setUpdateDate(new Date().toInstant());
        productInfoDAO.save(productInfo);}
    public void Update(ProductInfo ProductInfo) throws Exception {
        logger.info("Update ProductInfo: " + ProductInfo);
        if(ProductInfo.getMultipartFile()!=null && !ProductInfo.getMultipartFile().isEmpty()){
            String filename=System.currentTimeMillis() + "_" + ProductInfo.getMultipartFile().getOriginalFilename();
            ProductInfo.setImgUrl("/upload/"+ filename);
            processUploadFile(ProductInfo.getMultipartFile(),filename);}
        ProductInfo.setUpdateDate(new Date().toInstant());
        productInfoDAO.update(ProductInfo);
    }
    public void delete(ProductInfo ProductInfo) throws Exception {
        logger.info("Delete ProductInfo: " + ProductInfo);
        ProductInfo.setActiveFlag(0);
        ProductInfo.setUpdateDate(new Date().toInstant());
        productInfoDAO.update(ProductInfo);
    }
    public List<ProductInfo> FindByCodeProduct(String code) {
        logger.info("Finding ProductInfo: " + code);
        return productInfoDAO.findByProperty("code", code);

    }
    public List<ProductInfo> FindAllProduct(ProductInfo ProductInfo, paging paging) {
        logger.info("Finding all categories");
        StringBuffer query = new StringBuffer();
        Map<String, Object> params = new java.util.HashMap<>();
        query.append(" and model.activeFlag = :activeFlag");
        params.put("activeFlag", 1);
        if(ProductInfo != null){
            if (ProductInfo.getId()!=null && ProductInfo.getId()!=0){
                query.append(" and model.id=:id");
                params.put("id",ProductInfo.getId());
            }
            if (ProductInfo.getCode() != null && !ProductInfo.getCode().isEmpty()) {
                query.append(" and model.code = :code");
                params.put("code", ProductInfo.getCode());
            }

            if (ProductInfo.getName() != null && !ProductInfo.getName().isEmpty()) {
                // Tương tự cho trường Name (Có thể dùng 'like' để tìm kiếm gần đúng)
                query.append(" and model.name like :name");
                params.put("name", "%" + ProductInfo.getName() + "%");
            }
        }
        return productInfoDAO.findAll(query.toString(),params,paging);
    }
    public ProductInfo FindByIdProduct(int id) {
        logger.info("Finding ProductInfo: " + id);
        return productInfoDAO.findByProperty("id", id).get(0);
    }
    private void processUploadFile(MultipartFile file,String filename) throws IOException {
        String path = configLoader.getInstance().getProperty("upload.location");
        if (file != null && !file.isEmpty()) {

            File dest= new File(path);
            if (!dest.exists()) {
                dest.mkdirs();
            }
            File destFile = new File(path, filename);
            file.transferTo(destFile);}
    }
}
