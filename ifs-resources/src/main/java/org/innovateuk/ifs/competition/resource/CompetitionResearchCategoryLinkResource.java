package org.innovateuk.ifs.competition.resource;

import org.innovateuk.ifs.category.resource.CategoryLinkResource;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;

public class CompetitionResearchCategoryLinkResource extends CategoryLinkResource<CompetitionResource, ResearchCategoryResource> {

    private CompetitionResource competitionResource;

    public CompetitionResearchCategoryLinkResource() {

    }

    @Override
    public CompetitionResource getEntity() {
        return competitionResource;
    }
}
