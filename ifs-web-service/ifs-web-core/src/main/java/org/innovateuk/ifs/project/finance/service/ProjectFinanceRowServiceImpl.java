package org.innovateuk.ifs.project.finance.service;

import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.service.ProjectFinanceRowRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectFinanceRowServiceImpl implements ProjectFinanceRowService {

    @Autowired
    private ProjectFinanceRowRestService financeRowRestService;

    @Override
    public ValidationMessages add(Long applicationFinanceId, Long questionId, FinanceRowItem costItem) {
        return financeRowRestService.add(applicationFinanceId, questionId, costItem).getSuccessObjectOrThrowException();
    }

    @Override
    public FinanceRowItem addWithoutPersisting(Long applicationFinanceId, Long questionId) {
        return financeRowRestService.addWithoutPersisting(applicationFinanceId, questionId).getSuccessObjectOrThrowException();
    }

}
