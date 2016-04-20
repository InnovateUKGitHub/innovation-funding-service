package com.worth.ifs.application.transactional;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.application.domain.Section;
import com.worth.ifs.application.mapper.SectionMapper;
import com.worth.ifs.application.resource.SectionResource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static com.worth.ifs.application.builder.SectionBuilder.newSection;
import static com.worth.ifs.application.builder.SectionResourceBuilder.newSectionResource;
import static com.worth.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

public class SectionServiceTest extends BaseUnitTestMocksTest {
    private final Log log = LogFactory.getLog(getClass());

    @Mock
    SectionMapper sectionMapper;

    @InjectMocks
    protected SectionService sectionService = new SectionServiceImpl();

    @Test
    public void getNextSectionTest() throws Exception {
        Section section = newSection().withCompetitionAndPriority(newCompetition().build(), 1).build();
        Section nextSection = newSection().build();
        SectionResource nextSectionResource = newSectionResource().build();
        when(sectionRepositoryMock.findOne(anyLong())).thenReturn(section);
        when(sectionRepositoryMock.findFirstByCompetitionIdAndPriorityGreaterThanAndParentSectionIsNullOrderByPriorityAsc(
                anyLong(), anyInt()
        )).thenReturn(nextSection);
        when(sectionMapper.mapToResource(any(Section.class))).thenReturn(nextSectionResource);

        SectionResource returnSection = sectionService.getNextSection(section.getId()).getSuccessObject();
        assertEquals(nextSectionResource, returnSection);
    }

    @Test
    public void getPreviousSectionTest() throws Exception {
        Section section = newSection().withCompetitionAndPriority(newCompetition().build(), 1).build();
        Section previousSection = newSection().build();
        SectionResource previousSectionResource = newSectionResource().build();
        when(sectionRepositoryMock.findOne(anyLong())).thenReturn(section);
        when(sectionRepositoryMock.findFirstByCompetitionIdAndPriorityLessThanAndParentSectionIsNullOrderByPriorityDesc(
                anyLong(), anyInt()
        )).thenReturn(previousSection);
        when(sectionMapper.mapToResource(any(Section.class))).thenReturn(previousSectionResource);

        SectionResource returnSection = sectionService.getPreviousSection(section.getId()).getSuccessObject();
        assertEquals(previousSectionResource, returnSection);
    }

    @Test
    public void getNextSectionWithParentSectionTest() throws Exception {
        Section parentSection = newSection().build();
        Section section = newSection().withCompetitionAndPriorityAndParent(newCompetition().build(), 1, parentSection).build();
        Section siblingSection = newSection().withCompetitionAndPriorityAndParent(newCompetition().build(), 2, parentSection).build();
        SectionResource siblingSectionResource = newSectionResource().withCompetitionAndPriorityAndParent(newCompetition().build().getId(), 2, parentSection.getId()).build();
        when(sectionRepositoryMock.findOne(section.getId())).thenReturn(section);
        when(sectionRepositoryMock.findFirstByCompetitionIdAndParentSectionIdAndPriorityGreaterThanAndQuestionGroupTrueOrderByPriorityAsc(
                anyLong(), anyLong(), anyInt()
        )).thenReturn(siblingSection);
        when(sectionMapper.mapToResource(any(Section.class))).thenReturn(siblingSectionResource);

        SectionResource returnSection = sectionService.getNextSection(section.getId()).getSuccessObject();
        assertEquals(siblingSectionResource, returnSection);
    }

    @Test
    public void getPreviousSectionWithParentSectionTest() throws Exception {
        Section parentSection = newSection().build();
        Section section = newSection().withCompetitionAndPriorityAndParent(newCompetition().build(), 1, parentSection).build();
        Section siblingSection = newSection().withCompetitionAndPriorityAndParent(newCompetition().build(), 2, parentSection).build();
        SectionResource siblingSectionResource = newSectionResource().withCompetitionAndPriorityAndParent(newCompetition().build().getId(), 2, parentSection.getId()).build();
        when(sectionRepositoryMock.findOne(section.getId())).thenReturn(section);
        when(sectionRepositoryMock.findFirstByCompetitionIdAndParentSectionIdAndPriorityLessThanAndQuestionGroupTrueOrderByPriorityDesc(
                anyLong(), anyLong(), anyInt()
        )).thenReturn(siblingSection);
        when(sectionMapper.mapToResource(any(Section.class))).thenReturn(siblingSectionResource);

        SectionResource returnSection = sectionService.getPreviousSection(section.getId()).getSuccessObject();
        assertEquals(siblingSectionResource, returnSection);
    }
}