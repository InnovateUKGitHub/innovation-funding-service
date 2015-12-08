package com.worth.ifs.application.service;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.user.domain.Organisation;

import java.util.Optional;
import java.util.TreeSet;

/**
 * Interface for CRUD operations on {@link Organisation} related data.
 */
public interface OrganisationService {
    public TreeSet<Organisation> getApplicationOrganisations(ApplicationResource application);
    public Optional<Organisation> getApplicationLeadOrganisation(ApplicationResource application);
    public Optional<Organisation> getUserOrganisation(ApplicationResource application, Long userId);
}
