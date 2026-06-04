package inventory.model;

public class paging {
    private long totalRows;
    private int totalPages;
    private int recordPerPage=10;
    private int currentPage;
    private int previousPage;
    private int nextPage;
    private int offset;
    private int limit;
    public paging(int recordPerPage ) {
        this.recordPerPage=recordPerPage;
    }

    public long getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(Long totalRows) {
        this.totalRows = totalRows;
    }

    public int getTotalPages() {
        if (totalRows >0) {
            totalPages = Math.ceil((double) totalRows / recordPerPage) > 0 ? (int) Math.ceil((double) totalRows / recordPerPage) : 1;

        }
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getRecordPerPage() {
        return recordPerPage;
    }

    public void setRecordPerPage(int recordPerPage) {
        this.recordPerPage = recordPerPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPreviousPage() {
        return previousPage;
    }

    public void setPreviousPage(int previousPage) {
        this.previousPage = previousPage;
    }

    public int getNextPage() {
        return nextPage;
    }

    public void setNextPage(int nextPage) {
        this.nextPage = nextPage;
    }

    public int getOffset() {
        if(currentPage > 0) {
            offset = (currentPage - 1) * recordPerPage;//bat dau tu dau
        }
        return offset;
    }

    public void setOffset(int offset) {

        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
