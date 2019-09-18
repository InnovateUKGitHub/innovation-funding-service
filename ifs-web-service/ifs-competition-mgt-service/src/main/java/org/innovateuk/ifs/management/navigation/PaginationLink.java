package org.innovateuk.ifs.management.navigation;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.web.util.UriComponentsBuilder;

public class PaginationLink {

    private String path;
    private String title;

    public PaginationLink(int page, int pageSize, long totalElements, String existingQuery) {
        this.title = ((page * pageSize) + 1) + " to " + Math.min((page + 1) * ((long) pageSize), totalElements);
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

        PaginationLink that = (PaginationLink) o;

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

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("path", path)
                .append("title", title)
                .toString();
    }
}
