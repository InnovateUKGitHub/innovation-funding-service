package org.innovateuk.ifs.address.mapper;

import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.organisation.mapper.OrganisationApplicationAddressMapper;
import org.mapstruct.Mapper;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        OrganisationApplicationAddressMapper.class
    }
)
public abstract class AddressMapper  extends BaseMapper<Address, AddressResource, Long> {

    public Long mapAddressToId(Address object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
