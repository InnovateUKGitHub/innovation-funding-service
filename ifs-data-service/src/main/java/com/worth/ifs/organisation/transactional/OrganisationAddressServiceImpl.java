package com.worth.ifs.organisation.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.organisation.domain.OrganisationAddress;
import com.worth.ifs.organisation.mapper.OrganisationAddressMapper;
import com.worth.ifs.organisation.repository.OrganisationAddressRepository;
import com.worth.ifs.organisation.resource.OrganisationAddressResource;
import com.worth.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.util.EntityLookupCallbacks.find;

@Service
public class OrganisationAddressServiceImpl extends BaseTransactionalService implements OrganisationAddressService {

    @Autowired
    private OrganisationAddressRepository repository;

    @Autowired
    private OrganisationAddressMapper mapper;

    @Override
    public ServiceResult<OrganisationAddressResource> findOne(Long id) {
        return find(repository.findOne(id), notFoundError(OrganisationAddress.class)).andOnSuccessReturn(mapper::mapToResource);
    }
}