package org.innovateuk.ifs.profile.mapper;

import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
        componentModel = "spring",
        config = GlobalMapperConfig.class,
        uses = {}
)
public abstract class ProfileMapper {

    @Autowired
    private ProfileRepository repository;


    @Mappings({
            @Mapping(target = "innovationAreas", ignore = true)
    })
    public Profile mapIdToDomain(Long id) {
        if (id == null) {
            return null;
        }
        return repository.findOne(id);
    }

    public Long mapProfileToId(Profile object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}

