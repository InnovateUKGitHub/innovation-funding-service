package org.innovateuk.ifs.user.service;

import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.Role;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * This class contains methods to retrieve and store {@link OrganisationResource} related data,
 * through the RestService {@link org.innovateuk.ifs.user.service.OrganisationRestService}.
 */
@Service
public class OrganisationServiceImpl implements OrganisationService {

    private OrganisationRestService organisationRestService;
    private UserRestService userRestService;
    private UserService userService;


    public OrganisationServiceImpl(OrganisationRestService organisationRestService,
                                   UserRestService userRestService, UserService userService) {
        this.organisationRestService = organisationRestService;
        this.userRestService = userRestService;
        this.userService = userService;
    }

    @Override
    public Long getOrganisationType(long userId, long applicationId) {
        final ProcessRoleResource processRoleResource = userRestService.findProcessRole(userId, applicationId).getSuccess();
        if (processRoleResource != null && processRoleResource.getOrganisationId() != null) {
            final OrganisationResource organisationResource = organisationRestService.getOrganisationById(processRoleResource.getOrganisationId()).getSuccess();
            return organisationResource.getOrganisationType();
        }
        return null;
    }

    @Override
    public Optional<OrganisationResource> getOrganisationForUser(long userId, List<ProcessRoleResource> userApplicationRoles) {
        return userApplicationRoles.stream()
            .filter(uar -> uar.getUser().equals(userId))
            .filter(uar -> uar.getOrganisationId() != null)
            .map(uar -> organisationRestService.getOrganisationById(uar.getOrganisationId()).getOptionalSuccessObject())
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findFirst();
    }

    @Override
    public SortedSet<OrganisationResource> getApplicationOrganisations(List<ProcessRoleResource> userApplicationRoles) {
        Comparator<OrganisationResource> compareById =
                Comparator.comparingLong(OrganisationResource::getId);
        Supplier<SortedSet<OrganisationResource>> supplier = () -> new TreeSet<>(compareById);

        return userApplicationRoles.stream()
                .filter(uar -> uar.getRoleName().equals(Role.LEADAPPLICANT.getName())
                        || uar.getRoleName().equals(Role.COLLABORATOR.getName()))
                .map(uar -> organisationRestService.getOrganisationById(uar.getOrganisationId()).getSuccess())
                .collect(Collectors.toCollection(supplier));
    }

    @Override
    public SortedSet<OrganisationResource> getAcademicOrganisations(SortedSet<OrganisationResource> organisations) {
        Comparator<OrganisationResource> compareById =
                Comparator.comparingLong(OrganisationResource::getId);
        Supplier<TreeSet<OrganisationResource>> supplier = () -> new TreeSet<>(compareById);
        ArrayList<OrganisationResource> organisationList = new ArrayList<>(organisations);

        return organisationList.stream()
                .filter(o -> OrganisationTypeEnum.RESEARCH.getId() == o.getOrganisationType())
                .collect(Collectors.toCollection(supplier));
    }

    @Override
    public Optional<OrganisationResource> getApplicationLeadOrganisation(List<ProcessRoleResource> userApplicationRoles) {
        return userApplicationRoles.stream()
                .filter(uar -> uar.getRoleName().equals(Role.LEADAPPLICANT.getName()))
                .map(uar -> organisationRestService.getOrganisationById(uar.getOrganisationId()).getSuccess())
                .findFirst();
    }

    public OrganisationResource getLeadOrganisation(long applicationId, List<OrganisationResource> applicationOrganisations) {
        ProcessRoleResource leadApplicantUser = userService.getLeadApplicantProcessRole(applicationId);
        return applicationOrganisations.stream().filter(org -> org.getId().equals(leadApplicantUser.getOrganisationId())).findFirst().orElse(null);
    }
}