package org.innovateuk.ifs.project.pendingpartner.controller;

import static java.lang.String.format;
import static org.innovateuk.ifs.AsyncTestExpectationHelper.setupAsyncExpectations;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.finance.builder.OrganisationFinancesWithoutGrowthTableResourceBuilder.newOrganisationFinancesWithoutGrowthTableResource;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


import java.math.BigDecimal;
import java.util.concurrent.Future;
import javafx.util.Pair;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithoutGrowthTableForm;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithoutGrowthTableFormPopulator;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.viewmodel.YourOrganisationViewModel;
import org.innovateuk.ifs.async.generation.AsyncFuturesGenerator;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithoutGrowthTableResource;
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

@RunWith(MockitoJUnitRunner.Silent.class)
public class ProjectYourOrganisationWithoutGrowthTableControllerTest extends BaseControllerMockMVCTest<ProjectYourOrganisationWithoutGrowthTableController> {

    @Mock
    private AsyncFuturesGenerator asyncFuturesGenerator;

    @Mock
    private YourOrganisationViewModelPopulator viewModelPopulator;

    @Mock
    private ProjectYourOrganisationViewModel yourOrganisationViewModel;

    @Mock
    private ProjectYourOrganisationRestService yourOrganisationRestService;

    @Mock
    private YourOrganisationWithoutGrowthTableFormPopulator withoutGrowthTableFormPopulator;

    @Mock
    private PendingPartnerProgressRestService pendingPartnerProgressRestService;

    @Mock
    private YourOrganisationWithoutGrowthTableForm yourOrganisationWithoutGrowthTableForm;

    @Captor
    ArgumentCaptor<OrganisationFinancesWithoutGrowthTableResource> argCaptor;

    private static final long projectId = 3L;
    private static final long organisationId = 5L;
    private OrganisationFinancesWithoutGrowthTableResource organisationFinancesWithoutGrowthTableResource;
    private static final String VIEW_WITHOUT_GROWTH_TABLE_PAGE = "project/pending-partner-progress/your-organisation-without-growth-table";


    @Override
    protected ProjectYourOrganisationWithoutGrowthTableController supplyControllerUnderTest() {
        return new ProjectYourOrganisationWithoutGrowthTableController();
    }

    private void setupExpectations(){
        setupAsyncExpectations(asyncFuturesGenerator);
    }

    public void setupResource() {
        organisationFinancesWithoutGrowthTableResource = newOrganisationFinancesWithoutGrowthTableResource()
            .withOrganisationSize(OrganisationSize.SMALL)
            .withStateAidAgreed(true)
            .withHeadCount(1L)
            .withTurnover(BigDecimal.valueOf(2))
            .build();
    }

    @Test
    public void viewPage() throws Exception {
        setupResource();
        setupExpectations();
        when(viewModelPopulator.populate(projectId, organisationId)).thenReturn(yourOrganisationViewModel);
        when(yourOrganisationRestService.getOrganisationFinancesWithoutGrowthTable(projectId, organisationId)).thenReturn(serviceSuccess(organisationFinancesWithoutGrowthTableResource));
        when(withoutGrowthTableFormPopulator.populate(organisationFinancesWithoutGrowthTableResource)).thenReturn(yourOrganisationWithoutGrowthTableForm);

        MvcResult result = mockMvc.perform(get(viewPageUrl()))
            .andExpect(status().isOk())
            .andExpect(view().name(VIEW_WITHOUT_GROWTH_TABLE_PAGE))
            .andReturn();

        Future<YourOrganisationViewModel> viewModelRequest = (Future<YourOrganisationViewModel>) result.getModelAndView().getModel().get("model");
        assertTrue(viewModelRequest.get().equals(yourOrganisationViewModel));

        Future<YourOrganisationWithoutGrowthTableForm> formRequest = (Future<YourOrganisationWithoutGrowthTableForm>) result.getModelAndView().getModel().get("form");
        assertTrue(formRequest.get().equals(yourOrganisationWithoutGrowthTableForm));
    }

