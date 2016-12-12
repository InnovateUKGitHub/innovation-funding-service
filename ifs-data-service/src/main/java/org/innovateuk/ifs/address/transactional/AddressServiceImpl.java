package org.innovateuk.ifs.address.transactional;

import org.innovateuk.ifs.address.mapper.AddressMapper;
import org.innovateuk.ifs.address.repository.AddressRepository;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    AddressRepository addressRepository;
    @Autowired
    AddressMapper addressMapper;

    @Override
    public ServiceResult<AddressResource> getById(Long id) {
        return serviceSuccess(addressMapper.mapToResource(addressRepository.findOne(id)));
    }
}
