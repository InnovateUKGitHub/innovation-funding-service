package org.innovateuk.ifs.pagination;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.innovateuk.ifs.commons.resource.PageResource;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.URISyntaxException;

import static com.google.common.base.Strings.isNullOrEmpty;

public class PaginationViewModel {
    /**
     * How many results to display each side of the current page.
     */
    private static final int EACH_SIDE = 2;

    /**
     * The total number of elements across all pages.
     */
    private long totalElements;

    /**
     * The current page number.
     */
    private int currentPage;

    /**
     * The maximum size of any page's list of items.
     */
    private int size;

    /**
     * The total number of pages.
     */
    private int totalPages;

    /**
     * The page to start displaying as links.
     */
    private long startPage;

    /**
     * The page to end displaying as links.
     */
    private long endPage;

    /**
     * The start of the current page being displayed.
     */
    private long currentElementsTo;

    /**
     * THe end of the current pages being displayed.
     */
    private long currentElementsFrom;

    public PaginationViewModel(PageResource pageResource) {
        this.totalElements = pageResource.getTotalElements();
        this.totalPages = pageResource.getTotalPages();
        this.currentPage = pageResource.getNumber() + 1; //Pages are 0 indexed.
        this.size = pageResource.getSize();

        if (totalPages <= (2 * EACH_SIDE) + 5) {
            // in this case too few pages, so display them all
            this.startPage = 1L;
            this.endPage = totalPages;
        } else if (currentPage <= EACH_SIDE + 3) {
            // in this case, current page is too close to the beginning
            this.startPage = 1;
            this.endPage = (2 * EACH_SIDE) + 3;
        } else if (currentPage >= totalPages - (EACH_SIDE + 2)) {
            // in this case, current page is too close to the end
            this.startPage = totalPages - (2 * EACH_SIDE) - 2;
            this.endPage = totalPages;
        } else {
            // regular case page in the middle.
            this.startPage = currentPage - EACH_SIDE;
            this.endPage = currentPage + EACH_SIDE;
        }

        this.currentElementsFrom = ((currentPage * size) - size) + 1;
        this.currentElementsTo = Math.min((currentElementsFrom + size - 1), totalElements);
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getSize() {
        return size;
    }

    public long getStartPage() {
        return startPage;
    }

    public long getEndPage() {
        return endPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public long getCurrentElementsTo() {
        return currentElementsTo;
    }

    public long getCurrentElementsFrom() {
        return currentElementsFrom;
    }

    public String urlForPage(long page) throws URISyntaxException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes()).getRequest();
        String uri = request.getRequestURI();
        String query = request.getQueryString();
        if (!isNullOrEmpty(query)) {
            uri = uri + "?" + query;
        }
        URIBuilder builder = new URIBuilder(uri);
        builder.addParameter("page", String.valueOf(page));
        return builder.build().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        PaginationViewModel that = (PaginationViewModel) o;

        return new EqualsBuilder()
                .append(totalElements, that.totalElements)
                .append(currentPage, that.currentPage)
                .append(size, that.size)
                .append(totalPages, that.totalPages)
                .append(startPage, that.startPage)
                .append(endPage, that.endPage)
                .append(currentElementsTo, that.currentElementsTo)
                .append(currentElementsFrom, that.currentElementsFrom)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(totalElements)
                .append(currentPage)
                .append(size)
                .append(totalPages)
                .append(startPage)
                .append(endPage)
                .append(currentElementsTo)
                .append(currentElementsFrom)
                .toHashCode();
    }
}
