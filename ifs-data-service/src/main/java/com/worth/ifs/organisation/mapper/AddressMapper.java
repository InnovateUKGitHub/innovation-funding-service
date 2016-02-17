package com.worth.ifs.organisation.mapper;

import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.organisation.domain.Address;
import com.worth.ifs.organisation.resource.AddressResource;
import org.mapstruct.Mapper;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        OrganisationAddressMapper.class
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