package org.innovateuk.ifs.category.domain;

import java.util.List;

// todo class comment
public abstract class ContainerCategory<T extends Category> extends Category {

    ContainerCategory() {
        // default constructor
    }

    protected ContainerCategory(String name) {
        super(name);
    }

    public abstract List<T>  getChildren();
}
