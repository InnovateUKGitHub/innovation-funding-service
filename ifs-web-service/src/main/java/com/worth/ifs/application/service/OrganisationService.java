package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.user.domain.Organisation;

import java.util.Optional;
import java.util.TreeSet;

/**
 * Interface for CRUD operations on {@link Organisation} related data.
 */
public interface OrganisationService {
    public TreeSet<Organisation> getApplicationOrganisations(Application application);
    public Optional<Organisation> getApplicationLeadOrganisation(Application application);
    public Optional<Organisation> getUserOrganisation(Application application, Long userId);
}
