package org.innovateuk.ifs.publiccontent.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.category.domain.Category;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.repository.CategoryRepository;
import org.innovateuk.ifs.category.repository.InnovationAreaRepository;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemPageResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.publiccontent.domain.Keyword;
import org.innovateuk.ifs.publiccontent.domain.PublicContent;
import org.innovateuk.ifs.publiccontent.repository.KeywordRepository;
import org.innovateuk.ifs.publiccontent.repository.PublicContentRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.publiccontent.builder.KeywordBuilder.newKeyword;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentBuilder.newPublicContent;
import static org.junit.Assert.*;


public class PublicContentItemControllerIntegrationTest extends BaseControllerIntegrationTest<PublicContentItemController> {
    private static final Long COMPETITION_ID = 1L;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PublicContentRepository publicContentRepository;

    @Autowired
    private KeywordRepository keywordRepository;

    @Autowired
    private InnovationAreaRepository innovationAreaRepository;

    @Override
    @Autowired
    protected void setControllerUnderTest(PublicContentItemController controller) {
        this.controller = controller;
    }


    @Before
    public void setLoggedInUserOnThread() {
        loginSystemRegistrationUser();
        setupKeywords();
    }


    @Test
    @Rollback
    public void findFilteredItems_findByKeywords() throws Exception {
        Competition competition = competitionRepository.findById(COMPETITION_ID);
        long innovationId = 5L;
        Category category = categoryRepository.findOne(innovationId);

        flushAndClearSession();

        RestResult<PublicContentItemPageResource> resultOne = controller.findFilteredItems(Optional.of(innovationId), Optional.of("key wor"), Optional.of(0), 20);

        assertTrue(resultOne.isSuccess());
        List<PublicContentItemResource> publicContentItemResourcesOne = resultOne.getSuccessObject().getContent();

        assertEquals(1, publicContentItemResourcesOne.size());
        assertEquals(competition.getName(), publicContentItemResourcesOne.get(0).getCompetitionTitle());

        RestResult<PublicContentItemPageResource> resultTwo = controller.findFilteredItems(Optional.of(innovationId), Optional.empty(), Optional.of(0),20);

        assertTrue(resultTwo.isSuccess());
        List<PublicContentItemResource> publicContentItemResourcesTwo = resultTwo.getSuccessObject().getContent();

        assertEquals(1, publicContentItemResourcesTwo.size());
        assertEquals(competition.getName(), publicContentItemResourcesTwo.get(0).getCompetitionTitle());

        RestResult<PublicContentItemPageResource> resultThree = controller.findFilteredItems(Optional.empty(), Optional.of("key wor"), Optional.of(0), 20);

        assertTrue(resultThree.isSuccess());
        List<PublicContentItemResource> publicContentItemResourcesThree = resultThree.getSuccessObject().getContent();

        assertEquals(1, publicContentItemResourcesThree.size());
        assertEquals(competition.getName(), publicContentItemResourcesThree.get(0).getCompetitionTitle());


        RestResult<PublicContentItemPageResource> resultFour = controller.findFilteredItems(Optional.empty(), Optional.empty(), Optional.empty(), 20);

        assertTrue(resultFour.isSuccess());
        List<PublicContentItemResource> publicContentItemResourcesFour = resultFour.getSuccessObject().getContent();

        assertEquals(1, publicContentItemResourcesFour.size());
        assertEquals(competition.getName(), publicContentItemResourcesFour.get(0).getCompetitionTitle());


        RestResult<PublicContentItemPageResource> resultFive = controller.findFilteredItems(Optional.empty(), Optional.of("Nothing key wor"), Optional.of(0), 20);

        assertTrue(resultFive.isSuccess());
        List<PublicContentItemResource> publicContentItemResourcesFive = resultFive.getSuccessObject().getContent();

        assertEquals(1, publicContentItemResourcesFive.size());
        assertEquals(competition.getName(), publicContentItemResourcesFive.get(0).getCompetitionTitle());
    }

