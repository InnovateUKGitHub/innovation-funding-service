package org.innovateuk.ifs.project.spendprofile.validation.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.error.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.rest.LocalDateResource;
import org.innovateuk.ifs.commons.validation.SpendProfileCostValidator;
import org.innovateuk.ifs.project.builder.ProjectLeadStatusResourceBuilder;
import org.innovateuk.ifs.project.constant.ProjectActivityStates;
import org.innovateuk.ifs.project.spendprofile.form.SpendProfileForm;
import org.innovateuk.ifs.project.model.SpendProfileSummaryModel;
import org.innovateuk.ifs.project.model.SpendProfileSummaryYearModel;
import org.innovateuk.ifs.project.resource.*;
import org.innovateuk.ifs.project.spendprofile.controller.ProjectSpendProfileController;
import org.innovateuk.ifs.project.util.SpendProfileTableCalculator;
import org.innovateuk.ifs.project.spendprofile.viewmodel.ProjectSpendProfileProjectSummaryViewModel;
import org.innovateuk.ifs.project.spendprofile.viewmodel.ProjectSpendProfileViewModel;
import org.innovateuk.ifs.user.builder.OrganisationResourceBuilder;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.RoleResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
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
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.project.builder.ProjectPartnerStatusResourceBuilder.newProjectPartnerStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.builder.ProjectTeamStatusResourceBuilder.newProjectTeamStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.project.builder.SpendProfileResourceBuilder.newSpendProfileResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.PARTNER;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class ProjectSpendProfileControllerTest extends BaseControllerMockMVCTest<ProjectSpendProfileController> {
    @Mock
    public SpendProfileCostValidator spendProfileCostValidator;

    @Spy
    public SpendProfileTableCalculator spendProfileTableCalculator;

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

        ProjectResource projectResource = newProjectResource()
                .withName("projectName1")
                .withTargetStartDate(LocalDate.of(2018, 3, 1))
                .withDuration(3L)
                .withId(projectId)
                .build();

        SpendProfileTableResource expectedTable = buildSpendProfileTableResource(projectResource);
        ProjectTeamStatusResource teamStatus = buildProjectTeamStatusResource();

        when(projectService.getById(projectResource.getId())).thenReturn(projectResource);

        when(spendProfileService.getSpendProfileTable(projectResource.getId(), organisationId)).thenReturn(expectedTable);
        when(projectService.getProjectTeamStatus(projectResource.getId(), Optional.empty())).thenReturn(teamStatus);

        ProjectSpendProfileViewModel expectedViewModel = buildExpectedProjectSpendProfileViewModel(organisationId, projectResource, expectedTable);

        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/spend-profile", projectResource.getId(), organisationId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("project/spend-profile"));

    }

    @Test
    public void viewSpendProfileConfirm() throws Exception {

        Long organisationId = 1L;
        Long projectId = 1L;

        ProjectResource projectResource = newProjectResource()
                .withName("projectName1")
                .withTargetStartDate(LocalDate.of(2018, 3, 1))
                .withDuration(3L)
                .withId(projectId)
                .build();

        SpendProfileTableResource expectedTable = buildSpendProfileTableResource(projectResource);
        ProjectTeamStatusResource teamStatus = buildProjectTeamStatusResource();

        when(projectService.getById(projectResource.getId())).thenReturn(projectResource);

        when(spendProfileService.getSpendProfileTable(projectResource.getId(), organisationId)).thenReturn(expectedTable);
        when(projectService.getProjectTeamStatus(projectResource.getId(), Optional.empty())).thenReturn(teamStatus);

        ProjectSpendProfileViewModel expectedViewModel = buildExpectedProjectSpendProfileViewModel(organisationId, projectResource, expectedTable);

        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/spend-profile/confirm", projectResource.getId(), organisationId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("project/spend-profile-confirm"));

    }

    @Test
    public void saveSpendProfileWhenErrorWhilstSaving() throws Exception {

        Long projectId = 1L;
        Long organisationId = 1L;

        ProjectResource projectResource = newProjectResource()
                .withName("projectName1")
                .withTargetStartDate(LocalDate.of(2018, 3, 1))
                .withDuration(3L)
                .build();

        List<ProjectUserResource> projectUsers = newProjectUserResource()
                .withUser(1L)
                .withOrganisation(1L)
                .withRoleName(PARTNER)
                .build(1);

        OrganisationResource organisation = newOrganisationResource().withId(organisationId)
                .withOrganisationType(OrganisationTypeEnum.BUSINESS.getOrganisationTypeId())
                .withOrganisationTypeName("BUSINESS")
                .build();

        SpendProfileTableResource table = buildSpendProfileTableResource(projectResource);

        when(spendProfileService.getSpendProfileTable(projectId, organisationId)).thenReturn(table);
        List<Error> incorrectCosts = new ArrayList<>();
        incorrectCosts.add(new Error(SPEND_PROFILE_TOTAL_FOR_ALL_MONTHS_DOES_NOT_MATCH_ELIGIBLE_TOTAL_FOR_SPECIFIED_CATEGORY, asList("Labour", 1), HttpStatus.BAD_REQUEST));

        when(spendProfileService.saveSpendProfile(projectId, organisationId, table)).thenReturn(serviceFailure(incorrectCosts));

        when(projectService.getById(projectId)).thenReturn(projectResource);
        when(organisationService.getOrganisationById(organisationId)).thenReturn(organisation);
        when(projectService.getProjectUsersForProject(projectResource.getId())).thenReturn(projectUsers);

        ProjectTeamStatusResource teamStatus = buildProjectTeamStatusResource();
        when(projectService.getProjectTeamStatus(projectResource.getId(), Optional.empty())).thenReturn(teamStatus);


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
        verify(organisationService).getOrganisationById(organisationId);
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

        ProjectResource projectResource = newProjectResource()
                .withName("projectName1")
                .withTargetStartDate(LocalDate.of(2018, 3, 1))
                .withDuration(3L)
                .withId(projectId)
                .build();

        SpendProfileTableResource table = buildSpendProfileTableResource(projectResource);
        ProjectTeamStatusResource teamStatus = buildProjectTeamStatusResource();

        PartnerOrganisationResource partnerOrganisationResource = new PartnerOrganisationResource();
        partnerOrganisationResource.setOrganisation(organisationId);
        partnerOrganisationResource.setLeadOrganisation(false);

        when(projectService.getById(projectResource.getId())).thenReturn(projectResource);

        when(spendProfileService.getSpendProfileTable(projectResource.getId(), organisationId)).thenReturn(table);

        when(projectService.getLeadPartners(projectResource.getId())).thenReturn(Collections.emptyList());
        when(projectService.getProjectTeamStatus(projectResource.getId(), Optional.empty())).thenReturn(teamStatus);

        when(spendProfileService.markSpendProfileComplete(projectResource.getId(), organisationId)).thenReturn(serviceFailure(SPEND_PROFILE_CANNOT_MARK_AS_COMPLETE_BECAUSE_SPEND_HIGHER_THAN_ELIGIBLE));

        ProjectSpendProfileViewModel expectedViewModel = buildExpectedProjectSpendProfileViewModel(organisationId, projectResource, table);

        expectedViewModel.setObjectErrors(Collections.singletonList(new ObjectError(SPEND_PROFILE_CANNOT_MARK_AS_COMPLETE_BECAUSE_SPEND_HIGHER_THAN_ELIGIBLE.getErrorKey(), "Cannot mark as complete, because totals more than eligible")));

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

        ProjectResource projectResource = newProjectResource()
                .withName("projectName1")
                .withTargetStartDate(LocalDate.of(2018, 3, 1))
                .withDuration(3L)
                .withId(projectId)
                .build();

        SpendProfileTableResource table = buildSpendProfileTableResource(projectResource);
        ProjectTeamStatusResource teamStatus = buildProjectTeamStatusResource();

        PartnerOrganisationResource partnerOrganisationResource = new PartnerOrganisationResource();
        partnerOrganisationResource.setOrganisation(organisationId);
        partnerOrganisationResource.setLeadOrganisation(false);
        when(partnerOrganisationServiceMock.getPartnerOrganisations(projectId)).thenReturn(serviceSuccess(Collections.singletonList(partnerOrganisationResource)));

        when(projectService.getById(projectResource.getId())).thenReturn(projectResource);

        when(spendProfileService.getSpendProfileTable(projectResource.getId(), organisationId)).thenReturn(table);
        when(projectService.getLeadPartners(projectResource.getId())).thenReturn(Collections.emptyList());
        when(projectService.getProjectTeamStatus(projectResource.getId(), Optional.empty())).thenReturn(teamStatus);

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
        ProjectPartnerStatusResource leadProjectPartnerStatusResource = ProjectLeadStatusResourceBuilder.newProjectLeadStatusResource()
                .withSpendProfileStatus(ProjectActivityStates.ACTION_REQUIRED)
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
        when(organisationService.getOrganisationById(organisationId)).thenReturn(organisationResource);

        PartnerOrganisationResource partnerOrganisationResource = new PartnerOrganisationResource();
        partnerOrganisationResource.setOrganisation(organisationId);
        partnerOrganisationResource.setLeadOrganisation(false);

        when(projectService.getProjectTeamStatus(projectResource.getId(), Optional.empty())).thenReturn(teamStatus);
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

        ProjectResource projectResource = newProjectResource()
                .withName("projectName1")
                .withTargetStartDate(LocalDate.of(2018, 3, 1))
                .withDuration(3L)
                .build();

        List<ProjectUserResource> projectUsers = newProjectUserResource()
                .withUser(1L)
                .withOrganisation(1L)
                .withRoleName(PARTNER)
                .build(1);

        OrganisationResource organisation = newOrganisationResource().withId(organisationId)
                .withOrganisationType(OrganisationTypeEnum.BUSINESS.getOrganisationTypeId())
                .withOrganisationTypeName("BUSINESS")
                .build();

        SpendProfileTableResource table = buildSpendProfileTableResource(projectResource);

        when(spendProfileService.getSpendProfileTable(projectId, organisationId)).thenReturn(table);
        List<Error> incorrectCosts = new ArrayList<>();
        incorrectCosts.add(new Error(SPEND_PROFILE_TOTAL_FOR_ALL_MONTHS_DOES_NOT_MATCH_ELIGIBLE_TOTAL_FOR_SPECIFIED_CATEGORY, asList("Labour", 1), HttpStatus.BAD_REQUEST));

        when(spendProfileService.saveSpendProfile(projectId, organisationId, table)).thenReturn(serviceFailure(incorrectCosts));

        when(projectService.getById(projectId)).thenReturn(projectResource);
        when(organisationService.getOrganisationById(organisationId)).thenReturn(organisation);
        when(projectService.getProjectUsersForProject(projectResource.getId())).thenReturn(projectUsers);

        ProjectTeamStatusResource teamStatus = buildProjectTeamStatusResource();
        when(projectService.getProjectTeamStatus(projectResource.getId(), Optional.empty())).thenReturn(teamStatus);


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
        verify(organisationService).getOrganisationById(organisationId);
        verify(projectService, times(1)).getProjectUsersForProject(projectResource.getId());

    }

    @Test
    public void viewSpendProfileSuccessfulViewModelPopulationInLeadPartnerOrganisation() throws Exception {

        Long organisationId = 1L;
        Long projectId = 1L;

        ProjectResource projectResource = newProjectResource()
                .withName("projectName1")
                .withTargetStartDate(LocalDate.of(2018, 3, 1))
                .withDuration(3L)
                .withId(projectId)
                .build();

        List<ProjectUserResource> projectUserResources = newProjectUserResource()
                .withUser(1L)
                .withRoleName(UserRoleType.PARTNER)
                .withOrganisation(organisationId)
                .build(1);

        List<ProjectUserResource> leadUserResources = newProjectUserResource()
                .withUser(1L)
                .withRoleName(UserRoleType.LEADAPPLICANT)
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

        List<RoleResource> roleResources = newRoleResource().withType(UserRoleType.PARTNER).build(1);

        loggedInUser.setRoles(roleResources);
        when(projectService.getById(projectResource.getId())).thenReturn(projectResource);
        when(projectService.getProjectUsersForProject(projectResource.getId())).thenReturn(projectUserResources);
        when(projectService.getPartnerOrganisationsForProject(projectResource.getId())).thenReturn(partnerOrganisations);
        when(projectService.getProjectTeamStatus(projectResource.getId(), Optional.empty())).thenReturn(teamStatus);
        when(projectService.getLeadPartners(projectId)).thenReturn(leadUserResources);

        when(spendProfileService.getSpendProfile(projectResource.getId(), organisationId)).thenReturn(Optional.of(spendProfileResource));

        ProjectSpendProfileProjectSummaryViewModel expectedViewModel = buildExpectedProjectSpendProfileProjectManagerViewModel(projectResource, partnerOrganisations, partnerOrganisations.get(0).getName(), true);

        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/spend-profile", projectResource.getId(), organisationId))
                .andExpect(status().isOk())
                .andExpect(view().name("project/spend-profile-review"))
                .andExpect(model().attribute("model", expectedViewModel))
                .andReturn();

        verify(projectService).getLeadPartners(eq(projectResource.getId()));

    }

    @Test
    public void viewSpendProfileSuccessfulViewModelPopulationInNonLeadPartnerOrganisation() throws Exception {

        Long organisationId = 1L;
        Long projectId = 1L;

        ProjectResource projectResource = newProjectResource()
                .withName("projectName1")
                .withTargetStartDate(LocalDate.of(2018, 3, 1))
                .withDuration(3L)
                .withId(projectId)
                .build();

        SpendProfileTableResource expectedTable = buildSpendProfileTableResource(projectResource);
        ProjectTeamStatusResource teamStatus = buildProjectTeamStatusResource();

        PartnerOrganisationResource partnerOrganisationResource = new PartnerOrganisationResource();
        partnerOrganisationResource.setOrganisation(organisationId);
        partnerOrganisationResource.setLeadOrganisation(false);

        when(projectService.getById(projectResource.getId())).thenReturn(projectResource);

        when(spendProfileService.getSpendProfileTable(projectResource.getId(), organisationId)).thenReturn(expectedTable);
        when(projectService.getProjectTeamStatus(projectResource.getId(), Optional.empty())).thenReturn(teamStatus);

        when(projectService.getLeadPartners(projectResource.getId())).thenReturn(Collections.emptyList());

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

    private ProjectSpendProfileProjectSummaryViewModel buildExpectedProjectSpendProfileProjectManagerViewModel(ProjectResource projectResource, List<OrganisationResource> partnerOrganisations, String partner, Boolean editable) {

        Map<String, Boolean> partnersSpendProfileProgress = new HashMap<>();
        partnersSpendProfileProgress.put(partner, false);

        Map<String, Boolean> editablePartners = new HashMap<>();
        final OrganisationResource leadOrganisation = partnerOrganisations.get(0);
        editablePartners.put(partner, editable);

        return new ProjectSpendProfileProjectSummaryViewModel(projectResource.getId(),
                projectResource.getApplication(), projectResource.getName(),
                partnersSpendProfileProgress,
                partnerOrganisations,
                leadOrganisation,
                projectResource.getSpendProfileSubmittedDate() != null,
                editablePartners,
                false);
    }

    private ProjectSpendProfileViewModel buildExpectedProjectSpendProfileViewModel(Long organisationId, ProjectResource projectResource, SpendProfileTableResource expectedTable) {

        OrganisationResource organisationResource = OrganisationResourceBuilder.newOrganisationResource()
                .withId(organisationId)
                .withName("Org1")
                .withOrganisationTypeName("BUSINESS")
                .build();

        List<ProjectUserResource> projectUsers = newProjectUserResource()
                .withUser(1L)
                .withOrganisation(1L)
                .withRoleName(PARTNER)
                .build(1);

        when(organisationService.getOrganisationById(organisationId)).thenReturn(organisationResource);
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
