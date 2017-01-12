package org.innovateuk.ifs.category.domain;

import org.innovateuk.ifs.category.resource.CategoryType;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import static org.innovateuk.ifs.category.resource.CategoryType.RESEARCH_CATEGORY;

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

    @Override
    public CategoryType getType() {
        return RESEARCH_CATEGORY;
    }
}
