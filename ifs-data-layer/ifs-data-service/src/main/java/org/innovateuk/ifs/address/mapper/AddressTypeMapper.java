package org.innovateuk.ifs.address.mapper;

import org.innovateuk.ifs.address.domain.AddressType;
import org.innovateuk.ifs.address.resource.AddressTypeResource;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.mapstruct.Mapper;

@Mapper(
        config = GlobalMapperConfig.class
)
public abstract class AddressTypeMapper extends BaseMapper<AddressType, AddressTypeResource, Long>{

    public Long mapAddressTypeToId(AddressType object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
