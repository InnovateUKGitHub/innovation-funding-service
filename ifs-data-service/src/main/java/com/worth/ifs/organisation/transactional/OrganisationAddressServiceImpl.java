package com.worth.ifs.organisation.transactional;

import com.worth.ifs.organisation.domain.OrganisationAddress;
import com.worth.ifs.organisation.repository.OrganisationAddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrganisationAddressServiceImpl implements OrganisationAddressService {
    @Autowired
    private OrganisationAddressRepository repository;

    @Override
    public OrganisationAddress findOne(Long id) {
        return repository.findOne(id);
    }
}