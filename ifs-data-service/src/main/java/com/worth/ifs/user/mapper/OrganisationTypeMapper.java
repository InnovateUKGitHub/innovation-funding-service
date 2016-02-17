package com.worth.ifs.user.mapper;

import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.user.domain.OrganisationType;
import com.worth.ifs.user.resource.OrganisationTypeResource;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    uses = {

    }
)
public abstract class OrganisationTypeMapper extends BaseMapper<OrganisationType, OrganisationTypeResource, Long> {


    public Long mapOrganisationTypeToId(OrganisationType object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
