package com.worth.ifs.application.service;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.address.resource.OrganisationAddressType;
import com.worth.ifs.organisation.resource.OrganisationSearchResult;
import com.worth.ifs.organisation.service.CompanyHouseRestService;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.ProcessRoleResource;
import com.worth.ifs.user.service.OrganisationRestService;
import com.worth.ifs.user.service.ProcessRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * This class contains methods to retrieve and store {@link OrganisationResource} related data,
 * through the RestService {@link com.worth.ifs.user.service.OrganisationRestService}.
 */
@Service
public class OrganisationServiceImpl implements OrganisationService {
    @Autowired
    OrganisationRestService organisationRestService;

    @Autowired
    CompanyHouseRestService companyHouseRestService;

    @Autowired
    private ProcessRoleService processRoleService;

    @Override
    public OrganisationSearchResult getCompanyHouseOrganisation(String organisationId) {
        return companyHouseRestService.getOrganisationById(organisationId);
    }

    @Override
    public OrganisationResource getOrganisationById(Long organisationId) {
        return organisationRestService.getOrganisationById(organisationId).getSuccessObjectOrThrowException();
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
    public String getOrganisationType(Long userId, Long applicationId) {
        final ProcessRoleResource processRoleResource = processRoleService.findProcessRole(userId, applicationId);
        if (processRoleResource != null && processRoleResource.getOrganisation() != null) {
            final OrganisationResource organisationResource = organisationRestService.getOrganisationById(processRoleResource.getOrganisation()).getSuccessObjectOrThrowException();
            return organisationResource.getOrganisationTypeName();
        }
        return "";
    }

    @Override
    public Optional<OrganisationResource> getOrganisationForUser(Long userId, List<ProcessRoleResource> userApplicationRoles) {
        return userApplicationRoles.stream()
            .filter(uar -> uar.getUser().equals(userId))
            .map(uar -> organisationRestService.getOrganisationById(uar.getOrganisation()).getSuccessObjectOrThrowException())
            .findFirst();
    }
}
