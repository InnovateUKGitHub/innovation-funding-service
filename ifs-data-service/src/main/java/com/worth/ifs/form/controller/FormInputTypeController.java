package com.worth.ifs.form.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.form.resource.FormInputTypeResource;
import com.worth.ifs.form.transactional.FormInputService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/forminputtype")
public class FormInputTypeController {

    @Autowired
    private FormInputService service;

    @RequestMapping("/{id}")
    public RestResult<FormInputTypeResource> findById(@PathVariable("id") final Long id) {
        return service.findFormInputType(id).toDefaultRestResultForGet();
    }
}