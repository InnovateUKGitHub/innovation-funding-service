package com.worth.ifs.address.mapper;

import com.worth.ifs.address.domain.Address;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.organisation.mapper.OrganisationAddressMapper;
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