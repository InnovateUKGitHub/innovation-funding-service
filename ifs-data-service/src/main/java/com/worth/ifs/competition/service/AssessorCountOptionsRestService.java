package com.worth.ifs.competition.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.competition.domain.AssessorCountOption;
import com.worth.ifs.competition.resource.AssessorCountOptionResource;

import java.util.List;


/**
 * Interface for CRUD operations on {@link AssessorCountOption} related data.
 */
public interface AssessorCountOptionsRestService {

    RestResult<List<AssessorCountOptionResource>> findAllByCompetitionType(Long competitionTypeId);
}
