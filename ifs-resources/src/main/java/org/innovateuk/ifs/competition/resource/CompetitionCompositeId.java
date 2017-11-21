package org.innovateuk.ifs.competition.resource;

import org.innovateuk.ifs.util.CompositeId;


/**
 * TODO 
 */
public final class CompetitionCompositeId extends CompositeId {

    private CompetitionCompositeId(Long id) {
        super(id);
    }

    public static CompetitionCompositeId id(Long competitionId){
        return new CompetitionCompositeId(competitionId);
    }
}
