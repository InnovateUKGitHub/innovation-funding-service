package org.innovateuk.ifs.project.pendingpartner.controller;

import javafx.util.Pair;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithGrowthTableForm;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithGrowthTableFormPopulator;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.viewmodel.YourOrganisationViewModel;
import org.innovateuk.ifs.async.generation.AsyncFuturesGenerator;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithGrowthTableResource;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.project.finance.service.ProjectYourOrganisationRestService;
import org.innovateuk.ifs.project.pendingpartner.populator.YourOrganisationViewModelPopulator;
import org.innovateuk.ifs.project.pendingpartner.viewmodel.ProjectYourOrganisationViewModel;
import org.innovateuk.ifs.project.projectteam.PendingPartnerProgressRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.concurrent.Future;

import static java.lang.String.format;
import static org.innovateuk.ifs.AsyncTestExpectationHelper.setupAsyncExpectations;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.finance.builder.OrganisationFinancesWithGrowthTableResourceBuilder.newOrganisationFinancesWithGrowthTableResource;
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
public class ProjectYourOrganisationWithGrowthTableControllerTest extends BaseControllerMockMVCTest<ProjectYourOrganisationWithGrowthTableController> {

    @Mock
    private YourOrganisationViewModelPopulator viewModelPopulator;

    @Mock
    private ProjectYourOrganisationViewModel yourOrganisationViewModel;

    @Mock
    private YourOrganisationWithGrowthTableFormPopulator withGrowthTableFormPopulator;

    @Mock
    private ProjectYourOrganisationRestService yourOrganisationRestService;

    @Mock
    private PendingPartnerProgressRestService pendingPartnerProgressRestService;

    @Mock
    private AsyncFuturesGenerator asyncFuturesGenerator;

    @Captor
    ArgumentCaptor<OrganisationFinancesWithGrowthTableResource> argCaptor;

    private static final long projectId = 3L;
    private static final long organisationId = 5L;
    private OrganisationFinancesWithGrowthTableResource organisationFinancesWithGrowthTableResource;
    private static final String VIEW_WITH_GROWTH_TABLE_PAGE = "project/pending-partner-progress/your-organisation-with-growth-table";

    @Override
    protected ProjectYourOrganisationWithGrowthTableController supplyControllerUnderTest() {
        return new ProjectYourOrganisationWithGrowthTableController();
    }

    public void setupResource() {
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
        setupResource();
        setupAsyncExpectations(asyncFuturesGenerator);
        YourOrganisationWithGrowthTableForm yourOrganisationWithGrowthTableForm = new YourOrganisationWithGrowthTableForm();
        when(viewModelPopulator.populate(projectId, organisationId)).thenReturn(yourOrganisationViewModel);
        when(yourOrganisationRestService.getOrganisationFinancesWithGrowthTable(projectId, organisationId)).thenReturn(serviceSuccess(organisationFinancesWithGrowthTableResource));
        when(withGrowthTableFormPopulator.populate(organisationFinancesWithGrowthTableResource)).thenReturn(yourOrganisationWithGrowthTableForm);

        MvcResult result = mockMvc.perform(get(viewPageUrl()))
            .andExpect(status().isOk())
            .andExpect(view().name(VIEW_WITH_GROWTH_TABLE_PAGE))
            .andReturn();

        Future<YourOrganisationViewModel> viewModelRequest = (Future<YourOrganisationViewModel>) result.getModelAndView().getModel().get("model");
        assertEquals(viewModelRequest.get(), yourOrganisationViewModel);

        Future<YourOrganisationWithGrowthTableForm> formRequest = (Future<YourOrganisationWithGrowthTableForm>) result.getModelAndView().getModel().get("form");
        assertEquals(formRequest.get(), yourOrganisationWithGrowthTableForm);
    }

    @Test
    public void updateGrowthTable() throws Exception {
        returnSuccessForUpdateGrowthTable();

        mockMvc.perform(postAllFormParameters(new Pair("ignoredParameter","")))
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name(landingPageUrl()))
            .andReturn();

        assertTrue(isUpdateYourOrganisationWithGrowthTableCalledWithCorrectArguments());
    }

    @Test
    public void markAsCompleteWithGrowthTable_success() throws Exception {
        returnSuccessForUpdateGrowthTable();
        when(pendingPartnerProgressRestService.markYourOrganisationComplete(projectId, organisationId)).thenReturn(restSuccess());

        mockMvc.perform(postAllFormParameters(new Pair("mark-as-complete", "")))
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name(landingPageUrl()))
            .andReturn();

        assertTrue(isUpdateYourOrganisationWithGrowthTableCalledWithCorrectArguments());
        verify(pendingPartnerProgressRestService).markYourOrganisationComplete(projectId, organisationId);
    }

    @Test
    public void markAsCompleteWithGrowthTable_failure() throws Exception {
        when(viewModelPopulator.populate(projectId, organisationId)).thenReturn(yourOrganisationViewModel);

        MvcResult result = mockMvc.perform(post(viewPageUrl())
            .param("mark-as-complete", ""))
            .andExpect(status().isOk())
            .andExpect(view().name(VIEW_WITH_GROWTH_TABLE_PAGE))
            .andReturn();

        YourOrganisationViewModel viewModelRequest = (YourOrganisationViewModel) result.getModelAndView().getModel().get("model");
        assertEquals(viewModelRequest, yourOrganisationViewModel);

        YourOrganisationWithGrowthTableForm formRequest = (YourOrganisationWithGrowthTableForm) result.getModelAndView().getModel().get("form");
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
                organisationFinancesWithGrowthTableResource.getResearchAndDevelopmentSpendAtLastFinancialYear().toString())
            .param(param.getKey(), param.getValue());
    }

    private boolean isUpdateYourOrganisationWithGrowthTableCalledWithCorrectArguments() {
       verify(yourOrganisationRestService).updateOrganisationFinancesWithGrowthTable(eq(projectId), eq(organisationId),
           argCaptor.capture());
       return organisationFinancesWithGrowthTableResource.equals(argCaptor.getValue());
    }

    private boolean formIsEmpty(YourOrganisationWithGrowthTableForm form) {
        return form.getFinancialYearEnd() == null &&
        form.getAnnualExportAtLastFinancialYear() == null &&
        form.getAnnualProfitsAtLastFinancialYear() == null &&
        form.getAnnualTurnoverAtLastFinancialYear() == null &&
        form.getHeadCountAtLastFinancialYear() == null &&
        form.getOrganisationSize() == null &&
        form.getResearchAndDevelopmentSpendAtLastFinancialYear() == null;
    }

    private String viewPageUrl() {
        return format("/project/%d/organisation/%d/your-organisation/with-growth-table",
            projectId, organisationId);
    }

    private String landingPageUrl() {
        return format("redirect:/project/%d/organisation/%d/pending-partner" +
                "-progress",
            projectId, organisationId);
    }
}
