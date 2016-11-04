package com.worth.ifs.assessment.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.builder.QuestionResourceBuilder;
import com.worth.ifs.application.finance.view.OrganisationFinanceOverview;
import com.worth.ifs.application.resource.AppendixResource;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.assessment.form.AssessmentOverviewForm;
import com.worth.ifs.assessment.model.AssessmentFinancesSummaryModelPopulator;
import com.worth.ifs.assessment.model.AssessmentOverviewModelPopulator;
import com.worth.ifs.assessment.model.RejectAssessmentModelPopulator;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.assessment.resource.AssessorFormInputResponseResource;
import com.worth.ifs.assessment.service.AssessmentService;
import com.worth.ifs.assessment.service.AssessorFormInputResponseService;
import com.worth.ifs.assessment.viewmodel.AssessmentOverviewRowViewModel;
import com.worth.ifs.assessment.viewmodel.RejectAssessmentViewModel;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.file.service.FileEntryRestService;
import com.worth.ifs.filter.CookieFlashMessageFilter;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.form.builder.FormInputResourceBuilder;
import com.worth.ifs.form.resource.FormInputResource;
import com.worth.ifs.form.resource.FormInputResponseResource;
import com.worth.ifs.form.service.FormInputRestService;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.OrganisationSize;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.collect.Sets.newHashSet;
import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.*;
import static com.worth.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static com.worth.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static com.worth.ifs.assessment.builder.AssessorFormInputResponseResourceBuilder.newAssessorFormInputResponseResource;
import static com.worth.ifs.commons.error.CommonFailureKeys.ASSESSMENT_REJECTION_FAILED;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static com.worth.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static com.worth.ifs.finance.builder.OrganisationFinanceOverviewBuilder.newOrganisationFinanceOverviewBuilder;
import static com.worth.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static com.worth.ifs.form.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static java.lang.String.format;
import static java.util.Collections.nCopies;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class AssessmentOverviewControllerTest extends BaseControllerMockMVCTest<AssessmentOverviewController> {

    @InjectMocks
    private AssessmentOverviewController assessmentOverviewController;

    @Mock
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    @Mock
    private AssessmentService assessmentService;

    @Mock
    private AssessorFormInputResponseService assessorFormInputResponseService;

    @Mock
    private FormInputRestService formInputRestService;

    @Mock
    private FileEntryRestService fileEntryRestService;

    @Spy
    @InjectMocks
    private AssessmentOverviewModelPopulator assessmentOverviewModelPopulator;

    @Spy
    @InjectMocks
    private AssessmentFinancesSummaryModelPopulator assessmentFinancesSummaryModelPopulator;

    @Spy
    @InjectMocks
    private RejectAssessmentModelPopulator rejectAssessmentModelPopulator;

    @Override
    protected AssessmentOverviewController supplyControllerUnderTest() {
        return new AssessmentOverviewController();
    }

    @Before
    public void setUp() {
        super.setUp();

        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.setupApplicationResponses();
        this.loginDefaultUser();
        this.setupFinances();
        this.setupInvites();
        //noinspection unchecked
        when(organisationService.getOrganisationForUser(isA(Long.class), isA(List.class))).thenReturn(Optional.ofNullable(organisations.get(0)));
    }

    @Test
    public void AssessmentDetails() throws Exception {
        String SCORE_INPUT_TYPE = "assessor_score";
        AssessmentResource assessment = newAssessmentResource().withId(1L).withApplication(1L).build();
        CompetitionResource competition = newCompetitionResource()
                .withId(1L)
                .withAssessmentStartDate(LocalDateTime.now().minusDays(2))
                .withAssessmentEndDate(LocalDateTime.now().plusDays(4))
                .build();

        List<AssessorFormInputResponseResource> assessorResponses = newAssessorFormInputResponseResource()
                .withAssessment(1L, 1L, 1L, 1L)
                .withQuestion(1L, 1L, 2L, 2L)
                .withFormInput(1L, 2L, 3L, 4L)
                .withValue("Response to Q1 Form Input 1", "Response to Q1 Form Input 2", "Response to Q2 Form Input 1", "Response to Q2 Form Input 2")
                .build(4);

        List<FormInputResource> formInputs = FormInputResourceBuilder.newFormInputResource()
                .withId(1L, 2L, 3L, 4L)
                .withFormInputTypeTitle(SCORE_INPUT_TYPE)
                .build(4);
        List<QuestionResource> questions = QuestionResourceBuilder.newQuestionResource()
                .withId(32L, 33L, 1L, 20L, 21L, 22L, 23L, 10L, 30L, 31L)
                .withShortName("Question short name")
                .build(10);

        ApplicationResource app = applications.get(0);
        Set<Long> sections = newHashSet(1L, 2L);
        Map<Long, Set<Long>> mappedSections = new HashMap<>();
        mappedSections.put(organisations.get(0).getId(), sections);
        when(competitionService.getById(app.getCompetition())).thenReturn(competition);
        when(sectionService.getCompletedSectionsByOrganisation(anyLong())).thenReturn(mappedSections);
        when(assessmentService.getById(assessment.getId())).thenReturn(assessment);
        when(assessorFormInputResponseService.getAllAssessorFormInputResponses(assessment.getId())).thenReturn(assessorResponses);
        when(formInputService.findAssessmentInputsByQuestion(anyLong())).thenReturn(formInputs);
        when(questionService.getById(32L)).thenReturn(questions.get(0));
        Map<Long, AssessmentOverviewRowViewModel> assessorResponsesMap = new HashMap<>();
        questions.forEach(question -> assessorResponsesMap.put(question.getId(), new AssessmentOverviewRowViewModel(question, formInputs, assessorResponses)));

        FileEntryResource fileEntry = newFileEntryResource().build();
        FormInputResource formInput = newFormInputResource().withId(1L).withQuestion(32L).build();
        setupFormInputAndFileEntry(fileEntry, formInput, app);
        List<AppendixResource> appendices = setUpAppendices(fileEntry, formInput, app, questions.get(0));

        mockMvc.perform(get("/" + assessment.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("assessment/application-overview"))
                .andExpect(model().attribute("currentApplication", app))
                .andExpect(model().attribute("questionFeedback", assessorResponsesMap))
                .andExpect(model().attribute("appendices", appendices))
                .andExpect(model().attribute("currentCompetition", competitionService.getById(app.getCompetition())))
                .andExpect(model().attribute("daysLeft", 3L))
                .andExpect(model().attribute("daysLeftPercentage", 50L));
    }

    @Test
    public void assessmentFinance() throws Exception {
        AssessmentResource assessment = newAssessmentResource().withId(1L).withApplication(1L).build();
        CompetitionResource competition = newCompetitionResource()
                .withId(1L)
                .withAssessmentStartDate(LocalDateTime.now().minusDays(2))
                .withAssessmentEndDate(LocalDateTime.now().plusDays(4))
                .build();
        ApplicationResource app = applications.get(0);
        when(competitionService.getById(app.getCompetition())).thenReturn(competition);
        when(assessmentService.getById(assessment.getId())).thenReturn(assessment);

        FileEntryResource fileEntry = newFileEntryResource().build();
        FormInputResource formInput = newFormInputResource().withId(1L).build();
        setupFormInputAndFileEntry(fileEntry, formInput, app);
        SortedSet<OrganisationResource> orgSet = setupOrganisations();
        List<ApplicationFinanceResource> appFinanceList = setupFinances(app, orgSet);
        OrganisationFinanceOverview organisationFinanceOverview = newOrganisationFinanceOverviewBuilder()
                .withApplicationId(app.getId())
                .withOrganisationFinances(appFinanceList)
                .build();

        mockMvc.perform(get("/" + assessment.getId() + "/finances"))
                .andExpect(status().isOk())
                .andExpect(view().name("assessment/application-finances-summary"))
                .andExpect(model().attribute("currentApplication", app))
                .andExpect(model().attribute("currentCompetition", competitionService.getById(app.getCompetition())))
                .andExpect(model().attribute("assessmentId", assessment.getId()))
                .andExpect(model().attribute("applicationOrganisations", orgSet))
                .andExpect(model().attribute("organisationFinances", organisationFinanceOverview.getApplicationFinancesByOrganisation()))
                .andExpect(model().attribute("financeTotal", organisationFinanceOverview.getTotal()));
    }

    @Test
    public void rejectInvitation() throws Exception {
        Long assessmentId = 1L;
        Long competitionId = 2L;
        String reason = "reason";
        String comment = String.join(" ", nCopies(100, "comment"));

        AssessmentResource assessment = newAssessmentResource()
                .with(id(assessmentId))
                .withCompetition(competitionId)
                .build();

        when(assessmentService.getById(assessmentId)).thenReturn(assessment);
        when(assessmentService.rejectInvitation(assessmentId, reason, comment)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/{assessmentId}/reject", assessmentId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("rejectReason", reason)
                .param("rejectComment", comment))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/assessor/dashboard/competition/%s", competitionId)))
                .andReturn();

        verify(assessmentService, times(1)).getById(assessmentId);
        verify(assessmentService, times(1)).rejectInvitation(assessmentId, reason, comment);
        verifyNoMoreInteractions(assessmentService);
    }

    @Test
    public void rejectInvitation_noReason() throws Exception {
        Long assessmentId = 1L;
        Long applicationId = 2L;
        String reason = "";
        String comment = String.join(" ", nCopies(100, "comment"));

        AssessmentResource assessment = newAssessmentResource()
                .with(id(assessmentId))
                .withApplication(applicationId)
                .build();

        ApplicationResource application = newApplicationResource().build();

        when(assessmentService.getById(assessmentId)).thenReturn(assessment);
        when(applicationService.getById(applicationId)).thenReturn(application);

        // The non-js confirmation view should be returned with the comment pre-populated in the form and an error for the missing reason

        AssessmentOverviewForm expectedForm = new AssessmentOverviewForm();
        expectedForm.setRejectReason(reason);
        expectedForm.setRejectComment(comment);

        MvcResult result = mockMvc.perform(post("/{assessmentId}/reject", assessmentId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("rejectReason", reason)
                .param("rejectComment", comment))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(model().attributeExists("model"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "rejectReason"))
                .andExpect(view().name("assessment/reject-invitation-confirm"))
                .andReturn();

        RejectAssessmentViewModel model = (RejectAssessmentViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(assessmentId, model.getAssessmentId());
        assertEquals(application, model.getApplication());

        AssessmentOverviewForm form = (AssessmentOverviewForm) result.getModelAndView().getModel().get("form");

        assertEquals(reason, form.getRejectReason());
        assertEquals(comment, form.getRejectComment());

        BindingResult bindingResult = form.getBindingResult();
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("rejectReason"));
        assertEquals("Please enter a reason", bindingResult.getFieldError("rejectReason").getDefaultMessage());

        InOrder inOrder = Mockito.inOrder(assessmentService, applicationService);
        inOrder.verify(assessmentService, calls(1)).getById(assessmentId);
        inOrder.verify(applicationService, calls(1)).getById(applicationId);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void rejectInvitation_exceedsCharacterSizeLimit() throws Exception {
        Long assessmentId = 1L;
        Long applicationId = 2L;
        String reason = "reason";
        String comment = RandomStringUtils.random(5001);

        AssessmentResource assessment = newAssessmentResource()
                .with(id(assessmentId))
                .withApplication(applicationId)
                .build();

        ApplicationResource application = newApplicationResource().build();

        when(assessmentService.getById(assessmentId)).thenReturn(assessment);
        when(applicationService.getById(applicationId)).thenReturn(application);

        // The non-js confirmation view should be returned with the comment pre-populated in the form and an error for the missing reason

        AssessmentOverviewForm expectedForm = new AssessmentOverviewForm();
        expectedForm.setRejectReason(reason);
        expectedForm.setRejectComment(comment);

        MvcResult result = mockMvc.perform(post("/{assessmentId}/reject", assessmentId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("rejectReason", reason)
                .param("rejectComment", comment))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(model().attributeExists("model"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "rejectComment"))
                .andExpect(view().name("assessment/reject-invitation-confirm"))
                .andReturn();

        RejectAssessmentViewModel model = (RejectAssessmentViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(assessmentId, model.getAssessmentId());
        assertEquals(application, model.getApplication());

        AssessmentOverviewForm form = (AssessmentOverviewForm) result.getModelAndView().getModel().get("form");

        assertEquals(reason, form.getRejectReason());
        assertEquals(comment, form.getRejectComment());

        BindingResult bindingResult = form.getBindingResult();
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("rejectComment"));
        assertEquals("This field cannot contain more than {1} characters", bindingResult.getFieldError("rejectComment").getDefaultMessage());
        assertEquals(5000, bindingResult.getFieldError("rejectComment").getArguments()[1]);

        InOrder inOrder = Mockito.inOrder(assessmentService, applicationService);
        inOrder.verify(assessmentService, calls(1)).getById(assessmentId);
        inOrder.verify(applicationService, calls(1)).getById(applicationId);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void rejectInvitation_exceedsWordLimit() throws Exception {
        Long assessmentId = 1L;
        Long applicationId = 2L;
        String reason = "reason";
        String comment = String.join(" ", nCopies(101, "comment"));

        AssessmentResource assessment = newAssessmentResource()
                .with(id(assessmentId))
                .withApplication(applicationId)
                .build();

        ApplicationResource application = newApplicationResource().build();

        when(assessmentService.getById(assessmentId)).thenReturn(assessment);
        when(applicationService.getById(applicationId)).thenReturn(application);

        // The non-js confirmation view should be returned with the comment pre-populated in the form and an error for the missing reason

        AssessmentOverviewForm expectedForm = new AssessmentOverviewForm();
        expectedForm.setRejectReason(reason);
        expectedForm.setRejectComment(comment);

        MvcResult result = mockMvc.perform(post("/{assessmentId}/reject", assessmentId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("rejectReason", reason)
                .param("rejectComment", comment))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(model().attributeExists("model"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "rejectComment"))
                .andExpect(view().name("assessment/reject-invitation-confirm"))
                .andReturn();

        RejectAssessmentViewModel model = (RejectAssessmentViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(assessmentId, model.getAssessmentId());
        assertEquals(application, model.getApplication());

        AssessmentOverviewForm form = (AssessmentOverviewForm) result.getModelAndView().getModel().get("form");

        assertEquals(reason, form.getRejectReason());
        assertEquals(comment, form.getRejectComment());

        BindingResult bindingResult = form.getBindingResult();
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("rejectComment"));
        assertEquals("Maximum word count exceeded. Please reduce your word count to {1}.", bindingResult.getFieldError("rejectComment").getDefaultMessage());
        assertEquals(100, bindingResult.getFieldError("rejectComment").getArguments()[1]);

        InOrder inOrder = Mockito.inOrder(assessmentService, applicationService);
        inOrder.verify(assessmentService, calls(1)).getById(assessmentId);
        inOrder.verify(applicationService, calls(1)).getById(applicationId);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void rejectInvitation_eventNotAccepted() throws Exception {
        Long assessmentId = 1L;
        Long applicationId = 2L;
        String reason = "reason";
        String comment = String.join(" ", nCopies(100, "comment"));

        AssessmentResource assessment = newAssessmentResource()
                .with(id(assessmentId))
                .withApplication(applicationId)
                .build();

        ApplicationResource application = newApplicationResource().build();

        when(assessmentService.getById(assessmentId)).thenReturn(assessment);
        when(applicationService.getById(applicationId)).thenReturn(application);
        when(assessmentService.rejectInvitation(assessmentId, reason, comment)).thenReturn(serviceFailure(ASSESSMENT_REJECTION_FAILED));

        // The non-js confirmation view should be returned with the fields pre-populated in the form and a global error

        AssessmentOverviewForm expectedForm = new AssessmentOverviewForm();
        expectedForm.setRejectReason(reason);
        expectedForm.setRejectComment(comment);

        MvcResult result = mockMvc.perform(post("/{assessmentId}/reject", assessmentId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("rejectReason", reason)
                .param("rejectComment", comment))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(model().attributeExists("model"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form"))
                .andExpect(view().name("assessment/reject-invitation-confirm"))
                .andReturn();

        RejectAssessmentViewModel model = (RejectAssessmentViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(assessmentId, model.getAssessmentId());
        assertEquals(application, model.getApplication());

        AssessmentOverviewForm form = (AssessmentOverviewForm) result.getModelAndView().getModel().get("form");

        BindingResult bindingResult = form.getBindingResult();
        assertEquals(1, bindingResult.getGlobalErrorCount());
        assertEquals(0, bindingResult.getFieldErrorCount());
        assertEquals(ASSESSMENT_REJECTION_FAILED.name(), bindingResult.getGlobalError().getCode());

        InOrder inOrder = Mockito.inOrder(assessmentService, applicationService);
        inOrder.verify(assessmentService, calls(1)).getById(assessmentId);
        inOrder.verify(assessmentService, calls(1)).rejectInvitation(assessmentId, reason, comment);
        inOrder.verify(applicationService, calls(1)).getById(applicationId);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void rejectInvitationConfirm() throws Exception {
        Long assessmentId = 1L;
        Long applicationId = 2L;

        AssessmentResource assessment = newAssessmentResource()
                .with(id(assessmentId))
                .withApplication(applicationId)
                .build();

        ApplicationResource application = newApplicationResource().build();

        when(assessmentService.getById(assessmentId)).thenReturn(assessment);
        when(applicationService.getById(applicationId)).thenReturn(application);

        AssessmentOverviewForm expectedForm = new AssessmentOverviewForm();

        MvcResult result = mockMvc.perform(get("/{assessmentId}/reject/confirm", assessmentId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessment/reject-invitation-confirm"))
                .andReturn();

        RejectAssessmentViewModel model = (RejectAssessmentViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(assessmentId, model.getAssessmentId());
        assertEquals(application, model.getApplication());

        InOrder inOrder = Mockito.inOrder(assessmentService, applicationService);
        inOrder.verify(assessmentService, calls(1)).getById(assessmentId);
        inOrder.verify(applicationService, calls(1)).getById(applicationId);
        inOrder.verifyNoMoreInteractions();
    }

    private List<ApplicationFinanceResource> setupFinances(ApplicationResource app, SortedSet<OrganisationResource> orgSet) {
        List<OrganisationResource> orgList = orgSet.stream().collect(Collectors.toList());
        List<ApplicationFinanceResource> appFinanceList = new ArrayList<>();
        appFinanceList.add(new ApplicationFinanceResource(1L, orgList.get(0).getId(), app.getId(), OrganisationSize.LARGE));
        appFinanceList.add(new ApplicationFinanceResource(2L, orgList.get(1).getId(), app.getId(), OrganisationSize.LARGE));

        when(financeService.getApplicationFinanceTotals(app.getId())).thenReturn(appFinanceList);

        when(applicationFinanceRestService.getResearchParticipationPercentage(anyLong())).thenReturn(restSuccess(0.0));
        when(financeHandler.getFinanceFormHandler("Business")).thenReturn(defaultFinanceFormHandler);
        when(financeHandler.getFinanceModelManager("Business")).thenReturn(defaultFinanceModelManager);

        return appFinanceList;
    }

    private SortedSet<OrganisationResource> setupOrganisations() {
        OrganisationResource org1 = newOrganisationResource().withId(1L).withName("Empire Ltd").build();
        OrganisationResource org2 = newOrganisationResource().withId(2L).withName("Ludlow").build();
        Comparator<OrganisationResource> compareById = Comparator.comparingLong(OrganisationResource::getId);
        SortedSet<OrganisationResource> orgSet = new TreeSet<>(compareById);
        orgSet.add(org1);
        orgSet.add(org2);

        return orgSet;
    }

    private void setupFormInputAndFileEntry(FileEntryResource fileEntry, FormInputResource formInput, ApplicationResource app) {
        FormInputResponseResource formInputResponse = newFormInputResponseResource().withFormInputs(1L).withFileEntry(fileEntry).build();
        List<FormInputResponseResource> responses = new ArrayList<>();
        responses.add(formInputResponse);

        when(formInputResponseService.getByApplication(app.getId())).thenReturn(responses);
        when(formInputRestService.getById(formInputResponse.getFormInput())).thenReturn(restSuccess(formInput));
        when(fileEntryRestService.findOne(formInputResponse.getFileEntry())).thenReturn(restSuccess(fileEntry));
    }

    private List<AppendixResource> setUpAppendices(FileEntryResource fileEntry, FormInputResource formInput, ApplicationResource app, QuestionResource appendixQuestion) {
        return singletonList(new AppendixResource(app.getId(), formInput.getId(), appendixQuestion.getShortName(), fileEntry));
    }
}
