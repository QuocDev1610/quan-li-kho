package inventory.validate;

import inventory.dao.entity.Category;
import inventory.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.List;

@Component
public class CategoryValidator implements Validator {
    @Autowired
    private ProductService productService;
    @Override
    public boolean supports(Class<?> aClass) {
        return Category.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Category category = (Category) target;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "msg.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "code", "msg.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", "msg.required");
        if(category.getCode()!=null && !category.getCode().isEmpty()){
            List<Category> results= productService.FindByCode(category.getCode());
            if(results!=null && !results.isEmpty()) {
                Category existingCategory = results.get(0);
                if (category.getId() == null || !category.getId().equals(existingCategory.getId())) {
                    errors.rejectValue("code", "msg.code.exist");
                }
            }
        }
    }}
