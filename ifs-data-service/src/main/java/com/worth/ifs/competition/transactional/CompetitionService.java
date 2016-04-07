package com.worth.ifs.competition.transactional;

import java.util.List;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.security.NotSecured;

/**
 * Service for operations around the usage and processing of Competitions
 */
public interface CompetitionService {

    @NotSecured("Anyone can see a competition")
    ServiceResult<CompetitionResource> getCompetitionById(final Long id);

    @NotSecured("Anyone can see all competitions")
    ServiceResult<List<CompetitionResource>> findAll();
}
