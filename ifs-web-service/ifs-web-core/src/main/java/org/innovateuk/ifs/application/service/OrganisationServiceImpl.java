package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.innovateuk.ifs.organisation.service.CompanyHouseRestService;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * This class contains methods to retrieve and store {@link OrganisationResource} related data,
 * through the RestService {@link org.innovateuk.ifs.user.service.OrganisationRestService}.
 */
@Service
public class OrganisationServiceImpl implements OrganisationService {
    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private CompanyHouseRestService companyHouseRestService;

    @Autowired
    private ProcessRoleService processRoleService;

    @Override
    public OrganisationSearchResult getCompanyHouseOrganisation(String organisationId) {
        return companyHouseRestService.getOrganisationById(organisationId).getSuccessObjectOrThrowException();
    }

    @Override
    public OrganisationResource getOrganisationById(Long organisationId) {
        return organisationRestService.getOrganisationById(organisationId).getSuccessObjectOrThrowException();
    }

    @Override
    public OrganisationResource getOrganisationForUser(Long userId) {
        return organisationRestService.getOrganisationByUserId(userId).getSuccessObjectOrThrowException();
    }

    @Override
    public OrganisationResource getOrganisationByIdForAnonymousUserFlow(Long organisationId) {
        return organisationRestService.getOrganisationByIdForAnonymousUserFlow(organisationId).getSuccessObjectOrThrowException();
    }

    @Override
    public OrganisationResource save(OrganisationResource organisation) {
        return organisationRestService.update(organisation).getSuccessObjectOrThrowException();
    }

    @Override
    public OrganisationResource updateNameAndRegistration(OrganisationResource organisation){
        return organisationRestService.updateNameAndRegistration(organisation).getSuccessObjectOrThrowException();
    }

    @Override
    public OrganisationResource saveForAnonymousUserFlow(OrganisationResource organisation) {
        return organisationRestService.updateByIdForAnonymousUserFlow(organisation).getSuccessObjectOrThrowException();
    }

    @Override
    public OrganisationResource addAddress(OrganisationResource organisation, AddressResource address, OrganisationAddressType addressType) {
        return organisationRestService.addAddress(organisation, address, addressType).getSuccessObjectOrThrowException();
    }

    @Override
    public Long getOrganisationType(Long userId, Long applicationId) {
        final ProcessRoleResource processRoleResource = processRoleService.findProcessRole(userId, applicationId);
        if (processRoleResource != null && processRoleResource.getOrganisationId() != null) {
            final OrganisationResource organisationResource = organisationRestService.getOrganisationById(processRoleResource.getOrganisationId()).getSuccessObjectOrThrowException();
            return organisationResource.getOrganisationType();
        }
        return null;
    }

    @Override
    public Optional<OrganisationResource> getOrganisationForUser(Long userId, List<ProcessRoleResource> userApplicationRoles) {
        return userApplicationRoles.stream()
            .filter(uar -> uar.getUser().equals(userId))
            .map(uar -> organisationRestService.getOrganisationById(uar.getOrganisationId()).getSuccessObjectOrThrowException())
            .findFirst();
    }
}
