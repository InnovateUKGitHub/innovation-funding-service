package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.competition.resource.PostAwardService;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
public class CompetitionSetupPostAwardServiceRestServiceImpl extends BaseRestService implements CompetitionSetupPostAwardServiceRestService {

    private String COMPETITIONS_POST_AWARD_REST_URL = "/competition/setup";

    @Override
    public RestResult<Void> setPostAwardService(long competitionId, PostAwardService postAwardService) {
        return postWithRestResult(format("%s/%d/%s/%s", COMPETITIONS_POST_AWARD_REST_URL, competitionId, "post-award-service", postAwardService.name()));
    }
}
