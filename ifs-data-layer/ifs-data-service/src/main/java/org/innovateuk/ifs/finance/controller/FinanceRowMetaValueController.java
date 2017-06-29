package org.innovateuk.ifs.finance.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.resource.FinanceRowMetaValueResource;
import org.innovateuk.ifs.finance.transactional.FinanceRowMetaValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/costvalue")
public class FinanceRowMetaValueController {

    @Autowired
    private FinanceRowMetaValueService service;

    @GetMapping("/{id}")
    public RestResult<FinanceRowMetaValueResource> findById(@PathVariable("id") final Long id) {
        return service.findOne(id).toGetResponse();
    }
}
