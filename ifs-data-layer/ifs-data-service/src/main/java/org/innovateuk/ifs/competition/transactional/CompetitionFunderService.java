package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.springframework.security.core.parameters.P;

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
