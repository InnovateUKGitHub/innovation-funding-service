package org.innovateuk.ifs.address.mapper;

import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.organisation.mapper.OrganisationAddressMapper;
import org.mapstruct.Mapper;

import static org.mapstruct.ReportingPolicy.WARN;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        OrganisationAddressMapper.class
    },
    unmappedTargetPolicy = WARN
)
public abstract class AddressMapper  extends BaseMapper<Address, AddressResource, Long> {

    public Long mapAddressToId(Address object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
