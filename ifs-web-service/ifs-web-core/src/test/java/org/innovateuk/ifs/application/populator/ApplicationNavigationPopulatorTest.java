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
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.innovateuk.ifs.application.builder.SectionResourceBuilder.newSectionResource;
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

}
