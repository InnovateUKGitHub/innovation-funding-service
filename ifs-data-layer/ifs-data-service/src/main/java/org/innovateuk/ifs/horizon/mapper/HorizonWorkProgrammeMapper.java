package org.innovateuk.ifs.horizon.mapper;


import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.horizon.domain.HorizonWorkProgramme;
import org.innovateuk.ifs.horizon.resource.HorizonWorkProgrammeResource;
import org.mapstruct.Mapper;

@Mapper(
        config = GlobalMapperConfig.class
)
public abstract class HorizonWorkProgrammeMapper extends BaseMapper<HorizonWorkProgramme, HorizonWorkProgrammeResource, Long> {

    public Long mapHorizonWorkProgrammeToId(HorizonWorkProgramme object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

}
