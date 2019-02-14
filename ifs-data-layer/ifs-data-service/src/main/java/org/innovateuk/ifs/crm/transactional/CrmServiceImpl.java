package org.innovateuk.ifs.crm.transactional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.innovateuk.ifs.sil.crm.resource.SilContact;
import org.innovateuk.ifs.sil.crm.resource.SilOrganisation;
import org.innovateuk.ifs.sil.crm.service.SilCrmEndpoint;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.Title;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.transactional.BaseUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.resource.Role.MONITORING_OFFICER;
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
                return getUserOrganisationByUserIdOrCreateOrganisationForMonitoringOfficer(user.getSuccess()).andOnSuccess(organisations -> {
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

    private ServiceResult<List<OrganisationResource>> getUserOrganisationByUserIdOrCreateOrganisationForMonitoringOfficer(UserResource user) {
        if (user.hasRole(MONITORING_OFFICER)) {
            OrganisationResource monitoringOfficerOrganisationResource = createMonitoringOfficerOrganisation();
            return serviceSuccess(Collections.singletonList(monitoringOfficerOrganisationResource)); // static import
        }

        return organisationService.getAllByUserId(user.getId());
    }

    private OrganisationResource createMonitoringOfficerOrganisation() {
        OrganisationResource organisationResource = new OrganisationResource();
        organisationResource.setName("IFS MO Company");
        organisationResource.setCompaniesHouseNumber("");
        organisationResource.setId(1L);

        return organisationResource;
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
        silOrganisation.setRegistrationNumber(organisation.getCompaniesHouseNumber());
        silOrganisation.setSrcSysOrgId(String.valueOf(organisation.getId()));

        silContact.setOrganisation(silOrganisation);

        return silContact;
    }
}
