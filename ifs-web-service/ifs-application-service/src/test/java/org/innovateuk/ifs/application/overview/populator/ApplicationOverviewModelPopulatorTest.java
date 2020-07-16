package org.innovateuk.ifs.application.overview.populator;

import org.innovateuk.ifs.application.overview.viewmodel.ApplicationOverviewRowViewModel;
import org.innovateuk.ifs.application.overview.viewmodel.ApplicationOverviewSectionViewModel;
import org.innovateuk.ifs.application.overview.viewmodel.ApplicationOverviewViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.service.*;
import org.innovateuk.ifs.async.generation.AsyncFuturesGenerator;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
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
import org.springframework.context.MessageSource;

import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.primitives.Longs.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Comparator.comparing;
import static org.innovateuk.ifs.AsyncTestExpectationHelper.setupAsyncExpectations;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.QuestionStatusResourceBuilder.newQuestionStatusResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.CollaborationLevel.SINGLE;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.ASSESSED_QUESTION;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ApplicationOverviewModelPopulatorTest {

    @InjectMocks
    private ApplicationOverviewModelPopulator populator;

    @Mock
    private CompetitionRestService competitionRestService;
    @Mock
    private SectionRestService sectionRestService;
    @Mock
    private QuestionRestService questionRestService;
    @Mock
    private UserRestService userRestService;
    @Mock
    private MessageSource messageSource;
    @Mock
    private OrganisationRestService organisationRestService;
    @Mock
    private QuestionStatusRestService questionStatusRestService;
    @Mock
    private SectionStatusRestService sectionStatusRestService;
    @Mock
    private QuestionService questionService;
    @Mock
    private AsyncFuturesGenerator asyncFuturesGenerator;

    @Before
    public void setupExpectations() {
        setupAsyncExpectations(asyncFuturesGenerator);
    }

    @Test
    public void populateModel() {
        CompetitionResource competition = newCompetitionResource()
                .withCollaborationLevel(SINGLE)
                .build();
        ApplicationResource application = newApplicationResource()
                .withCompetition(competition.getId())
                .build();
        List<QuestionResource> questions = newQuestionResource()
                .withShortName("A question")
                .withQuestionSetupType(ASSESSED_QUESTION)
                .withQuestionNumber("4")
                .build(1);
        UserResource user = newUserResource().build();
        List<ProcessRoleResource> processRoles = newProcessRoleResource()
                .withUser(user, newUserResource().build())
                .withRole(Role.LEADAPPLICANT, Role.COLLABORATOR)
                .build(2);
        List<QuestionStatusResource> questionStatuses = newQuestionStatusResource()
                .withQuestion(questions.get(0).getId())
                .withMarkedAsComplete(false)
                .withAssignee(processRoles.get(1).getId())
                .build(1);
        OrganisationResource organisation = newOrganisationResource().build();

        SectionResource childSection = newSectionResource()
                .withName("Child finance")
                .withPriority(3)
                .build();
        List<SectionResource> sections = newSectionResource()
                .withPriority(1, 2)
                .withName("Section with questions", "Finances")
                .withDescription("Section with questions description", "")
                .withChildSections(Collections.emptyList(), Collections.singletonList(childSection.getId()))
                .withQuestions(questions.stream().map(QuestionResource::getId).collect(Collectors.toList()), emptyList())
                .build(2);
        sections.add(childSection);
        childSection.setParentSection(sections.get(1).getId());
        
        Map<Long, Set<Long>> completedSectionsByOrganisation = emptyMap();

        when(organisationRestService.getByUserAndApplicationId(user.getId(), application.getId())).thenReturn(restSuccess(organisation));
        when(competitionRestService.getCompetitionById(application.getCompetition())).thenReturn(restSuccess(competition));
        when(sectionRestService.getByCompetition(application.getCompetition())).thenReturn(restSuccess(sections));
        when(questionRestService.findByCompetition(application.getCompetition())).thenReturn(restSuccess(questions));
        when(userRestService.findProcessRole(application.getId())).thenReturn(restSuccess(processRoles));
        when(questionStatusRestService.findByApplicationAndOrganisation(application.getId(), organisation.getId())).thenReturn(restSuccess(questionStatuses));
        when(sectionStatusRestService.getCompletedSectionIds(application.getId(), organisation.getId())).thenReturn(restSuccess(asList(sections.get(1).getId(), childSection.getId())));
        when(questionService.getNotificationsForUser(questionStatuses, user.getId())).thenReturn(questionStatuses);
        when(messageSource.getMessage("ifs.section.finances.description", null, Locale.getDefault())).thenReturn("Finance description");
        when(sectionStatusRestService.getCompletedSectionsByOrganisation(application.getId())).thenReturn(restSuccess(completedSectionsByOrganisation));

        ApplicationOverviewViewModel viewModel = populator.populateModel(application, user);

        assertEquals(application, viewModel.getApplication());
        assertEquals(competition, viewModel.getCompetition());
        assertEquals(processRoles.get(0), viewModel.getProcessRole());
        assertTrue(viewModel.isLead());

        assertEquals(2, viewModel.getSections().size());

        Iterator<ApplicationOverviewSectionViewModel> sectionIterator = viewModel.getSections()
                .stream()
                .sorted(comparing(ApplicationOverviewSectionViewModel::getId))
                .iterator();

        ApplicationOverviewSectionViewModel sectionWithQuestions = sectionIterator.next();
        assertEquals("Section with questions", sectionWithQuestions.getTitle());
        assertEquals("Section with questions description", sectionWithQuestions.getSubTitle());
        assertEquals((long) sections.get(0).getId(), sectionWithQuestions.getId());
        assertEquals(1, sectionWithQuestions.getRows().size());

        ApplicationOverviewRowViewModel questionRow = sectionWithQuestions.getRows().iterator().next();
        assertEquals("4. A question", questionRow.getTitle());
        assertEquals(String.format("/application/%d/form/question/%d/generic", application.getId(), questions.get(0).getId()), questionRow.getUrl());
        assertEquals(false, questionRow.isComplete());
        assertEquals(processRoles.get(1), questionRow.getAssignButtonsViewModel().get().getAssignee());
        assertEquals(processRoles, questionRow.getAssignButtonsViewModel().get().getAssignableApplicants());

        ApplicationOverviewSectionViewModel sectionWithChildSections = sectionIterator.next();
        assertEquals("Finances", sectionWithChildSections.getTitle());
        assertEquals("Finance description", sectionWithChildSections.getSubTitle());
        assertEquals((long) sections.get(1).getId(), sectionWithChildSections.getId());

        ApplicationOverviewRowViewModel childSectionRow = sectionWithChildSections.getRows().iterator().next();
        assertEquals("Child finance", childSectionRow.getTitle());
        assertEquals(String.format("/application/%d/form/section/%d", application.getId(), childSection.getId()), childSectionRow.getUrl());
        assertEquals(true, childSectionRow.isComplete());
        assertFalse(childSectionRow.getAssignButtonsViewModel().isPresent());

        verify(questionService).removeNotifications(questionStatuses);
    }
}
