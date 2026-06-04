package inventory.dao;

import inventory.dao.entity.ProductInStock;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class ProductinStockDAOimpl extends BaseDAOimpl<ProductInStock> implements ProductinStockDAO {
}
