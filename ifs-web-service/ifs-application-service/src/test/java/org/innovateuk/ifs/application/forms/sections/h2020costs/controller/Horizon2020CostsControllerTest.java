package org.innovateuk.ifs.application.forms.sections.h2020costs.controller;

import org.innovateuk.ifs.AbstractAsyncWaitMockMVCTest;
import org.innovateuk.ifs.application.forms.sections.h2020costs.form.Horizon2020CostsForm;
import org.innovateuk.ifs.application.forms.sections.h2020costs.populator.Horizon2020CostsFormPopulator;
import org.innovateuk.ifs.application.forms.sections.h2020costs.saver.Horizon2020CostsSaver;
import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.populator.YourProjectCostsViewModelPopulator;
import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.saver.YourProjectCostsCompleter;
import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.viewmodel.YourProjectCostsViewModel;
import org.innovateuk.ifs.application.service.SectionStatusRestService;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class Horizon2020CostsControllerTest extends AbstractAsyncWaitMockMVCTest<Horizon2020CostsController> {

    @Override
    protected Horizon2020CostsController supplyControllerUnderTest() {
        return new Horizon2020CostsController();
    }

    private static final long APPLICATION_ID = 1L;
    private static final long SECTION_ID = 2L;
    private static final long PROCESS_ROLE_ID = 3L;
    private static final long ORGANISATION_ID = 4L;
    private static final String VIEW = "application/horizon-2020-costs";

    @Mock
    private Horizon2020CostsFormPopulator formPopulator;

    @Mock
    private YourProjectCostsViewModelPopulator viewModelPopulator;

    @Mock
    private Horizon2020CostsSaver saver;

    @Mock
    private SectionStatusRestService sectionStatusRestService;

    @Mock
    private UserRestService userRestService;

    @Mock
    private YourProjectCostsCompleter completeSectionAction;

    @Test
    public void viewYourProjectCosts() throws Exception {
        YourProjectCostsViewModel viewModel = mockViewModel();

        when(formPopulator.populate(APPLICATION_ID, ORGANISATION_ID)).thenReturn(new Horizon2020CostsForm());

        mockMvc.perform(get(APPLICATION_BASE_URL + "{applicationId}/form/horizon-2020-costs/organisation/{organisationId}/section/{sectionId}",
                APPLICATION_ID, ORGANISATION_ID, SECTION_ID))
                .andExpect(model().attribute("model", viewModel))
                .andExpect(view().name(VIEW))
                .andExpect(status().isOk());
    }

    @Test
    public void saveYourProjectCosts() throws Exception {
        when(saver.save(any(Horizon2020CostsForm.class), eq(APPLICATION_ID), eq(ORGANISATION_ID))).thenReturn(serviceSuccess());

        mockMvc.perform(post(APPLICATION_BASE_URL + "{applicationId}/form/horizon-2020-costs/organisation/{organisationId}/section/{sectionId}",
                APPLICATION_ID, ORGANISATION_ID, SECTION_ID)
                .param("eligibleAgreement", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/application/%s/form/%s", APPLICATION_ID, SectionType.FINANCE)));
    }

    @Test
    public void edit() throws Exception {
        when(userRestService.findProcessRole(APPLICATION_ID, getLoggedInUser().getId()))
                .thenReturn(restSuccess(newProcessRoleResource().withId(PROCESS_ROLE_ID).build()));
        when(sectionStatusRestService.markAsInComplete(SECTION_ID, APPLICATION_ID, PROCESS_ROLE_ID)).thenReturn(restSuccess());

        mockMvc.perform(post(APPLICATION_BASE_URL + "{applicationId}/form/horizon-2020-costs/organisation/{organisationId}/section/{sectionId}",
                APPLICATION_ID, ORGANISATION_ID, SECTION_ID)
                .param("edit", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("%s%s/form/horizon-2020-costs/organisation/%s/section/%s", APPLICATION_BASE_URL, APPLICATION_ID, ORGANISATION_ID, SECTION_ID)));


        verifyZeroInteractions(saver);
        verify(sectionStatusRestService).markAsInComplete(SECTION_ID, APPLICATION_ID, PROCESS_ROLE_ID);
    }

    @Test
    public void complete() throws Exception {
        ProcessRoleResource processRole = newProcessRoleResource().withId(PROCESS_ROLE_ID).build();
        when(saver.save(any(Horizon2020CostsForm.class), eq(APPLICATION_ID), eq(ORGANISATION_ID))).thenReturn(serviceSuccess());
        when(userRestService.findProcessRole(APPLICATION_ID, getLoggedInUser().getId()))
                .thenReturn(restSuccess(processRole));
        when(completeSectionAction.markAsComplete(SECTION_ID, APPLICATION_ID, processRole)).thenReturn(new ValidationMessages());

        mockMvc.perform(post(APPLICATION_BASE_URL + "{applicationId}/form/horizon-2020-costs/organisation/{organisationId}/section/{sectionId}",
                APPLICATION_ID, ORGANISATION_ID, SECTION_ID)
                .param("complete", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/application/%s/form/%s", APPLICATION_ID, SectionType.FINANCE)));

        verify(saver).save(any(Horizon2020CostsForm.class), eq(APPLICATION_ID), eq(ORGANISATION_ID));
        verify(completeSectionAction).markAsComplete(SECTION_ID, APPLICATION_ID, processRole);
    }

    private YourProjectCostsViewModel mockViewModel() {
        YourProjectCostsViewModel viewModel = mock(YourProjectCostsViewModel.class);
        when(viewModelPopulator.populate(APPLICATION_ID, SECTION_ID, ORGANISATION_ID, getLoggedInUser().isInternalUser(), "")).thenReturn(viewModel);
        return viewModel;
    }
}