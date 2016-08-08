package com.worth.ifs.finance.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.finance.resource.FinanceRowMetaValueId;
import com.worth.ifs.finance.resource.FinanceRowMetaValueResource;
import com.worth.ifs.finance.transactional.FinanceRowMetaValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/costvalue")
public class FinanceRowMetaValueController {

    @Autowired
    private FinanceRowMetaValueService service;

    @RequestMapping("/{id}")
    public RestResult<FinanceRowMetaValueResource> findById(@PathVariable("id") final FinanceRowMetaValueId id) {
        return service.findOne(id).toGetResponse();
    }
}