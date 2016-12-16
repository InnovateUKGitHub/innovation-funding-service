package org.innovateuk.ifs.application.model;

import org.innovateuk.ifs.application.builder.QuestionResourceBuilder;
import org.innovateuk.ifs.application.builder.SectionResourceBuilder;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

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
        Model model = mock(Model.class);
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

        target.addNavigation(section, applicationId, model);

        verify(model).addAttribute(eq("previousUrl"), contains(previousQuestion.getId().toString()));
        verify(model).addAttribute(eq("previousText"), contains(previousQuestion.getShortName()));
        verify(model).addAttribute(eq("nextUrl"), contains(nextQuestion.getId().toString()));
        verify(model).addAttribute(eq("nextText"), contains(nextQuestion.getShortName()));
    }

    @Test
    public void testAddAppropraiteBackURLToModel(){
        Long applicationId = 1L;
        Model model = mock(Model.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("referer")).thenReturn("/application/1");
        target.addAppropriateBackURLToModel(applicationId, request, model);
        verify(model).addAttribute(eq("backURL"), contains("/application/1"));

        when(request.getHeader("referer")).thenReturn("/application/1/summary");
        target.addAppropriateBackURLToModel(applicationId, request, model);
        verify(model).addAttribute(eq("backURL"), contains("/application/1/summary"));
    }

}
