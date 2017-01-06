package org.innovateuk.ifs.category.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.category.resource.CategoryType;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.OrderBy;
import java.util.List;

@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;

    @Enumerated(value = EnumType.STRING)
    private CategoryType type;

    @ManyToOne(optional = true)
    @JoinColumn(name="parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    @OrderBy("name ASC")
    private List<Category> children;

    @OneToMany(mappedBy="category")
    private List<CategoryLink> categoryLinks;

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

    public CategoryType getType() {
        return type;
    }

    public void setType(CategoryType type) {
        this.type = type;
    }

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

    public List<Category> getChildren() {
        return children;
    }

    public void setChildren(List<Category> children) {
        this.children = children;
    }


    public List<CategoryLink> getCategoryLinks() {
        return categoryLinks;
    }

    public void setCategoryLinks(List<CategoryLink> categoryLinks) {
        this.categoryLinks = categoryLinks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Category category = (Category) o;

        return new EqualsBuilder()
                .append(name, category.name)
                .append(type, category.type)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .append(type)
                .toHashCode();
    }
}
