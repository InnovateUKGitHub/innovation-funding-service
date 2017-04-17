package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.builder.QuestionResourceBuilder;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.resource.SectionType;
import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.service.FormInputService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;

import static org.innovateuk.ifs.application.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.innovateuk.ifs.form.resource.FormInputType.EMPTY;
import static org.innovateuk.ifs.form.resource.FormInputType.TEXTAREA;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class SectionServiceImplTest extends BaseServiceUnitTest<SectionServiceImpl> {

    @Mock
    private SectionRestService sectionRestService;
    @Mock
    private QuestionService questionService;
    @Mock
    private FormInputService formInputService;

    private CompetitionResource competition;
    private SectionResource parentSection;
    private SectionResource childSection1;
    private FormInputResource formInputResource1;
    private FormInputResource formInputResource2;

    @Override
    protected SectionServiceImpl supplyServiceUnderTest() {
        return new SectionServiceImpl();
    }

    @Before
    public void setUp() {
        super.setUp();

        competition = CompetitionResourceBuilder.newCompetitionResource().build();
        parentSection = newSectionResource().withCompetition(competition.getId()).build();
        childSection1 = newSectionResource().withCompetition(competition.getId()).withParentSection(parentSection.getId()).build();
        formInputResource1 = newFormInputResource().withType(EMPTY).build();
        formInputResource2 = newFormInputResource().withType(TEXTAREA).build();

        parentSection.setChildSections(asList(childSection1.getId()));

        when(sectionRestService.getById(eq(childSection1.getId()))).thenReturn(restSuccess(childSection1));
        when(sectionRestService.getById(eq(parentSection.getId()))).thenReturn(restSuccess(parentSection));

        when(sectionRestService.getByCompetition(anyLong())).thenReturn(restSuccess(asList(parentSection,childSection1)));

        QuestionResource question1 = QuestionResourceBuilder.newQuestionResource().withFormInputs(singletonList(formInputResource1.getId())).build();
        when(questionService.getById(eq(question1.getId()))).thenReturn(question1);

        QuestionResource question2 = QuestionResourceBuilder.newQuestionResource().withFormInputs(singletonList(formInputResource2.getId())).build();
        when(questionService.getById(eq(question2.getId()))).thenReturn(question2);

        childSection1.setQuestions(Arrays.asList(question1.getId(), question2.getId()));
        when(questionService.findByCompetition(childSection1.getCompetition())).thenReturn(asList(question1, question2));
        when(formInputService.getOne(formInputResource1.getId())).thenReturn(formInputResource1);
        when(formInputService.getOne(formInputResource2.getId())).thenReturn(formInputResource2);

        when(formInputService.findApplicationInputsByCompetition(anyLong())).thenReturn(asList(formInputResource1, formInputResource2));
    }

    @Test
    public void testFilterParentSections() throws Exception {
        List<SectionResource> parentSections = service.filterParentSections(asList(parentSection, childSection1));
        assertEquals(parentSection.getId(), parentSections.get(0).getId());
    }

    @Test
    public void testRemoveSectionsQuestionsWithType() throws Exception {
        assertEquals(2, childSection1.getQuestions().size());
        service.removeSectionsQuestionsWithType(parentSection, EMPTY);
        assertEquals(1, childSection1.getQuestions().size());
    }
    
    @Test
    public void testGetSectionsByType() {
    	SectionResource section = newSectionResource().build();
    	when(sectionRestService.getSectionsByCompetitionIdAndType(competition.getId(), SectionType.FINANCE)).thenReturn(restSuccess(asList(section)));
    	
    	List<SectionResource> result = service.getSectionsForCompetitionByType(competition.getId(), SectionType.FINANCE);
    	
    	assertEquals(1, result.size());
    	assertEquals(section, result.get(0));
    }
    
    @Test
    public void testGetFinanceSection() {
    	SectionResource section = newSectionResource().build();
    	when(sectionRestService.getSectionsByCompetitionIdAndType(competition.getId(), SectionType.FINANCE)).thenReturn(restSuccess(asList(section)));
    	
    	SectionResource result = service.getFinanceSection(competition.getId());
    	
    	assertEquals(section, result);
    }
    
    @Test
    public void testGetOrganisationFinanceSection() {
    	SectionResource section = newSectionResource().build();
    	when(sectionRestService.getSectionsByCompetitionIdAndType(competition.getId(), SectionType.ORGANISATION_FINANCES)).thenReturn(restSuccess(asList(section)));

    	SectionResource result = service.getOrganisationFinanceSection(competition.getId());

    	assertEquals(section, result);
    }
}
