package org.innovateuk.ifs.assessment.controller;

import org.innovateuk.ifs.BaseController;
import org.innovateuk.ifs.assessment.form.RejectCompetitionForm;
import org.innovateuk.ifs.assessment.model.CompetitionInviteModelPopulator;
import org.innovateuk.ifs.assessment.model.RejectCompetitionModelPopulator;
import org.innovateuk.ifs.assessment.service.CompetitionInviteRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.invite.resource.CompetitionRejectionResource;
import org.innovateuk.ifs.invite.resource.RejectionReasonResource;
import org.innovateuk.ifs.invite.service.RejectionReasonRestService;
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
import java.util.List;
import java.util.function.Supplier;

import static java.lang.String.format;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;

/**
 * Controller to manage Invites to a Competition.
 */
@Controller
public class CompetitionInviteController extends BaseController {

    @Autowired
    private CompetitionInviteRestService inviteRestService;

    @Autowired
    private RejectionReasonRestService rejectionReasonRestService;

    @Autowired
    private CompetitionInviteModelPopulator competitionInviteModelPopulator;

    @Autowired
    private RejectCompetitionModelPopulator rejectCompetitionModelPopulator;

    @RequestMapping(value = "/invite/competition/{inviteHash}", method = RequestMethod.GET)
    public String openInvite(@PathVariable("inviteHash") String inviteHash,
                             @ModelAttribute("form") RejectCompetitionForm form,
                             @ModelAttribute("loggedInUser") UserResource loggedInUser,
                             Model model) {
        boolean userLoggedIn = loggedInUser != null;
        model.addAttribute("model", competitionInviteModelPopulator.populateModel(inviteHash, userLoggedIn));

        return "assessor-competition-invite";
    }

    @RequestMapping(value = "/invite/competition/{inviteHash}/accept", method = RequestMethod.POST)
    public String acceptInvite(@PathVariable("inviteHash") String inviteHash,
                               @ModelAttribute("loggedInUser") UserResource loggedInUser,
                               Model model) {
        boolean userIsLoggedIn = loggedInUser != null;

        if (userIsLoggedIn) {
            return format("redirect:/invite-accept/competition/%s/accept", inviteHash);
        }

        return inviteRestService.checkExistingUser(inviteHash)
                .andOnSuccessReturn(userExists -> {
                    if (userExists) {
                        model.addAttribute("model", competitionInviteModelPopulator.populateModel(inviteHash, false));
                        return "assessor-competition-accept-user-exists-but-not-logged-in";
                    } else {
                        return format("redirect:/registration/%s/start", inviteHash);
                    }
                })
                .getSuccessObject();
    }

    /**
     * Unlike the other endpoints, this requires authentication through Shibboleth.
     * The /invite/ endpoints will not be authenticated and will not trigger a sign in screen.
     */
    @RequestMapping(value = "/invite-accept/competition/{inviteHash}/accept", method = RequestMethod.GET)
    public String confirmAcceptInvite(@PathVariable("inviteHash") String inviteHash) {
        inviteRestService.acceptInvite(inviteHash).getSuccessObjectOrThrowException();
        return "redirect:/assessor/dashboard";
    }

    @RequestMapping(value = "/invite/competition/{inviteHash}/reject", method = RequestMethod.POST)
    public String rejectInvite(Model model,
                               @PathVariable("inviteHash") String inviteHash,
                               @Valid @ModelAttribute("form") RejectCompetitionForm form,
                               @SuppressWarnings("unused") BindingResult bindingResult,
                               ValidationHandler validationHandler) {
        Supplier<String> failureView = () -> doViewRejectInvitationConfirm(model, inviteHash);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {

            RestResult<Void> updateResult = inviteRestService.rejectInvite(inviteHash, new CompetitionRejectionResource(form.getRejectReason(), form.getRejectComment()));

            return validationHandler.addAnyErrors(updateResult, fieldErrorsToFieldErrors(), asGlobalErrors()).
                    failNowOrSucceedWith(failureView, () -> format("redirect:/invite/competition/%s/reject/thank-you", inviteHash));
        });
    }

    @RequestMapping(value = "/invite/competition/{inviteHash}/reject/confirm", method = RequestMethod.GET)
    public String rejectInviteConfirm(Model model,
                                      @ModelAttribute("form") RejectCompetitionForm form,
                                      @PathVariable("inviteHash") String inviteHash) {
        return doViewRejectInvitationConfirm(model, inviteHash);
    }

    @RequestMapping(value = "/invite/competition/{inviteHash}/reject/thank-you", method = RequestMethod.GET)
    public String rejectThankYou(@PathVariable("inviteHash") String inviteHash) {
        return "assessor-competition-reject";
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
