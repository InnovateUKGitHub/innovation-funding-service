package org.innovateuk.ifs.project.state;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class OnHoldReasonResource {

    private String title;
    private String body;

    public OnHoldReasonResource() {
    }

    public OnHoldReasonResource(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        OnHoldReasonResource that = (OnHoldReasonResource) o;

        return new EqualsBuilder()
                .append(title, that.title)
                .append(body, that.body)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(title)
                .append(body)
                .toHashCode();
    }
}
