package org.innovateuk.ifs.assessment.invite.controller;

import org.innovateuk.ifs.assessment.invite.form.InterviewInviteForm;
import org.innovateuk.ifs.assessment.invite.populator.InterviewInviteModelPopulator;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.interview.service.InterviewInviteRestService;
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
 * Controller to manage Invites to an Assessment interview Panel.
 */
@Controller
@SecuredBySpring(value = "Controller", description = "Assessors can handle the decision to attend an interview panel", securedType = InterviewInviteController.class)
@PreAuthorize("permitAll")
public class InterviewInviteController {

    @Autowired
    private InterviewInviteRestService inviteRestService;

    @Autowired
    private RejectionReasonRestService rejectionReasonRestService;

    @Autowired
    private InterviewInviteModelPopulator interviewInviteModelPopulator;

    @GetMapping("/invite/interview/{inviteHash}")
    public String openInvite(@PathVariable("inviteHash") String inviteHash,
                             @ModelAttribute(name = "form", binding = false) InterviewInviteForm form,
                             UserResource loggedInUser,
                             Model model) {
        boolean userLoggedIn = loggedInUser != null;
        model.addAttribute("model", interviewInviteModelPopulator.populateModel(inviteHash, userLoggedIn));

        return "assessor-interview-invite";
    }

    @PostMapping("/invite/interview/{inviteHash}/decision")
    public String handleDecision(Model model,
                                 @PathVariable("inviteHash") String inviteHash,
                                 UserResource loggedInUser,
                                 @Valid @ModelAttribute("form") InterviewInviteForm form,
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

    private String doAcceptInvite(String inviteHash, UserResource loggedInUser, Model model) {
        boolean userIsLoggedIn = loggedInUser != null;

        if (userIsLoggedIn) {
            return format("redirect:/invite-accept/interview/%s/accept", inviteHash);
        }

        return inviteRestService.checkExistingUser(inviteHash)
                .andOnSuccessReturn(userExists -> {
                    if (userExists) {
                        model.addAttribute("model", interviewInviteModelPopulator.populateModel(inviteHash, false));
                        return "assessor-interview-accept-user-exists-but-not-logged-in";
                    } else {
                        return format("redirect:/registration/%s/start", inviteHash);
                    }
                })
                .getSuccess();
    }

    /**
     * Unlike the other endpoints, this requires authentication through Shibboleth.
     * The /invite/ endpoints will not be authenticated and will not trigger a sign in screen.
     */
    @GetMapping("/invite-accept/interview/{inviteHash}/accept")
    @SecuredBySpring(value = "TODO", description = "TODO")
    @PreAuthorize("hasAuthority('assessor')")
    public String confirmAcceptInvite(@PathVariable("inviteHash") String inviteHash) {
        inviteRestService.acceptInvite(inviteHash).getSuccess();
        return "redirect:/assessor/dashboard";
    }

    private String doRejectInvite(Model model,
                                  String inviteHash,
                                  InterviewInviteForm form,
                                  UserResource loggedInUser,
                                  ValidationHandler validationHandler) {
        Supplier<String> failureView = () -> openInvite(inviteHash, form, loggedInUser, model);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {

            RestResult<Void> updateResult = inviteRestService.rejectInvite(inviteHash);

            return validationHandler.addAnyErrors(updateResult, fieldErrorsToFieldErrors(), asGlobalErrors()).
                    failNowOrSucceedWith(failureView, () -> format("redirect:/invite/interview/%s/reject/thank-you", inviteHash));
        });
    }

    @GetMapping("/invite/interview/{inviteHash}/reject/thank-you")
    public String rejectThankYou(@PathVariable("inviteHash") String inviteHash) {
        return "assessor-interview-reject";
    }

    @ModelAttribute("rejectionReasons")
    public List<RejectionReasonResource> populateRejectionReasons() {
        return rejectionReasonRestService.findAllActive().getSuccess();
    }
}
