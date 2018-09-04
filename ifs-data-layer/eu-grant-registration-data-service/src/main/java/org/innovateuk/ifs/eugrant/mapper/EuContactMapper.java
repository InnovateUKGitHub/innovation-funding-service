package org.innovateuk.ifs.eugrant.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.eugrant.EuContactResource;
import org.innovateuk.ifs.eugrant.domain.EuContact;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.UUID;

@Mapper(
        config = GlobalMapperConfig.class
)
public abstract class EuContactMapper extends BaseMapper<EuContact, EuContactResource, Long> {

    @Mappings({
            @Mapping(target = "id", ignore = true),
    })
    @Override
    public abstract EuContact mapToDomain(EuContactResource resource);

}
