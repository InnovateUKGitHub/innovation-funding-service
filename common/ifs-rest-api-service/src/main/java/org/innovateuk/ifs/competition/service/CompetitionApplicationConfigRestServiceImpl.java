package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.competition.resource.CompetitionApplicationConfigResource;
import org.springframework.stereotype.Service;

@Service
public class CompetitionApplicationConfigRestServiceImpl extends BaseRestService implements CompetitionApplicationConfigRestService {

    private static String COMPETITION_APPLICATION_CONFIG_URL = "/competition-application-config";

    @Override
    public RestResult<CompetitionApplicationConfigResource> findOneByCompetitionId(long competitionId) {
        return getWithRestResult(COMPETITION_APPLICATION_CONFIG_URL + "/" + competitionId,
                CompetitionApplicationConfigResource.class);
    }

    @Override
    public RestResult<CompetitionApplicationConfigResource> update(long competitionId, CompetitionApplicationConfigResource competitionApplicationConfigResource) {
        return putWithRestResult(COMPETITION_APPLICATION_CONFIG_URL + "/" + competitionId, competitionApplicationConfigResource, CompetitionApplicationConfigResource.class);
    }

    @Override
    public RestResult<CompetitionApplicationConfigResource> updateImpactSurvey(long competitionId, CompetitionApplicationConfigResource competitionApplicationConfigResource) {
        return putWithRestResult(COMPETITION_APPLICATION_CONFIG_URL + "/" + competitionId+"/impact-survey", competitionApplicationConfigResource, CompetitionApplicationConfigResource.class);
    }
}
