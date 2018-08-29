package org.innovateuk.ifs.eugrant.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.innovateuk.ifs.eugrant.domain.EuGrant;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper(
        config = GlobalMapperConfig.class
)
public abstract class EuGrantMapper extends BaseMapper<EuGrant, EuGrantResource, UUID> {

    public String map(UUID value) {
        return value == null ? null : value.toString();
    }

    public UUID map(String value) {
        return value == null ? null : UUID.fromString(value);
    }

}