package org.innovateuk.ifs.assessment.feedback.viewmodel;

import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AssessmentFeedbackApplicationDetailsViewModelTest {

    @Test
    public void testKtpCompetition() {
        long applicationId = 1L;
        long applicationDurationInMonths = 15L;
        long daysLeft = 10L;
        long daysLeftPercentage = 5L;

        CompetitionResource competitionResource = CompetitionResourceBuilder.newCompetitionResource()
                .withFundingType(FundingType.KTP).build();

        AssessmentFeedbackApplicationDetailsViewModel viewModel = new AssessmentFeedbackApplicationDetailsViewModel(applicationId, null, null,
                applicationDurationInMonths, daysLeft, daysLeftPercentage, null, competitionResource.isKtp());

        assertTrue(viewModel.isKtpCompetition());
    }

    @Test
    public void testNonKtpCompetition() {
        long applicationId = 1L;
        long applicationDurationInMonths = 15L;
        long daysLeft = 10L;
        long daysLeftPercentage = 5L;

        CompetitionResource competitionResource = CompetitionResourceBuilder.newCompetitionResource()
                .withFundingType(FundingType.GRANT).build();

        AssessmentFeedbackApplicationDetailsViewModel viewModel = new AssessmentFeedbackApplicationDetailsViewModel(applicationId, null, null,
                applicationDurationInMonths, daysLeft, daysLeftPercentage, null, competitionResource.isKtp());

        assertFalse(viewModel.isKtpCompetition());
    }
}
