package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.competition.resource.ProjectDocumentResource;
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
    public RestResult<ProjectDocumentResource> save(ProjectDocumentResource projectDocumentResource) {
        return postWithRestResult(competitionSetupProjectDocumentRestURL + "/save", projectDocumentResource, ProjectDocumentResource.class);
    }

    @Override
    public RestResult<List<ProjectDocumentResource>> save(List<ProjectDocumentResource> projectDocumentResources) {
        return postWithRestResult(competitionSetupProjectDocumentRestURL + "/save-all", projectDocumentResources, projectDocumentResourceListType());
    }

    @Override
    public RestResult<ProjectDocumentResource> findOne(long id) {
        return getWithRestResult(competitionSetupProjectDocumentRestURL + "/" + id, ProjectDocumentResource.class);
    }

    @Override
    public RestResult<List<ProjectDocumentResource>> findByCompetitionId(long competitionId) {
        return getWithRestResult(competitionSetupProjectDocumentRestURL + "/find-by-competition-id/" + competitionId, projectDocumentResourceListType());
    }

    @Override
    public RestResult<Void> delete(long id) {
        return deleteWithRestResult(competitionSetupProjectDocumentRestURL + "/" + id, Void.class);
    }
}

