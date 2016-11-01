package com.worth.ifs.category.domain;

import javax.persistence.*;

@Entity
public class CategoryLink {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="categoryId", referencedColumnName="id")
    private Category category;
    private String className;
    private Long classPk;

    public CategoryLink() {
    }

    public CategoryLink(Category category, String className, Long classPk) {
        this.category = category;
        this.className = className;
        this.classPk = classPk;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Long getClassPk() {
        return classPk;
    }

    public void setClassPk(Long classPk) {
        this.classPk = classPk;
    }
}


