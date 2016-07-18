package com.worth.ifs.competition.mapper;

import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.domain.CompetitionCoFunder;
import com.worth.ifs.competition.resource.CompetitionCoFunderResource;
import org.mapstruct.Mapper;

/**
 * Created by skistapur on 18/07/2016.
 */
@Mapper(config = GlobalMapperConfig.class)
public abstract class CompetitionCoFunderMapper {


    public CompetitionCoFunderResource mapToResource(CompetitionCoFunder domain) {
        CompetitionCoFunderResource resource = new CompetitionCoFunderResource();
        resource.setCoFunder(domain.getCoFunder());
        resource.setCoFunderBudget(domain.getCoFunderBudget());
        resource.setCompetitionId(domain.getCompetition().getId());
        return resource;
    }

    public CompetitionCoFunder mapToDomain(CompetitionCoFunderResource resource) {
        CompetitionCoFunder domain = new CompetitionCoFunder();
        domain.setCoFunder(resource.getCoFunder());
        domain.setCoFunderBudget(resource.getCoFunderBudget());
        Competition competition = new Competition();
        competition.setId(resource.getCompetitionId());
        domain.setCompetition(competition);
        return domain;
    }


}



