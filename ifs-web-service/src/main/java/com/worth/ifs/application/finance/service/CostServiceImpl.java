package com.worth.ifs.application.finance.service;

import com.worth.ifs.finance.resource.CostFieldResource;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.service.CostFieldRestService;
import com.worth.ifs.finance.service.CostRestService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * {@code CostService} implements {@link CostService}
 */
@Service
public class CostServiceImpl implements CostService {
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    CostRestService costRestService;

    @Autowired
    CostFieldRestService costFieldRestService;

    @Override
    public List<CostFieldResource> getCostFields() {
        return costFieldRestService.getCostFields();
    }

    @Override
    public void update(CostItem costItem) {
        costRestService.update(costItem);
    }

    @Override
    public CostItem getById(Long costId) {
        return costRestService.findById(costId);
    }

    @Override
    public void delete(Long costId) {costRestService.delete(costId);}

    @Override
    public void add(Long applicationFinanceId, Long questionId, CostItem costItem) {
        costRestService.add(applicationFinanceId, questionId, costItem);
    }
}
