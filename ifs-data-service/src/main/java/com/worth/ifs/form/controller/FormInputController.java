package com.worth.ifs.form.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.form.domain.FormInput;
import com.worth.ifs.form.resource.FormInputResource;
import com.worth.ifs.form.transactional.FormInputService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/forminput")
public class FormInputController {

    @Autowired
    private FormInputService formInputService;

    @RequestMapping("/{id}")
    public RestResult<FormInputResource> findOne(@PathVariable("id") Long id){
        return formInputService.findFormInput(id).toGetResponse();
    }

    @RequestMapping("/findByQuestionId/{questionId}")
    public RestResult<List<FormInputResource>> findByQuestionId(@PathVariable("questionId") Long questionId){
        return formInputService.findByQuestionId(questionId).toGetResponse();
    }
}
