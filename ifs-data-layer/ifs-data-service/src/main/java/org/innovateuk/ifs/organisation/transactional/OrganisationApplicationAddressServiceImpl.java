package org.innovateuk.ifs.organisation.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.domain.OrganisationApplicationAddress;
import org.innovateuk.ifs.organisation.mapper.OrganisationApplicationAddressMapper;
import org.innovateuk.ifs.organisation.repository.OrganisationApplicationAddressRepository;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class OrganisationApplicationAddressServiceImpl extends BaseTransactionalService implements OrganisationApplicationAddressService {

    @Autowired
    private OrganisationApplicationAddressRepository repository;

    @Autowired
    private OrganisationApplicationAddressMapper mapper;

    @Override
    public ServiceResult<OrganisationAddressResource> findByOrganisationIdAndApplicationIdAndAddressId(long organisationId, long applicationId, long addressId) {
        return find(repository.findByOrganisationIdAndApplicationIdAndAddressId(organisationId, applicationId, addressId), notFoundError(OrganisationApplicationAddress.class)).andOnSuccessReturn(mapper::mapToResource);
    }
}
