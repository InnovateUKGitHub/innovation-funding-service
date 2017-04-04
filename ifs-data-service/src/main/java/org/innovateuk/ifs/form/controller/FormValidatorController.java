package org.innovateuk.ifs.form.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.form.resource.FormValidatorResource;
import org.innovateuk.ifs.form.transactional.FormValidatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/formvalidator")
public class FormValidatorController {

    @Autowired
    private FormValidatorService service;

    @GetMapping("/{id}")
    public RestResult<FormValidatorResource> findById(@PathVariable("id") final Long id) throws ClassNotFoundException{
        return service.findOne(id).toGetResponse();
    }
}
