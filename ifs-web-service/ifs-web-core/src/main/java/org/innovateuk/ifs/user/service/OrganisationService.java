package org.innovateuk.ifs.user.service;


import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;

import java.util.List;
import java.util.Optional;
import java.util.SortedSet;

/**
 * Interface for CRUD operations on {@link OrganisationResource} related data.
 */
public interface OrganisationService {

    Long getOrganisationType(long userId, long applicationId);

    Optional<OrganisationResource> getOrganisationForUser(long userId, List<ProcessRoleResource> userApplicationRoles);

    SortedSet<OrganisationResource> getApplicationOrganisations(List<ProcessRoleResource> userApplicationRoles);

    SortedSet<OrganisationResource> getAcademicOrganisations(SortedSet<OrganisationResource> organisations);

    Optional<OrganisationResource> getApplicationLeadOrganisation(List<ProcessRoleResource> userApplicationRoles);
}