package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.builder.QuestionResourceBuilder;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.form.resource.FormInputScope.APPLICATION;
import static org.innovateuk.ifs.form.resource.FormInputType.FILEUPLOAD;
import static org.innovateuk.ifs.form.resource.FormInputType.TEXTAREA;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class SectionServiceImplTest extends BaseServiceUnitTest<SectionServiceImpl> {

    @Mock
    private SectionRestService sectionRestService;
    @Mock
    private QuestionRestService questionRestService;
    @Mock
    private FormInputRestService formInputRestService;

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
        super.setup();

        competition = CompetitionResourceBuilder.newCompetitionResource().build();
        parentSection = newSectionResource().withCompetition(competition.getId()).build();
        childSection1 = newSectionResource().withCompetition(competition.getId()).withParentSection(parentSection.getId()).build();
        formInputResource1 = newFormInputResource().withType(FILEUPLOAD).build();
        formInputResource2 = newFormInputResource().withType(TEXTAREA).build();

        parentSection.setChildSections(asList(childSection1.getId()));

        when(sectionRestService.getById(eq(childSection1.getId()))).thenReturn(restSuccess(childSection1));
        when(sectionRestService.getById(eq(parentSection.getId()))).thenReturn(restSuccess(parentSection));

        when(sectionRestService.getByCompetition(anyLong())).thenReturn(restSuccess(asList(parentSection, childSection1)));

        QuestionResource question1 = QuestionResourceBuilder.newQuestionResource().withFormInputs(singletonList(formInputResource1.getId())).build();
        when(questionRestService.findById(eq(question1.getId()))).thenReturn(restSuccess(question1));

        QuestionResource question2 = QuestionResourceBuilder.newQuestionResource().withFormInputs(singletonList(formInputResource2.getId())).build();
        when(questionRestService.findById(eq(question2.getId()))).thenReturn(restSuccess(question2));

        childSection1.setQuestions(Arrays.asList(question1.getId(), question2.getId()));
        when(questionRestService.findByCompetition(childSection1.getCompetition())).thenReturn(restSuccess(asList(question1, question2)));
        when(formInputRestService.getById(formInputResource1.getId())).thenReturn(restSuccess(formInputResource1));
        when(formInputRestService.getById(formInputResource2.getId())).thenReturn(restSuccess(formInputResource2));

        when(formInputRestService.getByCompetitionIdAndScope(isA(Long.class), eq(APPLICATION))).thenReturn(restSuccess(asList(formInputResource1, formInputResource2)));
    }

    @Test
    public void testFilterParentSections() throws Exception {
        List<SectionResource> parentSections = service.filterParentSections(asList(parentSection, childSection1));
        assertEquals(parentSection.getId(), parentSections.get(0).getId());
    }

    @Test
    public void testFilterParentSectionsToEnsureSectionsSortedByPriority() throws Exception {

        // Set the sections with random priority
        SectionResource parentSection1 = newSectionResource().withCompetition(competition.getId()).withPriority(3).build();
        SectionResource parentSection2 = newSectionResource().withCompetition(competition.getId()).withPriority(1).build();
        SectionResource parentSection3 = newSectionResource().withCompetition(competition.getId()).withPriority(2).build();

        SectionResource childSection11 = newSectionResource().withCompetition(competition.getId()).withParentSection(parentSection1.getId()).build();
        SectionResource childSection12 = newSectionResource().withCompetition(competition.getId()).withParentSection(parentSection1.getId()).build();
        SectionResource childSection21 = newSectionResource().withCompetition(competition.getId()).withParentSection(parentSection2.getId()).build();

        parentSection1.setChildSections(asList(childSection11.getId(), childSection12.getId()));
        parentSection2.setChildSections(asList(childSection21.getId(), childSection12.getId()));

        List<SectionResource> allSections = asList(parentSection1, parentSection2, parentSection3, childSection11, childSection12, childSection21);
        when(sectionRestService.getByCompetition(anyLong())).thenReturn(restSuccess(allSections));

        List<SectionResource> filterParentSections = service.filterParentSections(allSections);

        // Ensure only parent sections are filtered and they are in the order of priority in the list
        assertEquals(3, filterParentSections.size());
        assertEquals(parentSection2, filterParentSections.get(0));
        assertEquals(parentSection3, filterParentSections.get(1));
        assertEquals(parentSection1, filterParentSections.get(2));
    }

    @Test
    public void testRemoveSectionsQuestionsWithType() throws Exception {
        assertEquals(2, childSection1.getQuestions().size());
        service.removeSectionsQuestionsWithType(parentSection, FILEUPLOAD);
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
