package org.innovateuk.ifs.user.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.user.domain.OrganisationType;
import org.innovateuk.ifs.user.resource.OrganisationTypeResource;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring"
)
public abstract class OrganisationTypeMapper extends BaseMapper<OrganisationType, OrganisationTypeResource, Long> {

    public Long mapOrganisationTypeToId(OrganisationType object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
