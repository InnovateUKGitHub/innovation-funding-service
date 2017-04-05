package org.innovateuk.ifs.publiccontent.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.repository.InnovationAreaRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemPageResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.publiccontent.domain.PublicContent;
import org.innovateuk.ifs.publiccontent.mapper.PublicContentMapper;
import org.innovateuk.ifs.publiccontent.repository.KeywordRepository;
import org.innovateuk.ifs.publiccontent.repository.PublicContentRepository;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.*;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.category.builder.InnovationAreaBuilder.newInnovationArea;
import static org.innovateuk.ifs.category.builder.InnovationSectorBuilder.newInnovationSector;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.MilestoneBuilder.newMilestone;
import static org.innovateuk.ifs.publiccontent.builder.KeywordBuilder.newKeyword;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentBuilder.newPublicContent;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
import static org.innovateuk.ifs.publiccontent.transactional.PublicContentItemServiceImpl.MAX_ALLOWED_KEYWORDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class PublicContentItemServiceImplTest extends BaseServiceUnitTest<PublicContentItemServiceImpl> {

    private static final String NON_IFS_URL = "www.google.co.uk";

    @Mock
    private InnovationAreaRepository innovationAreaRepository;

    @Mock
    private CompetitionRepository competitionRepository;

    @Mock
    private KeywordRepository keywordRepository;

    @Mock
    private PublicContentRepository publicContentRepository;

    @Mock
    private PublicContentMapper publicContentMapper;

    private static final Long COMPETITION_ID = 1L;
    private static final Long INNOVATION_AREA_ID = 3L;
    private static final Long PUBLIC_CONTENT_ID = 4L;
    private static final String SEARCH_STRING_TOO_LONG = "a b c d e f g h i j k l m n o p q r s t u v w x y z";


    @Override
    protected PublicContentItemServiceImpl supplyServiceUnderTest() {
        return new PublicContentItemServiceImpl();
    }

    @Test
    public void findFilteredItems_withNoParameters() {
        Page<Competition> competitionPage = getCompetitionPage();
        when(publicContentRepository.findByCompetitionId(COMPETITION_ID)).thenReturn(newPublicContent().build());
        when(publicContentRepository.findAllPublishedForOpenCompetition(new PageRequest(0, 10))).thenReturn(competitionPage);
        when(publicContentMapper.mapToResource(any(PublicContent.class))).thenReturn(newPublicContentResource().build());

        ServiceResult<PublicContentItemPageResource> result = service.findFilteredItems(Optional.empty(), Optional.empty(), Optional.empty(), 10);

        assertTrue(result.isSuccess());

        testResult(result);

        verify(publicContentRepository, times(1)).findAllPublishedForOpenCompetition(any());
        verify(publicContentRepository, times(40)).findByCompetitionId(COMPETITION_ID);
    }

    @Test
    public void findFilteredItems_withInnovationAreaOnly() {
        InnovationArea innovationArea = newInnovationArea().withSector(newInnovationSector().withId(2L).build()).build();

        List<Long> expectedCompetitionIds = singletonList(1L);

        Page<Competition> competitionPage = getCompetitionPage();
        when(publicContentRepository.findByCompetitionId(COMPETITION_ID)).thenReturn(newPublicContent().build());
        when(publicContentRepository.findAllPublishedForOpenCompetitionByInnovationId(expectedCompetitionIds, new PageRequest(0, 10))).thenReturn(competitionPage);
        when(publicContentMapper.mapToResource(any(PublicContent.class))).thenReturn(newPublicContentResource().build());


        List<Competition> competitions  = newCompetition()
                .withId(COMPETITION_ID)
                .build(1);

        when(innovationAreaRepository.findOne(INNOVATION_AREA_ID)).thenReturn(innovationArea);
        when(competitionRepository.findByInnovationSectorCategoryId(2L)).thenReturn(competitions);

        ServiceResult<PublicContentItemPageResource> result = service.findFilteredItems(Optional.of(INNOVATION_AREA_ID), Optional.empty(), Optional.empty(), 10);

        assertTrue(result.isSuccess());

        testResult(result);

        verify(publicContentRepository, times(40)).findByCompetitionId(COMPETITION_ID);
        verify(publicContentRepository, times(1)).findAllPublishedForOpenCompetitionByInnovationId(any(), any());
    }

    @Test
    public void findFilteredItems_withKeywordsOnly() {
        makePublicContentIdsFound();

        Set<Long> expectedPublicContentIds = new HashSet<Long>();
        expectedPublicContentIds.add(4L);

        Page<Competition> competitionPage = getCompetitionPage();
        when(publicContentRepository.findByCompetitionId(COMPETITION_ID)).thenReturn(newPublicContent().build());
        when(publicContentRepository.findAllPublishedForOpenCompetitionByKeywords(expectedPublicContentIds, new PageRequest(0, 10))).thenReturn(competitionPage);
        when(publicContentMapper.mapToResource(any(PublicContent.class))).thenReturn(newPublicContentResource().build());


        List<Competition> competitions = newCompetition()
                .withId(COMPETITION_ID)
                .build(1);

        when(competitionRepository.findByInnovationSectorCategoryId(2L)).thenReturn(competitions);


        ServiceResult<PublicContentItemPageResource> result = service.findFilteredItems(Optional.empty(), Optional.of("Big data"), Optional.empty(), 10);

        assertTrue(result.isSuccess());

        testResult(result);

        verify(publicContentRepository, times(40)).findByCompetitionId(COMPETITION_ID);
        verify(publicContentRepository, times(1)).findAllPublishedForOpenCompetitionByKeywords(any(), any());
    }

    @Test
    public void findFilteredItems_withAllParameters() {
        makePublicContentIdsFound();

        Set<Long> expectedPublicContentIds = new HashSet<Long>();
        expectedPublicContentIds.add(4L);

        List<Long> expectedCompetitionIds = singletonList(1L);

        Page<Competition> competitionPage = getCompetitionPage();

        List<Competition> competitions = newCompetition()
                .withId(COMPETITION_ID)
                .build(1);

        Long innovationSectorId = 2L;

        InnovationArea innovationArea = newInnovationArea()
                .withId(INNOVATION_AREA_ID)
                .withSector(newInnovationSector().withId(innovationSectorId).build())
                .build();

        when(publicContentRepository.findByCompetitionId(COMPETITION_ID)).thenReturn(newPublicContent().build());
        when(publicContentRepository.findAllPublishedForOpenCompetitionByKeywordsAndInnovationId(expectedPublicContentIds, expectedCompetitionIds, new PageRequest(1, 10))).thenReturn(competitionPage);
        when(publicContentMapper.mapToResource(any(PublicContent.class))).thenReturn(newPublicContentResource().build());
        when(innovationAreaRepository.findOne(INNOVATION_AREA_ID)).thenReturn(innovationArea);
        when(competitionRepository.findByInnovationSectorCategoryId(innovationSectorId)).thenReturn(competitions);

        ServiceResult<PublicContentItemPageResource> result = service.findFilteredItems(Optional.of(INNOVATION_AREA_ID), Optional.of("Big data"), Optional.of(1), 10);

        assertTrue(result.isSuccess());

        testResult(result);

        verify(publicContentRepository, times(40)).findByCompetitionId(COMPETITION_ID);
        verify(publicContentRepository, times(1)).findAllPublishedForOpenCompetitionByKeywordsAndInnovationId(any(), any(),any());
    }


    @Test
    public void findFilteredItems_searchStringTooLong() {
        makePublicContentIdsFound();

        Set<Long> expectedPublicContentIds = new HashSet<Long>();
        expectedPublicContentIds.add(4L);

        Page<Competition> competitionPage = getCompetitionPage();
        when(publicContentRepository.findByCompetitionId(COMPETITION_ID)).thenReturn(newPublicContent().build());
        when(publicContentRepository.findAllPublishedForOpenCompetitionByKeywords(expectedPublicContentIds, new PageRequest(0, 10))).thenReturn(competitionPage);
        when(publicContentMapper.mapToResource(any(PublicContent.class))).thenReturn(newPublicContentResource().build());


        List<Competition> competitions = newCompetition()
                .withId(COMPETITION_ID)
                .build(1);

        when(competitionRepository.findByInnovationSectorCategoryId(2L)).thenReturn(competitions);


        ServiceResult<PublicContentItemPageResource> result = service.findFilteredItems(Optional.empty(), Optional.of(SEARCH_STRING_TOO_LONG), Optional.empty(), 10);

        assertTrue(result.isSuccess());

        testResult(result);

        verify(keywordRepository, times(MAX_ALLOWED_KEYWORDS)).findByKeywordLike(any());
        verify(publicContentRepository, times(40)).findByCompetitionId(COMPETITION_ID);
        verify(publicContentRepository, times(1)).findAllPublishedForOpenCompetitionByKeywords(any(), any());
    }

    @Test
    public void testByCompetitionId() {
        Long competitionId = 4L;

        Competition competition = newCompetition().withNonIfs(false).withId(competitionId).withSetupComplete(true).withMilestones(
                newMilestone()
                        .withDate(LocalDateTime.of(2017,1,2,3,4), LocalDateTime.of(2017,3,2,1,4))
                        .withType(MilestoneType.OPEN_DATE, MilestoneType.SUBMISSION_DATE)
                        .build(2)
        ).build();
        PublicContent publicContent = newPublicContent().withCompetitionId(competitionId).build();
        PublicContentResource publicContentResource = newPublicContentResource().withCompetitionId(competitionId).build();
        when(competitionRepository.findById(competitionId)).thenReturn(competition);
        when(publicContentRepository.findByCompetitionId(competitionId)).thenReturn(publicContent);
        when(publicContentMapper.mapToResource(publicContent)).thenReturn(publicContentResource);

        ServiceResult<PublicContentItemResource> result = service.byCompetitionId(competitionId);
        assertTrue(result.isSuccess());

        PublicContentItemResource resultObject = result.getSuccessObject();

        assertEquals(competition.getEndDate(), resultObject.getCompetitionCloseDate());
        assertEquals(competition.getStartDate(), resultObject.getCompetitionOpenDate());
        assertEquals(competition.getName(), resultObject.getCompetitionTitle());
        assertEquals(publicContentResource, resultObject.getPublicContentResource());
        assertEquals(competition.getSetupComplete(), resultObject.getSetupComplete());

        verify(publicContentRepository, only()).findByCompetitionId(competitionId);
        verify(competitionRepository, only()).findById(competitionId);
    }

    @Test
    public void testByCompetitionIdNonIfs() {
        Long competitionId = 4L;

        Competition competition = newCompetition().withNonIfs(true).withId(competitionId).withSetupComplete(true).withMilestones(
                newMilestone()
                        .withDate(LocalDateTime.of(2017,1,2,3,4), LocalDateTime.of(2017,3,2,1,4))
                        .withType(MilestoneType.OPEN_DATE, MilestoneType.SUBMISSION_DATE)
                        .build(2)
        ).build();
        PublicContent publicContent = newPublicContent().withCompetitionId(competitionId).build();
        PublicContentResource publicContentResource = newPublicContentResource().withCompetitionId(competitionId).build();
        when(competitionRepository.findById(competitionId)).thenReturn(competition);
        when(publicContentRepository.findByCompetitionId(competitionId)).thenReturn(publicContent);
        when(publicContentMapper.mapToResource(publicContent)).thenReturn(publicContentResource);

        ServiceResult<PublicContentItemResource> result = service.byCompetitionId(competitionId);
        assertTrue(result.isSuccess());

        PublicContentItemResource resultObject = result.getSuccessObject();

        assertEquals(competition.getEndDate(), resultObject.getCompetitionCloseDate());
        assertEquals(competition.getStartDate(), resultObject.getCompetitionOpenDate());
        assertEquals(competition.getName(), resultObject.getCompetitionTitle());
        assertEquals(publicContentResource, resultObject.getPublicContentResource());
        assertEquals(true, resultObject.getSetupComplete());

        verify(publicContentRepository, only()).findByCompetitionId(competitionId);
        verify(competitionRepository, only()).findById(competitionId);
    }

    @Test
    public void testByCompetitionIdFailure() {
        Long competitionId = 4L;

        when(competitionRepository.findById(competitionId)).thenReturn(null);
        when(publicContentRepository.findByCompetitionId(competitionId)).thenReturn(null);

        ServiceResult<PublicContentItemResource> result = service.byCompetitionId(competitionId);
        assertTrue(result.isFailure());

        verify(publicContentRepository, only()).findByCompetitionId(competitionId);
        verify(competitionRepository, only()).findById(competitionId);
    }

    private void makePublicContentIdsFound() {
        PublicContent publicContent = newPublicContent().withId(PUBLIC_CONTENT_ID).withCompetitionId(COMPETITION_ID).build();

        when(keywordRepository.findByKeywordLike("%Big%")).thenReturn(newKeyword().withKeyword("Big Data").withPublicContent(publicContent).build(2));
        when(keywordRepository.findByKeywordLike("%data%")).thenReturn(newKeyword().withKeyword("Data").withPublicContent(publicContent).build(1));
        when(keywordRepository.findByKeywordLike("%a%")).thenReturn(newKeyword().withKeyword("Data").withPublicContent(publicContent).build(1));
    }

    private Page<Competition> getCompetitionPage() {
        Page<Competition> expected = mock(Page.class);

        List<Competition> competitions = newCompetition().withId(1L).withNonIfs(true).withNonIfsUrl(NON_IFS_URL).build(40);

        when(expected.getContent()).thenReturn(competitions);
        when(expected.getTotalElements()).thenReturn(62L);
        when(expected.getTotalPages()).thenReturn(2);
        when(expected.getNumber()).thenReturn(1);
        when(expected.getSize()).thenReturn(40);

        return expected;
    }

    private void testResult(ServiceResult<PublicContentItemPageResource> result) {
        assertEquals(40, result.getSuccessObject().getSize());
        assertEquals(1, result.getSuccessObject().getNumber());
        assertEquals(62L, result.getSuccessObject().getTotalElements());
        assertEquals(2, result.getSuccessObject().getTotalPages());

        assertEquals(40, result.getSuccessObject().getContent().size());
        assertThat(NON_IFS_URL, equalTo(result.getSuccessObject().getContent().get(0).getNonIfsUrl()));
    }

    private void testResultEmpty(ServiceResult<PublicContentItemPageResource> result) {
        assertEquals(0, result.getSuccessObject().getSize());
        assertEquals(0, result.getSuccessObject().getNumber());
        assertEquals(0L, result.getSuccessObject().getTotalElements());
        assertEquals(0, result.getSuccessObject().getTotalPages());

        assertEquals(0, result.getSuccessObject().getContent().size());
    }
}
