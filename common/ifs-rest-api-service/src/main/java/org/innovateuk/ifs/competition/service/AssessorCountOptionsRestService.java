package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.AssessorCountOptionResource;

import java.util.List;


/**
 * Interface for CRUD operations on {@link AssessorCountOptionResource} related data.
 */
public interface AssessorCountOptionsRestService {

    RestResult<List<AssessorCountOptionResource>> findAllByCompetitionType(Long competitionTypeId);
}
