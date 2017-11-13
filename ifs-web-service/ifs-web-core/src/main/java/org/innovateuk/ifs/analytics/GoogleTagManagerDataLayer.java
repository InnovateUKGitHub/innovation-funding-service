package org.innovateuk.ifs.analytics;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * The Google Analytics Tag Manager Data Layer. Contains our properties for Google Analytics.
 * See https://support.google.com/analytics/answer/6164391?hl=en
 */
public class GoogleTagManagerDataLayer {

    private String compName;

    public String getCompName() {
        return compName;
    }

    public void setCompName(String compName) {
        this.compName = compName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        GoogleTagManagerDataLayer that = (GoogleTagManagerDataLayer) o;

        return new EqualsBuilder()
                .append(compName, that.compName)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(compName)
                .toHashCode();
    }
}
