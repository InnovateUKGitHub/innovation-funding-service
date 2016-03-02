package com.worth.ifs.address.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.address.domain.Address;
import com.worth.ifs.address.mapper.AddressMapper;
import com.worth.ifs.organisation.repository.AddressRepository;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.util.EntityLookupCallbacks.find;

/**
 * Service for retrieving / updating the addresses for organisations and users
 */
@Service
public class AddressServiceImpl extends BaseTransactionalService implements AddressService {

    @Autowired
    private AddressRepository repository;

    @Autowired
    private AddressMapper mapper;

    @Override
    public ServiceResult<AddressResource> findOne(Long id) {
        return find(repository.findOne(id), notFoundError(Address.class, id)).andOnSuccessReturn(mapper::mapToResource);
    }
}