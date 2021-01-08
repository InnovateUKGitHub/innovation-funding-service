package org.innovateuk.ifs.crm.transactional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.address.domain.AddressType;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.commons.service.FailingOrSucceedingResult;
import org.innovateuk.ifs.commons.service.ServiceFailure;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.organisation.resource.OrganisationExecutiveOfficerResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationSicCodeResource;
import org.innovateuk.ifs.organisation.transactional.OrganisationAddressService;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.innovateuk.ifs.sil.crm.resource.SilAddress;
import org.innovateuk.ifs.sil.crm.resource.SilContact;
import org.innovateuk.ifs.sil.crm.resource.SilOrganisation;
import org.innovateuk.ifs.sil.crm.service.SilCrmEndpoint;
import org.innovateuk.ifs.user.resource.Title;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.transactional.BaseUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.resource.Role.MONITORING_OFFICER;

@Service
public class CrmServiceImpl implements CrmService {

    private static final Log LOG = LogFactory.getLog(CrmServiceImpl.class);
    @Autowired
    private BaseUserService userService;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private OrganisationAddressService organisationAddressService;

    @Autowired
    private SilCrmEndpoint silCrmEndpoint;

    @Value("${ifs.new.organisation.search.enabled:false}")
    private Boolean newOrganisationSearchEnabled;

    @Override
    public ServiceResult<Void> syncCrmContact(long userId) {
        return userService.getUserById(userId).andOnSuccess(user -> {

            syncExternalUser(user);
            syncMonitoringOfficer(user);

            return serviceSuccess();
        });
    }

    private void syncExternalUser(UserResource user) {
        if (!user.isInternalUser()) {
            organisationService.getAllByUserId(user.getId()).andOnSuccessReturn(organisations -> {
                ServiceResult<Void> result = serviceSuccess();
                for (OrganisationResource organisation : organisations) {
                    result = result.andOnSuccess(() -> {
                        SilContact silContact = externalUserToSilContact(user, organisation);
                        getSilContactEmailAndOrganisationNameAndUpdateContact(silContact);
                    });
                }
                return serviceSuccess();
            });
        }
    }

    private void syncMonitoringOfficer(UserResource user) {

        if (user.hasRole(MONITORING_OFFICER)) {
            SilContact silContact = monitoringOfficerToSilContact(user);
            getSilContactEmailAndOrganisationNameAndUpdateContact(silContact);
        }

    }

    private FailingOrSucceedingResult<Void, ServiceFailure> getSilContactEmailAndOrganisationNameAndUpdateContact(SilContact silContact) {
        LOG.info(format("Updating CRM contact %s and organisation %s",
                silContact.getEmail(), silContact.getOrganisation().getName()));
        return silCrmEndpoint.updateContact(silContact);
    }

    private SilContact externalUserToSilContact(UserResource user, OrganisationResource organisation) {

        SilContact silContact = setSilContactDetails(user);

        SilOrganisation silOrganisation = new SilOrganisation();
        silOrganisation.setName(organisation.getName());
        silOrganisation.setRegistrationNumber(organisation.getCompaniesHouseNumber());
        silOrganisation.setSrcSysOrgId(String.valueOf(organisation.getId()));

        if (newOrganisationSearchEnabled) {
            silOrganisation.setRegisteredAddress(getRegisteredAddress(organisation));
            silOrganisation.setDateOfIncorporation(organisation.getDateOfIncorporation());
            silOrganisation.setSicCodes(getSicCodes(organisation));
            silOrganisation.setExecutiveOfficers(getExecutiveOfficers(organisation));
        }

        silContact.setOrganisation(silOrganisation);

        return silContact;
    }

    private SilAddress getRegisteredAddress(OrganisationResource organisation) {
        AddressType addressType = new AddressType();
        addressType.setId(OrganisationAddressType.REGISTERED.getId());
        addressType.setName(OrganisationAddressType.REGISTERED.name());

        return organisationAddressService.findByOrganisationIdAndAddressType(organisation.getId(), addressType)
                .andOnSuccessReturn(addresses -> addresses.stream()
                        .findFirst()
                        .map(address -> organisationAddressToSilAddress(address))
                        .orElse(null))
                .getSuccess();
    }

    private SilAddress organisationAddressToSilAddress(OrganisationAddressResource organisationAddress) {
        AddressResource address = organisationAddress.getAddress();

        String[] street = new String[2];
        street[0] = address.getAddressLine2() == null ? "" : address.getAddressLine2();
        street[1] = (address.getAddressLine3() != null
                && address.getAddressLine3().trim().length() > 0) ? format(", %s", address.getAddressLine3()) : "";

        SilAddress silAddress = new SilAddress();
        silAddress.setBuildingName(address.getAddressLine1() == null ? "" : address.getAddressLine1());
        silAddress.setStreet(String.join("", street));
        silAddress.setLocality(address.getCounty() == null ? "" : address.getCounty());
        silAddress.setTown(address.getTown() == null ? "" : address.getTown());
        silAddress.setPostcode(address.getPostcode() == null ? "" : address.getPostcode());
        silAddress.setCountry(address.getCountry() == null ? "" : address.getCountry());

        return silAddress;
    }

    private List<String> getExecutiveOfficers(OrganisationResource organisation) {
        return organisation.getExecutiveOfficers().stream()
                .map(OrganisationExecutiveOfficerResource::getName)
                .collect(Collectors.toList());
    }

    private List<String> getSicCodes(OrganisationResource organisation) {
        return organisation.getSicCodes().stream()
                .map(OrganisationSicCodeResource::getSicCode)
                .collect(Collectors.toList());
    }

    private SilContact setSilContactDetails(UserResource user) {

        SilContact silContact = new SilContact();
        silContact.setEmail(user.getEmail());
        silContact.setFirstName(user.getFirstName());
        silContact.setLastName(user.getLastName());
        silContact.setTitle(Optional.ofNullable(user.getTitle()).map(Title::getDisplayName).orElse(null));
        silContact.setSrcSysContactId(String.valueOf(user.getId()));
        return silContact;
    }

    private SilContact monitoringOfficerToSilContact(UserResource user) {

        SilContact silContact = setSilContactDetails(user);

        SilOrganisation moSilOrganisation = new SilOrganisation();
        moSilOrganisation.setName("IFS MO Company");
        moSilOrganisation.setRegistrationNumber("");
        moSilOrganisation.setSrcSysOrgId("IFSMO01");

        silContact.setOrganisation(moSilOrganisation);

        return silContact;
    }
}
