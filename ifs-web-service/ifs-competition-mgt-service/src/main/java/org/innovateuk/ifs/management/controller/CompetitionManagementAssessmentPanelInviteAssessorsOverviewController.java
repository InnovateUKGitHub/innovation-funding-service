package org.innovateuk.ifs.management.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.innovateuk.ifs.assessment.service.AssessmentPanelInviteRestService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.management.controller.CompetitionManagementAssessorProfileController.AssessorProfileOrigin;
import org.innovateuk.ifs.management.form.AssessmentPanelOverviewSelectionForm;
import org.innovateuk.ifs.management.model.AssessmentPanelInviteAssessorsOverviewModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static org.innovateuk.ifs.util.BackLinkUtil.buildOriginQueryString;

/**
 * This controller handles the Overview tab for inviting assessors to an Assessor Panel.
 */
@Controller
@RequestMapping("/assessment/panel/competition/{competitionId}/assessors")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = CompetitionManagementAssessmentPanelInviteAssessorsOverviewController.class)
@PreAuthorize("hasAnyAuthority('comp_admin','project_finance')")
public class CompetitionManagementAssessmentPanelInviteAssessorsOverviewController extends CompetitionManagementCookieController<AssessmentPanelOverviewSelectionForm> {

    private static final String SELECTION_FORM = "assessorPanelOverviewSelectionForm";

    @Autowired
    private AssessmentPanelInviteRestService assessmentPanelInviteRestService;

    @Autowired
    private AssessmentPanelInviteAssessorsOverviewModelPopulator panelInviteAssessorsOverviewModelPopulator;

    @Override
    protected String getCookieName() {
        return SELECTION_FORM;
    }

    @Override
    protected Class<AssessmentPanelOverviewSelectionForm> getFormType() {
        return AssessmentPanelOverviewSelectionForm.class;
    }

    @GetMapping("/overview")
    public String overview(Model model,
                           @ModelAttribute(name = SELECTION_FORM, binding = false) AssessmentPanelOverviewSelectionForm selectionForm,
                           @SuppressWarnings("unused") BindingResult bindingResult,
                           @PathVariable("competitionId") long competitionId,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam MultiValueMap<String, String> queryParams,
                           HttpServletRequest request,
                           HttpServletResponse response) {

        String originQuery = buildOriginQueryString(AssessorProfileOrigin.PANEL_OVERVIEW, queryParams);
        updateOverviewSelectionForm(request, response, competitionId, selectionForm);

        model.addAttribute("model", panelInviteAssessorsOverviewModelPopulator.populateModel(
                competitionId,
                page,
                originQuery
        ));

        return "assessors/panel-overview";
    }

    private void updateOverviewSelectionForm(HttpServletRequest request,
                                             HttpServletResponse response,
                                             long competitionId,
                                             AssessmentPanelOverviewSelectionForm selectionForm) {
        AssessmentPanelOverviewSelectionForm storedSelectionForm = getSelectionFormFromCookie(request, competitionId).orElse(new AssessmentPanelOverviewSelectionForm());

        AssessmentPanelOverviewSelectionForm trimmedOverviewForm = trimSelectionByFilteredResult(
                storedSelectionForm,
                competitionId);
        selectionForm.setSelectedInviteIds(trimmedOverviewForm.getSelectedInviteIds());
        selectionForm.setAllSelected(trimmedOverviewForm.getAllSelected());

        saveFormToCookie(response, competitionId, selectionForm);
    }

    private AssessmentPanelOverviewSelectionForm trimSelectionByFilteredResult(AssessmentPanelOverviewSelectionForm selectionForm,
                                                                               Long competitionId) {
        List<Long> filteredResults = getAllInviteIds(competitionId);
        AssessmentPanelOverviewSelectionForm updatedSelectionForm = new AssessmentPanelOverviewSelectionForm();

        selectionForm.getSelectedInviteIds().retainAll(filteredResults);
        updatedSelectionForm.setSelectedInviteIds(selectionForm.getSelectedInviteIds());

        if (updatedSelectionForm.getSelectedInviteIds().equals(filteredResults)  && !updatedSelectionForm.getSelectedInviteIds().isEmpty()) {
            updatedSelectionForm.setAllSelected(true);
        } else {
            updatedSelectionForm.setAllSelected(false);
        }

        return updatedSelectionForm;
    }

    @PostMapping(value = "/overview", params = {"selectionId"})
    public @ResponseBody JsonNode selectAssessorForResendList(
            @PathVariable("competitionId") long competitionId,
            @RequestParam("selectionId") long assessorId,
            @RequestParam("isSelected") boolean isSelected,
            HttpServletRequest request,
            HttpServletResponse response) {

        boolean limitExceeded = false;
        try {
            List<Long> InviteIds = getAllInviteIds(competitionId);
            AssessmentPanelOverviewSelectionForm selectionForm = getSelectionFormFromCookie(request, competitionId).orElse(new AssessmentPanelOverviewSelectionForm());
            if (isSelected) {
                int predictedSize = selectionForm.getSelectedInviteIds().size() + 1;
                if(limitIsExceeded(predictedSize)){
                    limitExceeded = true;
                } else {
                    selectionForm.getSelectedInviteIds().add(assessorId);
                    if (selectionForm.getSelectedInviteIds().containsAll(InviteIds)) {
                        selectionForm.setAllSelected(true);
                    }
                }
            } else {
                selectionForm.getSelectedInviteIds().remove(assessorId);
                selectionForm.setAllSelected(false);
            }
            saveFormToCookie(response, competitionId, selectionForm);
            return createJsonObjectNode(selectionForm.getSelectedInviteIds().size(), selectionForm.getAllSelected(), limitExceeded);
        } catch (Exception e) {
            return createFailureResponse();
        }
    }

    @PostMapping(value = "/overview", params = {"addAll"})
    public @ResponseBody JsonNode addAllAssessorsToResendList(Model model,
                                                              @PathVariable("competitionId") long competitionId,
                                                              @RequestParam("addAll") boolean addAll,
                                                              HttpServletRequest request,
                                                              HttpServletResponse response) {
        try {
            AssessmentPanelOverviewSelectionForm selectionForm = getSelectionFormFromCookie(request, competitionId).orElse(new AssessmentPanelOverviewSelectionForm());

            if (addAll) {
                selectionForm.setSelectedInviteIds(getAllInviteIds(competitionId));
                selectionForm.setAllSelected(true);
            } else {
                selectionForm.getSelectedInviteIds().clear();
                selectionForm.setAllSelected(false);
            }

            saveFormToCookie(response, competitionId, selectionForm);

            return createSuccessfulResponseWithSelectionStatus(selectionForm.getSelectedInviteIds().size(), selectionForm.getAllSelected(), false);
        } catch (Exception e) {
            return createFailureResponse();
        }
    }

    private List<Long> getAllInviteIds(long competitionId) {
        return assessmentPanelInviteRestService.getNonAcceptedAssessorInviteIds(competitionId).getSuccessObjectOrThrowException();
    }
}
