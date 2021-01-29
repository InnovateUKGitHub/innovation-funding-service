package org.innovateuk.ifs.organisation.transactional;

import org.innovateuk.ifs.address.domain.AddressType;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.domain.OrganisationAddress;
import org.innovateuk.ifs.organisation.mapper.OrganisationAddressMapper;
import org.innovateuk.ifs.organisation.repository.OrganisationAddressRepository;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class OrganisationAddressServiceImpl extends BaseTransactionalService implements OrganisationAddressService {

    @Autowired
    private OrganisationAddressRepository repository;

    @Autowired
    private OrganisationAddressMapper mapper;

    @Override
    public ServiceResult<OrganisationAddressResource> findOne(Long id) {
        return find(repository.findById(id), notFoundError(OrganisationAddress.class)).andOnSuccessReturn(mapper::mapToResource);
    }

    @Override
    public ServiceResult<OrganisationAddressResource> findByOrganisationIdAndAddressId(long organisationId, long addressId) {
        return find(repository.findByOrganisationIdAndAddressId(organisationId, addressId), notFoundError(OrganisationAddress.class)).andOnSuccessReturn(mapper::mapToResource);
    }

    @Override
    public ServiceResult<List<OrganisationAddressResource>> findByOrganisationIdAndAddressType(long organisationId, AddressType addressType) {
        return serviceSuccess(simpleMap(repository.findByOrganisationIdAndAddressType(organisationId, addressType), mapper::mapToResource));
    }
}
