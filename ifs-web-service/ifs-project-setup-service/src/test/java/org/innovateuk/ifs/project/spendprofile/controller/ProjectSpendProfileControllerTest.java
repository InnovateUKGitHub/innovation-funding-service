package org.innovateuk.ifs.project.spendprofile.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.rest.LocalDateResource;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.constant.ProjectActivityStates;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectPartnerStatusResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
import org.innovateuk.ifs.project.spendprofile.SpendProfileSummaryModel;
import org.innovateuk.ifs.project.spendprofile.SpendProfileSummaryYearModel;
import org.innovateuk.ifs.project.spendprofile.SpendProfileTableCalculator;
import org.innovateuk.ifs.project.spendprofile.form.SpendProfileForm;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileResource;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileTableResource;
import org.innovateuk.ifs.project.spendprofile.validation.SpendProfileCostValidator;
import org.innovateuk.ifs.project.spendprofile.viewmodel.ProjectSpendProfileProjectSummaryViewModel;
import org.innovateuk.ifs.project.spendprofile.viewmodel.ProjectSpendProfileViewModel;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.spendprofile.SpendProfileService;
import org.innovateuk.ifs.status.StatusService;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.ObjectError;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.project.builder.ProjectPartnerStatusResourceBuilder.newProjectPartnerStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.builder.ProjectTeamStatusResourceBuilder.newProjectTeamStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.project.builder.SpendProfileResourceBuilder.newSpendProfileResource;
import static org.innovateuk.ifs.user.resource.Role.LEADAPPLICANT;
import static org.innovateuk.ifs.user.resource.Role.PARTNER;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ProjectSpendProfileControllerTest extends BaseControllerMockMVCTest<ProjectSpendProfileController> {
    @Mock
    public SpendProfileCostValidator spendProfileCostValidator;

    @Spy
    public SpendProfileTableCalculator spendProfileTableCalculator;

    @Mock
    private ProjectService projectService;

    @Mock
    private SpendProfileService spendProfileService;

    @Mock
    private StatusService statusService;

    @Mock
    private OrganisationRestService organisationRestService;

    @Mock
    private PartnerOrganisationRestService partnerOrganisationRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Override
    protected ProjectSpendProfileController supplyControllerUnderTest() {
        return new ProjectSpendProfileController();
    }

    @Test
    public void viewSpendProfileWhenProjectDetailsNotInDB() throws Exception {

        Long organisationId = 1L;

        ProjectResource projectResource = newProjectResource().build();

        when(projectService.getById(projectResource.getId())).
                thenThrow(new ObjectNotFoundException("Project not found", null));

        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/spend-profile", projectResource.getId(), organisationId))
                .andExpect(status().isNotFound())
                .andExpect(model().attributeDoesNotExist("model"));

        verify(spendProfileService, never()).getSpendProfileTable(projectResource.getId(), organisationId);
    }

    @Test
    public void viewSpendProfileWhenSpendProfileDetailsNotInDB() throws Exception {

        Long organisationId = 1L;

        ProjectResource projectResource = newProjectResource().build();

        when(projectService.getById(projectResource.getId())).
                thenReturn(projectResource);

        when(spendProfileService.getSpendProfileTable(projectResource.getId(), organisationId)).
                thenThrow(new ObjectNotFoundException("SpendProfile not found", null));


        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/spend-profile", projectResource.getId(), organisationId))
                .andExpect(status().isNotFound())
                .andExpect(model().attributeDoesNotExist("model"));
    }

    @Test
    public void viewSpendProfileSuccessfulViewModelPopulation() throws Exception {

        Long organisationId = 1L;
        Long projectId = 1L;
        Long competitionId = 1L;

        ProjectResource projectResource = newProjectResource()
                .withName("projectName1")
                .withTargetStartDate(LocalDate.of(2018, 3, 1))
                .withDuration(3L)
                .withId(projectId)
                .withCompetition(competitionId)
                .build();

        CompetitionResource competition = newCompetitionResource()
                .withIncludeJesForm(true)
                .withFundingType(FundingType.GRANT)
                .build();

        SpendProfileTableResource expectedTable = buildSpendProfileTableResource(projectResource);
        ProjectTeamStatusResource teamStatus = buildProjectTeamStatusResource();

        when(projectService.getById(projectResource.getId())).thenReturn(projectResource);

        when(spendProfileService.getSpendProfileTable(projectResource.getId(), organisationId)).thenReturn(expectedTable);
        when(statusService.getProjectTeamStatus(projectResource.getId(), Optional.empty())).thenReturn(teamStatus);

        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));

        ProjectSpendProfileViewModel expectedViewModel = buildExpectedProjectSpendProfileViewModel(organisationId, projectResource, expectedTable);

        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/spend-profile", projectResource.getId(), organisationId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("project/spend-profile"));

        assertTrue(expectedViewModel.isIncludeFinancialYearTable());

    }

    @Test
    public void viewSpendProfileConfirm() throws Exception {
        Long organisationId = 1L;
        Long projectId = 1L;
        Long competitionId = 1L;

        ProjectResource projectResource = newProjectResource()
                .withName("projectName1")
                .withTargetStartDate(LocalDate.of(2018, 3, 1))
                .withDuration(3L)
                .withId(projectId)
                .withCompetition(competitionId)
                .build();

        CompetitionResource competition = newCompetitionResource()
                .withIncludeJesForm(true)
                .withFundingType(FundingType.GRANT)
                .build();

        SpendProfileTableResource expectedTable = buildSpendProfileTableResource(projectResource);
        ProjectTeamStatusResource teamStatus = buildProjectTeamStatusResource();

        when(projectService.getById(projectResource.getId())).thenReturn(projectResource);

        when(spendProfileService.getSpendProfileTable(projectResource.getId(), organisationId)).thenReturn(expectedTable);
        when(statusService.getProjectTeamStatus(projectResource.getId(), Optional.empty())).thenReturn(teamStatus);

        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));

        ProjectSpendProfileViewModel expectedViewModel = buildExpectedProjectSpendProfileViewModel(organisationId, projectResource, expectedTable);

        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/spend-profile/confirm", projectResource.getId(), organisationId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("project/spend-profile-confirm"));

    }

    @Test
    public void saveSpendProfileWhenErrorWhilstSaving() throws Exception {

        Long projectId = 1L;
        Long organisationId = 2L;
        Long competitionId = 3L;

        ProjectResource projectResource = newProjectResource()
                .withName("projectName1")
                .withTargetStartDate(LocalDate.of(2018, 3, 1))
                .withDuration(3L)
                .withCompetition(competitionId)
                .build();

        CompetitionResource competition = newCompetitionResource()
                .withIncludeJesForm(true)
                .withFundingType(FundingType.GRANT)
                .build();

        List<ProjectUserResource> projectUsers = newProjectUserResource()
                .withUser(1L)
                .withOrganisation(1L)
                .withRole(PARTNER)
                .build(1);

        OrganisationResource organisation = newOrganisationResource().withId(organisationId)
                .withOrganisationType(OrganisationTypeEnum.BUSINESS.getId())
                .withOrganisationTypeName("BUSINESS")
                .build();

        SpendProfileTableResource table = buildSpendProfileTableResource(projectResource);

        when(spendProfileService.getSpendProfileTable(projectId, organisationId)).thenReturn(table);
        List<Error> incorrectCosts = new ArrayList<>();
        incorrectCosts.add(new Error(SPEND_PROFILE_TOTAL_FOR_ALL_MONTHS_DOES_NOT_MATCH_ELIGIBLE_TOTAL_FOR_SPECIFIED_CATEGORY, asList("Labour", 1), HttpStatus.BAD_REQUEST));

        when(spendProfileService.saveSpendProfile(projectId, organisationId, table)).thenReturn(serviceFailure(incorrectCosts));

        when(projectService.getById(projectId)).thenReturn(projectResource);
        when(organisationRestService.getOrganisationById(organisationId)).thenReturn(restSuccess(organisation));
        when(projectService.getProjectUsersForProject(projectResource.getId())).thenReturn(projectUsers);

        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));

        ProjectTeamStatusResource teamStatus = buildProjectTeamStatusResource();
        when(statusService.getProjectTeamStatus(projectResource.getId(), Optional.empty())).thenReturn(teamStatus);


        MvcResult result = mockMvc.perform(post("/project/{projectId}/partner-organisation/{organisationId}/spend-profile/edit", projectId, organisationId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("table.markedAsComplete", "true")
                .param("table.monthlyCostsPerCategoryMap[1][0]", "10")
                .param("table.monthlyCostsPerCategoryMap[1][1]", "10")
                .param("table.monthlyCostsPerCategoryMap[1][2]", "10")
                .param("table.monthlyCostsPerCategoryMap[2][0]", "10")
                .param("table.monthlyCostsPerCategoryMap[2][1]", "10")
                .param("table.monthlyCostsPerCategoryMap[2][2]", "10")
                .param("table.monthlyCostsPerCategoryMap[3][0]", "10")
                .param("table.monthlyCostsPerCategoryMap[3][1]", "10")
                .param("table.monthlyCostsPerCategoryMap[3][2]", "10")
        )
                .andExpect(view().name("project/spend-profile")).andReturn();

        SpendProfileForm form = (SpendProfileForm) result.getModelAndView().getModel().get("form");

        assertEquals(1, form.getObjectErrors().size());

        verify(projectService).getById(projectId);
        verify(spendProfileService, times(2)).getSpendProfileTable(projectId, organisationId);
        verify(organisationRestService).getOrganisationById(organisationId);
        verify(projectService, times(1)).getProjectUsersForProject(projectResource.getId());
    }

    @Test
    public void saveSpendProfileSuccess() throws Exception {

        Long projectId = 1L;
        Long organisationId = 1L;

        SpendProfileTableResource table = new SpendProfileTableResource();

        when(spendProfileService.getSpendProfileTable(projectId, organisationId)).thenReturn(table);

        when(spendProfileService.saveSpendProfile(projectId, organisationId, table)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/partner-organisation/{organisationId}/spend-profile/edit", projectId, organisationId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("table.markedAsComplete", "true")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/project/" + projectId + "/partner-organisation/" + organisationId + "/spend-profile"));
    }

    @Test
    public void saveSpendProfileSuccessLeadPartner() throws Exception {

        Long projectId = 1L;
        Long organisationId = 1L;

        SpendProfileTableResource table = new SpendProfileTableResource();

        when(spendProfileService.getSpendProfileTable(projectId, organisationId)).thenReturn(table);

        when(spendProfileService.saveSpendProfile(projectId, organisationId, table)).thenReturn(serviceSuccess());

        when(projectService.isUserLeadPartner(eq(projectId),any())).thenReturn(true);

        mockMvc.perform(post("/project/{projectId}/partner-organisation/{organisationId}/spend-profile/edit", projectId, organisationId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("table.markedAsComplete", "true")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/project/" + projectId + "/partner-organisation/" + organisationId + "/spend-profile/review"));
    }

    @Test
    public void markAsCompleteSpendProfileWhenSpendHigherThanEligible() throws Exception {
        Long organisationId = 1L;
        Long projectId = 2L;
        Long competitionId = 1L;

        ProjectResource projectResource = newProjectResource()
                .withName("projectName1")
                .withTargetStartDate(LocalDate.of(2018, 3, 1))
                .withDuration(3L)
                .withId(projectId)
                .withCompetition(competitionId)
                .build();

        CompetitionResource competition = newCompetitionResource()
                .withIncludeJesForm(true)
                .withFundingType(FundingType.GRANT)
                .build();

        SpendProfileTableResource table = buildSpendProfileTableResource(projectResource);
        ProjectTeamStatusResource teamStatus = buildProjectTeamStatusResource();

        PartnerOrganisationResource partnerOrganisationResource = new PartnerOrganisationResource();
        partnerOrganisationResource.setOrganisation(organisationId);
        partnerOrganisationResource.setLeadOrganisation(false);

        when(projectService.getById(projectResource.getId())).thenReturn(projectResource);

        when(spendProfileService.getSpendProfileTable(projectResource.getId(), organisationId)).thenReturn(table);

        when(projectService.getLeadPartners(projectResource.getId())).thenReturn(Collections.emptyList());
        when(statusService.getProjectTeamStatus(projectResource.getId(), Optional.empty())).thenReturn(teamStatus);

        when(spendProfileService.markSpendProfileComplete(projectResource.getId(), organisationId)).thenReturn(serviceFailure(SPEND_PROFILE_CANNOT_MARK_AS_COMPLETE_BECAUSE_SPEND_HIGHER_THAN_ELIGIBLE));

        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));

        ProjectSpendProfileViewModel expectedViewModel = buildExpectedProjectSpendProfileViewModel(organisationId, projectResource, table);

        expectedViewModel.setObjectErrors(singletonList(new ObjectError(SPEND_PROFILE_CANNOT_MARK_AS_COMPLETE_BECAUSE_SPEND_HIGHER_THAN_ELIGIBLE.getErrorKey(), "Cannot mark as complete, because totals more than eligible")));

        mockMvc.perform(post("/project/{projectId}/partner-organisation/{organisationId}/spend-profile/complete", projectResource.getId(), organisationId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("project/spend-profile"));

        verify(spendProfileService).markSpendProfileComplete(2L, 1L);

    }

    @Test
    public void markAsCompleteSpendProfileSuccess() throws Exception {
        final Long projectId = 1L;
        final Long organisationId = 2L;

        when(spendProfileService.markSpendProfileComplete(projectId, organisationId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/partner-organisation/{organisationId}/spend-profile/complete", projectId, organisationId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/project/" + projectId + "/partner-organisation/" + organisationId + "/spend-profile"));

        verify(spendProfileService).markSpendProfileComplete(1L, 2L);
    }

    @Test
    public void markAsIncompleteSpendProfileSuccess() throws Exception {

        Long projectId = 1L;
        Long organisationId = 2L;

        when(spendProfileService.markSpendProfileIncomplete(projectId, organisationId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/partner-organisation/{organisationId}/spend-profile/incomplete", projectId, organisationId)
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/project/" + projectId + "/partner-organisation/" + organisationId + "/spend-profile"));

        verify(spendProfileService).markSpendProfileIncomplete(1L, 2L);
    }

    @Test
    public void editSpendProfileSuccess() throws Exception {

        Long organisationId = 1L;
        Long projectId = 1L;
        Long competitionId = 1L;

        ProjectResource projectResource = newProjectResource()
                .withName("projectName1")
                .withTargetStartDate(LocalDate.of(2018, 3, 1))
                .withDuration(3L)
                .withId(projectId)
                .withCompetition(competitionId)
                .build();

        CompetitionResource competition = newCompetitionResource()
                .withIncludeJesForm(true)
                .withFundingType(FundingType.GRANT)
                .build();

        SpendProfileTableResource table = buildSpendProfileTableResource(projectResource);
        ProjectTeamStatusResource teamStatus = buildProjectTeamStatusResource();

        PartnerOrganisationResource partnerOrganisationResource = new PartnerOrganisationResource();
        partnerOrganisationResource.setOrganisation(organisationId);
        partnerOrganisationResource.setLeadOrganisation(false);
        when(partnerOrganisationRestService.getProjectPartnerOrganisations(projectId)).thenReturn(restSuccess(singletonList(partnerOrganisationResource)));

        when(projectService.getById(projectResource.getId())).thenReturn(projectResource);

        when(spendProfileService.getSpendProfileTable(projectResource.getId(), organisationId)).thenReturn(table);
        when(projectService.getLeadPartners(projectResource.getId())).thenReturn(Collections.emptyList());
        when(statusService.getProjectTeamStatus(projectResource.getId(), Optional.empty())).thenReturn(teamStatus);

        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));

        ProjectSpendProfileViewModel expectedViewModel = buildExpectedProjectSpendProfileViewModel(organisationId, projectResource, table);

        SpendProfileForm expectedForm = new SpendProfileForm();
        expectedForm.setTable(table);

        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/spend-profile/edit", projectResource.getId(), organisationId)
        )
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(view().name("project/spend-profile"));

        verify(projectService).getLeadPartners(eq(projectResource.getId()));
    }

    private ProjectTeamStatusResource buildProjectTeamStatusResource() {

        List<ProjectPartnerStatusResource> partnerStatuses = newProjectPartnerStatusResource().build(2);
        ProjectPartnerStatusResource leadProjectPartnerStatusResource = newProjectPartnerStatusResource()
                .withSpendProfileStatus(ProjectActivityStates.ACTION_REQUIRED)
                .withIsLeadPartner(true)
                .build();
        partnerStatuses.add(leadProjectPartnerStatusResource);

        return newProjectTeamStatusResource().withPartnerStatuses(partnerStatuses).build();
    }

    @Test
    public void partnerCannotEditAfterSubmission() throws Exception {
        Long projectId = 1L;
        Long organisationId = 2L;

        ProjectResource projectResource = newProjectResource()
                .withName("projectName1")
                .withTargetStartDate(LocalDate.of(2018, 3, 1))
                .withDuration(3L)
                .withId(projectId)
                .build();

        OrganisationResource organisationResource = newOrganisationResource().build();
        ProjectTeamStatusResource teamStatus = buildProjectTeamStatusResource();

        SpendProfileTableResource table = buildSpendProfileTableResource(projectResource);
        table.setMarkedAsComplete(true);

        when(projectService.getById(projectResource.getId())).thenReturn(projectResource);

        when(spendProfileService.getSpendProfileTable(projectResource.getId(), organisationId)).thenReturn(table);
        when(organisationRestService.getOrganisationById(organisationId)).thenReturn(restSuccess(organisationResource));

        PartnerOrganisationResource partnerOrganisationResource = new PartnerOrganisationResource();
        partnerOrganisationResource.setOrganisation(organisationId);
        partnerOrganisationResource.setLeadOrganisation(false);

        when(statusService.getProjectTeamStatus(projectResource.getId(), Optional.empty())).thenReturn(teamStatus);
        when(spendProfileService.markSpendProfileIncomplete(projectId, organisationId)).thenReturn(serviceFailure(GENERAL_SPRING_SECURITY_FORBIDDEN_ACTION));

        SpendProfileForm expectedForm = new SpendProfileForm();
        expectedForm.setTable(table);

        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/spend-profile/edit", projectResource.getId(), organisationId)
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(view().name("redirect:/project/1/partner-organisation/2/spend-profile"));

        verify(spendProfileService).markSpendProfileIncomplete(1L, 2L);
    }

    @Test
    public void saveSpendProfileWithMissingTableEntries() throws Exception {

        Long projectId = 1L;
        Long organisationId = 1L;
        Long competitionId = 1L;

        ProjectResource projectResource = newProjectResource()
                .withName("projectName1")
                .withTargetStartDate(LocalDate.of(2018, 3, 1))
                .withDuration(3L)
                .withCompetition(competitionId)
                .build();

        CompetitionResource competition = newCompetitionResource()
                .withIncludeJesForm(true)
                .withFundingType(FundingType.GRANT)
                .build();

        List<ProjectUserResource> projectUsers = newProjectUserResource()
                .withUser(1L)
                .withOrganisation(1L)
                .withRole(PARTNER)
                .build(1);

        OrganisationResource organisation = newOrganisationResource().withId(organisationId)
                .withOrganisationType(OrganisationTypeEnum.BUSINESS.getId())
                .withOrganisationTypeName("BUSINESS")
                .build();

        SpendProfileTableResource table = buildSpendProfileTableResource(projectResource);

        when(spendProfileService.getSpendProfileTable(projectId, organisationId)).thenReturn(table);
        List<Error> incorrectCosts = new ArrayList<>();
        incorrectCosts.add(new Error(SPEND_PROFILE_TOTAL_FOR_ALL_MONTHS_DOES_NOT_MATCH_ELIGIBLE_TOTAL_FOR_SPECIFIED_CATEGORY, asList("Labour", 1), HttpStatus.BAD_REQUEST));

        when(spendProfileService.saveSpendProfile(projectId, organisationId, table)).thenReturn(serviceFailure(incorrectCosts));

        when(projectService.getById(projectId)).thenReturn(projectResource);
        when(organisationRestService.getOrganisationById(organisationId)).thenReturn(restSuccess(organisation));
        when(projectService.getProjectUsersForProject(projectResource.getId())).thenReturn(projectUsers);

        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));

        ProjectTeamStatusResource teamStatus = buildProjectTeamStatusResource();
        when(statusService.getProjectTeamStatus(projectResource.getId(), Optional.empty())).thenReturn(teamStatus);


        MvcResult result = mockMvc.perform(post("/project/{projectId}/partner-organisation/{organisationId}/spend-profile/edit", projectId, organisationId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("table.markedAsComplete", "true")
                .param("table.monthlyCostsPerCategoryMap[1][0]", "a")
                .param("table.monthlyCostsPerCategoryMap[1][1]", "10")
                .param("table.monthlyCostsPerCategoryMap[1][2]", "10")
                .param("table.monthlyCostsPerCategoryMap[2][0]", "10")
                .param("table.monthlyCostsPerCategoryMap[2][1]", "10")
                .param("table.monthlyCostsPerCategoryMap[2][2]", "10")
                .param("table.monthlyCostsPerCategoryMap[3][0]", "10")
                .param("table.monthlyCostsPerCategoryMap[3][1]", "10")
                .param("table.monthlyCostsPerCategoryMap[3][2]", "10")
        )
                .andExpect(view().name("project/spend-profile")).andReturn();

        SpendProfileForm form = (SpendProfileForm) result.getModelAndView().getModel().get("form");

        assertEquals(1, form.getObjectErrors().size());

        verify(projectService).getById(projectId);
        verify(spendProfileService).getSpendProfileTable(projectId, organisationId);
        verify(organisationRestService).getOrganisationById(organisationId);
        verify(projectService, times(1)).getProjectUsersForProject(projectResource.getId());

    }

    @Test
    public void viewSpendProfileSuccessfulViewModelPopulationInLeadPartnerOrganisation() throws Exception {

        Long organisationId = 1L;
        Long projectId = 1L;
        Long competitionId = 1L;

        ProjectResource projectResource = newProjectResource()
                .withName("projectName1")
                .withTargetStartDate(LocalDate.of(2018, 3, 1))
                .withDuration(3L)
                .withId(projectId)
                .withCompetition(competitionId)
                .build();

        CompetitionResource competition = newCompetitionResource()
                .withIncludeJesForm(true)
                .build();

        List<ProjectUserResource> projectUserResources = newProjectUserResource()
                .withUser(1L)
                .withRole(PARTNER)
                .withOrganisation(organisationId)
                .build(1);

        List<ProjectUserResource> leadUserResources = newProjectUserResource()
                .withUser(1L)
                .withRole(LEADAPPLICANT)
                .withOrganisation(organisationId)
                .build(1);
        ProjectTeamStatusResource teamStatus = buildProjectTeamStatusResource();

        List<OrganisationResource> partnerOrganisations = newOrganisationResource()
                .withId(organisationId)
                .withName("abc")
                .build(1);

        PartnerOrganisationResource partnerOrganisationResource = new PartnerOrganisationResource();
        partnerOrganisationResource.setOrganisation(organisationId);
        partnerOrganisationResource.setLeadOrganisation(true);
        partnerOrganisationResource.setOrganisationName(partnerOrganisations.get(0).getName());

        SpendProfileResource spendProfileResource = newSpendProfileResource().build();

        List<Role> roleResources = singletonList(PARTNER);

        loggedInUser.setRoles(roleResources);
        when(projectService.getById(projectResource.getId())).thenReturn(projectResource);
        when(projectService.getLeadOrganisation(organisationId)).thenReturn(partnerOrganisations.get(0));
        when(projectService.getProjectUsersForProject(projectResource.getId())).thenReturn(projectUserResources);
        when(projectService.getPartnerOrganisationsForProject(projectResource.getId())).thenReturn(partnerOrganisations);
        when(statusService.getProjectTeamStatus(projectResource.getId(), Optional.empty())).thenReturn(teamStatus);
        when(projectService.getLeadPartners(projectId)).thenReturn(leadUserResources);

        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));

        when(spendProfileService.getSpendProfile(projectResource.getId(), organisationId)).thenReturn(Optional.of(spendProfileResource));

        ProjectSpendProfileProjectSummaryViewModel expectedViewModel = buildExpectedProjectSpendProfileProjectManagerViewModel(projectResource, partnerOrganisations, partnerOrganisations.get(0).getName(), true, false, true);

        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/spend-profile", projectResource.getId(), organisationId))
                .andExpect(status().isOk())
                .andExpect(view().name("project/spend-profile-review"))
                .andExpect(model().attribute("model", expectedViewModel))
                .andReturn();

        verify(projectService).getLeadPartners(eq(projectResource.getId()));

    }

    @Test
    public void monitoringOfficerSeesSpendProfileReviewPage() throws Exception {

        long organisationId = 1L;
        long projectId = 1L;
        long competitionId = 1L;

        ProjectResource projectResource = newProjectResource()
                .withName("projectName1")
                .withTargetStartDate(LocalDate.of(2018, 3, 1))
                .withDuration(3L)
                .withId(projectId)
                .withCompetition(competitionId)
                .withMonitoringOfficerUser(loggedInUser.getId())
                .build();

        CompetitionResource competition = newCompetitionResource()
                .withIncludeJesForm(true)
                .build();

        List<ProjectUserResource> projectUserResources = newProjectUserResource()
                .withUser(1L)
                .withRole(PARTNER)
                .withOrganisation(organisationId)
                .build(1);

        List<ProjectUserResource> leadUserResources = newProjectUserResource()
                .withUser(1L)
                .withRole(LEADAPPLICANT)
                .withOrganisation(organisationId)
                .build(1);
        ProjectTeamStatusResource teamStatus = buildProjectTeamStatusResource();

        List<OrganisationResource> partnerOrganisations = newOrganisationResource()
                .withId(organisationId)
                .withName("abc")
                .build(1);

        PartnerOrganisationResource partnerOrganisationResource = new PartnerOrganisationResource();
        partnerOrganisationResource.setOrganisation(organisationId);
        partnerOrganisationResource.setLeadOrganisation(true);
        partnerOrganisationResource.setOrganisationName(partnerOrganisations.get(0).getName());

        SpendProfileResource spendProfileResource = newSpendProfileResource().build();

        List<Role> roleResources = singletonList(PARTNER);

        when(projectService.getById(projectResource.getId())).thenReturn(projectResource);
        when(projectService.getLeadOrganisation(organisationId)).thenReturn(partnerOrganisations.get(0));
        when(projectService.getProjectUsersForProject(projectResource.getId())).thenReturn(projectUserResources);
        when(projectService.getPartnerOrganisationsForProject(projectResource.getId())).thenReturn(partnerOrganisations);
        when(statusService.getProjectTeamStatus(projectResource.getId(), Optional.empty())).thenReturn(teamStatus);
        when(projectService.getLeadPartners(projectId)).thenReturn(leadUserResources);

        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));

        when(spendProfileService.getSpendProfile(projectResource.getId(), organisationId)).thenReturn(Optional.of(spendProfileResource));

        ProjectSpendProfileProjectSummaryViewModel expectedViewModel = buildExpectedProjectSpendProfileProjectManagerViewModel(projectResource, partnerOrganisations, partnerOrganisations.get(0).getName(), true, false, true);

        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/spend-profile", projectResource.getId(), organisationId))
                .andExpect(status().isOk())
                .andExpect(view().name("project/spend-profile-review"))
                .andExpect(model().attribute("model", expectedViewModel))
                .andReturn();

        verify(projectService, never()).getLeadPartners(eq(projectResource.getId()));
    }

    @Test
    public void viewSpendProfileSuccessfulViewModelPopulationInNonLeadPartnerOrganisation() throws Exception {

        Long organisationId = 1L;
        Long projectId = 1L;
        Long competitionId = 1L;

        ProjectResource projectResource = newProjectResource()
                .withName("projectName1")
                .withTargetStartDate(LocalDate.of(2018, 3, 1))
                .withDuration(3L)
                .withId(projectId)
                .withCompetition(competitionId)
                .build();

        CompetitionResource competition = newCompetitionResource()
                .withIncludeJesForm(true)
                .withFundingType(FundingType.GRANT)
                .build();

        SpendProfileTableResource expectedTable = buildSpendProfileTableResource(projectResource);
        ProjectTeamStatusResource teamStatus = buildProjectTeamStatusResource();

        PartnerOrganisationResource partnerOrganisationResource = new PartnerOrganisationResource();
        partnerOrganisationResource.setOrganisation(organisationId);
        partnerOrganisationResource.setLeadOrganisation(false);

        when(projectService.getById(projectResource.getId())).thenReturn(projectResource);

        when(spendProfileService.getSpendProfileTable(projectResource.getId(), organisationId)).thenReturn(expectedTable);
        when(statusService.getProjectTeamStatus(projectResource.getId(), Optional.empty())).thenReturn(teamStatus);

        when(projectService.getLeadPartners(projectResource.getId())).thenReturn(Collections.emptyList());

        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));

        ProjectSpendProfileViewModel expectedViewModel = buildExpectedProjectSpendProfileViewModel(organisationId, projectResource, expectedTable);
        expectedViewModel.setLeadPartner(false);

        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/spend-profile", projectResource.getId(), organisationId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("project/spend-profile"));


    }

    private SpendProfileTableResource buildSpendProfileTableResource(ProjectResource projectResource) {

        SpendProfileTableResource expectedTable = new SpendProfileTableResource();

        expectedTable.setMarkedAsComplete(false);

        expectedTable.setMonths(asList(
                new LocalDateResource(2018, 3, 1),
                new LocalDateResource(2018, 4, 1),
                new LocalDateResource(2018, 5, 1)
        ));

        expectedTable.setEligibleCostPerCategoryMap(asMap(
                1L, new BigDecimal("100"),
                2L, new BigDecimal("150"),
                3L, new BigDecimal("55")));

        expectedTable.setMonthlyCostsPerCategoryMap(asMap(
                1L, asList(new BigDecimal("30"), new BigDecimal("30"), new BigDecimal("40")),
                2L, asList(new BigDecimal("70"), new BigDecimal("50"), new BigDecimal("60")),
                3L, asList(new BigDecimal("50"), new BigDecimal("5"), new BigDecimal("0"))));


        List<LocalDate> months = IntStream.range(0, projectResource.getDurationInMonths().intValue()).mapToObj(projectResource.getTargetStartDate()::plusMonths).collect(toList());
        List<LocalDateResource> monthResources = simpleMap(months, LocalDateResource::new);

        expectedTable.setMonths(monthResources);

        return expectedTable;
    }

    private ProjectSpendProfileProjectSummaryViewModel buildExpectedProjectSpendProfileProjectManagerViewModel(ProjectResource projectResource, List<OrganisationResource> partnerOrganisations, String partner, Boolean editable, Boolean markedComplete, Boolean userPartOfThisOrganisation) {

        Map<Long, OrganisationReviewDetails> editablePartners = new HashMap<>();
        final OrganisationResource leadOrganisation = partnerOrganisations.get(0);
        editablePartners.put(1L, new OrganisationReviewDetails(leadOrganisation.getName(), markedComplete, userPartOfThisOrganisation, editable));

        return new ProjectSpendProfileProjectSummaryViewModel(projectResource.getId(),
                projectResource.getApplication(), projectResource.getName(),
                partnerOrganisations,
                leadOrganisation,
                projectResource.getSpendProfileSubmittedDate() != null,
                editablePartners,
                false,
                false);
    }

    private ProjectSpendProfileViewModel buildExpectedProjectSpendProfileViewModel(Long organisationId, ProjectResource projectResource, SpendProfileTableResource expectedTable) {

        OrganisationResource organisationResource = OrganisationResourceBuilder.newOrganisationResource()
                .withId(organisationId)
                .withName("Org1")
                .withOrganisationTypeName(OrganisationTypeEnum.BUSINESS.name())
                .withOrganisationType(OrganisationTypeEnum.BUSINESS.getId())
                .build();

        List<ProjectUserResource> projectUsers = newProjectUserResource()
                .withUser(1L)
                .withOrganisation(1L)
                .withRole(PARTNER)
                .build(1);

        when(organisationRestService.getOrganisationById(organisationId)).thenReturn(restSuccess(organisationResource));
        when(projectService.getProjectUsersForProject(projectResource.getId())).thenReturn(projectUsers);

        List<SpendProfileSummaryYearModel> years = createSpendProfileSummaryYears();

        SpendProfileSummaryModel summary = new SpendProfileSummaryModel(years);

        // Build the expectedCategoryToActualTotal map based on the input
        Map<Long, BigDecimal> expectedCategoryToActualTotal = new LinkedHashMap<>();
        expectedCategoryToActualTotal.put(1L, new BigDecimal("100"));
        expectedCategoryToActualTotal.put(2L, new BigDecimal("180"));
        expectedCategoryToActualTotal.put(3L, new BigDecimal("55"));

        // Expected total for each month based on the input
        List<BigDecimal> expectedTotalForEachMonth = asList(new BigDecimal("150"), new BigDecimal("85"), new BigDecimal("100"));

        // Assert that the total of totals is correct for Actual Costs and Eligible Costs based on the input
        BigDecimal expectedTotalOfAllActualTotals = new BigDecimal("335");
        BigDecimal expectedTotalOfAllEligibleTotals = new BigDecimal("305");

        // Assert that the view model is populated with the correct values
        return new ProjectSpendProfileViewModel(projectResource, organisationResource, expectedTable,
                summary, false, expectedCategoryToActualTotal, expectedTotalForEachMonth,
                expectedTotalOfAllActualTotals, expectedTotalOfAllEligibleTotals, false, null, null ,false, true, false, false, false);
    }

    private List<SpendProfileSummaryYearModel> createSpendProfileSummaryYears() {
        return asList(new SpendProfileSummaryYearModel(2017, "150"), new SpendProfileSummaryYearModel(2018, "185"));
    }
}
