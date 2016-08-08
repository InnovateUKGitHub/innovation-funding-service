package com.worth.ifs.application.finance.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.finance.resource.FinanceRowMetaFieldResource;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.service.CostRestService;
import com.worth.ifs.finance.service.FinanceRowMetaFieldRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * {@code FinanceRowService} implements {@link FinanceRowService}
 */
@Service
public class FinanceRowServiceImpl implements FinanceRowService {

    @Autowired
    private CostRestService costRestService;

    @Autowired
    private FinanceRowMetaFieldRestService costFieldRestService;

    @Override
    public List<FinanceRowMetaFieldResource> getCostFields() {
        return costFieldRestService.getFinanceRowMetaFields().getSuccessObjectOrThrowException();
    }

    @Override
    public RestResult<ValidationMessages> update(CostItem costItem) {
        return costRestService.update(costItem);
    }

    @Override
    public void delete(Long costId) {costRestService.delete(costId);}

    @Override
    public ValidationMessages add(Long applicationFinanceId, Long questionId, CostItem costItem) {
        return costRestService.add(applicationFinanceId, questionId, costItem).getSuccessObjectOrThrowException();
    }
    
    @Override
    public CostItem addWithoutPersisting(Long applicationFinanceId, Long questionId) {
        return costRestService.addWithoutPersisting(applicationFinanceId, questionId).getSuccessObjectOrThrowException();
    }

	@Override
	public CostItem findById(Long costId) {
        return costRestService.findById(costId).getSuccessObjectOrThrowException();
	}
}