    @Test
    @Rollback
    public void findFilteredItems_findAllPublicContent() throws Exception {
        Competition competition = competitionRepository.findById(COMPETITION_ID);

        flushAndClearSession();

        RestResult<PublicContentItemPageResource> resultOne = controller.findFilteredItems(Optional.empty(), Optional.empty(), Optional.empty(), 10);

        assertTrue(resultOne.isSuccess());
        List<PublicContentItemResource> publicContentItemResourcesOne = resultOne.getSuccessObject().getContent();

        assertEquals(1, resultOne.getSuccessObject().getTotalElements());
        assertEquals(competition.getName(), publicContentItemResourcesOne.get(0).getCompetitionTitle());
    }

    @Test
    @Rollback
    public void findFilteredItems_findByInnovationAreaId() throws Exception {
        Competition competition = competitionRepository.findById(COMPETITION_ID);
        long innovationId = 5L;
        Category category = categoryRepository.findOne(innovationId);

        flushAndClearSession();

        RestResult<PublicContentItemPageResource> resultOne = controller.findFilteredItems(Optional.of(innovationId), Optional.empty(), Optional.empty(), 10);

        assertTrue(resultOne.isSuccess());
        List<PublicContentItemResource> publicContentItemResourcesOne = resultOne.getSuccessObject().getContent();

        assertEquals(1, publicContentItemResourcesOne.size());
        assertEquals(competition.getName(), publicContentItemResourcesOne.get(0).getCompetitionTitle());
    }

    @Test
    @Rollback
    public void findFilteredItems_findByKeywordsAndInnovationAreaId() throws Exception {
        Competition competition = competitionRepository.findById(COMPETITION_ID);
        long innovationId = 5L;
        Category category = categoryRepository.findOne(innovationId);

        flushAndClearSession();

        RestResult<PublicContentItemPageResource> resultOne = controller.findFilteredItems(Optional.of(innovationId), Optional.of("Nothing key wor"), Optional.empty(), 10);

        assertTrue(resultOne.isSuccess());
        List<PublicContentItemResource> publicContentItemResourcesOne = resultOne.getSuccessObject().getContent();

        assertEquals(1, publicContentItemResourcesOne.size());
        assertEquals(competition.getName(), publicContentItemResourcesOne.get(0).getCompetitionTitle());
    }

    @Test
    @Rollback
    public void findFilteredItems_openCompetitionsAreFilteredFromResultListAndTotalFound() throws Exception {
        RestResult<PublicContentItemPageResource> result = controller.findFilteredItems(Optional.empty(), Optional.of("Nothing key wor"), Optional.of(0), 20);

        assertTrue(result.isSuccess());
        PublicContentItemPageResource publicContentItemResourcesFive = result.getSuccessObject();

        assertEquals(1, publicContentItemResourcesFive.getTotalElements());
    }

    @Test
    @Rollback
    public void testByCompetitionId() throws Exception {
        RestResult<PublicContentItemResource> result = controller.byCompetitionId(COMPETITION_ID);

        assertTrue(result.isSuccess());

        PublicContentItemResource resultObject = result.getSuccessObject();
        assertEquals(COMPETITION_ID, resultObject.getPublicContentResource().getCompetitionId());
    }

    private void setupKeywords() {
        PublicContent publicContentResult = publicContentRepository.save(newPublicContent()
                .withCompetitionId(1L)
                .withPublishDate(ZonedDateTime.now().minusDays(1))
                .build());

        Keyword keywordOne = newKeyword().withKeyword("keyword").withPublicContent(publicContentResult).build();
        Keyword keywordTwo = newKeyword().withKeyword("word").withPublicContent(publicContentResult).build();
        Keyword keywordThree = newKeyword().withKeyword("unique").withPublicContent(publicContentResult).build();

        keywordRepository.save(asList(keywordOne, keywordTwo, keywordThree));

        InnovationArea innovationArea = innovationAreaRepository.findOne(5L);
        Competition competition = competitionRepository.findOne(1L);
        competition.setInnovationSector(innovationArea.getSector());

        competitionRepository.save(competition);
    }
}
