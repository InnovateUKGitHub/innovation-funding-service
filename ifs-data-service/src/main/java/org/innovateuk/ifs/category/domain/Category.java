package org.innovateuk.ifs.category.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.category.resource.CategoryType;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.List;

/**
 * Abstract Category.
 */
@Entity
@DiscriminatorColumn(name = "type")
public abstract class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;

    // the type attribute is used by a spring data query
    @Column(name = "type", insertable = false, updatable = false)
    @Enumerated(value = EnumType.STRING)
    private CategoryType type;

    Category() {
        // default constructor
    }

    protected Category(String name) {
        if (name == null) {
            throw new NullPointerException("name cannot be null");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("name cannot be empty");
        }
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public abstract CategoryType getType();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Category category = (Category) o;

        return new EqualsBuilder()
                .append(name, category.name)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .toHashCode();
    }
}
