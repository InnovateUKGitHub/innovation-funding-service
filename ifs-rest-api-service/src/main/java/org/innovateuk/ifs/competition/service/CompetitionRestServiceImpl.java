package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionTypeResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.*;

/**
 * CompetitionsRestServiceImpl is a utility for CRUD operations on {@link CompetitionResource}.
 * This class connects to the { org.innovateuk.ifs.competition.controller.CompetitionController}
 * through a REST call.
 */
@Service
public class CompetitionRestServiceImpl extends BaseRestService implements CompetitionRestService {

    private static final String COMPETITION_REST_SERVICE = "/competition";
    private static final String COMPETITION_TYPE_REST_SERVICE = "/competition-type";

    @Override
    public RestResult<List<CompetitionResource>> getAll() {
        return getWithRestResult(format("%s/%s", COMPETITION_REST_SERVICE, "find-all"), competitionResourceListType());
    }

    @Override
    public RestResult<CompetitionResource> getCompetitionById(long competitionId) {
        return getWithRestResult(format("%s/%d", COMPETITION_REST_SERVICE, competitionId), CompetitionResource.class);
    }

    @Override
    public RestResult<List<OrganisationTypeResource>> getCompetitionOrganisationType(long competitionId) {
        return getWithRestResultAnonymous(format("%s/%d/%s", COMPETITION_REST_SERVICE, competitionId, "get-organisation-types"), organisationTypeResourceListType());
    }

    @Override
    public RestResult<CompetitionResource> getPublishedCompetitionById(long competitionId) {
        return getWithRestResultAnonymous(format("%s/%d", COMPETITION_REST_SERVICE, competitionId), CompetitionResource.class);
    }

    @Override
    public RestResult<Void> updateTermsAndConditionsForCompetition(long competitionId, long termsAndConditionsId) {
        return putWithRestResult(format("%s/%d/%s/%d", COMPETITION_REST_SERVICE, competitionId, "update-terms-and-conditions", termsAndConditionsId), Void.class);
    }

    @Override
    public RestResult<List<CompetitionTypeResource>> getCompetitionTypes() {
        return getWithRestResult(format("%s/%s", COMPETITION_TYPE_REST_SERVICE, "find-all"), competitionTypeResourceListType());
    }

    @Override
    public RestResult<ByteArrayResource> downloadTerms(long competitionId) {
        return getWithRestResult(format("%s/%d/terms-and-conditions", COMPETITION_REST_SERVICE, competitionId), ByteArrayResource.class);
    }
}