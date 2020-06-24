package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionPostAwardServiceResource;
import org.innovateuk.ifs.competition.resource.PostAwardService;
import org.springframework.security.access.prepost.PreAuthorize;

public interface CompetitionSetupPostAwardServiceService {

    @PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionResource', 'CHOOSE_POST_AWARD_SERVICE')")
    ServiceResult<Void> configurePostAwardService(long competitionId, PostAwardService postAwardService);

    @PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionResource', 'CHOOSE_POST_AWARD_SERVICE')")
    ServiceResult<CompetitionPostAwardServiceResource> getPostAwardService(long competitionId);
}
