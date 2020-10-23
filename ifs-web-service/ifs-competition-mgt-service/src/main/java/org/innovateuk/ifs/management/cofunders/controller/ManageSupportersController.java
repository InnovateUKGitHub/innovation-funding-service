package org.innovateuk.ifs.management.supporters.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.management.supporters.populator.ManageSupportersViewModelPopulator;
import org.innovateuk.ifs.management.supporters.viewmodel.ManageSupportersViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/competition/{competitionId}/supporters")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = ManageSupportersController.class)
@PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionCompositeId', 'SUPPORTERS')")
public class ManageSupportersController {

    @Autowired
    private ManageSupportersViewModelPopulator manageSupportersViewModelPopulator;

    @GetMapping
    public String supporters(@PathVariable("competitionId") long competitionId, Model model) {

        ManageSupportersViewModel manageSupportersViewModel = manageSupportersViewModelPopulator.populateModel(competitionId);

        model.addAttribute("model", manageSupportersViewModel);

        return "supporters/manage";
    }

}
