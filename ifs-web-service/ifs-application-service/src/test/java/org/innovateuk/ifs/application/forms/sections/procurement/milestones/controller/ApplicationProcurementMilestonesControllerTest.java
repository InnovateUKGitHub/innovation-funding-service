package org.innovateuk.ifs.application.forms.sections.procurement.milestones.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.forms.sections.procurement.milestones.form.ProcurementMilestonesForm;
import org.innovateuk.ifs.application.forms.sections.procurement.milestones.populator.ProcurementMilestoneFormPopulator;
import org.innovateuk.ifs.application.forms.sections.procurement.milestones.saver.ApplicationProcurementMilestoneFormSaver;
import org.innovateuk.ifs.application.forms.sections.procurement.milestones.viewmodel.ApplicationProcurementMilestonesViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.service.SectionStatusRestService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.procurement.milestone.resource.ApplicationProcurementMilestoneResource;
import org.innovateuk.ifs.procurement.milestone.service.ApplicationProcurementMilestoneRestService;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;
import static org.innovateuk.ifs.commons.error.ValidationMessages.noErrors;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.procurement.milestone.builder.ApplicationProcurementMilestoneBuilder.newApplicationProcurementMilestoneResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ApplicationProcurementMilestonesControllerTest extends BaseControllerMockMVCTest<ApplicationProcurementMilestonesController> {

    private static final long APPLICATION_ID = 1L;
    private static final long SECTION_ID = 2L;
    private static final long PROCESS_ROLE_ID = 3L;
    private static final long ORGANISATION_ID = 4L;
    private static final String VIEW = "application/sections/procurement-milestones/procurement-milestones";

    @Mock
    private ProcurementMilestoneFormPopulator formPopulator;

    @Mock
    private ApplicationProcurementMilestoneRestService restService;

    @Mock
    private ApplicationRestService applicationRestService;

    @Mock
    private ApplicationProcurementMilestoneFormSaver saver;

    @Mock
    private SectionStatusRestService sectionStatusRestService;

    @Mock
    private ProcessRoleRestService processRoleRestService;

    @Mock
    private SectionService sectionService;

    @Mock
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Override
    protected ApplicationProcurementMilestonesController supplyControllerUnderTest() {
        return new ApplicationProcurementMilestonesController();
    }

    @Test
    public void viewMilestones() throws Exception {
        List<ApplicationProcurementMilestoneResource> milestones = newApplicationProcurementMilestoneResource().build(1);
        ProcurementMilestonesForm form = new ProcurementMilestonesForm();
        when(restService.getByApplicationIdAndOrganisationId(APPLICATION_ID, ORGANISATION_ID)).thenReturn(restSuccess(milestones));
        when(formPopulator.populate(milestones)).thenReturn(form);
        mockViewModel();

        MvcResult result = mockMvc.perform(get(APPLICATION_BASE_URL + "{applicationId}/form/procurement-milestones/organisation/{organisationId}/section/{sectionId}",
                APPLICATION_ID, ORGANISATION_ID, SECTION_ID))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", form))
                .andExpect(view().name(VIEW))
                .andReturn();

        ApplicationProcurementMilestonesViewModel model = (ApplicationProcurementMilestonesViewModel) result.getModelAndView().getModel().get("model");

        assertThat(model.getDurations(), is(equalTo(newArrayList(1L, 2L, 3L, 4L, 5L))));
        assertThat(model.getFinancesUrl(), is(equalTo(String.format("/application/%d/form/FINANCE/%d", APPLICATION_ID, ORGANISATION_ID))));
        assertThat(model.getFundingAmount(), is(equalTo(BigInteger.TEN)));
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

        mockMvc.perform(post(APPLICATION_BASE_URL + "{applicationId}/form/procurement-milestones/organisation/{organisationId}/section/{sectionId}",
                APPLICATION_ID, ORGANISATION_ID, SECTION_ID)
                .param("complete", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/application/%d/form/FINANCE", APPLICATION_ID)));

        verify(saver).save(any(ProcurementMilestonesForm.class), eq(APPLICATION_ID), eq(ORGANISATION_ID));
        verify(sectionStatusRestService).markAsComplete(SECTION_ID, APPLICATION_ID, PROCESS_ROLE_ID);
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

    private void mockViewModel() {
        ApplicationFinanceResource finance = mock(ApplicationFinanceResource.class);
        ApplicationResource application = newApplicationResource()
                .withDurationInMonths(5L)
                .build();
        when(applicationFinanceRestService.getFinanceDetails(APPLICATION_ID, ORGANISATION_ID)).thenReturn(restSuccess(finance));
        when(finance.getTotalFundingSought()).thenReturn(BigDecimal.TEN);
        when(applicationRestService.getApplicationById(APPLICATION_ID)).thenReturn(restSuccess(application));
        when(sectionService.getCompleted(APPLICATION_ID, ORGANISATION_ID)).thenReturn(newArrayList(SECTION_ID));
    }
}