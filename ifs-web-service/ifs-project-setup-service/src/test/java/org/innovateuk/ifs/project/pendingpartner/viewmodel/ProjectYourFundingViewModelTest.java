package org.innovateuk.ifs.project.pendingpartner.viewmodel;

import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.KTP;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class ProjectYourFundingViewModelTest {

    private final boolean ktp;
    private final boolean expected;

    @Parameterized.Parameters(name = "{index}: ktp->{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[] [] {
                {true, true}, {false, false}
        });
    }

    public ProjectYourFundingViewModelTest(boolean ktp, boolean expected) {
        this.ktp = ktp;
        this.expected = expected;
    }

    @Test
    public void isKtpFundingType() {

        int organisationId = 1;
        int maximumFundingLevel = 100;
        int competitionId = 2;
        String hash = "hash";
        ProjectResource project = newProjectResource().build();

        ProjectYourFundingViewModel viewModel = new ProjectYourFundingViewModel(project, organisationId, false,
                maximumFundingLevel, competitionId, false, KTP, OrganisationTypeEnum.BUSINESS,
                false, false, false,
                Optional.empty(), false, hash, false,
                false, false, ktp);

        assertEquals(expected, viewModel.isKtpFundingType());
    }
}
