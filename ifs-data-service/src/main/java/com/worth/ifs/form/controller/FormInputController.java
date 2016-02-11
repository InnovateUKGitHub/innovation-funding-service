package com.worth.ifs.form.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.form.domain.FormInput;
import com.worth.ifs.form.transactional.FormInputService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.worth.ifs.commons.rest.RestResultBuilder.newRestHandler;

@RestController
@RequestMapping("/forminput")
public class FormInputController {

    @Autowired
    private FormInputService formInputService;

    @RequestMapping("/{id}")
    public RestResult<FormInput> findOne(@PathVariable("id") Long id){
        return newRestHandler().perform(() -> formInputService.findFormInput(id));
    }
}
