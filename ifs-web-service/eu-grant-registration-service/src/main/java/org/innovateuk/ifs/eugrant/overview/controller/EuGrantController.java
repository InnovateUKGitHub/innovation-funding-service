package org.innovateuk.ifs.eugrant.overview.controller;

import org.innovateuk.ifs.eugrant.overview.populator.EuGrantOverviewViewModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * A controller for the Horizon 2020 grant registration.
 */
@Controller
@RequestMapping("/")
public class EuGrantController {

    private EuGrantOverviewViewModelPopulator euGrantOverviewViewModelPopulator;

    public EuGrantController() {
    }

    @Autowired
    public EuGrantController(EuGrantOverviewViewModelPopulator euGrantOverviewViewModelPopulator) {
        this.euGrantOverviewViewModelPopulator = euGrantOverviewViewModelPopulator;
    }

    @GetMapping("/overview")
    public String overview(Model model) {
        model.addAttribute("model", euGrantOverviewViewModelPopulator.populate());
        return "eugrant/overview";
    }

}
