package org.innovateuk.ifs.navigation;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.net.URI;

public class PageHistory {
    private String name;
    private String url;

    private PageHistory() {}

    public PageHistory(String url) {
        this.url = url;
    }

    public PageHistory(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        PageHistory that = (PageHistory) o;

        String thisPath = removeTrailingSlash(URI.create(url).getPath());
        String thatPath = removeTrailingSlash(URI.create(that.getUrl()).getPath());

        return thisPath.equals(thatPath);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(url)
                .toHashCode();
    }

    private String removeTrailingSlash(String url) {
        if (url.endsWith("/")) {
            return url.substring(0, url.length() - 1);
        }
        return url;
    }
}