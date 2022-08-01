package org.innovateuk.ifs.management.decision.controller;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.management.decision.form.*;
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
 * Handles the Competition Management Funding decision views and submission of funding decision.
 */
@Slf4j
@Controller
@RequestMapping(CompetitionManagementFundingDecisionController.DEFAULT_VIEW)
@SecuredBySpring(value = "Controller", description = "TODO", securedType = CompetitionManagementDecisionController.class)
@PreAuthorize("hasAnyAuthority('comp_admin')")
public class CompetitionManagementFundingDecisionController extends CompetitionManagementDecisionController {

    public static final String DEFAULT_VIEW = "/competition/{competitionId}/funding";

    protected String getCookieName() {
        return "decisionSelectionForm";
    }

    @GetMapping
    public String applications(Model model,
                               @PathVariable("competitionId") long competitionId,
                               @RequestParam(name = "filterChanged", required = false) boolean filterChanged,
                               @ModelAttribute @Valid DecisionPaginationForm paginationForm,
                               @ModelAttribute DecisionFilterForm filterForm,
                               @ModelAttribute DecisionSelectionForm selectionForm,
                               UserResource user,
                               BindingResult bindingResult,
                               HttpServletRequest request,
                               HttpServletResponse response) {
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
                               @ModelAttribute DecisionPaginationForm paginationForm,
                               @ModelAttribute DecisionSelectionForm decisionSelectionForm,
                               @ModelAttribute @Valid DecisionChoiceForm decisionChoiceForm,
                               @ModelAttribute DecisionFilterForm filterForm,
                               UserResource user,
                               BindingResult bindingResult,
                               HttpServletRequest request,
                               HttpServletResponse response) {
        return super.makeDecision(
                model,
                competitionId,
                paginationForm,
                decisionSelectionForm,
                decisionChoiceForm,
                filterForm,
                user,
                bindingResult,
                request,
                response);
    }

    @PostMapping(value = {""}, params = {"addAll"})
    public @ResponseBody JsonNode addAllApplicationsToDecisionSelectionList(@PathVariable("competitionId") long competitionId,
                                                                                   @RequestParam("addAll") boolean addAll,
                                                                                   HttpServletRequest request,
                                                                                   HttpServletResponse response) {
        return super.addAllApplicationsToDecisionSelectionList(
                competitionId,
                addAll,
                request,
                response);
    }

    @PostMapping(value = {""}, params = {"selectionId", "isSelected"})
    public @ResponseBody JsonNode addSelectedApplicationsToDecisionList(@PathVariable("competitionId") long competitionId,
                                                                               @RequestParam("selectionId") long applicationId,
                                                                               @RequestParam("isSelected") boolean isSelected,
                                                                               HttpServletRequest request,
                                                                               HttpServletResponse response) {
        return super.addSelectedApplicationsToDecisionList(
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