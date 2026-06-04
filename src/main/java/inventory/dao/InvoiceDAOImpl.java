package inventory.dao;

import inventory.dao.entity.Invoice;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class InvoiceDAOImpl extends BaseDAOimpl<Invoice> implements InvoiceDAO {
}
