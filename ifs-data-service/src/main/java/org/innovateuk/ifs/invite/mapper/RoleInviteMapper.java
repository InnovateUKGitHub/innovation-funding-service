package org.innovateuk.ifs.invite.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.invite.domain.RoleInvite;
import org.innovateuk.ifs.invite.resource.RoleInviteResource;
import org.innovateuk.ifs.user.mapper.RoleMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * Created by rav on 30/06/2017.
 */

@Mapper(
        componentModel = "spring",
        uses = {
                RoleMapper.class
        }
)
public abstract class RoleInviteMapper extends BaseMapper<RoleInvite, RoleInviteResource, Long> {

    @Mappings({
            @Mapping(source = "target.name", target = "roleName"),
            @Mapping(source = "target.id", target = "roleId")
    })
    @Override
    public abstract RoleInviteResource mapToResource(RoleInvite domain);

    public Long mapInviteToId(RoleInvite     object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
