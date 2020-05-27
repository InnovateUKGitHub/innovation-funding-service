package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.address.mapper.AddressMapper;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.application.domain.ApplicationOrganisationAddress;
import org.innovateuk.ifs.application.repository.ApplicationOrganisationAddressRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;


/**
 * Service for getting and saving application organisation addresses.
 */
@Service
public class ApplicationOrganisationAddressServiceImpl extends BaseTransactionalService implements ApplicationOrganisationAddressService {

    @Autowired
    private ApplicationOrganisationAddressRepository applicationOrganisationAddressRepository;

    @Autowired
    private AddressMapper addressMapper;

    @Override
    public ServiceResult<AddressResource> getAddress(long applicationId, long organisationId, OrganisationAddressType type) {
        return find(applicationOrganisationAddressRepository.findByApplicationIdAndOrganisationAddressOrganisationIdAndOrganisationAddressAddressTypeId(applicationId, organisationId, type.getId()),
                notFoundError(ApplicationOrganisationAddress.class, applicationId, organisationId, type))
                .andOnSuccessReturn(applicationOrganisationAddress -> addressMapper.mapToResource(applicationOrganisationAddress.getOrganisationAddress().getAddress()));
    }

    @Override
    @Transactional
    public ServiceResult<AddressResource> updateAddress(long applicationId, long organisationId, OrganisationAddressType type, AddressResource address) {
        return find(applicationOrganisationAddressRepository.findByApplicationIdAndOrganisationAddressOrganisationIdAndOrganisationAddressAddressTypeId(applicationId, organisationId, type.getId()),
                notFoundError(ApplicationOrganisationAddress.class, applicationId, organisationId, type))
                .andOnSuccessReturn(applicationOrganisationAddress -> {
                    applicationOrganisationAddress.getOrganisationAddress().getAddress().copyFrom(address);
                    return addressMapper.mapToResource(applicationOrganisationAddress.getOrganisationAddress().getAddress());
                });
    }
}
