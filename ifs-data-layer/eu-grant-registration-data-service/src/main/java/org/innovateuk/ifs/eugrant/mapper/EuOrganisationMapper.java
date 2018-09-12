package org.innovateuk.ifs.eugrant.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.eugrant.EuOrganisationResource;
import org.innovateuk.ifs.eugrant.domain.EuOrganisation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(
        config = GlobalMapperConfig.class,
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL
)
public abstract class EuOrganisationMapper extends BaseMapper<EuOrganisation, EuOrganisationResource, Long> {

    public abstract EuOrganisationResource mapToResource(EuOrganisation domain);

    @Mappings({
            @Mapping(target = "id",  ignore = true)
    })
    @Override
    public abstract EuOrganisation mapToDomain(EuOrganisationResource resource);
}