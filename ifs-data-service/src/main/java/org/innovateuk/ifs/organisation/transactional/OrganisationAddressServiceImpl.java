package org.innovateuk.ifs.organisation.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.domain.OrganisationAddress;
import org.innovateuk.ifs.organisation.mapper.OrganisationAddressMapper;
import org.innovateuk.ifs.organisation.repository.OrganisationAddressRepository;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

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
