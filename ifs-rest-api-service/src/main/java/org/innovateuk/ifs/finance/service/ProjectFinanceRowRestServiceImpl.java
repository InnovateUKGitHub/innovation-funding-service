package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.springframework.stereotype.Service;

/**
 * FinanceRowRestServiceImpl is a utility for CRUD operations on
 * {@link org.innovateuk.ifs.finance.resource.FinanceRowResource}.
 */
@Service
public class ProjectFinanceRowRestServiceImpl extends BaseFinanceRowRestServiceImpl implements
        ProjectFinanceRowRestService {

    public ProjectFinanceRowRestServiceImpl() {
        super("/cost/project");
    }

    @Override
    public RestResult<Void> delete(Long projectId, Long organisationId, Long costId) {
        return deleteWithRestResult(getCostRestUrl() + "/" + projectId + "/organisation/" + organisationId +
                "/delete/" + costId);
    }
}
