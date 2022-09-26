package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.competition.resource.CompetitionEoiEvidenceConfigResource;
import org.springframework.stereotype.Service;

@Service
public class CompetitionEoiEvidenceConfigRestServiceImpl extends BaseRestService implements CompetitionEoiEvidenceConfigRestService {

    private static String URL = "/competition";

    @Override
    public RestResult<CompetitionEoiEvidenceConfigResource> findOneByCompetitionId(long competitionId) {
        return getWithRestResult(URL + "/" +  competitionId + "/eoi-evidence-config" , CompetitionEoiEvidenceConfigResource.class);
    }
}
