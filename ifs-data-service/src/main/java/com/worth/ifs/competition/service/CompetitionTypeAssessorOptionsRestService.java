package com.worth.ifs.competition.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.competition.resource.CompetitionTypeAssessorOptionResource;

import java.util.List;


/**
 * Interface for CRUD operations on {@link com.worth.ifs.competition.domain.CompetitionTypeAssessorOption} related data.
 */
public interface CompetitionTypeAssessorOptionsRestService {

    RestResult<List<CompetitionTypeAssessorOptionResource>> findAllByCompetitionType(Long competitionTypeId);
}
