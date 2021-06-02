package org.innovateuk.ifs.navigation;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.net.URI;

import static com.google.common.base.Strings.isNullOrEmpty;

public class PageHistory {
    private String name;
    private String uri;
    private String query;

    private PageHistory() {
    }

    public PageHistory(String uri) {
        this.uri = uri;
    }

    public PageHistory(String name, String uri) {
        this.name = name;
        this.uri = uri;
    }

    public PageHistory(String name, String uri, String query) {
        this.name = name;
        this.uri = uri;
        this.query = query;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public String getQuery() {
        return query;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        PageHistory that = (PageHistory) o;

        String thisPath = removeTrailingSlash(URI.create(uri).getPath());
        String thatPath = removeTrailingSlash(URI.create(that.getUri()).getPath());

        return thisPath.equals(thatPath);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(uri)
                .toHashCode();
    }

    private String removeTrailingSlash(String url) {
        if (url.endsWith("/")) {
            return url.substring(0, url.length() - 1);
        }
        return url;
    }

    public String buildUrl() {
        if (isNullOrEmpty(query)) {
            return uri;
        } else {
            return uri + "?" + query;
        }
    }
}