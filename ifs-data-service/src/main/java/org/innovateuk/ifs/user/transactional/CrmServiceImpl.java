package org.innovateuk.ifs.user.transactional;

import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.commons.error.CommonErrors;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.sil.crm.resource.SilAddress;
import org.innovateuk.ifs.sil.crm.resource.SilContact;
import org.innovateuk.ifs.sil.crm.resource.SilOrganisation;
import org.innovateuk.ifs.sil.crm.service.SilCrmEndpoint;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.OrganisationRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.Title;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.security.SecurityRuleUtil.isInternal;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class CrmServiceImpl extends BaseTransactionalService implements CrmService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private SilCrmEndpoint silCrmEndpoint;

    @Override
    public ServiceResult<Void> syncCrmContact(long userId) {
        return find(userRepository.findOne(userId), notFoundError(User.class, userId)).andOnSuccess((user) -> {
            if (!isInternal(user)) {
                List<Organisation> organisations = organisationRepository.findByUsersId(userId);
                if (organisations.size() != 1) {
                    return serviceFailure(CommonErrors.notFoundError(Organisation.class));
                }
                return silCrmEndpoint.updateContact(toSilContact(user, organisations.get(0)));
            }
            return serviceSuccess();
        });
    }

    private SilContact toSilContact(User user, Organisation organisation) {
        SilContact silContact = new SilContact();
        silContact.setEmail(user.getEmail());
        silContact.setFirstName(user.getFirstName());
        silContact.setLastName(user.getLastName());
        silContact.setTitle(Optional.ofNullable(user.getTitle()).map(Title::getDisplayName).orElse(null));
        silContact.setSrcSysContactId(String.valueOf(user.getId()));

        SilOrganisation silOrganisation = new SilOrganisation();
        silOrganisation.setName(organisation.getName());
        silOrganisation.setRegistrationNumber(organisation.getCompanyHouseNumber());
        silOrganisation.setSrcSysOrgId(String.valueOf(organisation.getId()));

        SilAddress silRegisteredAddress = silRegisteredAddress(organisation);
        silOrganisation.setRegisteredAddress(silRegisteredAddress);
        silContact.setOrganisation(silOrganisation);

        return silContact;
    }

    private SilAddress silRegisteredAddress(Organisation organisation) {
        return organisation.getAddresses().stream()
                .filter(organisationAddress -> organisationAddress.getAddressType().getId().equals(OrganisationAddressType.REGISTERED.getOrdinal()))
                .findAny()
                .map(organisationAddress -> {
                    Address address = organisationAddress.getAddress();
                    SilAddress silAddress = new SilAddress();
                    silAddress.setBuildingName(address.getAddressLine1());
                    silAddress.setStreet(address.getAddressLine2());
                    silAddress.setLocality(address.getAddressLine3());
                    silAddress.setTown(address.getTown());
                    silAddress.setCountry("United Kingdom");
                    silAddress.setPostcode(address.getPostcode());
                    return silAddress;
                })
                .orElse(null);
    }
}
