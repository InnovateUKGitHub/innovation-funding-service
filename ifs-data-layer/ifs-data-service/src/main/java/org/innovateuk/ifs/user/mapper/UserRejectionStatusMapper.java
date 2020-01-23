package org.innovateuk.ifs.user.mapper;

import org.innovateuk.ifs.commons.mapper.BaseResourceMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.user.domain.UserRejectionStatus;
import org.innovateuk.ifs.user.resource.UserRejectionStatusResource;
import org.mapstruct.Mapper;

@Mapper(
        config = GlobalMapperConfig.class
)
public abstract class UserRejectionStatusMapper extends BaseResourceMapper<UserRejectionStatus, UserRejectionStatusResource> {
}
