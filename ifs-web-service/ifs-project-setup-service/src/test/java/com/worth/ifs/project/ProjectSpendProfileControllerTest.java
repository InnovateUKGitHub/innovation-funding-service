package com.worth.ifs.project;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.commons.error.exception.ObjectNotFoundException;
import com.worth.ifs.commons.rest.LocalDateResource;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.ProjectUserResource;
import com.worth.ifs.project.resource.SpendProfileResource;
import com.worth.ifs.project.resource.SpendProfileTableResource;
import com.worth.ifs.project.viewmodel.ProjectSpendProfileViewModel;
import com.worth.ifs.project.viewmodel.SpendProfileSummaryModel;
import com.worth.ifs.project.viewmodel.SpendProfileSummaryYearModel;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.RoleResource;
import com.worth.ifs.user.resource.UserRoleType;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.IntStream;

import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static com.worth.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static com.worth.ifs.project.builder.SpendProfileResourceBuilder.newSpendProfileResource;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static com.worth.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static com.worth.ifs.util.MapFunctions.asMap;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ProjectSpendProfileControllerTest extends BaseControllerMockMVCTest<ProjectSpendProfileController> {

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

        verify(projectFinanceService, never()).getSpendProfileTable(projectResource.getId(), organisationId);
    }

    @Test
    public void viewSpendProfileWhenSpendProfileDetailsNotInDB() throws Exception {

        Long organisationId = 1L;

        ProjectResource projectResource = newProjectResource().build();

        when(projectService.getById(projectResource.getId())).
                thenReturn(projectResource);

        when(projectFinanceService.getSpendProfileTable(projectResource.getId(), organisationId)).
                thenThrow(new ObjectNotFoundException("SpendProfile not found", null));


        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/spend-profile", projectResource.getId(), organisationId))
                .andExpect(status().isNotFound())
                .andExpect(model().attributeDoesNotExist("model"));
    }

    @Test
    public void viewSpendProfileSuccessfulViewModelPopulation() throws Exception {

        Long organisationId = 1L;

        ProjectResource projectResource = newProjectResource()
                .withName("projectName1")
                .withTargetStartDate(LocalDate.of(2018, 3, 1))
                .withDuration(3L)
                .build();

        SpendProfileTableResource expectedTable = new SpendProfileTableResource();

        expectedTable.setMarkedAsComplete(false);

        expectedTable.setMonths(asList(
                new LocalDateResource(2018, 3, 1),
                new LocalDateResource(2018, 4, 1),
                new LocalDateResource(2018, 5, 1)
        ));

        expectedTable.setEligibleCostPerCategoryMap(asMap(
                "Labour", new BigDecimal("100"),
                "Materials", new BigDecimal("150"),
                "Other costs", new BigDecimal("55")));

        expectedTable.setMonthlyCostsPerCategoryMap(asMap(
                "Labour", asList(new BigDecimal("30"), new BigDecimal("30"), new BigDecimal("40")),
                "Materials", asList(new BigDecimal("70"), new BigDecimal("50"), new BigDecimal("60")),
                "Other costs", asList(new BigDecimal("50"), new BigDecimal("5"), new BigDecimal("0"))));


        List<LocalDate> months = IntStream.range(0, projectResource.getDurationInMonths().intValue()).mapToObj(projectResource.getTargetStartDate()::plusMonths).collect(toList());
        List<LocalDateResource> monthResources = simpleMap(months, LocalDateResource::new);

        expectedTable.setMonths(monthResources);

        when(projectService.getById(projectResource.getId())).thenReturn(projectResource);

        when(projectFinanceService.getSpendProfileTable(projectResource.getId(), organisationId)).thenReturn(expectedTable);

        List<SpendProfileSummaryYearModel> years = createSpendProfileSummaryYears();

        SpendProfileSummaryModel summary = new SpendProfileSummaryModel(years);

        // Build the expectedCategoryToActualTotal map based on the input
        Map<String, BigDecimal> expectedCategoryToActualTotal = new LinkedHashMap<>();
        expectedCategoryToActualTotal.put("Labour", new BigDecimal("100"));
        expectedCategoryToActualTotal.put("Materials", new BigDecimal("180"));
        expectedCategoryToActualTotal.put("Other costs", new BigDecimal("55"));

        // Expected total for each month based on the input
        List<BigDecimal> expectedTotalForEachMonth = asList(new BigDecimal("150"), new BigDecimal("85"), new BigDecimal("100"));

        // Assert that the total of totals is correct for Actual Costs and Eligible Costs based on the input
        BigDecimal expectedTotalOfAllActualTotals = new BigDecimal("335");
        BigDecimal expectedTotalOfAllEligibleTotals = new BigDecimal("305");

        // Assert that the view model is populated with the correct values
        ProjectSpendProfileViewModel expectedViewModel = new ProjectSpendProfileViewModel(projectResource, organisationId, expectedTable,
                summary, false, expectedCategoryToActualTotal, expectedTotalForEachMonth,
                expectedTotalOfAllActualTotals, expectedTotalOfAllEligibleTotals);

        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/spend-profile", projectResource.getId(), organisationId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("project/spend-profile"));
    }

    @Test
    public void testProjectManagerViewSpendProfile() throws Exception {
        long projectId = 123L;
        long organisationId = 1L;

        ProjectResource projectResource = newProjectResource().withId(123L).build();
        List<ProjectUserResource> projectUserResources = newProjectUserResource()
                .withUser(1L)
                .withRoleName(UserRoleType.PROJECT_MANAGER)
                .build(1);
        OrganisationResource organisationResource = newOrganisationResource().withId(1L).build();
        SpendProfileResource spendProfileResource = newSpendProfileResource().build();
        List<RoleResource> roleResources = newRoleResource().withType(UserRoleType.PROJECT_MANAGER).build(1);
        loggedInUser.setRoles(roleResources);
        when(projectService.getById(projectId)).thenReturn(projectResource);
        when(projectService.getProjectUsersForProject(projectResource.getId())).thenReturn(projectUserResources);
        when(organisationService.getOrganisationById(projectUserResources.get(0).getOrganisation())).thenReturn(organisationResource);
        when(projectFinanceService.getSpendProfile(projectId, organisationId)).thenReturn(Optional.of(spendProfileResource));

        MvcResult result = mockMvc.perform(get("/project/{id}/partner-organisation/{organisationId}/spend-profile", projectId, organisationId))
                .andExpect(status().isOk())
                .andExpect(view().name("project/spend-profile-review"))
                .andReturn();
    }

    private List<SpendProfileSummaryYearModel> createSpendProfileSummaryYears() {
        return Collections.singletonList(new SpendProfileSummaryYearModel(2018, "335"));
    }
}
