package com.worth.ifs.organisation.mapper;

import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.finance.mapper.ApplicationFinanceMapper;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.mapper.OrganisationTypeMapper;
import com.worth.ifs.user.mapper.ProcessRoleMapper;
import com.worth.ifs.user.mapper.UserMapper;
import com.worth.ifs.user.resource.OrganisationResource;
import org.mapstruct.Mapper;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        ProcessRoleMapper.class,
        ApplicationFinanceMapper.class,
        OrganisationAddressMapper.class,
        OrganisationTypeMapper.class,
        UserMapper.class
    }
)
public abstract class OrganisationMapper extends BaseMapper<Organisation, OrganisationResource, Long> {

    public Long mapOrganisationToId(Organisation object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}