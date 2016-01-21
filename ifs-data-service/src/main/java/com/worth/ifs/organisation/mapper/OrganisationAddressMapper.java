package com.worth.ifs.organisation.mapper;

import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.organisation.domain.OrganisationAddress;
import com.worth.ifs.organisation.repository.OrganisationAddressRepository;
import com.worth.ifs.organisation.resource.OrganisationAddressResource;
import com.worth.ifs.user.mapper.OrganisationMapper;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        AddressMapper.class,
        OrganisationMapper.class
    }
)
public abstract class OrganisationAddressMapper {

    @Autowired
    private OrganisationAddressRepository repository;

    public abstract OrganisationAddressResource mapOrganisationAddressToResource(OrganisationAddress object);

    public abstract OrganisationAddress resourceToOrganisationAddress(OrganisationAddressResource resource);

    public Long mapOrganisationAddressToId(OrganisationAddress object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

    public OrganisationAddress mapIdToOrganisationAddress(Long id) {
        return repository.findOne(id);
    }
}