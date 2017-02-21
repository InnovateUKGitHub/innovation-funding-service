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
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;


@Controller
@RequestMapping("/competition/{competitionId}/manage-funding-applications")
@PreAuthorize("hasAuthority('comp_admin')")
public class CompetitionManagementManageFundingApplicationsController {


    private static final String MANAGE_FUNDING_APPLICATIONS_VIEW = "comp-mgt-manage-funding-applications";

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
                    return MANAGE_FUNDING_APPLICATIONS_VIEW;
                }
        );

    }

    @PostMapping
    public String selectApplications(Model model,
                                     @PathVariable("competitionId") Long competitionId,
                                     @ModelAttribute @Valid ManageFundingApplicationsQueryForm query,
                                     BindingResult queryFormBindingResult,
                                     ValidationHandler queryFormValidationHandler,
                                     @ModelAttribute("form") @Valid SelectApplicationsForEmailForm ids,
                                     BindingResult idsBindingResult,
                                     ValidationHandler idsValidationHandler) {
        return queryFormValidationHandler.failNowOrSucceedWith(queryFailureView(competitionId),  // Pass or fail JSR 303 on the query form
                () -> idsValidationHandler.failNowOrSucceedWith(idsFailureView(competitionId, query, model), // Pass or fail JSR 303 on the ids
                        () -> {
                            // Custom validation
                            List<Long> applicationIds = ids.getIds().stream().map(this::toLongOrNull).filter(Objects::nonNull).collect(toList());
                            if (applicationIds.isEmpty()) {
                                idsBindingResult.rejectValue("ids", "validation.manage.funding.applications.no.application.selected");
                            }
                            return idsValidationHandler.failNowOrSucceedWith(idsFailureView(competitionId, query, model), // Pass or fail custom validation
                                    () -> composeEmailRedirect(competitionId, applicationIds));
                        }
                )
        );
    }


    private String composeEmailRedirect(long competitionId, List<Long> ids) {
        String idParameters = ids.stream().map(Object::toString).collect(Collectors.joining(","));
        return "redirect:/competition/" + competitionId + "/funding/send?application_ids=" + idParameters;
    }

    private Supplier<String> queryFailureView(long competitionId) {
        return () -> "redirect:/competition/" + competitionId + "/funding";
    }

    private Supplier<String> idsFailureView(long competitionId, ManageFundingApplicationsQueryForm query, Model model) {
        return () -> {
            model.addAttribute("model", manageFundingApplicationsModelPopulator.populate(query, competitionId));
            return "comp-mgt-manage-funding-applications";
        };
    }

    private Long toLongOrNull(String value) {
        if (value != null)
        {
            try {
              return Long.parseLong(value);
            } catch (NumberFormatException e){
                return null;
            }
        }
        return null;
    }
}
