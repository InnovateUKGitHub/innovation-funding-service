package com.worth.ifs.user.mapper;

import com.worth.ifs.address.mapper.AddressMapper;
import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.user.domain.Profile;
import com.worth.ifs.user.resource.ProfileResource;
import org.mapstruct.Mapper;

@Mapper(
        config =  GlobalMapperConfig.class,
        uses = {
                AddressMapper.class,
                UserMapper.class,
                ContractMapper.class
        }
)
public abstract class ProfileMapper extends BaseMapper<Profile, ProfileResource, Long> {

    public Long mapProfileToId(Profile object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}

