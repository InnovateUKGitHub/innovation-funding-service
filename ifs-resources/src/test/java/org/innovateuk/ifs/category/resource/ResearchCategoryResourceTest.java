package org.innovateuk.ifs.category.resource;

import org.junit.Test;

import static org.innovateuk.ifs.category.builder.ResearchCategoryResourceBuilder.newResearchCategoryResource;
import static org.innovateuk.ifs.category.resource.CategoryType.RESEARCH_CATEGORY;
import static org.junit.Assert.assertEquals;

public class ResearchCategoryResourceTest {
    @Test
    public void getType() throws Exception {
        assertEquals(newResearchCategoryResource().build().getType(), RESEARCH_CATEGORY);
    }
}