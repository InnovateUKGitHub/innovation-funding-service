package com.worth.ifs.application.finance.service;

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
        return costFieldRestService.getCostFields().getSuccessObjectOrNull();
    }

    @Override
    public void update(CostItem costItem) {
        costRestService.update(costItem);
    }

    @Override
    public CostItem getById(Long costId) {
        return costRestService.findById(costId).getSuccessObjectOrNull();
    }

    @Override
    public void delete(Long costId) {costRestService.delete(costId);}

    @Override
    public void add(Long applicationFinanceId, Long questionId, CostItem costItem) {
        costRestService.add(applicationFinanceId, questionId, costItem);
    }
}
