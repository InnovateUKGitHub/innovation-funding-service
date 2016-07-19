package com.worth.ifs.form.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.form.resource.FormInputResource;
import com.worth.ifs.form.resource.FormInputScope;
import com.worth.ifs.form.transactional.FormInputService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/forminput")
public class FormInputController {

    @Autowired
    private FormInputService formInputService;

    @RequestMapping("/{id}")
    public RestResult<FormInputResource> findOne(@PathVariable("id") Long id) {
        return formInputService.findFormInput(id).toGetResponse();
    }

    @RequestMapping("/findByQuestionId/{questionId}")
    public RestResult<List<FormInputResource>> findByQuestionId(@PathVariable("questionId") Long questionId) {
        return formInputService.findByQuestionId(questionId).toGetResponse();
    }

    @RequestMapping(value = "findByQuestionId/{questionId}/scope/{scope}", method = RequestMethod.GET)
    public RestResult<List<FormInputResource>> findByQuestionIdAndScope(@PathVariable("questionId") Long questionId, @PathVariable("scope") FormInputScope scope) {
        return formInputService.findByQuestionIdAndScope(questionId, scope).toGetResponse();
    }

    @RequestMapping("/findByCompetitionId/{competitionId}")
    public RestResult<List<FormInputResource>> findByCompetitionId(@PathVariable("competitionId") Long competitionId) {
        return formInputService.findByCompetitionId(competitionId).toGetResponse();
    }

    @RequestMapping("/findByCompetitionId/{competitionId}/scope/{scope}")
    public RestResult<List<FormInputResource>> findByCompetitionIdAndScope(@PathVariable("competitionId") Long competitionId, @PathVariable("scope") FormInputScope scope) {
        return formInputService.findByCompetitionIdAndScope(competitionId, scope).toGetResponse();
    }
}
