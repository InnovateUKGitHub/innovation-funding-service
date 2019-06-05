package org.innovateuk.ifs.application.forms.questions.team.controller;

import org.innovateuk.ifs.application.forms.questions.team.form.ApplicationTeamOrganisationForm;
import org.innovateuk.ifs.application.forms.questions.team.viewmodel.ApplicationTeamAddOrganisationViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.function.Supplier;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;

@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form/question/{questionId}/team/new-organisation")
public class ApplicationTeamAddOrganisationControllerNew {

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private InviteRestService inviteRestService;


    @GetMapping
    public String addOrganisationForm(@ModelAttribute(value = "form", binding = false) ApplicationTeamOrganisationForm form,
                           BindingResult bindingResult,
                           Model model,
                           @PathVariable long applicationId,
                           @PathVariable long questionId) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        model.addAttribute("model", new ApplicationTeamAddOrganisationViewModel(application, questionId));
        return "application/questions/application-team-organisation";
    }

    @PostMapping
    public String addOrganisation(@Valid @ModelAttribute(value = "form") ApplicationTeamOrganisationForm form,
                           BindingResult bindingResult,
                           ValidationHandler validationHandler,
                           Model model,
                           @PathVariable long applicationId,
                           @PathVariable long questionId) {
        Supplier<String> failureView = () -> addOrganisationForm(form, bindingResult, model, applicationId, questionId);
        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            validationHandler.addAnyErrors(inviteRestService.createInvitesByInviteOrganisation(
                    form.getOrganisationName(), singletonList(new ApplicationInviteResource(form.getName(), form.getEmail(), applicationId))));
            return validationHandler.failNowOrSucceedWith(failureView,
                    () -> redirectToApplicationTeam(applicationId, questionId));

        });
    }

    private String redirectToApplicationTeam(long applicationId, long questionId) {
        return String.format("redirect:/application/%d/form/question/%d/team", applicationId, questionId);
    }
}
