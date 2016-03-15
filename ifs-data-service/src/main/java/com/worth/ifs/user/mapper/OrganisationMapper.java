package com.worth.ifs.user.mapper;

import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.finance.mapper.ApplicationFinanceMapper;
import com.worth.ifs.address.mapper.AddressMapper;
import com.worth.ifs.organisation.mapper.OrganisationAddressMapper;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.resource.OrganisationResource;
import org.mapstruct.Mapper;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        AddressMapper.class,
        ProcessRoleMapper.class,
        ApplicationFinanceMapper.class,
        OrganisationAddressMapper.class,
        OrganisationTypeMapper.class
    }
)
public abstract class OrganisationMapper  extends BaseMapper<Organisation, OrganisationResource, Long> {

    public Long mapOrganisationToId(Organisation object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}