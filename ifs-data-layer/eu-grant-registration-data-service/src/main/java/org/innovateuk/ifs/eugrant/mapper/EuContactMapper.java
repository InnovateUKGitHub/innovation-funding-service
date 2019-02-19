package org.innovateuk.ifs.eugrant.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.eugrant.EuContactResource;
import org.innovateuk.ifs.eugrant.domain.EuContact;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(
        config = GlobalMapperConfig.class,
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL
)
public abstract class EuContactMapper extends BaseMapper<EuContact, EuContactResource, Long> {

    @Mappings({
            @Mapping(target = "id", ignore = true),
    })
    @Override
    public abstract EuContact mapToDomain(EuContactResource resource);

    @Override
    public abstract EuContactResource mapToResource(EuContact euContact);

}
