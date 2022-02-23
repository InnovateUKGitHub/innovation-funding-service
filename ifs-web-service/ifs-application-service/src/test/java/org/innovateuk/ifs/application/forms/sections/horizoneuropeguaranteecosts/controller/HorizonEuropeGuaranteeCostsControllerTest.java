package org.innovateuk.ifs.application.forms.sections.horizoneuropeguaranteecosts.controller;

import org.innovateuk.ifs.AbstractAsyncWaitMockMVCTest;
import org.innovateuk.ifs.application.forms.sections.hecpcosts.controller.HorizonEuropeGuaranteeCostsController;
import org.innovateuk.ifs.application.forms.sections.hecpcosts.form.HorizonEuropeGuaranteeCostsForm;
import org.innovateuk.ifs.application.forms.sections.hecpcosts.populator.HorizonEuropeGuaranteeCostsFormPopulator;
import org.innovateuk.ifs.application.forms.sections.hecpcosts.saver.HorizonEuropeGuaranteeCostsSaver;
import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.populator.YourProjectCostsViewModelPopulator;
import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.viewmodel.YourProjectCostsViewModel;
import org.innovateuk.ifs.application.service.SectionStatusRestService;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
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
public class HorizonEuropeGuaranteeCostsControllerTest extends AbstractAsyncWaitMockMVCTest<HorizonEuropeGuaranteeCostsController> {

    @Override
    protected HorizonEuropeGuaranteeCostsController supplyControllerUnderTest() {
        return new HorizonEuropeGuaranteeCostsController();
    }

    private static final long APPLICATION_ID = 1L;
    private static final long SECTION_ID = 2L;
    private static final long PROCESS_ROLE_ID = 3L;
    private static final long ORGANISATION_ID = 4L;
    private static final String VIEW = "application/sections/your-project-costs/hecp-costs";

    @Mock
    private HorizonEuropeGuaranteeCostsFormPopulator formPopulator;

    @Mock
    private YourProjectCostsViewModelPopulator viewModelPopulator;

    @Mock
    private HorizonEuropeGuaranteeCostsSaver saver;

    @Mock
    private SectionStatusRestService sectionStatusRestService;

    @Mock
    private ProcessRoleRestService processRoleRestService;

    @Test
    public void viewYourProjectCosts() throws Exception {
        YourProjectCostsViewModel viewModel = mockViewModel();

        when(formPopulator.populate(APPLICATION_ID, ORGANISATION_ID)).thenReturn(new HorizonEuropeGuaranteeCostsForm());

        mockMvc.perform(get(APPLICATION_BASE_URL + "{applicationId}/form/hecp-costs/organisation/{organisationId}/section/{sectionId}",
                APPLICATION_ID, ORGANISATION_ID, SECTION_ID))
                .andExpect(model().attribute("model", viewModel))
                .andExpect(view().name(VIEW))
                .andExpect(status().isOk());
    }

    @Test
    public void saveYourProjectCosts() throws Exception {
        when(saver.save(any(HorizonEuropeGuaranteeCostsForm.class), eq(APPLICATION_ID), eq(ORGANISATION_ID))).thenReturn(serviceSuccess());

        mockMvc.perform(post(APPLICATION_BASE_URL + "{applicationId}/form/hecp-costs/organisation/{organisationId}/section/{sectionId}",
                APPLICATION_ID, ORGANISATION_ID, SECTION_ID)
                .param("eligibleAgreement", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/application/%s/form/%s", APPLICATION_ID, SectionType.FINANCE)));
    }

    @Test
    public void edit() throws Exception {
        when(processRoleRestService.findProcessRole(APPLICATION_ID, getLoggedInUser().getId()))
                .thenReturn(restSuccess(newProcessRoleResource().withId(PROCESS_ROLE_ID).build()));
        when(sectionStatusRestService.markAsInComplete(SECTION_ID, APPLICATION_ID, PROCESS_ROLE_ID)).thenReturn(restSuccess());

        mockMvc.perform(post(APPLICATION_BASE_URL + "{applicationId}/form/hecp-costs/organisation/{organisationId}/section/{sectionId}",
                APPLICATION_ID, ORGANISATION_ID, SECTION_ID)
                .param("edit", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("%s%s/form/hecp-costs/organisation/%s/section/%s", APPLICATION_BASE_URL, APPLICATION_ID, ORGANISATION_ID, SECTION_ID)));


        verifyZeroInteractions(saver);
        verify(sectionStatusRestService).markAsInComplete(SECTION_ID, APPLICATION_ID, PROCESS_ROLE_ID);
    }

    @Test
    public void complete() throws Exception {
        ProcessRoleResource processRole = newProcessRoleResource().withId(PROCESS_ROLE_ID).build();
        when(saver.save(any(HorizonEuropeGuaranteeCostsForm.class), eq(APPLICATION_ID), eq(ORGANISATION_ID))).thenReturn(serviceSuccess());
        when(processRoleRestService.findProcessRole(APPLICATION_ID, getLoggedInUser().getId()))
                .thenReturn(restSuccess(processRole));
        when(sectionStatusRestService.markAsComplete(SECTION_ID, APPLICATION_ID, processRole.getId())).thenReturn(restSuccess(new ValidationMessages()));

        mockMvc.perform(post(APPLICATION_BASE_URL + "{applicationId}/form/hecp-costs/organisation/{organisationId}/section/{sectionId}",
                APPLICATION_ID, ORGANISATION_ID, SECTION_ID)
                .param("complete", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/application/%s/form/%s", APPLICATION_ID, SectionType.FINANCE)));

        verify(saver).save(any(HorizonEuropeGuaranteeCostsForm.class), eq(APPLICATION_ID), eq(ORGANISATION_ID));
    }

    private YourProjectCostsViewModel mockViewModel() {
        YourProjectCostsViewModel viewModel = mock(YourProjectCostsViewModel.class);
        when(viewModelPopulator.populate(APPLICATION_ID, SECTION_ID, ORGANISATION_ID, getLoggedInUser())).thenReturn(viewModel);
        return viewModel;
    }
}