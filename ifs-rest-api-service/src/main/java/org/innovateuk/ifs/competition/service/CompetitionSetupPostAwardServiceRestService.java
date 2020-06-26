package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionPostAwardServiceResource;
import org.innovateuk.ifs.competition.resource.PostAwardService;

public interface CompetitionSetupPostAwardServiceRestService {
    RestResult<CompetitionPostAwardServiceResource> getPostAwardService(long competitionId);
    RestResult<Void> setPostAwardService(long competitionId, PostAwardService postAwardService);
}
