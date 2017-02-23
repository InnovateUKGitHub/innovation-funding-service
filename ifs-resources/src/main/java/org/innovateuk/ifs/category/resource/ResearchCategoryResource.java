package org.innovateuk.ifs.category.resource;

import static org.innovateuk.ifs.category.resource.CategoryType.RESEARCH_CATEGORY;

/**
 * DTO for <code>ResearchCategory</code>s.
 */
public class ResearchCategoryResource extends CategoryResource {
    @Override
    public CategoryType getType() {
        return RESEARCH_CATEGORY;
    }
}
