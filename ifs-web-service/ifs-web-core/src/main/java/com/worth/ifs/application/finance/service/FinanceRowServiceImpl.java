package com.worth.ifs.application.finance.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.finance.resource.FinanceRowMetaFieldResource;
import com.worth.ifs.finance.resource.cost.FinanceRowItem;
import com.worth.ifs.finance.service.FinanceRowMetaFieldRestService;
import com.worth.ifs.finance.service.FinanceRowRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * {@code FinanceRowService} implements {@link FinanceRowService}
 */
@Service
public class FinanceRowServiceImpl implements FinanceRowService {

    @Autowired
    private FinanceRowRestService financeRowRestService;

    @Autowired
    private FinanceRowMetaFieldRestService financeRowMetaFieldRestService;

    @Override
    public List<FinanceRowMetaFieldResource> getCostFields() {
        return financeRowMetaFieldRestService.getFinanceRowMetaFields().getSuccessObjectOrThrowException();
    }

    @Override
    public RestResult<ValidationMessages> update(FinanceRowItem costItem) {
        return financeRowRestService.update(costItem);
    }

    @Override
    public void delete(Long costId) {
        financeRowRestService.delete(costId);}

    @Override
    public ValidationMessages add(Long applicationFinanceId, Long questionId, FinanceRowItem costItem) {
        return financeRowRestService.add(applicationFinanceId, questionId, costItem).getSuccessObjectOrThrowException();
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
