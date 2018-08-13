package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.assessment.builder.AssessmentResourceBuilder;
import org.innovateuk.ifs.competition.builder.CompetitionResearchCategoryLinkResourceBuilder;
import org.innovateuk.ifs.competition.domain.CompetitionResearchCategoryLink;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.category.builder.ResearchCategoryResourceBuilder.newResearchCategoryResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

/**
 * Helper for Spring REST Docs, specifically for CompetitionResearchCategoryLinks.
 */
public class CompetitionResearchCategoryLinkDocs {

    public static final FieldDescriptor[] competitionResearchCategoryLinkResourceFields = {
            fieldWithPath("[].id").description("Id of the competitionResearchCategoryLinkResource"),
            fieldWithPath("[].category").description("The research category object"),
            fieldWithPath("[].className").description("The type of class for the category"),
            fieldWithPath("[].entity").description("The competition the research categories belong to")
    };

    public static final CompetitionResearchCategoryLinkResourceBuilder competitionResearchCategoryLinkBuilder =
            CompetitionResearchCategoryLinkResourceBuilder.newCompetitionResearchCategoryLinkResource()
                    .withCategory(newResearchCategoryResource().build())
                    .withCompetition(newCompetitionResource().build());

}
