package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.PostAwardService;

public interface CompetitionSetupPostAwardServiceRestService {
    RestResult<Void> setPostAwardService(long competitionId, PostAwardService postAwardService);
}
