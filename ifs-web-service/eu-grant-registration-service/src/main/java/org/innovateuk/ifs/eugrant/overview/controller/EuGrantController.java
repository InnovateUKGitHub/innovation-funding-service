package org.innovateuk.ifs.eugrant.overview.controller;

import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.innovateuk.ifs.eugrant.EuGrantRestService;
import org.innovateuk.ifs.eugrant.overview.populator.EuGrantOverviewViewModelPopulator;
import org.innovateuk.ifs.eugrant.overview.service.EuGrantCookieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * A controller for the Horizon 2020 grant registration.
 */
@Controller
@RequestMapping("/")
public class EuGrantController {

    @Autowired
    private EuGrantOverviewViewModelPopulator euGrantOverviewViewModelPopulator;

    @Autowired
    private EuGrantRestService euGrantRestService;

    @Autowired
    private EuGrantCookieService euGrantCookieService;

    @GetMapping("/overview")
    public String overview(Model model) {
        model.addAttribute("model", euGrantOverviewViewModelPopulator.populate());
        return "eugrant/overview";
    }

    @PostMapping(value = "/overview", params = "without-dialog")
    public String noJsDialogView(Model model) {
        return "eugrant/confirm-submit";
    }

    @PostMapping("/overview")
    public String submit(Model model) {
        EuGrantResource euGrantResource = euGrantCookieService.get();
        if (euGrantResource.getId() == null) {
            return overview(model);
        }
        return euGrantRestService.submit(euGrantResource.getId()).andOnSuccessReturn(r -> {
            euGrantCookieService.clear();
            euGrantCookieService.setPreviouslySubmitted(r);
            return "redirect:/submitted";
        }).getSuccess();
    }

    @GetMapping("/submitted")
    public String submitted(Model model) {
        return euGrantCookieService.getPreviouslySubmitted()
                .map(euGrant -> {
                    model.addAttribute("model", euGrant);
                    return "eugrant/submitted";
                })
                .orElse("redirect:/overview");
    }
}