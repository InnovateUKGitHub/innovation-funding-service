package com.worth.ifs.application.finance.service;

import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.domain.CostField;
import com.worth.ifs.finance.service.CostFieldRestService;
import com.worth.ifs.finance.service.CostRestService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CostServiceImpl implements CostService {
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    CostRestService costRestService;

    @Autowired
    CostFieldRestService costFieldRestService;

    @Override
    public List<CostField> getCostFields() {
        return costFieldRestService.getCostFields();
    }

    @Override
    public void update(Cost cost) {
        costRestService.update(cost);
    }

    @Override
    public Cost getById(Long costId) {
        return costRestService.findById(costId);
    }
}
