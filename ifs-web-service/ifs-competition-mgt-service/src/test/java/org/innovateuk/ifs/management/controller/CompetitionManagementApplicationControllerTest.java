package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.populator.ApplicationModelPopulator;
import org.innovateuk.ifs.application.populator.ApplicationSectionAndQuestionModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.resource.IneligibleOutcomeResource;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.management.form.ReinstateIneligibleApplicationForm;
import org.innovateuk.ifs.management.model.ApplicationOverviewIneligibilityModelPopulator;
import org.innovateuk.ifs.management.model.ReinstateIneligibleApplicationModelPopulator;
import org.innovateuk.ifs.management.service.CompetitionManagementApplicationServiceImpl;
import org.innovateuk.ifs.management.viewmodel.ApplicationOverviewIneligibilityViewModel;
import org.innovateuk.ifs.management.viewmodel.ReinstateIneligibleApplicationViewModel;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.IneligibleOutcomeResourceBuilder.newIneligibleOutcomeResource;
import static org.innovateuk.ifs.application.resource.ApplicationState.SUBMITTED;
import static org.innovateuk.ifs.application.service.Futures.settable;
import static org.innovateuk.ifs.category.builder.ResearchCategoryResourceBuilder.newResearchCategoryResource;
import static org.innovateuk.ifs.commons.error.CommonErrors.internalServerErrorError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.APPLICATION_MUST_BE_SUBMITTED;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class CompetitionManagementApplicationControllerTest extends BaseControllerMockMVCTest<CompetitionManagementApplicationController> {

    @Spy
    @InjectMocks
    private ApplicationModelPopulator applicationModelPopulator;

    @Spy
    @InjectMocks
    private ApplicationSectionAndQuestionModelPopulator applicationSectionAndQuestionModelPopulator;

    @Spy
    @InjectMocks
    private ApplicationOverviewIneligibilityModelPopulator applicationOverviewIneligibilityModelPopulator;

    @Spy
    @InjectMocks
    private CompetitionManagementApplicationServiceImpl competitionManagementApplicationServiceImpl;

    @Spy
    @InjectMocks
    private ReinstateIneligibleApplicationModelPopulator reinstateIneligibleApplicationModelPopulator;

    @Test
    public void displayApplicationOverview() throws Exception {
        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.loginDefaultUser();
        this.setupInvites();
        this.setupOrganisationTypes();
    }


    @Test
    public void displayApplicationOverview_backUrlPreservesQueryParams() throws Exception {
        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.setupApplicationResponses();
        this.loginDefaultUser();
        this.setupInvites();
        this.setupOrganisationTypes();
        this.setupResearchCategories();

        String expectedBackUrl = "/competition/" + competitionResource.getId() + "/applications/all?param1=abc&param2=def%26ghi";

        mockMvc.perform(get("/competition/{competitionId}/application/{applicationId}", competitionResource.getId(), applications.get(0).getId())
                .param("param1", "abc")
                .param("param2", "def&ghi"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition-mgt-application-overview"))
                .andExpect(model().attribute("backUrl", expectedBackUrl));
    }

    @Test
    public void displayApplicationOverview_backUrlEncodesReservedChars() throws Exception {
        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.setupApplicationResponses();
        this.loginDefaultUser();
        this.setupInvites();
        this.setupOrganisationTypes();
        this.setupResearchCategories();

        String expectedBackUrl = "/competition/" + competitionResource.getId() + "/applications/all?p1=%26&p2=%3D&p3=%25&p4=%20";

        mockMvc.perform(get("/competition/{competitionId}/application/{applicationId}", competitionResource.getId(), applications.get(0).getId())
                .param("p1", "&")
                .param("p2", "=")
                .param("p3", "%")
                .param("p4", " "))
                .andExpect(status().isOk())
                .andExpect(view().name("competition-mgt-application-overview"))
                .andExpect(model().attribute("backUrl", expectedBackUrl));
    }

    @Test
    public void displayApplicationOverview_applicationIneligible() throws Exception {
        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.setupEmptyResponses();
        this.loginDefaultUser();
        this.setupInvites();
        this.setupOrganisationTypes();
        this.setupResearchCategories();

        ZonedDateTime now = ZonedDateTime.now();

        applications.get(0).setApplicationState(ApplicationState.INELIGIBLE);
        applications.get(0).setIneligibleOutcome(newIneligibleOutcomeResource()
                .withReason("Reason for removal...")
                .withRemovedBy("Removed by")
                .withRemovedOn(now)
                .build());

        ApplicationOverviewIneligibilityViewModel expectedIneligibility = new ApplicationOverviewIneligibilityViewModel(
                "Removed by", now, "Reason for removal...");

        mockMvc.perform(get("/competition/{competitionId}/application/{applicationId}", competitionResource.getId(), applications.get(0).getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("competition-mgt-application-overview"))
                .andExpect(model().attribute("applicationReadyForSubmit", false))
                .andExpect(model().attribute("isCompManagementDownload", true))
                .andExpect(model().attribute("responses", new HashMap<>()))
                .andExpect(model().attribute("ineligibility", expectedIneligibility))
                .andExpect(model().attribute("backUrl", "/competition/" + competitionResource.getId() + "/applications/all"));
    }

    @Test
    public void displayApplicationOverview_submittedApplicationsOrigin() throws Exception {
        this.setupCompetition();
        this.setupApplicationWithRoles();

        assertApplicationOverviewWithBackUrl("SUBMITTED_APPLICATIONS",
                "/competition/" + competitionResource.getId() + "/applications/submitted");
    }

    @Test
    public void displayApplicationOverview_manageApplicationsOrigin() throws Exception {
        this.setupCompetition();
        this.setupApplicationWithRoles();

        assertApplicationOverviewWithBackUrl("MANAGE_APPLICATIONS",
                "/assessment/competition/" + competitionResource.getId());
    }

    @Test
    public void displayApplicationOverview_applicationProgressOrigin() throws Exception {
        this.setupCompetition();
        this.setupApplicationWithRoles();

        assertApplicationOverviewWithBackUrl("APPLICATION_PROGRESS",
                "/competition/" + competitionResource.getId() + "/application/" + applications.get(0).getId() + "/assessors");
    }

    @Test
    public void displayApplicationOverview_fundingApplicationsOrigin() throws Exception {
        this.setupCompetition();
        this.setupApplicationWithRoles();

        assertApplicationOverviewWithBackUrl("FUNDING_APPLICATIONS",
                "/competition/" + competitionResource.getId() + "/funding");
    }

    @Test
    public void displayApplicationOverview_IneligibleApplicationsOrigin() throws Exception {
        this.setupCompetition();
        this.setupApplicationWithRoles();

        assertApplicationOverviewWithBackUrl("INELIGIBLE_APPLICATIONS",
                "/competition/" + competitionResource.getId() + "/applications/ineligible");
    }

    @Test
    public void displayApplicationOverview_invalidOrigin() throws Exception {
        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.loginDefaultUser();
        this.setupInvites();
        this.setupOrganisationTypes();

        mockMvc.perform(get("/competition/{competitionId}/application/{applicationId}", competitionResource.getId(), applications.get(0).getId())
                .param("origin", "NOT_A_VALID_ORIGIN"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void displayApplicationForCompetitionAdministratorWithCorrectAssessorFeedbackReadonly() throws Exception {
        asList(CompetitionStatus.values()).forEach(status -> {

            this.setupCompetition();
            this.setupApplicationWithRoles();
            this.loginDefaultUser();
            this.setupInvites();
            this.setupOrganisationTypes();

            competitionResource.setCompetitionStatus(status);
        });
    }

    @Test
    public void reinstateIneligibleApplication() throws Exception {
        long competitionId = 1L;
        long applicationId = 2L;

        when(applicationRestService.updateApplicationState(applicationId, SUBMITTED)).thenReturn(restSuccess());

        mockMvc.perform(post("/competition/{competitionId}/application/{applicationId}/reinstateIneligibleApplication", competitionId, applicationId))
                .andExpect(model().attributeExists("form"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/competition/%s/applications/ineligible", competitionId)));

        verify(applicationRestService).updateApplicationState(applicationId, SUBMITTED);
        verifyNoMoreInteractions(applicationRestService);
    }

    @Test
    public void reinstateIneligibleApplication_failureUpdatingState() throws Exception {
        long competitionId = 1L;

        ApplicationResource applicationResource = newApplicationResource()
                .withCompetition(competitionId)
                .withName("Plastic reprocessing with zero waste")
                .build();

        when(applicationRestService.updateApplicationState(applicationResource.getId(), SUBMITTED)).thenReturn(restFailure(internalServerErrorError()));
        when(applicationRestService.getApplicationById(applicationResource.getId())).thenReturn(restSuccess(applicationResource));

        ReinstateIneligibleApplicationViewModel expectedViewModel = new ReinstateIneligibleApplicationViewModel(competitionId,
                applicationResource.getId(), "Plastic reprocessing with zero waste");

        MvcResult mvcResult = mockMvc.perform(post("/competition/{competitionId}/application/{applicationId}/reinstateIneligibleApplication", competitionId, applicationResource.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(view().name("application/reinstate-ineligible-application-confirm"))
                .andReturn();

        ReinstateIneligibleApplicationForm form = (ReinstateIneligibleApplicationForm) mvcResult.getModelAndView().getModel().get("form");

        BindingResult bindingResult = form.getBindingResult();
        assertEquals(1, bindingResult.getGlobalErrorCount());
        assertFalse(bindingResult.hasFieldErrors());
        assertEquals(CommonFailureKeys.GENERAL_UNEXPECTED_ERROR.getErrorKey(), bindingResult.getGlobalError().getCode());

        InOrder inOrder = inOrder(applicationRestService);
        inOrder.verify(applicationRestService).updateApplicationState(applicationResource.getId(), SUBMITTED);
        inOrder.verify(applicationRestService).getApplicationById(applicationResource.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void reinstateIneligibleApplicationConfirm() throws Exception {
        long competitionId = 1L;

        ApplicationResource applicationResource = newApplicationResource()
                .withCompetition(competitionId)
                .withName("Plastic reprocessing with zero waste")
                .build();

        when(applicationRestService.getApplicationById(applicationResource.getId())).thenReturn(restSuccess(applicationResource));

        ReinstateIneligibleApplicationViewModel expectedViewModel = new ReinstateIneligibleApplicationViewModel(competitionId,
                applicationResource.getId(), "Plastic reprocessing with zero waste");

        mockMvc.perform(get("/competition/{competitionId}/application/{applicationId}/reinstateIneligibleApplication/confirm",
                competitionId, applicationResource.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("application/reinstate-ineligible-application-confirm"));

        verify(applicationRestService).getApplicationById(applicationResource.getId());
        verifyNoMoreInteractions(applicationRestService);
    }

    @Test
    public void markAsIneligible() throws Exception {
        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.loginDefaultUser();
        this.setupInvites();
        this.setupOrganisationTypes();

        IneligibleOutcomeResource ineligibleOutcomeResource = newIneligibleOutcomeResource().build();
        when(applicationService.markAsIneligible(eq(applications.get(0).getId()), eq(ineligibleOutcomeResource))).thenReturn(serviceSuccess());

        mockMvc.perform(post("/competition/{competitionId}/application/{applicationId}", competitionResource.getId(), applications.get(0).getId())
                .param("markAsIneligible", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/competition/" + competitionResource.getId() + "/applications/ineligible"));

        verify(applicationService).markAsIneligible(eq(applications.get(0).getId()), eq(ineligibleOutcomeResource));
    }

    @Test
    public void markAsIneligible_notSubmitted() throws Exception {
        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.loginDefaultUser();
        this.setupInvites();
        this.setupOrganisationTypes();

        IneligibleOutcomeResource ineligibleOutcomeResource = newIneligibleOutcomeResource().build();
        ProcessRoleResource userApplicationRole = newProcessRoleResource().withApplication(applications.get(0).getId()).withOrganisation(organisations.get(0).getId()).build();
        List<ResearchCategoryResource> researchCategories = newResearchCategoryResource().build(3);

        when(formInputResponseRestService.getResponsesByApplicationId(applications.get(0).getId())).thenReturn(restSuccess(new ArrayList<>()));
        when(financeHandler.getFinanceModelManager(OrganisationTypeEnum.BUSINESS.getId())).thenReturn(defaultFinanceModelManager);
        when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));
        when(userRestServiceMock.findProcessRole(loggedInUser.getId(), applications.get(0).getId())).thenReturn(restSuccess(userApplicationRole));
        when(categoryRestServiceMock.getResearchCategories()).thenReturn(restSuccess(researchCategories));
        when(applicationService.markAsIneligible(eq(applications.get(0).getId()), eq(ineligibleOutcomeResource))).thenReturn(serviceFailure(new Error(APPLICATION_MUST_BE_SUBMITTED)));

        mockMvc.perform(post("/competition/" + competitionResource.getId() + "/application/" + applications.get(0).getId() + "")
                .param("markAsIneligible", ""))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("competition-mgt-application-overview"));

        verify(applicationService).markAsIneligible(eq(applications.get(0).getId()), eq(ineligibleOutcomeResource));
    }

    @Test
    public void markAsIneligible_correctBackUrlAfterSubmission() throws Exception {
        String reason = "Test reason";

        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.loginDefaultUser();
        this.setupInvites();
        this.setupOrganisationTypes();

        IneligibleOutcomeResource ineligibleOutcomeResource = newIneligibleOutcomeResource().withReason(reason).build();
        ProcessRoleResource userApplicationRole = newProcessRoleResource().withApplication(applications.get(0).getId()).withOrganisation(organisations.get(0).getId()).build();
        List<ResearchCategoryResource> researchCategories = newResearchCategoryResource().build(3);

        when(formInputResponseRestService.getResponsesByApplicationId(applications.get(0).getId())).thenReturn(restSuccess(new ArrayList<>()));
        when(financeHandler.getFinanceModelManager(OrganisationTypeEnum.BUSINESS.getId())).thenReturn(defaultFinanceModelManager);
        when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));
        when(userRestServiceMock.findProcessRole(loggedInUser.getId(), applications.get(0).getId())).thenReturn(restSuccess(userApplicationRole));
        when(categoryRestServiceMock.getResearchCategories()).thenReturn(restSuccess(researchCategories));
        when(applicationService.markAsIneligible(eq(applications.get(0).getId()), eq(ineligibleOutcomeResource))).thenReturn(serviceFailure(new Error(APPLICATION_MUST_BE_SUBMITTED)));

        mockMvc.perform(post("/competition/" + competitionResource.getId() + "/application/" + applications.get(0).getId() + "?origin=SUBMITTED_APPLICATIONS&page=2&sort=name")
                .param("markAsIneligible", "")
                .param("ineligibleReason", reason))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("competition-mgt-application-overview"))
                .andExpect(model().attribute("backUrl", "/competition/" + competitionResource.getId() + "/applications/submitted?page=2&sort=name"));

        verify(applicationService).markAsIneligible(eq(applications.get(0).getId()), eq(ineligibleOutcomeResource));
    }

    private void assertApplicationOverviewWithBackUrl(final String origin, final String expectedBackUrl) throws Exception {
        this.setupApplicationResponses();
        this.setupInvites();
        this.setupResearchCategories();

        mockMvc.perform(get("/competition/{competitionId}/application/{applicationId}", competitionResource.getId(), applications.get(0).getId())
                .param("origin", origin))
                .andExpect(status().isOk())
                .andExpect(view().name("competition-mgt-application-overview"))
                .andExpect(model().attribute("backUrl", expectedBackUrl));
    }

    private void setupEmptyResponses() {
        when(formInputResponseRestService.getResponsesByApplicationId(applications.get(0).getId())).thenReturn(restSuccess(new ArrayList<>()));
    }

    private List<ResearchCategoryResource> setupResearchCategories() {
        List<ResearchCategoryResource> researchCategories = newResearchCategoryResource().build(3);
        when(categoryRestServiceMock.getResearchCategories()).thenReturn(restSuccess(researchCategories));
        return researchCategories;
    }

    @Override
    protected CompetitionManagementApplicationController supplyControllerUnderTest() {
        return new CompetitionManagementApplicationController();
    }
}
