package com.worth.ifs.finance.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.finance.resource.CostValueId;
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

    @RequestMapping("/{id}")
    public RestResult<CostValueResource> findById(@PathVariable("id") final CostValueId id) {
        return service.findOne(id).toGetResponse();
    }
}