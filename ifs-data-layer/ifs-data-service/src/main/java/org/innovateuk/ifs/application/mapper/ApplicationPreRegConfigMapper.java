package org.innovateuk.ifs.application.mapper;

import org.innovateuk.ifs.application.domain.ApplicationPreRegConfig;
import org.innovateuk.ifs.application.resource.ApplicationPreRegConfigResource;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapperConfig.class)
public  abstract class ApplicationPreRegConfigMapper {

    public ApplicationPreRegConfigResource mapToResource(ApplicationPreRegConfig domain) {
        ApplicationPreRegConfigResource applicationPreRegConfigResource = new ApplicationPreRegConfigResource();

    if (domain != null) {
        applicationPreRegConfigResource.setId(domain.getId());
        applicationPreRegConfigResource.setEnableForEOI(domain.isEnableForEOI());
    }
    return  applicationPreRegConfigResource;
}
}
