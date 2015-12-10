package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.model.UserApplicationRole;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.organisation.resource.CompanyHouseBusiness;
import com.worth.ifs.organisation.service.CompanyHouseRestService;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * This class contains methods to retrieve and store {@link Organisation} related data,
 * through the RestService {@link com.worth.ifs.user.service.OrganisationRestService}.
 */
@Service
public class OrganisationServiceImpl implements OrganisationService {

    @Autowired
    CompanyHouseRestService companyHouseRestService;
    @Autowired
    private ProcessRoleService processRoleService;

    public TreeSet<Organisation> getApplicationOrganisations(ApplicationResource application) {
        List<ProcessRole> userApplicationRoles = application.getProcessRoleIds().stream()
            .map(id -> processRoleService.getById(id))
            .collect(Collectors.toList());

        Comparator<Organisation> compareById =
                Comparator.comparingLong(Organisation::getId);
        Supplier<TreeSet<Organisation>> supplier = () -> new TreeSet<>(compareById);

        return userApplicationRoles.stream()
                .filter(uar -> (uar.getRole().getName().equals(UserApplicationRole.LEAD_APPLICANT.getRoleName()) || uar.getRole().getName().equals(UserApplicationRole.COLLABORATOR.getRoleName())))
                        .map(ProcessRole::getOrganisation)
                        .collect(Collectors.toCollection(supplier));
    }

    public Optional<Organisation> getApplicationLeadOrganisation(ApplicationResource application) {
        List<ProcessRole> userApplicationRoles = application.getProcessRoleIds().stream()
            .map(id -> processRoleService.getById(id))
            .collect(Collectors.toList());

        return userApplicationRoles.stream()
                .filter(uar -> uar.getRole().getName().equals(UserApplicationRole.LEAD_APPLICANT.getRoleName()))
                .map(ProcessRole::getOrganisation)
                .findFirst();
    }

    public Optional<Organisation> getUserOrganisation(ApplicationResource application, Long userId) {
        List<ProcessRole> userApplicationRoles = application.getProcessRoleIds().stream()
            .map(id -> processRoleService.getById(id))
            .collect(Collectors.toList());
        return userApplicationRoles.stream()
                .filter(uar -> uar.getUser().getId().equals(userId))
                .map(ProcessRole::getOrganisation)
                .findFirst();
    }

    @Override
    public CompanyHouseBusiness getCompanyHouseOrganisation(String organisationId) {
        return  companyHouseRestService.getOrganisationById(organisationId);
    }

    @Override
    public List<CompanyHouseBusiness> searchCompanyHouseOrganisations(String name) {
        return  companyHouseRestService.searchOrganisationsByName(name);
    }


}
