package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.competition.resource.CompetitionAssessmentConfigResource;
import org.springframework.stereotype.Service;

@Service
public class CompetitionAssessmentConfigRestServiceImpl extends BaseRestService implements CompetitionAssessmentConfigRestService {

    private String competitionAssessmentConfigUrl = "/competition-assessment-config";

    @Override
    public RestResult<CompetitionAssessmentConfigResource> findOneByCompetitionId(long competitionId) {
        return getWithRestResult(competitionAssessmentConfigUrl + "/" + competitionId,
                CompetitionAssessmentConfigResource.class);
    }

    @Override
    public RestResult<CompetitionAssessmentConfigResource> update(long competitionId, CompetitionAssessmentConfigResource competitionAssessmentConfigResource) {
        return putWithRestResult(competitionAssessmentConfigUrl + "/" + competitionId, competitionAssessmentConfigResource, CompetitionAssessmentConfigResource.class);
    }
}
