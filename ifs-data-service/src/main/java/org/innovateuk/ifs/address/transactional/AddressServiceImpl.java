package org.innovateuk.ifs.address.transactional;

import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.address.mapper.AddressMapper;
import org.innovateuk.ifs.address.repository.AddressRepository;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
@Transactional(readOnly = true)
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private AddressMapper addressMapper;

    @Override
    public ServiceResult<AddressResource> getById(Long id) {
        return find(addressRepository.findOne(id), notFoundError(Address.class, id)).andOnSuccessReturn(addressMapper::mapToResource);
    }
}
