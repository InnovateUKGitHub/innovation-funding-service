package com.worth.ifs.competition.transactional;

import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.security.NotSecured;
import org.springframework.security.access.method.P;

/**
 * Service for operations around the usage and processing of Competition Co Funders
 */
public interface CompetitionCoFunderService {

    @NotSecured(value = "Not secured here, because this is only added extra information on the competition instance.", mustBeSecuredByOtherServices = true)
    void reinsertCoFunders(@P("competitionResource") final CompetitionResource competitionResource);

}
