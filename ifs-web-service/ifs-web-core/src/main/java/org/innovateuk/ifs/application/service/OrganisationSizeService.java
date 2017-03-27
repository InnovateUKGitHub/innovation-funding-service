package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.finance.resource.OrganisationSizeResource;

import java.util.List;

/**
 * Interface for CRUD operations on {@link OrganisationSizeResource} related data.
 */
public interface OrganisationSizeService {
    List<OrganisationSizeResource> getOrganisationSizes();
}
