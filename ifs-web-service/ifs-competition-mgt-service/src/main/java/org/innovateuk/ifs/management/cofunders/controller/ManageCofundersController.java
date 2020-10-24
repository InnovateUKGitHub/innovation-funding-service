package org.innovateuk.ifs.management.cofunders.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.management.cofunders.populator.ManageCofundersViewModelPopulator;
import org.innovateuk.ifs.management.cofunders.viewmodel.ManageCofundersViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/competition/{competitionId}/cofunders")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = ManageCofundersController.class)
@PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionCompositeId', 'COFUNDERS')")
public class ManageCofundersController {

    @Autowired
    private ManageCofundersViewModelPopulator manageCofundersViewModelPopulator;

    @GetMapping
    public String cofunders(@PathVariable("competitionId") long competitionId, Model model) {

        ManageCofundersViewModel manageCofundersViewModel = manageCofundersViewModelPopulator.populateModel(competitionId);

        model.addAttribute("model", manageCofundersViewModel);

        return "cofunders/manage";
    }

}
