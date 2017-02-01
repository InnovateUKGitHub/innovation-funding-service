package org.innovateuk.ifs.assessment.controller;

import org.innovateuk.ifs.assessment.form.AssessmentOverviewForm;
import org.innovateuk.ifs.assessment.model.AssessmentFinancesSummaryModelPopulator;
import org.innovateuk.ifs.assessment.model.AssessmentOverviewModelPopulator;
import org.innovateuk.ifs.assessment.model.RejectAssessmentModelPopulator;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.assessment.service.AssessmentService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.function.Supplier;

import static java.lang.String.format;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;

@Controller
@RequestMapping(value = "/{assessmentId}")
public class AssessmentOverviewController {

    private static final String FORM_ATTR_NAME = "form";

    @Autowired
    private AssessmentOverviewModelPopulator assessmentOverviewModelPopulator;

    @Autowired
    private AssessmentFinancesSummaryModelPopulator assessmentFinancesSummaryModelPopulator;

    @Autowired
    private RejectAssessmentModelPopulator rejectAssessmentModelPopulator;

    @Autowired
    private AssessmentService assessmentService;

    @RequestMapping(method = RequestMethod.GET)
    public String getOverview(Model model,
                              @ModelAttribute(FORM_ATTR_NAME) AssessmentOverviewForm form,
                              @PathVariable("assessmentId") Long assessmentId,
                              @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        model.addAttribute("model", assessmentOverviewModelPopulator.populateModel(assessmentId));
        return "assessment/application-overview";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/finances")
    public String getFinancesSummary(Model model, @PathVariable("assessmentId") Long assessmentId) {

        assessmentFinancesSummaryModelPopulator.populateModel(assessmentId, model);

        return "assessment/application-finances-summary";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/reject")
    public String rejectInvitation(
            Model model,
            @Valid @ModelAttribute(FORM_ATTR_NAME) AssessmentOverviewForm form,
            @SuppressWarnings("unused") BindingResult bindingResult,
            ValidationHandler validationHandler,
            @PathVariable("assessmentId") Long assessmentId) {
        Supplier<String> failureView = () -> doViewRejectInvitationConfirm(model, assessmentId);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {

            AssessmentResource assessment = assessmentService.getById(assessmentId);
            ServiceResult<Void> updateResult = assessmentService.rejectInvitation(assessment.getId(), form.getRejectReason(), form.getRejectComment());

            return validationHandler.addAnyErrors(updateResult, fieldErrorsToFieldErrors(), asGlobalErrors()).
                    failNowOrSucceedWith(failureView, () -> redirectToAssessorCompetitionDashboard(assessment.getCompetition()));
        });
    }

    @RequestMapping(method = RequestMethod.GET, value = "/reject/confirm")
    public String rejectInvitationConfirm(
            Model model,
            @ModelAttribute(FORM_ATTR_NAME) AssessmentOverviewForm form,
            @PathVariable("assessmentId") Long assessmentId) {
        return doViewRejectInvitationConfirm(model, assessmentId);
    }

    private String doViewRejectInvitationConfirm(Model model, Long assessmentId) {
        model.addAttribute("model", rejectAssessmentModelPopulator.populateModel(assessmentId));
        return "assessment/reject-invitation-confirm";
    }

    private String redirectToAssessorCompetitionDashboard(Long competitionId) {
        return format("redirect:/assessor/dashboard/competition/%s", competitionId);
    }
}
