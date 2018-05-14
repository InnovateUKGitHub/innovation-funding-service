package org.innovateuk.ifs.affiliation.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.user.resource.AffiliationListResource;
import org.innovateuk.ifs.user.resource.AffiliationResource;

/**
 * Interface for CRUD operations on {@link AffiliationResource} related data.
 */
public interface AffiliationRestService {
    RestResult<AffiliationListResource> getUserAffiliations(long userId);
    RestResult<Void> updateUserAffiliations(long userId, AffiliationListResource affiliations);
}
