package org.innovateuk.ifs.eugrant.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.innovateuk.ifs.eugrant.domain.EuGrant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.UUID;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = { EuOrganisationMapper.class }
)
public abstract class EuGrantMapper extends BaseMapper<EuGrant, EuGrantResource, UUID> {

    @Mappings({
            // TODO add to resource
            @Mapping(target = "contact",  ignore = true),
            @Mapping(target = "funding", ignore = true)
    })
    @Override
    public abstract EuGrant mapToDomain(EuGrantResource resource);
}