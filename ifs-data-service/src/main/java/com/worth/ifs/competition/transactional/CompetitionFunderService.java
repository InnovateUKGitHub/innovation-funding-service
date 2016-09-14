package com.worth.ifs.competition.transactional;

import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.security.NotSecured;
import org.springframework.security.access.method.P;

/**
 * Service for operations around the usage and processing of Competition Co Funders
 */
public interface CompetitionFunderService {

    /**
     * @param competitionResource
     *
     * Method will clear all the exiting co-funders and reinsert the new ones. The reason we are clearing the old records is to handle the
     * deletion and update at the same time.
     */
    @NotSecured(value = "Not secured here, because this is only added extra information on the competition instance.", mustBeSecuredByOtherServices = true)
    void reinsertFunders(@P("competitionResource") final CompetitionResource competitionResource);

}
