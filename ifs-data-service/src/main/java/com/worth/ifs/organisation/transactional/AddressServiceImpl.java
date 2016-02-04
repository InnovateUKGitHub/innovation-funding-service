package com.worth.ifs.organisation.transactional;

import com.worth.ifs.organisation.domain.Address;
import com.worth.ifs.organisation.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddressServiceImpl implements AddressService {
    @Autowired
    private AddressRepository repository;

    @Override
    public Address findOne(Long id) {
        return repository.findOne(id);
    }
}