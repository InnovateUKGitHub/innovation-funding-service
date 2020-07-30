package org.innovateuk.ifs.project.pendingpartner.controller;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationKtpFinancialYearsForm;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationKtpFinancialYearsFormPopulator;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationKtpFinancialYearsFormSaver;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.viewmodel.YourOrganisationViewModel;
import org.innovateuk.ifs.async.generation.AsyncFuturesGenerator;
import org.innovateuk.ifs.finance.resource.KtpYearResource;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesKtpYearsResource;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.project.finance.service.ProjectYourOrganisationRestService;
import org.innovateuk.ifs.project.pendingpartner.populator.YourOrganisationViewModelPopulator;
import org.innovateuk.ifs.project.projectteam.PendingPartnerProgressRestService;
import org.innovateuk.ifs.project.yourorganisation.viewmodel.ProjectYourOrganisationViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.concurrent.Future;

import static java.lang.String.format;
import static org.innovateuk.ifs.AsyncTestExpectationHelper.setupAsyncExpectations;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.finance.builder.KtpYearResourceBuilder.newKtpYearResource;
import static org.innovateuk.ifs.finance.builder.OrganisationFinancesKtpYearsResourceBuilder.newOrganisationFinancesKtpYearsResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ProjectYourOrganisationKtpFinancialYearsControllerTest extends BaseControllerMockMVCTest<ProjectYourOrganisationKtpFinancialYearsController> {

    @Mock
    private YourOrganisationViewModelPopulator viewModelPopulator;

    @Mock
    private ProjectYourOrganisationViewModel yourOrganisationViewModel;

    @Mock
    private YourOrganisationKtpFinancialYearsFormPopulator formPopulator;

    @Mock
    private ProjectYourOrganisationRestService yourOrganisationRestService;

    @Mock
    private PendingPartnerProgressRestService pendingPartnerProgressRestService;

    @Mock
    private AsyncFuturesGenerator asyncFuturesGenerator;

    @Spy
    private YourOrganisationKtpFinancialYearsFormSaver saver;

    @Captor
    ArgumentCaptor<OrganisationFinancesKtpYearsResource> argCaptor;

    private static final long projectId = 3L;
    private static final long organisationId = 5L;
    private OrganisationFinancesKtpYearsResource organisationFinancesResource;
    private static final String VIEW_WITH_GROWTH_TABLE_PAGE = "project/pending-partner-progress/your-organisation";

    @Override
    protected ProjectYourOrganisationKtpFinancialYearsController supplyControllerUnderTest() {
        return new ProjectYourOrganisationKtpFinancialYearsController();
    }

    public void setupResource() {
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
    }

    @Test
    public void viewPage() throws Exception {
        setupResource();
        setupAsyncExpectations(asyncFuturesGenerator);
        YourOrganisationKtpFinancialYearsForm form = new YourOrganisationKtpFinancialYearsForm();
        when(viewModelPopulator.populate(projectId, organisationId, getLoggedInUser())).thenReturn(yourOrganisationViewModel);
        when(yourOrganisationRestService.getOrganisationKtpYears(projectId, organisationId)).thenReturn(serviceSuccess(organisationFinancesResource));
        when(formPopulator.populate(organisationFinancesResource)).thenReturn(form);

        MvcResult result = mockMvc.perform(get(viewPageUrl()))
            .andExpect(status().isOk())
            .andExpect(view().name(VIEW_WITH_GROWTH_TABLE_PAGE))
            .andReturn();

        Future<YourOrganisationViewModel> viewModelRequest = (Future<YourOrganisationViewModel>) result.getModelAndView().getModel().get("model");
        assertEquals(viewModelRequest.get(), yourOrganisationViewModel);

        Future<YourOrganisationKtpFinancialYearsForm> formRequest = (Future<YourOrganisationKtpFinancialYearsForm>) result.getModelAndView().getModel().get("form");
        assertEquals(formRequest.get(), form);
    }

    @Test
    public void updateGrowthTable() throws Exception {
        returnSuccessForUpdateGrowthTable();

        mockMvc.perform(postAllFormParameters(Pair.of("ignoredParameter","")))
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name(landingPageUrl()))
            .andReturn();

        assertTrue(isUpdateCalledWithCorrectArguments());
    }

    @Test
    public void markAsCompleteWithGrowthTable_success() throws Exception {
        returnSuccessForUpdateGrowthTable();
        when(pendingPartnerProgressRestService.markYourOrganisationComplete(projectId, organisationId)).thenReturn(restSuccess());

        mockMvc.perform(postAllFormParameters(Pair.of("mark-as-complete", "")))
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name(landingPageUrl()))
            .andReturn();

        assertTrue(isUpdateCalledWithCorrectArguments());
        verify(pendingPartnerProgressRestService).markYourOrganisationComplete(projectId, organisationId);
    }

    @Test
    public void markAsCompleteWithGrowthTable_failure() throws Exception {
        when(viewModelPopulator.populate(projectId, organisationId, getLoggedInUser())).thenReturn(yourOrganisationViewModel);

        MvcResult result = mockMvc.perform(post(viewPageUrl())
            .param("mark-as-complete", ""))
            .andExpect(status().isOk())
            .andExpect(view().name(VIEW_WITH_GROWTH_TABLE_PAGE))
            .andReturn();

        YourOrganisationViewModel viewModelRequest = (YourOrganisationViewModel) result.getModelAndView().getModel().get("model");
        assertEquals(viewModelRequest, yourOrganisationViewModel);

        YourOrganisationKtpFinancialYearsForm formRequest = (YourOrganisationKtpFinancialYearsForm) result.getModelAndView().getModel().get("form");
        assertTrue(formIsEmpty(formRequest));
    }

    @Test
    public void markAsIncomplete() throws Exception {
        when(pendingPartnerProgressRestService.markYourOrganisationIncomplete(projectId, organisationId)).thenReturn(restSuccess());

        mockMvc.perform(post(viewPageUrl())
            .param("mark-as-incomplete", ""))
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:" + viewPageUrl()))
            .andReturn();

        verify(pendingPartnerProgressRestService).markYourOrganisationIncomplete(projectId, organisationId);
    }

    private void returnSuccessForUpdateGrowthTable() {
        when(yourOrganisationRestService.updateOrganisationFinancesWithGrowthTable(eq(projectId), eq(organisationId),
            any())).thenReturn(serviceSuccess());
    }

    private RequestBuilder postAllFormParameters(Pair<String, String> param) {
        setupResource();
        MockHttpServletRequestBuilder builder = post(viewPageUrl())
            .param("organisationSize", organisationFinancesResource.getOrganisationSize().toString())
            .param("financialYearEnd", "financialYearEnd")
            .param("financialYearEndMonthValue",
                String.valueOf(organisationFinancesResource.getFinancialYearEnd().getMonth().getValue()))
            .param("financialYearEndYearValue",
                String.valueOf(organisationFinancesResource.getFinancialYearEnd().getYear()))
            .param("groupEmployees",
                    organisationFinancesResource.getGroupEmployees().toString())
            .param(param.getKey(), param.getValue());
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

    private boolean isUpdateCalledWithCorrectArguments() {
       verify(yourOrganisationRestService).updateOrganisationFinancesKtpYears(eq(projectId), eq(organisationId),
           argCaptor.capture());
       return organisationFinancesResource.equals(argCaptor.getValue());
    }

    private boolean formIsEmpty(YourOrganisationKtpFinancialYearsForm form) {
        return form.getFinancialYearEnd() == null &&
        form.getGroupEmployees() == null &&
        form.getOrganisationSize() == null &&
        form.getYears() == null;
    }

    private String viewPageUrl() {
        return format("/project/%d/organisation/%d/your-organisation/ktp-financial-years",
            projectId, organisationId);
    }

    private String landingPageUrl() {
        return format("redirect:/project/%d/organisation/%d/pending-partner" +
                "-progress",
            projectId, organisationId);
    }
}
