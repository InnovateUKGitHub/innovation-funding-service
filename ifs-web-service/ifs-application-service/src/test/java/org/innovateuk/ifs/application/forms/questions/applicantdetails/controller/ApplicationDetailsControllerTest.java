package org.innovateuk.ifs.application.forms.questions.applicantdetails.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.forms.questions.applicationdetails.controller.ApplicationDetailsController;
import org.innovateuk.ifs.application.forms.questions.applicationdetails.form.ApplicationDetailsForm;
import org.innovateuk.ifs.application.forms.questions.applicationdetails.model.ApplicationDetailsViewModel;
import org.innovateuk.ifs.application.forms.questions.applicationdetails.populator.ApplicationDetailsViewModelPopulator;
import org.innovateuk.ifs.application.populator.ApplicationNavigationPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.junit.Test;
import org.mockito.Mock;

import java.time.LocalDate;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class ApplicationDetailsControllerTest extends BaseControllerMockMVCTest<ApplicationDetailsController> {

    @Mock
    private ApplicationDetailsViewModelPopulator applicationDetailsViewModelPopulator;

    @Mock
    private QuestionStatusRestService questionStatusRestService;

    @Mock
    private UserRestService userRestService;

    @Mock
    private ApplicantRestService applicantRestService;

    @Mock
    private ApplicationNavigationPopulator applicationNavigationPopulator;

    @Mock
    private ApplicationService applicationService;

    @Override
    protected ApplicationDetailsController supplyControllerUnderTest() {
        return new ApplicationDetailsController(
            applicationDetailsViewModelPopulator,
            questionStatusRestService,
            userRestService,
            applicantRestService,
            applicationNavigationPopulator,
            applicationService
        );
    }

    @Test
    public void viewApplicationDetails() throws Exception {
        long questionId = 1L;
        long applicationId = 2L;

        ApplicationDetailsViewModel viewModel = mock(ApplicationDetailsViewModel.class);
        ApplicantQuestionResource applicantQuestionResource = mock(ApplicantQuestionResource.class);
        when(applicantRestService.getQuestion(anyLong(), anyLong(), anyLong())).thenReturn(applicantQuestionResource);
        when(viewModel.getApplication()).thenReturn(newApplicationResource().build());
        when(applicationDetailsViewModelPopulator.populate(any(ApplicantQuestionResource.class))).thenReturn(viewModel);
        mockMvc.perform(
                get("/application/{applicationId}/form/question/{questionId}/application-details", applicationId, questionId))
                .andExpect(status().isOk())
                .andExpect(view().name("application/questions/application-details"))
                .andReturn();
    }

    @Test
    public void saveAndReturn() throws Exception {
        long questionId = 1L;
        long applicationId = 2L;
        ApplicationDetailsForm applicationDetailsForm = new ApplicationDetailsForm();
        applicationDetailsForm.setName("name");
        applicationDetailsForm.setResubmission(FALSE);
        applicationDetailsForm.setStartDate(LocalDate.now().plusYears(1));
        applicationDetailsForm.setDurationInMonths(3L);

        ApplicationDetailsViewModel viewModel = mock(ApplicationDetailsViewModel.class);
        ApplicantQuestionResource applicantQuestionResource = mock(ApplicantQuestionResource.class);
        when(applicantRestService.getQuestion(anyLong(), anyLong(), anyLong())).thenReturn(applicantQuestionResource);
        when(viewModel.getApplication()).thenReturn(newApplicationResource().build());
        when(applicationDetailsViewModelPopulator.populate(any(ApplicantQuestionResource.class))).thenReturn(viewModel);
        when(applicationService.getById(anyLong())).thenReturn(newApplicationResource().build());
        when(applicationService.save(any(ApplicationResource.class))).thenReturn(null);

        mockMvc.perform(
                post("/application/{applicationId}/form/question/{questionId}/application-details", applicationId, questionId)
                        .param("name", String.valueOf(applicationDetailsForm.getName()))
                        .param("startDate", "startDate")
                        .param("startDate.year",  String.valueOf(applicationDetailsForm.getStartDate().getYear()))
                        .param("startDate.monthValue",  String.valueOf(applicationDetailsForm.getStartDate().getMonthValue()))
                        .param("startDate.dayOfMonth",  String.valueOf(applicationDetailsForm.getStartDate().getDayOfMonth()))
                        .param("durationInMonths", String.valueOf(applicationDetailsForm.getDurationInMonths()))
                        .param("resubmission", String.valueOf(applicationDetailsForm.getResubmission()))
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/application/%d", applicationId)))
                .andReturn();
    }

    @Test
    public void saveAndReturnInvalidForm() throws Exception {
        long questionId = 1L;
        long applicationId = 2L;
        ApplicationDetailsForm invalidForm = new ApplicationDetailsForm();
        invalidForm.setName("");
        invalidForm.setResubmission(TRUE);
        invalidForm.setStartDate(LocalDate.now().plusYears(1));
        invalidForm.setDurationInMonths(3L);

        ApplicationDetailsViewModel viewModel = mock(ApplicationDetailsViewModel.class);
        ApplicantQuestionResource applicantQuestionResource = mock(ApplicantQuestionResource.class);
        when(applicantRestService.getQuestion(anyLong(), anyLong(), anyLong())).thenReturn(applicantQuestionResource);
        when(viewModel.getApplication()).thenReturn(newApplicationResource().build());
        when(applicationDetailsViewModelPopulator.populate(any(ApplicantQuestionResource.class))).thenReturn(viewModel);
        when(applicationService.getById(anyLong())).thenReturn(newApplicationResource().build());
        when(applicationService.save(any(ApplicationResource.class))).thenReturn(null);

        mockMvc.perform(
                post("/application/{applicationId}/form/question/{questionId}/application-details", applicationId, questionId)
                        .param("name", String.valueOf(invalidForm.getName()))
                        .param("startDate",  String.valueOf(invalidForm.getStartDate()))
                        .param("durationInMonths", String.valueOf(invalidForm.getDurationInMonths()))
                        .param("resubmission", String.valueOf(invalidForm.getResubmission()))
        )
                .andExpect(status().isOk())
                .andExpect(view().name("application/questions/application-details"))
                .andReturn();
    }

    @Test
    public void markAsComplete() throws Exception {
        long questionId = 1L;
        long applicationId = 2L;
        ApplicationDetailsForm applicationDetailsForm = new ApplicationDetailsForm();
        applicationDetailsForm.setName("name");
        applicationDetailsForm.setResubmission(FALSE);
        applicationDetailsForm.setStartDate(LocalDate.now().plusYears(1));
        applicationDetailsForm.setDurationInMonths(3L);

        ApplicationDetailsViewModel viewModel = mock(ApplicationDetailsViewModel.class);
        ApplicantQuestionResource applicantQuestionResource = mock(ApplicantQuestionResource.class);
        when(applicantRestService.getQuestion(anyLong(), anyLong(), anyLong())).thenReturn(applicantQuestionResource);
        when(viewModel.getApplication()).thenReturn(newApplicationResource().build());
        when(applicationDetailsViewModelPopulator.populate(any(ApplicantQuestionResource.class))).thenReturn(viewModel);
        when(applicationService.getById(anyLong())).thenReturn(newApplicationResource().build());
        when(applicationService.save(any(ApplicationResource.class))).thenReturn(null);
        when(userRestService.findProcessRole(anyLong(), anyLong())).thenReturn(restSuccess(newProcessRoleResource().build()));
        when(questionStatusRestService.markAsComplete(anyLong(), anyLong(), anyLong())).thenReturn(restSuccess(emptyList()));

        mockMvc.perform(
                post("/application/{applicationId}/form/question/{questionId}/application-details", applicationId, questionId)
                        .param("mark_as_complete", String.valueOf(TRUE))
                        .param("name", String.valueOf(applicationDetailsForm.getName()))
                        .param("startDate", "startDate")
                        .param("startDate.year",  String.valueOf(applicationDetailsForm.getStartDate().getYear()))
                        .param("startDate.monthValue",  String.valueOf(applicationDetailsForm.getStartDate().getMonthValue()))
                        .param("startDate.dayOfMonth",  String.valueOf(applicationDetailsForm.getStartDate().getDayOfMonth()))
                        .param("durationInMonths", String.valueOf(applicationDetailsForm.getDurationInMonths()))
                        .param("resubmission", String.valueOf(applicationDetailsForm.getResubmission()))
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/application/%d/form/question/%d/application-details", applicationId, questionId)))
                .andReturn();
    }

    @Test
    public void markAsCompleteInvalidForm() throws Exception {
        long questionId = 1L;
        long applicationId = 2L;
        ApplicationDetailsForm applicationDetailsForm = new ApplicationDetailsForm();
        applicationDetailsForm.setName("");
        applicationDetailsForm.setResubmission(FALSE);
        applicationDetailsForm.setStartDate(LocalDate.now().plusYears(1));
        applicationDetailsForm.setDurationInMonths(3L);

        ApplicationDetailsViewModel viewModel = mock(ApplicationDetailsViewModel.class);
        ApplicantQuestionResource applicantQuestionResource = mock(ApplicantQuestionResource.class);
        when(applicantRestService.getQuestion(anyLong(), anyLong(), anyLong())).thenReturn(applicantQuestionResource);
        when(viewModel.getApplication()).thenReturn(newApplicationResource().build());
        when(applicationDetailsViewModelPopulator.populate(any(ApplicantQuestionResource.class))).thenReturn(viewModel);

        mockMvc.perform(
                post("/application/{applicationId}/form/question/{questionId}/application-details", applicationId, questionId)
                        .param("mark_as_complete", String.valueOf(TRUE))
                        .param("name", String.valueOf(applicationDetailsForm.getName()))
                        .param("startDate", "startDate")
                        .param("startDate.year",  String.valueOf(applicationDetailsForm.getStartDate().getYear()))
                        .param("startDate.monthValue",  String.valueOf(applicationDetailsForm.getStartDate().getMonthValue()))
                        .param("startDate.dayOfMonth",  String.valueOf(applicationDetailsForm.getStartDate().getDayOfMonth()))
                        .param("durationInMonths", String.valueOf(applicationDetailsForm.getDurationInMonths()))
                        .param("resubmission", String.valueOf(applicationDetailsForm.getResubmission()))
        )
                .andExpect(status().isOk())
                .andExpect(view().name("application/questions/application-details"))
                .andReturn();
    }

    @Test
    public void markAsIncomplete() throws Exception {
        long questionId = 1L;
        long applicationId = 2L;
        ApplicationDetailsForm applicationDetailsForm = new ApplicationDetailsForm();
        applicationDetailsForm.setName("name");
        applicationDetailsForm.setResubmission(FALSE);
        applicationDetailsForm.setStartDate(LocalDate.now().plusYears(1));
        applicationDetailsForm.setDurationInMonths(3L);

        ApplicationDetailsViewModel viewModel = mock(ApplicationDetailsViewModel.class);
        ApplicantQuestionResource applicantQuestionResource = mock(ApplicantQuestionResource.class);
        when(applicantRestService.getQuestion(anyLong(), anyLong(), anyLong())).thenReturn(applicantQuestionResource);
        when(viewModel.getApplication()).thenReturn(newApplicationResource().build());
        when(applicationDetailsViewModelPopulator.populate(any(ApplicantQuestionResource.class))).thenReturn(viewModel);
        when(userRestService.findProcessRole(anyLong(), anyLong())).thenReturn(restSuccess(newProcessRoleResource().build()));
        when(questionStatusRestService.markAsInComplete(anyLong(), anyLong(), anyLong())).thenReturn(restSuccess());

        mockMvc.perform(
                post("/application/{applicationId}/form/question/{questionId}/application-details", applicationId, questionId)
                        .param("mark_as_incomplete", String.valueOf(TRUE))
                        .param("name", String.valueOf(applicationDetailsForm.getName()))
                        .param("startDate", "startDate")
                        .param("startDate.year",  String.valueOf(applicationDetailsForm.getStartDate().getYear()))
                        .param("startDate.monthValue",  String.valueOf(applicationDetailsForm.getStartDate().getMonthValue()))
                        .param("startDate.dayOfMonth",  String.valueOf(applicationDetailsForm.getStartDate().getDayOfMonth()))
                        .param("durationInMonths", String.valueOf(applicationDetailsForm.getDurationInMonths()))
                        .param("resubmission", String.valueOf(applicationDetailsForm.getResubmission()))
        )
                .andExpect(status().isOk())
                .andExpect(view().name("application/questions/application-details"))
                .andReturn();
    }

}
