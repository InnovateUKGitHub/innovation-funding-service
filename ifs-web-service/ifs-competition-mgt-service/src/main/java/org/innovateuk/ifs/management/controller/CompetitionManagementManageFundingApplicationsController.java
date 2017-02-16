package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.competition.form.ManageFundingApplicationsQueryForm;
import org.innovateuk.ifs.management.model.ManageFundingApplicationsModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.function.Supplier;

import static org.innovateuk.ifs.controller.ValidationHandler.newBindingResultHandler;


@Controller
@RequestMapping("/competition/{competitionId}/manage-funding-applications")
@PreAuthorize("hasAuthority('comp_admin')")
public class CompetitionManagementManageFundingApplicationsController {


    @Autowired
    private ManageFundingApplicationsModelPopulator manageFundingApplicationsModelPopulator;

    @GetMapping
    public String applications(Model model,
                               @PathVariable("competitionId") Long competitionId,
                               @ModelAttribute @Valid ManageFundingApplicationsQueryForm queryForm,
                               BindingResult bindingResult) {

        Supplier<String> failureView = () -> "redirect:/competition/" + competitionId + "/funding";

        return newBindingResultHandler(bindingResult).failNowOrSucceedWith(failureView, () -> {
                    model.addAttribute("model", manageFundingApplicationsModelPopulator.populate(queryForm, competitionId));
                    return "comp-mgt-manage-funding-applications";
                }
        );

    }

}
