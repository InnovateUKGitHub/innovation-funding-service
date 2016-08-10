package com.worth.ifs.form.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.form.resource.FormInputResource;
import com.worth.ifs.form.resource.FormInputScope;
import com.worth.ifs.form.transactional.FormInputService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Exposes CRUD operations through a REST API to manage {@link com.worth.ifs.form.domain.FormInput} related data.
 */
@RestController
@RequestMapping("/forminput")
public class FormInputController {

    @Autowired
    private FormInputService formInputService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public RestResult<FormInputResource> findOne(@PathVariable("id") Long id) {
        return formInputService.findFormInput(id).toGetResponse();
    }

    @RequestMapping(value = "/findByQuestionId/{questionId}", method = RequestMethod.GET)
    public RestResult<List<FormInputResource>> findByQuestionId(@PathVariable("questionId") Long questionId) {
        return formInputService.findByQuestionId(questionId).toGetResponse();
    }

    @RequestMapping(value = "findByQuestionId/{questionId}/scope/{scope}", method = RequestMethod.GET)
    public RestResult<List<FormInputResource>> findByQuestionIdAndScope(@PathVariable("questionId") Long questionId, @PathVariable("scope") FormInputScope scope) {
        return formInputService.findByQuestionIdAndScope(questionId, scope).toGetResponse();
    }

    @RequestMapping(value = "/findByCompetitionId/{competitionId}", method = RequestMethod.GET)
    public RestResult<List<FormInputResource>> findByCompetitionId(@PathVariable("competitionId") Long competitionId) {
        return formInputService.findByCompetitionId(competitionId).toGetResponse();
    }

    @RequestMapping(value = "/findByCompetitionId/{competitionId}/scope/{scope}", method = RequestMethod.GET)
    public RestResult<List<FormInputResource>> findByCompetitionIdAndScope(@PathVariable("competitionId") Long competitionId, @PathVariable("scope") FormInputScope scope) {
        return formInputService.findByCompetitionIdAndScope(competitionId, scope).toGetResponse();
    }

    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public RestResult<FormInputResource> save(@RequestBody FormInputResource formInputResource) {
        return formInputService.save(formInputResource).toGetResponse();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public RestResult<Void> delete(@PathVariable("id") Long id) {
        return formInputService.delete(id).toDeleteResponse();
    }
}
