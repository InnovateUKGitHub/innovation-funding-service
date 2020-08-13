package org.innovateuk.ifs.project.projectdetails.viewmodel;

import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.project.builder.ProjectResourceBuilder;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ProjectDetailsStartDateViewModelTest {

    @Test
    public void testKtpCompetition() {
        ProjectResource projectResource = ProjectResourceBuilder.newProjectResource()
                .withDuration(15L).build();
        CompetitionResource competitionResource = CompetitionResourceBuilder.newCompetitionResource()
                .withFundingType(FundingType.KTP).build();

        ProjectDetailsStartDateViewModel viewModel = new ProjectDetailsStartDateViewModel(projectResource, competitionResource);

        assertTrue(viewModel.isKtpCompetition());
    }

}
