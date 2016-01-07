package com.worth.ifs.application.transactional;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.application.domain.Section;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.mockito.InjectMocks;

import static com.worth.ifs.BuilderAmendFunctions.name;
import static com.worth.ifs.application.builder.SectionBuilder.newSection;
import static com.worth.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class SectionServiceTest extends BaseUnitTestMocksTest {
    private final Log log = LogFactory.getLog(getClass());

    @InjectMocks
    protected SectionService sectionService = new SectionServiceImpl();

    @Test
    public void findByNameTest() throws Exception {
        Section section = newSection().with(name("testname")).build();
        when(sectionRepositoryMock.findByName(section.getName())).thenReturn(section);

        assertEquals(section, sectionService.findByName(section.getName()));
    }

    @Test
    public void getNextSectionTest() throws Exception {
        Section section = newSection().withCompetitionAndPriority(newCompetition().build(), 1).build();
        Section nextSection = newSection().build();
        when(sectionRepositoryMock.findOne(section.getId())).thenReturn(section);
        when(sectionRepositoryMock.findFirstByCompetitionIdAndPriorityGreaterThanAndParentSectionIsNullOrderByPriorityAsc(
                section.getCompetition().getId(), section.getPriority()
        )).thenReturn(nextSection);

        Section returnSection = sectionService.getNextSection(section.getId());
        assertEquals(nextSection, returnSection);
    }

    @Test
    public void getPreviousSectionTest() throws Exception {
        Section section = newSection().withCompetitionAndPriority(newCompetition().build(), 1).build();
        Section previousSection = newSection().build();
        when(sectionRepositoryMock.findOne(section.getId())).thenReturn(section);
        when(sectionRepositoryMock.findFirstByCompetitionIdAndPriorityLessThanAndParentSectionIsNullOrderByPriorityDesc(
                section.getCompetition().getId(), section.getPriority()
        )).thenReturn(previousSection);

        Section returnSection = sectionService.getPreviousSection(section.getId());
        assertEquals(previousSection, returnSection);
    }

    @Test
    public void getNextSectionWitParentSectionTest() throws Exception {
        Section parentSection = newSection().build();
        Section section = newSection().withCompetitionAndPriorityAndParent(newCompetition().build(), 1, parentSection).build();
        Section siblingSection = newSection().withCompetitionAndPriorityAndParent(newCompetition().build(), 2, parentSection).build();
        when(sectionRepositoryMock.findOne(section.getId())).thenReturn(section);
        when(sectionRepositoryMock.findFirstByCompetitionIdAndParentSectionIdAndPriorityGreaterThanAndQuestionGroupTrueOrderByPriorityAsc(
                section.getCompetition().getId(), section.getParentSection().getId(), section.getPriority()
        )).thenReturn(siblingSection);

        Section returnSection = sectionService.getNextSection(section.getId());
        assertEquals(siblingSection, returnSection);
    }

    @Test
    public void getPreviousSectionWithParentSectionTest() throws Exception {
        Section parentSection = newSection().build();
        Section section = newSection().withCompetitionAndPriorityAndParent(newCompetition().build(), 1, parentSection).build();
        Section siblingSection = newSection().withCompetitionAndPriorityAndParent(newCompetition().build(), 2, parentSection).build();
        when(sectionRepositoryMock.findOne(section.getId())).thenReturn(section);
        when(sectionRepositoryMock.findFirstByCompetitionIdAndParentSectionIdAndPriorityLessThanAndQuestionGroupTrueOrderByPriorityDesc(
                section.getCompetition().getId(), section.getParentSection().getId(), section.getPriority()
        )).thenReturn(siblingSection);

        Section returnSection = sectionService.getPreviousSection(section.getId());
        assertEquals(siblingSection, returnSection);
    }
}