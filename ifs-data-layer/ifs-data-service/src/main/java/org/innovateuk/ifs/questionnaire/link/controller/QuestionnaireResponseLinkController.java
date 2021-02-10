package org.innovateuk.ifs.questionnaire.link.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.questionnaire.link.service.QuestionnaireResponseLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/questionnaire-response-link")
public class QuestionnaireResponseLinkController {

    @Autowired
    private QuestionnaireResponseLinkService service;

    @GetMapping("/{questionnaireId}/application/{applicationId}/organisation/{organisationId}")
    public RestResult<Long> get(@PathVariable long questionnaireId,
                                @PathVariable long applicationId,
                                @PathVariable long organisationId) {
        return service.getResponseIdByApplicationIdAndOrganisationIdAndQuestionnaireId(applicationId, organisationId, questionnaireId).toGetResponse();
    }
}
