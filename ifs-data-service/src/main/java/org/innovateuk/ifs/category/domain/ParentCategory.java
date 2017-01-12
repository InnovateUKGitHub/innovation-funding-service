package org.innovateuk.ifs.category.domain;

import java.util.List;

/**
 * A abstract {@link Category} with child {@link Category}s
 * @param <T> the type of the child Categories
 */
public abstract class ParentCategory<T extends Category> extends Category {

    ParentCategory() {
        // default constructor
    }

    protected ParentCategory(String name) {
        super(name);
    }

    public abstract List<T>  getChildren();
}
