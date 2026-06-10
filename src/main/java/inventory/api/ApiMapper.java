package inventory.api;

import inventory.api.dto.CategoryDto;
import inventory.api.dto.HistoryDto;
import inventory.api.dto.InvoiceDto;
import inventory.api.dto.MenuDto;
import inventory.api.dto.ProductDto;
import inventory.api.dto.ProductInStockDto;
import inventory.api.dto.RoleDto;
import inventory.api.dto.UserDto;
import inventory.dao.entity.Category;
import inventory.dao.entity.History;
import inventory.dao.entity.Invoice;
import inventory.dao.entity.Menu;
import inventory.dao.entity.ProductInStock;
import inventory.dao.entity.ProductInfo;
import inventory.dao.entity.Role;
import inventory.dao.entity.User;
import inventory.dao.entity.UserRole;
import org.springframework.validation.BindingResult;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public final class ApiMapper {
    private ApiMapper() {
    }

    public static CategoryDto toCategoryDto(Category category) {
        if (category == null) {
            return null;
        }
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setCode(category.getCode());
        dto.setDescription(category.getDescription());
        dto.setActiveFlag(category.getActiveFlag());
        dto.setCreateDate(category.getCreateDate());
        dto.setUpdateDate(category.getUpdateDate());
        return dto;
    }

    public static ProductDto toProductDto(ProductInfo product) {
        if (product == null) {
            return null;
        }
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setCode(product.getCode());
        dto.setDescription(product.getDescription());
        dto.setImgUrl(product.getImgUrl());
        dto.setActiveFlag(product.getActiveFlag());
        dto.setCreateDate(product.getCreateDate());
        dto.setUpdateDate(product.getUpdateDate());
        if (product.getCate() != null) {
            dto.setCategoryId(product.getCate().getId());
            dto.setCategoryName(product.getCate().getName());
        }
        return dto;
    }

    public static RoleDto toRoleDto(Role role) {
        if (role == null) {
            return null;
        }
        RoleDto dto = new RoleDto();
        dto.setId(role.getId());
        dto.setRoleName(role.getRoleName());
        dto.setDescription(role.getDescription());
        dto.setActiveFlag(role.getActiveFlag());
        dto.setCreateDate(role.getCreateDate());
        dto.setUpdateDate(role.getUpdateDate());
        return dto;
    }

    public static UserDto toUserDto(User user) {
        if (user == null) {
            return null;
        }
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUserName(user.getUserName());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setActiveFlag(user.getActiveFlag());
        dto.setCreateDate(user.getCreateDate());
        dto.setUpdateDate(user.getUpdateDate());
        UserRole userRole = firstUserRole(user);
        if (userRole != null && userRole.getRole() != null) {
            dto.setRoleId(userRole.getRole().getId());
            dto.setRoleName(userRole.getRole().getRoleName());
            dto.setRoleDescription(userRole.getRole().getDescription());
        }
        return dto;
    }

    public static InvoiceDto toInvoiceDto(Invoice invoice) {
        if (invoice == null) {
            return null;
        }
        InvoiceDto dto = new InvoiceDto();
        dto.setId(invoice.getId());
        dto.setCode(invoice.getCode());
        dto.setType(invoice.getType());
        dto.setQty(invoice.getQty());
        dto.setPrice(invoice.getPrice());
        dto.setActiveFlag(invoice.getActiveFlag());
        dto.setCreateDate(invoice.getCreateDate());
        dto.setUpdateDate(invoice.getUpdateDate());
        if (invoice.getProduct() != null) {
            dto.setProductId(invoice.getProduct().getId());
            dto.setProductCode(invoice.getProduct().getCode());
            dto.setProductName(invoice.getProduct().getName());
        }
        return dto;
    }

    public static ProductInStockDto toProductInStockDto(ProductInStock stock) {
        if (stock == null) {
            return null;
        }
        ProductInStockDto dto = new ProductInStockDto();
        dto.setId(stock.getId());
        dto.setQty(stock.getQty());
        dto.setPrice(stock.getPrice());
        dto.setActiveFlag(stock.getActiveFlag());
        dto.setCreateDate(stock.getCreateDate());
        dto.setUpdateDate(stock.getUpdateDate());
        if (stock.getProduct() != null) {
            dto.setProductId(stock.getProduct().getId());
            dto.setProductCode(stock.getProduct().getCode());
            dto.setProductName(stock.getProduct().getName());
            if (stock.getProduct().getCate() != null) {
                dto.setCategoryName(stock.getProduct().getCate().getName());
            }
        }
        return dto;
    }

    public static HistoryDto toHistoryDto(History history) {
        if (history == null) {
            return null;
        }
        HistoryDto dto = new HistoryDto();
        dto.setId(history.getId());
        dto.setActionName(history.getActionName());
        dto.setType(history.getType());
        dto.setQty(history.getQty());
        dto.setPrice(history.getPrice());
        dto.setActiveFlag(history.getActiveFlag());
        dto.setCreateDate(history.getCreateDate());
        dto.setUpdateDate(history.getUpdateDate());
        if (history.getProduct() != null) {
            dto.setProductId(history.getProduct().getId());
            dto.setProductCode(history.getProduct().getCode());
            dto.setProductName(history.getProduct().getName());
        }
        return dto;
    }

    public static MenuDto toMenuDto(Menu menu) {
        if (menu == null) {
            return null;
        }
        MenuDto dto = new MenuDto();
        dto.setId(menu.getId());
        dto.setParentId(menu.getParentId());
        dto.setUrl(menu.getUrl());
        dto.setName(menu.getName());
        dto.setOrderIndex(menu.getOrderIndex());
        dto.setActiveFlag(menu.getActiveFlag());
        dto.setCreateDate(menu.getCreateDate());
        dto.setUpdateDate(menu.getUpdateDate());
        if (menu.getChildrenMap() != null) {
            dto.setPermissions(new TreeMap<>(menu.getChildrenMap()));
        }
        if (menu.getChildren() != null) {
            dto.setChildren(toMenuDtoList(menu.getChildren()));
        }
        return dto;
    }

    public static List<CategoryDto> toCategoryDtoList(List<Category> categories) {
        return categories == null ? Collections.emptyList() : categories.stream().map(ApiMapper::toCategoryDto).collect(Collectors.toList());
    }

    public static List<ProductDto> toProductDtoList(List<ProductInfo> products) {
        return products == null ? Collections.emptyList() : products.stream().map(ApiMapper::toProductDto).collect(Collectors.toList());
    }

    public static List<RoleDto> toRoleDtoList(List<Role> roles) {
        return roles == null ? Collections.emptyList() : roles.stream().map(ApiMapper::toRoleDto).collect(Collectors.toList());
    }

    public static List<UserDto> toUserDtoList(List<User> users) {
        return users == null ? Collections.emptyList() : users.stream().map(ApiMapper::toUserDto).collect(Collectors.toList());
    }

    public static List<InvoiceDto> toInvoiceDtoList(List<Invoice> invoices) {
        return invoices == null ? Collections.emptyList() : invoices.stream().map(ApiMapper::toInvoiceDto).collect(Collectors.toList());
    }

    public static List<ProductInStockDto> toProductInStockDtoList(List<ProductInStock> stocks) {
        return stocks == null ? Collections.emptyList() : stocks.stream().map(ApiMapper::toProductInStockDto).collect(Collectors.toList());
    }

    public static List<HistoryDto> toHistoryDtoList(List<History> histories) {
        return histories == null ? Collections.emptyList() : histories.stream().map(ApiMapper::toHistoryDto).collect(Collectors.toList());
    }

    public static List<MenuDto> toMenuDtoList(List<Menu> menus) {
        return menus == null ? Collections.emptyList() : menus.stream().map(ApiMapper::toMenuDto).collect(Collectors.toList());
    }

    public static Map<String, String> validationErrors(BindingResult bindingResult) {
        Map<String, String> errors = new TreeMap<>();
        bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        bindingResult.getGlobalErrors().forEach(error -> errors.put(error.getObjectName(), error.getDefaultMessage()));
        return errors;
    }

    private static UserRole firstUserRole(User user) {
        if (user.getUserRoles() == null || user.getUserRoles().isEmpty()) {
            return null;
        }
        return user.getUserRoles().iterator().next();
    }
}
