package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.populator.ApplicationModelPopulator;
import org.innovateuk.ifs.application.populator.ApplicationSectionAndQuestionModelPopulator;
import org.innovateuk.ifs.application.populator.forminput.FormInputViewModelGenerator;
import org.innovateuk.ifs.application.resource.*;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.error.exception.ForbiddenActionException;
import org.innovateuk.ifs.commons.error.exception.ObjectNotFoundException;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.management.form.ReinstateIneligibleApplicationForm;
import org.innovateuk.ifs.management.model.ApplicationOverviewIneligibilityModelPopulator;
import org.innovateuk.ifs.management.model.ApplicationTeamModelPopulator;
import org.innovateuk.ifs.management.model.ReinstateIneligibleApplicationModelPopulator;
import org.innovateuk.ifs.management.service.CompetitionManagementApplicationServiceImpl;
import org.innovateuk.ifs.management.viewmodel.ApplicationOverviewIneligibilityViewModel;
import org.innovateuk.ifs.management.viewmodel.ApplicationTeamViewModel;
import org.innovateuk.ifs.management.viewmodel.ReinstateIneligibleApplicationViewModel;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;

import java.time.ZonedDateTime;
import java.util.*;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantQuestionResourceBuilder.newApplicantQuestionResource;
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
import static org.innovateuk.ifs.organisation.builder.OrganisationAddressResourceBuilder.newOrganisationAddressResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
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

    @Mock
    private ApplicantRestService applicantRestService;

    @Mock
    private FormInputViewModelGenerator formInputViewModelGenerator;

    @Spy
    @InjectMocks
    private ReinstateIneligibleApplicationModelPopulator reinstateIneligibleApplicationModelPopulator;

    @Spy
    @InjectMocks
    private ApplicationTeamModelPopulator applicationTeamModelPopulator;

    @Test
    public void displayApplicationOverview() throws Exception {
        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.setupApplicationResponses();
        this.loginDefaultUser();
        this.setupInvites();
        this.setupOrganisationTypes();
        this.setupResearchCategories();
        setupApplicantResource();

        mockMvc.perform(get("/competition/{competitionId}/application/{applicationId}", competitionResource.getId(), applications.get(0).getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("competition-mgt-application-overview"))
                .andExpect(model().attribute("isSupportUser", false));
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
        setupApplicantResource();

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
        setupApplicantResource();

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
        setupApplicantResource();

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
        this.setupApplicationResponses();
        this.loginDefaultUser();
        this.setupInvites();
        this.setupOrganisationTypes();
        this.setupResearchCategories();
        setupApplicantResource();

        assertApplicationOverviewWithBackUrl("MANAGE_APPLICATIONS",
                "/assessment/competition/" + competitionResource.getId() + "/applications");
    }

    @Test
    public void displayApplicationOverview_applicationProgressOrigin() throws Exception {
        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.setupApplicationResponses();
        this.loginDefaultUser();
        this.setupInvites();
        this.setupOrganisationTypes();
        this.setupResearchCategories();
        setupApplicantResource();

        assertApplicationOverviewWithBackUrl("APPLICATION_PROGRESS",
                "/assessment/competition/" + competitionResource.getId() + "/application/" + applications.get(0).getId() + "/assessors");
    }

    @Test
    public void displayApplicationOverview_fundingApplicationsOrigin() throws Exception {
        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.setupApplicationResponses();
        this.loginDefaultUser();
        this.setupInvites();
        this.setupOrganisationTypes();
        this.setupResearchCategories();
        setupApplicantResource();

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
        setupApplicantResource();

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
            setupApplicantResource();

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

        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.loginDefaultUser();
        this.setupInvites();
        this.setupOrganisationTypes();
        setupApplicantResource();
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

        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.loginDefaultUser();
        this.setupInvites();
        this.setupOrganisationTypes();
        setupApplicantResource();
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
        setupApplicantResource();

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
        setupApplicantResource();

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
    public void showApplicationTeam() throws Exception {
        this.setupCompetition();
        this.setupApplicationWithRoles();
        ApplicationTeamResource teamResource = new ApplicationTeamResource();
        ApplicationTeamOrganisationResource leadOrg = new ApplicationTeamOrganisationResource();
        leadOrg.setOrganisationName("lead");
        AddressResource regAddress = newAddressResource().withAddressLine1("1").withAddressLine2("Floor 1").withAddressLine3("Polaris House").withCounty("Wilts").withPostcode("SN1 1AB").withTown("Swindon").build();
        AddressResource opAddress = newAddressResource().withAddressLine1("A").withAddressLine2("Floor G").withAddressLine3("North Star House").withCounty("Somerset").withPostcode("TN1 1ZZ").withTown("Taunton").build();
        OrganisationAddressResource regAddressResource = newOrganisationAddressResource().withAddress(regAddress).build();
        OrganisationAddressResource opAddressResource = newOrganisationAddressResource().withAddress(opAddress).build();
        leadOrg.setOperatingAddress(opAddressResource);
        leadOrg.setRegisteredAddress(regAddressResource);
        ApplicationTeamUserResource leaderUser = new ApplicationTeamUserResource();
        leaderUser.setEmail("lee.der@email.com");
        leaderUser.setName("Lee Der");
        leaderUser.setPhoneNumber("0800 0800");
        leaderUser.setLead(true);
        leadOrg.setUsers(Collections.singletonList(leaderUser));
        ApplicationTeamOrganisationResource partnerOrg = new ApplicationTeamOrganisationResource();
        partnerOrg.setOrganisationName("Partner");
        partnerOrg.setOperatingAddress(regAddressResource);
        partnerOrg.setRegisteredAddress(opAddressResource);
        ApplicationTeamUserResource partnerUser = new ApplicationTeamUserResource();
        partnerUser.setEmail("pard.ner@email.com");
        partnerUser.setName("Pard Ner");
        partnerUser.setPhoneNumber("0900 9999");
        partnerUser.setLead(false);
        partnerOrg.setUsers(Collections.singletonList(partnerUser));
        teamResource.setLeadOrganisation(leadOrg);
        teamResource.setPartnerOrganisations(Collections.singletonList(partnerOrg));
        when(applicationRestService.getApplicationById(applications.get(0).getId())).thenReturn(restSuccess(applications.get(0)));
        when(applicationSummaryRestService.getApplicationTeam(applications.get(0).getId())).thenReturn(restSuccess(teamResource));
        MvcResult mvcResult = mockMvc.perform(get("/competition/" + competitionResource.getId() + "/application/" + applications.get(0).getId() + "/team"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("application/team-read-only"))
                .andReturn();

        ApplicationTeamViewModel resultModel = (ApplicationTeamViewModel) mvcResult.getModelAndView().getModel().get("model");
        assertEquals(applications.get(0).getId().longValue(), resultModel.getApplicationId());
        assertEquals(applications.get(0).getCompetition().longValue(), resultModel.getCompetitionId());
        assertEquals(applications.get(0).getName(), resultModel.getApplicationName());
        assertEquals("lead", resultModel.getTeam().getLeadOrganisation().getOrganisationName());
        assertEquals("TN1 1ZZ", resultModel.getTeam().getLeadOrganisation().getOperatingAddress().getAddress().getPostcode());
        assertEquals("SN1 1AB", resultModel.getTeam().getLeadOrganisation().getRegisteredAddress().getAddress().getPostcode());
        assertEquals("TN1 1ZZ", resultModel.getTeam().getPartnerOrganisations().get(0).getRegisteredAddress().getAddress().getPostcode());
        assertEquals("SN1 1AB", resultModel.getTeam().getPartnerOrganisations().get(0).getOperatingAddress().getAddress().getPostcode());
        assertEquals("lee.der@email.com", resultModel.getTeam().getLeadOrganisation().getUsers().get(0).getEmail());
        assertEquals("pard.ner@email.com", resultModel.getTeam().getPartnerOrganisations().get(0).getUsers().get(0).getEmail());
        verify(applicationRestService).getApplicationById(applications.get(0).getId());
        verify(applicationSummaryRestService).getApplicationTeam(applications.get(0).getId());
    }

    @Test
    public void showApplicationTeamNoApplication() throws Exception {
        this.setupCompetition();
        this.setupApplicationWithRoles();

        when(applicationRestService.getApplicationById(applications.get(0).getId())).thenThrow(new ForbiddenActionException());
        mockMvc.perform(get("/competition/" + competitionResource.getId() + "/application/" + applications.get(0).getId() + "/team"))
                .andExpect(status().isForbidden())
                .andExpect(view().name("forbidden"));
        verify(applicationRestService).getApplicationById(applications.get(0).getId());
    }

    @Test
    public void showApplicationTeamNoTeam() throws Exception {
        this.setupCompetition();
        this.setupApplicationWithRoles();

        when(applicationRestService.getApplicationById(applications.get(0).getId())).thenReturn(restSuccess(applications.get(0)));
        when(applicationSummaryRestService.getApplicationTeam(applications.get(0).getId())).thenThrow(new ObjectNotFoundException());
        mockMvc.perform(get("/competition/" + competitionResource.getId() + "/application/" + applications.get(0).getId() + "/team"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("404"));
        verify(applicationRestService).getApplicationById(applications.get(0).getId());
        verify(applicationSummaryRestService).getApplicationTeam(applications.get(0).getId());
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

    private void setupApplicantResource() {
        when(applicantRestService.getQuestion(anyLong(), anyLong(), anyLong())).thenReturn(newApplicantQuestionResource().build());
        when(formInputViewModelGenerator.fromQuestion(any(), any())).thenReturn(Collections.emptyList());
    }

    @Override
    protected CompetitionManagementApplicationController supplyControllerUnderTest() {
        return new CompetitionManagementApplicationController();
    }
}
