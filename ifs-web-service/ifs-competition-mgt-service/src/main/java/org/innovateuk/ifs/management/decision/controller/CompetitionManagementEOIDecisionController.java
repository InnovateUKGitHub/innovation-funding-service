package org.innovateuk.ifs.management.decision.controller;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.management.decision.form.FundingDecisionChoiceForm;
import org.innovateuk.ifs.management.decision.form.FundingDecisionFilterForm;
import org.innovateuk.ifs.management.decision.form.FundingDecisionPaginationForm;
import org.innovateuk.ifs.management.decision.form.FundingDecisionSelectionForm;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * Handles the Competition Management EOI decision views and submission of EOI decision.
 */
@Slf4j
@Controller
@RequestMapping(CompetitionManagementEOIDecisionController.DEFAULT_VIEW)
@SecuredBySpring(value = "Controller", description = "TODO", securedType = CompetitionManagementEOIDecisionController.class)
@PreAuthorize("hasAnyAuthority('comp_admin', 'support')")
public class CompetitionManagementEOIDecisionController extends CompetitionManagementDecisionController {

    public static final String DEFAULT_VIEW = "/competition/{competitionId}/applications/eoi";

    protected String getCookieName() {
        return "eoiDecisionSelectionForm";
    }

    @GetMapping
    public String applications(Model model,
                               @PathVariable("competitionId") long competitionId,
                               @RequestParam(name = "filterChanged", required = false) boolean filterChanged,
                               @ModelAttribute @Valid FundingDecisionPaginationForm paginationForm,
                               @ModelAttribute FundingDecisionFilterForm filterForm,
                               @ModelAttribute FundingDecisionSelectionForm selectionForm,
                               UserResource user,
                               BindingResult bindingResult,
                               HttpServletRequest request,
                               HttpServletResponse response) {
        filterForm.setEoi(true);
        return super.applications(model,
                competitionId,
                filterChanged,
                paginationForm,
                filterForm,
                selectionForm,
                user,
                bindingResult,
                request,
                response);
    }

    @PostMapping(value = {""})
    public String makeDecision(Model model,
                               @PathVariable("competitionId") long competitionId,
                               @ModelAttribute FundingDecisionPaginationForm paginationForm,
                               @ModelAttribute FundingDecisionSelectionForm fundingDecisionSelectionForm,
                               @ModelAttribute @Valid FundingDecisionChoiceForm fundingDecisionChoiceForm,
                               @ModelAttribute FundingDecisionFilterForm filterForm,
                               UserResource user,
                               BindingResult bindingResult,
                               HttpServletRequest request,
                               HttpServletResponse response) {
        return super.makeDecision(
                model,
                competitionId,
                paginationForm,
                fundingDecisionSelectionForm,
                fundingDecisionChoiceForm,
                filterForm,
                user,
                bindingResult,
                request,
                response);
    }

    @PostMapping(value = {""}, params = {"addAll"})
    public @ResponseBody JsonNode addAllApplicationsToFundingDecisionSelectionList(@PathVariable("competitionId") long competitionId,
                                                                                   @RequestParam("addAll") boolean addAll,
                                                                                   HttpServletRequest request,
                                                                                   HttpServletResponse response) {
        return super.addAllApplicationsToFundingDecisionSelectionList(
                competitionId,
                addAll,
                request,
                response);
    }

    @PostMapping(value = {""}, params = {"selectionId", "isSelected"})
    public @ResponseBody JsonNode addSelectedApplicationsToFundingDecisionList(@PathVariable("competitionId") long competitionId,
                                                                               @RequestParam("selectionId") long applicationId,
                                                                               @RequestParam("isSelected") boolean isSelected,
                                                                               HttpServletRequest request,
                                                                               HttpServletResponse response) {
        return super.addSelectedApplicationsToFundingDecisionList(
                competitionId,
                applicationId,
                isSelected,
                request,
                response);
    }

    protected String getDefaultView(long competitionId) {
        return DEFAULT_VIEW.replace("{competitionId}", Long.toString(competitionId));
    }

}