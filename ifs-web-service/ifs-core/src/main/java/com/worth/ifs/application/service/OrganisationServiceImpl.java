package com.worth.ifs.application.service;

import com.worth.ifs.address.domain.AddressType;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.application.UserApplicationRole;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.organisation.resource.OrganisationSearchResult;
import com.worth.ifs.organisation.service.CompanyHouseRestService;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.service.OrganisationRestService;
import com.worth.ifs.user.service.ProcessRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.worth.ifs.application.service.Futures.call;

/**
 * This class contains methods to retrieve and store {@link Organisation} related data,
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
    public SortedSet<OrganisationResource> getApplicationOrganisations(ApplicationResource application) {
        List<ProcessRole> userApplicationRoles = call(application.getProcessRoles().stream()
                .map(id -> processRoleService.getById(id))
                .collect(Collectors.toList()));

        Comparator<OrganisationResource> compareById =
                Comparator.comparingLong(OrganisationResource::getId);
        Supplier<SortedSet<OrganisationResource>> supplier = () -> new TreeSet<>(compareById);

        // TODO DW - INFUND-1604 - remove the step of mapping organisations to OrganisationResources below when ProcessRoles
        // are converted to ProcessRoleResources
        return userApplicationRoles.stream()
                .filter(uar -> (uar.getRole().getName().equals(UserApplicationRole.LEAD_APPLICANT.getRoleName()) || uar.getRole().getName().equals(UserApplicationRole.COLLABORATOR.getRoleName())))
                .map(ProcessRole::getOrganisation)
                .map(organisation -> organisationRestService.getOrganisationById(organisation.getId()).getSuccessObjectOrThrowException())
                .collect(Collectors.toCollection(supplier));
    }

    @Override
    public Optional<OrganisationResource> getApplicationLeadOrganisation(ApplicationResource application) {
        List<ProcessRole> userApplicationRoles = call(application.getProcessRoles().stream()
                .map(id -> processRoleService.getById(id))
                .collect(Collectors.toList()));

        // TODO DW - INFUND-1604 - remove the step of mapping organisations to OrganisationResources below when ProcessRoles
        // are converted to ProcessRoleResources
        return userApplicationRoles.stream()
                .filter(uar -> uar.getRole().getName().equals(UserApplicationRole.LEAD_APPLICANT.getRoleName()))
                .map(ProcessRole::getOrganisation)
                .map(organisation -> organisationRestService.getOrganisationById(organisation.getId()).getSuccessObjectOrThrowException())
                .findFirst();
    }

    @Override
    public Optional<OrganisationResource> getUserOrganisation(ApplicationResource application, Long userId) {
        List<ProcessRole> userApplicationRoles = call(application.getProcessRoles().stream()
                .map(id -> processRoleService.getById(id))
                .collect(Collectors.toList()));

        // TODO DW - INFUND-1604 - remove the step of mapping organisations to OrganisationResources below when ProcessRoles
        // are converted to ProcessRoleResources
        return userApplicationRoles.stream()
                .filter(uar -> uar.getUser().getId().equals(userId))
                .map(ProcessRole::getOrganisation)
                .map(organisation -> organisationRestService.getOrganisationById(organisation.getId()).getSuccessObjectOrThrowException())
                .findFirst();
    }

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
    public OrganisationResource addAddress(OrganisationResource organisation, AddressResource address, AddressType addressType) {
        return organisationRestService.addAddress(organisation, address, addressType).getSuccessObjectOrThrowException();
    }

    @Override
    public String getOrganisationType(Long userId, Long applicationId) {
        ProcessRole processRole = processRoleService.findProcessRole(userId, applicationId);
        if(processRole!=null && processRole.getOrganisation()!=null) {
            return processRole.getOrganisation().getOrganisationType().getName();
        }
        return "";
    }
}
