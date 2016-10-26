package com.worth.ifs.assessment.controller;

import com.worth.ifs.BaseController;
import com.worth.ifs.assessment.form.RejectCompetitionForm;
import com.worth.ifs.assessment.model.CompetitionInviteModelPopulator;
import com.worth.ifs.assessment.model.RejectCompetitionModelPopulator;
import com.worth.ifs.assessment.service.CompetitionInviteRestService;
import com.worth.ifs.commons.rest.RestResult;
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
    private CompetitionInviteModelPopulator competitionInviteModelPopulator;

    @Autowired
    private RejectCompetitionModelPopulator rejectCompetitionModelPopulator;

    @RequestMapping(value = "assignment", method = RequestMethod.GET)
    public String viewAssignment(@PathVariable("id") String id,
                                 @ModelAttribute("form") RejectCompetitionForm form,
                                 @PathVariable("assessmentId") Long assessmentId,
                                 Model model) {
       // model.addAttribute("model", competitionInviteModelPopulator.populateModel(inviteHash));
        return "assessment/assessment-invitation";
    }

    @RequestMapping(value = "assignment/accept", method = RequestMethod.POST)
    public String acceptAssignment(@PathVariable("id") String id,
                               @ModelAttribute("loggedInUser") UserResource loggedInUser,
                               Model model) {
        return format("redirect:/assign-accept/application/%s/accept", id);

    }

    @RequestMapping(value = "assignment/reject", method = RequestMethod.POST)
    public String rejectAssignment(Model model,
                               @PathVariable("id") String id,
                               @Valid @ModelAttribute("form") RejectCompetitionForm form,
                               @SuppressWarnings("unused") BindingResult bindingResult,
                               ValidationHandler validationHandler) {
        Supplier<String> failureView = () -> doViewRejectAssignmentConfirm(model, id);
        return "assessment/assessment-invitation";

    }

    @RequestMapping(value = "assignment/reject/confirm", method = RequestMethod.GET)
    public String rejectAssignmentConfirm(Model model,
                                      @ModelAttribute("form") RejectCompetitionForm form,
                                      @PathVariable("id") String id) {
        return doViewRejectAssignmentConfirm(model, id);
    }

    @RequestMapping(value = "assignment/reject/thank-you", method = RequestMethod.GET)
    public String rejectThankYou(@PathVariable("inviteHash") String inviteHash) {
        return "assessor-competition-reject";
    }

    @ModelAttribute("rejectionReasons")
    public List<RejectionReasonResource> populateRejectionReasons() {
        return rejectionReasonRestService.findAllActive().getSuccessObjectOrThrowException();
    }

    private String doViewRejectAssignmentConfirm(Model model, String inviteHash) {
        model.addAttribute("model", rejectCompetitionModelPopulator.populateModel(inviteHash));
        return "assignment/reject-invitation-confirm";
    }


}
