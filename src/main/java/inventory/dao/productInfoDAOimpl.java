package inventory.dao;

import inventory.dao.entity.ProductInfo;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class productInfoDAOimpl extends BaseDAOimpl<ProductInfo> implements productInfoDA0 {
}
