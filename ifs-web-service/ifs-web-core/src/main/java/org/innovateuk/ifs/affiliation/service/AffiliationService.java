package org.innovateuk.ifs.affiliation.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.AffiliationResource;

import java.util.List;

/**
 * Interface for CRUD operations on {@link AffiliationResource} related data.
 */
public interface AffiliationService {
    List<AffiliationResource> getUserAffiliations(Long userId);
    ServiceResult<Void> updateUserAffiliations(Long userId, List<AffiliationResource> affiliations);
}
