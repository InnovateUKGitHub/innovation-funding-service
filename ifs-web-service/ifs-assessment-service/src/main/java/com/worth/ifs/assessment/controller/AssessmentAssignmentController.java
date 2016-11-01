package com.worth.ifs.assessment.controller;

import com.worth.ifs.BaseController;
import com.worth.ifs.assessment.form.AssessmentAssignmentForm;
import com.worth.ifs.assessment.form.RejectCompetitionForm;
import com.worth.ifs.assessment.model.AssessmentAssignmentModelPopulator;
import com.worth.ifs.assessment.model.CompetitionInviteModelPopulator;
import com.worth.ifs.assessment.model.RejectAssessmentModelPopulator;
import com.worth.ifs.assessment.model.RejectCompetitionModelPopulator;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.assessment.service.AssessmentService;
import com.worth.ifs.assessment.service.CompetitionInviteRestService;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.controller.ValidationHandler;
import com.worth.ifs.invite.resource.CompetitionRejectionResource;
import com.worth.ifs.invite.resource.RejectionReasonResource;
import com.worth.ifs.invite.service.RejectionReasonRestService;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.List;
import java.util.function.Supplier;

import static com.worth.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static com.worth.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;
import static java.lang.String.format;

/**
 * Controller to manage assignment of Applications
 */
@Controller
@RequestMapping(value = "/{assessmentId}")
public class AssessmentAssignmentController extends BaseController {

    @Autowired
    private CompetitionInviteRestService inviteRestService;

    @Autowired
    private RejectionReasonRestService rejectionReasonRestService;

    @Autowired
    private AssessmentAssignmentModelPopulator assessmentAssignmentModelPopulator;

    @Autowired
    private RejectAssessmentModelPopulator rejectAssessmentModelPopulator;

    @Autowired
    private AssessmentService assessmentService;


    @RequestMapping(value = "assignment", method = RequestMethod.GET)
    public String viewAssignment(@PathVariable("assessmentId") Long assessmentId,
                                 @ModelAttribute("form") AssessmentAssignmentForm form,
                                 Model model) {
        model.addAttribute("model", assessmentAssignmentModelPopulator.populateModel(assessmentId));
        return "assessment/assessment-invitation";
    }

    @RequestMapping(value = "assignment/accept", method = RequestMethod.POST)
    public String acceptAssignment(@PathVariable("assessmentId") Long assessmentId,
                               @ModelAttribute("loggedInUser") UserResource loggedInUser,
                               Model model) {
        assessmentService.acceptInvitation(assessmentId).getSuccessObjectOrThrowException();
        AssessmentResource assessment = assessmentService.getById(assessmentId);
        return redirectToAssessorCompetitionDashboard(assessment.getCompetition());
    }

    @RequestMapping(value = "assignment/reject", method = RequestMethod.POST)
    public String rejectAssignment(Model model,
                                   @PathVariable("assessmentId") Long assessmentId,
                                   @Valid @ModelAttribute("form") AssessmentAssignmentForm form,
                                   @SuppressWarnings("unused") BindingResult bindingResult,
                                   ValidationHandler validationHandler) {
        Supplier<String> failureView = () -> doViewRejectAssignmentConfirm(model, assessmentId);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            AssessmentResource assessment = assessmentService.getById(assessmentId);
            ServiceResult<Void> updateResult = assessmentService.rejectInvitation(assessment.getId(), form.getRejectReason(), form.getRejectComment());

            return validationHandler.addAnyErrors(updateResult, fieldErrorsToFieldErrors(), asGlobalErrors()).
                    failNowOrSucceedWith(failureView, () -> redirectToAssessorCompetitionDashboard(assessment.getCompetition()));
        });
    }

    @RequestMapping(value = "assignment/reject/confirm", method = RequestMethod.GET)
    public String rejectAssignmentConfirm(Model model,
                                          @ModelAttribute("form") AssessmentAssignmentForm form,
                                          @PathVariable("assessmentId") Long assessmentId) {
        return doViewRejectAssignmentConfirm(model, assessmentId);
    }

    private String doViewRejectAssignmentConfirm(Model model, Long assessmentId) {
        model.addAttribute("model", rejectAssessmentModelPopulator.populateModel(assessmentId));
        return "assessment/assessment-reject-confirm";
    }

    private String redirectToAssessorCompetitionDashboard(Long competitionId) {
        return format("redirect:/assessor/dashboard/competition/%s", competitionId);
    }
}
