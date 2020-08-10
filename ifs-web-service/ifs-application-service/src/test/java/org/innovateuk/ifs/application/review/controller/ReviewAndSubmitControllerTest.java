package org.innovateuk.ifs.application.review.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.user.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.servlet.http.HttpServletResponse;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.resource.ApplicationState.SUBMITTED;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.LOAN;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ReviewAndSubmitControllerTest extends BaseControllerMockMVCTest<ReviewAndSubmitController> {

    @Mock
    private ApplicationRestService applicationRestService;
    @Mock
    private CompetitionRestService competitionRestService;
    @Mock
    private CookieFlashMessageFilter cookieFlashMessageFilter;
    @Mock
    private UserService userService;

    @Override
    protected ReviewAndSubmitController supplyControllerUnderTest() {
        return new ReviewAndSubmitController();
    }

    @Test
    public void applicationConfirmSubmit() throws Exception {
        long applicationId = 2L;

        mockMvc.perform(get("/application/" + applicationId + "/confirm-submit")
                .flashAttr("termsAgreed", true))
                .andExpect(view().name("application-confirm-submit"))
                .andExpect(model().attribute("applicationId", applicationId));

    }

    @Test
    public void applicationSubmit() throws Exception {
        ApplicationResource application = newApplicationResource()
                .withCompetitionStatus(CompetitionStatus.OPEN)
                .withApplicationState(ApplicationState.OPENED)
                .build();

        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(userService.isLeadApplicant( loggedInUser.getId(), application)).thenReturn(true);
        when(applicationRestService.updateApplicationState(application.getId(), SUBMITTED)).thenReturn(restSuccess());

        mockMvc.perform(post("/application/" + application.getId() + "/confirm-submit"))
                .andExpect(redirectedUrl("/application/" + application.getId() + "/track"));

        verify(applicationRestService).updateApplicationState(application.getId(), SUBMITTED);
    }

    @Test
    public void applicationSubmitAppisNotSubmittable() throws Exception {
        ApplicationResource application = newApplicationResource()
                .withCompetitionStatus(CompetitionStatus.CLOSED)
                .withApplicationState(ApplicationState.OPENED)
                .build();

        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(userService.isLeadApplicant(loggedInUser.getId(), application)).thenReturn(true);
        when(applicationRestService.updateApplicationState(application.getId(), SUBMITTED)).thenReturn(restSuccess());

        mockMvc.perform(post("/application/" + application.getId() + "/confirm-submit")
                .param("agreeTerms", "yes"))
                .andExpect(redirectedUrl("/application/1/confirm-submit?termsAgreed=true"));

        verify(cookieFlashMessageFilter).setFlashMessage(isA(HttpServletResponse.class), eq("cannotSubmit"));
        verify(applicationRestService, never()).updateApplicationState(any(Long.class), any(ApplicationState.class));
    }

    @Test
    public void applicationTrack() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .build();

        ApplicationResource application = newApplicationResource()
                .withApplicationState(SUBMITTED)
                .withCompetition(competition.getId())
                .build();
        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));

        mockMvc.perform(get("/application/" + application.getId() + "/track"))
                .andExpect(view().name("application-track"));
    }

    @Test
    public void loanApplicationTrack() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withFundingType(LOAN)
                .build();

        ApplicationResource application = newApplicationResource()
                .withApplicationState(SUBMITTED)
                .withCompetition(competition.getId())
                .build();
        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));

        mockMvc.perform(get("/application/" + application.getId() + "/track"))
                .andExpect(view().name("loan-application-track"));
    }

    @Test
    public void h2020GrantTransferTrack() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withFundingType(FundingType.GRANT)
                .withCompetitionTypeName("Horizon 2020")
                .build();

        ApplicationResource application = newApplicationResource()
                .withApplicationState(SUBMITTED)
                .withCompetition(competition.getId())
                .build();
        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));


        mockMvc.perform(get("/application/" + application.getId() + "/track"))
                .andExpect(view().name("h2020-grant-transfer-track"));
    }

    @Test
    public void notSubmittedApplicationTrack() throws Exception {
        ApplicationResource application = newApplicationResource()
                .withApplicationState(ApplicationState.OPENED)
                .build();
        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));


        mockMvc.perform(get("/application/" + application.getId() + "/track"))
                .andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/application/" + application.getId()));
    }
}
