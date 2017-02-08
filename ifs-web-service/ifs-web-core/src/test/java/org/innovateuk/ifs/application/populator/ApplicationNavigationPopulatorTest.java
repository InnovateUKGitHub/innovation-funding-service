package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.application.builder.QuestionResourceBuilder;
import org.innovateuk.ifs.application.builder.SectionResourceBuilder;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.resource.SectionType;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.viewmodel.NavigationViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.application.builder.SectionResourceBuilder.newSectionResource;
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

    @Test
    public void testAddNavigation() {
        SectionResource section = SectionResourceBuilder.newSectionResource().build();
        Long applicationId = 1L;
        String previousName = "prev";
        String nextName = "next";
        QuestionResource previousQuestion = QuestionResourceBuilder.newQuestionResource()
                .withShortName(previousName).build();
        QuestionResource nextQuestion = QuestionResourceBuilder.newQuestionResource()
                .withShortName(nextName).build();
        SectionResource previousSection = SectionResourceBuilder.newSectionResource().withQuestionGroup(false).build();
        SectionResource nextSection = SectionResourceBuilder.newSectionResource().withQuestionGroup(false).build();
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
    public void testAddAppropriateBackURLToModelWithoutSection(){
        Long applicationId = 1L;
        Model model = mock(Model.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("referer")).thenReturn("/application/1");
        target.addAppropriateBackURLToModel(applicationId, request, model, null);
        verify(model).addAttribute(eq("backURL"), contains("/application/1"));

        when(request.getHeader("referer")).thenReturn("/application/1/summary");
        target.addAppropriateBackURLToModel(applicationId, request, model, null);
        verify(model).addAttribute(eq("backURL"), contains("/application/1/summary"));

        verify(model, times(2)).addAttribute(eq("backTitle"), contains("Application Overview"));
    }

    @Test
    public void testAddAppropriateBackURLToModelWithFinanceSubSection(){
        Long applicationId = 1L;
        Model model = mock(Model.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        SectionResource section = newSectionResource().withType(SectionType.FUNDING_FINANCES).build();
        target.addAppropriateBackURLToModel(applicationId, request, model, section);

        verify(model).addAttribute(eq("backURL"), contains("/application/1/form/FINANCE"));
        verify(model).addAttribute(eq("backTitle"), contains("Your finances"));
    }

    @Test
    public void testAddAppropriateBackURLToModelWithGeneralSection(){
        Long applicationId = 1L;
        Model model = mock(Model.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("referer")).thenReturn("/application/1");
        SectionResource section = newSectionResource().withType(SectionType.GENERAL).build();
        target.addAppropriateBackURLToModel(applicationId, request, model, section);

        verify(model).addAttribute(eq("backURL"), contains("/application/1"));
        verify(model).addAttribute(eq("backTitle"), contains("Application Overview"));
    }

    @Test
    public void testNavigationSkipsExcludedSectionTypesWithQuestionGroup() {
        SectionResource section = SectionResourceBuilder.newSectionResource().withId(1L).build();
        Long applicationId = 1L;

        QuestionResource previousQuestion = QuestionResourceBuilder.newQuestionResource().build();
        QuestionResource nextQuestion = QuestionResourceBuilder.newQuestionResource().build();

        List<Long> projectCostQuestions = Arrays.asList(1L,2L);
        List<Long> organisationQuestions = Arrays.asList(2L,3L);
        List<Long> fundingQuestions = Arrays.asList(4L,5L);

        SectionResource previousSection = SectionResourceBuilder.newSectionResource().withId(2L).withType(SectionType.ORGANISATION_FINANCES).withQuestionGroup(true).withQuestions(projectCostQuestions).build();
        SectionResource nextSection = SectionResourceBuilder.newSectionResource().withId(3L).withType(SectionType.ORGANISATION_FINANCES).withQuestionGroup(true).withQuestions(organisationQuestions).build();
        SectionResource validPreviousSection = SectionResourceBuilder.newSectionResource().withName("Section 4").withId(4L).withType(SectionType.PROJECT_COST_FINANCES).withQuestionGroup(true).withQuestions(fundingQuestions).build();
        SectionResource validNextSection = SectionResourceBuilder.newSectionResource().withName("Section 5").withId(5L).withType(SectionType.FUNDING_FINANCES).withQuestionGroup(true).withQuestions(organisationQuestions).build();

        SectionResource sectionToSkip = SectionResourceBuilder.newSectionResource().withType(SectionType.ORGANISATION_FINANCES).build();

        when(questionService.getPreviousQuestionBySection(section.getId())).thenReturn(Optional.of(previousQuestion));
        when(questionService.getPreviousQuestion(anyLong())).thenReturn(Optional.of(previousQuestion));
        when(questionService.getNextQuestionBySection(section.getId())).thenReturn(Optional.of(nextQuestion));
        when(sectionService.getSectionByQuestionId(anyLong())).thenReturn(previousSection).thenReturn(validPreviousSection).thenReturn(nextSection).thenReturn(validNextSection);
        when(questionService.getNextQuestion(anyLong())).thenReturn(Optional.of(nextQuestion));

        target.addSectionTypeToSkip(sectionToSkip.getType());

        NavigationViewModel result = target.addNavigation(section, applicationId);

        assertTrue(result.getPreviousUrl().contains("/section/4"));
        assertEquals("Section 4", result.getPreviousText());
        assertTrue(result.getNextUrl().contains("/section/5"));
        assertEquals("Section 5", result.getNextText());
    }

    @Test
    public void testNavigationSkipsExcludedSectionTypesWithNoQuestionGroup() {
        SectionResource section = SectionResourceBuilder.newSectionResource().withId(1L).build();
        Long applicationId = 1L;

        QuestionResource previousQuestion = QuestionResourceBuilder.newQuestionResource().withId(1L).build();
        QuestionResource nextQuestion = QuestionResourceBuilder.newQuestionResource().withId(2L).build();
        QuestionResource previousValidQuestion = QuestionResourceBuilder.newQuestionResource().withId(3L).withShortName("Question 3").build();
        QuestionResource nextValidQuestion = QuestionResourceBuilder.newQuestionResource().withId(4L).withShortName("Question 4").build();

        List<Long> projectCostQuestions = Arrays.asList(1L,2L);
        List<Long> organisationQuestions = Arrays.asList(2L,3L);
        List<Long> fundingQuestions = Arrays.asList(4L,5L);

        SectionResource previousSection = SectionResourceBuilder.newSectionResource().withId(2L).withType(SectionType.ORGANISATION_FINANCES).withQuestionGroup(false).withQuestions(projectCostQuestions).build();
        SectionResource nextSection = SectionResourceBuilder.newSectionResource().withId(3L).withType(SectionType.ORGANISATION_FINANCES).withQuestionGroup(false).withQuestions(organisationQuestions).build();
        SectionResource validPreviousSection = SectionResourceBuilder.newSectionResource().withName("Section 4").withId(4L).withType(SectionType.PROJECT_COST_FINANCES).withQuestionGroup(false).withQuestions(fundingQuestions).build();
        SectionResource validNextSection = SectionResourceBuilder.newSectionResource().withName("Section 5").withId(5L).withType(SectionType.FUNDING_FINANCES).withQuestionGroup(false).withQuestions(organisationQuestions).build();

        SectionResource sectionToSkip = SectionResourceBuilder.newSectionResource().withType(SectionType.ORGANISATION_FINANCES).build();

        when(questionService.getPreviousQuestionBySection(section.getId())).thenReturn(Optional.of(previousQuestion));
        when(questionService.getPreviousQuestion(anyLong())).thenReturn(Optional.of(previousValidQuestion));
        when(questionService.getNextQuestionBySection(section.getId())).thenReturn(Optional.of(nextQuestion));
        when(sectionService.getSectionByQuestionId(anyLong())).thenReturn(previousSection).thenReturn(validPreviousSection).thenReturn(nextSection).thenReturn(validNextSection);
        when(questionService.getNextQuestion(anyLong())).thenReturn(Optional.of(nextValidQuestion));

        target.addSectionTypeToSkip(sectionToSkip.getType());

        NavigationViewModel result = target.addNavigation(section, applicationId);

        assertTrue(result.getPreviousUrl().contains("/question/3"));
        assertEquals("Question 3", result.getPreviousText());
        assertTrue(result.getNextUrl().contains("/question/4"));
        assertEquals("Question 4", result.getNextText());
    }
}
