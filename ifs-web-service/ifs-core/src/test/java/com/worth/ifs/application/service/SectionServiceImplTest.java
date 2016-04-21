package com.worth.ifs.application.service;

import java.util.ArrayList;
import java.util.List;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.application.builder.QuestionBuilder;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.Section;
import com.worth.ifs.application.resource.SectionResource;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.competition.builder.CompetitionBuilder;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.form.builder.FormInputBuilder;
import com.worth.ifs.form.domain.FormInput;
import com.worth.ifs.form.domain.FormInputType;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class SectionServiceImplTest extends BaseUnitTestMocksTest {


    @InjectMocks
    private SectionService service = new SectionServiceImpl();
    @Mock
    SectionRestService sectionRestService;
    @Mock
    QuestionService questionService;

    private SectionResource parentSection;
    private SectionResource childSection1;

    @Before
    public void setUp() {
        super.setUp();

        ArrayList<Section> sections = new ArrayList<>();
        Competition competition = CompetitionBuilder.newCompetition().build();
        parentSection = new SectionResource(10L, competition, new ArrayList<>(), "ParentSection", null);
        childSection1 = new SectionResource(20L, competition, new ArrayList<>(), "childSection1", parentSection.getId());

        parentSection.setChildSections(asList(childSection1.getId()));

        when(sectionRestService.getById(eq(childSection1.getId()))).thenReturn(RestResult.restSuccess(childSection1));
        when(sectionRestService.getById(eq(parentSection.getId()))).thenReturn(RestResult.restSuccess(parentSection));

        FormInputType formInputType1 = new FormInputType(1L, "empty");
        FormInput formInputs1 = FormInputBuilder.newFormInput().withFormInputType(formInputType1).build();
        Question question1 = QuestionBuilder.newQuestion().withFormInputs(asList(formInputs1)).build();

        when(questionService.getById(eq(question1.getId()))).thenReturn(question1);

        FormInputType formInputType2 = new FormInputType(2L, "Something");
        FormInput formInputs2 = FormInputBuilder.newFormInput().withFormInputType(formInputType2).build();
        Question question2 = QuestionBuilder.newQuestion().withFormInputs(asList(formInputs2)).build();
        when(questionService.getById(eq(question2.getId()))).thenReturn(question2);

        when(questionService.findByCompetition(competition.getId())).thenReturn(asList(question1, question2));
        childSection1.setQuestions(asList(question1.getId(), question2.getId()));
    }

    @Test
    public void testFilterParentSections() throws Exception {
        List<SectionResource> parentSections = service.filterParentSections(asList(parentSection, childSection1));
        assertEquals(parentSection.getId(), parentSections.get(0).getId());
    }

    @Test
    public void testRemoveSectionsQuestionsWithType() throws Exception {
        assertEquals(2, childSection1.getQuestions().size());
        service.removeSectionsQuestionsWithType(parentSection, "empty");
        assertEquals(1, childSection1.getQuestions().size());
    }
}