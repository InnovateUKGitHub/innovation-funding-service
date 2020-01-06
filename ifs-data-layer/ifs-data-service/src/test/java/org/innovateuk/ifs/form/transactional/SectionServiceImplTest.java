package org.innovateuk.ifs.form.transactional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.form.domain.Section;
import org.innovateuk.ifs.form.mapper.SectionMapper;
import org.innovateuk.ifs.form.repository.SectionRepository;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.form.builder.SectionBuilder.newSection;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class SectionServiceImplTest extends BaseUnitTestMocksTest {
    private final Log log = LogFactory.getLog(getClass());

    @Mock
    SectionMapper sectionMapper;

    @Mock
    private SectionRepository sectionRepositoryMock;

    @InjectMocks
    protected SectionService sectionService = new SectionServiceImpl();

    @Test
    public void getNextSectionTest() throws Exception {
        Section section = newSection().withCompetitionAndPriority(newCompetition().build(), 1).build();
        Section nextSection = newSection().build();
        SectionResource nextSectionResource = newSectionResource().build();
        when(sectionRepositoryMock.findById(anyLong())).thenReturn(Optional.of(section));
        when(sectionRepositoryMock.findFirstByCompetitionIdAndPriorityGreaterThanAndParentSectionIsNullOrderByPriorityAsc(
                nullable(Long.class), nullable(Integer.class)
        )).thenReturn(nextSection);
        when(sectionMapper.mapToResource(any(Section.class))).thenReturn(nextSectionResource);

        SectionResource returnSection = sectionService.getNextSection(section.getId()).getSuccess();
        assertEquals(nextSectionResource, returnSection);
    }

    @Test
    public void getPreviousSectionTest() throws Exception {
        Section section = newSection().withCompetitionAndPriority(newCompetition().build(), 1).build();
        Section previousSection = newSection().build();
        SectionResource previousSectionResource = newSectionResource().build();
        when(sectionRepositoryMock.findById(anyLong())).thenReturn(Optional.of(section));
        when(sectionRepositoryMock.findFirstByCompetitionIdAndPriorityLessThanAndParentSectionIsNullOrderByPriorityDesc(
                nullable(Long.class), nullable(Integer.class)
        )).thenReturn(previousSection);
        when(sectionMapper.mapToResource(any(Section.class))).thenReturn(previousSectionResource);

        SectionResource returnSection = sectionService.getPreviousSection(section.getId()).getSuccess();
        assertEquals(previousSectionResource, returnSection);
    }

    @Test
    public void getNextSectionWithParentSectionTest() throws Exception {
        Section parentSection = newSection().build();
        Section section = newSection().withCompetitionAndPriorityAndParent(newCompetition().build(), 1, parentSection).build();
        Section siblingSection = newSection().withCompetitionAndPriorityAndParent(newCompetition().build(), 2, parentSection).build();
        SectionResource siblingSectionResource = newSectionResource().withCompetitionAndPriorityAndParent(newCompetition().build().getId(), 2, parentSection.getId()).build();
        when(sectionRepositoryMock.findById(section.getId())).thenReturn(Optional.of(section));
        when(sectionRepositoryMock.findFirstByCompetitionIdAndParentSectionIdAndPriorityGreaterThanAndQuestionGroupTrueOrderByPriorityAsc(
                anyLong(), anyLong(), anyInt()
        )).thenReturn(siblingSection);
        when(sectionMapper.mapToResource(any(Section.class))).thenReturn(siblingSectionResource);

        SectionResource returnSection = sectionService.getNextSection(section.getId()).getSuccess();
        assertEquals(siblingSectionResource, returnSection);
    }

    @Test
    public void getPreviousSectionWithParentSectionTest() throws Exception {
        Section parentSection = newSection().build();
        Section section = newSection().withCompetitionAndPriorityAndParent(newCompetition().build(), 1, parentSection).build();
        Section siblingSection = newSection().withCompetitionAndPriorityAndParent(newCompetition().build(), 2, parentSection).build();
        SectionResource siblingSectionResource = newSectionResource().withCompetitionAndPriorityAndParent(newCompetition().build().getId(), 2, parentSection.getId()).build();
        when(sectionRepositoryMock.findById(section.getId())).thenReturn(Optional.of(section));
        when(sectionRepositoryMock.findFirstByCompetitionIdAndParentSectionIdAndPriorityLessThanAndQuestionGroupTrueOrderByPriorityDesc(
                anyLong(), anyLong(), anyInt()
        )).thenReturn(siblingSection);
        when(sectionMapper.mapToResource(any(Section.class))).thenReturn(siblingSectionResource);

        SectionResource returnSection = sectionService.getPreviousSection(section.getId()).getSuccess();
        assertEquals(siblingSectionResource, returnSection);
    }

    @Test
    public void getByCompetitionIdVisibleForAssessmentTest() throws Exception {
        Long competitionId = 1L;

        List<Section> sections = newSection().build(2);
        List<SectionResource> sectionResources = newSectionResource().build(2);

        when(sectionRepositoryMock.findByCompetitionIdAndDisplayInAssessmentApplicationSummaryTrueOrderByPriorityAsc(competitionId)).thenReturn(sections);
        when(sectionMapper.mapToResource(same(sections.get(0)))).thenReturn(sectionResources.get(0));
        when(sectionMapper.mapToResource(same(sections.get(1)))).thenReturn(sectionResources.get(1));

        ServiceResult<List<SectionResource>> result = sectionService.getByCompetitionIdVisibleForAssessment(competitionId);
        assertTrue(result.isSuccess());
        assertEquals(sectionResources, result.getSuccess());
    }

    @Test
    public void getChildSectionsByParentId() throws Exception {
        List<SectionResource> childSectionResources = newSectionResource().build(2);
        List<Section> childSections = newSection().build(2);
        Section parentSection = newSection().withChildSections(childSections).build();

        when(sectionRepositoryMock.findById(parentSection.getId())).thenReturn(Optional.of(parentSection));
        when(sectionService.getChildSectionsByParentId(parentSection.getId())).thenReturn(serviceSuccess(childSectionResources));
        when(sectionMapper.mapToResource(same(childSections.get(0)))).thenReturn(childSectionResources.get(0));
        when(sectionMapper.mapToResource(same(childSections.get(1)))).thenReturn(childSectionResources.get(1));

        ServiceResult<List<SectionResource>> result = sectionService.getChildSectionsByParentId(parentSection.getId());
        assertTrue(result.isSuccess());
        assertEquals(childSectionResources, result.getSuccess());
    }
}
