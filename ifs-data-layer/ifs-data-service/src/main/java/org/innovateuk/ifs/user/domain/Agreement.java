package org.innovateuk.ifs.user.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.commons.util.AuditableEntity;
import org.innovateuk.ifs.profile.domain.Profile;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * An agreement with main text and three appendices. The agreement is signed by assessors on registration (as part of
 * their {@link Profile}) and again in subsequent years.
 * <p>
 * There must be only one <em>current</em> agreement.
 *
 * @see Profile
 */
@Entity
public class Agreement extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private boolean current;

    private String text;

    public Agreement() {
    }

    public Agreement(String text, boolean current) {
        setText(text);
        this.current = current;
    }

    public Long getId() {
        return id;
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
        if (text == null) {
            throw new NullPointerException("text cannot be null");
        }
        if (text.isEmpty()) {
            throw new IllegalArgumentException("text cannot be empty");
        }
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

        Agreement agreement = (Agreement) o;

        return new EqualsBuilder()
                .append(current, agreement.current)
                .append(id, agreement.id)
                .append(text, agreement.text)
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
