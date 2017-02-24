package org.innovateuk.ifs.management.viewmodel;


import org.innovateuk.ifs.commons.resource.PageResource;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class PaginationViewModel {
    private boolean hasPrevious;
    private boolean hasNext;
    private int totalPages;
    private int currentPage;
    private int pageSize;
    private long totalCount;
    private List<PaginationLinkViewModel> pageNames;

    public PaginationViewModel(PageResource pageResource, String existingQuery) {
        this.totalCount = pageResource.getTotalElements();
        this.hasPrevious = pageResource.hasPrevious();
        this.hasNext = pageResource.hasNext();
        this.totalPages = pageResource.getTotalPages();
        this.currentPage = pageResource.getNumber();
        this.pageSize = pageResource.getSize();
        this.pageNames = IntStream.range(0,totalPages)
                .mapToObj(i -> new PaginationLinkViewModel(i, pageSize, totalCount, existingQuery))
                .collect(toList());
    }

    public boolean isHasPrevious() {
        return hasPrevious;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public List<PaginationLinkViewModel> getPageNames() {
        return pageNames;
    }

    public long getTotalCount() {
        return totalCount;
    }
}
