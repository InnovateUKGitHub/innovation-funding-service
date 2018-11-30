package org.innovateuk.ifs.application.forms.yourprojectcosts.controller;

import org.innovateuk.ifs.AbstractAsyncWaitMockMVCTest;
import org.innovateuk.ifs.application.forms.saver.ApplicationSectionFinanceSaver;
import org.innovateuk.ifs.application.forms.yourprojectcosts.form.LabourRowForm;
import org.innovateuk.ifs.application.forms.yourprojectcosts.form.YourProjectCostsForm;
import org.innovateuk.ifs.application.forms.yourprojectcosts.populator.ApplicationYourProjectCostsFormPopulator;
import org.innovateuk.ifs.application.forms.yourprojectcosts.populator.YourProjectCostsViewModelPopulator;
import org.innovateuk.ifs.application.forms.yourprojectcosts.saver.ApplicationYourProjectCostsSaver;
import org.innovateuk.ifs.application.forms.yourprojectcosts.saver.YourProjectCostsAutosaver;
import org.innovateuk.ifs.application.forms.yourprojectcosts.validator.YourProjectCostsFormValidator;
import org.innovateuk.ifs.application.forms.yourprojectcosts.viewmodel.YourProjectCostsViewModel;
import org.innovateuk.ifs.application.service.SectionStatusRestService;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class YourProjectCostsControllerTest extends AbstractAsyncWaitMockMVCTest<YourProjectCostsController> {

    @Override
    protected YourProjectCostsController supplyControllerUnderTest() {
        return new YourProjectCostsController();
    }

    private static final long APPLICATION_ID = 1L;
    private static final long SECTION_ID = 2L;
    private static final long PROCESS_ROLE_ID = 3L;
    private static final long ORGANISATION_ID = 4L;
    private static final String VIEW = "application/your-project-costs";

    @Mock
    private ApplicationYourProjectCostsFormPopulator formPopulator;

    @Mock
    private YourProjectCostsViewModelPopulator viewModelPopulator;

    @Mock
    private ApplicationYourProjectCostsSaver saver;

    @Mock
    private YourProjectCostsAutosaver autosaver;

    @Mock
    private SectionStatusRestService sectionStatusRestService;

    @Mock
    private UserRestService userRestService;

    @Mock
    private YourProjectCostsFormValidator yourFundingFormValidator;

    @Mock
    private ApplicationSectionFinanceSaver completeSectionAction;

    @Test
    public void viewYourProjectCosts() throws Exception {
        YourProjectCostsViewModel viewModel = mockViewModel();

        mockMvc.perform(get(APPLICATION_BASE_URL + "{applicationId}/form/your-project-costs/organisation/{organisationId}/section/{sectionId}",
                APPLICATION_ID, ORGANISATION_ID, SECTION_ID))
                .andExpect(model().attribute("model", viewModel))
                .andExpect(view().name(VIEW))
                .andExpect(status().isOk());

        verify(formPopulator).populateForm(any(YourProjectCostsForm.class), eq(APPLICATION_ID), eq(getLoggedInUser()));
    }

    @Test
    public void saveYourProjectCosts() throws Exception {
        when(saver.save(any(YourProjectCostsForm.class), eq(APPLICATION_ID), eq(getLoggedInUser()))).thenReturn(serviceSuccess());

        mockMvc.perform(post(APPLICATION_BASE_URL + "{applicationId}/form/your-project-costs/organisation/{organisationId}/section/{sectionId}",
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

        mockMvc.perform(post(APPLICATION_BASE_URL + "{applicationId}/form/your-project-costs/organisation/{organisationId}/section/{sectionId}",
                APPLICATION_ID, ORGANISATION_ID, SECTION_ID)
                .param("edit", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("%s%s/form/your-project-costs/organisation/%s/section/%s", APPLICATION_BASE_URL, APPLICATION_ID, ORGANISATION_ID, SECTION_ID)));


        verifyZeroInteractions(saver);
        verify(sectionStatusRestService).markAsInComplete(SECTION_ID, APPLICATION_ID, PROCESS_ROLE_ID);
    }

    @Test
    public void complete() throws Exception {
        ProcessRoleResource processRole = newProcessRoleResource().withId(PROCESS_ROLE_ID).build();
        when(saver.save(any(YourProjectCostsForm.class), eq(APPLICATION_ID), eq(getLoggedInUser()))).thenReturn(serviceSuccess());
        when(userRestService.findProcessRole(APPLICATION_ID, getLoggedInUser().getId()))
                .thenReturn(restSuccess(processRole));
        when(sectionStatusRestService.markAsComplete(SECTION_ID, APPLICATION_ID, PROCESS_ROLE_ID)).thenReturn(restSuccess(emptyList()));

        mockMvc.perform(post(APPLICATION_BASE_URL + "{applicationId}/form/your-project-costs/organisation/{organisationId}/section/{sectionId}",
                APPLICATION_ID, ORGANISATION_ID, SECTION_ID)
                .param("complete", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/application/%s/form/%s", APPLICATION_ID, SectionType.FINANCE)));

        verify(saver).save(any(YourProjectCostsForm.class), eq(APPLICATION_ID), eq(getLoggedInUser()));
        verify(sectionStatusRestService).markAsComplete(SECTION_ID, APPLICATION_ID, PROCESS_ROLE_ID);
        verify(completeSectionAction).handleMarkProjectCostsAsComplete(processRole);
    }


    @Test
    public void complete_error() throws Exception {
        YourProjectCostsViewModel viewModel = mockViewModel();
        doAnswer((invocationOnMock) -> {
            ((ValidationHandler) invocationOnMock.getArguments()[1]).addAnyErrors(new ValidationMessages(fieldError("requestingFunding", "something", "error")));
            return Void.class;
        }).when(yourFundingFormValidator).validate(any(), any());

        mockMvc.perform(post(APPLICATION_BASE_URL + "{applicationId}/form/your-project-costs/organisation/{organisationId}/section/{sectionId}",
                APPLICATION_ID, ORGANISATION_ID, SECTION_ID)
                .param("complete", "true"))
                .andExpect(model().attribute("model", viewModel))
                .andExpect(view().name(VIEW))
                .andExpect(status().isOk());

        verifyZeroInteractions(saver);
    }


    @Test
    public void addRowFormPost() throws Exception {
        YourProjectCostsViewModel viewModel = mockViewModel();
        FinanceRowType type = FinanceRowType.LABOUR;

        mockMvc.perform(post(APPLICATION_BASE_URL + "{applicationId}/form/your-project-costs/organisation/{organisationId}/section/{sectionId}",
                APPLICATION_ID, ORGANISATION_ID, SECTION_ID)
                .param("add_cost", type.name()))
                .andExpect(model().attribute("model", viewModel))
                .andExpect(view().name(VIEW))
                .andExpect(status().isOk());

        verify(saver).addRowForm(any(YourProjectCostsForm.class), eq(type));
    }

    @Test
    public void removeRowFormPost() throws Exception {
        YourProjectCostsViewModel viewModel = mockViewModel();
        String rowToRemove = "5";

        mockMvc.perform(post(APPLICATION_BASE_URL + "{applicationId}/form/your-project-costs/organisation/{organisationId}/section/{sectionId}",
                APPLICATION_ID, ORGANISATION_ID, SECTION_ID)
                .param("remove_cost", rowToRemove))
                .andExpect(model().attribute("model", viewModel))
                .andExpect(view().name(VIEW))
                .andExpect(status().isOk());

        verify(saver).removeRowFromForm(any(YourProjectCostsForm.class), eq(rowToRemove));
    }

    @Test
    public void autoSave() throws Exception {
        String field = "field";
        String value = "value";
        String fieldId = "123";

        when(autosaver.autoSave(field, value, APPLICATION_ID, getLoggedInUser())).thenReturn(Optional.of(Long.valueOf(fieldId)));
        mockMvc.perform(post(APPLICATION_BASE_URL + "{applicationId}/form/your-project-costs/organisation/{organisationId}/section/{sectionId}/auto-save",
                APPLICATION_ID, ORGANISATION_ID, SECTION_ID)
                .param("field", field)
                .param("value", value))
                .andExpect(jsonPath("$.fieldId", equalTo(Integer.valueOf(fieldId))))
                .andExpect(status().isOk());

    }

    @Test
    public void ajaxRemoveRow() throws Exception {
        String costId = "123";

        mockMvc.perform(post(APPLICATION_BASE_URL + "{applicationId}/form/your-project-costs/organisation/{organisationId}/section/{sectionId}/remove-row/{rowId}",
                APPLICATION_ID, ORGANISATION_ID, SECTION_ID, costId))
                .andExpect(status().isOk());

        verify(saver).removeFinanceRow(costId);
    }

    @Test
    public void ajaxAddRow() throws Exception {
        String rowId = "123";
        LabourRowForm row = new LabourRowForm();
        row.setCostId(Long.valueOf(rowId));
        FinanceRowType type = FinanceRowType.LABOUR;

        doAnswer((invocation) -> {
            YourProjectCostsForm form = (YourProjectCostsForm) invocation.getArguments()[0];
            form.getLabour().getRows().put(rowId, row);
            return form.getLabour().getRows().entrySet().iterator().next();
        }).when(saver).addRowForm(any(YourProjectCostsForm.class), eq(type));

        mockMvc.perform(post(APPLICATION_BASE_URL + "{applicationId}/form/your-project-costs/organisation/{organisationId}/section/{sectionId}/add-row/{type}",
                APPLICATION_ID, ORGANISATION_ID, SECTION_ID, type))
                .andExpect(view().name("application/your-project-costs-fragments :: ajax_labour_row"))
                .andExpect(model().attribute("row", row))
                .andExpect(model().attribute("id", rowId))
                .andExpect(status().isOk());

        verify(saver).addRowForm(any(YourProjectCostsForm.class), eq(type));
    }


    private YourProjectCostsViewModel mockViewModel() {
        YourProjectCostsViewModel viewModel = mock(YourProjectCostsViewModel.class);
        when(viewModelPopulator.populate(APPLICATION_ID, SECTION_ID, ORGANISATION_ID, getLoggedInUser().isInternalUser(), "")).thenReturn(viewModel);
        return viewModel;
    }
}