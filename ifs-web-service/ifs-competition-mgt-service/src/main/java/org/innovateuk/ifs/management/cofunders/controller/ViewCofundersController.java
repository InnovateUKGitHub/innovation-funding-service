package org.innovateuk.ifs.management.supporters.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.management.supporters.populator.ReviewSupporterViewModelPopulator;
import org.innovateuk.ifs.management.supporters.populator.ViewSupporterViewModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/competition/{competitionId}/supporters/view")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = ViewSupportersController.class)
@PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionCompositeId', 'SUPPORTERS')")
public class ViewSupportersController {

    @Autowired
    private ViewSupporterViewModelPopulator viewSupporterViewModelPopulator;

    @Autowired
    private ReviewSupporterViewModelPopulator reviewSupporterViewModelPopulator;

    @GetMapping
    public String overview(@PathVariable long competitionId,
                           @RequestParam(value = "page", defaultValue = "0") int page,
                           @RequestParam(value = "applicationFilter", required = false) String applicationFilter,
                           Model model) {
        model.addAttribute("model", viewSupporterViewModelPopulator.populateModel(competitionId, applicationFilter, page));
        return "supporters/overview";
    }

    @GetMapping("/{applicationId}")
    public String review(@PathVariable long competitionId,
                         @PathVariable long applicationId,
                         Model model) {
        model.addAttribute("model", reviewSupporterViewModelPopulator.populateModel(applicationId));
        return "supporters/review";
    }
}
