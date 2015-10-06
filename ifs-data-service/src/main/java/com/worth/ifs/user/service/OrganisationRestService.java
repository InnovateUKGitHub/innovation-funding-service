package com.worth.ifs.user.service;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.user.domain.Organisation;
import java.util.List;

/**
 * Interface for CRUD operations on {@link Organisation} related data.
 */
public interface OrganisationRestService {
    public List<Organisation> getOrganisationsByApplicationId(Long applicationId);
}
