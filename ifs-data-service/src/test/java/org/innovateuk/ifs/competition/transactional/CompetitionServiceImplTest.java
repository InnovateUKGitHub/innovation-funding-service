package org.innovateuk.ifs.competition.transactional;

import com.google.common.collect.Lists;
import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.category.domain.Category;
import org.innovateuk.ifs.category.domain.CompetitionCategoryLink;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.Milestone;
import org.innovateuk.ifs.competition.resource.*;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.innovateuk.ifs.category.builder.CategoryBuilder.newCategory;
import static org.innovateuk.ifs.category.builder.CompetitionCategoryLinkBuilder.newCompetitionCategoryLink;
import static org.innovateuk.ifs.category.resource.CategoryType.INNOVATION_AREA;
import static org.innovateuk.ifs.category.resource.CategoryType.INNOVATION_SECTOR;
import static org.innovateuk.ifs.category.resource.CategoryType.RESEARCH_CATEGORY;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.MilestoneBuilder.newMilestone;
import static org.innovateuk.ifs.competition.resource.MilestoneType.*;
import static org.innovateuk.ifs.util.CollectionFunctions.forEachWithIndex;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CompetitionServiceImplTest extends BaseServiceUnitTest<CompetitionServiceImpl> {

    @Override
    protected CompetitionServiceImpl supplyServiceUnderTest() {
        return new CompetitionServiceImpl();
    }

    @Test
    public void test_getCompetitionById() throws Exception {
        Long competitionId = 1L;
        Competition competition = new Competition();
        CompetitionResource resource = new CompetitionResource();
        when(competitionRepositoryMock.findById(competitionId)).thenReturn(competition);
        when(competitionMapperMock.mapToResource(competition)).thenReturn(resource);

        CompetitionResource response = service.getCompetitionById(competitionId).getSuccessObjectOrThrowException();

        assertEquals(resource, response);
    }

    @Test
    public void test_addCategories() throws Exception {
        Long competitionId = 1L;
        Competition competition = newCompetition().withId(competitionId).build();

        Category innovationSector = newCategory().withType(INNOVATION_SECTOR).build();
        Set<Category> researchCategories = newCategory().withType(RESEARCH_CATEGORY, RESEARCH_CATEGORY).withName("foo", "bar").buildSet(2);
        Category innovationArea = newCategory().withType(INNOVATION_AREA).build();

        when(competitionCategoryLinkRepositoryMock.findByCompetitionIdAndCategory_Type(competitionId, INNOVATION_SECTOR))
                .thenReturn(newCompetitionCategoryLink().withCompetition(competition).withCategory(innovationSector).build());
        when(competitionCategoryLinkRepositoryMock.findByCompetitionIdAndCategory_Type(competitionId, INNOVATION_AREA))
                .thenReturn(newCompetitionCategoryLink().withCompetition(competition).withCategory(innovationArea).build());
        when(competitionCategoryLinkRepositoryMock.findAllByCompetitionIdAndCategoryType(competitionId, RESEARCH_CATEGORY))
                .thenReturn(Arrays.asList(
                        newCompetitionCategoryLink()
                                .withCompetition(competition, competition)
                                .withCategory(researchCategories.toArray(new Category[2]))
                                .buildArray(2, CompetitionCategoryLink.class)));

        Competition compResp = service.addCategories(competition);

        assertEquals(competition, compResp);
        assertEquals(competition.getInnovationArea(), innovationArea);
        assertEquals(competition.getInnovationSector(), innovationSector);
        assertEquals(competition.getResearchCategories(), researchCategories);
    }

    @Test
    public void test_findAll() throws Exception {
        List<Competition> competitions = Lists.newArrayList(new Competition());
        List<CompetitionResource> resources = Lists.newArrayList(new CompetitionResource());
        when(competitionRepositoryMock.findAll()).thenReturn(competitions);
        when(competitionMapperMock.mapToResource(competitions)).thenReturn(resources);

        List<CompetitionResource> response = service.findAll().getSuccessObjectOrThrowException();

        assertEquals(resources, response);
    }

    @Test
    public void test_findLiveCompetitions() throws Exception {
        List<Competition> competitions = Lists.newArrayList(new Competition());
        when(competitionRepositoryMock.findLive()).thenReturn(competitions);

        List<CompetitionSearchResultItem> response = service.findLiveCompetitions().getSuccessObjectOrThrowException();

        assertCompetitionSearchResultsEqualToCompetitions(competitions, response);
    }

    @Test
    public void test_findProjectSetupCompetitions() throws Exception {
        List<Competition> competitions = Lists.newArrayList(new Competition());
        when(competitionRepositoryMock.findProjectSetup()).thenReturn(competitions);

        List<CompetitionSearchResultItem> response = service.findProjectSetupCompetitions().getSuccessObjectOrThrowException();

        assertCompetitionSearchResultsEqualToCompetitions(competitions, response);
    }

    @Test
    public void test_findUpcomingCompetitions() throws Exception {
        List<Competition> competitions = Lists.newArrayList(new Competition());
        when(competitionRepositoryMock.findUpcoming()).thenReturn(competitions);

        List<CompetitionSearchResultItem> response = service.findUpcomingCompetitions().getSuccessObjectOrThrowException();

        assertCompetitionSearchResultsEqualToCompetitions(competitions, response);
    }

    @Test
    public void test_countCompetitions() throws Exception {
        Long countLive = 1L;
        Long countProjectSetup = 2L;
        Long countUpcoming = 3L;
        when(competitionRepositoryMock.countLive()).thenReturn(countLive);
        when(competitionRepositoryMock.countProjectSetup()).thenReturn(countProjectSetup);
        when(competitionRepositoryMock.countUpcoming()).thenReturn(countUpcoming);

        CompetitionCountResource response = service.countCompetitions().getSuccessObjectOrThrowException();

        assertEquals(countLive, response.getLiveCount());
        assertEquals(countProjectSetup, response.getProjectSetupCount());
        assertEquals(countUpcoming, response.getUpcomingCount());
    }

    @Test
    public void test_searchCompetitions() throws Exception {
        String searchQuery = "SearchQuery";
        String searchLike = "%" + searchQuery + "%";
        int page = 1;
        int size = 20;
        PageRequest pageRequest = new PageRequest(page, size);
        Page<Competition> queryResponse = mock(Page.class);
        long totalElements = 2L;
        int totalPages = 1;
        Competition competition = newCompetition().build();
        when(queryResponse.getTotalElements()).thenReturn(totalElements);
        when(queryResponse.getTotalPages()).thenReturn(totalPages);
        when(queryResponse.getNumber()).thenReturn(page);
        when(queryResponse.getNumberOfElements()).thenReturn(size);
        when(queryResponse.getContent()).thenReturn(singletonList(competition));
        when(competitionRepositoryMock.search(searchLike, pageRequest)).thenReturn(queryResponse);

        CompetitionSearchResult response = service.searchCompetitions(searchQuery, page, size).getSuccessObjectOrThrowException();

        assertEquals(totalElements, response.getTotalElements());
        assertEquals(totalPages, response.getTotalPages());
        assertEquals(page, response.getNumber());
        assertEquals(size, response.getSize());

        CompetitionSearchResultItem expectedSearchResult = new CompetitionSearchResultItem(competition.getId(),
                competition.getName(), null, 0, "", CompetitionStatus.COMPETITION_SETUP, "Comp Type",0);
        assertEquals(singletonList(expectedSearchResult), response.getContent());
    }

    private void assertCompetitionSearchResultsEqualToCompetitions(List<Competition> competitions, List<CompetitionSearchResultItem> searchResults) {

        assertEquals(competitions.size(), searchResults.size());

        forEachWithIndex(searchResults, (i, searchResult) -> {

            Competition originalCompetition = competitions.get(i);
            assertEquals(originalCompetition.getId(), searchResult.getId());
            assertEquals(originalCompetition.getName(), searchResult.getName());
        });
    }


    @Test
    public void test_closeAssessment() throws Exception {
        Long competitionId = 1L;
        List<Milestone> milestones = newMilestone()
                .withDate(LocalDateTime.now().minusDays(1))
                .withType(OPEN_DATE,SUBMISSION_DATE,ASSESSORS_NOTIFIED).build(3);
        milestones.addAll(newMilestone()
                .withDate(LocalDateTime.now().plusDays(1))
                .withType(NOTIFICATIONS, ASSESSOR_DEADLINE)
                .build(2));
        Competition competition = newCompetition().withSetupComplete(true)
                .withMilestones(milestones)
                .build();
        when(competitionRepositoryMock.findById(competitionId)).thenReturn(competition);

        service.closeAssessment(competitionId);

        assertEquals(CompetitionStatus.FUNDERS_PANEL,competition.getCompetitionStatus());
    }


    @Test
    public void test_notifyAssessors() throws Exception {
        Long competitionId = 1L;
        List<Milestone> milestones = newMilestone()
                .withDate(LocalDateTime.now().minusDays(1))
                .withType(OPEN_DATE,SUBMISSION_DATE,ALLOCATE_ASSESSORS).build(3);
        milestones.addAll(newMilestone()
                .withDate(LocalDateTime.now().plusDays(1))
                .withType(ASSESSMENT_CLOSED)
                .build(1));

        Competition competition = newCompetition().withSetupComplete(true)
                .withMilestones(milestones)
                .build();
        when(competitionRepositoryMock.findById(competitionId)).thenReturn(competition);

        service.notifyAssessors(competitionId);

        assertEquals(CompetitionStatus.IN_ASSESSMENT,competition.getCompetitionStatus());
    }
}
