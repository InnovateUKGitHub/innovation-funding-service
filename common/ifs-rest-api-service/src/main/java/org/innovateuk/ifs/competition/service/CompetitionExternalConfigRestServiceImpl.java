package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.competition.resource.CompetitionExternalConfigResource;
import org.springframework.stereotype.Service;

@Service
public class CompetitionExternalConfigRestServiceImpl extends BaseRestService implements CompetitionExternalConfigRestService {

    private String competitionExternalConfigUrl = "/competition-external-config";

    @Override
    public RestResult<CompetitionExternalConfigResource> findOneByCompetitionId(long competitionId) {
        return getWithRestResult(competitionExternalConfigUrl + "/" + competitionId,
                CompetitionExternalConfigResource.class);
    }

    @Override
    public RestResult<CompetitionExternalConfigResource> update(long competitionId, CompetitionExternalConfigResource competitionExternalConfigResource) {
        return putWithRestResult(competitionExternalConfigUrl + "/" + competitionId, competitionExternalConfigResource, CompetitionExternalConfigResource.class);
    }
}
