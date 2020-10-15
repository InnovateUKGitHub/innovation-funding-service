package org.innovateuk.ifs.cofunder.service;

import org.innovateuk.ifs.cofunder.resource.CofunderDashboardApplicationPageResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.springframework.web.bind.annotation.PathVariable;

public interface CofunderDashboardRestService {
    RestResult<CofunderDashboardApplicationPageResource> getCofunderCompetitionDashboardApplications(@PathVariable long userId, @PathVariable long competitionId, int page);
}
