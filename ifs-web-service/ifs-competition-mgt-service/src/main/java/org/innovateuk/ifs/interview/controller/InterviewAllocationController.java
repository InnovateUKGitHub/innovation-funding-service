package org.innovateuk.ifs.interview.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.interview.form.InterviewAllocationSelectionForm;
import org.innovateuk.ifs.interview.service.InterviewAllocationRestService;
import org.innovateuk.ifs.management.controller.CompetitionManagementAssessorProfileController;
import org.innovateuk.ifs.management.controller.CompetitionManagementCookieController;
import org.innovateuk.ifs.management.model.InterviewAcceptedAssessorsModelPopulator;
import org.innovateuk.ifs.management.model.UnallocatedInterviewApplicationsModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static org.innovateuk.ifs.util.BackLinkUtil.buildOriginQueryString;

/**
 * This controller will handle all Competition Management requests related to allocating applications to assessors for interview panel.
 */
@Controller
@RequestMapping("/assessment/interview/competition/{competitionId}/assessors")
@SecuredBySpring(value = "Controller", description = "Comp Admins and Project Finance users can assign application to assessors for an Interview Panel", securedType = InterviewAllocationController.class)
@PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionCompositeId', 'INTERVIEW')")
public class InterviewAllocationController extends CompetitionManagementCookieController<InterviewAllocationSelectionForm> {

    private static final String SELECTION_FORM = "interviewAllocationSelectionForm";

    @Autowired
    private InterviewAcceptedAssessorsModelPopulator interviewAcceptedAssessorsModelPopulator;

    @Autowired
    private UnallocatedInterviewApplicationsModelPopulator unallocatedInterviewApplicationsModelPopulator;

    @Autowired
    private InterviewAllocationRestService interviewAllocationRestService;

    @Autowired
    private CompetitionService competitionService;

    @Override
    protected String getCookieName() {
        return SELECTION_FORM;
    }

    @Override
    protected Class<InterviewAllocationSelectionForm> getFormType() {
        return InterviewAllocationSelectionForm.class;
    }


    @GetMapping("/allocate-assessors")
    public String overview(Model model,
                           @PathVariable("competitionId") long competitionId,
                           @RequestParam MultiValueMap<String, String> queryParams,
                           @RequestParam(value = "page", defaultValue = "0") int page
    ) {
        CompetitionResource competitionResource = competitionService.getById(competitionId);

        String originQuery = buildOriginQueryString(CompetitionManagementAssessorProfileController.AssessorProfileOrigin.INTERVIEW_ACCEPTED, queryParams);

        model.addAttribute("model", interviewAcceptedAssessorsModelPopulator.populateModel(
                competitionResource,
                originQuery
        ));
        model.addAttribute("originQuery", originQuery);

        return "assessors/interview/allocate-accepted-assessors";
    }

    @GetMapping("/unallocated-applications/{userId}")
    public String applications(@ModelAttribute(name = SELECTION_FORM, binding = false) InterviewAllocationSelectionForm selectionForm,
                               Model model,
                               @PathVariable("competitionId") long competitionId,
                               @PathVariable("userId") long userId,
                               @RequestParam(value = "page", defaultValue = "0") int page,
                               HttpServletRequest request,
                               HttpServletResponse response
    ) {
        updateSelectionForm(request, response, competitionId, selectionForm);


        model.addAttribute("model", unallocatedInterviewApplicationsModelPopulator.populateModel(
                competitionId,
                userId,
                page
        ));
        return "assessors/interview/unallocated-applications";
    }

    @PostMapping(value = "/unallocated-applications/{userId}", params = {"addAll"})
    public @ResponseBody JsonNode addAllAssessorsToInviteList(Model model,
                                                              @PathVariable("competitionId") long competitionId,
                                                              @PathVariable("userId") long userId,
                                                              @RequestParam("addAll") boolean addAll,
                                                              HttpServletRequest request,
                                                              HttpServletResponse response) {
        try {
            InterviewAllocationSelectionForm selectionForm = getSelectionFormFromCookie(request, competitionId).orElse(new InterviewAllocationSelectionForm());

            if (addAll) {
                selectionForm.setSelectedIds(getAllSelectableApplicationIds(competitionId, userId));
                selectionForm.setAllSelected(true);
            } else {
                selectionForm.getSelectedIds().clear();
                selectionForm.setAllSelected(false);
            }

            saveFormToCookie(response, competitionId, selectionForm);

            return createSuccessfulResponseWithSelectionStatus(selectionForm.getSelectedIds().size(), selectionForm.getAllSelected(), false);
        } catch (Exception e) {
            return createFailureResponse();
        }
    }
    @PostMapping(value = "/unallocated-applications/{userId}", params = {"selectionId"})
    public @ResponseBody
    JsonNode selectAssessorForInviteList(
            @PathVariable("competitionId") long competitionId,
            @PathVariable("userId") long userId,
            @RequestParam("selectionId") long assessorId,
            @RequestParam("isSelected") boolean isSelected,
            HttpServletRequest request,
            HttpServletResponse response) {

        boolean limitExceeded = false;
        try {
            List<Long> assessorIds = getAllSelectableApplicationIds(competitionId, userId);
            InterviewAllocationSelectionForm selectionForm = getSelectionFormFromCookie(request, competitionId).orElse(new InterviewAllocationSelectionForm());
            if (isSelected) {
                int predictedSize = selectionForm.getSelectedIds().size() + 1;
                if(limitIsExceeded(predictedSize)){
                    limitExceeded = true;
                } else {
                    selectionForm.getSelectedIds().add(assessorId);
                    if (selectionForm.getSelectedIds().containsAll(assessorIds)) {
                        selectionForm.setAllSelected(true);
                    }
                }
            } else {
                selectionForm.getSelectedIds().remove(assessorId);
                selectionForm.setAllSelected(false);
            }
            saveFormToCookie(response, competitionId, selectionForm);
            return createJsonObjectNode(selectionForm.getSelectedIds().size(), selectionForm.getAllSelected(), limitExceeded);
        } catch (Exception e) {
            return createFailureResponse();
        }
    }

    private List<Long> getAllSelectableApplicationIds(long competitionId, long assessorId) {
        return interviewAllocationRestService.getUnallocatedApplicationIds(competitionId, assessorId).getSuccess();
    }

    private void updateSelectionForm(HttpServletRequest request,
                                     HttpServletResponse response,
                                     long competitionId,
                                     InterviewAllocationSelectionForm selectionForm) {
        InterviewAllocationSelectionForm storedSelectionForm = getSelectionFormFromCookie(request, competitionId).orElse(new InterviewAllocationSelectionForm());

        selectionForm.setSelectedIds(storedSelectionForm.getSelectedIds());
        selectionForm.setAllSelected(storedSelectionForm.getAllSelected());

        saveFormToCookie(response, competitionId, selectionForm);
    }
}
