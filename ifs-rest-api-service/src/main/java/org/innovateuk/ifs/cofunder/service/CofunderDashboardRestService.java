package org.innovateuk.ifs.cofunder.service;

import org.innovateuk.ifs.cofunder.resource.CofunderDashboardCompetitionResource;
import org.innovateuk.ifs.commons.rest.RestResult;

public interface CofunderDashboardRestService {
    RestResult<CofunderDashboardCompetitionResource> getCofunderCompetitionDashboard(long userId);
}
