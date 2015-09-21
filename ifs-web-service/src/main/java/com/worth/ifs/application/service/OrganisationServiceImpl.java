package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.model.UserApplicationRole;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class OrganisationServiceImpl implements OrganisationService {
    public TreeSet<Organisation> getApplicationOrganisations(Application application) {
        List<ProcessRole> userApplicationRoles = application.getProcessRoles();
        Comparator<Organisation> compareById =
                Comparator.comparingLong(Organisation::getId);
        Supplier<TreeSet<Organisation>> supplier = () -> new TreeSet<Organisation>(compareById);
        TreeSet<Organisation> organisations = userApplicationRoles.stream()
                .map(uar -> uar.getOrganisation())
                .collect(Collectors.toCollection(supplier));

        return organisations;
    }

    public Optional<Organisation> getApplicationLeadOrganisation(Application application) {
        List<ProcessRole> userApplicationRoles = application.getProcessRoles();

        Optional<Organisation> leadOrganisation = userApplicationRoles.stream()
                .filter(uar -> uar.getRole().getName().equals(UserApplicationRole.LEAD_APPLICANT.getRoleName()))
                .map(uar -> uar.getOrganisation())
                .findFirst();

        return leadOrganisation;
    }

    public Optional<Organisation> getUserOrganisation(Application application, Long userId) {
        List<ProcessRole> userApplicationRoles = application.getProcessRoles();

        Optional<Organisation> userOrganisation = userApplicationRoles.stream()
                .filter(uar -> uar.getUser().getId().equals(userId))
                .map(uar -> uar.getOrganisation())
                .findFirst();

        return userOrganisation;
    }
}
