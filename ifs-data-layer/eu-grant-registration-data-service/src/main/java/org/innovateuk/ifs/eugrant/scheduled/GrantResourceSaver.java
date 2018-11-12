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

        return grantService.create().andOnSuccessDo(
               newGrant -> grantResource.setId(newGrant.getId())).andOnSuccess(() ->
               grantService.update(grantResource.getId(), grantResource).andOnSuccess(() ->
               grantService.submit(grantResource.getId(), false)));
    }
}
