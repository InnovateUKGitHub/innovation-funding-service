package org.innovateuk.ifs.project.spendprofile.viewmodel;

import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.project.builder.ProjectResourceBuilder;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TotalSpendProfileViewModelTest {

    @Test
    public void testKtpCompetition() {
        CompetitionResource competitionResource = CompetitionResourceBuilder.newCompetitionResource()
                .withFundingType(FundingType.KTP).build();
        ProjectResource projectResource = ProjectResourceBuilder.newProjectResource().build();

        TotalSpendProfileViewModel viewModel = new TotalSpendProfileViewModel(projectResource, null, null, competitionResource.isKtp());

        assertTrue(viewModel.isKtpCompetition());
    }

    @Test
    public void testNonKtpCompetition() {
        CompetitionResource competitionResource = CompetitionResourceBuilder.newCompetitionResource()
                .withFundingType(FundingType.GRANT).build();
        ProjectResource projectResource = ProjectResourceBuilder.newProjectResource().build();

        TotalSpendProfileViewModel viewModel = new TotalSpendProfileViewModel(projectResource, null, null, competitionResource.isKtp());

        assertFalse(viewModel.isKtpCompetition());
    }
}
