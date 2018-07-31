package org.innovateuk.ifs.crm.transactional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.innovateuk.ifs.sil.crm.resource.SilAddress;
import org.innovateuk.ifs.sil.crm.resource.SilContact;
import org.innovateuk.ifs.sil.crm.resource.SilOrganisation;
import org.innovateuk.ifs.sil.crm.service.SilCrmEndpoint;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.Title;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.transactional.BaseUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class CrmServiceImpl implements CrmService {

    private static final Log LOG = LogFactory.getLog(CrmServiceImpl.class);
    @Autowired
    private BaseUserService userService;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private SilCrmEndpoint silCrmEndpoint;

    @Override
    public ServiceResult<Void> syncCrmContact(long userId) {
         return find(userService.getUserById(userId), notFoundError(User.class, userId)).andOnSuccess(user -> {
            if (!user.getSuccess().isInternalUser()) {
                return organisationService.getAllByUserId(userId).andOnSuccess(organisations -> {
                    ServiceResult<Void> result = serviceSuccess();
                    for (OrganisationResource organisation : organisations) {
                        result = result.andOnSuccess(() -> {
                            SilContact silContact = toSilContact(user.getSuccess(), organisation);
                            LOG.info(format("Updating CRM contact %s and organisation %s",
                                    silContact.getEmail(), silContact.getOrganisation().getName()));
                            return silCrmEndpoint.updateContact(silContact);
                        });
                    }
                    return result;
                });
            }
            return serviceSuccess();
         });
    }

    private SilContact toSilContact(UserResource user, OrganisationResource organisation) {
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

    private SilAddress silRegisteredAddress(OrganisationResource organisation) {
        return organisation.getAddresses().stream()
                .filter(organisationAddress -> Arrays.asList(OrganisationAddressType.OPERATING.getOrdinal(), OrganisationAddressType.REGISTERED.getOrdinal())
                        .contains(organisationAddress.getAddressType().getId()))
                .findAny()
                .map(organisationAddress -> {
                    AddressResource address = organisationAddress.getAddress();
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
