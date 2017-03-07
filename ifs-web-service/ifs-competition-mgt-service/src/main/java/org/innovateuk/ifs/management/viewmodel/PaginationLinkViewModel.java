package org.innovateuk.ifs.management.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.web.util.UriComponentsBuilder;

public class PaginationLinkViewModel {
    private String path;
    private String title;

    public PaginationLinkViewModel(int page, int pageSize, long totalElements, String existingQuery) {
        this.title = ((page * pageSize) + 1) + " to " + Math.min((page + 1) * pageSize, totalElements);
        this.path = UriComponentsBuilder.fromUriString(existingQuery).replaceQueryParam("page", page).toUriString();
    }

    public String getPath() {
        return path;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        PaginationLinkViewModel that = (PaginationLinkViewModel) o;

        return new EqualsBuilder()
                .append(path, that.path)
                .append(title, that.title)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(path)
                .append(title)
                .toHashCode();
    }
}
