package com.worth.ifs.user.transactional;

import com.worth.ifs.user.domain.OrganisationType;
import com.worth.ifs.user.repository.OrganisationTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrganisationTypeServiceImpl implements OrganisationTypeService {
    @Autowired
    private OrganisationTypeRepository repository;

    @Override
    public OrganisationType findOne(Long id) {
        return repository.findOne(id);
    }

    @Override
    public Iterable<OrganisationType> findAll() {
        return repository.findAll();
    }
}