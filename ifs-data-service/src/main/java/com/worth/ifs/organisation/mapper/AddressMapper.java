package com.worth.ifs.organisation.mapper;

import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.organisation.domain.Address;
import com.worth.ifs.organisation.repository.AddressRepository;
import com.worth.ifs.organisation.resource.AddressResource;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        OrganisationAddressMapper.class
    }
)
public abstract class AddressMapper {

    @Autowired
    private AddressRepository repository;

    public abstract AddressResource mapAddressToResource(Address object);

    public abstract Address resourceToAddress(AddressResource resource);

    public Long mapAddressToId(Address object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

    public Address mapIdToAddress(Long id) {
        return repository.findOne(id);
    }
}