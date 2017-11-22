package org.innovateuk.ifs.competition.resource;

import org.innovateuk.ifs.util.CompositeId;


/**
 * Class to enable the spring security to apply type information when applying security rules to entity ids.
 * In this case determine that the id in question relates to a competition.
 */
public final class CompetitionCompositeId extends CompositeId {

    private CompetitionCompositeId(Long id) {
        super(id);
    }

    public static CompetitionCompositeId id(Long competitionId){
        return new CompetitionCompositeId(competitionId);
    }
}
