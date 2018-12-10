package org.innovateuk.ifs.finance.service;

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
}
