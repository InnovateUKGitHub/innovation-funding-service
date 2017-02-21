package org.innovateuk.ifs.category.domain;

import org.innovateuk.ifs.category.resource.CategoryType;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

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
    private String description;

    // the type attribute is used by a spring data query
    @Column(name = "type", insertable = false, updatable = false)
    @Enumerated(value = EnumType.STRING)
    private CategoryType type;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CategoryInnerLink> links = new HashSet<>();

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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Category category = (Category) o;

        if (id != null ? !id.equals(category.id) : category.id != null) return false;
        if (name != null ? !name.equals(category.name) : category.name != null) return false;
        if (description != null ? !description.equals(category.description) : category.description != null)
            return false;
        if (type != category.type) return false;
        return links != null ? links.equals(category.links) : category.links == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (links != null ? links.hashCode() : 0);
        return result;
    }
}
