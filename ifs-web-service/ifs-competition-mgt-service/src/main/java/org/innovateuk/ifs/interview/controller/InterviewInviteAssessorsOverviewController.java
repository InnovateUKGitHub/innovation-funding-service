package org.innovateuk.ifs.interview.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.interview.form.InterviewOverviewSelectionForm;
import org.innovateuk.ifs.interview.model.InterviewInviteAssessorsOverviewModelPopulator;
import org.innovateuk.ifs.interview.service.InterviewInviteRestService;
import org.innovateuk.ifs.management.assessor.controller.CompetitionManagementAssessorProfileController.AssessorProfileOrigin;
import org.innovateuk.ifs.management.cookie.CompetitionManagementCookieController;
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

import static org.innovateuk.ifs.origin.BackLinkUtil.buildOriginQueryString;

/**
 * This controller handles the Overview tab for inviting assessors to an Interview Panel.
 */
@Controller
@RequestMapping("/assessment/interview/competition/{competitionId}/assessors")
@SecuredBySpring(value = "Controller", description = "Comp Admins and Project Finance users can invite assessors to an Interview Panel", securedType = InterviewInviteAssessorsOverviewController.class)
@PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionCompositeId', 'INTERVIEW')")
public class InterviewInviteAssessorsOverviewController extends CompetitionManagementCookieController<InterviewOverviewSelectionForm> {

    private static final Log LOG = LogFactory.getLog(InterviewInviteAssessorsOverviewController.class);

    private static final String SELECTION_FORM = "interviewOverviewSelectionForm";

    @Autowired
    private InterviewInviteRestService interviewInviteRestService;

    @Autowired
    private InterviewInviteAssessorsOverviewModelPopulator interviewInviteAssessorsOverviewModelPopulator;

    @Override
    protected String getCookieName() {
        return SELECTION_FORM;
    }

    @Override
    protected Class<InterviewOverviewSelectionForm> getFormType() {
        return InterviewOverviewSelectionForm.class;
    }

    @GetMapping("/pending-and-declined")
    public String overview(Model model,
                           @ModelAttribute(name = SELECTION_FORM, binding = false) InterviewOverviewSelectionForm selectionForm,
                           @SuppressWarnings("unused") BindingResult bindingResult,
                           @PathVariable("competitionId") long competitionId,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam MultiValueMap<String, String> queryParams,
                           HttpServletRequest request,
                           HttpServletResponse response) {

        String originQuery = buildOriginQueryString(AssessorProfileOrigin.INTERVIEW_OVERVIEW, queryParams);
        updateOverviewSelectionForm(request, response, competitionId, selectionForm);

        model.addAttribute("model", interviewInviteAssessorsOverviewModelPopulator.populateModel(
                competitionId,
                page,
                originQuery
        ));

        return "assessors/interview/assessor-overview";
    }

    private void updateOverviewSelectionForm(HttpServletRequest request,
                                             HttpServletResponse response,
                                             long competitionId,
                                             InterviewOverviewSelectionForm selectionForm) {
        InterviewOverviewSelectionForm storedSelectionForm = getSelectionFormFromCookie(request, competitionId).orElse(new InterviewOverviewSelectionForm());

        InterviewOverviewSelectionForm trimmedOverviewForm = trimSelectionByFilteredResult(
                storedSelectionForm,
                competitionId);
        selectionForm.setSelectedInviteIds(trimmedOverviewForm.getSelectedInviteIds());
        selectionForm.setAllSelected(trimmedOverviewForm.getAllSelected());

        saveFormToCookie(response, competitionId, selectionForm);
    }

    private InterviewOverviewSelectionForm trimSelectionByFilteredResult(InterviewOverviewSelectionForm selectionForm,
                                                                      long competitionId) {
        List<Long> filteredResults = getAllInviteIds(competitionId);
        InterviewOverviewSelectionForm updatedSelectionForm = new InterviewOverviewSelectionForm();

        selectionForm.getSelectedInviteIds().retainAll(filteredResults);
        updatedSelectionForm.setSelectedInviteIds(selectionForm.getSelectedInviteIds());

        if (updatedSelectionForm.getSelectedInviteIds().equals(filteredResults)  && !updatedSelectionForm.getSelectedInviteIds().isEmpty()) {
            updatedSelectionForm.setAllSelected(true);
        } else {
            updatedSelectionForm.setAllSelected(false);
        }

        return updatedSelectionForm;
    }

    @PostMapping(value = "/pending-and-declined", params = {"selectionId"})
    public @ResponseBody JsonNode selectAssessorForResendList(
            @PathVariable("competitionId") long competitionId,
            @RequestParam("selectionId") long assessorId,
            @RequestParam("isSelected") boolean isSelected,
            HttpServletRequest request,
            HttpServletResponse response) {

        boolean limitExceeded = false;
        try {
            List<Long> InviteIds = getAllInviteIds(competitionId);
            InterviewOverviewSelectionForm selectionForm = getSelectionFormFromCookie(request, competitionId).orElse(new InterviewOverviewSelectionForm());
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
            LOG.error("exception thrown selecting assessors for resend list", e);
            return createFailureResponse();
        }
    }

    @PostMapping(value = "/pending-and-declined", params = {"addAll"})
    public @ResponseBody JsonNode addAllAssessorsToResendList(Model model,
                                                              @PathVariable("competitionId") long competitionId,
                                                              @RequestParam("addAll") boolean addAll,
                                                              HttpServletRequest request,
                                                              HttpServletResponse response) {
        try {
            InterviewOverviewSelectionForm selectionForm = getSelectionFormFromCookie(request, competitionId).orElse(new InterviewOverviewSelectionForm());

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
            LOG.error("exception thrown adding assessors to resend list", e);

            return createFailureResponse();
        }
    }

    private List<Long> getAllInviteIds(long competitionId) {
        return interviewInviteRestService.getNonAcceptedAssessorInviteIds(competitionId).getSuccess();
    }
}
