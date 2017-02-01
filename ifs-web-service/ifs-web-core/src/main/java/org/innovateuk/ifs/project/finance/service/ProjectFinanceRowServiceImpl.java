package org.innovateuk.ifs.project.finance.service;

import org.innovateuk.ifs.commons.rest.RestResult;
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
    public ValidationMessages add(Long projectFinanceId, Long questionId, FinanceRowItem costItem) {
        return financeRowRestService.add(projectFinanceId, questionId, costItem).getSuccessObjectOrThrowException();
    }

    @Override
    public RestResult<ValidationMessages> update(FinanceRowItem costItem) {
        return financeRowRestService.update(costItem);
    }

    @Override
    public void delete(Long costId) {
        financeRowRestService.delete(costId);
    }

    @Override
    public FinanceRowItem addWithoutPersisting(Long applicationFinanceId, Long questionId) {
        return financeRowRestService.addWithoutPersisting(applicationFinanceId, questionId).getSuccessObjectOrThrowException();
    }

    @Override
    public FinanceRowItem findById(Long costId) {
        return financeRowRestService.findById(costId).getSuccessObjectOrThrowException();
    }
}
