package com.worth.ifs.organisation.mapper;

import com.worth.ifs.address.mapper.AddressMapper;
import com.worth.ifs.address.mapper.AddressTypeMapper;
import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.organisation.domain.OrganisationAddress;
import com.worth.ifs.organisation.resource.OrganisationAddressResource;
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