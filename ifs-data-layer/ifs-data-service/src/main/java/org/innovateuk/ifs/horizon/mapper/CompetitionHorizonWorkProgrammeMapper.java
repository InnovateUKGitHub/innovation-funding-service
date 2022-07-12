package org.innovateuk.ifs.horizon.mapper;


import org.innovateuk.ifs.application.mapper.ApplicationMapper;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.competition.mapper.CompetitionMapper;
import org.innovateuk.ifs.horizon.domain.ApplicationHorizonWorkProgramme;
import org.innovateuk.ifs.horizon.domain.CompetitionHorizonWorkProgramme;
import org.innovateuk.ifs.horizon.domain.HorizonWorkProgramme;
import org.innovateuk.ifs.horizon.resource.ApplicationHorizonWorkProgrammeResource;
import org.innovateuk.ifs.horizon.resource.CompetitionHorizonWorkProgrammeResource;
import org.mapstruct.Mapper;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {CompetitionMapper.class, HorizonWorkProgrammeMapper.class}
)
public abstract class CompetitionHorizonWorkProgrammeMapper extends BaseMapper<CompetitionHorizonWorkProgramme, CompetitionHorizonWorkProgrammeResource, Long> {

    @Override
    public abstract CompetitionHorizonWorkProgrammeResource mapToResource(CompetitionHorizonWorkProgramme domain);

    public CompetitionHorizonWorkProgramme mapIdAndWorkProgrammeToDomain(long competitionId, HorizonWorkProgramme workProgramme) {
        CompetitionHorizonWorkProgramme competitionHorizonWorkProgramme = new CompetitionHorizonWorkProgramme();
        competitionHorizonWorkProgramme.setWorkProgramme(workProgramme);
        competitionHorizonWorkProgramme.setCompetitionId(competitionId);
        return competitionHorizonWorkProgramme;
    }

    public Long mapCompetitionHorizonWorkProgrammeToId(CompetitionHorizonWorkProgramme object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

}
