package org.innovateuk.ifs.assessment.upcoming.viewmodel;

import org.innovateuk.ifs.competition.builder.CompetitionAssessmentConfigResourceBuilder;
import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.resource.CompetitionAssessmentConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.junit.Test;

import static org.innovateuk.ifs.publiccontent.builder.PublicContentItemResourceBuilder.newPublicContentItemResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
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

        PublicContentResource publicContent = newPublicContentResource().build();
        PublicContentItemResource publicContentItem = newPublicContentItemResource().withPublicContentResource(publicContent).build();

        String hash = publicContentItem.getPublicContentResource().getHash();

        UpcomingCompetitionViewModel viewModel = new UpcomingCompetitionViewModel(competitionResource, competitionAssessmentConfigResource, hash);

        assertFalse(viewModel.isKtpCompetition());
        assertFalse(viewModel.isAlwaysOpenCompetition());
    }

    @Test
    public void testKtpCompetition() {
        CompetitionResource competitionResource = CompetitionResourceBuilder.newCompetitionResource()
                .withFundingType(FundingType.KTP)
                .build();

        CompetitionAssessmentConfigResource competitionAssessmentConfigResource = CompetitionAssessmentConfigResourceBuilder
                .newCompetitionAssessmentConfigResource().build();

        PublicContentResource publicContent = newPublicContentResource().build();
        PublicContentItemResource publicContentItem = newPublicContentItemResource().withPublicContentResource(publicContent).build();

        String hash = publicContentItem.getPublicContentResource().getHash();

        UpcomingCompetitionViewModel viewModel = new UpcomingCompetitionViewModel(competitionResource, competitionAssessmentConfigResource, hash);

        assertTrue(viewModel.isKtpCompetition());
        assertFalse(viewModel.isAlwaysOpenCompetition());
    }

    @Test
    public void testAlwaysOpenCompetition() {
        CompetitionResource competitionResource = CompetitionResourceBuilder.newCompetitionResource()
                .withFundingType(FundingType.GRANT)
                .withAlwaysOpen(true)
                .build();

        CompetitionAssessmentConfigResource competitionAssessmentConfigResource = CompetitionAssessmentConfigResourceBuilder
                .newCompetitionAssessmentConfigResource().build();

        PublicContentResource publicContent = newPublicContentResource().build();
        PublicContentItemResource publicContentItem = newPublicContentItemResource().withPublicContentResource(publicContent).build();

        String hash = publicContentItem.getPublicContentResource().getHash();

        UpcomingCompetitionViewModel viewModel = new UpcomingCompetitionViewModel(competitionResource, competitionAssessmentConfigResource, hash);

        assertTrue(viewModel.isAlwaysOpenCompetition());
    }
}
