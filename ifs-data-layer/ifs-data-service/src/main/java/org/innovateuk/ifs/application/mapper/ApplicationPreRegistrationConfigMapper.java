package org.innovateuk.ifs.application.mapper;

import org.innovateuk.ifs.application.domain.ApplicationPreRegistrationConfig;
import org.innovateuk.ifs.application.resource.ApplicationPreRegistrationConfigResource;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                ApplicationMapper.class
        }
)
public abstract class ApplicationPreRegistrationConfigMapper extends BaseMapper<ApplicationPreRegistrationConfig, ApplicationPreRegistrationConfigResource, Long> {
    @Mappings({
            @Mapping(source = "application.id", target = "applicationId")
    })

    @Override
    public abstract ApplicationPreRegistrationConfigResource mapToResource(ApplicationPreRegistrationConfig domain);

    @Mappings({
            @Mapping(source = "applicationId", target = "application")
    })
    @Override
    public abstract ApplicationPreRegistrationConfig mapToDomain(ApplicationPreRegistrationConfigResource resource);

    public Long mapApplicationPreRegConfigToId(ApplicationPreRegistrationConfig object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

    public ApplicationPreRegistrationConfig build() {
        return createDefault(ApplicationPreRegistrationConfig.class);
    }
}
