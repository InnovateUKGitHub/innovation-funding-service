package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.competition.form.ManageFundingApplicationsQueryForm;
import org.innovateuk.ifs.competition.form.SelectApplicationsForEmailForm;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.management.model.ManageFundingApplicationsModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.function.Supplier;



@Controller
@RequestMapping("/competition/{competitionId}/manage-funding-applications")
@PreAuthorize("hasAuthority('comp_admin')")
public class CompetitionManagementManageFundingApplicationsController {


    @Autowired
    private ManageFundingApplicationsModelPopulator manageFundingApplicationsModelPopulator;

    @GetMapping
    public String applications(Model model,
                               @PathVariable("competitionId") Long competitionId,
                               @ModelAttribute @Valid ManageFundingApplicationsQueryForm query,
                               BindingResult bindingResult,
                               ValidationHandler validationHandler) {
        return validationHandler.failNowOrSucceedWith(queryFailureView(competitionId), () -> {
                    model.addAttribute("model", manageFundingApplicationsModelPopulator.populate(query, competitionId));
                    model.addAttribute("form", new SelectApplicationsForEmailForm());
                    return "comp-mgt-manage-funding-applications";
                }
        );

    }

    @PostMapping
    public String selectApplications(Model model,
                                     @PathVariable("competitionId") Long competitionId,
                                     @ModelAttribute @Valid ManageFundingApplicationsQueryForm query,
                                     BindingResult queryFormBindingResult,
                                     ValidationHandler queryFormValidationHandler,
                                     @ModelAttribute @Valid SelectApplicationsForEmailForm ids,
                                     BindingResult idsBindingResult,
                                     ValidationHandler idsValidationHandler){
        return queryFormValidationHandler.failNowOrSucceedWith(queryFailureView(competitionId), () ->
            idsValidationHandler.failNowOrSucceedWith(idsFailureView(competitionId, query, model), () ->
                composeEmailRedirect(competitionId, ids)
            )
        );
    }

    private String composeEmailRedirect(long competitionId, SelectApplicationsForEmailForm ids){
        return "redirect:/competition/" + competitionId + "/funding/send?application_ids=";
    }

    private Supplier<String> queryFailureView(long competitionId) {
        return  () -> "redirect:/competition/" + competitionId + "/funding";
    }

    private Supplier<String> idsFailureView(long competitionId, ManageFundingApplicationsQueryForm query, Model model) {
        return () -> {
            model.addAttribute("model", manageFundingApplicationsModelPopulator.populate(query, competitionId));
            return "comp-mgt-manage-funding-applications";
        };
    }
}
