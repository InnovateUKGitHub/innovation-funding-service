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
import org.innovateuk.ifs.application.viewmodel.forminput.ApplicationDetailsInputViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.junit.Test;
import org.mockito.Mock;

import java.time.LocalDate;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.resource.CompanyAge.ESTABLISHED_1_TO_5_YEARS;
import static org.innovateuk.ifs.application.resource.CompanyPrimaryFocus.AEROSPACE_AND_DEFENCE;
import static org.innovateuk.ifs.application.resource.CompetitionReferralSource.BUSINESS_CONTACT;
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

    @Mock
    private CompetitionRestService competitionRestService;

    @Override
    protected ApplicationDetailsController supplyControllerUnderTest() {
        return new ApplicationDetailsController(
                applicationDetailsViewModelPopulator,
                questionStatusRestService,
                userRestService,
                applicantRestService,
                applicationNavigationPopulator,
                applicationService,
                competitionRestService
        );
    }

    @Test
    public void viewApplicationDetails() throws Exception {
        long questionId = 1L;
        long applicationId = 2L;
        long competitionId = 3L;

        ApplicationDetailsViewModel viewModel = mock(ApplicationDetailsViewModel.class);
        ApplicantQuestionResource applicantQuestionResource = mock(ApplicantQuestionResource.class);
        when(applicantRestService.getQuestion(anyLong(), anyLong(), anyLong())).thenReturn(applicantQuestionResource);
        when(viewModel.getApplication()).thenReturn(newApplicationResource().build());
        when(applicationDetailsViewModelPopulator.populate(any(ApplicantQuestionResource.class), any(CompetitionResource.class))).thenReturn(viewModel);
        ApplicationDetailsInputViewModel applicationDetailsInputViewModel = mock(ApplicationDetailsInputViewModel.class);
        when(applicationDetailsInputViewModel.getSelectedInnovationAreaName()).thenReturn(null);
        when(applicationDetailsInputViewModel.isCanSelectInnovationArea()).thenReturn(false);
        when(applicationDetailsInputViewModel.getInnovationAreaText()).thenReturn(null);
        when(viewModel.getFormInputViewModel()).thenReturn(applicationDetailsInputViewModel);
        when(applicationService.getById(anyLong())).thenReturn(newApplicationResource().withCompetition(competitionId).build());
        CompetitionResource competitionResource = mock(CompetitionResource.class);
        when(competitionRestService.getCompetitionById(anyLong())).thenReturn(restSuccess(competitionResource));

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
        applicationDetailsForm.setCompetitionReferralSource(BUSINESS_CONTACT.toString());
        applicationDetailsForm.setCompanyAge(ESTABLISHED_1_TO_5_YEARS.toString());
        applicationDetailsForm.setCompanyPrimaryFocus(AEROSPACE_AND_DEFENCE.toString());

        ApplicationDetailsViewModel viewModel = mock(ApplicationDetailsViewModel.class);
        ApplicantQuestionResource applicantQuestionResource = mock(ApplicantQuestionResource.class);
        when(applicantRestService.getQuestion(anyLong(), anyLong(), anyLong())).thenReturn(applicantQuestionResource);
        when(viewModel.getApplication()).thenReturn(newApplicationResource().build());
        when(applicationDetailsViewModelPopulator.populate(any(ApplicantQuestionResource.class), any(CompetitionResource.class))).thenReturn(viewModel);
        when(applicationService.getById(anyLong())).thenReturn(newApplicationResource().build());
        when(applicationService.save(any(ApplicationResource.class))).thenReturn(null);

        mockMvc.perform(
                post("/application/{applicationId}/form/question/{questionId}/application-details", applicationId, questionId)
                        .param("name", valueOf(applicationDetailsForm.getName()))
                        .param("startDate", "startDate")
                        .param("startDate.year",  valueOf(applicationDetailsForm.getStartDate().getYear()))
                        .param("startDate.monthValue",  valueOf(applicationDetailsForm.getStartDate().getMonthValue()))
                        .param("startDate.dayOfMonth",  valueOf(applicationDetailsForm.getStartDate().getDayOfMonth()))
                        .param("durationInMonths", valueOf(applicationDetailsForm.getDurationInMonths()))
                        .param("resubmission", valueOf(applicationDetailsForm.getResubmission()))
                        .param("competitionReferralSource", valueOf(applicationDetailsForm.getCompetitionReferralSource()))
                        .param("companyAge", valueOf(applicationDetailsForm.getCompanyAge()))
                        .param("companyPrimaryFocus", valueOf(applicationDetailsForm.getCompanyPrimaryFocus()))
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/application/%d", applicationId)))
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
        when(applicationDetailsViewModelPopulator.populate(any(ApplicantQuestionResource.class), any(CompetitionResource.class))).thenReturn(viewModel);
        when(applicationService.getById(anyLong())).thenReturn(newApplicationResource().build());
        when(applicationService.save(any(ApplicationResource.class))).thenReturn(null);

        mockMvc.perform(
                post("/application/{applicationId}/form/question/{questionId}/application-details", applicationId, questionId)
                        .param("name", valueOf(invalidForm.getName()))
                        .param("startDate",  valueOf(invalidForm.getStartDate()))
                        .param("durationInMonths", valueOf(invalidForm.getDurationInMonths()))
                        .param("resubmission", valueOf(invalidForm.getResubmission()))
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(format("redirect:/application/%d", applicationId)))
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
        when(applicationDetailsViewModelPopulator.populate(any(ApplicantQuestionResource.class), any(CompetitionResource.class))).thenReturn(viewModel);
        when(applicationService.getById(anyLong())).thenReturn(newApplicationResource().build());
        when(applicationService.save(any(ApplicationResource.class))).thenReturn(null);
        when(userRestService.findProcessRole(anyLong(), anyLong())).thenReturn(restSuccess(newProcessRoleResource().build()));
        when(questionStatusRestService.markAsComplete(anyLong(), anyLong(), anyLong())).thenReturn(restSuccess(emptyList()));

        mockMvc.perform(
                post("/application/{applicationId}/form/question/{questionId}/application-details", applicationId, questionId)
                        .param("mark_as_complete", valueOf(TRUE))
                        .param("name", valueOf(applicationDetailsForm.getName()))
                        .param("startDate", "startDate")
                        .param("startDate.year",  valueOf(applicationDetailsForm.getStartDate().getYear()))
                        .param("startDate.monthValue",  valueOf(applicationDetailsForm.getStartDate().getMonthValue()))
                        .param("startDate.dayOfMonth",  valueOf(applicationDetailsForm.getStartDate().getDayOfMonth()))
                        .param("durationInMonths", valueOf(applicationDetailsForm.getDurationInMonths()))
                        .param("resubmission", valueOf(applicationDetailsForm.getResubmission()))
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/application/%d/form/question/%d/application-details", applicationId, questionId)))
                .andReturn();
    }

    @Test
    public void saveAndReturnWithInvalidEnums() throws Exception {
        long questionId = 1L;
        long applicationId = 2L;
        ApplicationDetailsForm applicationDetailsForm = new ApplicationDetailsForm();
        applicationDetailsForm.setName("name");
        applicationDetailsForm.setResubmission(FALSE);
        applicationDetailsForm.setStartDate(LocalDate.now().plusYears(1));
        applicationDetailsForm.setDurationInMonths(3L);
        applicationDetailsForm.setCompetitionReferralSource(BUSINESS_CONTACT.toString() + "_invalid");
        applicationDetailsForm.setCompanyAge(ESTABLISHED_1_TO_5_YEARS.toString() + "_invalid");
        applicationDetailsForm.setCompanyPrimaryFocus(AEROSPACE_AND_DEFENCE.toString() + "_invalid");

        ApplicationDetailsViewModel viewModel = mock(ApplicationDetailsViewModel.class);
        ApplicantQuestionResource applicantQuestionResource = mock(ApplicantQuestionResource.class);
        when(applicantRestService.getQuestion(anyLong(), anyLong(), anyLong())).thenReturn(applicantQuestionResource);
        when(viewModel.getApplication()).thenReturn(newApplicationResource().build());
        when(applicationDetailsViewModelPopulator.populate(any(ApplicantQuestionResource.class), any(CompetitionResource.class))).thenReturn(viewModel);
        when(applicationService.getById(anyLong())).thenReturn(newApplicationResource().build());
        when(applicationService.save(any(ApplicationResource.class))).thenReturn(null);

        mockMvc.perform(
                post("/application/{applicationId}/form/question/{questionId}/application-details", applicationId, questionId)
                        .param("name", valueOf(applicationDetailsForm.getName()))
                        .param("startDate", "startDate")
                        .param("startDate.year",  valueOf(applicationDetailsForm.getStartDate().getYear()))
                        .param("startDate.monthValue",  valueOf(applicationDetailsForm.getStartDate().getMonthValue()))
                        .param("startDate.dayOfMonth",  valueOf(applicationDetailsForm.getStartDate().getDayOfMonth()))
                        .param("durationInMonths", valueOf(applicationDetailsForm.getDurationInMonths()))
                        .param("resubmission", valueOf(applicationDetailsForm.getResubmission()))
                        .param("competitionReferralSource", valueOf(applicationDetailsForm.getCompetitionReferralSource()))
                        .param("companyAge", valueOf(applicationDetailsForm.getCompanyAge()))
                        .param("companyPrimaryFocus", valueOf(applicationDetailsForm.getCompanyPrimaryFocus()))
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(format("redirect:/application/%d", applicationId)))
                .andReturn();
    }

    @Test
    public void markAsCompleteInvalidForm() throws Exception {
        long questionId = 1L;
        long applicationId = 2L;
        long competitionId = 3L;
        ApplicationDetailsForm applicationDetailsForm = new ApplicationDetailsForm();
        applicationDetailsForm.setName("");
        applicationDetailsForm.setResubmission(FALSE);
        applicationDetailsForm.setStartDate(LocalDate.now().plusYears(1));
        applicationDetailsForm.setDurationInMonths(3L);

        ApplicationDetailsViewModel viewModel = mock(ApplicationDetailsViewModel.class);
        ApplicantQuestionResource applicantQuestionResource = mock(ApplicantQuestionResource.class);
        when(applicantRestService.getQuestion(anyLong(), anyLong(), anyLong())).thenReturn(applicantQuestionResource);
        when(viewModel.getApplication()).thenReturn(newApplicationResource().build());
        ApplicationDetailsInputViewModel applicationDetailsInputViewModel = mock(ApplicationDetailsInputViewModel.class);
        when(applicationDetailsInputViewModel.getSelectedInnovationAreaName()).thenReturn(null);
        when(applicationDetailsInputViewModel.isCanSelectInnovationArea()).thenReturn(false);
        when(applicationDetailsInputViewModel.getInnovationAreaText()).thenReturn(null);
        when(viewModel.getFormInputViewModel()).thenReturn(applicationDetailsInputViewModel);
        when(applicationDetailsViewModelPopulator.populate(any(ApplicantQuestionResource.class), any(CompetitionResource.class))).thenReturn(viewModel);
        when(applicationService.getById(anyLong())).thenReturn(newApplicationResource().withCompetition(competitionId).build());
        CompetitionResource competitionResource = mock(CompetitionResource.class);
        when(competitionRestService.getCompetitionById(anyLong())).thenReturn(restSuccess(competitionResource));

        mockMvc.perform(
                post("/application/{applicationId}/form/question/{questionId}/application-details", applicationId, questionId)
                        .param("mark_as_complete", valueOf(TRUE))
                        .param("name", valueOf(applicationDetailsForm.getName()))
                        .param("startDate", "startDate")
                        .param("startDate.year",  valueOf(applicationDetailsForm.getStartDate().getYear()))
                        .param("startDate.monthValue",  valueOf(applicationDetailsForm.getStartDate().getMonthValue()))
                        .param("startDate.dayOfMonth",  valueOf(applicationDetailsForm.getStartDate().getDayOfMonth()))
                        .param("durationInMonths", valueOf(applicationDetailsForm.getDurationInMonths()))
                        .param("resubmission", valueOf(applicationDetailsForm.getResubmission()))
        )
                .andExpect(status().isOk())
                .andExpect(view().name("application/questions/application-details"))
                .andReturn();
    }

    @Test
    public void markAsIncomplete() throws Exception {
        long questionId = 1L;
        long applicationId = 2L;
        long competitionId = 3L;
        ApplicationDetailsForm applicationDetailsForm = new ApplicationDetailsForm();
        applicationDetailsForm.setName("name");
        applicationDetailsForm.setResubmission(FALSE);
        applicationDetailsForm.setStartDate(LocalDate.now().plusYears(1));
        applicationDetailsForm.setDurationInMonths(3L);

        ApplicationDetailsViewModel viewModel = mock(ApplicationDetailsViewModel.class);
        ApplicantQuestionResource applicantQuestionResource = mock(ApplicantQuestionResource.class);
        when(applicantRestService.getQuestion(anyLong(), anyLong(), anyLong())).thenReturn(applicantQuestionResource);
        when(viewModel.getApplication()).thenReturn(newApplicationResource().build());
        when(applicationDetailsViewModelPopulator.populate(any(ApplicantQuestionResource.class), any(CompetitionResource.class))).thenReturn(viewModel);
        when(userRestService.findProcessRole(anyLong(), anyLong())).thenReturn(restSuccess(newProcessRoleResource().build()));
        when(questionStatusRestService.markAsInComplete(anyLong(), anyLong(), anyLong())).thenReturn(restSuccess());
        ApplicationDetailsInputViewModel applicationDetailsInputViewModel = mock(ApplicationDetailsInputViewModel.class);
        when(applicationDetailsInputViewModel.getSelectedInnovationAreaName()).thenReturn(null);
        when(applicationDetailsInputViewModel.isCanSelectInnovationArea()).thenReturn(false);
        when(applicationDetailsInputViewModel.getInnovationAreaText()).thenReturn(null);
        when(viewModel.getFormInputViewModel()).thenReturn(applicationDetailsInputViewModel);
        when(applicationService.getById(anyLong())).thenReturn(newApplicationResource().withCompetition(competitionId).build());
        CompetitionResource competitionResource = mock(CompetitionResource.class);
        when(competitionRestService.getCompetitionById(anyLong())).thenReturn(restSuccess(competitionResource));

        mockMvc.perform(
                post("/application/{applicationId}/form/question/{questionId}/application-details", applicationId, questionId)
                        .param("mark_as_incomplete", valueOf(TRUE))
                        .param("name", valueOf(applicationDetailsForm.getName()))
                        .param("startDate", "startDate")
                        .param("startDate.year",  valueOf(applicationDetailsForm.getStartDate().getYear()))
                        .param("startDate.monthValue",  valueOf(applicationDetailsForm.getStartDate().getMonthValue()))
                        .param("startDate.dayOfMonth",  valueOf(applicationDetailsForm.getStartDate().getDayOfMonth()))
                        .param("durationInMonths", valueOf(applicationDetailsForm.getDurationInMonths()))
                        .param("resubmission", valueOf(applicationDetailsForm.getResubmission()))
        )
                .andExpect(status().isOk())
                .andExpect(view().name("application/questions/application-details"))
                .andReturn();
    }

}
