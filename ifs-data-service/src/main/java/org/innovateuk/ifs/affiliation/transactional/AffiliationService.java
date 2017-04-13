package org.innovateuk.ifs.affiliation.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.AffiliationResource;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * A Service that covers basic operations concerning Affiliations
 */
public interface AffiliationService {

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<AffiliationResource>> getUserAffiliations(Long userId);

    @PreAuthorize("hasPermission(#userId, 'org.innovateuk.ifs.user.resource.UserResource', 'UPDATE')")
    ServiceResult<Void> updateUserAffiliations(Long userId, List<AffiliationResource> affiliations);
}
