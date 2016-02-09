package com.worth.ifs.form.controller;

import com.worth.ifs.form.mapper.FormInputTypeMapper;
import com.worth.ifs.form.resource.FormInputTypeResource;
import com.worth.ifs.form.transactional.FormInputTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/forminputtype")
public class FormInputTypeController {
    @Autowired
    private FormInputTypeService service;

    @Autowired
    private FormInputTypeMapper mapper;

    @RequestMapping("/{id}")
    public FormInputTypeResource findById(@PathVariable("id") final Long id) {
        return mapper.mapFormInputTypeToResource(service.findOne(id));
    }
}