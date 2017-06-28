package org.innovateuk.ifs.competition.mapper;

import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionFunder;
import org.innovateuk.ifs.competition.resource.CompetitionFunderResource;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapperConfig.class)
public abstract class CompetitionFunderMapper {

    public CompetitionFunderResource mapToResource(CompetitionFunder domain) {
        CompetitionFunderResource resource = new CompetitionFunderResource();
        resource.setFunder(domain.getFunder());
        resource.setId(domain.getId());
        resource.setFunderBudget(domain.getFunderBudget());
        resource.setCoFunder(domain.getCoFunder());
        if (domain.getCompetition() != null) {
            resource.setCompetitionId(domain.getCompetition().getId());
        }
        return resource;
    }

    public CompetitionFunder mapToDomain(CompetitionFunderResource resource) {
        CompetitionFunder domain = new CompetitionFunder();
        domain.setId(resource.getId());
        domain.setFunder(resource.getFunder());
        domain.setFunderBudget(resource.getFunderBudget());
        domain.setCoFunder(resource.getCoFunder());
        Competition competition = new Competition();
        competition.setId(resource.getCompetitionId());
        domain.setCompetition(competition);
        return domain;
    }


}



