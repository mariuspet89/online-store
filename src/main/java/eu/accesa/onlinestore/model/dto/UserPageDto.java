package eu.accesa.onlinestore.model.dto;

import org.springframework.data.domain.Sort;

public class UserPageDto {
    //defines the first page
    private Integer pageNo=0;
    //defines default page size in case of no inputs
    private Integer pageSize=15;
    //sets default sort direction
    private Sort.Direction sortDirection=Sort.Direction.ASC;
    //sets  default sort field for the page
    private String sortBy="brand";

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Sort.Direction getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(Sort.Direction sortDirection) {
        this.sortDirection = sortDirection;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
}
