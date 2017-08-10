package org.innovateuk.ifs.application.forms.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.applicant.builder.ApplicantSectionResourceBuilder;
import org.innovateuk.ifs.applicant.resource.ApplicantResource;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.finance.view.DefaultFinanceFormHandler;
import org.innovateuk.ifs.application.finance.viewmodel.ApplicationFinanceOverviewViewModel;
import org.innovateuk.ifs.application.finance.viewmodel.FinanceViewModel;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.form.Form;
import org.innovateuk.ifs.application.forms.populator.OrganisationDetailsViewModelPopulator;
import org.innovateuk.ifs.application.forms.populator.QuestionModelPopulator;
import org.innovateuk.ifs.application.forms.saver.ApplicationQuestionSaver;
import org.innovateuk.ifs.application.forms.service.ApplicationRedirectionService;
import org.innovateuk.ifs.application.overheads.OverheadFileSaver;
import org.innovateuk.ifs.application.populator.*;
import org.innovateuk.ifs.application.populator.forminput.FormInputViewModelGenerator;
import org.innovateuk.ifs.application.populator.section.YourFinancesSectionPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.resource.SectionType;
import org.innovateuk.ifs.application.viewmodel.section.YourFinancesSectionViewModel;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.applicant.builder.ApplicantQuestionResourceBuilder.newApplicantQuestionResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantResourceBuilder.newApplicantResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantSectionResourceBuilder.newApplicantSectionResource;
import static org.innovateuk.ifs.application.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.*;
import static org.innovateuk.ifs.application.service.Futures.settable;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.rest.ValidationMessages.noErrors;
import static org.innovateuk.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class ApplicationQuestionControllerTest extends BaseControllerMockMVCTest<ApplicationQuestionController> {

    @Spy
    @InjectMocks
    private QuestionModelPopulator questionModelPopulator;

    @Spy
    @InjectMocks
    private OpenSectionModelPopulator openSectionModel;

    @Spy
    @InjectMocks
    private OpenApplicationFinanceSectionModelPopulator openFinanceSectionModel;

    @Spy
    @InjectMocks
    private OrganisationDetailsViewModelPopulator organisationDetailsViewModelPopulator;

    @Mock
    private ApplicationModelPopulator applicationModelPopulator;

    @Mock
    private ApplicationSectionAndQuestionModelPopulator applicationSectionAndQuestionModelPopulator;

    @Mock
    private OverheadFileSaver overheadFileSaver;

    @Mock
    private DefaultFinanceFormHandler defaultFinanceFormHandler;
    @Mock
    private FormInputViewModelGenerator formInputViewModelGenerator;
    @Mock
    private YourFinancesSectionPopulator yourFinancesSectionPopulator;

    @Mock
    private ApplicationNavigationPopulator applicationNavigationPopulator;

    @Mock
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    @Mock
    private ApplicantRestService applicantRestService;

    @Spy
    @InjectMocks
    private ApplicationRedirectionService applicationRedirectionService;

    @Mock
    private ApplicationQuestionSaver applicationSaver;

    private ApplicationResource application;
    private Long sectionId;
    private Long questionId;
    private Long formInputId;
    private Long costId;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yy");
    private ApplicantSectionResourceBuilder sectionBuilder;

    @Override
    protected ApplicationQuestionController supplyControllerUnderTest() {
        return new ApplicationQuestionController();
    }

    @Before
    @Override
    public void setUp() {

        // Process mock annotations
        super.setUp();

        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.setupApplicationResponses();
        this.loginDefaultUser();
        this.setupUserRoles();
        this.setupFinances();
        this.setupInvites();
        this.setupQuestionStatus(applications.get(0));

        application = applications.get(0);
        sectionId = 1L;
        questionId = 1L;
        formInputId = 111L;
        costId = 1L;

        // save actions should always succeed.
        when(formInputResponseRestService.saveQuestionResponse(anyLong(), anyLong(), anyLong(), eq(""), anyBoolean())).thenReturn(restSuccess(new ValidationMessages(fieldError("value", "", "Please enter some text 123"))));
        when(formInputResponseRestService.saveQuestionResponse(anyLong(), anyLong(), anyLong(), anyString(), anyBoolean())).thenReturn(restSuccess(noErrors()));
        when(organisationService.getOrganisationById(anyLong())).thenReturn(organisations.get(0));
        when(overheadFileSaver.handleOverheadFileRequest(any())).thenReturn(noErrors());
        when(financeHandler.getFinanceFormHandler(any())).thenReturn(defaultFinanceFormHandler);

        ApplicantResource applicant = newApplicantResource().withProcessRole(processRoles.get(0)).withOrganisation(organisations.get(0)).build();
        when(applicantRestService.getQuestion(anyLong(), anyLong(), anyLong())).thenReturn(newApplicantQuestionResource().withApplication(application).withCompetition(competitionResource).withCurrentApplicant(applicant).withApplicants(asList(applicant)).withQuestion(questionResources.values().iterator().next()).withCurrentUser(loggedInUser).build());
        sectionBuilder =  newApplicantSectionResource().withApplication(application).withCompetition(competitionResource).withCurrentApplicant(applicant).withApplicants(asList(applicant)).withSection(newSectionResource().withType(SectionType.FINANCE).build()).withCurrentUser(loggedInUser);
        when(applicantRestService.getSection(anyLong(), anyLong(), anyLong())).thenReturn(sectionBuilder.build());
        when(formInputViewModelGenerator.fromQuestion(any(), any())).thenReturn(Collections.emptyList());
        when(formInputViewModelGenerator.fromSection(any(), any(), any(), any())).thenReturn(Collections.emptyList());
        when(yourFinancesSectionPopulator.populate(any(), any(), any(), any(), any(), any(), any())).thenReturn(new YourFinancesSectionViewModel(null, null, null, false, Optional.empty(), false));

        ApplicationFinanceOverviewViewModel financeOverviewViewModel = new ApplicationFinanceOverviewViewModel();
        when(applicationFinanceOverviewModelManager.getFinanceDetailsViewModel(competitionResource.getId(), application.getId())).thenReturn(financeOverviewViewModel);

        FinanceViewModel financeViewModel = new FinanceViewModel();
        financeViewModel.setOrganisationGrantClaimPercentage(76);

        when(defaultFinanceModelManager.getFinanceViewModel(anyLong(), anyList(), anyLong(), any(Form.class), anyLong())).thenReturn(financeViewModel);
        when(applicationSaver.saveApplicationForm(anyLong(), any(ApplicationForm.class), anyLong(), anyLong(), any(HttpServletRequest.class), any(HttpServletResponse.class), anyBoolean(), any(Optional.class)))
                .thenReturn(new ValidationMessages());
    }

    @Test
    public void testQuestionPage() throws Exception {
        ApplicationResource application = applications.get(0);

        when(sectionService.getAllByCompetitionId(anyLong())).thenReturn(sectionResources);
        when(applicationService.getById(application.getId())).thenReturn(application);
        when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));

        // just check if these pages are not throwing errors.
        mockMvc.perform(get("/application/1/form/question/10")).andExpect(status().isOk());
        mockMvc.perform(get("/application/1/form/question/21")).andExpect(status().isOk());
        mockMvc.perform(get("/application/1/form/question/edit/1")).andExpect(status().isOk());
        mockMvc.perform(get("/application/1/form/question/edit/1?mark_as_complete=false")).andExpect(status().isOk());
        mockMvc.perform(get("/application/1/form/question/edit/1?mark_as_complete=")).andExpect(status().isOk());

        verify(applicationSaver, never()).saveApplicationForm(anyLong(), any(ApplicationForm.class), anyLong(), anyLong(), any(HttpServletRequest.class), any(HttpServletResponse.class), anyBoolean(), any(Optional.class));

        mockMvc.perform(get("/application/1/form/question/edit/1?mark_as_complete=true")).andExpect(status().isOk());

        verify(applicationSaver, times(1)).saveApplicationForm(anyLong(), any(ApplicationForm.class), anyLong(), anyLong(), any(HttpServletRequest.class), any(HttpServletResponse.class), anyBoolean(), any(Optional.class));

        mockMvc.perform(get("/application/1/form/question/edit/21")).andExpect(status().isOk());
    }

    @Test
    public void testQuestionSubmit() throws Exception {
        ApplicationResource application = applications.get(0);

        when(applicationService.getById(application.getId())).thenReturn(application);
        mockMvc.perform(
                post("/application/1/form/question/1")
                        .param("formInput[1]", "Some Value...")

        )
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void testQuestionSubmitEdit() throws Exception {
        ApplicationResource application = applications.get(0);

        when(applicationService.getById(application.getId())).thenReturn(application);
        mockMvc.perform(
                post("/application/1/form/question/1")
                        .param(EDIT_QUESTION, "1_2")
        )
                .andExpect(view().name("application-form"));
        verify(applicationNavigationPopulator).addAppropriateBackURLToModel(any(Long.class), any(Model.class), any(SectionResource.class), any(Optional.class));
    }

    @Test
    public void testQuestionSubmitAssign() throws Exception {
        ApplicationResource application = applications.get(0);

        when(applicationService.getById(application.getId())).thenReturn(application);
        mockMvc.perform(
                post("/application/1/form/question/1")
                        .param(ASSIGN_QUESTION_PARAM, "1_2")

        )
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void testQuestionSubmitMarkAsCompleteQuestion() throws Exception {
        ApplicationResource application = applications.get(0);

        when(applicationService.getById(application.getId())).thenReturn(application);
        mockMvc.perform(
                post("/application/1/form/question/1")
                        .param(MARK_AS_COMPLETE, "1")
        ).andExpect(status().is3xxRedirection());
    }

    @Test
    public void testQuestionSubmitSaveElement() throws Exception {
        ApplicationResource application = applications.get(0);

        when(applicationService.getById(application.getId())).thenReturn(application);

        mockMvc.perform(post("/application/1/form/question/1"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void testApplicationDetailsFormSubmitMarkAsComplete_returnsErrorsWithEmptyFields() throws Exception {
        FormInputResource resource = newFormInputResource().withId(1L).withType(FormInputType.APPLICATION_DETAILS).build();
        when(formInputRestService.getByQuestionIdAndScope(questionId, FormInputScope.APPLICATION)).thenReturn(restSuccess(Collections.singletonList(resource)));
        MvcResult result = mockMvc.perform(
                post("/application/{applicationId}/form/question/{questionId}", application.getId(), questionId)
                        .param("mark_as_complete", questionId.toString())
                        .param("application.name", "")
                        .param("application.resubmission", "")
                        .param("application.startDate", "")
                        .param("application.startDate.year", "")
                        .param("application.startDate.dayOfMonth", "")
                        .param("application.startDate.monthValue", "")
        ).andReturn();

        BindingResult bindingResult = (BindingResult) result.getModelAndView().getModel().get("org.springframework.validation.BindingResult.form");

        assertEquals("NotBlank", bindingResult.getFieldError("application.name").getCode());
        assertEquals("NotNull", bindingResult.getFieldError("application.durationInMonths").getCode());
        assertEquals("FutureLocalDate", bindingResult.getFieldError("application.startDate").getCode());
        assertEquals("NotNull", bindingResult.getFieldError("application.resubmission").getCode());

        verify(applicationNavigationPopulator).addAppropriateBackURLToModel(any(Long.class), any(Model.class), any(SectionResource.class), any(Optional.class));
    }

    @Test
    public void testApplicationDetailsFormSubmitMarkAsComplete_returnsErrorsWithResubmissionSelected() throws Exception {
        FormInputResource resource = newFormInputResource().withId(1L).withType(FormInputType.APPLICATION_DETAILS).build();
        when(formInputRestService.getByQuestionIdAndScope(questionId, FormInputScope.APPLICATION)).thenReturn(restSuccess(Collections.singletonList(resource)));
        LocalDate yesterday = LocalDate.now().minusDays(1L);

        MvcResult result = mockMvc.perform(
                post("/application/{applicationId}/form/question/{questionId}", application.getId(), questionId)
                        .param("mark_as_complete", questionId.toString())
                        .param("application.resubmission", "1")
                        .param("application.previousApplicationNumber", "")
                        .param("application.previousApplicationTitle", "")
        ).andReturn();

        BindingResult bindingResult = (BindingResult) result.getModelAndView().getModel().get("org.springframework.validation.BindingResult.form");

        assertEquals("FieldRequiredIf", bindingResult.getFieldError("application.previousApplicationNumber").getCode());
        assertEquals("FieldRequiredIf", bindingResult.getFieldError("application.previousApplicationTitle").getCode());

        verify(applicationNavigationPopulator).addAppropriateBackURLToModel(any(Long.class), any(Model.class), any(SectionResource.class), any(Optional.class));
    }

    @Test
    public void testApplicationDetailsFormSubmitMarkAsComplete_returnsErrorsForPastDate() throws Exception {
        FormInputResource resource = newFormInputResource().withId(1L).withType(FormInputType.APPLICATION_DETAILS).build();
        when(formInputRestService.getByQuestionIdAndScope(questionId, FormInputScope.APPLICATION)).thenReturn(restSuccess(Collections.singletonList(resource)));
        LocalDate yesterday = LocalDate.now().minusDays(1L);

        MvcResult result = mockMvc.perform(
                post("/application/{applicationId}/form/question/{questionId}", application.getId(), questionId)
                        .param("mark_as_complete", questionId.toString())
                        .param("application.startDate", "")
                        .param("application.startDate.year", String.valueOf(yesterday.getDayOfYear()))
                        .param("application.startDate.dayOfMonth", String.valueOf(yesterday.getDayOfMonth()))
                        .param("application.startDate.monthValue", String.valueOf(yesterday.getMonthValue()))
        ).andReturn();

        BindingResult bindingResult = (BindingResult) result.getModelAndView().getModel().get("org.springframework.validation.BindingResult.form");
        assertEquals("FutureLocalDate", bindingResult.getFieldError("application.startDate").getCode());
        verify(applicationNavigationPopulator).addAppropriateBackURLToModel(any(Long.class), any(Model.class), any(SectionResource.class), any(Optional.class));
    }

    @Test
    public void testApplicationDetailsFormSubmitMarkAsComplete_returnsErrorForTooFewMonths() throws Exception {
        FormInputResource resource = newFormInputResource().withId(1L).withType(FormInputType.APPLICATION_DETAILS).build();
        when(formInputRestService.getByQuestionIdAndScope(questionId, FormInputScope.APPLICATION)).thenReturn(restSuccess(Collections.singletonList(resource)));
        MvcResult result = mockMvc.perform(
                post("/application/{applicationId}/form/question/{questionId}", application.getId(), questionId)
                        .param("mark_as_complete", questionId.toString())
                        .param("application.durationInMonths", "0")
        ).andReturn();

        BindingResult bindingResult = (BindingResult) result.getModelAndView().getModel().get("org.springframework.validation.BindingResult.form");
        assertEquals("Min", bindingResult.getFieldError("application.durationInMonths").getCode());
        verify(applicationNavigationPopulator).addAppropriateBackURLToModel(any(Long.class), any(Model.class), any(SectionResource.class), any(Optional.class));
    }

    @Test
    public void testApplicationDetailsFormSubmitMarkAsComplete_returnsErrorForTooManyMonths() throws Exception {
        FormInputResource resource = newFormInputResource().withId(1L).withType(FormInputType.APPLICATION_DETAILS).build();
        when(formInputRestService.getByQuestionIdAndScope(questionId, FormInputScope.APPLICATION)).thenReturn(restSuccess(Collections.singletonList(resource)));
        MvcResult result = mockMvc.perform(
                post("/application/{applicationId}/form/question/{questionId}", application.getId(), questionId)
                        .param("mark_as_complete", questionId.toString())
                        .param("application.durationInMonths", "37")
        ).andReturn();

        BindingResult bindingResult = (BindingResult) result.getModelAndView().getModel().get("org.springframework.validation.BindingResult.form");
        assertEquals("Max", bindingResult.getFieldError("application.durationInMonths").getCode());
        verify(applicationNavigationPopulator).addAppropriateBackURLToModel(any(Long.class), any(Model.class), any(SectionResource.class), any(Optional.class));
    }

    @Test
    public void testApplicationDetailsFormSubmitMarkAsComplete_returnsErrorForNoResearchCategorySelected() throws Exception {
        MvcResult result = mockMvc.perform(
                post("/application/{applicationId}/form/question/{questionId}", application.getId(), questionId)
                        .param("mark_as_complete", questionId.toString())
                        .param("application.name", "random")
        ).andReturn();

        BindingResult bindingResult = (BindingResult) result.getModelAndView().getModel().get("org.springframework.validation.BindingResult.form");
        assertEquals("NotNull", bindingResult.getFieldError("application.researchCategory").getCode());
        verify(applicationNavigationPopulator).addAppropriateBackURLToModel(any(Long.class), any(Model.class), any(SectionResource.class), any(Optional.class));
    }

    @Test
    public void testApplicationDetailsFormSubmitMarkAsComplete_returnsErrorForNoInnovationAreaSelected() throws Exception {
        MvcResult result = mockMvc.perform(
                post("/application/{applicationId}/form/question/{questionId}", application.getId(), questionId)
                        .param("mark_as_complete", questionId.toString())
                        .param("application.name", "random")
        ).andReturn();

        BindingResult bindingResult = (BindingResult) result.getModelAndView().getModel().get("org.springframework.validation.BindingResult.form");
        assertEquals("NotNull", bindingResult.getFieldError("application.innovationArea").getCode());
        verify(applicationNavigationPopulator).addAppropriateBackURLToModel(any(Long.class), any(Model.class), any(SectionResource.class), any(Optional.class));
    }

    @Test
    public void testApplicationDetailsFormSubmitMarkAsComplete_returnsErrorsWithInvalidValues() throws Exception {
        FormInputResource resource = newFormInputResource().withId(1L).withType(FormInputType.APPLICATION_DETAILS).build();
        when(formInputRestService.getByQuestionIdAndScope(questionId, FormInputScope.APPLICATION)).thenReturn(restSuccess(Collections.singletonList(resource)));
        LocalDate yesterday = LocalDate.now().minusDays(1L);

        MvcResult result = mockMvc.perform(
                post("/application/{applicationId}/form/question/{questionId}", application.getId(), questionId)
                        .param("mark_as_complete", questionId.toString())
                        .param("application.resubmission", "0")
                        .param("application.previousApplicationNumber", "")
                        .param("application.previousApplicationTitle", "")
        ).andReturn();

        BindingResult bindingResult = (BindingResult) result.getModelAndView().getModel().get("org.springframework.validation.BindingResult.form");

        assertNull(bindingResult.getFieldError("application.previousApplicationNumber"));
        assertNull(bindingResult.getFieldError("application.previousApplicationTitle"));
        verify(applicationNavigationPopulator).addAppropriateBackURLToModel(any(Long.class), any(Model.class), any(SectionResource.class), any(Optional.class));
    }

    @Test
    public void applicationDetailsFormSubmit_incorrectFileType() throws Exception {
        long formInputId = 2L;
        String fileError = "file error";
        MockMultipartFile file = new MockMultipartFile("formInput[" + formInputId +"]", "filename.txt", "text/plain", "someText".getBytes());

        long fileQuestionId = 31L;
        ValidationMessages validationMessages = new ValidationMessages();
        validationMessages.addError(fieldError("formInput[" + formInputId + "]", new Error(fileError, UNSUPPORTED_MEDIA_TYPE)));
        when(applicationSaver.saveApplicationForm(anyLong(), any(ApplicationForm.class), anyLong(), anyLong(), any(HttpServletRequest.class), any(HttpServletResponse.class), anyBoolean(), any(Optional.class)))
                .thenReturn(validationMessages);

        MvcResult result = mockMvc.perform(
            fileUpload("/application/{applicationId}/form/question/{questionId}", application.getId(), fileQuestionId)
                        .file(file)
                        .param("upload_file", "")
        ).andReturn();

        BindingResult bindingResult = (BindingResult) result.getModelAndView().getModel().get("org.springframework.validation.BindingResult.form");
        assertEquals(fileError, bindingResult.getFieldError("formInput[" + formInputId + "]").getCode());
    }
}
