package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.competition.resource.CompetitionOrganisationConfigResource;
import org.springframework.stereotype.Service;

@Service
public class CompetitionOrganisationConfigRestServiceImpl extends BaseRestService implements CompetitionOrganisationConfigRestService {

    private String competitionOrganisationConfigUrl = "/competition-organisation-config";

    @Override
    public RestResult<CompetitionOrganisationConfigResource> findByCompetitionId(long competitionId) {
        return getWithRestResultAnonymous(competitionOrganisationConfigUrl + "/find-by-competition-id/" + competitionId, CompetitionOrganisationConfigResource.class);
    }

    @Override
    public RestResult<CompetitionOrganisationConfigResource> update(long competitionId, CompetitionOrganisationConfigResource competitionOrganisationConfigResource) {
        return putWithRestResult(competitionOrganisationConfigUrl + "/update/" + competitionId, competitionOrganisationConfigResource, CompetitionOrganisationConfigResource.class);
    }
}