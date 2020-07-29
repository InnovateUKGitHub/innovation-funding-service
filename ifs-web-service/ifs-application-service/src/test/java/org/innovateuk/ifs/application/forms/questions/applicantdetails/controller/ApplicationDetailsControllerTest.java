package org.innovateuk.ifs.application.forms.questions.applicantdetails.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.forms.questions.applicationdetails.controller.ApplicationDetailsController;
import org.innovateuk.ifs.application.forms.questions.applicationdetails.form.ApplicationDetailsForm;
import org.innovateuk.ifs.application.forms.questions.applicationdetails.model.ApplicationDetailsViewModel;
import org.innovateuk.ifs.application.forms.questions.applicationdetails.populator.ApplicationDetailsViewModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.validation.Validator;

import java.time.LocalDate;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.resource.CompanyAge.ESTABLISHED_1_TO_5_YEARS;
import static org.innovateuk.ifs.application.resource.CompanyPrimaryFocus.AEROSPACE_AND_DEFENCE;
import static org.innovateuk.ifs.application.resource.CompetitionReferralSource.BUSINESS_CONTACT;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ApplicationDetailsControllerTest extends BaseControllerMockMVCTest<ApplicationDetailsController> {

    @Mock
    private ApplicationDetailsViewModelPopulator applicationDetailsViewModelPopulator;
    @Mock
    private QuestionStatusRestService questionStatusRestService;
    @Mock
    private UserRestService userRestService;
    @Mock
    private ApplicationRestService applicationRestService;
    @Mock
    private CompetitionRestService competitionRestService;
    @Mock
    private Validator validator;

    @Override
    protected ApplicationDetailsController supplyControllerUnderTest() {
        return new ApplicationDetailsController();
    }

    @Test
    public void viewApplicationDetails() throws Exception {
        long questionId = 1L;
        long applicationId = 2L;

        ApplicationResource application = newApplicationResource().build();
        ApplicationDetailsViewModel viewModel = mock(ApplicationDetailsViewModel.class);
        when(applicationRestService.getApplicationById(applicationId)).thenReturn(restSuccess(application));
        when(applicationDetailsViewModelPopulator.populate(application, questionId, getLoggedInUser())).thenReturn(viewModel);

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
        applicationDetailsForm.setCompetitionReferralSource(BUSINESS_CONTACT);
        applicationDetailsForm.setCompanyAge(ESTABLISHED_1_TO_5_YEARS);
        applicationDetailsForm.setCompanyPrimaryFocus(AEROSPACE_AND_DEFENCE);
        applicationDetailsForm.setKtpCompetition(false);

        ApplicationResource application = newApplicationResource().build();
        when(applicationRestService.getApplicationById(applicationId)).thenReturn(restSuccess(application));
        when(applicationRestService.saveApplication(any(ApplicationResource.class))).thenReturn(restSuccess(ValidationMessages.noErrors()));

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
                        .param("ktpCompetition", valueOf(applicationDetailsForm.isKtpCompetition()))
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/application/%d", applicationId)))
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
        applicationDetailsForm.setCompetitionReferralSource(BUSINESS_CONTACT);
        applicationDetailsForm.setCompanyAge(ESTABLISHED_1_TO_5_YEARS);
        applicationDetailsForm.setCompanyPrimaryFocus(AEROSPACE_AND_DEFENCE);
        applicationDetailsForm.setKtpCompetition(false);

        CompetitionResource competition = newCompetitionResource()
                .withInnovationAreas(singleton(1L))
                .build();
        ApplicationResource application = newApplicationResource()
                .withCompetition(competition.getId())
                .build();
        ProcessRoleResource processRoleResource = newProcessRoleResource().build();
        when(applicationRestService.getApplicationById(applicationId)).thenReturn(restSuccess(application));
        when(applicationRestService.saveApplication(any(ApplicationResource.class))).thenReturn(restSuccess(ValidationMessages.noErrors()));
        when(competitionRestService.getCompetitionById(application.getCompetition())).thenReturn(restSuccess(competition));
        when(userRestService.findProcessRole(getLoggedInUser().getId(), applicationId)).thenReturn(restSuccess(processRoleResource));
        when(questionStatusRestService.markAsComplete(questionId, applicationId, processRoleResource.getId())).thenReturn(restSuccess(emptyList()));

        mockMvc.perform(
                post("/application/{applicationId}/form/question/{questionId}/application-details", applicationId, questionId)
                        .param("complete", valueOf(TRUE))
                        .param("name", valueOf(applicationDetailsForm.getName()))
                        .param("startDate", "startDate")
                        .param("startDate.year",  valueOf(applicationDetailsForm.getStartDate().getYear()))
                        .param("startDate.monthValue",  valueOf(applicationDetailsForm.getStartDate().getMonthValue()))
                        .param("startDate.dayOfMonth",  valueOf(applicationDetailsForm.getStartDate().getDayOfMonth()))
                        .param("durationInMonths", valueOf(applicationDetailsForm.getDurationInMonths()))
                        .param("resubmission", valueOf(applicationDetailsForm.getResubmission()))
                        .param("ktpCompetition", valueOf(applicationDetailsForm.isKtpCompetition()))
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/application/%d/form/question/%d/application-details", applicationId, questionId)))
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

        CompetitionResource competition = newCompetitionResource()
                .withInnovationAreas(CollectionFunctions.asLinkedSet(1L, 2L))
                .withFundingType(FundingType.PROCUREMENT)
                .build();
        ApplicationResource application = newApplicationResource()
                .withCompetition(competition.getId())
                .build();
        when(applicationRestService.getApplicationById(applicationId)).thenReturn(restSuccess(application));
        when(applicationRestService.saveApplication(any(ApplicationResource.class))).thenReturn(restSuccess(ValidationMessages.noErrors()));;
        when(competitionRestService.getCompetitionById(application.getCompetition())).thenReturn(restSuccess(competition));

        mockMvc.perform(
                post("/application/{applicationId}/form/question/{questionId}/application-details", applicationId, questionId)
                        .param("complete", valueOf(true))
                        .param("resubmission", valueOf(true))
        )
                .andExpect(status().isOk())
                .andExpect(view().name("application/questions/application-details"))
                .andExpect(model().errorCount(10))
                .andExpect(model().attributeHasFieldErrors("form",
                        "name", "startDate", "durationInMonths", "previousApplicationNumber",
                        "previousApplicationTitle", "competitionReferralSource", "companyAge", "companyPrimaryFocus", "innovationAreaErrorHolder"))
                .andReturn();
    }

    @Test
    public void markAsIncomplete() throws Exception {
        long questionId = 1L;
        long applicationId = 2L;

        ProcessRoleResource processRoleResource = newProcessRoleResource().build();
        when(userRestService.findProcessRole(getLoggedInUser().getId(), applicationId)).thenReturn(restSuccess(processRoleResource));
        when(questionStatusRestService.markAsInComplete(questionId, applicationId, processRoleResource.getId())).thenReturn(restSuccess());
        ApplicationResource application = newApplicationResource().build();
        ApplicationDetailsViewModel viewModel = mock(ApplicationDetailsViewModel.class);
        when(applicationRestService.getApplicationById(applicationId)).thenReturn(restSuccess(application));
        when(applicationDetailsViewModelPopulator.populate(application, questionId, getLoggedInUser())).thenReturn(viewModel);

        mockMvc.perform(
                post("/application/{applicationId}/form/question/{questionId}/application-details", applicationId, questionId)
                        .param("edit", valueOf(TRUE))
        )
                .andExpect(status().isOk())
                .andExpect(view().name("application/questions/application-details"))
                .andReturn();
    }

}
