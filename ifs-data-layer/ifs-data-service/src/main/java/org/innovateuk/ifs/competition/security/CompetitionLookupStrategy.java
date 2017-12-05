package org.innovateuk.ifs.competition.security;

import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.mapper.CompetitionMapper;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.resource.CompetitionCompositeId;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Class for providing competition resource and domain objects for security checks
 */

@Component
@PermissionEntityLookupStrategies
public class CompetitionLookupStrategy {

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private CompetitionMapper competitionMapper;

    @PermissionEntityLookupStrategy
    public Competition getCompetition(Long competitionId) {
        return competitionRepository.findOne(competitionId);
    }

    @PermissionEntityLookupStrategy
    public CompetitionResource getCompetititionResource(Long competitionId) {
        return competitionMapper.mapToResource(competitionRepository.findOne(competitionId));
    }

    @PermissionEntityLookupStrategy
    public CompetitionCompositeId getCompetitionCompositeId(Long competitionId) {
        return CompetitionCompositeId.id(competitionId);
    }
}