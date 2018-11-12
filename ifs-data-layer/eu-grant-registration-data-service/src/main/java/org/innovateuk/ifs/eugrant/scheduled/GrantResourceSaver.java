package org.innovateuk.ifs.eugrant.scheduled;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.innovateuk.ifs.eugrant.transactional.EuGrantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * TODO DW - document this class
 */
@Component
public class GrantResourceSaver {

    private EuGrantService grantService;

    @Autowired
    GrantResourceSaver(@Autowired EuGrantService grantService) {
        this.grantService = grantService;
    }

    ServiceResult<EuGrantResource> saveGrant(EuGrantResource grantResource) {

        return grantService.create().andOnSuccess(newGrant ->
               grantService.update(newGrant.getId(), grantResource).andOnSuccess(() ->
               grantService.findById(newGrant.getId())));
    }
}
