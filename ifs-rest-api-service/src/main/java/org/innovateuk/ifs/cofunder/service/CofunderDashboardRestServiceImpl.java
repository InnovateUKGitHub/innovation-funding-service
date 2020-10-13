package org.innovateuk.ifs.cofunder.service;

import org.innovateuk.ifs.cofunder.resource.AssessorDashboardState;
import org.innovateuk.ifs.cofunder.resource.CofunderDashboardCompetitionResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.mapAssessorDashboardStateToCofunderDashboardCompetitionResourceListType;

@Service
public class CofunderDashboardRestServiceImpl extends BaseRestService implements CofunderDashboardRestService {

    private final String baseUrl = "/assessment/user/%d";

    @Override
    public RestResult<Map<AssessorDashboardState, List<CofunderDashboardCompetitionResource>>> getCofunderCompetitionDashboard(long userId) {
        return getWithRestResult(format(baseUrl + "/dashboard", userId), mapAssessorDashboardStateToCofunderDashboardCompetitionResourceListType());
    }
}
