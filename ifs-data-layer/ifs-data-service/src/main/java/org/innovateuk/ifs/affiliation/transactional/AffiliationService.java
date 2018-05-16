package org.innovateuk.ifs.affiliation.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.AffiliationListResource;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * A Service that covers basic operations concerning Affiliations
 */
public interface AffiliationService {

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<AffiliationListResource> getUserAffiliations(long userId);

    @PreAuthorize("hasPermission(#userId, 'org.innovateuk.ifs.user.resource.UserResource', 'UPDATE')")
    ServiceResult<Void> updateUserAffiliations(long userId, AffiliationListResource affiliations);
}
