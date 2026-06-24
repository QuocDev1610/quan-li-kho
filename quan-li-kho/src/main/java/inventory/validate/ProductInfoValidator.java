package inventory.validate;

import inventory.dao.entity.Category;
import inventory.dao.entity.ProductInfo;
import inventory.service.ProductService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.List;
@Component
public class ProductInfoValidator implements Validator {
   @Autowired
   private ProductService productService;
        @Override
        public boolean supports(Class<?> aClass) {
            return ProductInfo.class.isAssignableFrom(aClass);
        }

        @Override
        public void validate(Object target, Errors errors) {
            ProductInfo ProductInfo = (ProductInfo) target;
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "msg.required");
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "code", "msg.required");
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", "msg.required");

            if(ProductInfo.getCode()!=null && !ProductInfo.getCode().isEmpty()){
                List<ProductInfo> results= productService.FindByCodeProduct(ProductInfo.getCode());
                if(results!=null && !results.isEmpty()) {
                    ProductInfo existingCategory = results.get(0);
                    if (ProductInfo.getId() == null || !ProductInfo.getId().equals(existingCategory.getId())) {
                        errors.rejectValue("code", "msg.code.exist");
                    }
                }
            }
            if(ProductInfo.getMultipartFile()!=null && !ProductInfo.getMultipartFile().isEmpty()){
                String ext = FilenameUtils.getExtension(ProductInfo.getMultipartFile().getOriginalFilename());
                if(!ext.equals("jpg") && !ext.equals("png") && !ext.equals("gif")){
                    errors.rejectValue("multipartFile", "msg.wrong.multipartfile");
                }


            }
        }}


