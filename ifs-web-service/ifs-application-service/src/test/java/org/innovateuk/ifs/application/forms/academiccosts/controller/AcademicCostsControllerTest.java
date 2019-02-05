package org.innovateuk.ifs.application.forms.academiccosts.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.forms.academiccosts.form.AcademicCostForm;
import org.innovateuk.ifs.application.forms.academiccosts.populator.AcademicCostFormPopulator;
import org.innovateuk.ifs.application.forms.academiccosts.populator.AcademicCostViewModelPopulator;
import org.innovateuk.ifs.application.forms.academiccosts.saver.AcademicCostSaver;
import org.innovateuk.ifs.application.forms.academiccosts.viewmodel.AcademicCostViewModel;
import org.innovateuk.ifs.application.forms.yourprojectcosts.saver.YourProjectCostsCompleter;
import org.innovateuk.ifs.application.service.SectionStatusRestService;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class AcademicCostsControllerTest extends BaseControllerMockMVCTest<AcademicCostsController> {

    @Override
    protected AcademicCostsController supplyControllerUnderTest() {
        return new AcademicCostsController();
    }

    private static final long APPLICATION_ID = 1L;
    private static final long SECTION_ID = 2L;
    private static final long PROCESS_ROLE_ID = 3L;
    private static final long ORGANISATION_ID = 4L;
    private static final String VIEW = "application/academic-costs";

    @Mock
    private AcademicCostFormPopulator formPopulator;

    @Mock
    private AcademicCostViewModelPopulator viewModelPopulator;

    @Mock
    private AcademicCostSaver saver;

    @Mock
    private SectionStatusRestService sectionStatusRestService;

    @Mock
    private UserRestService userRestService;

    @Mock
    private YourProjectCostsCompleter completeSectionAction;

    @Test
    public void viewAcademicCosts() throws Exception {
        AcademicCostViewModel viewModel = mockViewModel();

        mockMvc.perform(get(APPLICATION_BASE_URL + "{applicationId}/form/academic-costs/organisation/{organisationId}/section/{sectionId}",
                APPLICATION_ID, ORGANISATION_ID, SECTION_ID))
                .andExpect(model().attribute("model", viewModel))
                .andExpect(view().name(VIEW))
                .andExpect(status().isOk());

        verify(formPopulator).populate(any(AcademicCostForm.class), eq(APPLICATION_ID), eq(ORGANISATION_ID));
    }

    @Test
    public void saveAcademicCosts() throws Exception {
        when(saver.save(any(AcademicCostForm.class), eq(APPLICATION_ID), eq(ORGANISATION_ID))).thenReturn(serviceSuccess());

        mockMvc.perform(post(APPLICATION_BASE_URL + "{applicationId}/form/academic-costs/organisation/{organisationId}/section/{sectionId}",
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

        mockMvc.perform(post(APPLICATION_BASE_URL + "{applicationId}/form/academic-costs/organisation/{organisationId}/section/{sectionId}",
                APPLICATION_ID, ORGANISATION_ID, SECTION_ID)
                .param("edit", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("%s%s/form/academic-costs/organisation/%s/section/%s", APPLICATION_BASE_URL, APPLICATION_ID, ORGANISATION_ID, SECTION_ID)));


        verifyZeroInteractions(saver);
        verify(sectionStatusRestService).markAsInComplete(SECTION_ID, APPLICATION_ID, PROCESS_ROLE_ID);
    }

    @Test
    public void complete() throws Exception {
        ProcessRoleResource processRole = newProcessRoleResource().withId(PROCESS_ROLE_ID).build();
        when(saver.save(any(AcademicCostForm.class), eq(APPLICATION_ID), eq(ORGANISATION_ID))).thenReturn(serviceSuccess());
        when(userRestService.findProcessRole(APPLICATION_ID, getLoggedInUser().getId()))
                .thenReturn(restSuccess(processRole));
        when(completeSectionAction.markAsComplete(SECTION_ID, APPLICATION_ID, processRole)).thenReturn(new ValidationMessages());

        mockMvc.perform(post(APPLICATION_BASE_URL + "{applicationId}/form/academic-costs/organisation/{organisationId}/section/{sectionId}",
                APPLICATION_ID, ORGANISATION_ID, SECTION_ID)
                .param("complete", "true")
                .param("tsbReference", "TSB"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/application/%s/form/%s", APPLICATION_ID, SectionType.FINANCE)));

        verify(saver).save(any(AcademicCostForm.class), eq(APPLICATION_ID), eq(ORGANISATION_ID));
        verify(completeSectionAction).markAsComplete(SECTION_ID, APPLICATION_ID, processRole);
    }

    @Test
    public void complete_error() throws Exception {
        AcademicCostViewModel viewModel = mockViewModel();

        mockMvc.perform(post(APPLICATION_BASE_URL + "{applicationId}/form/academic-costs/organisation/{organisationId}/section/{sectionId}",
                APPLICATION_ID, ORGANISATION_ID, SECTION_ID)
                .param("complete", "true"))
                .andExpect(model().attribute("model", viewModel))
                .andExpect(model().attributeHasFieldErrorCode("form", "tsbReference", "NotBlank"))
                .andExpect(view().name(VIEW))
                .andExpect(status().isOk());

        verifyZeroInteractions(saver);
    }

    @Test
    public void autoSave() throws Exception {
        String field = "field";
        String value = "value";
        String fieldId = "123";

        when(saver.autoSave(field, value, APPLICATION_ID, ORGANISATION_ID)).thenReturn(Optional.of(Long.valueOf(fieldId)));
        mockMvc.perform(post(APPLICATION_BASE_URL + "{applicationId}/form/academic-costs/organisation/{organisationId}/section/{sectionId}/auto-save",
                APPLICATION_ID, ORGANISATION_ID, SECTION_ID)
                .param("field", field)
                .param("value", value))
                .andExpect(jsonPath("$.fieldId", equalTo(Integer.valueOf(fieldId))))
                .andExpect(status().isOk());

    }

    private AcademicCostViewModel mockViewModel() {
        AcademicCostViewModel viewModel = mock(AcademicCostViewModel.class);
        when(viewModelPopulator.populate(ORGANISATION_ID, APPLICATION_ID, SECTION_ID, true)).thenReturn(viewModel);
        return viewModel;
    }
}