package com.worth.ifs.address.transactional;

import com.worth.ifs.address.mapper.AddressMapper;
import com.worth.ifs.address.repository.AddressRepository;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.commons.service.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;

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
