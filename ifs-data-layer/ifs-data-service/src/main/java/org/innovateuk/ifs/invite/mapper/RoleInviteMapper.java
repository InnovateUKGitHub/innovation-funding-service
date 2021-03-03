package org.innovateuk.ifs.invite.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.invite.domain.RoleInvite;
import org.innovateuk.ifs.invite.resource.RoleInviteResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * Mapper for converting between RoleInvite domain and resource objects
 */

@Mapper(componentModel = "spring")
public abstract class RoleInviteMapper extends BaseMapper<RoleInvite, RoleInviteResource, Long> {

    @Mappings({
            @Mapping(source = "target", target = "role"),
            @Mapping(source = "simpleOrganisation.name", target = "organisation")
    })
    @Override
    public abstract RoleInviteResource mapToResource(RoleInvite domain);

    public Long mapInviteToId(RoleInvite object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
