package com.worth.ifs.user.service;

import com.worth.ifs.user.domain.Organisation;
import java.util.List;

/**
 * ApplicationRestRestService is a utility to use client-side to retrieve Application data from the data-service controllers.
 */

public interface OrganisationRestService {
    public List<Organisation> getOrganisationsByApplicationId(Long applicationId);
}
