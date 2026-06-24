package inventory.desktop;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ResourceConfig {
    public final String title;
    public final String path;
    public final List<String> columns;
    public final List<String> editableFields;
    public final Map<String, String> nestedFields;
    public final boolean writable;
    public final boolean exportable;

    private ResourceConfig(String title, String path, List<String> columns, List<String> editableFields,
                           Map<String, String> nestedFields, boolean writable, boolean exportable) {
        this.title = title;
        this.path = path;
        this.columns = columns;
        this.editableFields = editableFields;
        this.nestedFields = nestedFields;
        this.writable = writable;
        this.exportable = exportable;
    }

    public static List<ResourceConfig> defaults() {
        Map<String, String> productNested = new LinkedHashMap<>();
        productNested.put("categoryId", "cate");

        return Arrays.asList(
                new ResourceConfig("Categories", "/api/categories",
                        Arrays.asList("id", "code", "name", "description", "activeFlag"),
                        Arrays.asList("code", "name", "description"), new LinkedHashMap<>(), true, false),
                new ResourceConfig("Products", "/api/products",
                        Arrays.asList("id", "code", "name", "categoryName", "imgUrl", "activeFlag"),
                        Arrays.asList("code", "name", "description", "imgUrl", "categoryId"), productNested, true, false),
                new ResourceConfig("Product In Stock", "/api/product-in-stocks",
                        Arrays.asList("id", "productCode", "productName", "categoryName", "qty", "price"),
                        Arrays.asList(), new LinkedHashMap<>(), false, false),
                new ResourceConfig("Goods Receipts", "/api/goods-receipts",
                        Arrays.asList("id", "code", "productId", "productCode", "productName", "qty", "price", "updateDate"),
                        Arrays.asList("code", "productId", "qty", "price"), new LinkedHashMap<>(), true, true),
                new ResourceConfig("Goods Issues", "/api/goods-issues",
                        Arrays.asList("id", "code", "productId", "productCode", "productName", "qty", "price", "updateDate"),
                        Arrays.asList("code", "productId", "qty", "price"), new LinkedHashMap<>(), true, true),
                new ResourceConfig("Users", "/api/users",
                        Arrays.asList("id", "userName", "name", "email", "roleName", "activeFlag"),
                        Arrays.asList("userName", "password", "name", "email", "roleID"), new LinkedHashMap<>(), true, false),
                new ResourceConfig("Roles", "/api/roles",
                        Arrays.asList("id", "roleName", "description", "activeFlag"),
                        Arrays.asList("roleName", "description"), new LinkedHashMap<>(), true, false),
                new ResourceConfig("Menus", "/api/menus",
                        Arrays.asList("id", "name", "url", "parentId", "orderIndex", "activeFlag"),
                        Arrays.asList(), new LinkedHashMap<>(), false, false),
                new ResourceConfig("Histories", "/api/histories",
                        Arrays.asList("id", "actionName", "type", "productCode", "productName", "qty", "price", "updateDate"),
                        Arrays.asList(), new LinkedHashMap<>(), false, false)
        );
    }
}
