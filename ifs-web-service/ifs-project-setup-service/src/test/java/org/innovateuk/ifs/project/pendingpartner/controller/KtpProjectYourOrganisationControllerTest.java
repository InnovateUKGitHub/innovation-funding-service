package org.innovateuk.ifs.project.pendingpartner.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collection;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.KTP;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.KTP_AKT;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlTemplate;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(Parameterized.class)
public class KtpProjectYourOrganisationControllerTest extends BaseControllerMockMVCTest<ProjectYourOrganisationController> {

    private final FundingType fundingType;

    private CompetitionResource competition;
    private ProjectResource project;
    private String baseUrl;
    private static final long projectId = 3L;
    private static final long organisationId = 5L;
    private static final long competitionId = 7L;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private ProjectRestService projectRestService;

    @Parameterized.Parameters(name = "{index}: FundingType->{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[] [] {
                {KTP}, {KTP_AKT}
        });
    }

    @Override
    protected ProjectYourOrganisationController supplyControllerUnderTest() {
        return new ProjectYourOrganisationController();
    }

    public KtpProjectYourOrganisationControllerTest(FundingType fundingType) {
        this.fundingType = fundingType;
    }

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        baseUrl = format("/project/%d/organisation/%d/your-organisation", projectId, organisationId);
        competition = newCompetitionResource()
                .withId(competitionId)
                .withFundingType(FundingType.KTP)
                .build();
        project = newProjectResource()
                .withId(projectId)
                .withCompetition(competition.getId())
                .build();
    }

    @Test
    public void viewPage_redirectToKtpFinancialYears() throws Exception {
        when(projectRestService.getProjectById(projectId)).thenReturn(restSuccess(project));
        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));
        competition.setIncludeProjectGrowthTable(true);

        mockMvc.perform(get(baseUrl))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlTemplate(baseUrl +"/ktp-financial-years"));
    }
}
