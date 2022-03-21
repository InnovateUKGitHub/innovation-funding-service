package org.innovateuk.ifs.heukar.mapper;

import org.innovateuk.ifs.application.mapper.ApplicationMapper;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.heukar.domain.ApplicationHeukarLocation;
import org.innovateuk.ifs.heukar.resource.ApplicationHeukarLocationResource;
import org.innovateuk.ifs.heukar.resource.HeukarLocation;
import org.mapstruct.Mapper;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {ApplicationMapper.class}
)
public abstract class ApplicationHeukarLocationMapper extends BaseMapper<ApplicationHeukarLocation, ApplicationHeukarLocationResource, Long> {

    @Override
    public abstract ApplicationHeukarLocationResource mapToResource(ApplicationHeukarLocation domain);

    public ApplicationHeukarLocation mapIdAndLocationToDomain(long applicationId, HeukarLocation location){
        ApplicationHeukarLocation applicationHeukarLocation = new ApplicationHeukarLocation();
        applicationHeukarLocation.setLocation(location);
        applicationHeukarLocation.setApplicationId(applicationId);
        return applicationHeukarLocation;
    }

    public Long mapApplicationHeukarLocationToId(ApplicationHeukarLocation object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
