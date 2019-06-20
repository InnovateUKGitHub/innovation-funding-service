package org.innovateuk.ifs.application.review.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.forms.form.ApplicationSubmitForm;
import org.innovateuk.ifs.application.populator.ApplicationModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.review.populator.ReviewAndSubmitViewModelPopulator;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.user.service.UserRestService;
import org.innovateuk.ifs.user.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletResponse;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.resource.ApplicationState.SUBMITTED;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ReviewAndSubmitControllerTest extends BaseControllerMockMVCTest<ReviewAndSubmitController> {

    @Mock
    private ReviewAndSubmitViewModelPopulator reviewAndSubmitViewModelPopulator;
    @Mock
    private ApplicationRestService applicationRestService;
    @Mock
    private CompetitionRestService competitionRestService;
    @Mock
    private ApplicationModelPopulator applicationModelPopulator;
    @Mock
    private CookieFlashMessageFilter cookieFlashMessageFilter;
    @Mock
    private UserService userService;
    @Mock
    private QuestionStatusRestService questionStatusRestService;
    @Mock
    private UserRestService userRestService;
    @Mock
    private QuestionRestService questionRestService;

    @Override
    protected ReviewAndSubmitController supplyControllerUnderTest() {
        return new ReviewAndSubmitController(reviewAndSubmitViewModelPopulator, applicationRestService, competitionRestService, applicationModelPopulator, cookieFlashMessageFilter, userService, questionStatusRestService, userRestService, questionRestService);
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
                .withApplicationState(ApplicationState.OPEN)
                .build();

        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(applicationModelPopulator.userIsLeadApplicant(application, loggedInUser.getId())).thenReturn(true);
        when(applicationRestService.updateApplicationState(application.getId(), SUBMITTED)).thenReturn(restSuccess());

        mockMvc.perform(post("/application/" + application.getId() + "/confirm-submit"))
                .andExpect(redirectedUrl("/application/" + application.getId() + "/track"));

        verify(applicationRestService).updateApplicationState(application.getId(), SUBMITTED);
    }

    @Test
    public void applicationSubmitAppisNotSubmittable() throws Exception {
        ApplicationResource application = newApplicationResource()
                .withCompetitionStatus(CompetitionStatus.CLOSED)
                .withApplicationState(ApplicationState.OPEN)
                .build();

        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(applicationModelPopulator.userIsLeadApplicant(application, loggedInUser.getId())).thenReturn(true);
        when(applicationRestService.updateApplicationState(application.getId(), SUBMITTED)).thenReturn(restSuccess());

        mockMvc.perform(post("/application/" + application.getId() + "/confirm-submit")
                .param("agreeTerms", "yes"))
                .andExpect(redirectedUrl("/application/1/confirm-submit"));

        verify(cookieFlashMessageFilter).setFlashMessage(isA(HttpServletResponse.class), eq("cannotSubmit"));
        verify(applicationRestService, never()).updateApplicationState(any(Long.class), any(ApplicationState.class));
    }

    @Test
    public void applicationSummaryProcurementSubmitAgreeToTerms() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withFundingType(FundingType.PROCUREMENT)
                .build();

        ApplicationResource application = newApplicationResource()
                .withCompetition(competition.getId())
                .build();


        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));

        MvcResult result = mockMvc.perform(post("/application/" + application.getId() + "/review-and-submit")
                .param("agreeTerms", "false")
                .param("submit-application", ""))
                .andExpect(redirectedUrl("/application/" + application.getId() + "/summary"))
                .andReturn();

        BindingResult bindingResult = (BindingResult) result.getFlashMap().get(BindingResult.class.getCanonicalName() + "." + ReviewAndSubmitController.FORM_ATTR_NAME);
        ApplicationSubmitForm submitForm = (ApplicationSubmitForm) result.getFlashMap().get(ReviewAndSubmitController.FORM_ATTR_NAME);

        assertFalse(submitForm.isAgreeTerms());
        assertEquals("validation.application.procurement.terms.required", bindingResult.getFieldError("agreeTerms").getCode());
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
                .withApplicationState(ApplicationState.OPEN)
                .build();
        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));


        mockMvc.perform(get("/application/" + application.getId() + "/track"))
                .andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/application/" + application.getId()));
    }
}
