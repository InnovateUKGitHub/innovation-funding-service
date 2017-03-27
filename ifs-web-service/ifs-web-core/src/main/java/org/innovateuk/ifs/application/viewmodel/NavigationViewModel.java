package org.innovateuk.ifs.application.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.util.StringUtils;

/**
 * ViewModel used for showing navigation on questions and sections in applications
 */
public class NavigationViewModel {
    private String previousUrl;
    private String previousText;
    private String nextUrl;
    private String nextText;

    public String getPreviousUrl() {
        return previousUrl;
    }

    public void setPreviousUrl(String previousUrl) {
        this.previousUrl = previousUrl;
    }

    public String getPreviousText() {
        return previousText;
    }

    public void setPreviousText(String previousText) {
        this.previousText = previousText;
    }

    public String getNextUrl() {
        return nextUrl;
    }

    public void setNextUrl(String nextUrl) {
        this.nextUrl = nextUrl;
    }

    public String getNextText() {
        return nextText;
    }

    public void setNextText(String nextText) {
        this.nextText = nextText;
    }

    public Boolean getHasNavigation(){
        return !StringUtils.isEmpty(previousUrl) || !StringUtils.isEmpty(nextUrl);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        NavigationViewModel that = (NavigationViewModel) o;

        return new EqualsBuilder()
                .append(previousUrl, that.previousUrl)
                .append(previousText, that.previousText)
                .append(nextUrl, that.nextUrl)
                .append(nextText, that.nextText)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(previousUrl)
                .append(previousText)
                .append(nextUrl)
                .append(nextText)
                .toHashCode();
    }
}
