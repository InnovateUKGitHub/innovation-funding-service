package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.competition.resource.CompetitionThirdPartyConfigResource;
import org.springframework.stereotype.Service;

@Service
public class CompetitionThirdPartyConfigRestServiceImpl extends BaseRestService implements CompetitionThirdPartyConfigRestService {

    private static String COMPETITION_THIRD_PARTY_CONFIG_URL = "/competition-third-party-config";

    @Override
    public RestResult<CompetitionThirdPartyConfigResource> findOneByCompetitionId(long competitionId) {
        return getWithRestResult(COMPETITION_THIRD_PARTY_CONFIG_URL + "/" + competitionId,
                CompetitionThirdPartyConfigResource.class);
    }

    @Override
    public RestResult<CompetitionThirdPartyConfigResource> create(CompetitionThirdPartyConfigResource competitionThirdPartyConfigResource) {
        return postWithRestResult(COMPETITION_THIRD_PARTY_CONFIG_URL, competitionThirdPartyConfigResource, CompetitionThirdPartyConfigResource.class);
    }

    @Override
    public RestResult<Void> update(long competitionId, CompetitionThirdPartyConfigResource competitionThirdPartyConfigResource) {
        return putWithRestResult(COMPETITION_THIRD_PARTY_CONFIG_URL + "/" + competitionId, competitionThirdPartyConfigResource, Void.class);
    }
}
