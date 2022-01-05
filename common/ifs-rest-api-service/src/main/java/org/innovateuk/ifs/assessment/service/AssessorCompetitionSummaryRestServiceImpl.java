package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.assessment.resource.AssessorCompetitionSummaryResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
public class AssessorCompetitionSummaryRestServiceImpl extends BaseRestService implements AssessorCompetitionSummaryRestService {

    private final String baseUrl = "/assessor/%s/competition/%s";

    @Override
    public RestResult<AssessorCompetitionSummaryResource> getAssessorSummary(long assessorId, long competitionId) {
        return getWithRestResult(format(baseUrl + "/summary", assessorId, competitionId), AssessorCompetitionSummaryResource.class);
    }
}
