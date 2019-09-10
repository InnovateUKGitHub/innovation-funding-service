package org.innovateuk.ifs.application.forms.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.summary.controller.ApplicationSummaryController;
import org.innovateuk.ifs.application.summary.populator.ApplicationSummaryViewModelPopulator;
import org.innovateuk.ifs.application.summary.viewmodel.ApplicationSummaryViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.granttransfer.service.EuGrantTransferRestService;
import org.innovateuk.ifs.interview.service.InterviewAssignmentRestService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.innovateuk.ifs.user.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MvcResult;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ApplicationSummaryControllerTest extends BaseControllerMockMVCTest<ApplicationSummaryController> {

    @Mock
    private ApplicationService applicationService;
    @Mock
    private UserService userService;
    @Mock
    private UserRestService userRestService;
    @Mock
    private CompetitionRestService competitionRestService;
    @Mock
    private InterviewAssignmentRestService interviewAssignmentRestService;
    @Mock
    private ApplicationSummaryViewModelPopulator applicationSummaryViewModelPopulator;
    @Mock
    private EuGrantTransferRestService euGrantTransferRestService;

    @Override
    protected ApplicationSummaryController supplyControllerUnderTest() {
        return new ApplicationSummaryController();
    }

    @Test
    public void applicationSummary() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withCompetitionStatus(CompetitionStatus.CLOSED)
                .build();
        ApplicationResource app = newApplicationResource().build();
        app.setCompetition(competition.getId());
        when(applicationService.getById(app.getId())).thenReturn(app);
        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(interviewAssignmentRestService.isAssignedToInterview(app.getId())).thenReturn(restSuccess(false));

        ApplicationSummaryViewModel expectedModel = mock(ApplicationSummaryViewModel.class);
        when(applicationSummaryViewModelPopulator.populate(app, competition, loggedInUser)).thenReturn(expectedModel);
        MvcResult result = mockMvc.perform(get("/application/" + app.getId() + "/summary"))
                .andExpect(status().isOk())
                .andExpect(view().name("application-summary"))
                .andReturn();

        ApplicationSummaryViewModel model = (ApplicationSummaryViewModel) result.getModelAndView().getModel().get("model");
        assertEquals(model, expectedModel);
    }
}
