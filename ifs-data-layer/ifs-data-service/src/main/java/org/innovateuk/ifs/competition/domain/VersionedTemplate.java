package org.innovateuk.ifs.competition.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Immutable;
import org.innovateuk.ifs.commons.util.AuditableEntity;

import javax.persistence.*;

/**
 * Represents a set of Terms and Conditions, comprising a name, a version, and an identifier for the template to use
 * for those terms and conditions.
 */
@Entity
@Immutable
@DiscriminatorColumn(name = "type")
@Table(name = "terms_and_conditions")
public abstract class VersionedTemplate extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @SuppressWarnings("unused")
    private String name;

    @SuppressWarnings("unused")
    private String template;

    @SuppressWarnings("unused")
    @Column(columnDefinition = "smallint(20)")
    private int version;

    public Long getId() {
        return id;
    }

    /**
     * Setter for MapStruct
     */
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getTemplate() {
        return template;
    }

    public int getVersion() {
        return version;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setTemplate(final String template) {
        this.template = template;
    }

    public void setVersion(final int version) {
        this.version = version;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final VersionedTemplate that = (VersionedTemplate) o;

        return new EqualsBuilder()
                .append(version, that.version)
                .append(id, that.id)
                .append(name, that.name)
                .append(template, that.template)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(name)
                .append(template)
                .append(version)
                .toHashCode();
    }
}
