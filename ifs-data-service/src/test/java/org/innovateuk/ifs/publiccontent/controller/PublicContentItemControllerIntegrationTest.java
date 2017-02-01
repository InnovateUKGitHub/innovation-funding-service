package org.innovateuk.ifs.publiccontent.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.category.repository.CategoryRepository;
import org.innovateuk.ifs.category.repository.CompetitionCategoryLinkRepository;
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

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.category.builder.CompetitionCategoryLinkBuilder.newCompetitionCategoryLink;
import static org.innovateuk.ifs.publiccontent.builder.KeywordBuilder.newKeyword;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentBuilder.newPublicContent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class PublicContentItemControllerIntegrationTest extends BaseControllerIntegrationTest<PublicContentItemController> {
    private static final Long COMPETITION_ID = 1L;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CompetitionCategoryLinkRepository competitionCategoryLinkRepository;

    @Autowired
    private PublicContentRepository publicContentRepository;

    @Autowired
    private KeywordRepository keywordRepository;

    @Override
    @Autowired
    protected void setControllerUnderTest(PublicContentItemController controller) {
        this.controller = controller;
    }


    @Before
    public void setLoggedInUserOnThread() {
        loginCompAdmin();
    }


    @Test
    @Rollback
    public void testFindFilteredItems() throws Exception {
        Competition competition = competitionRepository.findById(1L);

        competitionCategoryLinkRepository.save(newCompetitionCategoryLink()
                .withCategory(categoryRepository.findOne(1L))
                .withCompetition(competition)
                .build());

        makeSetupOfKeywords();

        flushAndClearSession();

        RestResult<PublicContentItemPageResource> resultOne = controller.findFilteredItems(Optional.of(17L), Optional.of("key wor"), Optional.of(0), Optional.of(20));

        assertTrue(resultOne.isSuccess());
        List<PublicContentItemResource> publicContentItemResourcesOne = resultOne.getSuccessObject().getContent();

        assertEquals(1, publicContentItemResourcesOne.size());
        assertEquals(competition.getName(), publicContentItemResourcesOne.get(0).getCompetitionTitle());

        RestResult<PublicContentItemPageResource> resultTwo = controller.findFilteredItems(Optional.of(17L), Optional.empty(), Optional.of(0), Optional.of(20));

        assertTrue(resultTwo.isSuccess());
        List<PublicContentItemResource> publicContentItemResourcesTwo = resultTwo.getSuccessObject().getContent();

        assertEquals(1, publicContentItemResourcesTwo.size());
        assertEquals(competition.getName(), publicContentItemResourcesTwo.get(0).getCompetitionTitle());

        RestResult<PublicContentItemPageResource> resultThree = controller.findFilteredItems(Optional.empty(), Optional.of("key wor"), Optional.of(0), Optional.of(20));

        assertTrue(resultThree.isSuccess());
        List<PublicContentItemResource> publicContentItemResourcesThree = resultThree.getSuccessObject().getContent();

        assertEquals(1, publicContentItemResourcesThree.size());
        assertEquals(competition.getName(), publicContentItemResourcesThree.get(0).getCompetitionTitle());


        RestResult<PublicContentItemPageResource> resultFour = controller.findFilteredItems(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());

        assertTrue(resultFour.isSuccess());
        List<PublicContentItemResource> publicContentItemResourcesFour = resultFour.getSuccessObject().getContent();

        assertEquals(1, publicContentItemResourcesFour.size());
        assertEquals(competition.getName(), publicContentItemResourcesFour.get(0).getCompetitionTitle());


        RestResult<PublicContentItemPageResource> resultFive = controller.findFilteredItems(Optional.empty(), Optional.of("Nothing key wor"), Optional.of(0), Optional.of(20));

        assertTrue(resultFive.isSuccess());
        List<PublicContentItemResource> publicContentItemResourcesFive = resultFive.getSuccessObject().getContent();

        assertEquals(1, publicContentItemResourcesFive.size());
        assertEquals(competition.getName(), publicContentItemResourcesFive.get(0).getCompetitionTitle());
    }

    @Test
    @Rollback
    public void testByCompetitionId() throws Exception {
        //Save competition, content
        flushAndClearSession();

        RestResult<PublicContentItemResource> result = controller.byCompetitionId(COMPETITION_ID);

        //assertTrue(result.isSuccess());

        //check result
    }

    private void makeSetupOfKeywords() {
        PublicContent publicContentOne = newPublicContent().withCompetitionId(1L).build();

        PublicContent publicContentResult = publicContentRepository.save(publicContentOne);

        Keyword keywordOne = newKeyword().withKeyword("keyword").withPublicContent(publicContentResult).build();
        Keyword keywordTwo = newKeyword().withKeyword("word").withPublicContent(publicContentResult).build();

        keywordRepository.save(asList(keywordOne, keywordTwo));
    }
}
