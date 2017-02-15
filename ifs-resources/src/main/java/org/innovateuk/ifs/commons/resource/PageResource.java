package org.innovateuk.ifs.commons.resource;

import java.util.List;

/**
 * Wraps a list of resources in {@link this#content} and exposes pageable properties
 * that can be used to perform further pagination queries.
 *
 * @param <PageableResource> the resource type that is being paginated.
 */
public abstract class PageResource<PageableResource> {

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
     * The size of the current page's list of items.
     */
    private int size;

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
}
