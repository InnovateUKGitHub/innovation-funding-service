package com.worth.ifs.competition.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.security.NotSecured;

import java.util.List;

/**
 * Service for operations around the usage and processing of Competitions
 */
public interface CompetitionService {

    @NotSecured("TODO DW - secure when permissions known")
    ServiceResult<Competition> getCompetitionById(final Long id);

    @NotSecured("TODO DW - secure when permissions known")
    ServiceResult<List<Competition>> findAll();
}
