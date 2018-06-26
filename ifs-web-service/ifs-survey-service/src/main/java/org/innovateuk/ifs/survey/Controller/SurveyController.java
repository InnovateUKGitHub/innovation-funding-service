package org.innovateuk.ifs.survey.Controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.survey.*;
import org.innovateuk.ifs.survey.Form.FeedbackForm;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.RedirectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.rest.RestFailure.error;
import static org.innovateuk.ifs.util.CollectionFunctions.removeDuplicates;

/**
 * A controller for users to complete the satisfaction survey after submitting an application.
 */
@Controller
@RequestMapping("/")
public class SurveyController {

    @Autowired
    private SurveyRestService surveyRestService;

    @GetMapping("/{competitionId}/feedback")
    public String viewFeedback(@ModelAttribute("form") FeedbackForm feedbackForm,
                               BindingResult bindingResult,
                               @PathVariable("competitionId") long competitionId,
                               Model model) {

        model.addAttribute("competitionId", competitionId);

        return "survey/survey";
    }

    @PostMapping("/{competitionId}/feedback")
    public String submitFeedback(HttpServletRequest request,
                                 @ModelAttribute("form") @Valid FeedbackForm feedbackForm,
                                 BindingResult bindingResult,
                                 ValidationHandler validationHandler,
                                 @PathVariable("competitionId") long competitionId,
                                 UserResource user,
                                 Model model) {

        String userUrl = user.getRoles().get(0).getUrl();

        Supplier<String> failureView = () -> viewFeedback(feedbackForm, bindingResult, competitionId, model);
        Supplier<String> successView = () -> RedirectUtils.buildRedirect(request, userUrl);

        SurveyResource surveyResource = getSurveyResource(feedbackForm, competitionId);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            RestResult<Void> sendResult = surveyRestService.save(surveyResource);

            return validationHandler.addAnyErrors(error(removeDuplicates(sendResult.getErrors()))).
                    failNowOrSucceedWith(failureView, successView);
        });
    }

    private SurveyResource getSurveyResource(FeedbackForm feedbackForm, long competitionId) {

        Satisfaction satisfaction = null;

        if (feedbackForm.getSatisfaction() != null){
            satisfaction = Satisfaction.getById(Long.valueOf(feedbackForm.getSatisfaction()));
        }

        SurveyResource surveyResource = new SurveyResource(SurveyType.APPLICATION_SUBMISSION,
                SurveyTargetType.COMPETITION,
                competitionId,
                satisfaction,
                feedbackForm.getComments());

        return surveyResource;
    }
}
