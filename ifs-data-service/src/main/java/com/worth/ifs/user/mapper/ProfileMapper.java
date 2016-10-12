package com.worth.ifs.user.mapper;

import com.worth.ifs.address.mapper.AddressMapper;
import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.user.domain.Profile;
import com.worth.ifs.user.resource.ProfileResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                AddressMapper.class,
                UserMapper.class,
                ContractMapper.class
        }
)
public abstract class ProfileMapper {

    public Long mapProfileToId(Profile object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

    public Profile createEntity() {
        return BaseMapper.createDefault(Profile.class);
    }

    @Mappings({
            @Mapping(target = "user", ignore = true),
    })
    public abstract ProfileResource mapToResource(Profile domain);

    public abstract Iterable<ProfileResource> mapToResource(Iterable<Profile> domain);

    public abstract Profile mapToDomain(ProfileResource resource);

    public abstract Iterable<Profile> mapToDomain(Iterable<ProfileResource> resource);
}