    @Test
    public void updateWithoutGrowthTable() throws Exception {
        returnSuccessForUpdateWithoutGrowthTable();
        MvcResult result = mockMvc.perform(postAllFormParameters(new Pair("ignoredParameter","")))
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name(landingPageUrl()))
            .andReturn();

        assertTrue(isUpdateYourOrganisationWithoutGrowthTableCalledWithCorrectArguments());
    }

    @Test
    public void markAsCompleteWithoutGrowthTable_success() throws Exception {
        returnSuccessForUpdateWithoutGrowthTable();
        when(pendingPartnerProgressRestService.markYourOrganisationComplete(projectId, organisationId)).thenReturn(restSuccess());

        MvcResult result = mockMvc.perform(postAllFormParameters(new Pair("mark-as-complete", "")))
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name(landingPageUrl()))
            .andReturn();

        assertTrue(isUpdateYourOrganisationWithoutGrowthTableCalledWithCorrectArguments());
        verify(pendingPartnerProgressRestService).markYourOrganisationComplete(projectId, organisationId);
    }

    @Test
    public void markAsCompleteWithoutGrowthTable_failure() throws Exception {
        when(viewModelPopulator.populate(projectId, organisationId)).thenReturn(yourOrganisationViewModel);
        MvcResult result = mockMvc.perform(post(viewPageUrl())
            .param("mark-as-complete", ""))
            .andExpect(status().isOk())
            .andExpect(view().name(VIEW_WITHOUT_GROWTH_TABLE_PAGE))
            .andReturn();

        YourOrganisationViewModel viewModelRequest = (YourOrganisationViewModel) result.getModelAndView().getModel().get("model");
        assertTrue(viewModelRequest.equals(yourOrganisationViewModel));

        YourOrganisationWithoutGrowthTableForm formRequest =
            (YourOrganisationWithoutGrowthTableForm) result.getModelAndView().getModel().get("form");
        assertTrue(formIsEmpty(formRequest));
    }

    @Test
    public void markAsIncomplete() throws Exception {
        when(pendingPartnerProgressRestService.markYourOrganisationIncomplete(projectId, organisationId)).thenReturn(restSuccess());

        MvcResult result = mockMvc.perform(post(viewPageUrl())
            .param("mark-as-incomplete", ""))
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:" + viewPageUrl()))
            .andReturn();

        verify(pendingPartnerProgressRestService).markYourOrganisationIncomplete(projectId, organisationId);
    }

    private RequestBuilder postAllFormParameters(Pair<String, String> param) {
        setupResource();
        return post(viewPageUrl())
            .param("organisationSize", organisationFinancesWithoutGrowthTableResource.getOrganisationSize().toString())
            .param("stateAidAgreed", organisationFinancesWithoutGrowthTableResource.getStateAidAgreed().toString())
            .param("headCount",
                organisationFinancesWithoutGrowthTableResource.getHeadCount().toString())
            .param("turnover",
                organisationFinancesWithoutGrowthTableResource.getTurnover().toString())
            .param(param.getKey(), param.getValue());
    }

    private void returnSuccessForUpdateWithoutGrowthTable() {
        when(yourOrganisationRestService.updateOrganisationFinancesWithoutGrowthTable(eq(projectId), eq(organisationId),
            any())).thenReturn(serviceSuccess());
    }

    private String viewPageUrl() {
        return format("/project/%d/organisation/%d/your-organisation/without-growth-table",
            projectId, organisationId);
    }

    private String landingPageUrl() {
        return format("redirect:/project/%d/organisation/%d/pending-partner" +
                "-progress",
            projectId, organisationId);
    }

    private boolean isUpdateYourOrganisationWithoutGrowthTableCalledWithCorrectArguments() {
        verify(yourOrganisationRestService).updateOrganisationFinancesWithoutGrowthTable(eq(projectId),
            eq(organisationId),
            argCaptor.capture());
        return organisationFinancesWithoutGrowthTableResource.equals(argCaptor.getValue());
    }

    private boolean formIsEmpty(YourOrganisationWithoutGrowthTableForm form) {
        return form.getTurnover() == null &&
            form.getHeadCount() == null &&
            form.getOrganisationSize() == null &&
            form.getStateAidAgreed() == null;
    }
}


