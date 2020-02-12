package org.innovateuk.ifs.user.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.user.domain.RoleProfileStatus;
import org.innovateuk.ifs.user.resource.RoleProfileStatusResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                UserMapper.class
        }
)
public abstract class RoleProfileStatusMapper extends BaseMapper<RoleProfileStatus, RoleProfileStatusResource, Long> {

    @Mappings({
            @Mapping(source = "user.id", target = "userId"),
            @Mapping(source = "createdBy.id", target = "createdBy"),
            @Mapping(source = "modifiedBy.id", target = "modifiedBy")
    })
    @Override
    public abstract RoleProfileStatusResource mapToResource(RoleProfileStatus domain);

    @Override
    public abstract RoleProfileStatus mapToDomain(RoleProfileStatusResource resource);
}
