package org.innovateuk.ifs.organisation.mapper;

import org.innovateuk.ifs.address.mapper.AddressMapper;
import org.innovateuk.ifs.address.mapper.AddressTypeMapper;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.organisation.domain.OrganisationAddress;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.mapstruct.Mapper;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
            AddressMapper.class,
            AddressTypeMapper.class,
            OrganisationMapper.class
    }
)
public abstract class OrganisationAddressMapper extends BaseMapper<OrganisationAddress, OrganisationAddressResource, Long> {

    public Long mapOrganisationAddressToId(OrganisationAddress object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
