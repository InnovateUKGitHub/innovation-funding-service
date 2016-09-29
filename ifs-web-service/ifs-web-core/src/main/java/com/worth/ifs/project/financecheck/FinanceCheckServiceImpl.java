package com.worth.ifs.project.financecheck;

import com.worth.ifs.project.finance.resource.FinanceCheckResource;
import com.worth.ifs.project.finance.service.FinanceCheckRestService;
import com.worth.ifs.project.resource.ProjectOrganisationCompositeId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FinanceCheckServiceImpl implements FinanceCheckService {
    @Autowired
    FinanceCheckRestService financeCheckRestService;

    @Override
    public FinanceCheckResource getByProjectAndOrganisation(ProjectOrganisationCompositeId key){
        return financeCheckRestService.getByProjectAndOrganisation(key.getProjectId(), key.getOrganisationId()).getSuccessObject();
    }

    @Override
    public FinanceCheckResource update(FinanceCheckResource toUpdate){
        return financeCheckRestService.update(toUpdate).getSuccessObjectOrThrowException();
    }
}
