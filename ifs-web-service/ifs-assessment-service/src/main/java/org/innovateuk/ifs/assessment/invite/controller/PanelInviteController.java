package org.innovateuk.ifs.assessment.invite.controller;

import org.innovateuk.ifs.assessment.invite.form.PanelInviteForm;
import org.innovateuk.ifs.assessment.invite.populator.PanelInviteModelPopulator;
import org.innovateuk.ifs.assessment.invite.populator.RejectCompetitionModelPopulator;
import org.innovateuk.ifs.assessment.service.AssessmentPanelInviteRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.invite.resource.CompetitionRejectionResource;
import org.innovateuk.ifs.invite.resource.RejectionReasonResource;
import org.innovateuk.ifs.invite.service.RejectionReasonRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;
import java.util.function.Supplier;

import static java.lang.String.format;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;

/**
 * Controller to manage Invites to an Assessment Panel.
 */
@Controller
@PreAuthorize("permitAll")
public class PanelInviteController {

    @Autowired
    private AssessmentPanelInviteRestService inviteRestService;

    @Autowired
    private RejectionReasonRestService rejectionReasonRestService;

    @Autowired
    private PanelInviteModelPopulator panelInviteModelPopulator;

    @Autowired
    private RejectCompetitionModelPopulator rejectCompetitionModelPopulator;

    @GetMapping("/invite/panel/{inviteHash}")
    public String openInvite(@PathVariable("inviteHash") String inviteHash,
                             @ModelAttribute(name = "form", binding = false) PanelInviteForm form,
                             UserResource loggedInUser,
                             Model model) {
        boolean userLoggedIn = loggedInUser != null;
        model.addAttribute("model", panelInviteModelPopulator.populateModel(inviteHash, userLoggedIn));

        return "assessor-panel-invite";
    }

    @PostMapping("/invite/panel/{inviteHash}/decision")
    public String handleDecision(Model model,
                                 @PathVariable("inviteHash") String inviteHash,
                                 UserResource loggedInUser,
                                 @Valid @ModelAttribute("form") PanelInviteForm form,
                                 BindingResult bindingResult,
                                 ValidationHandler validationHandler) {

        Supplier<String> failureView = () -> openInvite(inviteHash, form, loggedInUser, model);
        return validationHandler.failNowOrSucceedWith(failureView,  () -> {
            if (form.getAcceptInvitation()) {
                return doAcceptInvite(inviteHash, loggedInUser, model);
            } else {
                return doRejectInvite(model, inviteHash, form, loggedInUser, validationHandler);
            }
        });
    }

    private String doAcceptInvite(String inviteHash,UserResource loggedInUser, Model model) {
        boolean userIsLoggedIn = loggedInUser != null;

        if (userIsLoggedIn) {
            return format("redirect:/invite-accept/panel/%s/accept", inviteHash);
        }

        return inviteRestService.checkExistingUser(inviteHash)
                .andOnSuccessReturn(userExists -> {
                    if (userExists) {
                        model.addAttribute("model", panelInviteModelPopulator.populateModel(inviteHash, false));
                        return "assessor-panel-accept-user-exists-but-not-logged-in";
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
    @GetMapping("/invite-accept/panel/{inviteHash}/accept")
    @PreAuthorize("hasAuthority('assessor')")
    public String confirmAcceptInvite(@PathVariable("inviteHash") String inviteHash) {
        inviteRestService.acceptInvite(inviteHash).getSuccessObjectOrThrowException();
        return "redirect:/assessor/dashboard";
    }

    private String doRejectInvite(Model model,
                                  String inviteHash,
                                  PanelInviteForm form,
                                  UserResource loggedInUser,
                                  ValidationHandler validationHandler) {
        Supplier<String> failureView = () -> openInvite(inviteHash, form, loggedInUser, model);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {

            RestResult<Void> updateResult = inviteRestService.rejectInvite(inviteHash, new CompetitionRejectionResource(form.getRejectReason(), form.getRejectComment()));

            return validationHandler.addAnyErrors(updateResult, fieldErrorsToFieldErrors(), asGlobalErrors()).
                    failNowOrSucceedWith(failureView, () -> format("redirect:/invite/panel/%s/reject/thank-you", inviteHash));
        });
    }

    @GetMapping("/invite/panel/{inviteHash}/reject/thank-you")
    public String rejectThankYou(@PathVariable("inviteHash") String inviteHash) {
        return "assessor-panel-reject";
    }

    @ModelAttribute("rejectionReasons")
    public List<RejectionReasonResource> populateRejectionReasons() {
        return rejectionReasonRestService.findAllActive().getSuccessObjectOrThrowException();
    }
}
