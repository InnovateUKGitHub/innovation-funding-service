package com.worth.ifs.form.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.form.resource.FormValidatorResource;
import com.worth.ifs.form.transactional.FormValidatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.worth.ifs.commons.rest.RestResultBuilder.newRestHandler;

@RestController
@RequestMapping("/formvalidator")
public class FormValidatorController {

    @Autowired
    private FormValidatorService service;

    @RequestMapping("/{id}")
    public RestResult<FormValidatorResource> findById(@PathVariable("id") final Long id) throws ClassNotFoundException{
        return newRestHandler().perform(() -> service.findOne(id));
    }
}