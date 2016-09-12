package com.worth.ifs.competition.mapper;

import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.domain.CompetitionFunder;
import com.worth.ifs.competition.resource.CompetitionFunderResource;
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



