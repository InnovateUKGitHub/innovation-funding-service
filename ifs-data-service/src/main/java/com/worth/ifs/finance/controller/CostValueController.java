package com.worth.ifs.finance.controller;

import com.worth.ifs.finance.domain.CostValueId;
import com.worth.ifs.finance.mapper.CostValueMapper;
import com.worth.ifs.finance.resource.CostValueResource;
import com.worth.ifs.finance.transactional.CostValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/costvalue")
public class CostValueController {
    @Autowired
    private CostValueService service;

    @Autowired
    private CostValueMapper mapper;

    @RequestMapping("/{id}")
    public CostValueResource findById(@PathVariable("id") final CostValueId id) {
        return mapper.mapCostValueToResource(service.findOne(id));
    }
}