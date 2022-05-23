package org.innovateuk.ifs.form.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.controller.FileControllerUtils;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.innovateuk.ifs.form.transactional.FormInputService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * Exposes CRUD operations through a REST API to manage {@link org.innovateuk.ifs.form.domain.FormInput} related data.
 */
@RestController
@RequestMapping("/forminput")
public class FormInputController {

    @Autowired
    private FormInputService formInputService;

    private final static FileControllerUtils fileControllerUtils = new FileControllerUtils();

    @GetMapping("/{id}")
    public RestResult<FormInputResource> findOne(@PathVariable("id") Long id) {
        return formInputService.findFormInput(id).toGetResponse();
    }

    @GetMapping("/find-by-question-id/{questionId}")
    public RestResult<List<FormInputResource>> findByQuestionId(@PathVariable("questionId") Long questionId) {
        return formInputService.findByQuestionId(questionId).toGetResponse();
    }

    @GetMapping("find-by-question-id/{questionId}/scope/{scope}")
    public RestResult<List<FormInputResource>> findByQuestionIdAndScope(@PathVariable("questionId") Long questionId, @PathVariable("scope") FormInputScope scope) {
        return formInputService.findByQuestionIdAndScope(questionId, scope).toGetResponse();
    }

    @GetMapping("/find-by-competition-id/{competitionId}")
    public RestResult<List<FormInputResource>> findByCompetitionId(@PathVariable("competitionId") Long competitionId) {
        return formInputService.findByCompetitionId(competitionId).toGetResponse();
    }

    @GetMapping("/find-by-competition-id/{competitionId}/scope/{scope}")
    public RestResult<List<FormInputResource>> findByCompetitionIdAndScope(@PathVariable("competitionId") Long competitionId, @PathVariable("scope") FormInputScope scope) {
        return formInputService.findByCompetitionIdAndScope(competitionId, scope).toGetResponse();
    }

    @DeleteMapping("/{id}")
    public RestResult<Void> delete(@PathVariable("id") Long id) {
        return formInputService.delete(id).toDeleteResponse();
    }

    @GetMapping(value = "/file/{formInputId}", produces = "application/json")
    public @ResponseBody
    ResponseEntity<Object> downloadFile(@PathVariable long formInputId) throws IOException {
        return fileControllerUtils.handleFileDownload(() -> formInputService.downloadFile(formInputId));
    }

    @GetMapping(value = "/file-details/{formInputId}", produces = "application/json")
    public RestResult<FileEntryResource> findFile(@PathVariable long formInputId) throws IOException {
        return formInputService.findFile(formInputId).toGetResponse();
    }
}
