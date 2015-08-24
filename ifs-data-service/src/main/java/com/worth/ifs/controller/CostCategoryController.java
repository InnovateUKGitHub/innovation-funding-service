package com.worth.ifs.controller;

import com.worth.ifs.domain.CostCategory;
import com.worth.ifs.repository.CostCategoryRepository;
import com.worth.ifs.service.CostCategoryService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/costcategory")
public class CostCategoryController {
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    CostCategoryRepository costCategoryRepository;

    @RequestMapping("/findByApplicationFinance/{applicationFinanceId}")
    public List<CostCategory> findByApplicationFinance(
            @PathVariable("applicationFinanceId") final Long applicationFinanceId) {
        return costCategoryRepository.findByApplicationFinanceId(applicationFinanceId);
    }
}
