package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.competition.resource.CompetitionThirdPartyConfigResource;
import org.springframework.stereotype.Service;

@Service
public class CompetitionThirdPartyConfigRestServiceImpl extends BaseRestService implements CompetitionThirdPartyConfigRestService {

    private String competitionThirdPartyConfigUrl = "/competition-third-party-config";

    @Override
    public RestResult<CompetitionThirdPartyConfigResource> findOneByCompetitionId(long competitionId) {
        return getWithRestResult(competitionThirdPartyConfigUrl + "/" + competitionId,
                CompetitionThirdPartyConfigResource.class);
    }
}
