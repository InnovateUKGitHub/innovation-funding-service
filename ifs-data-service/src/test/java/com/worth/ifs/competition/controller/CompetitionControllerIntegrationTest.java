package com.worth.ifs.competition.controller;

import com.worth.ifs.BaseControllerIntegrationTest;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.competition.resource.CompetitionResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertTrue;

/**
 * Integration test for testing the rest servcies of the competition controller
 */
@Rollback
@Transactional
public class CompetitionControllerIntegrationTest extends BaseControllerIntegrationTest<CompetitionController> {


    private static final Long COMPETITION_ID = 1L;
    private static final Long NEW_COMPETITION_ID = 2L;

    @Override
    @Autowired
    protected void setControllerUnderTest(CompetitionController controller) {
        this.controller = controller;
    }

    @Before
    public void setLoggedInUserOnThread() {
        loginCompAdmin();
    }

    @Rollback
    @Test
    public void testGetAllCompetitions() throws Exception {
        RestResult<List<CompetitionResource>> allCompetitionsResult = controller.findAll();
        assertTrue(allCompetitionsResult.isSuccess());
        List<CompetitionResource> competitions = allCompetitionsResult.getSuccessObject();

        assertThat(competitions, hasSize(1));
        checkExistingCompetition(competitions.get(0));
    }

    @Rollback
    @Test
    public void testGetOneCompetitions() throws Exception {
        RestResult<CompetitionResource> competitionsResult = controller.getCompetitionById(COMPETITION_ID);
        assertTrue(competitionsResult.isSuccess());
        CompetitionResource competition = competitionsResult.getSuccessObject();

        checkExistingCompetition(competition);
    }


    @Rollback
    @Test
    public void testCreateCompetition() throws Exception {
        RestResult<CompetitionResource> competitionsResult = controller.create();
        assertTrue(competitionsResult.isSuccess());
        CompetitionResource competition = competitionsResult.getSuccessObject();
        assertThat(competition.getId(), is(NEW_COMPETITION_ID));
        assertThat(competition.getName(), isEmptyOrNullString());

        RestResult<List<CompetitionResource>> allCompetitionsResult = controller.findAll();
        assertTrue(allCompetitionsResult.isSuccess());
        List<CompetitionResource> competitions = allCompetitionsResult.getSuccessObject();
        assertThat(competitions, hasSize(2));

        checkExistingCompetition(competitions.get(0));
        checkNewCompetition(competitions.get(1));
    }

    private void checkExistingCompetition(CompetitionResource competition) {
        assertThat(competition, notNullValue());
        assertThat(competition.getId(), is(COMPETITION_ID));
        assertThat(competition.getName(), is("Connected digital additive manufacturing"));
        assertThat(competition.getCompetitionStatus(), is(CompetitionResource.Status.OPEN));
    }
    private void checkNewCompetition(CompetitionResource competition) {
        assertThat(competition, notNullValue());
        assertThat(competition.getId(), is(NEW_COMPETITION_ID));
        assertThat(competition.getName(), isEmptyOrNullString());
        assertThat(competition.getCompetitionStatus(), is(CompetitionResource.Status.PROJECT_SETUP));
    }
}
