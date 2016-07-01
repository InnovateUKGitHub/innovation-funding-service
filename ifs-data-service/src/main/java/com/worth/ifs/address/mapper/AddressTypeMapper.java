package com.worth.ifs.address.mapper;

import com.worth.ifs.address.domain.AddressType;
import com.worth.ifs.address.resource.AddressTypeResource;
import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.organisation.mapper.OrganisationAddressMapper;
import org.mapstruct.Mapper;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {OrganisationAddressMapper.class}
)
public abstract class AddressTypeMapper extends BaseMapper<AddressType, AddressTypeResource, Long>{

    public Long mapAddressTypeToId(AddressType object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
