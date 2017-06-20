package org.innovateuk.ifs.category.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Represents a Research Category. {@link ResearchCategory}s don't have parents of children.
 */
@Entity
@DiscriminatorValue("RESEARCH_CATEGORY")
public class ResearchCategory extends Category {

    public ResearchCategory() {
        // default constructor
    }

    public ResearchCategory(String name) {
        super(name);
    }
}
