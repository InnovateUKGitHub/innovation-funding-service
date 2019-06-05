package org.innovateuk.ifs.application.forms.questions.team.controller;


import org.innovateuk.ifs.application.forms.questions.grantagreement.controller.GrantAgreementController;
import org.innovateuk.ifs.application.forms.questions.team.form.ApplicationTeamForm;
import org.innovateuk.ifs.application.forms.questions.team.populator.ApplicationTeamPopulator;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;

@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form/question/{questionId}/team")
@SecuredBySpring(value = "Controller", description = "Only applicants can edit their application team", securedType = GrantAgreementController.class)
@PreAuthorize("hasAuthority('applicant')")
public class ApplicationTeamController {

    @Autowired
    private ApplicationTeamPopulator applicationTeamPopulator;

    @Autowired
    private InviteRestService inviteRestService;

    @GetMapping
    public String viewTeam(@ModelAttribute(value = "form", binding = false) ApplicationTeamForm form,
                           BindingResult bindingResult,
                           Model model,
                           @PathVariable long applicationId,
                           @PathVariable long questionId,
                           UserResource userResource) {
        model.addAttribute("model", applicationTeamPopulator.populate(applicationId, questionId, userResource));
        return "application/questions/application-team";
    }

    @PostMapping(params = "remove-team-member")
    public String removeUser(@PathVariable long applicationId,
                             @PathVariable long questionId,
                             @RequestParam("remove-team-member") final long inviteId) {
        inviteRestService.removeApplicationInvite(inviteId).getSuccess();
        return redirectToApplicationTeam(applicationId, questionId);
    }

    @PostMapping(params = "remove-invite")
    public String removeInvite(@PathVariable long applicationId,
                               @PathVariable long questionId,
                               @RequestParam("remove-invite") final long inviteId) {
        inviteRestService.removeApplicationInvite(inviteId).getSuccess();
        return redirectToApplicationTeam(applicationId, questionId);
    }

    @PostMapping(params = "add-team-member")
    public String openAddTeamMemberForm(@ModelAttribute(value = "form", binding = false) ApplicationTeamForm form,
                                        BindingResult bindingResult,
                                        @PathVariable long applicationId,
                                        @PathVariable long questionId,
                                        @RequestParam("add-team-member") final long organisationId,
                                        Model model,
                                        UserResource userResource) {
        model.addAttribute("model", applicationTeamPopulator.populate(applicationId, questionId, userResource)
                .openAddTeamMemberForm(organisationId));
        return "application/questions/application-team";
    }

    @PostMapping(params = "close-add-team-member-form")
    public String closeAddTeamMemberForm(@PathVariable long applicationId,
                                         @PathVariable long questionId) {
        return redirectToApplicationTeam(applicationId, questionId);
    }

    @PostMapping(params = "invite-to-organisation")
    public String inviteToOrganisation(@Valid @ModelAttribute("form") ApplicationTeamForm form,
                                       BindingResult bindingResult,
                                       ValidationHandler validationHandler,
                                       @PathVariable long applicationId,
                                       @PathVariable long questionId,
                                       @RequestParam("invite-to-organisation") final long organisationId,
                                       Model model,
                                       UserResource user) {
        return inviteToOrganisation(form, validationHandler, applicationId, questionId, organisationId, model, user,
                invite -> inviteRestService.saveInvites(singletonList(invite))
        );
    }

    @PostMapping(params = "invite-to-existing-organisation")
    public String inviteToExistingOrganisation(@Valid @ModelAttribute("form") ApplicationTeamForm form,
                                               BindingResult bindingResult,
                                               ValidationHandler validationHandler,
                                               @PathVariable long applicationId,
                                               @PathVariable long questionId,
                                               @RequestParam("invite-to-existing-organisation") final long organisationId,
                                               Model model,
                                               UserResource user) {
        return inviteToOrganisation(form, validationHandler, applicationId, questionId, organisationId, model, user,
                invite -> inviteRestService.createInvitesByOrganisationForApplication(applicationId, organisationId, singletonList(invite))
        );
    }

    private String inviteToOrganisation(ApplicationTeamForm form,
                                        ValidationHandler validationHandler,
                                        long applicationId,
                                        long questionId,
                                        long organisationId,
                                        Model model,
                                        UserResource user,
                                        Function<ApplicationInviteResource, RestResult<Void>> inviteAction) {


        Supplier<String> failureView = () -> {
            model.addAttribute("model", applicationTeamPopulator.populate(applicationId, questionId, user)
                    .openAddTeamMemberForm(organisationId));
            return "application/questions/application-team";
        };

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ApplicationInviteResource invite = new ApplicationInviteResource(
                    form.getName(),
                    form.getEmail(),
                    applicationId
            );
            invite.setInviteOrganisation(organisationId);
            validationHandler.addAnyErrors(inviteAction.apply(invite));
            return validationHandler.failNowOrSucceedWith(failureView, () -> redirectToApplicationTeam(applicationId, questionId));
        });
    }

    private String redirectToApplicationTeam(long applicationId, long questionId) {
        return String.format("redirect:/application/%d/form/question/%d/team", applicationId, questionId);
    }

}
