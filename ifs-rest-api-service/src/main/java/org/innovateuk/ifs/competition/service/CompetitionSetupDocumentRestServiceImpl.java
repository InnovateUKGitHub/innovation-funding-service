package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.competition.resource.CompetitionDocumentResource;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.competitionDocumentResourceListType;

/**
 * Implements {@link CompetitionSetupDocumentRestService}
 */
@Service
public class CompetitionSetupDocumentRestServiceImpl extends BaseRestService implements CompetitionSetupDocumentRestService {

    private String competitionSetupProjectDocumentRestURL = "/competition/setup/project-document";

    @Override
    public RestResult<CompetitionDocumentResource> save(CompetitionDocumentResource competitionDocumentResource) {
        return postWithRestResult(competitionSetupProjectDocumentRestURL + "/save", competitionDocumentResource, CompetitionDocumentResource.class);
    }

    @Override
    public RestResult<List<CompetitionDocumentResource>> save(List<CompetitionDocumentResource> competitionDocumentResources) {
        return postWithRestResult(competitionSetupProjectDocumentRestURL + "/save-all", competitionDocumentResources, competitionDocumentResourceListType());
    }

    @Override
    public RestResult<CompetitionDocumentResource> findOne(long id) {
        return getWithRestResult(competitionSetupProjectDocumentRestURL + "/" + id, CompetitionDocumentResource.class);
    }

    @Override
    public RestResult<List<CompetitionDocumentResource>> findByCompetitionId(long competitionId) {
        return getWithRestResult(competitionSetupProjectDocumentRestURL + "/find-by-competition-id/" + competitionId, competitionDocumentResourceListType());
    }

    @Override
    public RestResult<Void> delete(long id) {
        return deleteWithRestResult(competitionSetupProjectDocumentRestURL + "/" + id, Void.class);
    }
}

