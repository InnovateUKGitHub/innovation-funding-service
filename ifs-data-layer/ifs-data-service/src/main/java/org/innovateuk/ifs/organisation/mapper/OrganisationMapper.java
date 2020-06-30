package org.innovateuk.ifs.organisation.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.mapper.ProcessRoleMapper;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        ProcessRoleMapper.class,
        OrganisationAddressMapper.class,
        OrganisationTypeMapper.class,
        UserMapper.class
    }
)
public abstract class OrganisationMapper extends BaseMapper<Organisation, OrganisationResource, Long> {

    @Mappings({
            @Mapping(source = "organisationType.name", target = "organisationTypeName"),
            @Mapping(source = "organisationType.description", target = "organisationTypeDescription"),
            @Mapping(target = "addresses", ignore = true)
    })
    @Override
    public abstract OrganisationResource mapToResource(Organisation domain);

    public Long mapOrganisationToId(Organisation object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}