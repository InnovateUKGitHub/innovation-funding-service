package org.innovateuk.ifs.invite.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.invite.domain.RoleInvite;
import org.innovateuk.ifs.invite.resource.RoleInviteResource;
import org.innovateuk.ifs.user.mapper.RoleMapper;
import org.mapstruct.Mapper;

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
    @Override
    public abstract RoleInviteResource mapToResource(RoleInvite domain);

    public Long mapInviteToId(RoleInvite     object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
