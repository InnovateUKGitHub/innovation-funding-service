package com.worth.ifs.application.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.application.builder.QuestionResourceBuilder;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.application.resource.SectionResource;
import com.worth.ifs.competition.builder.CompetitionBuilder;
import com.worth.ifs.competition.builder.CompetitionResourceBuilder;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.form.resource.FormInputResource;
import com.worth.ifs.form.service.FormInputService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static com.worth.ifs.application.builder.SectionResourceBuilder.newSectionResource;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static com.worth.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;


public class SectionServiceImplTest extends BaseUnitTestMocksTest {


    @InjectMocks
    private SectionService service = new SectionServiceImpl();
    @Mock
    SectionRestService sectionRestService;
    @Mock
    QuestionService questionService;
    @Mock
    FormInputService formInputService;

    private SectionResource parentSection;
    private SectionResource childSection1;
    private FormInputResource formInputResource1;
    private FormInputResource formInputResource2;

    @Before
    public void setUp() {
        super.setUp();

        ArrayList<SectionResource> sections = new ArrayList<>();
        CompetitionResource competition = CompetitionResourceBuilder.newCompetitionResource().build();
        parentSection = newSectionResource().withId(10L).withCompetition(competition.getId()).build();
        childSection1 = newSectionResource().withId(20L).withCompetition(competition.getId()).withParentSection(parentSection.getId()).build();
        formInputResource1 = newFormInputResource().withFormInputTypeTitle("empty").build();
        formInputResource2 = newFormInputResource().withFormInputTypeTitle("textarea").build();


        parentSection.setChildSections(asList(childSection1.getId()));

        when(sectionRestService.getById(eq(childSection1.getId()))).thenReturn(restSuccess(childSection1));
        when(sectionRestService.getById(eq(parentSection.getId()))).thenReturn(restSuccess(parentSection));

        when(sectionRestService.getByCompetition(anyLong())).thenReturn(restSuccess(asList(parentSection,childSection1)));

        QuestionResource question1 = QuestionResourceBuilder.newQuestionResource().withFormInputs(Arrays.asList(1L)).build();
        when(questionService.getById(eq(question1.getId()))).thenReturn(question1);

        QuestionResource question2 = QuestionResourceBuilder.newQuestionResource().withFormInputs(Arrays.asList(2L)).build();
        when(questionService.getById(eq(question2.getId()))).thenReturn(question2);

        childSection1.setQuestions(Arrays.asList(question1.getId(), question2.getId()));
        when(questionService.findByCompetition(childSection1.getCompetition())).thenReturn(asList(question1, question2));
        when(formInputService.getOne(1L)).thenReturn(formInputResource1);
        when(formInputService.getOne(2L)).thenReturn(formInputResource2);

        when(formInputService.findByCompetitionId(anyLong())).thenReturn(asList(formInputResource1, formInputResource2));
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
