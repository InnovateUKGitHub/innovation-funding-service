package com.worth.ifs.user.mapper;

import com.worth.ifs.address.mapper.AddressMapper;
import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.user.domain.Profile;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.ProfileResource;
import com.worth.ifs.user.resource.UserResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
        config =  GlobalMapperConfig.class,
        uses = {
                AddressMapper.class,
                UserMapper.class,
                ContractMapper.class
        }
)
public abstract class ProfileMapper extends BaseMapper<Profile, ProfileResource, Long> {

    @Mappings({
            @Mapping(target = "user", ignore = true ),
    })
    @Override
    public abstract ProfileResource mapToResource(Profile domain);

    public Long mapProfileToId(Profile object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

    public Profile createEntity() {
        return createDefault(Profile.class);
    }
}

