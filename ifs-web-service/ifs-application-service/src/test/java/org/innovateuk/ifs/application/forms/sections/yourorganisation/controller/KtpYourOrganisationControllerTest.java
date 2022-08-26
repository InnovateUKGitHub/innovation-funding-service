package org.innovateuk.ifs.application.forms.sections.yourorganisation.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.Collection;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.KTP;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.KTP_AKT;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(Parameterized.class)
public class KtpYourOrganisationControllerTest extends BaseControllerMockMVCTest<YourOrganisationController> {

    private final FundingType fundingType;

    private long competitionId = 111L;
    private long applicationId = 123L;
    private long sectionId = 456L;
    private long organisationId = 789L;

    @Mock
    private CompetitionRestService competitionRestService;

    @Parameterized.Parameters(name = "{index}: FundingType->{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[] [] {
                {KTP}, {KTP_AKT}
        });
    }

    @Override
    protected YourOrganisationController supplyControllerUnderTest() {
        return new YourOrganisationController();
    }

    public KtpYourOrganisationControllerTest(FundingType fundingType) {
        this.fundingType = fundingType;
    }

    @Test
    public void viewYourOrganisationPageAsAnApplicant() throws Exception {

        CompetitionResource competition = newCompetitionResource()
                .withId(competitionId)
                .withFundingType(fundingType)
                .build();

        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));

        setLoggedInUser(applicant);
        mockMvc.perform(get("/application/{applicationId}/form/your-organisation/competition/{competitionId}/organisation/{organisationId}/section/{sectionId}",
                        applicationId, competitionId, organisationId, sectionId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/application/123/form/your-organisation/competition/111/organisation/789/section/456/ktp-financial-years"));
    }
}
