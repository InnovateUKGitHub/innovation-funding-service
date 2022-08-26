package org.innovateuk.ifs.project.pendingpartner.viewmodel;

import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.KTP;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.KTP_AKT;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class ProjectYourFundingViewModelTest {

    private final FundingType fundingType;

    @Parameterized.Parameters(name = "{index}: FundingType->{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[] [] {
                {KTP}, {KTP_AKT}
        });
    }

    public ProjectYourFundingViewModelTest(FundingType fundingType) {
        this.fundingType = fundingType;
    }

    @Test
    public void isKtpFundingType() {

        int organisationId = 1;
        int maximumFundingLevel = 100;
        int competitionId = 2;
        String hash = "hash";
        ProjectResource project = newProjectResource().build();

        ProjectYourFundingViewModel viewModel = new ProjectYourFundingViewModel(project, organisationId, false,
                maximumFundingLevel, competitionId, false, fundingType, OrganisationTypeEnum.BUSINESS,
                false, false, false,
                Optional.empty(), false, hash, false);

        assertTrue(viewModel.isKtpFundingType());
    }
}
