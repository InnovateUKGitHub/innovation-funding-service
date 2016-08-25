package com.worth.ifs.assessment.controller;

import com.worth.ifs.BaseController;
import com.worth.ifs.assessment.form.RejectCompetitionForm;
import com.worth.ifs.assessment.model.CompetitionInviteModelPopulator;
import com.worth.ifs.assessment.model.RejectCompetitionModelPopulator;
import com.worth.ifs.assessment.resource.CompetitionRejectionReasonResource;
import com.worth.ifs.assessment.service.CompetitionInviteRestService;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.controller.ValidationHandler;
import com.worth.ifs.invite.resource.RejectionReasonResource;
import com.worth.ifs.invite.service.RejectionReasonRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.function.Supplier;

import static com.worth.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static com.worth.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;

/**
 * Controller to manage Invites to a Competition.
 */
@Controller
@RequestMapping("/invite")
public class CompetitionInviteController extends BaseController {

    @Autowired
    private CompetitionInviteRestService inviteRestService;

    @Autowired
    private RejectionReasonRestService rejectionReasonRestService;

    @Autowired
    private CompetitionInviteModelPopulator competitionInviteModelPopulator;

    @Autowired
    private RejectCompetitionModelPopulator rejectCompetitionModelPopulator;

    @RequestMapping(value = "competition/{inviteHash}", method = RequestMethod.GET)
    public String openInvite(@PathVariable("inviteHash") String inviteHash, Model model) {
        model.addAttribute("model", competitionInviteModelPopulator.populateModel(inviteHash));
        return "assessor-competition-invite";
    }

    @RequestMapping(value = "competition/{inviteHash}/accept", method = RequestMethod.POST)
    public String acceptInvite(@PathVariable("inviteHash") String inviteHash, HttpServletResponse response,
                               HttpServletRequest request) {
        inviteRestService.acceptInvite(inviteHash);
        return "";
    }

    @RequestMapping(value = "competition/{inviteHash}/reject", method = RequestMethod.POST)
    public String rejectInvite(Model model,
                               @PathVariable("inviteHash") String inviteHash,
                               @Valid @ModelAttribute("form") RejectCompetitionForm form,
                               @SuppressWarnings("unused") BindingResult bindingResult,
                               ValidationHandler validationHandler) {
        Supplier<String> failureView = () -> doViewRejectInvitationConfirm(model, inviteHash);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {

            // TODO replace CompetitionRejectionReasonResource with RejectionReasonResource
            // TODO this needs rejection comment from the form
            RestResult<Void> updateResult = inviteRestService.rejectInvite(inviteHash, new CompetitionRejectionReasonResource(form.getRejectReason().getId()));

            // TODO should the succeed be a redirect, e.g. GET competition/reject/thank-you instead?
            return validationHandler.addAnyErrors(updateResult, fieldErrorsToFieldErrors(), asGlobalErrors()).
                    failNowOrSucceedWith(failureView, () -> "assessor-competition-reject");
        });
    }

    @RequestMapping(value = "competition/{inviteHash}/reject/confirm", method = RequestMethod.GET)
    public String rejectInviteConfirm(Model model,
                                      @ModelAttribute("form") RejectCompetitionForm form,
                                      @PathVariable("inviteHash") String inviteHash) {
        return doViewRejectInvitationConfirm(model, inviteHash);
    }

    @ModelAttribute("rejectionReasons")
    public List<RejectionReasonResource> populateRejectionReasons() {
        return rejectionReasonRestService.findAllActive().getSuccessObjectOrThrowException();
    }

    private String doViewRejectInvitationConfirm(Model model, String inviteHash) {
        model.addAttribute("model", rejectCompetitionModelPopulator.populateModel(inviteHash));
        return "assessor-competition-reject-confirm";
    }
}
