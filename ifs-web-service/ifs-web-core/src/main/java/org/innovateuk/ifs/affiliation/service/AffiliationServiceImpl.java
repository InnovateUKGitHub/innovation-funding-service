package org.innovateuk.ifs.affiliation.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.AffiliationResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * This class contains methods to retrieve and store {@link AffiliationResource} related data,
 * through the RestService {@link AffiliationRestService}.
 */
@Service
public class AffiliationServiceImpl implements AffiliationService{

    @Autowired
    private AffiliationRestService affiliationRestService;

    @Override
    public List<AffiliationResource> getUserAffiliations(Long userId) {
        return affiliationRestService.getUserAffiliations(userId).getSuccessObjectOrThrowException();
    }

    @Override
    public ServiceResult<Void> updateUserAffiliations(Long userId, List<AffiliationResource> affiliations) {
        return affiliationRestService.updateUserAffiliations(userId, affiliations).toServiceResult();
    }
}
