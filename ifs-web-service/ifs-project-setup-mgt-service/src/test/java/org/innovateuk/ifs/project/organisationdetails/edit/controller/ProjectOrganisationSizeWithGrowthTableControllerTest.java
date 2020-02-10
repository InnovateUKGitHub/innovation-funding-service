package org.innovateuk.ifs.project.organisationdetails.edit.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithGrowthTableForm;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithGrowthTableFormPopulator;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithGrowthTableResource;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.finance.service.ProjectYourOrganisationRestService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.RequestBuilder;

import java.math.BigDecimal;
import java.time.YearMonth;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.finance.builder.OrganisationFinancesWithGrowthTableResourceBuilder.newOrganisationFinancesWithGrowthTableResource;
import static org.innovateuk.ifs.finance.resource.OrganisationSize.LARGE;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


@RunWith(MockitoJUnitRunner.Silent.class)
public class ProjectOrganisationSizeWithGrowthTableControllerTest extends BaseControllerMockMVCTest<ProjectOrganisationSizeWithGrowthTableController> {


    @Mock
    private ProjectRestService projectRestService;

    @Mock
    private OrganisationRestService organisationRestService;

    @Mock
    private ProjectYourOrganisationRestService projectYourOrganisationRestService;

    @Mock
    private YourOrganisationWithGrowthTableFormPopulator viewModelPopulator;

    private static final long projectId = 3L;
    private static final long organisationId = 5L;
    private OrganisationFinancesWithGrowthTableResource organisationFinancesWithGrowthTableResource;
    private ProjectResource projectResource;
    private OrganisationResource organisationResource;
    private static final String VIEW_WITH_GROWTH_TABLE_PAGE = "project/organisationdetails/edit-organisation-size-with-growth-table";

    @Override
    protected ProjectOrganisationSizeWithGrowthTableController supplyControllerUnderTest() {
        return new ProjectOrganisationSizeWithGrowthTableController();
    }

    @Before
    public void setup() {
        projectResource = newProjectResource()
                .withId(projectId)
                .build();
        organisationResource = newOrganisationResource()
                .withId(organisationId)
                .withName("SmithZone Ltd")
                .build();
        organisationFinancesWithGrowthTableResource = newOrganisationFinancesWithGrowthTableResource()
                .withOrganisationSize(OrganisationSize.SMALL)
                .withFinancialYearEnd(YearMonth.now().minusMonths(1))
                .withHeadCount(1L)
                .withTurnover(BigDecimal.valueOf(2))
                .withAnnualProfits(BigDecimal.valueOf(3))
                .withAnnualExport(BigDecimal.valueOf(4))
                .withResearchAndDevelopment(BigDecimal.valueOf(5))
                .build();
    }

    @Test
    public void viewPage() throws Exception {
        YourOrganisationWithGrowthTableForm yourOrganisationWithGrowthTableForm = new YourOrganisationWithGrowthTableForm();
        when(projectRestService.getProjectById(projectId)).thenReturn(restSuccess(projectResource));
        when(organisationRestService.getOrganisationById(organisationId)).thenReturn(restSuccess(organisationResource));
        when(projectYourOrganisationRestService.getOrganisationFinancesWithGrowthTable(projectId, organisationId)).thenReturn(serviceSuccess(organisationFinancesWithGrowthTableResource));
        when(viewModelPopulator.populate(organisationFinancesWithGrowthTableResource)).thenReturn(yourOrganisationWithGrowthTableForm);

        mockMvc.perform(get(viewPageUrl()))
                .andExpect(status().isOk())
                .andExpect(view().name(VIEW_WITH_GROWTH_TABLE_PAGE))
                .andReturn();
    }

    @Test
    public void saveWithGrowthTable_success() throws Exception {
        returnSuccessForUpdateGrowthTable();
        when(projectRestService.getProjectById(projectId)).thenReturn(restSuccess(projectResource));


        mockMvc.perform(postAllFormParameters())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(organisationDetailsPageUrl()))
                .andReturn();

        assertEquals(organisationFinancesWithGrowthTableResource.getOrganisationSize(), LARGE);
    }

    @Test
    public void saveWithGrowthTable_failure() throws Exception {
        organisationFinancesWithGrowthTableResource.setOrganisationSize(null);
        YourOrganisationWithGrowthTableForm yourOrganisationWithGrowthTableForm = new YourOrganisationWithGrowthTableForm();
        when(projectRestService.getProjectById(projectId)).thenReturn(restSuccess(projectResource));
        when(organisationRestService.getOrganisationById(organisationId)).thenReturn(restSuccess(organisationResource));
        when(projectYourOrganisationRestService.getOrganisationFinancesWithGrowthTable(projectId, organisationId)).thenReturn(serviceSuccess(organisationFinancesWithGrowthTableResource));
        when(viewModelPopulator.populate(organisationFinancesWithGrowthTableResource)).thenReturn(yourOrganisationWithGrowthTableForm);

        mockMvc.perform(get(viewPageUrl()))
                .andExpect(status().isOk())
                .andExpect(view().name(VIEW_WITH_GROWTH_TABLE_PAGE))
                .andReturn();
    }

    private RequestBuilder postAllFormParameters() {
        organisationFinancesWithGrowthTableResource.setOrganisationSize(LARGE);
        return post(viewPageUrl())
                .param("organisationSize", organisationFinancesWithGrowthTableResource.getOrganisationSize().toString())
                .param("financialYearEnd", "financialYearEnd")
                .param("financialYearEndMonthValue",
                        String.valueOf(organisationFinancesWithGrowthTableResource.getFinancialYearEnd().getMonth().getValue()))
                .param("financialYearEndYearValue",
                        String.valueOf(organisationFinancesWithGrowthTableResource.getFinancialYearEnd().getYear()))
                .param("headCountAtLastFinancialYear",
                        organisationFinancesWithGrowthTableResource.getHeadCountAtLastFinancialYear().toString())
                .param("annualTurnoverAtLastFinancialYear",
                        organisationFinancesWithGrowthTableResource.getAnnualTurnoverAtLastFinancialYear().toString())
                .param("annualProfitsAtLastFinancialYear",
                        organisationFinancesWithGrowthTableResource.getAnnualProfitsAtLastFinancialYear().toString())
                .param("annualExportAtLastFinancialYear",
                        organisationFinancesWithGrowthTableResource.getAnnualExportAtLastFinancialYear().toString())
                .param("researchAndDevelopmentSpendAtLastFinancialYear",
                        organisationFinancesWithGrowthTableResource.getResearchAndDevelopmentSpendAtLastFinancialYear().toString());
    }

    private String viewPageUrl() {
        return format("/project/%d/organisation/%d/edit/with-growth-table",
                projectId, organisationId);
    }

    private String organisationDetailsPageUrl() {
        return format("redirect:/competition/%d/project/%d/organisation/%d/details/with-growth-table",
                projectResource.getCompetition(), projectId, organisationId);
    }

    private void returnSuccessForUpdateGrowthTable() {
        when(projectYourOrganisationRestService.updateOrganisationFinancesWithGrowthTable(eq(projectId), eq(organisationId),
                any())).thenReturn(serviceSuccess());
    }
}