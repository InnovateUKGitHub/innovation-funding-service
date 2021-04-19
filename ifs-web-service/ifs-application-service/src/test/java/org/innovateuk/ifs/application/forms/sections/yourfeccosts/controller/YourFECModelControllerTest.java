package org.innovateuk.ifs.application.forms.sections.yourfeccosts.controller;

import org.innovateuk.ifs.AbstractAsyncWaitMockMVCTest;
import org.innovateuk.ifs.application.forms.sections.common.viewmodel.CommonYourFinancesViewModelPopulator;
import org.innovateuk.ifs.application.forms.sections.common.viewmodel.CommonYourProjectFinancesViewModel;
import org.innovateuk.ifs.application.forms.sections.yourfeccosts.form.YourFECModelForm;
import org.innovateuk.ifs.application.forms.sections.yourfeccosts.form.YourFECModelFormPopulator;
import org.innovateuk.ifs.application.forms.sections.yourfeccosts.populator.YourFECViewModelPopulator;
import org.innovateuk.ifs.application.forms.sections.yourfeccosts.viewmodel.YourFECViewModel;
import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.saver.YourProjectCostsAutosaver;
import org.innovateuk.ifs.application.service.SectionRestService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.service.SectionStatusRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.commons.error.ValidationMessages.noErrors;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class YourFECModelControllerTest extends AbstractAsyncWaitMockMVCTest<YourFECModelController> {

    @Mock
    private YourFECViewModelPopulator YourFECViewModelPopulatorMock;

    @Mock
    private YourFECModelFormPopulator formPopulatorMock;

    @Mock
    private ApplicationFinanceRestService applicationFinanceRestServiceMock;

    @Mock
    private SectionService sectionServiceMock;

    @Mock
    private ProcessRoleRestService processRoleRestServiceMock;

    @Mock
    private OrganisationRestService organisationRestServiceMock;

    @Mock
    private YourProjectCostsAutosaver yourProjectCostsAutosaverMock;

    @Mock
    private SectionStatusRestService sectionStatusRestServiceMock;
    @Mock
    private SectionRestService sectionRestServiceMock;
    @Mock
    private CompetitionRestService competitionRestServiceMock;

    private long applicationId = 123L;
    private long sectionId = 456L;
    private long organisationId = 789L;

    private String yourFinancesRedirectUrl = String.format("redirect:/application/%d/form/FINANCE", applicationId);

    private ApplicationFinanceResource applicationFinance = newApplicationFinanceResource().build();

    private YourFECViewModel yourFECViewModel =
            new YourFECViewModel("/finances", "Competition name", "Application name", 1L, 2L, false, false, true, false, false, applicationFinance.getId());

    @Test
    public void viewPage() throws Exception {
        assertViewPageSuccessful(true);
    }

    @Test
    public void viewPageInternalUser() throws Exception {
        setLoggedInUser(admin);
        assertViewPageSuccessful(true);
    }

    @Test
    public void viewPageKta() throws Exception {
        setLoggedInUser(kta);
        assertViewPageSuccessful(true);
    }


    private void assertViewPageSuccessful(boolean internalUser) throws Exception {

        when(YourFECViewModelPopulatorMock.populate(organisationId, applicationId, sectionId, getLoggedInUser())).thenReturn(yourFECViewModel);

        MvcResult result = mockMvc.perform(get("/application/{applicationId}/form/your-fec-model/" +
                "organisation/{organisationId}/section/{sectionId}", applicationId, organisationId, sectionId))
                .andExpect(status().isOk())
                .andExpect(view().name("application/sections/your-fec-model/your-fec-model"))
                .andReturn();

        Map<String, Object> model = result.getModelAndView().getModel();

        assertThat(model.get("model")).isEqualTo(yourFECViewModel);

        verify(formPopulatorMock, times(1)).populate(any(YourFECModelForm.class), eq(applicationId), eq(organisationId));

        verifyNoMoreInteractionsWithMocks();
    }

    @Test
    public void updateFECModel() throws Exception {
        assertUpdateFECModelSuccessful();
    }

    private void assertUpdateFECModelSuccessful() throws Exception {

        when(applicationFinanceRestServiceMock.getApplicationFinance(applicationId, organisationId)).thenReturn(
                restSuccess(applicationFinance));
        yourProjectCostsAutosaverMock.resetCostRowEntriesBasedOnFecModelUpdate(applicationId, organisationId);
        ArgumentCaptor<ApplicationFinanceResource> updatedApplicationFinanceCaptor = ArgumentCaptor.forClass(ApplicationFinanceResource.class);

        when(applicationFinanceRestServiceMock.update(eq(applicationFinance.getId()), updatedApplicationFinanceCaptor.capture())).thenReturn(
                restSuccess(applicationFinance));

        when(organisationRestServiceMock.getOrganisationById(organisationId)).thenReturn(restSuccess(OrganisationResourceBuilder.newOrganisationResource().build()));

        mockMvc.perform(post("/application/{applicationId}/form/your-fec-model/" +
                "organisation/{organisationId}/section/{sectionId}", applicationId, organisationId, sectionId)
                .param("fecModelEnabled", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(yourFinancesRedirectUrl))
                .andReturn();

        ApplicationFinanceResource applicationFinanceBeingUpdated = updatedApplicationFinanceCaptor.getValue();
        assertThat(applicationFinanceBeingUpdated.getFecModelEnabled()).isEqualTo(true);

        verify(applicationFinanceRestServiceMock, times(1)).getApplicationFinance(applicationId, organisationId);
        verify(applicationFinanceRestServiceMock, times(1)).update(applicationFinance.getId(), applicationFinance);

        verifyNoMoreInteractionsWithMocks();
    }

    @Test
    public void markAsComplete() throws Exception {
        assertMarkAsCompleteSuccessful();
    }

    private void assertMarkAsCompleteSuccessful() throws Exception {

        when(applicationFinanceRestServiceMock.getApplicationFinance(applicationId, organisationId)).thenReturn(
                restSuccess(applicationFinance));
        yourProjectCostsAutosaverMock.resetCostRowEntriesBasedOnFecModelUpdate(applicationId, organisationId);

        ArgumentCaptor<ApplicationFinanceResource> updatedApplicationFinanceCaptor = ArgumentCaptor.forClass(ApplicationFinanceResource.class);

        when(applicationFinanceRestServiceMock.update(eq(applicationFinance.getId()), updatedApplicationFinanceCaptor.capture())).thenReturn(
                restSuccess(applicationFinance));

        ProcessRoleResource processRole = newProcessRoleResource().build();
        when(processRoleRestServiceMock.findProcessRole(loggedInUser.getId(), applicationId)).thenReturn(restSuccess(processRole));

        when(sectionServiceMock.markAsComplete(sectionId, applicationId, processRole.getId())).thenReturn(noErrors());

        mockMvc.perform(post("/application/{applicationId}/form/your-fec-model/" +
                "organisation/{organisationId}/section/{sectionId}", applicationId, organisationId, sectionId)
                .param("mark-as-complete", "")
                .param("fecModelEnabled", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(yourFinancesRedirectUrl))
                .andReturn();

        ApplicationFinanceResource applicationFinanceBeingUpdated = updatedApplicationFinanceCaptor.getValue();
        assertThat(applicationFinanceBeingUpdated.getFecModelEnabled()).isEqualTo(true);

        verify(applicationFinanceRestServiceMock, times(1)).getApplicationFinance(applicationId, organisationId);
        verify(applicationFinanceRestServiceMock, times(1)).update(applicationFinance.getId(), applicationFinance);
        verify(processRoleRestServiceMock, times(1)).findProcessRole(loggedInUser.getId(), applicationId);
        verify(sectionServiceMock, times(1)).markAsComplete(sectionId, applicationId, processRole.getId());

        verifyNoMoreInteractionsWithMocks();
    }

    @Test
    public void markAsIncomplete() throws Exception {
        long applicationId=1L;
        ProcessRoleResource processRole = newProcessRoleResource().build();
        when(processRoleRestServiceMock.findProcessRole(loggedInUser.getId(), applicationId)).thenReturn(restSuccess(processRole));
        CompetitionResource competition = newCompetitionResource().build();
        when(competitionRestServiceMock.getCompetitionForApplication(applicationId)).thenReturn(restSuccess(competition));
        SectionResource financeSection = newSectionResource().build();
        when(sectionRestServiceMock.getSectionsByCompetitionIdAndType(competition.getId(), SectionType.PROJECT_COST_FINANCES)).thenReturn(restSuccess(singletonList(financeSection)));
        when(sectionStatusRestServiceMock.markAsInComplete(financeSection.getId(), applicationId, processRole.getId())).thenReturn(restSuccess());

        mockMvc.perform(post("/application/{applicationId}/form/your-fec-model/" +
                "organisation/{organisationId}/section/{sectionId}", applicationId, organisationId, sectionId)
                .param("mark-as-incomplete", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("application/sections/your-fec-model/your-fec-model"))
                .andReturn();

        verify(processRoleRestServiceMock, times(1)).findProcessRole(loggedInUser.getId(), applicationId);
        verify(sectionServiceMock, times(1)).markAsInComplete(sectionId, applicationId, processRole.getId());

    }

    private void verifyNoMoreInteractionsWithMocks() {
        verifyNoMoreInteractions(formPopulatorMock, applicationFinanceRestServiceMock,
                sectionServiceMock, processRoleRestServiceMock);
    }

    @Override
    protected YourFECModelController supplyControllerUnderTest() {
        return new YourFECModelController();
    }
}
