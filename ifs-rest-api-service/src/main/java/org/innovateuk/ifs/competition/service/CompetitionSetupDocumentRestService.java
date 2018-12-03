package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionDocumentResource;

import java.util.List;

/**
 * Interface for CRUD operations on {@link CompetitionDocumentResource} related data when in setup.
 */
public interface CompetitionSetupDocumentRestService {

    RestResult<CompetitionDocumentResource> save(CompetitionDocumentResource competitionDocumentResource);

    RestResult<List<CompetitionDocumentResource>> save(List<CompetitionDocumentResource> competitionDocumentResources);

    RestResult<CompetitionDocumentResource> findOne(long id);

    RestResult<List<CompetitionDocumentResource>> findByCompetitionId(long competitionId);

    RestResult<Void> delete(long id);
}
