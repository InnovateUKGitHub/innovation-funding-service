package com.worth.ifs.controller;

import com.worth.ifs.domain.Cost;
import com.worth.ifs.domain.CostCategory;
import com.worth.ifs.repository.CostCategoryRepository;
import com.worth.ifs.repository.CostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cost")
public class CostController {

    @Autowired
    CostCategoryRepository costCategoryRepository;

    @Autowired
    CostRepository costRepository;

    @RequestMapping("/addAnother/{costCategoryId}")
    public void addAnother(
            @PathVariable("costCategoryId") final Long costCategoryId) {
        CostCategory costCategory = costCategoryRepository.findOne(costCategoryId);
        Cost cost = new Cost("", "", 0, 0d, costCategory);
        costRepository.save(cost);
    }
}
