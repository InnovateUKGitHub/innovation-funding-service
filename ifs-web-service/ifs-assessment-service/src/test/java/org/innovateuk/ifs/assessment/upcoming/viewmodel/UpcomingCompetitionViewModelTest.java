package org.innovateuk.ifs.assessment.upcoming.viewmodel;

import org.innovateuk.ifs.assessment.invite.viewmodel.CompetitionInviteViewModel;
import org.innovateuk.ifs.competition.builder.CompetitionAssessmentConfigResourceBuilder;
import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionAssessmentConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UpcomingCompetitionViewModelTest {

    @Test
    public void testNonKtpCompetition() {

        CompetitionResource competitionResource = CompetitionResourceBuilder.newCompetitionResource()
                .withFundingType(FundingType.GRANT)
                .build();

        CompetitionAssessmentConfigResource competitionAssessmentConfigResource = CompetitionAssessmentConfigResourceBuilder
                .newCompetitionAssessmentConfigResource().build();

        UpcomingCompetitionViewModel viewModel = new UpcomingCompetitionViewModel(competitionResource, competitionAssessmentConfigResource);

        assertFalse(viewModel.isKtpCompetition());
    }

    @Test
    public void testKtpCompetition() {
        CompetitionResource competitionResource = CompetitionResourceBuilder.newCompetitionResource()
                .withFundingType(FundingType.KTP)
                .build();

        CompetitionAssessmentConfigResource competitionAssessmentConfigResource = CompetitionAssessmentConfigResourceBuilder
                .newCompetitionAssessmentConfigResource().build();

        UpcomingCompetitionViewModel viewModel = new UpcomingCompetitionViewModel(competitionResource, competitionAssessmentConfigResource);

        assertTrue(viewModel.isKtpCompetition());
    }
}
