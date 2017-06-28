package org.innovateuk.ifs.finance.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.finance.domain.OrganisationSize;
import org.innovateuk.ifs.finance.resource.OrganisationSizeResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(config = GlobalMapperConfig.class)
public abstract class OrganisationSizeMapper extends BaseMapper<OrganisationSize, OrganisationSizeResource, Long> {

    @Mappings({
            @Mapping(target = "description", ignore = true)
    })
    @Override
    public abstract OrganisationSize mapToDomain(OrganisationSizeResource resource);

    @Override
    public abstract OrganisationSizeResource mapToResource(OrganisationSize domain);

    public Long mapOrganisationSizeToId(OrganisationSize object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
