package org.innovateuk.ifs.management.cofunders.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.management.cofunders.form.AssignCofundersForm;
import org.innovateuk.ifs.management.cofunders.populator.AssignCofundersViewModelPopulator;
import org.innovateuk.ifs.management.cofunders.viewmodel.AssignCofundersViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/competition/{competitionId}/cofunders/assign/{applicationId}")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = AssignCofundersController.class)
@PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionCompositeId', 'COFUNDERS')")
public class AssignCofundersController {

    @Autowired
    private AssignCofundersViewModelPopulator assignCofundersViewModelPopulator;

    @GetMapping
    public String assignment(Model model,
                             @ModelAttribute AssignCofundersForm assignCofundersForm,
                             @SuppressWarnings("unused") BindingResult bindingResult,
                             @PathVariable("competitionId") long competitionId,
                             @PathVariable("applicationId") long applicationId) {

        AssignCofundersViewModel assignCofundersViewModel = assignCofundersViewModelPopulator.populateModel(
                competitionId,
                applicationId,
                assignCofundersForm.getFilter(),
                assignCofundersForm.getPage());

        model.addAttribute("model", assignCofundersViewModel);

        return "cofunders/assign";
    }
}
