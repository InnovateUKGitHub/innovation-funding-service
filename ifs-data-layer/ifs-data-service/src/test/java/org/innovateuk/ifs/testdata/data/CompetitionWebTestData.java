package org.innovateuk.ifs.testdata.data;

import org.innovateuk.ifs.competition.resource.CompetitionResource;

import java.util.List;

import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;

public class CompetitionWebTestData {

    public static List<CompetitionResource> buildCompetitionResources() {
        return newCompetitionResource()
                .withName()
                .withCompetitionType()
                .withInnovationAreas()
                .withInnovationSectorName()
                .withFundingRules()
                .withResearchCategories()
                .withLeadTechnologist()
                .withExecutiveName()
                .withBudgetCode()
                .withPafCode()
                .withCompetitionCode()
                .withActivityCode()
                .withMultiStream()
                .withCollaborationLevel()
                .withLeadApplicantType()
                .withMaxResearchRatio()
                .withResubmission()
                .withNonIfsUrl()
                .withFundingType()
                .withCompletionStage()
                .withIncludeJesForm()
                .withApplicationFinanceType()
                .withIncludeProjectGrowthTable()
                .withIncludeYourOrganisationSection()
                .withAlwaysOpen()
                .build(2);
    }
}
