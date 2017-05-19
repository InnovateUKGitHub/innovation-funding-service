package org.innovateuk.ifs.affiliation.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.user.resource.AffiliationResource;

import java.util.List;

/**
 * Interface for CRUD operations on {@link AffiliationResource} related data.
 */
public interface AffiliationRestService {
    RestResult<List<AffiliationResource>> getUserAffiliations(Long userId);
    RestResult<Void> updateUserAffiliations(Long userId, List<AffiliationResource> affiliations);
}
