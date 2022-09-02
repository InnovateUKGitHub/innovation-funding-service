package org.innovateuk.ifs.project.monitoringofficer.viewmodel;

import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.innovateuk.ifs.application.builder.CompetitionSummaryResourceBuilder.newCompetitionSummaryResource;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.KTP;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.KTP_AKT;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class MonitoringOfficerViewModelTest {

    private final FundingType fundingType;

    @Parameterized.Parameters(name = "{index}: FundingType->{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[] [] {
                {KTP}, {KTP_AKT}
        });
    }

    public MonitoringOfficerViewModelTest(FundingType fundingType) {
        this.fundingType = fundingType;
    }

    @Test
    public void isKtp() {
        String area = "area";
        String projectManagerName = "projectManagerName";
        String leadOrganisationName = "leadOrganisationName";
        ProjectResource project = newProjectResource().build();
        CompetitionSummaryResource competitionSummary = newCompetitionSummaryResource()
                .withFundingType(fundingType)
                .build();

        MonitoringOfficerViewModel viewModel = new MonitoringOfficerViewModel(project, area, projectManagerName,
                Collections.emptyList(), leadOrganisationName, competitionSummary, false, true);

        assertTrue(viewModel.isKtp());
    }
}
