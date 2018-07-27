package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.ProjectDocumentResource;

import java.util.List;

/**
 * Interface for CRUD operations on {@link ProjectDocumentResource} related data when in setup.
 */
public interface CompetitionSetupProjectDocumentRestService {

    RestResult<ProjectDocumentResource> save(ProjectDocumentResource projectDocumentResource);

    RestResult<List<ProjectDocumentResource>> save(List<ProjectDocumentResource> projectDocumentResources);

    RestResult<ProjectDocumentResource> findOne(long id);

    RestResult<List<ProjectDocumentResource>> findByCompetitionId(long competitionId);

    RestResult<Void> delete(Long id);
}
