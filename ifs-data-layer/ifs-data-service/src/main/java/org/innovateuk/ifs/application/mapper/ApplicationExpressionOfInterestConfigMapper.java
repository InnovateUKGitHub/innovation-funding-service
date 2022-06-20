package org.innovateuk.ifs.application.mapper;

import org.innovateuk.ifs.application.domain.ApplicationExpressionOfInterestConfig;
import org.innovateuk.ifs.application.resource.ApplicationExpressionOfInterestConfigResource;
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
public abstract class ApplicationExpressionOfInterestConfigMapper extends BaseMapper<ApplicationExpressionOfInterestConfig, ApplicationExpressionOfInterestConfigResource, Long> {
    @Mappings({
            @Mapping(source = "application.id", target = "applicationId")
    })

    @Override
    public abstract ApplicationExpressionOfInterestConfigResource mapToResource(ApplicationExpressionOfInterestConfig domain);

    @Mappings({
            @Mapping(source = "applicationId", target = "application")
    })
    @Override
    public abstract ApplicationExpressionOfInterestConfig mapToDomain(ApplicationExpressionOfInterestConfigResource resource);

    public Long mapApplicationExpressionOfInterestConfigToId(ApplicationExpressionOfInterestConfig object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

    public ApplicationExpressionOfInterestConfig build() {
        return createDefault(ApplicationExpressionOfInterestConfig.class);
    }
}
