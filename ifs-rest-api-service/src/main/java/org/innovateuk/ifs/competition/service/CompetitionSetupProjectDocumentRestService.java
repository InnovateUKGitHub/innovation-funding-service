package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.ProjectDocumentResource;

/**
 * Interface for CRUD operations on {@link ProjectDocumentResource} related data when in setup.
 */
public interface CompetitionSetupProjectDocumentRestService {

    RestResult<ProjectDocumentResource> save(ProjectDocumentResource projectDocumentResource);

    RestResult<ProjectDocumentResource> findOne(Long id);

    RestResult<Void> delete(Long id);
}
