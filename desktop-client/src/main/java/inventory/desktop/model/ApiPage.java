package inventory.desktop.model;

import java.util.List;

public class ApiPage<T> {
    public List<T> items;
    public int currentPage;
    public int recordPerPage;
    public int totalPages;
    public long totalRows;
}
