package org.innovateuk.ifs.application.readonly.populator;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings;
import org.innovateuk.ifs.application.readonly.viewmodel.*;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.application.service.SectionRestService;
import org.innovateuk.ifs.assessment.resource.ApplicationAssessmentResource;
import org.innovateuk.ifs.assessment.service.AssessorFormInputResponseRestService;
import org.innovateuk.ifs.async.generation.AsyncFuturesGenerator;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.form.resource.*;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.supporter.resource.SupporterAssignmentResource;
import org.innovateuk.ifs.supporter.resource.SupporterState;
import org.innovateuk.ifs.supporter.service.SupporterAssignmentRestService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.AsyncTestExpectationHelper.setupAsyncExpectations;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static org.innovateuk.ifs.application.builder.QuestionStatusResourceBuilder.newQuestionStatusResource;
import static org.innovateuk.ifs.supporter.builder.SupporterAssignmentResourceBuilder.newSupporterAssignmentResource;
import static org.innovateuk.ifs.application.builder.ApplicationAssessmentResourceBuilder.newApplicationAssessmentResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ApplicationReadOnlyViewModelPopulatorTest {

    @InjectMocks
    private ApplicationReadOnlyViewModelPopulator populator;

    @Mock
    private ApplicationRestService applicationRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private FormInputRestService formInputRestService;

    @Mock
    private FormInputResponseRestService formInputResponseRestService;

    @Mock
    private SectionRestService sectionRestService;

    @Mock
    private QuestionRestService questionRestService;

    @Mock
    private FinanceReadOnlyViewModelPopulator financeSummaryViewModelPopulator;

    @Mock
    private QuestionStatusRestService questionStatusRestService;

    @Mock
    private OrganisationRestService organisationRestService;

    @Mock
    private List<QuestionReadOnlyViewModelPopulator<?>> mocklist;

    @Mock
    private UserRestService userRestService;

    @Mock
    private AssessorFormInputResponseRestService assessorFormInputResponseRestService;

    @Mock
    private AsyncFuturesGenerator futuresGeneratorMock;

    @Mock
    private SupporterAssignmentRestService supporterAssignmentRestService;

    @Before
    public void setupExpectations() {
        setupAsyncExpectations(futuresGeneratorMock);
    }

    @Test
    public void populate() {
        long applicationId = 1L;
        long assessmentId = 2L;
        UserResource user = newUserResource()
                .withRoleGlobal(Role.APPLICANT)
                .build();
        ApplicationReadOnlySettings settings = ApplicationReadOnlySettings.defaultSettings()
                .setIncludeQuestionLinks(true)
                .setIncludeStatuses(true)
                .setAssessmentId(assessmentId);

        QuestionReadOnlyViewModelPopulator mockPopulator = mock(QuestionReadOnlyViewModelPopulator.class);
        ScoreAssessmentQuestionReadOnlyPopulator scoreAssessmentQuestionReadOnlyPopulator = mock(ScoreAssessmentQuestionReadOnlyPopulator.class);
        setField(populator, "populatorMap", asMap(QuestionSetupType.APPLICATION_TEAM, mockPopulator,
                QuestionSetupType.KTP_ASSESSMENT, scoreAssessmentQuestionReadOnlyPopulator));
        setField(populator, "asyncFuturesGenerator", futuresGeneratorMock);

        CompetitionResource competition = newCompetitionResource()
                .withFundingType(FundingType.GRANT)
                .build();
        ApplicationResource application = newApplicationResource()
                .withId(applicationId)
                .withCompetition(competition.getId())
                .build();
        List<QuestionResource> questions = newQuestionResource()
                .withQuestionSetupType(QuestionSetupType.APPLICATION_TEAM)
                .build(1);
        List<FormInputResource> formInputs = newFormInputResource().withQuestion(2L).build(1);
        List<FormInputResponseResource> responses = newFormInputResponseResource().build(1);
        List<QuestionStatusResource> questionStatuses = newQuestionStatusResource()
                .withQuestion(questions.get(0).getId())
                .build(1);
        OrganisationResource organisation = newOrganisationResource().build();
        List<SectionResource> sections = newSectionResource()
                .withName("Section with questions", "Finance section", "Score assessment")
                .withChildSections(Collections.emptyList(), Collections.singletonList(1L), Collections.emptyList())
                .withQuestions(questions.stream().map(QuestionResource::getId).collect(Collectors.toList()), emptyList(), emptyList())
                .withType(SectionType.GENERAL, SectionType.FINANCE, SectionType.KTP_ASSESSMENT)
                .build(3);

        ProcessRoleResource processRole = newProcessRoleResource().withRole(Role.LEADAPPLICANT).withUser(user).build();

        Map<Long, BigDecimal> scores = new HashMap<>();
        scores.put(1L, new BigDecimal("9"));
        Map<Long, String> feedback = new HashMap<>();
        feedback.put(1L, "Hello world");

        ApplicationAssessmentResource assessorResponseFuture = newApplicationAssessmentResource()
                .withApplicationId(applicationId)
                .withTestId(3L)
                .withAveragePercentage(new BigDecimal("50.0"))
                .withScores(scores)
                .withFeedback(feedback)
                .build();

        ApplicationReadOnlyData expectedData = new ApplicationReadOnlyData(application, competition, user, newArrayList(processRole),
                questions, formInputs, responses, questionStatuses, singletonList(assessorResponseFuture), emptyList());
        ApplicationQuestionReadOnlyViewModel expectedRowModel = mock(ApplicationQuestionReadOnlyViewModel.class);
        FinanceReadOnlyViewModel expectedFinanceSummary = mock(FinanceReadOnlyViewModel.class);

        when(financeSummaryViewModelPopulator.populate(expectedData)).thenReturn(expectedFinanceSummary);
        when(applicationRestService.getApplicationById(applicationId)).thenReturn(restSuccess(application));
        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(questionRestService.findByCompetition(competition.getId())).thenReturn(restSuccess(questions));
        when(formInputRestService.getByCompetitionId(competition.getId())).thenReturn(restSuccess(formInputs));
        when(formInputResponseRestService.getResponsesByApplicationId(application.getId())).thenReturn(restSuccess(responses));
        when(organisationRestService.getByUserAndApplicationId(user.getId(), applicationId)).thenReturn(restSuccess(organisation));
        when(questionStatusRestService.findByApplicationAndOrganisation(applicationId, organisation.getId())).thenReturn(restSuccess(questionStatuses));
        when(sectionRestService.getByCompetition(competition.getId())).thenReturn(restSuccess(sections));
        when(userRestService.findProcessRole(application.getId())).thenReturn(restSuccess(newArrayList(processRole)));
        when(assessorFormInputResponseRestService.getApplicationAssessment(applicationId, assessmentId)).thenReturn(restSuccess(assessorResponseFuture));

        when(mockPopulator.populate(competition, questions.get(0), expectedData, settings)).thenReturn(expectedRowModel);

        ApplicationReadOnlyViewModel viewModel = populator.populate(applicationId, user, settings);

        assertEquals(viewModel.getSettings(), settings);
        assertEquals(viewModel.getSections().size(), 2);

        Iterator<ApplicationSectionReadOnlyViewModel> iterator = viewModel.getSections().iterator();
        ApplicationSectionReadOnlyViewModel sectionWithQuestion = iterator.next();

        assertEquals(sectionWithQuestion.getName(), "Section with questions");
        assertEquals(sectionWithQuestion.getQuestions().iterator().next(), expectedRowModel);

        ApplicationSectionReadOnlyViewModel financeSection = iterator.next();
        assertEquals(financeSection.getName(), "Finance section");
        assertEquals(financeSection.getQuestions().iterator().next(), expectedFinanceSummary);

        verify(mockPopulator).populate(competition, questions.get(0), expectedData, settings);
        verifyZeroInteractions(scoreAssessmentQuestionReadOnlyPopulator);
    }

    @Test
    public void populateKtp() {
        long applicationId = 1L;
        long assessmentId = 2L;
        OrganisationResource organisation = newOrganisationResource().build();
        UserResource user = newUserResource()
                .withRolesGlobal(Arrays.asList(Role.KNOWLEDGE_TRANSFER_ADVISER, Role.ASSESSOR))
                .build();
        ApplicationReadOnlySettings settings = ApplicationReadOnlySettings.defaultSettings()
                .setIncludeQuestionLinks(true)
                .setIncludeStatuses(true)
                .setAssessmentId(assessmentId)
                .setIncludeAllAssessorFeedback(true)
                .setIncludeAllSupporterFeedback(true);

        QuestionReadOnlyViewModelPopulator mockPopulator = mock(QuestionReadOnlyViewModelPopulator.class);
        ScoreAssessmentQuestionReadOnlyPopulator scoreAssessmentQuestionReadOnlyPopulator = mock(ScoreAssessmentQuestionReadOnlyPopulator.class);
        setField(populator, "populatorMap", asMap(QuestionSetupType.APPLICATION_TEAM, mockPopulator,
                QuestionSetupType.KTP_ASSESSMENT, scoreAssessmentQuestionReadOnlyPopulator));
        setField(populator, "asyncFuturesGenerator", futuresGeneratorMock);

        CompetitionResource competition = newCompetitionResource()
                .withFundingType(FundingType.KTP)
                .build();
        ApplicationResource application = newApplicationResource()
                .withId(applicationId)
                .withCompetition(competition.getId())
                .withLeadOrganisationId(organisation.getId())
                .build();
        List<QuestionResource> questions = newQuestionResource()
                .withQuestionSetupType(QuestionSetupType.APPLICATION_TEAM, QuestionSetupType.KTP_ASSESSMENT)
                .build(2);
        List<FormInputResource> formInputs = newFormInputResource().withQuestion(2L).build(1);
        List<FormInputResponseResource> responses = newFormInputResponseResource().build(1);
        List<QuestionStatusResource> questionStatuses = newQuestionStatusResource()
                .withQuestion(questions.get(0).getId(), questions.get(1).getId())
                .build(2);
        List<SectionResource> sections = newSectionResource()
                .withName("Section with questions", "Finance section", "Score assessment")
                .withChildSections(Collections.emptyList(), Collections.singletonList(1L), Collections.emptyList())
                .withQuestions(questions.stream()
                                .filter(questionResource -> questionResource.getQuestionSetupType() != QuestionSetupType.KTP_ASSESSMENT)
                                .map(QuestionResource::getId).collect(Collectors.toList()),
                        emptyList(),
                        questions.stream()
                                .filter(questionResource -> questionResource.getQuestionSetupType() == QuestionSetupType.KTP_ASSESSMENT)
                                .map(QuestionResource::getId).collect(Collectors.toList()))
                .withType(SectionType.GENERAL, SectionType.FINANCE, SectionType.KTP_ASSESSMENT)
                .build(3);

        ProcessRoleResource processRole = newProcessRoleResource()
                .withRole(Role.KNOWLEDGE_TRANSFER_ADVISER, Role.ASSESSOR)
                .withUser(user)
                .build();

        Map<Long, BigDecimal> scores = new HashMap<>();
        scores.put(1L, new BigDecimal("9"));
        Map<Long, String> feedback = new HashMap<>();
        feedback.put(1L, "Hello world");

        ApplicationAssessmentResource assessorResponseFuture = newApplicationAssessmentResource()
                .withApplicationId(applicationId)
                .withTestId(3L)
                .withAveragePercentage(new BigDecimal("50.0"))
                .withScores(scores)
                .withFeedback(feedback)
                .withOverallFeedback("Overall Feedback")
                .build();

        List<SupporterAssignmentResource> supporterResponseFuture = newSupporterAssignmentResource()
                .withAssignmentId(1, 2, 3, 4, 5)
                .withState(SupporterState.ACCEPTED, SupporterState.ACCEPTED, SupporterState.REJECTED, SupporterState.REJECTED, SupporterState.CREATED)
                .withComments("accepted one", "accepted two", "rejected one", "rejected two", "created")
                .withUserSimpleOrganisation("Org A", "Org B", "Org C", "Org D", "Org E")
                .build(5);

        ApplicationReadOnlyData expectedData = new ApplicationReadOnlyData(application, competition, user, newArrayList(processRole),
                questions, formInputs, responses, questionStatuses, singletonList(assessorResponseFuture), supporterResponseFuture);
        ApplicationQuestionReadOnlyViewModel expectedRowModel = mock(ApplicationQuestionReadOnlyViewModel.class);
        FinanceReadOnlyViewModel expectedFinanceSummary = mock(FinanceReadOnlyViewModel.class);
        ScoreAssessmentQuestionReadOnlyViewModel expectedScoreAssessmentModel = mock(ScoreAssessmentQuestionReadOnlyViewModel.class);

        when(financeSummaryViewModelPopulator.populate(expectedData)).thenReturn(expectedFinanceSummary);
        when(applicationRestService.getApplicationById(applicationId)).thenReturn(restSuccess(application));
        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(questionRestService.findByCompetition(competition.getId())).thenReturn(restSuccess(questions));
        when(formInputRestService.getByCompetitionId(competition.getId())).thenReturn(restSuccess(formInputs));
        when(formInputResponseRestService.getResponsesByApplicationId(application.getId())).thenReturn(restSuccess(responses));
        when(organisationRestService.getByUserAndApplicationId(user.getId(), applicationId)).thenReturn(restSuccess(organisation));
        when(questionStatusRestService.findByApplicationAndOrganisation(applicationId, organisation.getId())).thenReturn(restSuccess(questionStatuses));
        when(sectionRestService.getByCompetition(competition.getId())).thenReturn(restSuccess(sections));
        when(userRestService.findProcessRole(application.getId())).thenReturn(restSuccess(newArrayList(processRole)));
        when(assessorFormInputResponseRestService.getApplicationAssessment(applicationId, assessmentId)).thenReturn(restSuccess(assessorResponseFuture));
        when(supporterAssignmentRestService.getAssignmentsByApplicationId(applicationId)).thenReturn(restSuccess(supporterResponseFuture));

        when(mockPopulator.populate(competition, questions.get(0), expectedData, settings)).thenReturn(expectedRowModel);
        when(scoreAssessmentQuestionReadOnlyPopulator.populate(competition, questions.get(1), expectedData, settings)).thenReturn(expectedScoreAssessmentModel);

        ApplicationReadOnlyViewModel viewModel = populator.populate(applicationId, user, settings);

        assertEquals(settings, viewModel.getSettings());

        assertEquals(viewModel.getSections().size(), 3);

        Iterator<ApplicationSectionReadOnlyViewModel> iterator = viewModel.getSections().iterator();

        ApplicationSectionReadOnlyViewModel sectionWithQuestion = iterator.next();
        assertEquals(sectionWithQuestion.getName(), "Section with questions");
        assertEquals(sectionWithQuestion.getQuestions().iterator().next(), expectedRowModel);

        ApplicationSectionReadOnlyViewModel financeSection = iterator.next();
        assertEquals(financeSection.getName(), "Finance section");
        assertEquals(financeSection.getQuestions().iterator().next(), expectedFinanceSummary);

        ApplicationSectionReadOnlyViewModel scoreAssessmentSection = iterator.next();
        assertEquals(scoreAssessmentSection.getName(), "Score assessment");
        assertEquals(scoreAssessmentSection.getQuestions().iterator().next(), expectedScoreAssessmentModel);

        assertTrue(viewModel.isShouldDisplayKtpApplicationFeedback());

        assertNotNull(viewModel.getOverallFeedbacks());
        assertEquals(1, viewModel.getOverallFeedbacks().size());
        assertEquals("Overall Feedback", viewModel.getOverallFeedbacks().get(0));

        assertNotNull(viewModel.getAssignments());
        assertEquals(3, viewModel.getAssignments().size());

        assertNotNull(viewModel.getAssignments().get(SupporterState.ACCEPTED));
        assertEquals(2, viewModel.getAssignments().get(SupporterState.ACCEPTED).size());
        assertNotNull(viewModel.getAssignments().get(SupporterState.ACCEPTED).get(0));
        assertEquals("accepted one", viewModel.getAssignments().get(SupporterState.ACCEPTED).get(0).getComments());
        assertEquals("Org A", viewModel.getAssignments().get(SupporterState.ACCEPTED).get(0).getUserSimpleOrganisation());
        assertNotNull(viewModel.getAssignments().get(SupporterState.ACCEPTED).get(1));
        assertEquals("accepted two", viewModel.getAssignments().get(SupporterState.ACCEPTED).get(1).getComments());
        assertEquals("Org B", viewModel.getAssignments().get(SupporterState.ACCEPTED).get(1).getUserSimpleOrganisation());
        assertTrue(viewModel.isAccepted());
        assertEquals(2, viewModel.getAcceptedCount());

        assertNotNull(viewModel.getAssignments().get(SupporterState.REJECTED));
        assertEquals(2, viewModel.getAssignments().get(SupporterState.REJECTED).size());
        assertNotNull(viewModel.getAssignments().get(SupporterState.REJECTED).get(0));
        assertEquals("rejected one", viewModel.getAssignments().get(SupporterState.REJECTED).get(0).getComments());
        assertEquals("Org C", viewModel.getAssignments().get(SupporterState.REJECTED).get(0).getUserSimpleOrganisation());
        assertNotNull(viewModel.getAssignments().get(SupporterState.REJECTED).get(1));
        assertEquals("rejected two", viewModel.getAssignments().get(SupporterState.REJECTED).get(1).getComments());
        assertEquals("Org D", viewModel.getAssignments().get(SupporterState.REJECTED).get(1).getUserSimpleOrganisation());
        assertTrue(viewModel.isDeclined());
        assertEquals(2, viewModel.getDeclinedCount());

        assertNotNull(viewModel.getAssignments().get(SupporterState.CREATED));
        assertEquals(1, viewModel.getAssignments().get(SupporterState.CREATED).size());
        assertNotNull(viewModel.getAssignments().get(SupporterState.CREATED).get(0));
        assertEquals("created", viewModel.getAssignments().get(SupporterState.CREATED).get(0).getComments());
        assertEquals("Org E", viewModel.getAssignments().get(SupporterState.CREATED).get(0).getUserSimpleOrganisation());
        assertTrue(viewModel.isPending());
        assertEquals(1, viewModel.getPendingCount());

        verify(mockPopulator).populate(competition, questions.get(0), expectedData, settings);
        verify(scoreAssessmentQuestionReadOnlyPopulator).populate(competition, questions.get(1), expectedData, settings);
    }

}
