package org.innovateuk.ifs.registration.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.service.CompetitionSetupStakeholderRestService;
import org.innovateuk.ifs.invite.resource.StakeholderInviteResource;
import org.innovateuk.ifs.registration.form.StakeholderRegistrationForm;
import org.innovateuk.ifs.registration.populator.StakeholderRegistrationModelPopulator;
import org.innovateuk.ifs.registration.viewmodel.StakeholderRegistrationViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/stakeholder")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = StakeholderRegistrationController.class)
@PreAuthorize("permitAll")
public class StakeholderRegistrationController {

    private final StakeholderRegistrationModelPopulator stakeholderRegistrationModelPopulator;

    private final CompetitionSetupStakeholderRestService competitionSetupStakeholderRestService;

    @Autowired
    public StakeholderRegistrationController(StakeholderRegistrationModelPopulator stakeholderRegistrationModelPopulator, CompetitionSetupStakeholderRestService competitionSetupStakeholderRestService) {
        this.stakeholderRegistrationModelPopulator = stakeholderRegistrationModelPopulator;
        this.competitionSetupStakeholderRestService = competitionSetupStakeholderRestService;
    }


    @GetMapping("/{inviteHash}/register")
    public String createAccount(@PathVariable("inviteHash") String inviteHash, Model model, @ModelAttribute("form") StakeholderRegistrationForm stakeholderRegistrationForm) {
        StakeholderInviteResource stakeholderInviteResource = competitionSetupStakeholderRestService.getInvite(inviteHash).getSuccess();
        model.addAttribute("model", stakeholderRegistrationModelPopulator.populateModel(stakeholderInviteResource.getEmail()));
        return "registration/register";
    }

}
