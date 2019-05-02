package org.innovateuk.ifs.application.summary.populator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.application.service.SectionRestService;
import org.innovateuk.ifs.application.summary.ApplicationSummaryData;
import org.innovateuk.ifs.application.summary.ApplicationSummarySettings;
import org.innovateuk.ifs.application.summary.viewmodel.ApplicationRowGroupSummaryViewModel;
import org.innovateuk.ifs.application.summary.viewmodel.ApplicationRowSummaryViewModel;
import org.innovateuk.ifs.application.summary.viewmodel.ApplicationRowsSummaryViewModel;
import org.innovateuk.ifs.application.summary.viewmodel.FinanceSummaryViewModel;
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
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.AsyncTestExpectationHelper.setupAsyncExpectations;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static org.innovateuk.ifs.application.builder.QuestionStatusResourceBuilder.newQuestionStatusResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationRowsSummaryViewModelPopulatorTest {

    @InjectMocks
    private ApplicationRowsSummaryViewModelPopulator populator;

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
    private FinanceSummaryViewModelPopulator financeSummaryViewModelPopulator;

    @Mock
    private QuestionStatusRestService questionStatusRestService;

    @Mock
    private OrganisationRestService organisationRestService;

    @Mock
    private AsyncFuturesGenerator futuresGeneratorMock;

    @Mock
    private List<QuestionSummaryViewModelPopulator<?>> mocklist;

    @Before
    public void setupExpectations() {
        setupAsyncExpectations(futuresGeneratorMock);
    }

    @Test
    public void populate() {
        long applicationId = 1L;
        UserResource user = newUserResource().build();
        ApplicationSummarySettings settings = ApplicationSummarySettings.defaultSettings().setIncludeQuestionLinks(true).setIncludeStatuses(true);

        QuestionSummaryViewModelPopulator mockPopulator = mock(QuestionSummaryViewModelPopulator.class);
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
        List<QuestionStatusResource> questionStatuses = newQuestionStatusResource().build(1);
        OrganisationResource organisation = newOrganisationResource().build();
        List<SectionResource> sections = newSectionResource()
                .withName("Section with questions", "Finance section")
                .withChildSections(Collections.emptyList(), Collections.singletonList(1L))
                .withQuestions(questions.stream().map(QuestionResource::getId).collect(Collectors.toList()), emptyList())
                .build(2);

        ApplicationSummaryData expectedData = new ApplicationSummaryData(application, competition, user, questions, formInputs, responses, questionStatuses);
        ApplicationRowSummaryViewModel expectedRowModel = mock(ApplicationRowSummaryViewModel.class);
        FinanceSummaryViewModel expectedFinanceSummary = mock(FinanceSummaryViewModel.class);

        when(financeSummaryViewModelPopulator.populate(expectedData)).thenReturn(expectedFinanceSummary);
        when(applicationRestService.getApplicationById(applicationId)).thenReturn(restSuccess(application));
        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(questionRestService.findByCompetition(competition.getId())).thenReturn(restSuccess(questions));
        when(formInputRestService.getByCompetitionId(competition.getId())).thenReturn(restSuccess(formInputs));
        when(formInputResponseRestService.getResponsesByApplicationId(application.getId())).thenReturn(restSuccess(responses));
        when(organisationRestService.getByUserAndApplicationId(user.getId(), applicationId)).thenReturn(restSuccess(organisation));
        when(questionStatusRestService.findByApplicationAndOrganisation(applicationId, organisation.getId())).thenReturn(restSuccess(questionStatuses));
        when(sectionRestService.getByCompetition(competition.getId())).thenReturn(restSuccess(sections));
        when(mockPopulator.populate(questions.get(0), expectedData)).thenReturn(expectedRowModel);

        ApplicationRowsSummaryViewModel viewModel = populator.populate(applicationId, user, settings);

        assertEquals(viewModel.getSettings(), settings);
        assertEquals(viewModel.getSections().size(), 2);

        Iterator<ApplicationRowGroupSummaryViewModel> iterator = viewModel.getSections().iterator();
        ApplicationRowGroupSummaryViewModel sectionWithQuestion = iterator.next();

        assertEquals(sectionWithQuestion.getName(), "Section with questions");
        assertEquals(sectionWithQuestion.getQuestions().iterator().next(), expectedRowModel);

        ApplicationRowGroupSummaryViewModel financeSection = iterator.next();
        assertEquals(financeSection.getName(), "Finance section");
        assertEquals(financeSection.getQuestions().iterator().next(), expectedFinanceSummary);

        verify(mockPopulator).populate(questions.get(0), expectedData);
    }
}
