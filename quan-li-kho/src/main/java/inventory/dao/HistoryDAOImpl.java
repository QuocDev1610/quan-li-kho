package inventory.dao;

import inventory.dao.entity.History;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class HistoryDAOImpl extends BaseDAOimpl<History> implements HistoryDAO {
}
