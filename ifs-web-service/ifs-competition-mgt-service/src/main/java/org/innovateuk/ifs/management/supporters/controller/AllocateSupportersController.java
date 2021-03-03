package org.innovateuk.ifs.management.supporters.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.management.supporters.form.AllocateSupportersForm;
import org.innovateuk.ifs.management.supporters.populator.AllocateSupportersViewModelPopulator;
import org.innovateuk.ifs.management.supporters.viewmodel.AllocateSupportersViewModel;
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
@RequestMapping("/competition/{competitionId}/supporters/allocate")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = AllocateSupportersController.class)
@PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionCompositeId', 'ASSIGN_SUPPORTERS')")
public class AllocateSupportersController {

    @Autowired
    private AllocateSupportersViewModelPopulator allocateSupportersViewModelPopulator;

    @GetMapping
    public String allocation(Model model,
                            @ModelAttribute AllocateSupportersForm allocateSupportersForm,
                            @SuppressWarnings("unused") BindingResult bindingResult,
                            @PathVariable("competitionId") long competitionId) {

        AllocateSupportersViewModel allocateSupportersViewModel = allocateSupportersViewModelPopulator.populateModel(
                competitionId,
                allocateSupportersForm.getFilter(),
                allocateSupportersForm.getPage());

        model.addAttribute("model", allocateSupportersViewModel);

        return "supporters/allocate";
    }
}
