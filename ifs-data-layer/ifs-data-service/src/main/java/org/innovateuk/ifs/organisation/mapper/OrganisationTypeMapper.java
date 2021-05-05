package org.innovateuk.ifs.organisation.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.organisation.domain.OrganisationType;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;
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
