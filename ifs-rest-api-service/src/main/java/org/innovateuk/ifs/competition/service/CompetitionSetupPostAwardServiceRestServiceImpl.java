package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.competition.resource.CompetitionPostAwardServiceResource;
import org.innovateuk.ifs.competition.resource.PostAwardService;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
public class CompetitionSetupPostAwardServiceRestServiceImpl extends BaseRestService implements CompetitionSetupPostAwardServiceRestService {

    private static final String COMPETITIONS_POST_AWARD_REST_URL = "/competition/setup";
    private static final String POST_AWARD_SERVICE = "post-award-service";

    @Override
    public RestResult<CompetitionPostAwardServiceResource> getPostAwardService(long competitionId) {
        return getWithRestResult(format("%s/%d/%s", COMPETITIONS_POST_AWARD_REST_URL, competitionId, POST_AWARD_SERVICE), CompetitionPostAwardServiceResource.class);
    }

    @Override
    public RestResult<Void> setPostAwardService(long competitionId, PostAwardService postAwardService) {
        return postWithRestResult(format("%s/%d/%s/%s", COMPETITIONS_POST_AWARD_REST_URL, competitionId, POST_AWARD_SERVICE, postAwardService.name()));
    }
}
