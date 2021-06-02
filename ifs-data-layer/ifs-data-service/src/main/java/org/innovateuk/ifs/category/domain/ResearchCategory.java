package org.innovateuk.ifs.category.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Represents a Research Category. {@link ResearchCategory}s don't have parents of children.
 */
@Entity
@DiscriminatorValue("RESEARCH_CATEGORY")
public class ResearchCategory extends Category {
    public static final long FEASIBILITY_STUDIES_ID = 33L;
    public static final long INDUSTRIAL_RESEARCH_ID = 34L;
    public static final long EXPERIMENTAL_DEVELOPMENT_ID = 35L;

    public ResearchCategory() {
        // default constructor
    }

    public ResearchCategory(String name) {
        super(name);
    }
}
