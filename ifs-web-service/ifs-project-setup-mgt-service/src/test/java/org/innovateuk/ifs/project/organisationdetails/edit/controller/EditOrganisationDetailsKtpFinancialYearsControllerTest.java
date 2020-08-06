package org.innovateuk.ifs.project.organisationdetails.edit.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.*;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.resource.KtpYearResource;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesKtpYearsResource;
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
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.time.YearMonth;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.finance.builder.KtpYearResourceBuilder.newKtpYearResource;
import static org.innovateuk.ifs.finance.builder.OrganisationFinancesKtpYearsResourceBuilder.newOrganisationFinancesKtpYearsResource;
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
public class EditOrganisationDetailsKtpFinancialYearsControllerTest extends BaseControllerMockMVCTest<EditOrganisationDetailsKtpFinancialYearsController> {

    @Mock
    private ProjectRestService projectRestService;

    @Mock
    private OrganisationRestService organisationRestService;

    @Mock
    private ProjectYourOrganisationRestService projectYourOrganisationRestService;

    @Mock
    private YourOrganisationKtpFinancialYearsFormPopulator formPopulator;

    @Mock
    private CompetitionRestService competitionRestService;

    @Spy
    private YourOrganisationKtpFinancialYearsFormSaver saver;

    private static final long projectId = 3L;
    private static final long organisationId = 5L;
    private OrganisationFinancesKtpYearsResource organisationFinancesResource;
    private ProjectResource projectResource;
    private OrganisationResource organisationResource;
    private static CompetitionResource competitionResource;
    private static final String VIEW_WITH_GROWTH_TABLE_PAGE = "project/organisationdetails/edit-organisation-size";

    @Override
    protected EditOrganisationDetailsKtpFinancialYearsController supplyControllerUnderTest() {
        return new EditOrganisationDetailsKtpFinancialYearsController();
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
        organisationFinancesResource = newOrganisationFinancesKtpYearsResource()
                .withOrganisationSize(OrganisationSize.SMALL)
                .withFinancialYearEnd(YearMonth.now().minusMonths(1))
                .withGroupEmployees(2L)
                .withKtpYears(newKtpYearResource()
                        .withYear(0,1,2)
                        .withTurnover(BigDecimal.valueOf(1))
                        .withPreTaxProfit(BigDecimal.valueOf(2))
                        .withCurrentAssets(BigDecimal.valueOf(3))
                        .withLiabilities(BigDecimal.valueOf(4))
                        .withShareholderValue(BigDecimal.valueOf(5))
                        .withLoans(BigDecimal.valueOf(6))
                        .withEmployees(7L)
                        .build(3))
                .build();
        competitionResource = newCompetitionResource()
                .build();
    }

    @Test
    public void viewPage() throws Exception {
        YourOrganisationKtpFinancialYearsForm form = new YourOrganisationKtpFinancialYearsForm();
        when(projectRestService.getProjectById(projectId)).thenReturn(restSuccess(projectResource));
        when(organisationRestService.getOrganisationById(organisationId)).thenReturn(restSuccess(organisationResource));
        when(projectYourOrganisationRestService.getOrganisationKtpYears(projectId, organisationId)).thenReturn(serviceSuccess(organisationFinancesResource));
        when(formPopulator.populate(organisationFinancesResource)).thenReturn(form);
        when(competitionRestService.getCompetitionById(projectResource.getCompetition())).thenReturn(restSuccess(competitionResource));

        mockMvc.perform(get(viewPageUrl()))
                .andExpect(status().isOk())
                .andExpect(view().name(VIEW_WITH_GROWTH_TABLE_PAGE))
                .andReturn();
    }

    @Test
    public void saveWithGrowthTable_success() throws Exception {
        returnSuccessForUpdate();
        when(projectRestService.getProjectById(projectId)).thenReturn(restSuccess(projectResource));


        mockMvc.perform(postAllFormParameters())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(organisationDetailsPageUrl()))
                .andReturn();

        assertEquals(organisationFinancesResource.getOrganisationSize(), LARGE);
    }

    @Test
    public void saveWithGrowthTable_failure() throws Exception {
        organisationFinancesResource.setOrganisationSize(null);
        YourOrganisationKtpFinancialYearsForm form = new YourOrganisationKtpFinancialYearsForm();
        when(projectRestService.getProjectById(projectId)).thenReturn(restSuccess(projectResource));
        when(organisationRestService.getOrganisationById(organisationId)).thenReturn(restSuccess(organisationResource));
        when(projectYourOrganisationRestService.getOrganisationKtpYears(projectId, organisationId)).thenReturn(serviceSuccess(organisationFinancesResource));
        when(formPopulator.populate(organisationFinancesResource)).thenReturn(form);
        when(competitionRestService.getCompetitionById(projectResource.getCompetition())).thenReturn(restSuccess(competitionResource));

        mockMvc.perform(get(viewPageUrl()))
                .andExpect(status().isOk())
                .andExpect(view().name(VIEW_WITH_GROWTH_TABLE_PAGE))
                .andReturn();
    }

    private RequestBuilder postAllFormParameters() {
        organisationFinancesResource.setOrganisationSize(LARGE);
        MockHttpServletRequestBuilder builder = post(viewPageUrl())
                .param("organisationSize", organisationFinancesResource.getOrganisationSize().toString())
                .param("financialYearEnd", "financialYearEnd")
                .param("financialYearEndMonthValue",
                        String.valueOf(organisationFinancesResource.getFinancialYearEnd().getMonth().getValue()))
                .param("financialYearEndYearValue",
                        String.valueOf(organisationFinancesResource.getFinancialYearEnd().getYear()))
                .param("groupEmployees",
                        organisationFinancesResource.getGroupEmployees().toString());
        int i = 0;
        for (KtpYearResource year : organisationFinancesResource.getYears()) {
            builder.param("years[" + i + "].year",
                    year.getYear().toString());
            builder.param("years[" + i + "].turnover",
                    year.getTurnover().toString());
            builder.param("years[" + i + "].preTaxProfit",
                    year.getPreTaxProfit().toString());
            builder.param("years[" + i + "].currentAssets",
                    year.getCurrentAssets().toString());
            builder.param("years[" + i + "].liabilities",
                    year.getLiabilities().toString());
            builder.param("years[" + i + "].shareholderValue",
                    year.getShareholderValue().toString());
            builder.param("years[" + i + "].loans",
                    year.getLoans().toString());
            builder.param("years[" + i + "].employees",
                    year.getEmployees().toString());
            i++;
        }
        return builder;
    }

    private String viewPageUrl() {
        return format("/project/%d/organisation/%d/edit/ktp-financial-years",
                projectId, organisationId);
    }

    private String organisationDetailsPageUrl() {
        return format("redirect:/competition/%d/project/%d/organisation/%d/details/ktp-financial-years",
                projectResource.getCompetition(), projectId, organisationId);
    }

    private void returnSuccessForUpdate() {
        when(projectYourOrganisationRestService.updateOrganisationFinancesKtpYears(eq(projectId), eq(organisationId),
                any())).thenReturn(serviceSuccess());
    }
}