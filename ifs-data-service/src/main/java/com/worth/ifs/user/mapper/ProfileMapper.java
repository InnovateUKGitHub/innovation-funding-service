package com.worth.ifs.user.mapper;

import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.user.domain.Profile;
import com.worth.ifs.user.repository.ProfileRepository;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
        componentModel = "spring",
        config = GlobalMapperConfig.class,
        uses = {
        }
)
public abstract class ProfileMapper {

    @Autowired
    private ProfileRepository repository;


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

