package org.innovateuk.ifs.project.spendprofile.viewmodel;

import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.builder.ProjectResourceBuilder;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ProjectSpendProfileViewModelTest {

    @Test
    public void testKtpCompetition() {
        CompetitionResource competitionResource = CompetitionResourceBuilder.newCompetitionResource()
                .withFundingType(FundingType.KTP).build();
        ProjectResource projectResource = ProjectResourceBuilder.newProjectResource().build();
        OrganisationResource organisationResource = OrganisationResourceBuilder.newOrganisationResource().build();

        ProjectSpendProfileViewModel viewModel = new ProjectSpendProfileViewModel(projectResource, organisationResource,
                null, null, null, null, null, null,
                null, false, null, null, null,
                false, false, false, false, competitionResource.isKtp());

        assertTrue(viewModel.isKtpCompetition());
    }

    @Test
    public void testNonKtpCompetition() {
        CompetitionResource competitionResource = CompetitionResourceBuilder.newCompetitionResource()
                .withFundingType(FundingType.GRANT).build();
        ProjectResource projectResource = ProjectResourceBuilder.newProjectResource().build();
        OrganisationResource organisationResource = OrganisationResourceBuilder.newOrganisationResource().build();

        ProjectSpendProfileViewModel viewModel = new ProjectSpendProfileViewModel(projectResource, organisationResource,
                null, null, null, null, null, null,
                null, false, null, null, null,
                false, false, false, false, competitionResource.isKtp());

        assertFalse(viewModel.isKtpCompetition());
    }
}
