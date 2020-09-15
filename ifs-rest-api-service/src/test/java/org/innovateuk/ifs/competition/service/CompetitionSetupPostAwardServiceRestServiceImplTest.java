package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionPostAwardServiceResource;
import org.innovateuk.ifs.competition.resource.PostAwardService;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CompetitionSetupPostAwardServiceRestServiceImplTest extends
        BaseRestServiceUnitTest<CompetitionSetupPostAwardServiceRestServiceImpl> {

    private static final String COMPETITIONS_POST_AWARD_REST_URL = "/competition/setup";

    @Override
    protected CompetitionSetupPostAwardServiceRestServiceImpl registerRestServiceUnderTest() {
        return new CompetitionSetupPostAwardServiceRestServiceImpl();
    }

    @Test
    public void setPostAwardService() {
        // given
        Long competitionId = 4L;
        PostAwardService postAwardService = PostAwardService.CONNECT;

        setupPostWithRestResultExpectations(format("%s/%d/%s/%s", COMPETITIONS_POST_AWARD_REST_URL, competitionId,
                "post-award-service", "CONNECT"), HttpStatus.OK);

        // when
        RestResult<Void> result = service.setPostAwardService(competitionId, postAwardService);

        // then
        assertTrue(result.isSuccess());
    }

    @Test
    public void getPostAwardService() {
        // given
        Long competitionId = 4L;
        CompetitionPostAwardServiceResource competitionPostAwardService = new CompetitionPostAwardServiceResource();
        competitionPostAwardService.setCompetitionId(competitionId);
        competitionPostAwardService.setPostAwardService(PostAwardService.CONNECT);

        setupGetWithRestResultExpectations(format("%s/%d/%s", COMPETITIONS_POST_AWARD_REST_URL, competitionId,
                "post-award-service"), CompetitionPostAwardServiceResource.class, competitionPostAwardService);

        // when
        RestResult<CompetitionPostAwardServiceResource> result = service.getPostAwardService(competitionId);

        // then
        assertTrue(result.isSuccess());
        assertEquals(competitionPostAwardService, result.getSuccess());
    }
}
