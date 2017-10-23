package org.innovateuk.ifs.management.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.innovateuk.ifs.assessment.service.AssessmentPanelInviteRestService;
import org.innovateuk.ifs.assessment.service.CompetitionInviteRestService;
import org.innovateuk.ifs.invite.resource.ParticipantStatusResource;
import org.innovateuk.ifs.management.controller.CompetitionManagementAssessorProfileController.AssessorProfileOrigin;
import org.innovateuk.ifs.management.form.AssessorPanelOverviewSelectionForm;
import org.innovateuk.ifs.management.form.OverviewAssessorsFilterForm;
import org.innovateuk.ifs.management.form.OverviewSelectionForm;
import org.innovateuk.ifs.management.model.InviteAssessorsFindModelPopulator;
import org.innovateuk.ifs.management.model.InviteAssessorsInviteModelPopulator;
import org.innovateuk.ifs.management.model.InviteAssessorsOverviewModelPopulator;
import org.innovateuk.ifs.management.model.PanelInviteAssessorsOverviewModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.innovateuk.ifs.invite.resource.ParticipantStatusResource.PENDING;
import static org.innovateuk.ifs.invite.resource.ParticipantStatusResource.REJECTED;
import static org.innovateuk.ifs.util.BackLinkUtil.buildOriginQueryString;

/**
 * This controller handles the Overview tab for inviting assessors to an Assessor Panel.
 */
@Controller
@RequestMapping("/assessment/panel/competition/{competitionId}/assessors")
@PreAuthorize("hasAnyAuthority('comp_admin','project_finance')")
public class CompetitionManagementPanelInviteAssessorsOverviewController extends CompetitionManagementCookieController<AssessorPanelOverviewSelectionForm> {

    private static final String SELECTION_FORM = "assessorPanelOverviewSelectionForm";

    @Autowired
    private AssessmentPanelInviteRestService assessmentPanelInviteRestService;

    @Autowired
    private PanelInviteAssessorsOverviewModelPopulator panelInviteAssessorsOverviewModelPopulator;

    @Override
    protected String getCookieName() {
        return SELECTION_FORM;
    }

    @Override
    protected Class<AssessorPanelOverviewSelectionForm> getFormType() {
        return AssessorPanelOverviewSelectionForm.class;
    }

    @GetMapping("/overview")
    public String overview(Model model,
                           @ModelAttribute(name = SELECTION_FORM, binding = false) AssessorPanelOverviewSelectionForm selectionForm,
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
                                             AssessorPanelOverviewSelectionForm selectionForm) {
        AssessorPanelOverviewSelectionForm storedSelectionForm = getSelectionFormFromCookie(request, competitionId).orElse(new AssessorPanelOverviewSelectionForm());

        AssessorPanelOverviewSelectionForm trimmedOverviewForm = trimSelectionByFilteredResult(
                storedSelectionForm,
                competitionId);
        selectionForm.setSelectedInviteIds(trimmedOverviewForm.getSelectedInviteIds());
        selectionForm.setAllSelected(trimmedOverviewForm.getAllSelected());

        saveFormToCookie(response, competitionId, selectionForm);
    }

    private AssessorPanelOverviewSelectionForm trimSelectionByFilteredResult(AssessorPanelOverviewSelectionForm selectionForm,
                                                                Long competitionId) {
        List<Long> filteredResults = getAllInviteIds(competitionId);
        AssessorPanelOverviewSelectionForm updatedSelectionForm = new AssessorPanelOverviewSelectionForm();

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
            AssessorPanelOverviewSelectionForm selectionForm = getSelectionFormFromCookie(request, competitionId).orElse(new AssessorPanelOverviewSelectionForm());
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
            AssessorPanelOverviewSelectionForm selectionForm = getSelectionFormFromCookie(request, competitionId).orElse(new AssessorPanelOverviewSelectionForm());

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
