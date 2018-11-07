package org.innovateuk.ifs.eugrant.scheduled;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.innovateuk.ifs.eugrant.transactional.EuGrantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

/**
 * TODO DW - document this class
 */
@Component
public class GrantsImporter {

    private EuGrantService euGrantService;

    @Autowired
    GrantsImporter(@Autowired EuGrantService euGrantService) {
        this.euGrantService = euGrantService;
    }

    ServiceResult<List<ServiceResult<UUID>>> importGrants(List<ServiceResult<EuGrantResource>> grants) {
        return serviceSuccess(emptyList());
    }
}
