package org.innovateuk.ifs.competitionsetup.stakeholder.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.service.CompetitionSetupStakeholderRestService;
import org.innovateuk.ifs.competitionsetup.core.service.CompetitionSetupService;
import org.innovateuk.ifs.competitionsetup.stakeholder.form.InviteStakeholderForm;
import org.innovateuk.ifs.competitionsetup.stakeholder.populator.ManageStakeholderModelPopulator;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.invite.resource.InviteUserResource;
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
import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.STAKEHOLDERS_CANNOT_BE_INTERNAL_USERS;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.STAKEHOLDER_INVITE_INVALID_EMAIL;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.*;

/**
 * Controller for managing stakeholders
 */
@Controller
@RequestMapping("/competition/setup")
@SecuredBySpring(value = "Controller", description = "Controller for managing stakeholders", securedType = CompetitionSetupStakeholderController.class)
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'ifs_administrator')")
public class CompetitionSetupStakeholderController {
    private static final String COMPETITION_ID_KEY = "competitionId";

    private static final String MODEL = "model";
    private static final String FORM_ATTR_NAME = "form";
    private static final String DEFAULT_TAB = "add";
    private static final String ADDED_TAB = "added";

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private CompetitionSetupStakeholderRestService competitionSetupStakeholderRestService;

    @Autowired
    private CompetitionSetupService competitionSetupService;

    @Autowired
    private ManageStakeholderModelPopulator manageStakeholderModelPopulator;

    @PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionCompositeId', 'MANAGE_STAKEHOLDERS')")
    @GetMapping("/{competitionId}/manage-stakeholders")
    public String manageStakeholders(@PathVariable(COMPETITION_ID_KEY) long competitionId,
                                     @RequestParam(value = "tab", defaultValue = DEFAULT_TAB) String tab,
                                     Model model) {

        InviteStakeholderForm form = new InviteStakeholderForm();
        return doViewManageStakeholders(competitionId, model, form, tab);
    }

    private String doViewManageStakeholders(long competitionId, Model model, InviteStakeholderForm form, String tab) {

        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();

        if (!competitionSetupService.isInitialDetailsCompleteOrTouched(competitionId)) {
            return "redirect:/competition/setup/" + competitionId;
        }

        model.addAttribute(MODEL, manageStakeholderModelPopulator.populateModel(competition, tab));
        model.addAttribute(FORM_ATTR_NAME, form);

        return "competition/setup/manage-stakeholders";
    }

    @PostMapping(value = "/{competitionId}/manage-stakeholders", params = {"inviteStakeholder"})
    public String inviteStakeholder(@PathVariable(COMPETITION_ID_KEY) long competitionId,
                                    @RequestParam(value = "tab", defaultValue = DEFAULT_TAB) String tab,
                                    Model model,
                                    @Valid @ModelAttribute(FORM_ATTR_NAME) InviteStakeholderForm form,
                                    @SuppressWarnings("unused") BindingResult bindingResult, ValidationHandler validationHandler) {

        Supplier<String> failureView = () -> doViewManageStakeholders(competitionId, model, form, tab);
        form.setVisible(true);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            InviteUserResource inviteUserResource = constructInviteUserResource(form);
            RestResult<Void> saveResult = competitionSetupStakeholderRestService.inviteStakeholder(inviteUserResource, competitionId);
            return handleInviteStakeholderErrors(saveResult, validationHandler).
                    failNowOrSucceedWith(failureView, () -> "redirect:/competition/setup/" + competitionId + "/manage-stakeholders?tab=" + tab);
        });
    }

    @PostMapping(value = "/{competitionId}/manage-stakeholders", params = {"addStakeholder"})
    public String addStakeholder(@PathVariable(COMPETITION_ID_KEY) long competitionId,
                                 @RequestParam("stakeholderUserId") long stakeholderUserId,
                                 Model model) {

        competitionSetupStakeholderRestService.addStakeholder(competitionId, stakeholderUserId);
        return "redirect:/competition/setup/" + competitionId + "/manage-stakeholders?tab=" + DEFAULT_TAB;
    }

    @PostMapping(value = "/{competitionId}/manage-stakeholders", params = {"removeStakeholder"})
    public String removeStakeholder(@PathVariable(COMPETITION_ID_KEY) long competitionId,
                                    @RequestParam("stakeholderUserId") long stakeholderUserId,
                                    Model model) {

        competitionSetupStakeholderRestService.removeStakeholder(competitionId, stakeholderUserId);
        return "redirect:/competition/setup/" + competitionId + "/manage-stakeholders?tab=" + ADDED_TAB;
    }

    private boolean isUserAlreadyStakeholderOnCompetition(long competitionId, UserResource userResource) {
        List<UserResource> competitionStakeholders = competitionSetupStakeholderRestService.findStakeholders(competitionId).getSuccess();
        return competitionStakeholders.contains(userResource);
    }

    private ValidationHandler handleInviteStakeholderErrors(RestResult<Void> saveResult, ValidationHandler validationHandler) {
        return validationHandler.addAnyErrors(saveResult, mappingErrorKeyToField(STAKEHOLDER_INVITE_INVALID_EMAIL, "emailAddress"), fieldErrorsToFieldErrors(), asGlobalErrors());
    }

    private InviteUserResource constructInviteUserResource(InviteStakeholderForm form) {
        UserResource invitedUser = new UserResource();
        invitedUser.setFirstName(form.getFirstName());
        invitedUser.setLastName(form.getLastName());
        invitedUser.setEmail(form.getEmailAddress());
        return new InviteUserResource(invitedUser);
    }
}
