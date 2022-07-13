package org.innovateuk.ifs.horizon.mapper;


import org.innovateuk.ifs.application.mapper.ApplicationMapper;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.horizon.domain.ApplicationHorizonWorkProgramme;
import org.innovateuk.ifs.horizon.domain.HorizonWorkProgramme;
import org.innovateuk.ifs.horizon.resource.ApplicationHorizonWorkProgrammeResource;
import org.mapstruct.Mapper;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {ApplicationMapper.class, HorizonWorkProgrammeMapper.class}
)
public abstract class ApplicationHorizonWorkProgrammeMapper extends BaseMapper<ApplicationHorizonWorkProgramme, ApplicationHorizonWorkProgrammeResource, Long> {

    @Override
    public abstract ApplicationHorizonWorkProgrammeResource mapToResource(ApplicationHorizonWorkProgramme domain);

    public ApplicationHorizonWorkProgramme mapIdAndWorkProgrammeToDomain(long applicationId, HorizonWorkProgramme workProgramme) {
        ApplicationHorizonWorkProgramme applicationHorizonWorkProgramme = new ApplicationHorizonWorkProgramme();
        applicationHorizonWorkProgramme.setWorkProgramme(workProgramme);
        applicationHorizonWorkProgramme.setApplicationId(applicationId);
        return applicationHorizonWorkProgramme;
    }

    public Long mapApplicationHorizonWorkProgrammeToId(ApplicationHorizonWorkProgramme object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

}
