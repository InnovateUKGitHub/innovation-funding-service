package org.innovateuk.ifs.survey.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.survey.SurveyResource;
import org.innovateuk.ifs.survey.transactional.SurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SurveyController {

    @Autowired
    private SurveyService surveyService;

    @PostMapping("/survey")
    public RestResult<Void> saveSurvey(@RequestBody SurveyResource surveyResource) {
        return surveyService.save(surveyResource).toPostCreateResponse();
    }

}
