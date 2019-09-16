package org.innovateuk.ifs.survey.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.survey.*;
import org.innovateuk.ifs.survey.form.FeedbackForm;
import org.innovateuk.ifs.util.NavigationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.rest.RestFailure.error;
import static org.innovateuk.ifs.survey.SurveyType.getSurveyTypeFromString;
import static org.innovateuk.ifs.util.CollectionFunctions.removeDuplicates;

/**
 * A controller for users to complete the satisfaction survey after submitting an application.
 */
@Controller
@RequestMapping("/")
public class SurveyController {

    @Autowired
    private SurveyRestService surveyRestService;

    @Autowired
    private NavigationUtils navigationUtils;

    @GetMapping("/{competitionId}/feedback")
    public String viewFeedback(@ModelAttribute("form") FeedbackForm feedbackForm,
                               BindingResult bindingResult,
                               @RequestParam(value = "type", defaultValue = "APPLICATION_SUBMISSION") String surveyType,
                               @PathVariable("competitionId") long competitionId,
                               Model model) {

        model.addAttribute("competitionId", competitionId);
        model.addAttribute("surveyType", surveyType);

        return "survey/survey";
    }

    @PostMapping("/{competitionId}/feedback")
    public String submitFeedback(HttpServletRequest request,
                                 @ModelAttribute("form") @Valid FeedbackForm feedbackForm,
                                 BindingResult bindingResult,
                                 ValidationHandler validationHandler,
                                 @PathVariable("competitionId") long competitionId,
                                 @RequestParam(value = "type", defaultValue = "APPLICATION_SUBMISSION") String surveyType,
                                 Model model) {

        Supplier<String> failureView = () -> viewFeedback(feedbackForm, bindingResult, surveyType, competitionId, model);
        Supplier<String> successView = () -> navigationUtils.getRedirectToLandingPageUrl(request);

        SurveyResource surveyResource = getSurveyResource(feedbackForm, competitionId, surveyType);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            RestResult<Void> sendResult = surveyRestService.save(surveyResource);

            if (sendResult.getStatusCode().equals(HttpStatus.SERVICE_UNAVAILABLE)) {
                return serviceUnavailable();
            }

            return validationHandler.addAnyErrors(error(removeDuplicates(sendResult.getErrors()))).
                    failNowOrSucceedWith(failureView, successView);
        });
    }

    private SurveyResource getSurveyResource(FeedbackForm feedbackForm, long competitionId, String surveyType) {

        Satisfaction satisfaction = null;

        SurveyType.valueOf(surveyType);



        if (feedbackForm.getSatisfaction() != null){
            satisfaction = Satisfaction.getById(Long.valueOf(feedbackForm.getSatisfaction()));
        }

        SurveyResource surveyResource = new SurveyResource(
                getSurveyTypeFromString(surveyType),
                SurveyTargetType.COMPETITION,
                competitionId,
                satisfaction,
                feedbackForm.getComments());

        return surveyResource;
    }

    private String serviceUnavailable() {
        return "content/service-problems";
    }
}
