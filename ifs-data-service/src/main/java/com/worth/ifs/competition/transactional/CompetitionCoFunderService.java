package com.worth.ifs.competition.transactional;

import com.worth.ifs.competition.resource.CompetitionResource;

/**
 * Service for operations around the usage and processing of Competition Co Funders
 */
public interface CompetitionCoFunderService {

    void reinsertCoFunders(final CompetitionResource competitionResource);

}
