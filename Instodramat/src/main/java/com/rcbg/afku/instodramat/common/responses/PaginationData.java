package com.rcbg.afku.instodramat.common.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaginationData {

    private int pageNumber;
    private int totalPages;
    private int pageSize;
    private long totalElements;
    private boolean hasNext;
    private boolean isFirst;
    private boolean isEmpty;

    public PaginationData(Page<?> page){
        this.pageNumber = page.getNumber();
        this.totalPages = page.getTotalPages();
        this.pageSize = page.getSize();
        this.totalElements = page.getTotalElements();
        this.hasNext = page.hasNext();
        this.isFirst = page.isFirst();
        this.isEmpty = page.isEmpty();
    }
}
