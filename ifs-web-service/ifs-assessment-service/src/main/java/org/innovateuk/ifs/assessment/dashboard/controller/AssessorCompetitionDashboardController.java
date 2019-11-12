package org.innovateuk.ifs.assessment.dashboard.controller;

import org.innovateuk.ifs.assessment.common.service.AssessmentService;
import org.innovateuk.ifs.assessment.dashboard.form.AssessorCompetitionDashboardAssessmentForm;
import org.innovateuk.ifs.assessment.dashboard.populator.AssessorCompetitionDashboardModelPopulator;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.function.Supplier;

import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;

/**
 * This controller will handle all requests that are related to the assessor competition dashboard.
 */
@Controller
@RequestMapping(value = "/assessor")
@SecuredBySpring(value = "Controller", description = "Only assessors can access the competition if its still open for" +
        " assessment", securedType = AssessorCompetitionDashboardController.class)
@PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionCompositeId', 'ASSESSOR_COMPETITION')")
public class AssessorCompetitionDashboardController {

    private static final String FORM_ATTR_NAME = "form";

    @Autowired
    private AssessorCompetitionDashboardModelPopulator assessorCompetitionDashboardModelPopulator;

    @Autowired
    private AssessmentService assessmentService;

    @GetMapping("/dashboard/competition/{competitionId}")
    public String competitionDashboard(final Model model,
                                       UserResource loggedInUser,
                                       @PathVariable("competitionId") final Long competitionId,
                                       @ModelAttribute(name = FORM_ATTR_NAME, binding = false) AssessorCompetitionDashboardAssessmentForm form) {

        model.addAttribute("model", assessorCompetitionDashboardModelPopulator.populateModel(competitionId, loggedInUser.getId()));
        return "assessor-competition-dashboard";
    }

    @PostMapping("/dashboard/competition/{competitionId}")
    public String submitAssessments(Model model,
                                    @PathVariable("competitionId") Long competitionId,
                                    UserResource loggedInUser,
                                    @ModelAttribute(FORM_ATTR_NAME) @Valid AssessorCompetitionDashboardAssessmentForm form,
                                    @SuppressWarnings("UnusedParameters") BindingResult bindingResult,
                                    ValidationHandler validationHandler) {

        Supplier<String> renderDashboard = () -> competitionDashboard(model, loggedInUser, competitionId, form);

        return validationHandler.failNowOrSucceedWith(
                renderDashboard,
                () -> {
                    ServiceResult<Void> serviceResult = assessmentService.submitAssessments(form.getAssessmentIds());

                    return validationHandler.addAnyErrors(serviceResult, asGlobalErrors())
                            .failNowOrSucceedWith(renderDashboard, renderDashboard);
                }
        );
    }

    @PostMapping("/dashboard/confirm-competition/{competitionId}")
    public String confirmSubmitAssessments(Model model,
                                           @PathVariable("competitionId") final Long competitionId,
                                           UserResource loggedInUser,
                                           @ModelAttribute(FORM_ATTR_NAME) @Valid AssessorCompetitionDashboardAssessmentForm form,
                                           @SuppressWarnings("UnusedParameters") BindingResult bindingResult,
                                           ValidationHandler validationHandler) {


        Supplier<String> renderDashboard = () -> competitionDashboard(model, loggedInUser, competitionId, form);

        return validationHandler.failNowOrSucceedWith(
                renderDashboard,
                () -> {
                    model.addAttribute("competitionId", competitionId);

                    return "assessment/assessment-submit-confirm";
                }
        );
    }
}
