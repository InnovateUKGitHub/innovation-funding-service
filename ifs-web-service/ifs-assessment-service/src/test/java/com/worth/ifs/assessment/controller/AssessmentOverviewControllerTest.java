package com.worth.ifs.assessment.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.builder.QuestionResourceBuilder;
import com.worth.ifs.application.finance.view.OrganisationFinanceOverview;
import com.worth.ifs.application.resource.AppendixResource;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.assessment.model.AssessmentFinancesSummaryModelPopulator;
import com.worth.ifs.assessment.model.AssessmentOverviewModelPopulator;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.assessment.resource.AssessorFormInputResponseResource;
import com.worth.ifs.assessment.service.AssessmentService;
import com.worth.ifs.assessment.service.AssessorFormInputResponseService;
import com.worth.ifs.assessment.viewmodel.AssessmentOverviewRowViewModel;
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
import com.worth.ifs.user.resource.ProcessRoleResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.collect.Sets.newHashSet;
import static com.worth.ifs.BaseBuilderAmendFunctions.id;
import static com.worth.ifs.application.service.Futures.settable;
import static com.worth.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static com.worth.ifs.assessment.builder.AssessorFormInputResponseResourceBuilder.newAssessorFormInputResponseResource;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static com.worth.ifs.file.resource.builders.FileEntryResourceBuilder.newFileEntryResource;
import static com.worth.ifs.finance.builder.OrganisationFinanceOverviewBuilder.newOrganisationFinanceOverviewBuilder;
import static com.worth.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static com.worth.ifs.form.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static com.worth.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static java.util.Arrays.asList;
import static org.mockito.Matchers.anyList;
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
        when(organisationService.getOrganisationForUser(anyLong(), anyList())).thenReturn(Optional.ofNullable(organisations.get(0)));
    }

    @Test
    public void testAssessmentDetails() throws Exception {
        String SCORE_INPUT_TYPE = "assessor_score";
        AssessmentResource assessment = newAssessmentResource().withId(1L).withProcessRole(0L).build();
        ProcessRoleResource processRole = newProcessRoleResource().with(id(0L)).withApplication(1L).build();
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
        List<QuestionResource> questions = QuestionResourceBuilder.newQuestionResource().withId(32L, 33L, 1L, 20L, 21L, 22L, 23L, 10L, 30L, 31L).build(10);

        ApplicationResource app = applications.get(0);
        Set<Long> sections = newHashSet(1L, 2L);
        Map<Long, Set<Long>> mappedSections = new HashMap<>();
        mappedSections.put(organisations.get(0).getId(), sections);
        when(competitionService.getById(app.getCompetition())).thenReturn(competition);
        when(sectionService.getCompletedSectionsByOrganisation(anyLong())).thenReturn(mappedSections);
        when(applicationService.getById(app.getId())).thenReturn(app);
        when(assessmentService.getById(assessment.getId())).thenReturn(assessment);
        when(processRoleService.getById(assessment.getProcessRole())).thenReturn(settable(processRole));
        when(assessorFormInputResponseService.getAllAssessorFormInputResponses(assessment.getId())).thenReturn(assessorResponses);
        when(formInputService.findAssessmentInputsByQuestion(anyLong())).thenReturn(formInputs);
        Map<Long, AssessmentOverviewRowViewModel> assessorResponsesMap = new HashMap<>();
        questions.forEach(question -> {
            assessorResponsesMap.put(question.getId(),new AssessmentOverviewRowViewModel(question,formInputs,assessorResponses));
        });

        FileEntryResource fileEntry = newFileEntryResource().build();
        FormInputResource formInput = newFormInputResource().withId(1L).build();
        setupFormInputAndFileEntry(fileEntry, formInput, app);
        List<AppendixResource> appendices = setUpAppendices(fileEntry, formInput, app);

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
    public void testAssessmentFinance() throws Exception {
        AssessmentResource assessment = newAssessmentResource().withId(1L).withProcessRole(0L).build();
        ProcessRoleResource processRole = newProcessRoleResource().with(id(0L)).withApplication(1L).build();
        CompetitionResource competition = newCompetitionResource()
                .withId(1L)
                .withAssessmentStartDate(LocalDateTime.now().minusDays(2))
                .withAssessmentEndDate(LocalDateTime.now().plusDays(4))
                .build();
        ApplicationResource app = applications.get(0);
        when(competitionService.getById(app.getCompetition())).thenReturn(competition);
        when(applicationService.getById(app.getId())).thenReturn(app);
        when(assessmentService.getById(assessment.getId())).thenReturn(assessment);
        when(processRoleService.getById(assessment.getProcessRole())).thenReturn(settable(processRole));

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
    public void testRejectInvitation() throws Exception {
        final Long assessmentId = 1L;
        final String reason = "reason";
        final String comment = "comment";

        when(assessmentService.rejectInvitation(assessmentId, reason, comment)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/{assessmentId}/reject", assessmentId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("rejectReason", reason)
                .param("rejectComment", comment))
                .andExpect(status().isOk())
                .andExpect(view().name("assessor-dashboard"))
                .andReturn();

        verify(assessmentService, only()).rejectInvitation(assessmentId, reason, comment);
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

    private List<AppendixResource> setUpAppendices(FileEntryResource fileEntry, FormInputResource formInput, ApplicationResource app) {
        return asList(new AppendixResource(app.getId(), formInput.getId(), "test", fileEntry));
    }
}
