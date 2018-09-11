package org.innovateuk.ifs.competition.security;

import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.innovateuk.ifs.competition.mapper.CompetitionTypeMapper;
import org.innovateuk.ifs.competition.repository.CompetitionTypeRepository;
import org.innovateuk.ifs.competition.resource.CompetitionTypeResource;
import org.springframework.stereotype.Component;

@Component
@PermissionEntityLookupStrategies
public class CompetitionTypeLookupStrategy {

    private CompetitionTypeRepository competitionTypeRepository;
    private CompetitionTypeMapper competitionTypeMapper;

    public CompetitionTypeLookupStrategy(CompetitionTypeRepository competitionTypeRepository,
                                         CompetitionTypeMapper competitionTypeMapper) {
        this.competitionTypeRepository = competitionTypeRepository;
        this.competitionTypeMapper = competitionTypeMapper;
    }

    @PermissionEntityLookupStrategy
    public CompetitionTypeResource getCompetititionTypeResource(Long competitionTypeId) {
        return competitionTypeMapper.mapToResource(competitionTypeRepository.findOne(competitionTypeId));
    }
}
