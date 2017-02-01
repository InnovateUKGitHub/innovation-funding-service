package org.innovateuk.ifs.publiccontent.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.category.domain.CompetitionCategoryLink;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.repository.CompetitionCategoryLinkRepository;
import org.innovateuk.ifs.category.repository.InnovationAreaRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemPageResource;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.publiccontent.domain.PublicContent;
import org.innovateuk.ifs.publiccontent.mapper.PublicContentMapper;
import org.innovateuk.ifs.publiccontent.repository.KeywordRepository;
import org.innovateuk.ifs.publiccontent.repository.PublicContentRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.*;

import static com.google.common.primitives.Longs.asList;
import static org.innovateuk.ifs.category.builder.CompetitionCategoryLinkBuilder.newCompetitionCategoryLink;
import static org.innovateuk.ifs.category.builder.InnovationAreaBuilder.newInnovationArea;
import static org.innovateuk.ifs.category.builder.InnovationSectorBuilder.newInnovationSector;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.MilestoneBuilder.newMilestone;
import static org.innovateuk.ifs.publiccontent.builder.KeywordBuilder.newKeyword;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentBuilder.newPublicContent;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
import static org.innovateuk.ifs.publiccontent.transactional.PublicContentItemServiceImpl.MAX_ALLOWED_KEYWORDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class PublicContentItemServiceImplTest extends BaseServiceUnitTest<PublicContentItemServiceImpl> {

    @Mock
    private InnovationAreaRepository innovationAreaRepository;

    @Mock
    private CompetitionCategoryLinkRepository competitionCategoryLinkRepository;

    @Mock
    private KeywordRepository keywordRepository;

    @Mock
    private PublicContentRepository publicContentRepository;

    @Mock
    private PublicContentMapper publicContentMapper;

    @Mock
    private CompetitionRepository competitionRepository;

    private static final Long COMPETITION_ID = 1L;
    private static final Long COMPETITION_ID_TWO = 3823L;
    private static final Long INNOVATION_AREA_ID = 8243L;
    private static final Long PUBLIC_CONTENT_ID = 235234L;
    private static final String SEARCH_STRING = "Big data";
    private static final String SEARCH_STRING_TOO_LONG = "a b c d e f g h i j k l m n o p q r s t u v w x y z";
    private static final Long INNOVATION_AREA_ID_NOT_FOUND = 82313L;

    @Override
    protected PublicContentItemServiceImpl supplyServiceUnderTest() {
        return new PublicContentItemServiceImpl();
    }

    @Before
    public void setup() {
        InnovationArea innovationArea = newInnovationArea().withSector(newInnovationSector().withId(2L).build()).build();

        when(innovationAreaRepository.findOne(INNOVATION_AREA_ID)).thenReturn(innovationArea);
        when(competitionCategoryLinkRepository.findByCategoryId(2L)).thenReturn(Collections.emptyList());

        when(keywordRepository.findByKeywordLike(anyString())).thenReturn(Collections.emptyList());

        when(competitionRepository.findById(anyLong())).thenReturn(newCompetition().with(competition -> {
            competition.setMilestones(newMilestone()
                    .withDate(LocalDateTime.MIN, LocalDateTime.MAX)
                    .withType(MilestoneType.OPEN_DATE, MilestoneType.SUBMISSION_DATE)
                    .build(2));
            competition.setName("Competition one");
        }).build());

        when(publicContentMapper.mapToResource(any(PublicContent.class))).thenReturn(newPublicContentResource().build());
    }

    @Test
    public void testFilteredItemsWithOptionals() {
        makeCompetitionIdsFound();
        makePublicContentIdsFound();

        Page<PublicContent> expected = getPublicContentPage();

        when(publicContentRepository.findByCompetitionIdInAndIdIn(asList(COMPETITION_ID, COMPETITION_ID, COMPETITION_ID), getSetWithPublicContentIds(), new PageRequest(1, 40)))
                .thenReturn(expected);

        ServiceResult<PublicContentItemPageResource> result = service.findFilteredItems(Optional.of(INNOVATION_AREA_ID), Optional.of(SEARCH_STRING), Optional.of(1), Optional.of(40));

        assertTrue(result.isSuccess());

        testResult(result);

        verify(publicContentRepository).findByCompetitionIdInAndIdIn(asList(COMPETITION_ID, COMPETITION_ID, COMPETITION_ID), getSetWithPublicContentIds(), new PageRequest(1, 40));
        verify(competitionRepository, times(40)).findById(COMPETITION_ID);
        verify(keywordRepository,times(2)).findByKeywordLike(anyString());
    }

    @Test
    public void testFilteredItemsWithOptionalsNothingFound() {
        Page<PublicContent> expected = getPublicContentPageEmpty();

        when(publicContentRepository.findByCompetitionIdInAndIdIn(Collections.emptyList(), Collections.emptySet(), new PageRequest(1, 40))).thenReturn(expected);

        ServiceResult<PublicContentItemPageResource> result = service.findFilteredItems(Optional.of(INNOVATION_AREA_ID), Optional.of(SEARCH_STRING), Optional.of(1), Optional.of(40));
        assertTrue(result.isSuccess());

        testResultEmpty(result);

        verify(publicContentRepository).findByCompetitionIdInAndIdIn(Collections.emptyList(), Collections.emptySet(), new PageRequest(1, 40));
    }

    @Test
    public void testFilteredItemsWithNoOptionals() {
        Page<PublicContent> expected = getPublicContentPage();

        when(publicContentRepository.findAll(new PageRequest(1, 40))).thenReturn(expected);

        ServiceResult<PublicContentItemPageResource> result = service.findFilteredItems(Optional.empty(), Optional.empty(), Optional.of(1), Optional.of(40));
        assertTrue(result.isSuccess());

        testResult(result);

        verify(publicContentRepository).findAll(new PageRequest(1, 40));
    }

    @Test
    public void testFilteredItemsWithOptionalInnovationAreaId() {
        makeCompetitionIdsFound();
        Page<PublicContent> expected = getPublicContentPage();

        when(publicContentRepository.findByCompetitionIdIn(asList(COMPETITION_ID, COMPETITION_ID, COMPETITION_ID), new PageRequest(1, 40))).thenReturn(expected);

        ServiceResult<PublicContentItemPageResource> result = service.findFilteredItems(Optional.of(INNOVATION_AREA_ID), Optional.empty(), Optional.of(1), Optional.of(40));
        assertTrue(result.isSuccess());

        testResult(result);

        verify(publicContentRepository).findByCompetitionIdIn(asList(COMPETITION_ID, COMPETITION_ID, COMPETITION_ID), new PageRequest(1, 40));
    }

    @Test
    public void testFilteredItemsWithOptionalInnovationAreaIdNothingFound() {
        Page<PublicContent> expected = getPublicContentPageEmpty();

        when(publicContentRepository.findByCompetitionIdIn(Collections.emptyList(), new PageRequest(1, 40))).thenReturn(expected);

        ServiceResult<PublicContentItemPageResource> result = service.findFilteredItems(Optional.of(INNOVATION_AREA_ID_NOT_FOUND), Optional.empty(), Optional.of(1), Optional.of(40));
        assertTrue(result.isSuccess());

        testResultEmpty(result);

        verify(publicContentRepository).findByCompetitionIdIn(Collections.emptyList(), new PageRequest(1, 40));
    }

    @Test
    public void testFilteredItemsWithOptionalSearchString() {
        makePublicContentIdsFound();
        Page<PublicContent> expected = getPublicContentPage();

        when(publicContentRepository.findByIdIn(getSetWithPublicContentIds(), new PageRequest(1, 40))).thenReturn(expected);

        ServiceResult<PublicContentItemPageResource> result = service.findFilteredItems(Optional.empty(), Optional.of(SEARCH_STRING), Optional.of(1), Optional.of(40));
        assertTrue(result.isSuccess());

        testResult(result);

        verify(publicContentRepository).findByIdIn(getSetWithPublicContentIds(), new PageRequest(1, 40));
        verify(keywordRepository, times(2)).findByKeywordLike(anyString());
    }


    @Test
    public void testFilteredItemsWithOptionalSearchStringTooLong() {
        makePublicContentIdsFound();
        Page<PublicContent> expected = getPublicContentPage();

        when(publicContentRepository.findByIdIn(getSetWithPublicContentIds(), new PageRequest(1, 40))).thenReturn(expected);

        ServiceResult<PublicContentItemPageResource> result = service.findFilteredItems(Optional.empty(), Optional.of(SEARCH_STRING_TOO_LONG), Optional.of(1), Optional.of(40));
        assertTrue(result.isSuccess());

        testResult(result);

        verify(publicContentRepository).findByIdIn(getSetWithPublicContentIds(), new PageRequest(1, 40));
        verify(keywordRepository, times(MAX_ALLOWED_KEYWORDS)).findByKeywordLike(anyString());
    }

    @Test
    public void testFilteredItemsWithOptionalSearchStringNothingFound() {
        makePublicContentIdsFound();
        Page<PublicContent> expected = getPublicContentPageEmpty();

        when(publicContentRepository.findByIdIn(Collections.emptySet(), new PageRequest(1, 40))).thenReturn(expected);

        ServiceResult<PublicContentItemPageResource> result = service.findFilteredItems(Optional.empty(), Optional.of("NothingWillBeFound"), Optional.of(1), Optional.of(40));
        assertTrue(result.isSuccess());

        testResultEmpty(result);

        verify(publicContentRepository).findByIdIn(Collections.emptySet(), new PageRequest(1, 40));
        verify(keywordRepository, atMost(MAX_ALLOWED_KEYWORDS)).findByKeywordLike(anyString());
    }

    private void makeCompetitionIdsFound() {
        InnovationArea innovationArea = newInnovationArea().withSector(newInnovationSector().withId(2L).build()).build();
        List<CompetitionCategoryLink> competitionCategories = newCompetitionCategoryLink().withCompetition(newCompetition().withId(COMPETITION_ID, COMPETITION_ID_TWO).build()).build(3);

        when(innovationAreaRepository.findOne(INNOVATION_AREA_ID)).thenReturn(innovationArea);
        when(competitionCategoryLinkRepository.findByCategoryId(2L)).thenReturn(competitionCategories);
    }

    private void makePublicContentIdsFound() {
        PublicContent publicContent = newPublicContent().with(publicContent1 -> publicContent1.setId(PUBLIC_CONTENT_ID)).withCompetitionId(COMPETITION_ID).build();

        when(keywordRepository.findByKeywordLike("Big")).thenReturn(newKeyword().withKeyword("Big Data").withPublicContent(publicContent).build(2));
        when(keywordRepository.findByKeywordLike("data")).thenReturn(newKeyword().withKeyword("Data").withPublicContent(publicContent).build(1));
        when(keywordRepository.findByKeywordLike("a")).thenReturn(newKeyword().withKeyword("Data").withPublicContent(publicContent).build(1));
    }

    private Page<PublicContent> getPublicContentPage() {
        Page<PublicContent> expected = mock(Page.class);
        when(expected.getTotalElements()).thenReturn(62L);
        when(expected.getTotalPages()).thenReturn(2);
        when(expected.getContent()).thenReturn(newPublicContent().with((integer, publicContent) -> {
            publicContent.setId(integer + 1L);
            publicContent.setCompetitionId(COMPETITION_ID);
        }).build(40));
        when(expected.getNumber()).thenReturn(1);
        when(expected.getSize()).thenReturn(40);

        return expected;
    }

    private Page<PublicContent> getPublicContentPageEmpty() {
        Page<PublicContent> expected = mock(Page.class);
        when(expected.getTotalElements()).thenReturn(0L);
        when(expected.getTotalPages()).thenReturn(0);
        when(expected.getContent()).thenReturn(Collections.emptyList());
        when(expected.getNumber()).thenReturn(0);
        when(expected.getSize()).thenReturn(0);

        return expected;
    }

    private Set<Long> getSetWithPublicContentIds() {
        Set<Long> tempSet = new HashSet<>();
        tempSet.add(PUBLIC_CONTENT_ID);

        return tempSet;
    }

    private void testResult(ServiceResult<PublicContentItemPageResource> result) {
        assertEquals(40, result.getSuccessObject().getSize());
        assertEquals(1, result.getSuccessObject().getNumber());
        assertEquals(62L, result.getSuccessObject().getTotalElements());
        assertEquals(2, result.getSuccessObject().getTotalPages());

        assertEquals(40, result.getSuccessObject().getContent().size());
    }

    private void testResultEmpty(ServiceResult<PublicContentItemPageResource> result) {
        assertEquals(0, result.getSuccessObject().getSize());
        assertEquals(0, result.getSuccessObject().getNumber());
        assertEquals(0L, result.getSuccessObject().getTotalElements());
        assertEquals(0, result.getSuccessObject().getTotalPages());

        assertEquals(0, result.getSuccessObject().getContent().size());
    }
}
