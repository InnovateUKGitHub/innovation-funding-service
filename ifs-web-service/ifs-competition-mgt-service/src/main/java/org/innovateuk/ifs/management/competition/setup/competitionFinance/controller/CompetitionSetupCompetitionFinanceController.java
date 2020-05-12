package org.innovateuk.ifs.management.competition.setup.competitionFinance.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.service.CompetitionSetupExternalFinanceUsersRestService;
import org.innovateuk.ifs.controller.ErrorToObjectErrorConverter;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.invite.resource.InviteUserResource;
import org.innovateuk.ifs.management.competition.setup.competitionFinance.form.InviteCompetitionFinanceForm;
import org.innovateuk.ifs.management.competition.setup.competitionFinance.populator.ManageCompetitionFinanceUsersModelPopulator;
import org.innovateuk.ifs.management.competition.setup.core.service.CompetitionSetupService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.function.Supplier;

import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.*;

@Controller
@RequestMapping("/competition/setup")
@SecuredBySpring(value = "Controller", description = "Controller for managing comp finance users", securedType = CompetitionSetupCompetitionFinanceController.class)
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'ifs_administrator')")
public class CompetitionSetupCompetitionFinanceController {

    private static final String COMPETITION_ID_KEY = "competitionId";

    private static final String MODEL = "model";
    private static final String FORM_ATTR_NAME = "form";
    private static final String DEFAULT_TAB = "add";
    private static final String ADDED_TAB = "added";

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private CompetitionSetupExternalFinanceUsersRestService competitionSetupExternalFinanceUsersRestService;

    @Autowired
    private CompetitionSetupService competitionSetupService;

    @Autowired
    private ManageCompetitionFinanceUsersModelPopulator manageCompetitionFinanceUsersModelPopulator;

    @PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionCompositeId', 'MANAGE_COMP_FINANCE_USERS')")
    @GetMapping("/{competitionId}/manage-finance-users")
    public String manageFinanceUsers(@PathVariable(COMPETITION_ID_KEY) long competitionId,
                                     @RequestParam(value = "tab", defaultValue = DEFAULT_TAB) String tab,
                                     Model model) {

        InviteCompetitionFinanceForm form = new InviteCompetitionFinanceForm();
        return doViewManageCompetitionFinanceUser(competitionId, model, form, tab);
    }

    private String doViewManageCompetitionFinanceUser(long competitionId, Model model, InviteCompetitionFinanceForm form, String tab) {

        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();

        if (!competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competitionId)) {
            return "redirect:/competition/setup/" + competitionId;
        }

        model.addAttribute(MODEL, manageCompetitionFinanceUsersModelPopulator.populateModel(competition, tab));
        model.addAttribute(FORM_ATTR_NAME, form);

        return "competition/setup/manage-finance-users";
    }

    @PostMapping(value = "/{competitionId}/manage-finance-users", params = {"inviteFinanceUser"})
    public String inviteFinanceUsers(@PathVariable(COMPETITION_ID_KEY) long competitionId,
                                    @RequestParam(value = "tab", defaultValue = DEFAULT_TAB) String tab,
                                    Model model,
                                    @Valid @ModelAttribute(FORM_ATTR_NAME) InviteCompetitionFinanceForm form,
                                    @SuppressWarnings("unused") BindingResult bindingResult, ValidationHandler validationHandler) {

        Supplier<String> failureView = () -> doViewManageCompetitionFinanceUser(competitionId, model, form, tab);
        form.setVisible(true);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            InviteUserResource inviteUserResource = constructInviteUserResource(form);
            RestResult<Void> saveResult = competitionSetupExternalFinanceUsersRestService.inviteExternalFinanceUsers(inviteUserResource, competitionId);
            return handleInviteCompetitionFinanceErrors(saveResult, validationHandler, form).
                    failNowOrSucceedWith(failureView, () -> "redirect:/competition/setup/" + competitionId + "/manage-finance-users?tab=" + tab);
        });
    }

    @PostMapping(value = "/{competitionId}/manage-finance-users", params = {"addFinanceUser"})
    public String addFinanceUsers(@PathVariable(COMPETITION_ID_KEY) long competitionId,
                                 @RequestParam("userId") long userId,
                                 Model model) {

        competitionSetupExternalFinanceUsersRestService.addExternalFinanceUsers(competitionId, userId);
        return "redirect:/competition/setup/" + competitionId + "/manage-finance-users?tab=" + DEFAULT_TAB;
    }

    @PostMapping(value = "/{competitionId}/manage-finance-users", params = {"removeFinanceUser"})
    public String removeFinanceUsers(@PathVariable(COMPETITION_ID_KEY) long competitionId,
                                    @RequestParam("userId") long userId,
                                    Model model) {

        competitionSetupExternalFinanceUsersRestService.removeExternalFinanceUsers(competitionId, userId);
        return "redirect:/competition/setup/" + competitionId + "/manage-finance-users?tab=" + ADDED_TAB;
    }

    private ValidationHandler handleInviteCompetitionFinanceErrors(RestResult<Void> saveResult, ValidationHandler validationHandler, InviteCompetitionFinanceForm form) {
        if (saveResult.getErrors().size() > 0) {
            ErrorToObjectErrorConverter error = mappingErrorKeyToField(saveResult.getErrors().get(0).getErrorKey(), "emailAddress");
            return validationHandler.addAnyErrors(saveResult, error, fieldErrorsToFieldErrors(), asGlobalErrors());
        } else {
            return validationHandler;
        }
    }

    private InviteUserResource constructInviteUserResource(InviteCompetitionFinanceForm form) {
        UserResource invitedUser = new UserResource();
        invitedUser.setFirstName(form.getFirstName());
        invitedUser.setLastName(form.getLastName());
        invitedUser.setEmail(form.getEmailAddress());
        return new InviteUserResource(invitedUser);
    }
}