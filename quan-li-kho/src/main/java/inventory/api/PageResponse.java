package inventory.api;

import inventory.model.paging;

import java.util.List;

public class PageResponse<T> {
    private List<T> items;
    private int currentPage;
    private int recordPerPage;
    private int totalPages;
    private long totalRows;

    public PageResponse() {
    }

    public PageResponse(List<T> items, paging paging) {
        this.items = items;
        if (paging != null) {
            this.currentPage = paging.getCurrentPage();
            this.recordPerPage = paging.getRecordPerPage();
            this.totalPages = paging.getTotalPages();
            this.totalRows = paging.getTotalRows();
        }
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getRecordPerPage() {
        return recordPerPage;
    }

    public void setRecordPerPage(int recordPerPage) {
        this.recordPerPage = recordPerPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(long totalRows) {
        this.totalRows = totalRows;
    }
}
