package org.innovateuk.ifs.management.competition.setup.core.sectionupdater;

import org.innovateuk.ifs.competition.resource.CompetitionCompletionStage;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.management.competition.setup.fundinglevelpercentage.form.FundingLevelPercentageForm;
import org.innovateuk.ifs.management.competition.setup.fundinglevelpercentage.sectionupdater.FundingLevelPercentageSectionUpdater;
import org.junit.Test;

import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertEquals;

public class CompetitionSetupSectionUpdaterTest {

    @Test
    public void getNextSection() {
        String expectedPath = String.format("redirect:/competition/setup/1/section/%s", CompetitionSetupSection.FUNDING_LEVEL_PERCENTAGE.getPath());

        CompetitionSetupSectionUpdater competitionSetupSectionUpdater = new FundingLevelPercentageSectionUpdater();

        FundingLevelPercentageForm form = new FundingLevelPercentageForm();

        CompetitionResource competition = newCompetitionResource()
                .withId(1L)
                .withCompletionStage(CompetitionCompletionStage.COMPETITION_CLOSE)
                .build();

        String path = competitionSetupSectionUpdater.getNextSection(form, competition, CompetitionSetupSection.FUNDING_ELIGIBILITY);
        assertEquals(expectedPath, path);
    }
}
