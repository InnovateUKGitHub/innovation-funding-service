package com.worth.ifs.application.finance.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.finance.resource.CostFieldResource;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.service.CostFieldRestService;
import com.worth.ifs.finance.service.CostRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * {@code CostService} implements {@link CostService}
 */
// TODO DW - INFUND-1555 - handle rest results
@Service
public class CostServiceImpl implements CostService {

    @Autowired
    private CostRestService costRestService;

    @Autowired
    private CostFieldRestService costFieldRestService;

    @Override
    public List<CostFieldResource> getCostFields() {
        return costFieldRestService.getCostFields().getSuccessObjectOrThrowException();
    }

    @Override
    public RestResult<ValidationMessages> update(CostItem costItem) {
        RestResult<ValidationMessages> validationMessages = costRestService.update(costItem);
        return validationMessages;
    }

    @Override
    public void delete(Long costId) {costRestService.delete(costId);}

    @Override
    public CostItem add(Long applicationFinanceId, Long questionId, CostItem costItem) {
        return costRestService.add(applicationFinanceId, questionId, costItem).getSuccessObjectOrThrowException();
    }
    
    @Override
    public CostItem addWithoutPersisting(Long applicationFinanceId, Long questionId) {
        return costRestService.addWithoutPersisting(applicationFinanceId, questionId).getSuccessObjectOrThrowException();
    }
}
