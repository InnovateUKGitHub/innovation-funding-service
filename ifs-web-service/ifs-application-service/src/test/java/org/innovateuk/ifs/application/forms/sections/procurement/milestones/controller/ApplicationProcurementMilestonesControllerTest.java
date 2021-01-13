package org.innovateuk.ifs.application.forms.sections.procurement.milestones.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.forms.sections.procurement.milestones.form.ProcurementMilestoneForm;
import org.innovateuk.ifs.application.forms.sections.procurement.milestones.form.ProcurementMilestonesForm;
import org.innovateuk.ifs.application.forms.sections.procurement.milestones.populator.ApplicationProcurementMilestoneViewModelPopulator;
import org.innovateuk.ifs.application.forms.sections.procurement.milestones.populator.ProcurementMilestoneFormPopulator;
import org.innovateuk.ifs.application.forms.sections.procurement.milestones.saver.ApplicationProcurementMilestoneFormSaver;
import org.innovateuk.ifs.application.forms.sections.procurement.milestones.validator.ProcurementMilestoneFormValidator;
import org.innovateuk.ifs.application.forms.sections.procurement.milestones.viewmodel.ApplicationProcurementMilestonesViewModel;
import org.innovateuk.ifs.application.service.SectionStatusRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.procurement.milestone.resource.ApplicationProcurementMilestoneResource;
import org.innovateuk.ifs.procurement.milestone.service.ApplicationProcurementMilestoneRestService;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;
import static org.innovateuk.ifs.application.forms.sections.procurement.milestones.form.ProcurementMilestonesForm.generateUnsavedRowId;
import static org.innovateuk.ifs.commons.error.ValidationMessages.noErrors;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.procurement.milestone.builder.ApplicationProcurementMilestoneBuilder.newApplicationProcurementMilestoneResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ApplicationProcurementMilestonesControllerTest extends BaseControllerMockMVCTest<ApplicationProcurementMilestonesController> {

    private static final long APPLICATION_ID = 1L;
    private static final long SECTION_ID = 2L;
    private static final long PROCESS_ROLE_ID = 3L;
    private static final long ORGANISATION_ID = 4L;
    private static final String VIEW = "application/sections/procurement-milestones/application-procurement-milestones";

    @Mock
    private ProcurementMilestoneFormPopulator formPopulator;

    @Mock
    private ApplicationProcurementMilestoneRestService restService;

    @Mock
    private ApplicationProcurementMilestoneFormSaver saver;

    @Mock
    private SectionStatusRestService sectionStatusRestService;

    @Mock
    private ProcessRoleRestService processRoleRestService;

    @Mock
    private ApplicationProcurementMilestoneViewModelPopulator viewModelPopulator;

    @Mock
    private ProcurementMilestoneFormValidator procurementMilestoneFormValidator;

    @Mock
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Override
    protected ApplicationProcurementMilestonesController supplyControllerUnderTest() {
        return new ApplicationProcurementMilestonesController();
    }

    @Test
    public void viewMilestones() throws Exception {
        List<ApplicationProcurementMilestoneResource> milestones = newApplicationProcurementMilestoneResource()
                .withDescription("description")
                .build(1);
        ProcurementMilestonesForm form = new ProcurementMilestonesForm();
        ProcurementMilestoneForm milestoneForm = new ProcurementMilestoneForm();
        milestoneForm.setDescription(milestones.get(0).getDescription());
        form.getMilestones().put(String.valueOf(milestones.get(0).getId()), milestoneForm);
        when(restService.getByApplicationIdAndOrganisationId(APPLICATION_ID, ORGANISATION_ID)).thenReturn(restSuccess(milestones));
        when(formPopulator.populate(milestones)).thenReturn(form);
        ApplicationProcurementMilestonesViewModel model = mockViewModel();

        mockMvc.perform(get(APPLICATION_BASE_URL + "{applicationId}/form/procurement-milestones/organisation/{organisationId}/section/{sectionId}",
                APPLICATION_ID, ORGANISATION_ID, SECTION_ID))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", form))
                .andExpect(model().attribute("model", model))
                .andExpect(view().name(VIEW));
    }

    @Test
    public void saveMilestones() throws Exception {
        when(saver.save(any(ProcurementMilestonesForm.class), eq(APPLICATION_ID), eq(ORGANISATION_ID))).thenReturn(serviceSuccess());

        mockMvc.perform(post(APPLICATION_BASE_URL + "{applicationId}/form/procurement-milestones/organisation/{organisationId}/section/{sectionId}",
                APPLICATION_ID, ORGANISATION_ID, SECTION_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/application/%d/form/FINANCE", APPLICATION_ID)));

        verify(saver).save(any(ProcurementMilestonesForm.class), eq(APPLICATION_ID), eq(ORGANISATION_ID));
    }

    @Test
    public void complete() throws Exception {
        when(saver.save(any(ProcurementMilestonesForm.class), eq(APPLICATION_ID), eq(ORGANISATION_ID))).thenReturn(serviceSuccess());
        when(processRoleRestService.findProcessRole(APPLICATION_ID, getLoggedInUser().getId()))
                .thenReturn(restSuccess(newProcessRoleResource().withId(PROCESS_ROLE_ID).build()));
        when(sectionStatusRestService.markAsComplete(SECTION_ID, APPLICATION_ID, PROCESS_ROLE_ID)).thenReturn(restSuccess(noErrors()));
        ApplicationFinanceResource finance = mock(ApplicationFinanceResource.class);
        when(applicationFinanceRestService.getFinanceDetails(APPLICATION_ID, ORGANISATION_ID)).thenReturn(restSuccess(finance));

        mockMvc.perform(post(APPLICATION_BASE_URL + "{applicationId}/form/procurement-milestones/organisation/{organisationId}/section/{sectionId}",
                APPLICATION_ID, ORGANISATION_ID, SECTION_ID)
                .param("complete", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/application/%d/form/FINANCE", APPLICATION_ID)));

        verify(saver).save(any(ProcurementMilestonesForm.class), eq(APPLICATION_ID), eq(ORGANISATION_ID));
        verify(sectionStatusRestService).markAsComplete(SECTION_ID, APPLICATION_ID, PROCESS_ROLE_ID);
        verify(procurementMilestoneFormValidator).validate(any(ProcurementMilestonesForm.class), eq(finance), any(ValidationHandler.class));
    }

    @Test
    public void edit() throws Exception {
        when(processRoleRestService.findProcessRole(APPLICATION_ID, getLoggedInUser().getId()))
                .thenReturn(restSuccess(newProcessRoleResource().withId(PROCESS_ROLE_ID).build()));
        when(sectionStatusRestService.markAsInComplete(SECTION_ID, APPLICATION_ID, PROCESS_ROLE_ID)).thenReturn(restSuccess());

        mockMvc.perform(post(APPLICATION_BASE_URL + "{applicationId}/form/procurement-milestones/organisation/{organisationId}/section/{sectionId}",
                APPLICATION_ID, ORGANISATION_ID, SECTION_ID)
                .param("edit", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("%s%s/form/procurement-milestones/organisation/%s/section/%s", APPLICATION_BASE_URL, APPLICATION_ID, ORGANISATION_ID, SECTION_ID)));
    }


    @Test
    public void addRowFormPost() throws Exception {
        mockViewModel();

        mockMvc.perform(post(APPLICATION_BASE_URL + "{applicationId}/form/procurement-milestones/organisation/{organisationId}/section/{sectionId}",
                APPLICATION_ID, ORGANISATION_ID, SECTION_ID)
                .param("add_row", ""))
                .andExpect(view().name(VIEW))
                .andExpect(status().isOk());

        verify(saver).addRowForm(any(ProcurementMilestonesForm.class));
    }

    @Test
    public void removeRowFormPost() throws Exception {
        mockViewModel();
        String rowToRemove = "5";

        when(saver.removeRowFromForm(any(ProcurementMilestonesForm.class), eq(rowToRemove))).thenReturn(serviceSuccess());

        mockMvc.perform(post(APPLICATION_BASE_URL + "{applicationId}/form/procurement-milestones/organisation/{organisationId}/section/{sectionId}",
                APPLICATION_ID, ORGANISATION_ID, SECTION_ID)
                .param("remove_row", rowToRemove))
                .andExpect(view().name(VIEW))
                .andExpect(status().isOk());

        verify(saver).removeRowFromForm(any(ProcurementMilestonesForm.class), eq(rowToRemove));
    }

    @Test
    public void autoSave() throws Exception {
        String field = "field";
        String value = "value";
        String fieldId = "123";

        when(saver.autoSave(field, value, APPLICATION_ID, ORGANISATION_ID)).thenReturn(Optional.of(Long.valueOf(fieldId)));
        mockMvc.perform(post(APPLICATION_BASE_URL + "{applicationId}/form/procurement-milestones/organisation/{organisationId}/section/{sectionId}/auto-save",
                APPLICATION_ID, ORGANISATION_ID, SECTION_ID)
                .param("field", field)
                .param("value", value))
                .andExpect(jsonPath("$.fieldId", equalTo(Integer.valueOf(fieldId))))
                .andExpect(status().isOk());

    }

    @Test
    public void ajaxRemoveRow() throws Exception {
        String rowId = "123";

        mockMvc.perform(post(APPLICATION_BASE_URL + "{applicationId}/form/procurement-milestones/organisation/{organisationId}/section/{sectionId}/remove-row/{rowId}",
                APPLICATION_ID, ORGANISATION_ID, SECTION_ID, rowId))
                .andExpect(status().isOk());

        verify(saver).removeRow(rowId);
    }

    @Test
    public void ajaxAddRow() throws Exception {
        ApplicationProcurementMilestonesViewModel model = mockViewModel();
        ProcurementMilestoneForm row = new ProcurementMilestoneForm();
        String rowId = generateUnsavedRowId();
        final ProcurementMilestonesForm[] form = new ProcurementMilestonesForm[1];
        doAnswer((invocation) -> {
            form[0] = (ProcurementMilestonesForm) invocation.getArguments()[0];
            form[0].getMilestones().put(rowId, row);
            return form[0].getMilestones().entrySet().iterator().next();
        }).when(saver).addRowForm(any(ProcurementMilestonesForm.class));

        mockMvc.perform(post(APPLICATION_BASE_URL + "{applicationId}/form/procurement-milestones/organisation/{organisationId}/section/{sectionId}/add-row",
                APPLICATION_ID, ORGANISATION_ID, SECTION_ID))
                .andExpect(view().name("application/procurement-milestones :: ajax-milestone-row"))
                .andExpect(model().attribute("form", form[0]))
                .andExpect(model().attribute("row", row))
                .andExpect(model().attribute("id", rowId))
                .andExpect(model().attribute("model", model))
                .andExpect(status().isOk());

        verify(saver).addRowForm(any(ProcurementMilestonesForm.class));
    }

    private ApplicationProcurementMilestonesViewModel mockViewModel() {
        ApplicationProcurementMilestonesViewModel model = mock(ApplicationProcurementMilestonesViewModel.class);
        when(viewModelPopulator.populate(getLoggedInUser(), APPLICATION_ID, ORGANISATION_ID, SECTION_ID)).thenReturn(model);
        return model;
    }
}