package org.innovateuk.ifs.application.mapper;

import org.innovateuk.ifs.application.domain.ApplicationPreRegConfig;
import org.innovateuk.ifs.application.resource.ApplicationPreRegConfigResource;
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
public abstract class ApplicationPreRegConfigMapper extends BaseMapper<ApplicationPreRegConfig, ApplicationPreRegConfigResource, Long> {
    @Mappings({
            @Mapping(source = "application.id", target = "applicationId")
    })

    @Override
    public abstract ApplicationPreRegConfigResource mapToResource(ApplicationPreRegConfig domain);

    @Mappings({
            @Mapping(source = "applicationId", target = "application")
    })
    @Override
    public abstract ApplicationPreRegConfig mapToDomain(ApplicationPreRegConfigResource resource);

    public Long mapApplicationPreRegConfigToId(ApplicationPreRegConfig object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

    public ApplicationPreRegConfig build() {
        return createDefault(ApplicationPreRegConfig.class);
    }
}
