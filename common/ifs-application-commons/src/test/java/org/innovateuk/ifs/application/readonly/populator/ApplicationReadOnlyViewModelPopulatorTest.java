package org.innovateuk.ifs.application.readonly.populator;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings;
import org.innovateuk.ifs.application.readonly.viewmodel.ApplicationQuestionReadOnlyViewModel;
import org.innovateuk.ifs.application.readonly.viewmodel.ApplicationReadOnlyViewModel;
import org.innovateuk.ifs.application.readonly.viewmodel.ApplicationSectionReadOnlyViewModel;
import org.innovateuk.ifs.application.readonly.viewmodel.FinanceReadOnlyViewModel;
import org.innovateuk.ifs.application.resource.ApplicationExpressionOfInterestConfigResource;
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
import org.innovateuk.ifs.competition.resource.CompetitionTypeEnum;
import org.innovateuk.ifs.competition.resource.GrantTermsAndConditionsResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.horizon.resource.ApplicationHorizonWorkProgrammeResource;
import org.innovateuk.ifs.horizon.resource.HorizonWorkProgrammeResource;
import org.innovateuk.ifs.horizon.service.HorizonWorkProgrammeRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.supporter.resource.SupporterAssignmentResource;
import org.innovateuk.ifs.supporter.resource.SupporterState;
import org.innovateuk.ifs.supporter.service.SupporterAssignmentRestService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.ProcessRoleType;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
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
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.AsyncTestExpectationHelper.setupAsyncExpectations;
import static org.innovateuk.ifs.application.builder.ApplicationAssessmentResourceBuilder.newApplicationAssessmentResource;
import static org.innovateuk.ifs.application.builder.ApplicationExpressionOfInterestConfigResourceBuilder.newApplicationExpressionOfInterestConfigResource;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static org.innovateuk.ifs.application.builder.QuestionStatusResourceBuilder.newQuestionStatusResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.GrantTermsAndConditionsResourceBuilder.newGrantTermsAndConditionsResource;
import static org.innovateuk.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.supporter.builder.SupporterAssignmentResourceBuilder.newSupporterAssignmentResource;
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
    private ProcessRoleRestService processRoleRestService;

    @Mock
    private AssessorFormInputResponseRestService assessorFormInputResponseRestService;

    @Mock
    private AsyncFuturesGenerator futuresGeneratorMock;

    @Mock
    private SupporterAssignmentRestService supporterAssignmentRestService;

    @Mock
    private HorizonWorkProgrammeRestService horizonWorkProgrammeRestService;

    private final long applicationId = 1L;
    private final long assessmentId = 2L;
    private final long eoiApplicationId = 3L;
    private final QuestionReadOnlyViewModelPopulator mockPopulator = mock(QuestionReadOnlyViewModelPopulator.class);
    private List<FormInputResource> formInputs = new ArrayList<>();
    private List<FormInputResponseResource> responses = new ArrayList<>();
    private List<ApplicationHorizonWorkProgrammeResource> workProgrammeFuture = new ArrayList<>();


    @Before
    public void setupExpectations() {
        setupAsyncExpectations(futuresGeneratorMock);

        formInputs = newFormInputResource().withQuestion(2L).build(1);
        responses = newFormInputResponseResource().withFormInputs(formInputs.get(0).getId()).build(1);
        workProgrammeFuture = singletonList(new ApplicationHorizonWorkProgrammeResource());

    }

    @Test
    public void populate() {
        UserResource user = newUserResource()
                .withRoleGlobal(Role.APPLICANT)
                .build();
        ApplicationReadOnlySettings settings = ApplicationReadOnlySettings.defaultSettings()
                .setIncludeQuestionLinks(true)
                .setIncludeStatuses(true)
                .setAssessmentId(assessmentId);

        setField(populator, "populatorMap", asMap(QuestionSetupType.APPLICATION_TEAM, mockPopulator));
        setField(populator, "asyncFuturesGenerator", futuresGeneratorMock);

        GrantTermsAndConditionsResource grantTermsAndConditionsResource = newGrantTermsAndConditionsResource()
                .withName("Innovate UK")
                .build();
        CompetitionResource competition = newCompetitionResource()
                .withFundingType(FundingType.GRANT)
                .withTermsAndConditions(grantTermsAndConditionsResource)
                .build();
        ApplicationResource application = newApplicationResource()
                .withId(applicationId)
                .withCompetition(competition.getId())
                .build();
        List<QuestionResource> questions = newQuestionResource()
                .withQuestionSetupType(QuestionSetupType.APPLICATION_TEAM)
                .build(1);
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

        ProcessRoleResource processRole = newProcessRoleResource().withRole(ProcessRoleType.LEADAPPLICANT).withUser(user).build();

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
                questions, formInputs, responses, questionStatuses, singletonList(assessorResponseFuture), emptyList(), Optional.of(workProgrammeFuture));
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
        when(processRoleRestService.findProcessRole(application.getId())).thenReturn(restSuccess(newArrayList(processRole)));
        when(assessorFormInputResponseRestService.getApplicationAssessment(applicationId, assessmentId)).thenReturn(restSuccess(assessorResponseFuture));
        when(horizonWorkProgrammeRestService.findSelected(applicationId)).thenReturn(restSuccess(workProgrammeFuture));

        when(mockPopulator.populate(questions.get(0), expectedData, settings)).thenReturn(expectedRowModel);

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

        assertFalse(viewModel.isKtpCompetition());

        verify(mockPopulator).populate(questions.get(0), expectedData, settings);
    }

    @Test
    public void populateKtp() {
        OrganisationResource organisation = newOrganisationResource().build();
        UserResource user = newUserResource()
                .withRolesGlobal(asList(Role.KNOWLEDGE_TRANSFER_ADVISER, Role.ASSESSOR))
                .build();
        ApplicationReadOnlySettings settings = ApplicationReadOnlySettings.defaultSettings()
                .setIncludeQuestionLinks(true)
                .setIncludeStatuses(true)
                .setAssessmentId(assessmentId)
                .setIncludeAllAssessorFeedback(true)
                .setIncludeAllSupporterFeedback(true);

        setField(populator, "populatorMap", asMap(QuestionSetupType.KTP_ASSESSMENT, mockPopulator));
        setField(populator, "asyncFuturesGenerator", futuresGeneratorMock);

        GrantTermsAndConditionsResource grantTermsAndConditionsResource = newGrantTermsAndConditionsResource()
                .withName("Knowledge Transfer Partnership (KTP)")
                .build();
        CompetitionResource competition = newCompetitionResource()
                .withFundingType(FundingType.KTP)
                .withTermsAndConditions(grantTermsAndConditionsResource)
                .build();
        ApplicationResource application = newApplicationResource()
                .withId(applicationId)
                .withCompetition(competition.getId())
                .withLeadOrganisationId(organisation.getId())
                .build();
        List<QuestionResource> questions = newQuestionResource()
                .withQuestionSetupType(QuestionSetupType.KTP_ASSESSMENT)
                .build(1);
        List<QuestionStatusResource> questionStatuses = newQuestionStatusResource()
                .withQuestion(questions.get(0).getId())
                .build(1);
        List<SectionResource> sections = newSectionResource()
                .withName("Score assessment")
                .withChildSections(Collections.emptyList())
                .withQuestions(questions.stream()
                        .filter(questionResource -> questionResource.getQuestionSetupType() == QuestionSetupType.KTP_ASSESSMENT)
                        .map(QuestionResource::getId).collect(Collectors.toList()))
                .withType(SectionType.KTP_ASSESSMENT)
                .build(1);

        ProcessRoleResource processRole = newProcessRoleResource()
                .withRole(ProcessRoleType.KNOWLEDGE_TRANSFER_ADVISER, ProcessRoleType.ASSESSOR)
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
                questions, formInputs, responses, questionStatuses, singletonList(assessorResponseFuture), supporterResponseFuture, Optional.of(workProgrammeFuture));
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
        when(processRoleRestService.findProcessRole(application.getId())).thenReturn(restSuccess(newArrayList(processRole)));
        when(assessorFormInputResponseRestService.getApplicationAssessment(applicationId, assessmentId)).thenReturn(restSuccess(assessorResponseFuture));
        when(supporterAssignmentRestService.getAssignmentsByApplicationId(applicationId)).thenReturn(restSuccess(supporterResponseFuture));
        when(horizonWorkProgrammeRestService.findSelected(applicationId)).thenReturn(restSuccess(workProgrammeFuture));

        when(mockPopulator.populate(questions.get(0), expectedData, settings)).thenReturn(expectedRowModel);

        ApplicationReadOnlyViewModel viewModel = populator.populate(applicationId, user, settings);

        assertEquals(settings, viewModel.getSettings());

        assertEquals(viewModel.getSections().size(), 1);

        Iterator<ApplicationSectionReadOnlyViewModel> iterator = viewModel.getSections().iterator();

        ApplicationSectionReadOnlyViewModel scoreAssessmentSection = iterator.next();
        assertEquals(scoreAssessmentSection.getName(), "Score assessment");
        assertEquals(scoreAssessmentSection.getQuestions().iterator().next(), expectedRowModel);

        assertTrue(viewModel.isShouldDisplayKtpApplicationFeedback());

        assertNotNull(viewModel.getOverallFeedbacks());
        assertEquals(1, viewModel.getOverallFeedbacks().size());
        assertEquals("Overall Feedback", viewModel.getOverallFeedbacks().get(0));

        assertNotNull(viewModel.getAssignments());
        assertEquals(3, viewModel.getAssignments().size());

        assertNotNull(viewModel.getAssignments().get("accepted"));
        assertEquals(2, viewModel.getAssignments().get("accepted").size());
        assertNotNull(viewModel.getAssignments().get("accepted").get(0));
        assertEquals("accepted one", viewModel.getAssignments().get("accepted").get(0).getComments());
        assertEquals("Org A", viewModel.getAssignments().get("accepted").get(0).getUserSimpleOrganisation());
        assertNotNull(viewModel.getAssignments().get("accepted").get(1));
        assertEquals("accepted two", viewModel.getAssignments().get("accepted").get(1).getComments());
        assertEquals("Org B", viewModel.getAssignments().get("accepted").get(1).getUserSimpleOrganisation());
        assertTrue(viewModel.isAccepted());
        assertEquals(2, viewModel.getAcceptedCount());

        assertNotNull(viewModel.getAssignments().get("rejected"));
        assertEquals(2, viewModel.getAssignments().get("rejected").size());
        assertNotNull(viewModel.getAssignments().get("rejected").get(0));
        assertEquals("rejected one", viewModel.getAssignments().get("rejected").get(0).getComments());
        assertEquals("Org C", viewModel.getAssignments().get("rejected").get(0).getUserSimpleOrganisation());
        assertNotNull(viewModel.getAssignments().get("rejected").get(1));
        assertEquals("rejected two", viewModel.getAssignments().get("rejected").get(1).getComments());
        assertEquals("Org D", viewModel.getAssignments().get("rejected").get(1).getUserSimpleOrganisation());
        assertTrue(viewModel.isDeclined());
        assertEquals(2, viewModel.getDeclinedCount());

        assertNotNull(viewModel.getAssignments().get("created"));
        assertEquals(1, viewModel.getAssignments().get("created").size());
        assertNotNull(viewModel.getAssignments().get("created").get(0));
        assertEquals("created", viewModel.getAssignments().get("created").get(0).getComments());
        assertEquals("Org E", viewModel.getAssignments().get("created").get(0).getUserSimpleOrganisation());
        assertTrue(viewModel.isPending());
        assertEquals(1, viewModel.getPendingCount());

        assertTrue(viewModel.isKtpCompetition());

        verify(mockPopulator).populate(questions.get(0), expectedData, settings);
    }

    @Test
    public void populateHecp() {
        UserResource user = newUserResource()
                .withRoleGlobal(Role.APPLICANT)
                .build();
        ApplicationReadOnlySettings settings = ApplicationReadOnlySettings.defaultSettings()
                .setIncludeQuestionLinks(true)
                .setIncludeStatuses(true);

        setField(populator, "populatorMap", asMap(QuestionSetupType.HORIZON_WORK_PROGRAMME, mockPopulator));
        setField(populator, "asyncFuturesGenerator", futuresGeneratorMock);

        GrantTermsAndConditionsResource grantTermsAndConditionsResource = newGrantTermsAndConditionsResource()
                .withName("Horizon Europe Guarantee")
                .build();
        CompetitionResource competition = newCompetitionResource()
                .withFundingType(FundingType.HECP)
                .withTermsAndConditions(grantTermsAndConditionsResource)
                .withCompetitionTypeEnum(CompetitionTypeEnum.HORIZON_EUROPE_GUARANTEE)
                .build();
        ApplicationResource application = newApplicationResource()
                .withId(applicationId)
                .withCompetition(competition.getId())
                .build();
        List<QuestionResource> questions = newQuestionResource()
                .withQuestionSetupType(QuestionSetupType.HORIZON_WORK_PROGRAMME)
                .withName("Work programme")
                .withShortName("Work programme")
                .build(1);
        List<QuestionStatusResource> questionStatuses = newQuestionStatusResource()
                .withQuestion(questions.get(0).getId())
                .build(1);
        OrganisationResource organisation = newOrganisationResource().build();
        List<SectionResource> sections = newSectionResource()
                .withName("Section with questions", "Finance section")
                .withChildSections(Collections.emptyList(), Collections.singletonList(1L))
                .withQuestions(questions.stream().map(QuestionResource::getId).collect(Collectors.toList()), emptyList())
                .withType(SectionType.PROJECT_DETAILS, SectionType.FINANCE)
                .build(2);
        HorizonWorkProgrammeResource workProgramme = new HorizonWorkProgrammeResource(6, "CL6", null, true);
        HorizonWorkProgrammeResource callerId = new HorizonWorkProgrammeResource(6, "CL6", workProgramme, true);

        workProgrammeFuture = asList(new ApplicationHorizonWorkProgrammeResource(applicationId, workProgramme),
                new ApplicationHorizonWorkProgrammeResource(applicationId, callerId));

        ProcessRoleResource processRole = newProcessRoleResource().withRole(ProcessRoleType.LEADAPPLICANT).withUser(user).build();

        ApplicationReadOnlyData expectedData = new ApplicationReadOnlyData(application, competition, user, newArrayList(processRole),
                questions, formInputs, responses, questionStatuses, emptyList(), emptyList(), Optional.of(workProgrammeFuture));
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
        when(processRoleRestService.findProcessRole(application.getId())).thenReturn(restSuccess(newArrayList(processRole)));
        when(horizonWorkProgrammeRestService.findSelected(applicationId)).thenReturn(restSuccess(workProgrammeFuture));

        when(mockPopulator.populate(questions.get(0), expectedData, settings)).thenReturn(expectedRowModel);

        ApplicationReadOnlyViewModel viewModel = populator.populate(applicationId, user, settings);

        assertEquals(viewModel.getSettings(), settings);
        assertEquals(viewModel.getSections().size(), 2);

        Iterator<ApplicationSectionReadOnlyViewModel> iterator = viewModel.getSections().iterator();
        ApplicationSectionReadOnlyViewModel sectionWithQuestion = iterator.next();

        assertEquals(sectionWithQuestion.getName(), "Section with questions");
        assertEquals(sectionWithQuestion.getQuestions().iterator().next(), expectedRowModel);

        assertEquals(workProgrammeFuture, expectedData.getApplicationHorizonWorkProgrammeResource().get());

        verify(mockPopulator).populate(questions.get(0), expectedData, settings);
    }

    @Test
    public void populateEOI() {
        UserResource user = newUserResource()
                .withRoleGlobal(Role.APPLICANT)
                .build();

        ApplicationExpressionOfInterestConfigResource applicationExpressionOfInterestConfig = newApplicationExpressionOfInterestConfigResource()
                .withEnabledForExpressionOfInterest(true)
                .build();

        ApplicationReadOnlySettings settings = ApplicationReadOnlySettings.defaultSettings()
                .setIncludeQuestionLinks(true)
                .setIncludeStatuses(true)
                .setAssessmentId(assessmentId)
                .setIncludeQuestionNumber(false);

        setField(populator, "populatorMap", asMap(QuestionSetupType.APPLICATION_TEAM, mockPopulator));
        setField(populator, "asyncFuturesGenerator", futuresGeneratorMock);

        GrantTermsAndConditionsResource grantTermsAndConditionsResource = newGrantTermsAndConditionsResource()
                .withName("Innovate UK")
                .build();
        CompetitionResource competition = newCompetitionResource()
                .withFundingType(FundingType.GRANT)
                .withTermsAndConditions(grantTermsAndConditionsResource)
                .build();
        ApplicationResource application = newApplicationResource()
                .withId(applicationId)
                .withCompetition(competition.getId())
                .withApplicationExpressionOfInterestConfigResource(applicationExpressionOfInterestConfig)
                .build();
        List<QuestionResource> questions = newQuestionResource()
                .withQuestionSetupType(QuestionSetupType.APPLICATION_TEAM)
                .withEnabledForPreRegistration(false)
                .build(1);
        List<QuestionStatusResource> questionStatuses = newQuestionStatusResource()
                .withQuestion(questions.get(0).getId())
                .build(1);
        OrganisationResource organisation = newOrganisationResource().build();
        List<SectionResource> sections = newSectionResource()
                .withName("Section with questions", "Finance section", "Score assessment")
                .withChildSections(Collections.emptyList(), Collections.singletonList(1L), Collections.emptyList())
                .withQuestions(questions.stream().map(QuestionResource::getId).collect(Collectors.toList()), emptyList(), emptyList())
                .withType(SectionType.GENERAL, SectionType.FINANCE, SectionType.KTP_ASSESSMENT)
                .withEnabledForPreRegistration(false)
                .build(3);

        ProcessRoleResource processRole = newProcessRoleResource().withRole(ProcessRoleType.LEADAPPLICANT).withUser(user).build();

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
                questions, formInputs, responses, questionStatuses, singletonList(assessorResponseFuture), emptyList(), Optional.of(workProgrammeFuture));
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
        when(processRoleRestService.findProcessRole(application.getId())).thenReturn(restSuccess(newArrayList(processRole)));
        when(assessorFormInputResponseRestService.getApplicationAssessment(applicationId, assessmentId)).thenReturn(restSuccess(assessorResponseFuture));
        when(horizonWorkProgrammeRestService.findSelected(applicationId)).thenReturn(restSuccess(workProgrammeFuture));

        when(mockPopulator.populate(questions.get(0), expectedData, settings)).thenReturn(expectedRowModel);

        ApplicationReadOnlyViewModel viewModel = populator.populate(applicationId, user, settings);

        assertEquals(viewModel.getSettings(), settings);
        assertEquals(viewModel.getSections().size(), 2);
        assertFalse(viewModel.isEoiFullApplication());

        Iterator<ApplicationSectionReadOnlyViewModel> iterator = viewModel.getSections().iterator();
        ApplicationSectionReadOnlyViewModel sectionWithQuestion = iterator.next();

        assertEquals(sectionWithQuestion.getName(), "Section with questions");
        assertEquals(sectionWithQuestion.isVisible(), false);
        assertEquals(sectionWithQuestion.getQuestions().size(), 0);


        ApplicationSectionReadOnlyViewModel financeSection = iterator.next();
        assertEquals(financeSection.getName(), "Finance section");
        assertEquals(financeSection.isVisible(), false);
        assertEquals(financeSection.getQuestions().iterator().next(), expectedFinanceSummary);
    }

    @Test
    public void populateEOIFullApplication() {
        UserResource user = newUserResource()
                .withRoleGlobal(Role.APPLICANT)
                .build();

        ApplicationExpressionOfInterestConfigResource applicationExpressionOfInterestConfig = newApplicationExpressionOfInterestConfigResource()
                .withEnabledForExpressionOfInterest(false)
                .withEoiApplicationId(eoiApplicationId)
                .build();

        ApplicationReadOnlySettings settings = ApplicationReadOnlySettings.defaultSettings()
                .setIncludeQuestionLinks(true)
                .setIncludeStatuses(true)
                .setAssessmentId(assessmentId)
                .setIncludeQuestionNumber(false);

        setField(populator, "populatorMap", asMap(QuestionSetupType.APPLICATION_TEAM, mockPopulator));
        setField(populator, "asyncFuturesGenerator", futuresGeneratorMock);

        GrantTermsAndConditionsResource grantTermsAndConditionsResource = newGrantTermsAndConditionsResource()
                .withName("Innovate UK")
                .build();
        CompetitionResource competition = newCompetitionResource()
                .withFundingType(FundingType.GRANT)
                .withTermsAndConditions(grantTermsAndConditionsResource)
                .build();
        ApplicationResource application = newApplicationResource()
                .withId(applicationId)
                .withCompetition(competition.getId())
                .withApplicationExpressionOfInterestConfigResource(applicationExpressionOfInterestConfig)
                .build();
        List<QuestionResource> questions = newQuestionResource()
                .withQuestionSetupType(QuestionSetupType.APPLICATION_TEAM)
                .withEnabledForPreRegistration(false)
                .build(1);
        List<QuestionStatusResource> questionStatuses = newQuestionStatusResource()
                .withQuestion(questions.get(0).getId())
                .build(1);
        OrganisationResource organisation = newOrganisationResource().build();
        List<SectionResource> sections = newSectionResource()
                .withName("Section with questions", "Finance section", "Score assessment")
                .withChildSections(Collections.emptyList(), Collections.singletonList(1L), Collections.emptyList())
                .withQuestions(questions.stream().map(QuestionResource::getId).collect(Collectors.toList()), emptyList(), emptyList())
                .withType(SectionType.GENERAL, SectionType.FINANCE, SectionType.KTP_ASSESSMENT)
                .withEnabledForPreRegistration(false)
                .build(3);

        ProcessRoleResource processRole = newProcessRoleResource().withRole(ProcessRoleType.LEADAPPLICANT).withUser(user).build();

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
                questions, formInputs, responses, questionStatuses, singletonList(assessorResponseFuture), emptyList(), Optional.of(workProgrammeFuture));
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
        when(processRoleRestService.findProcessRole(application.getId())).thenReturn(restSuccess(newArrayList(processRole)));
        when(assessorFormInputResponseRestService.getApplicationAssessment(applicationId, assessmentId)).thenReturn(restSuccess(assessorResponseFuture));
        when(horizonWorkProgrammeRestService.findSelected(applicationId)).thenReturn(restSuccess(workProgrammeFuture));

        when(mockPopulator.populate(questions.get(0), expectedData, settings)).thenReturn(expectedRowModel);

        ApplicationReadOnlyViewModel viewModel = populator.populate(applicationId, user, settings);

        assertTrue(viewModel.isEoiFullApplication());
    }
}
