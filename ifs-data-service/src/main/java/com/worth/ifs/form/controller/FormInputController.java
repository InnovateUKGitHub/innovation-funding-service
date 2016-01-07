package com.worth.ifs.form.controller;

import com.worth.ifs.form.domain.FormInput;
import com.worth.ifs.form.repository.FormInputRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/forminput")
public class FormInputController {

    @Autowired
    FormInputRepository formInputRepository;

    @RequestMapping("/{id}")
    public FormInput findOne(@PathVariable("id") Long id){
        return formInputRepository.findOne(id);
    }
}
