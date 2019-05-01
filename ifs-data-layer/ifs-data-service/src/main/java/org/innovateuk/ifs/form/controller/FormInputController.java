package org.innovateuk.ifs.form.controller;

import org.innovateuk.ifs.commons.ZeroDowntime;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.innovateuk.ifs.form.transactional.FormInputService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Exposes CRUD operations through a REST API to manage {@link org.innovateuk.ifs.form.domain.FormInput} related data.
 */
@RestController
@RequestMapping("/forminput")
public class FormInputController {

    @Autowired
    private FormInputService formInputService;

    @GetMapping("/{id}")
    public RestResult<FormInputResource> findOne(@PathVariable("id") Long id) {
        return formInputService.findFormInput(id).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({"/findByQuestionId/{questionId}", "/find-by-question-id/{questionId}"})
    public RestResult<List<FormInputResource>> findByQuestionId(@PathVariable("questionId") Long questionId) {
        return formInputService.findByQuestionId(questionId).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({"findByQuestionId/{questionId}/scope/{scope}", "find-by-question-id/{questionId}/scope/{scope}"})
    public RestResult<List<FormInputResource>> findByQuestionIdAndScope(@PathVariable("questionId") Long questionId, @PathVariable("scope") FormInputScope scope) {
        return formInputService.findByQuestionIdAndScope(questionId, scope).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({"/findByCompetitionId/{competitionId}", "/find-by-competition-id/{competitionId}"})
    public RestResult<List<FormInputResource>> findByCompetitionId(@PathVariable("competitionId") Long competitionId) {
        return formInputService.findByCompetitionId(competitionId).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({"/findByCompetitionId/{competitionId}/scope/{scope}", "/find-by-competition-id/{competitionId}/scope/{scope}"})
    public RestResult<List<FormInputResource>> findByCompetitionIdAndScope(@PathVariable("competitionId") Long competitionId, @PathVariable("scope") FormInputScope scope) {
        return formInputService.findByCompetitionIdAndScope(competitionId, scope).toGetResponse();
    }

    @PutMapping("/")
    public RestResult<FormInputResource> save(@RequestBody FormInputResource formInputResource) {
        return formInputService.save(formInputResource).toGetResponse();
    }

    @DeleteMapping("/{id}")
    public RestResult<Void> delete(@PathVariable("id") Long id) {
        return formInputService.delete(id).toDeleteResponse();
    }
}
