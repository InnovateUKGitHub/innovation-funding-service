package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.viewmodel.NavigationViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationNavigationPopulatorTest {

    @InjectMocks
    private ApplicationNavigationPopulator target;

    @Mock
    private QuestionService questionService;

    @Mock
    private SectionService sectionService;

    @Mock
    private ApplicationService applicationService;

    @Before
    public void setUp() {
        reset(questionService, sectionService, applicationService);
    }

    @Test
    public void addNavigation() {
        SectionResource section = newSectionResource().build();
        Long applicationId = 1L;
        String previousName = "prev";
        String nextName = "next";
        QuestionResource previousQuestion = newQuestionResource()
                .withShortName(previousName).build();
        QuestionResource nextQuestion = newQuestionResource()
                .withShortName(nextName).build();
        SectionResource previousSection = newSectionResource().withQuestionGroup(false).build();
        SectionResource nextSection = newSectionResource().withQuestionGroup(false).build();
        when(questionService.getPreviousQuestionBySection(section.getId())).thenReturn(Optional.of(previousQuestion));
        when(questionService.getNextQuestionBySection(section.getId())).thenReturn(Optional.of(nextQuestion));
        when(sectionService.getSectionByQuestionId(previousQuestion.getId())).thenReturn(previousSection);
        when(sectionService.getSectionByQuestionId(nextQuestion.getId())).thenReturn(nextSection);

        NavigationViewModel result = target.addNavigation(section, applicationId);

        assertTrue(result.getPreviousUrl().contains(previousQuestion.getId().toString()));
        assertEquals(previousQuestion.getShortName(), result.getPreviousText());
        assertTrue(result.getNextUrl().contains(nextQuestion.getId().toString()));
        assertEquals(nextQuestion.getShortName(), result.getNextText());
    }

    @Test
    public void addAppropriateBackURLToModelWithoutSection(){
        Long applicationId = 1L;
        Model model = mock(Model.class);

        setupApplicationOpen(applicationId);

        target.addAppropriateBackURLToModel(applicationId, model, null, Optional.empty(), Optional.empty(), false);
        verify(model).addAttribute(eq("backURL"), contains("/application/1"));
        verify(model).addAttribute(eq("backTitle"), eq("Application overview"));
    }

    @Test
    public void addAppropriateBackURLToModelWithoutSectionClosedApplication(){
        Long applicationId = 1L;
        Model model = mock(Model.class);

        setupApplicationClosed(applicationId);

        target.addAppropriateBackURLToModel(applicationId, model, null, Optional.empty(), Optional.empty(), false);
        verify(model).addAttribute(eq("backURL"), contains("/application/1/summary"));
        verify(model).addAttribute(eq("backTitle"), contains("Application summary"));
    }

    @Test
    public void addAppropriateBackURLToModelWithoutSectionClosedCompetition(){
        Long applicationId = 1L;
        Model model = mock(Model.class);

        setupApplicationCompetitionClosed(applicationId);
        target.addAppropriateBackURLToModel(applicationId, model, null, Optional.empty(), Optional.empty(), false);
        verify(model).addAttribute(eq("backURL"), contains("/application/1"));
        verify(model).addAttribute(eq("backTitle"), contains("Application overview"));
    }

    @Test
    public void addAppropriateBackURLToModelWithFinanceSubSection(){
        Long applicationId = 1L;
        Model model = mock(Model.class);

        setupApplicationOpen(applicationId);

        SectionResource section = newSectionResource().withType(SectionType.FUNDING_FINANCES).build();
        target.addAppropriateBackURLToModel(applicationId, model, section, Optional.empty(), Optional.empty(), false);

        verify(model).addAttribute(eq("backURL"), contains("/application/1/form/FINANCE"));
        verify(model).addAttribute(eq("backTitle"), contains("Your finances"));
    }

    @Test
    public void addAppropriateBackURLToModelWithGeneralSection(){
        Long applicationId = 1L;
        Model model = mock(Model.class);
        SectionResource section = newSectionResource().withType(SectionType.GENERAL).build();
        setupApplicationOpen(applicationId);

        target.addAppropriateBackURLToModel(applicationId, model, section, Optional.empty(), Optional.empty(), false);

        verify(model).addAttribute(eq("backURL"), contains("/application/1"));
        verify(model).addAttribute(eq("backTitle"), eq("Application overview"));
    }

    @Test
    public void navigationSkipsExcludedSectionTypesWithQuestionGroup() {
        long applicationId = 1L;
        SectionResource section = newSectionResource().withId(1L).build();

        QuestionResource previousQuestion = newQuestionResource().withId(1L).build();
        QuestionResource nextQuestion = newQuestionResource().withId(2L).build();

        List<Long> projectCostQuestions = asList(1L, 2L);

        SectionResource previousSection =
                newSectionResource()
                        .withId(8L)
                        .withType(SectionType.ORGANISATION_FINANCES)
                        .withQuestionGroup(true)
                        .withQuestions(projectCostQuestions)
                        .withName("foo")
                        .build();

        SectionResource nextSection =
                newSectionResource()
                        .withId(9L)
                        .withType(SectionType.ORGANISATION_FINANCES)
                        .withQuestionGroup(true)
                        .withQuestions(projectCostQuestions)
                        .withName("bar")
                        .build();

        when(questionService.getPreviousQuestionBySection(section.getId())).thenReturn(Optional.of(previousQuestion));
        when(questionService.getNextQuestionBySection(section.getId())).thenReturn(Optional.of(nextQuestion));
        when(sectionService.getSectionByQuestionId(1L)).thenReturn(previousSection);
        when(sectionService.getSectionByQuestionId(2L)).thenReturn(nextSection);

        NavigationViewModel result = target.addNavigation(section, applicationId);

        assertTrue(result.getPreviousUrl().contains("/section/" + previousSection.getId()));
        assertEquals(previousSection.getName(), result.getPreviousText());
        assertTrue(result.getNextUrl().contains("/section/" + nextSection.getId()));
        assertEquals(nextSection.getName(), result.getNextText());

        InOrder inOrder = inOrder(questionService, sectionService);
        inOrder.verify(questionService).getPreviousQuestionBySection(section.getId());
        inOrder.verify(questionService).getNextQuestionBySection(section.getId());
        inOrder.verify(sectionService).getSectionByQuestionId(anyLong());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void navigationSkipsExcludedSectionTypesWithNoQuestionGroup() {
        long applicationId = 1L;
        SectionResource section = newSectionResource().withId(1L).build();

        QuestionResource previousQuestion = newQuestionResource().withId(7L).withShortName("foo").build();
        QuestionResource nextQuestion = newQuestionResource().withId(11L).withShortName("bar").build();

        List<Long> projectCostQuestions = asList(1L,2L);

        SectionResource previousSection = newSectionResource()
                .withId(3L)
                .withType(SectionType.ORGANISATION_FINANCES)
                .withQuestionGroup(false)
                .withQuestions(projectCostQuestions)
                .build();

        SectionResource nextSection = newSectionResource()
                .withId(5L)
                .withType(SectionType.ORGANISATION_FINANCES)
                .withQuestionGroup(false)
                .withQuestions(projectCostQuestions)
                .build();

        when(questionService.getPreviousQuestionBySection(section.getId())).thenReturn(Optional.of(previousQuestion));
        when(questionService.getNextQuestionBySection(section.getId())).thenReturn(Optional.of(nextQuestion));
        when(sectionService.getSectionByQuestionId(previousQuestion.getId())).thenReturn(previousSection);
        when(sectionService.getSectionByQuestionId(nextQuestion.getId())).thenReturn(nextSection);

        NavigationViewModel result = target.addNavigation(section, applicationId);

        assertTrue(result.getPreviousUrl().contains("/question/" + previousQuestion.getId()));
        assertEquals(previousQuestion.getShortName(), result.getPreviousText());
        assertTrue(result.getNextUrl().contains("/question/" + nextQuestion.getId()));
        assertEquals(nextQuestion.getShortName(), result.getNextText());

        InOrder inOrder = inOrder(questionService, sectionService);
        inOrder.verify(questionService).getPreviousQuestionBySection(section.getId());
        inOrder.verify(questionService).getNextQuestionBySection(section.getId());
        inOrder.verify(sectionService).getSectionByQuestionId(nextQuestion.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void addAppropriateBackURLToModelWithApplicantOrganisation(){
        Long applicationId = 1L;
        Model model = mock(Model.class);
        SectionResource section = newSectionResource().withCompetition(11L).withType(SectionType.OVERVIEW_FINANCES).build();

        setupApplicationOpen(applicationId);

        target.addAppropriateBackURLToModel(applicationId, model, section, Optional.of(22L), Optional.empty(), false);
        verify(model).addAttribute(eq("backURL"), contains("application/" + applicationId + "/summary"));
        verify(model).addAttribute(eq("backTitle"), eq("Application summary"));
    }

    @Test
    public void addAppropriateBackURLToModelWithFinanceSubSectionWithApplicantOrganisation(){
        Long applicationId = 1L;
        Model model = mock(Model.class);

        setupApplicationOpen(applicationId);

        SectionResource section = newSectionResource().withType(SectionType.FUNDING_FINANCES).withParentSection(33L).build();
        target.addAppropriateBackURLToModel(applicationId, model, section, Optional.of(22L), Optional.empty(), false);

        verify(model).addAttribute(eq("backURL"), contains("/application/1/form/section/33/22"));
        verify(model).addAttribute(eq("backTitle"), contains("Your finances"));
    }

    private void setupApplicationOpen(Long applicationId) {
        when(applicationService.getById(applicationId)).thenReturn(newApplicationResource()
                .withApplicationState(ApplicationState.OPEN)
                .withCompetitionStatus(CompetitionStatus.OPEN)
                .build());
    }

    private void setupApplicationClosed(Long applicationId) {
        when(applicationService.getById(applicationId)).thenReturn(newApplicationResource()
                .withApplicationState(ApplicationState.SUBMITTED)
                .withCompetitionStatus(CompetitionStatus.OPEN)
                .build());
    }

    private void setupApplicationCompetitionClosed(Long applicationId) {
        when(applicationService.getById(applicationId)).thenReturn(newApplicationResource()
                .withApplicationState(ApplicationState.OPEN)
                .withCompetitionStatus(CompetitionStatus.IN_ASSESSMENT)
                .build());
    }
}
