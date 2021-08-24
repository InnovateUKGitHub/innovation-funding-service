package org.innovateuk.ifs.application.mapper;

import org.innovateuk.ifs.application.domain.ApplicationExternalConfig;
import org.innovateuk.ifs.application.resource.ApplicationExternalConfigResource;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapperConfig.class)
public abstract class ApplicationExternalConfigMapper {

    public ApplicationExternalConfigResource mapToResource(ApplicationExternalConfig domain) {
        ApplicationExternalConfigResource applicationExternalConfigResource = new ApplicationExternalConfigResource();

        if (domain != null) {
            applicationExternalConfigResource.setExternalApplicationId(domain.getExternalApplicationId());
            applicationExternalConfigResource.setExternalApplicantName(domain.getExternalApplicantName());
        }
        return  applicationExternalConfigResource;
    }
}