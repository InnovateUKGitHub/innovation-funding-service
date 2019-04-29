package org.innovateuk.ifs.application.terms.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.GrantTermsAndConditionsResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ApplicationTermsControllerTest extends BaseControllerMockMVCTest<ApplicationTermsController> {

    @Mock
    private ApplicationRestService applicationRestServiceMock;
    @Mock
    private CompetitionRestService competitionRestServiceMock;

    @Override
    protected ApplicationTermsController supplyControllerUnderTest() {
        return new ApplicationTermsController(applicationRestServiceMock, competitionRestServiceMock);
    }

    @Test
    public void getTerms() throws Exception {
        testGetTerms(false);
    }

    @Test
    public void getTerms_collaborative() throws Exception {
        testGetTerms(true);
    }

    private void testGetTerms(boolean collaborative) throws Exception {
        String termsTemplate = "terms-template";

        GrantTermsAndConditionsResource grantTermsAndConditions =
                new GrantTermsAndConditionsResource("name", termsTemplate, 1);
        CompetitionResource competition = newCompetitionResource()
                .withTermsAndConditions(grantTermsAndConditions)
                .build();
        ApplicationResource application = newApplicationResource()
                .withCompetition(competition.getId())
                .withCollaborativeProject(collaborative)
                .build();

        when(applicationRestServiceMock.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(competitionRestServiceMock.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));

        mockMvc.perform(get("/application/{applicationId}/terms-and-conditions", application.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("applicationId", application.getId()))
                .andExpect(model().attribute("template", grantTermsAndConditions.getTemplate()))
                .andExpect(model().attribute("collaborative", collaborative))
                .andExpect(view().name("application/terms-and-conditions"));

        InOrder inOrder = inOrder(applicationRestServiceMock, competitionRestServiceMock);
        inOrder.verify(applicationRestServiceMock).getApplicationById(application.getId());
        inOrder.verify(competitionRestServiceMock).getCompetitionById(competition.getId());
        inOrder.verifyNoMoreInteractions();
    }
}