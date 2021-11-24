package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.competition.builder.CompetitionResearchCategoryLinkResourceBuilder;

import static org.innovateuk.ifs.category.builder.ResearchCategoryResourceBuilder.newResearchCategoryResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;

/**
 * Helper for Spring REST Docs, specifically for CompetitionResearchCategoryLinks.
 */
public class CompetitionResearchCategoryLinkDocs {

    public static final CompetitionResearchCategoryLinkResourceBuilder competitionResearchCategoryLinkBuilder =
            CompetitionResearchCategoryLinkResourceBuilder.newCompetitionResearchCategoryLinkResource()
                    .withCategory(newResearchCategoryResource().build())
                    .withCompetition(newCompetitionResource().build());

}
