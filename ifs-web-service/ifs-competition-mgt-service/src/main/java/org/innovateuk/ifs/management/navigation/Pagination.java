package org.innovateuk.ifs.management.navigation;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.innovateuk.ifs.commons.resource.PageResource;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class Pagination {

    private boolean hasPrevious;
    private boolean hasNext;
    private int totalPages;
    private int currentPage;
    private int pageSize;
    private long totalCount;
    private List<PaginationLink> pageNames;

    public Pagination(PageResource pageResource) {
        this(pageResource, getQueryStringOrNull());
    }

    public Pagination(PageResource pageResource, String existingQuery) {
        this.totalCount = pageResource.getTotalElements();
        this.hasPrevious = pageResource.hasPrevious();
        this.hasNext = pageResource.hasNext();
        this.totalPages = pageResource.getTotalPages();
        this.currentPage = pageResource.getNumber();
        this.pageSize = pageResource.getSize();
        this.pageNames = IntStream.range(0,totalPages)
                .mapToObj(i -> new PaginationLink(i, pageSize, totalCount, existingQuery))
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

    public List<PaginationLink> getPageNames() {
        return pageNames;
    }

    public long getTotalCount() {
        return totalCount;
    }

    private static String getQueryStringOrNull() {
        return Optional.ofNullable(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()))
                .map(ServletRequestAttributes::getRequest)
                .map(HttpServletRequest::getQueryString)
                .map(query -> "?" + query)
                .orElse("");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Pagination that = (Pagination) o;

        return new EqualsBuilder()
                .append(hasPrevious, that.hasPrevious)
                .append(hasNext, that.hasNext)
                .append(totalPages, that.totalPages)
                .append(currentPage, that.currentPage)
                .append(pageSize, that.pageSize)
                .append(totalCount, that.totalCount)
                .append(pageNames, that.pageNames)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(hasPrevious)
                .append(hasNext)
                .append(totalPages)
                .append(currentPage)
                .append(pageSize)
                .append(totalCount)
                .append(pageNames)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("hasPrevious", hasPrevious)
                .append("hasNext", hasNext)
                .append("totalPages", totalPages)
                .append("currentPage", currentPage)
                .append("pageSize", pageSize)
                .append("totalCount", totalCount)
                .append("pageNames", pageNames)
                .toString();
    }
}
