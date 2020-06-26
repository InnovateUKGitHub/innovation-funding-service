package org.innovateuk.ifs.application.readonly.populator;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings;
import org.innovateuk.ifs.application.readonly.viewmodel.ApplicationQuestionReadOnlyViewModel;
import org.innovateuk.ifs.application.readonly.viewmodel.ApplicationReadOnlyViewModel;
import org.innovateuk.ifs.application.readonly.viewmodel.ApplicationSectionReadOnlyViewModel;
import org.innovateuk.ifs.application.readonly.viewmodel.FinanceReadOnlyViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.application.service.SectionRestService;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponseResource;
import org.innovateuk.ifs.assessment.service.AssessorFormInputResponseRestService;
import org.innovateuk.ifs.async.generation.AsyncFuturesGenerator;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.AsyncTestExpectationHelper.setupAsyncExpectations;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static org.innovateuk.ifs.application.builder.QuestionStatusResourceBuilder.newQuestionStatusResource;
import static org.innovateuk.ifs.assessment.builder.AssessorFormInputResponseResourceBuilder.newAssessorFormInputResponseResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.assertEquals;
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
        setField(populator, "populatorMap", asMap(QuestionSetupType.APPLICATION_TEAM, mockPopulator));
        setField(populator, "asyncFuturesGenerator", futuresGeneratorMock);


        CompetitionResource competition = newCompetitionResource().build();
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
                .withName("Section with questions", "Finance section")
                .withChildSections(Collections.emptyList(), Collections.singletonList(1L))
                .withQuestions(questions.stream().map(QuestionResource::getId).collect(Collectors.toList()), emptyList())
                .build(2);
        List<AssessorFormInputResponseResource> assessorFormInputResponseResources = newAssessorFormInputResponseResource().withQuestion(1L).build(1);
        ProcessRoleResource processRole = newProcessRoleResource().build();

        ApplicationReadOnlyData expectedData = new ApplicationReadOnlyData(application, competition, user, Optional.of(processRole), questions, formInputs, responses, questionStatuses, assessorFormInputResponseResources);
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
        when(userRestService.findProcessRole(user.getId(), application.getId())).thenReturn(restSuccess(processRole));
        when(assessorFormInputResponseRestService.getAllAssessorFormInputResponses(assessmentId)).thenReturn(restSuccess(assessorFormInputResponseResources));
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
    }
}
