package org.innovateuk.ifs.commons.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Wraps a list of resources in {@link this#content} and exposes pageable properties
 * that can be used to perform further pagination queries.
 *
 * @param <PageableResource> the resource type that is being paginated.
 */
@SuppressWarnings("unchecked")
public class PageResource<PageableResource> {

    /**
     * The total number of elements across all pages.
     */
    private long totalElements;
    /**
     * The total number of all pages.
     */
    private int totalPages;
    /**
     * The current page's list of items.
     */
    private List<PageableResource> content;
    /**
     * The current page number.
     */
    private int number;
    /**
     * The maximum size of any page's list of items.
     */
    private int size;

    public PageResource() {

    }

    public PageResource(long totalElements, int totalPages, List<PageableResource> content, int number, int size) {
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.content = content;
        this.number = number;
        this.size = size;
    }

    public static <PageableResource> PageResource<PageableResource> fromListZeroBased(List<PageableResource> all, int number, int size){
        List<PageableResource> results = all == null ? new ArrayList<>() : all;
        int totalElements = results .size();
        int totalPages = ((totalElements - 1) / size) + 1;
        int startIndex = max(0, min(number * size, totalElements));
        int endIndex = max(0, min((number + 1) * size, totalElements));
        List<PageableResource> content = results.subList(startIndex, endIndex);
        return new PageResource(totalElements, totalPages, content, number, size);
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public List<PageableResource> getContent() {
        return content;
    }

    public void setContent(List<PageableResource> content) {
        this.content = content;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean hasPrevious() {
        return number > 0;
    }

    public boolean hasNext() {
        return totalPages > (number + 1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        PageResource<?> that = (PageResource<?>) o;

        return new EqualsBuilder()
                .append(totalElements, that.totalElements)
                .append(totalPages, that.totalPages)
                .append(number, that.number)
                .append(size, that.size)
                .append(content, that.content)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(totalElements)
                .append(totalPages)
                .append(content)
                .append(number)
                .append(size)
                .toHashCode();
    }
}
