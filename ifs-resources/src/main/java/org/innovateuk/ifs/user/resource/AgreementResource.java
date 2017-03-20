package org.innovateuk.ifs.user.resource;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Agreement Data Transfer Object
 */
public class AgreementResource {

    private Long id;
    private boolean current;
    private String text;

    public AgreementResource() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AgreementResource that = (AgreementResource) o;

        return new EqualsBuilder()
                .append(current, that.current)
                .append(id, that.id)
                .append(text, that.text)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(current)
                .append(text)
                .toHashCode();
    }
}
