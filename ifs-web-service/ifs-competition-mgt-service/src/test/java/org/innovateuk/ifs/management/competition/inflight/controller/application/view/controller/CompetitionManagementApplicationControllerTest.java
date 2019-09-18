package org.innovateuk.ifs.management.competition.inflight.controller.application.view.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.populator.ApplicationPrintPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.FormInputResponseFileEntryResource;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.resource.IneligibleOutcomeResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.management.application.list.form.ReinstateIneligibleApplicationForm;
import org.innovateuk.ifs.management.application.view.controller.CompetitionManagementApplicationController;
import org.innovateuk.ifs.management.application.view.form.IneligibleApplicationForm;
import org.innovateuk.ifs.management.application.view.populator.ManagementApplicationPopulator;
import org.innovateuk.ifs.management.application.view.populator.ReinstateIneligibleApplicationModelPopulator;
import org.innovateuk.ifs.management.application.view.viewmodel.ManagementApplicationViewModel;
import org.innovateuk.ifs.management.application.view.viewmodel.ReinstateIneligibleApplicationViewModel;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;

import java.util.Collections;
import java.util.List;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static org.innovateuk.ifs.application.builder.IneligibleOutcomeResourceBuilder.newIneligibleOutcomeResource;
import static org.innovateuk.ifs.application.resource.ApplicationState.SUBMITTED;
import static org.innovateuk.ifs.application.service.Futures.settable;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.commons.error.CommonErrors.internalServerErrorError;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class CompetitionManagementApplicationControllerTest extends BaseControllerMockMVCTest<CompetitionManagementApplicationController> {

    @Mock
    private ProcessRoleService processRoleService;
    @Mock
    private UserRestService userRestService;
    @Mock
    private ApplicationPrintPopulator applicationPrintPopulator;
    @Mock
    private ApplicationRestService applicationRestService;
    @Mock
    private FormInputResponseRestService formInputResponseRestService;
    @Mock
    private ApplicationSummaryRestService applicationSummaryRestService;
    @Mock
    private ReinstateIneligibleApplicationModelPopulator reinstateIneligibleApplicationModelPopulator;
    @Mock
    private ManagementApplicationPopulator managementApplicationPopulator;

    @Test
    public void displayApplicationOverviewAsCompAdmin() throws Exception {
        long competitionId = 1L;
        long applicationId = 2L;
        ManagementApplicationViewModel viewModel = mock(ManagementApplicationViewModel.class);
        when(managementApplicationPopulator.populate(eq(applicationId), eq(getLoggedInUser()))).thenReturn(viewModel);

        mockMvc.perform(get("/competition/{competitionId}/application/{applicationId}", competitionId, applicationId))
                .andExpect(status().isOk())
                .andExpect(view().name("competition-mgt-application-overview"))
                .andExpect(model().attribute("model", viewModel))
                .andReturn();
    }

    @Test
    public void reinstateIneligibleApplication() throws Exception {
        long competitionId = 1L;
        long applicationId = 2L;

        when(applicationRestService.updateApplicationState(applicationId, SUBMITTED)).thenReturn(restSuccess());

        mockMvc.perform(post("/competition/{competitionId}/application/{applicationId}/reinstateIneligibleApplication", competitionId, applicationId))
                .andExpect(model().attributeExists("form"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/competition/%s/applications/ineligible", competitionId)));

        verify(applicationRestService).updateApplicationState(applicationId, SUBMITTED);
        verifyNoMoreInteractions(applicationRestService);
    }

    @Test
    public void reinstateIneligibleApplication_failureUpdatingState() throws Exception {
        long competitionId = 1L;
        ApplicationResource applicationResource = newApplicationResource()
                .withCompetition(competitionId)
                .withName("Plastic reprocessing with zero waste")
                .build();

        ReinstateIneligibleApplicationViewModel expectedViewModel = new ReinstateIneligibleApplicationViewModel(competitionId,
                applicationResource.getId(), "Plastic reprocessing with zero waste");

        when(applicationRestService.updateApplicationState(applicationResource.getId(), SUBMITTED)).thenReturn(restFailure(internalServerErrorError()));
        when(applicationRestService.getApplicationById(applicationResource.getId())).thenReturn(restSuccess(applicationResource));
        when(reinstateIneligibleApplicationModelPopulator.populateModel(applicationResource)).thenReturn(expectedViewModel);

        MvcResult mvcResult = mockMvc.perform(post("/competition/{competitionId}/application/{applicationId}/reinstateIneligibleApplication", competitionId, applicationResource.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(view().name("application/reinstate-ineligible-application-confirm"))
                .andReturn();

        ReinstateIneligibleApplicationForm form = (ReinstateIneligibleApplicationForm) mvcResult.getModelAndView().getModel().get("form");

        BindingResult bindingResult = form.getBindingResult();
        assertEquals(1, bindingResult.getGlobalErrorCount());
        assertFalse(bindingResult.hasFieldErrors());
        assertEquals(CommonFailureKeys.GENERAL_UNEXPECTED_ERROR.getErrorKey(), bindingResult.getGlobalError().getCode());

        InOrder inOrder = inOrder(applicationRestService);
        inOrder.verify(applicationRestService).updateApplicationState(applicationResource.getId(), SUBMITTED);
        inOrder.verify(applicationRestService).getApplicationById(applicationResource.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void reinstateIneligibleApplicationConfirm() throws Exception {
        long competitionId = 1L;
        ApplicationResource applicationResource = newApplicationResource()
                .withCompetition(competitionId)
                .withName("Plastic reprocessing with zero waste")
                .build();

        when(applicationRestService.getApplicationById(applicationResource.getId())).thenReturn(restSuccess(applicationResource));

        ReinstateIneligibleApplicationViewModel expectedViewModel = new ReinstateIneligibleApplicationViewModel(competitionId,
                applicationResource.getId(), "Plastic reprocessing with zero waste");
        when(reinstateIneligibleApplicationModelPopulator.populateModel(applicationResource)).thenReturn(expectedViewModel);

        mockMvc.perform(get("/competition/{competitionId}/application/{applicationId}/reinstateIneligibleApplication/confirm",
                competitionId, applicationResource.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("application/reinstate-ineligible-application-confirm"));

        verify(applicationRestService).getApplicationById(applicationResource.getId());
        verifyNoMoreInteractions(applicationRestService);
    }

    @Test
    public void markAsIneligible() throws Exception {
        long applicationId = 1L;
        long competitionId = 2L;
        IneligibleOutcomeResource ineligibleOutcomeResource = newIneligibleOutcomeResource().withReason("coz").build();
        when(applicationRestService.markAsIneligible(applicationId, ineligibleOutcomeResource)).thenReturn(restSuccess());

        mockMvc.perform(post("/competition/{competitionId}/application/{applicationId}", competitionId, applicationId)
                .param("markAsIneligible", "")
                .param("ineligibleReason", "coz"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/competition/" + competitionId + "/applications/ineligible"));

        verify(applicationRestService).markAsIneligible(applicationId, ineligibleOutcomeResource);
    }

    @Test
    public void markAsIneligible_noReason() throws Exception {
        long applicationId = 1L;
        long competitionId = 2L;

        ManagementApplicationViewModel viewModel = mock(ManagementApplicationViewModel.class);
        when(managementApplicationPopulator.populate(eq(applicationId), eq(getLoggedInUser()))).thenReturn(viewModel);

        MvcResult result = mockMvc.perform(post("/competition/{competitionId}/application/{applicationId}", competitionId, applicationId)
                .param("markAsIneligible", ""))
                .andExpect(view().name("competition-mgt-application-overview"))
                .andReturn();

        IneligibleApplicationForm form = (IneligibleApplicationForm) result.getModelAndView().getModel().get("ineligibleForm");
        BindingResult bindingResult = form.getBindingResult();
        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("ineligibleReason"));
        assertEquals("This field cannot be left blank.", bindingResult.getFieldError("ineligibleReason").getDefaultMessage());

    }

    @Test
    public void downloadAsInternalUser() throws Exception {
        Role.internalRoles().forEach(role -> {
            try {
                setLoggedInUser(newUserResource().withRolesGlobal(singletonList(role)).build());
                Long formInputId = 35L;
                long applicationId = 2L;
                long competitionId = 3L;
                long processRoleId = role.ordinal(); // mapping role ordinal as process role (just for mocking)
                List<FormInputResponseResource> inputResponse = newFormInputResponseResource().withUpdatedBy(processRoleId).build(1);
                when(formInputResponseRestService.getByFormInputIdAndApplication(formInputId, applicationId)).thenReturn(RestResult.restSuccess(inputResponse));

                ProcessRoleResource processRoleResource = newProcessRoleResource().withId(processRoleId).build();
                when(processRoleService.getById(processRoleId)).thenReturn(settable(processRoleResource));
                ByteArrayResource bar = new ByteArrayResource("File contents".getBytes());
                when(formInputResponseRestService.getFile(formInputId, applicationId, processRoleId)).thenReturn(restSuccess(bar));
                FileEntryResource fileEntryResource = newFileEntryResource().with(id(999L)).withName("file1").withMediaType("text/csv").build();
                FormInputResponseFileEntryResource formInputResponseFileEntryResource = new FormInputResponseFileEntryResource(fileEntryResource, 123L, 456L, 789L);
                when(formInputResponseRestService.getFileDetails(formInputId, applicationId, processRoleId)).thenReturn(RestResult.restSuccess(formInputResponseFileEntryResource));

                mockMvc.perform(get("/competition/" + competitionId + "/application/" + applicationId + "/forminput/" + formInputId + "/download"))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(("text/csv")))
                        .andExpect(header().string("Content-Type", "text/csv"))
                        .andExpect(header().string("Content-disposition", "inline; filename=\"file1\""))
                        .andExpect(content().string("File contents"));

                verify(formInputResponseRestService).getFile(formInputId, applicationId, processRoleId);
                verify(formInputResponseRestService).getFileDetails(formInputId, applicationId, processRoleId);

            } catch (Exception e) {
                fail();
            }
        });
    }

    @Test
    public void downloadAsPartner() throws Exception {
        UserResource user = newUserResource().withRolesGlobal(Collections.singletonList(Role.PARTNER)).build();
        long applicationId = 2L;
        long competitionId = 3L;
        setLoggedInUser(user);

        Long formInputId = 35L;
        long processRoleId = 73L;
        ProcessRoleResource processRoleResource = newProcessRoleResource().withId(processRoleId).build();
        when(userRestService.findProcessRole(user.getId(), applicationId)).thenReturn(restSuccess(processRoleResource));
        ByteArrayResource bar = new ByteArrayResource("File contents".getBytes());
        when(formInputResponseRestService.getFile(formInputId, applicationId, processRoleId)).thenReturn(restSuccess(bar));
        FileEntryResource fileEntryResource = newFileEntryResource().with(id(999L)).withName("file1").withMediaType("text/csv").build();
        FormInputResponseFileEntryResource formInputResponseFileEntryResource = new FormInputResponseFileEntryResource(fileEntryResource, 123L, 456L, 789L);
        when(formInputResponseRestService.getFileDetails(formInputId, applicationId, processRoleId)).thenReturn(RestResult.restSuccess(formInputResponseFileEntryResource));

        mockMvc.perform(get("/competition/" + competitionId + "/application/" + applicationId + "/forminput/" + formInputId + "/download"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(("text/csv")))
                .andExpect(header().string("Content-Type", "text/csv"))
                .andExpect(header().string("Content-disposition", "inline; filename=\"file1\""))
                .andExpect(content().string("File contents"));

        verify(formInputResponseRestService).getFile(formInputId, applicationId, processRoleId);
        verify(formInputResponseRestService).getFileDetails(formInputId, applicationId, processRoleId);
    }

    @Override
    protected CompetitionManagementApplicationController supplyControllerUnderTest() {
        return new CompetitionManagementApplicationController();
    }
}
