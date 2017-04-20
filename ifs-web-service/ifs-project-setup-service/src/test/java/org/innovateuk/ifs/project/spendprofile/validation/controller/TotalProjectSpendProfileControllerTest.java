package org.innovateuk.ifs.project.spendprofile.validation.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.rest.LocalDateResource;
import org.innovateuk.ifs.commons.validation.SpendProfileCostValidator;
import org.innovateuk.ifs.project.model.SpendProfileSummaryModel;
import org.innovateuk.ifs.project.model.SpendProfileSummaryYearModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileTableResource;
import org.innovateuk.ifs.project.spendprofile.controller.TotalProjectSpendProfileController;
import org.innovateuk.ifs.project.util.SpendProfileTableCalculator;
import org.innovateuk.ifs.project.spendprofile.viewmodel.TotalProjectSpendProfileTableViewModel;
import org.innovateuk.ifs.project.spendprofile.viewmodel.TotalSpendProfileViewModel;
import org.innovateuk.ifs.user.builder.OrganisationResourceBuilder;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Spy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleToMap;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class TotalProjectSpendProfileControllerTest extends BaseControllerMockMVCTest<TotalProjectSpendProfileController> {
    @Mock
    public SpendProfileCostValidator spendProfileCostValidator;
    @Spy
    public SpendProfileTableCalculator spendProfileTableCalculator;

    @Override
    protected TotalProjectSpendProfileController supplyControllerUnderTest() {
        return new TotalProjectSpendProfileController();
    }

    @Test
    public void viewSpendProfileSuccessfulViewModelPopulation() throws Exception {

        ProjectResource projectResource = newProjectResource()
                .withName("projectName1")
                .withTargetStartDate(LocalDate.of(2018, 3, 1))
                .withDuration(3L)
                .build();


        Long organisationOneId = 1L;
        Long organisationTwoId = 2L;
        List<OrganisationResource> organisations = asList(
                OrganisationResourceBuilder.newOrganisationResource().withName("Org2").withId(organisationTwoId).build(),
                OrganisationResourceBuilder.newOrganisationResource().withName("Org1").withId(organisationOneId).build());

        when(projectService.getLeadOrganisation(projectResource.getId())).thenReturn(organisations.get(0));
        when(projectService.getPartnerOrganisationsForProject(projectResource.getId())).thenReturn(organisations);
        when(projectService.getById(projectResource.getId())).thenReturn(projectResource);

        SpendProfileTableResource tableOne = buildSpendProfileTableResource(projectResource);
        SpendProfileTableResource tableTwo = buildSpendProfileTableResource(projectResource);

        when(spendProfileService.getSpendProfileTable(projectResource.getId(), organisationOneId)).thenReturn(tableOne);
        when(spendProfileService.getSpendProfileTable(projectResource.getId(), organisationTwoId)).thenReturn(tableTwo);

        TotalSpendProfileViewModel expectedViewModel = buildTotalSpendProfileViewModel(organisations, projectResource, tableTwo, tableOne);

        mockMvc.perform(get("/project/{projectId}/spend-profile/total", projectResource.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("project/spend-profile-totals"));
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

    private TotalSpendProfileViewModel buildTotalSpendProfileViewModel(List<OrganisationResource> organisations, ProjectResource projectResource, SpendProfileTableResource tableOne, SpendProfileTableResource tableTwo) {
        List<SpendProfileSummaryYearModel> years = createSpendProfileSummaryYears();
        SpendProfileSummaryModel summary = new SpendProfileSummaryModel(years);

        List<LocalDateResource> months = tableOne.getMonths();
        Map<Long, List<BigDecimal>> monthlyCostsPerOrganisationMap = asMap(
                organisations.get(0).getId(), asList(new BigDecimal("150"), new BigDecimal("85"), new BigDecimal("100")),
                organisations.get(1).getId(),  asList(new BigDecimal("150"), new BigDecimal("85"), new BigDecimal("100")));

        Map<Long, BigDecimal> eligibleCostPerOrganisationMap = asMap(
                organisations.get(0).getId(), new BigDecimal("305"),
                organisations.get(1).getId(), new BigDecimal("305"));

        Map<Long, BigDecimal> organisationToActualTotal = asMap(
                organisations.get(0).getId(), new BigDecimal("335"),
                organisations.get(1).getId(), new BigDecimal("335"));

        List<BigDecimal> totalForEachMonth = asList(new BigDecimal("300"), new BigDecimal("170"), new BigDecimal("200"));
        BigDecimal totalOfAllActualTotals = new BigDecimal("670");
        BigDecimal totalOfAllEligibleTotals = new BigDecimal("610");
        TotalProjectSpendProfileTableViewModel table = new TotalProjectSpendProfileTableViewModel(months, monthlyCostsPerOrganisationMap,
                eligibleCostPerOrganisationMap, organisationToActualTotal, totalForEachMonth, totalOfAllActualTotals,
                totalOfAllEligibleTotals, simpleToMap(organisations, OrganisationResource::getId, OrganisationResource::getName),
                organisations.get(0));

        return new TotalSpendProfileViewModel(projectResource, table, summary);
    }

    private List<SpendProfileSummaryYearModel> createSpendProfileSummaryYears() {
        return asList(new SpendProfileSummaryYearModel(2017, "300"), new SpendProfileSummaryYearModel(2018, "370"));
    }
}