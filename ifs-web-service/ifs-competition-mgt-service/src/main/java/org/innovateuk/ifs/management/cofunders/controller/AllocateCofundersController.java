package org.innovateuk.ifs.management.cofunders.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.management.cofunders.form.AllocateCofundersForm;
import org.innovateuk.ifs.management.cofunders.populator.AllocateCofundersViewModelPopulator;
import org.innovateuk.ifs.management.cofunders.viewmodel.AllocateCofundersViewModel;
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
@RequestMapping("/competition/{competitionId}/cofunders/allocate")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = AllocateCofundersController.class)
@PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionCompositeId', 'ASSIGN_COFUNDERS')")
public class AllocateCofundersController {

    @Autowired
    private AllocateCofundersViewModelPopulator allocateCofundersViewModelPopulator;

    @GetMapping
    public String allocation(Model model,
                            @ModelAttribute AllocateCofundersForm allocateCofundersForm,
                            @SuppressWarnings("unused") BindingResult bindingResult,
                            @PathVariable("competitionId") long competitionId) {

        AllocateCofundersViewModel allocateCofundersViewModel = allocateCofundersViewModelPopulator.populateModel(
                competitionId,
                allocateCofundersForm.getFilter(),
                allocateCofundersForm.getPage());

        model.addAttribute("model", allocateCofundersViewModel);

        return "cofunders/allocate";
    }
}
