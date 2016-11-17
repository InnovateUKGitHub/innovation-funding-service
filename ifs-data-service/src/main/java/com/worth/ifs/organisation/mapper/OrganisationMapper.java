package com.worth.ifs.organisation.mapper;

import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.mapper.OrganisationTypeMapper;
import com.worth.ifs.user.mapper.ProcessRoleMapper;
import com.worth.ifs.user.mapper.UserMapper;
import com.worth.ifs.user.resource.OrganisationResource;
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
            @Mapping(target = "applicationFinances", ignore = true)
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