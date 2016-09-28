package com.worth.ifs.project.financecheck;

import com.worth.ifs.project.finance.resource.FinanceCheckResource;
import com.worth.ifs.project.resource.ProjectOrganisationCompositeId;

public interface FinanceCheckService {
    FinanceCheckResource getByProjectAndOrganisation(ProjectOrganisationCompositeId key);

    FinanceCheckResource update(FinanceCheckResource toUpdate);
}
