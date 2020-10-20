package org.innovateuk.ifs.management.cofunders.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.management.cofunders.populator.ReviewCofunderViewModelPopulator;
import org.innovateuk.ifs.management.cofunders.populator.ViewCofunderViewModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/competition/{competitionId}/cofunders/view")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = ViewCofundersController.class)
@PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionCompositeId', 'COFUNDERS')")
public class ViewCofundersController {

    @Autowired
    private ViewCofunderViewModelPopulator viewCofunderViewModelPopulator;

    @Autowired
    private ReviewCofunderViewModelPopulator reviewCofunderViewModelPopulator;

    @GetMapping
    public String overview(@PathVariable long competitionId,
                           @RequestParam(value = "page", defaultValue = "0") int page,
                           @RequestParam(value = "applicationFilter", required = false) String applicationFilter,
                           Model model) {
        model.addAttribute("model", viewCofunderViewModelPopulator.populateModel(competitionId, applicationFilter, page));
        return "cofunders/overview";
    }

    @GetMapping("/{applicationId}")
    public String review(@PathVariable long competitionId,
                         @PathVariable long applicationId,
                         Model model) {
        model.addAttribute("model", reviewCofunderViewModelPopulator.populateModel(applicationId));
        return "cofunders/review";
    }
}
