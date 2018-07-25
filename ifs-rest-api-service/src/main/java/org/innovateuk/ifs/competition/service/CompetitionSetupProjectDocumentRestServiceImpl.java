package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.competition.resource.ProjectDocumentResource;
import org.springframework.stereotype.Service;

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
    public RestResult<ProjectDocumentResource> findOne(Long id) {
        return getWithRestResult(competitionSetupProjectDocumentRestURL + "/" + id, ProjectDocumentResource.class);
    }
}

