package org.innovateuk.ifs.competition.security;

import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.innovateuk.ifs.competition.resource.CompetitionCompositeId;
import org.springframework.stereotype.Component;

@Component
@PermissionEntityLookupStrategies
public class CompetitionLookupStrategy {

    @PermissionEntityLookupStrategy
    public CompetitionCompositeId getCompetitionCompositeId(Long competitionId) {
        return CompetitionCompositeId.id(competitionId);
    }
}