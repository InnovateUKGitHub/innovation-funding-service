package org.innovateuk.ifs.application.forms.questions.team.controller;


import org.innovateuk.ifs.application.forms.questions.team.form.ApplicationKtaForm;
import org.innovateuk.ifs.application.forms.questions.team.form.ApplicationTeamForm;
import org.innovateuk.ifs.application.forms.questions.team.populator.ApplicationTeamPopulator;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ErrorToObjectErrorConverter;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.*;

@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form/question/{questionId}/team")
@SecuredBySpring(value = "Controller", description = "Only applicants can edit their application team", securedType = ApplicationTeamController.class)
@PreAuthorize("hasAuthority('applicant')")
public class ApplicationTeamController {

    @Autowired
    private ApplicationTeamPopulator applicationTeamPopulator;

    @Autowired
    private InviteRestService inviteRestService;

    @Autowired
    private QuestionStatusRestService questionStatusRestService;

    @Autowired
    private UserRestService userRestService;

    @GetMapping
    public String viewTeam(@ModelAttribute(value = "form", binding = false) ApplicationTeamForm form,
                           @ModelAttribute(value = "ktaForm", binding = false) ApplicationKtaForm ktaForm,
                           BindingResult bindingResult,
                           Model model,
                           @PathVariable long applicationId,
                           @PathVariable long questionId,
                           UserResource user) {
        model.addAttribute("model", applicationTeamPopulator.populate(applicationId, questionId, user));
        return "application/questions/application-team";
    }

    @GetMapping(params = "show-errors")
    public String showErrors(@ModelAttribute(value = "form") ApplicationTeamForm form,
                             @ModelAttribute(value = "ktaForm") ApplicationKtaForm ktaForm,
                                         BindingResult bindingResult,
                                         ValidationHandler validationHandler,
                                         Model model,
                                         @PathVariable long applicationId,
                                         @PathVariable long questionId,
                                         UserResource user) {
        return markAsComplete(form, ktaForm, bindingResult, validationHandler, model, applicationId, questionId, user);
    }

    @PostMapping(params = "invite-kta")
    public String addKta(@ModelAttribute(value = "ktaForm") ApplicationKtaForm ktaForm,
                         BindingResult bindingResult,
                         ValidationHandler validationHandler,
                         Model model,
                         @PathVariable long applicationId,
                         @PathVariable long questionId,
                         UserResource user) {
        int x = 2;
        return redirectToApplicationTeam(applicationId, questionId);
    }

    @PostMapping(params = "complete")
    public String markAsComplete(@ModelAttribute(value = "form") ApplicationTeamForm form,
                                 @ModelAttribute(value = "ktaForm") ApplicationKtaForm ktaForm,
                                 BindingResult bindingResult,
                                 ValidationHandler validationHandler,
                                 Model model,
                                 @PathVariable long applicationId,
                                 @PathVariable long questionId,
                                 UserResource user) {
        List<ValidationMessages> validationMessages = questionStatusRestService.markAsComplete(questionId, applicationId, processRoleId(user.getId(), applicationId)).getSuccess();
        validationMessages.forEach(messages -> validationHandler.addAnyErrors(messages,
                mapTeamCompletionError(),
                defaultConverters()));
        return validationHandler.failNowOrSucceedWith(() -> {
                    questionStatusRestService.markAsInComplete(questionId, applicationId, processRoleId(user.getId(), applicationId)).getSuccess();
                    return viewTeam(form, ktaForm, bindingResult, model, applicationId, questionId, user);
                },
                () -> redirectToApplicationTeam(applicationId, questionId));
    }

    private ErrorToObjectErrorConverter mapTeamCompletionError() {
        return  e -> {
            if ("validation.applicationteam.pending.invites".equals(e.getErrorKey())) {
                return Optional.of(newFieldError(e, "organisation." + e.getArguments().get(1), e.getFieldRejectedValue()));
            }
            return Optional.empty();
        };
    }

    @PostMapping(params = "edit")
    public String edit(@PathVariable long applicationId,
                       @PathVariable long questionId,
                       UserResource user) {
        questionStatusRestService.markAsInComplete(questionId, applicationId, processRoleId(user.getId(), applicationId)).getSuccess();
        return redirectToApplicationTeam(applicationId, questionId);
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

    @PostMapping(params = "resend-invite")
    public String resendInvite(@PathVariable long applicationId,
                               @PathVariable long questionId,
                               @RequestParam("resend-invite") final long inviteId) {
        resendApplicationInvite(inviteId, applicationId);
        return redirectToApplicationTeam(applicationId, questionId);
    }

    private void resendApplicationInvite(long inviteId, long applicationId){
        List<InviteOrganisationResource> inviteOrganisationResources = inviteRestService.getInvitesByApplication(applicationId).getSuccess();
        inviteOrganisationResources.stream()
                .map(InviteOrganisationResource::getInviteResources)
                .flatMap(List::stream)
                .filter(applicationInvite -> applicationInvite.getId().equals(inviteId))
                .findFirst()
                .ifPresent(invite -> inviteRestService.resendInvite(invite));
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
            validationHandler.addAnyErrors(inviteAction.apply(invite),
                    mappingErrorKeyToField("email.already.in.invite", "email"),
                    defaultConverters());
            return validationHandler.failNowOrSucceedWith(failureView, () -> redirectToApplicationTeam(applicationId, questionId));
        });
    }

    private String redirectToApplicationTeam(long applicationId, long questionId) {
        return String.format("redirect:/application/%d/form/question/%d/team", applicationId, questionId);
    }

    private long processRoleId(long userId, long applicationId) {
        return userRestService.findProcessRole(userId, applicationId).getSuccess().getId();
    }

}
