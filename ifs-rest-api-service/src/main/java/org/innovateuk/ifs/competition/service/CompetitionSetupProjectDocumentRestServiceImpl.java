package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.competition.resource.CompetitionDocumentResource;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.projectDocumentResourceListType;

/**
 * Implements {@link CompetitionSetupProjectDocumentRestService}
 */
@Service
public class CompetitionSetupProjectDocumentRestServiceImpl extends BaseRestService implements CompetitionSetupProjectDocumentRestService {

    private String competitionSetupProjectDocumentRestURL = "/competition/setup/project-document";

    @Override
    public RestResult<CompetitionDocumentResource> save(CompetitionDocumentResource competitionDocumentResource) {
        return postWithRestResult(competitionSetupProjectDocumentRestURL + "/save", competitionDocumentResource, CompetitionDocumentResource.class);
    }

    @Override
    public RestResult<List<CompetitionDocumentResource>> save(List<CompetitionDocumentResource> competitionDocumentResources) {
        return postWithRestResult(competitionSetupProjectDocumentRestURL + "/save-all", competitionDocumentResources, projectDocumentResourceListType());
    }

    @Override
    public RestResult<CompetitionDocumentResource> findOne(long id) {
        return getWithRestResult(competitionSetupProjectDocumentRestURL + "/" + id, CompetitionDocumentResource.class);
    }

    @Override
    public RestResult<List<CompetitionDocumentResource>> findByCompetitionId(long competitionId) {
        return getWithRestResult(competitionSetupProjectDocumentRestURL + "/find-by-competition-id/" + competitionId, projectDocumentResourceListType());
    }

    @Override
    public RestResult<Void> delete(long id) {
        return deleteWithRestResult(competitionSetupProjectDocumentRestURL + "/" + id, Void.class);
    }
}

