package org.innovateuk.ifs.interview.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.interview.form.InterviewAllocationNotifyForm;
import org.innovateuk.ifs.interview.form.InterviewAllocationSelectionForm;
import org.innovateuk.ifs.interview.resource.InterviewNotifyAllocationResource;
import org.innovateuk.ifs.interview.service.InterviewAllocationRestService;
import org.innovateuk.ifs.management.controller.CompetitionManagementAssessorProfileController;
import org.innovateuk.ifs.management.controller.CompetitionManagementCookieController;
import org.innovateuk.ifs.management.model.AllocateInterviewApplicationsModelPopulator;
import org.innovateuk.ifs.management.model.InterviewAcceptedAssessorsModelPopulator;
import org.innovateuk.ifs.management.model.UnallocatedInterviewApplicationsModelPopulator;
import org.innovateuk.ifs.management.service.CompetitionManagementApplicationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.management.service.CompetitionManagementApplicationServiceImpl.ApplicationOverviewOrigin.INTERVIEW_APPLICATION_ALLOCATION;
import static org.innovateuk.ifs.commons.rest.RestFailure.error;
import static org.innovateuk.ifs.util.BackLinkUtil.buildOriginQueryString;
import static org.innovateuk.ifs.util.CollectionFunctions.removeDuplicates;
import static org.innovateuk.ifs.util.MapFunctions.asMap;

/**
 * This controller will handle all Competition Management requests related to allocating applications to assessors for interview panel.
 */
@Controller
@RequestMapping("/assessment/interview/competition/{competitionId}/assessors")
@SecuredBySpring(value = "Controller", description = "Comp Admins and Project Finance users can assign application to assessors for an Interview Panel", securedType = InterviewAllocationController.class)
@PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionCompositeId', 'INTERVIEW')")
public class InterviewAllocationController extends CompetitionManagementCookieController<InterviewAllocationSelectionForm> {

    static final String SELECTION_FORM = "interviewAllocationSelectionForm";

    @Autowired
    private InterviewAcceptedAssessorsModelPopulator interviewAcceptedAssessorsModelPopulator;

    @Autowired
    private UnallocatedInterviewApplicationsModelPopulator unallocatedInterviewApplicationsModelPopulator;

    @Autowired
    private AllocateInterviewApplicationsModelPopulator allocateInterviewApplicationsModelPopulator;

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
                           @RequestParam(value = "page", defaultValue = "0") int page) {
        CompetitionResource competitionResource = competitionService.getById(competitionId);

        String originQuery = buildOriginQueryString(CompetitionManagementAssessorProfileController.AssessorProfileOrigin.INTERVIEW_ALLOCATION, queryParams);

        model.addAttribute("model", interviewAcceptedAssessorsModelPopulator.populateModel(
                competitionResource,
                originQuery
        ));
        model.addAttribute("originQuery", originQuery);

        return "assessors/interview/allocate-accepted-assessors";
    }

    @GetMapping("/unallocated-applications/{assessorId}")
    public String applications(@ModelAttribute(name = SELECTION_FORM, binding = false) InterviewAllocationSelectionForm selectionForm,
                               Model model,
                               @PathVariable("competitionId") long competitionId,
                               @RequestParam MultiValueMap<String, String> queryParams,
                               @PathVariable("assessorId") long assessorId,
                               @RequestParam(value = "page", defaultValue = "0") int page,
                               HttpServletRequest request,
                               HttpServletResponse response) {
        updateSelectionForm(request, response, competitionId, assessorId, selectionForm);
        queryParams.put("assessorId", singletonList(String.valueOf(assessorId)));
        String originQuery = buildOriginQueryString(INTERVIEW_APPLICATION_ALLOCATION, queryParams);
        model.addAttribute("model", unallocatedInterviewApplicationsModelPopulator.populateModel(
                competitionId,
                assessorId,
                page,
                originQuery
        ));
        return "assessors/interview/unallocated-applications";
    }

    @PostMapping("/allocate-applications/{userId}/addSelected")
    public String allocateApplications(@PathVariable("competitionId") long competitionId,
                                       @PathVariable("userId") long userId) {
        return redirectToSend(competitionId, userId);
    }

    @GetMapping("/allocate-applications/{userId}")
    public String allocateApplications(Model model,
                                       @ModelAttribute(name = "form", binding = false) InterviewAllocationNotifyForm form,
                                       @PathVariable("competitionId") long competitionId,
                                       @PathVariable("userId") long userId,
                                       @RequestParam MultiValueMap<String, String> queryParams,
                                       HttpServletRequest request) {

        return ifSelectionFormIsNotEmpty(competitionId, userId, request, selectionForm -> {
            model.addAttribute("model", allocateInterviewApplicationsModelPopulator.populateModel(competitionId, userId, selectionForm.getSelectedIds()));
            model.addAttribute("form", form);
            CompetitionResource competition = competitionService.getById(competitionId);
            form.setSubject(format("Applications for interview panel for '%s'", competition.getName()));
            String originQuery = buildOriginQueryString(CompetitionManagementApplicationServiceImpl.ApplicationOverviewOrigin.INTERVIEW_PANEL_ALLOCATE, queryParams);
            model.addAttribute("originQuery", originQuery);

            return "assessors/interview/allocate-applications";
        });
    }

    @PostMapping("/allocate-applications/{userId}")
    public String notifyAssessor(Model model,
                                 @ModelAttribute(name = "form") InterviewAllocationNotifyForm form,
                                 @PathVariable("competitionId") long competitionId,
                                 @PathVariable("userId") long userId,
                                 ValidationHandler validationHandler,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
        Supplier<String> failureView = () -> "foo";
        Supplier<String> successView = () -> {
            removeCookie(response, combineIds(competitionId, userId));
            return redirectToAllocatedTab(competitionId, userId);
        };

        return ifSelectionFormIsNotEmpty(competitionId, userId, request, selectionForm ->
            validationHandler
                .failNowOrSucceedWith(
                        failureView,
                        () -> {
                            RestResult<Void> sendResult =
                                    interviewAllocationRestService.notifyAllocations(
                                            new InterviewNotifyAllocationResource(
                                                    competitionId,
                                                    userId,
                                                    form.getSubject(),
                                                    form.getContent(),
                                                    selectionForm.getSelectedIds()
                                            )
                                    );
                            return validationHandler
                                    .addAnyErrors(error(removeDuplicates(sendResult.getErrors())))
                                    .failNowOrSucceedWith(
                                            failureView,
                                            successView
                                    );
                        }
                )
        );
    }

    @PostMapping(value = "/allocate-applications/{userId}", params = {"remove"})
    public String removeInviteFromInviteView(Model model,
                                             @PathVariable("competitionId") long competitionId,
                                             @PathVariable("userId") long userId,
                                             @RequestParam(name = "remove") long removeId,
                                             HttpServletRequest request,
                                             HttpServletResponse response) {
        return ifSelectionFormIsNotEmpty(competitionId, userId, request, selectionForm -> {
            boolean removed = selectionForm.getSelectedIds().remove(removeId);
            if (removed) {
                selectionForm.setAllSelected(false);
                saveFormToCookie(response, combineIds(competitionId, userId), selectionForm);
            }
            return redirectToSend(competitionId, userId);
        });
    }

    private String ifSelectionFormIsNotEmpty(long competitionId, long userId, HttpServletRequest request, Function<InterviewAllocationSelectionForm, String> success) {
        Optional<InterviewAllocationSelectionForm> maybeSelectionForm = getSelectionFormFromCookie(request, combineIds(competitionId, userId))
                .filter(f -> !f.getSelectedIds().isEmpty());

        if (!maybeSelectionForm.isPresent()) {
            return redirectToUnallocatedTab(competitionId, userId);
        } else {
            return success.apply(maybeSelectionForm.get());
        }
    }

    private String redirectToAllocatedTab(long competitionId, long userId) {
        return "redirect:" + UriComponentsBuilder
                // TODO this needs to point to the allocated applications
                .fromPath("/assessment/interview/competition/{competitionId}/assessors/unallocated-applications/{userId}")
                .buildAndExpand(asMap("competitionId", competitionId, "userId", userId))
                .toUriString();
    }

    private String redirectToUnallocatedTab(long competitionId, long userId) {
        return  "redirect:" + UriComponentsBuilder
                .fromPath("/assessment/interview/competition/{competitionId}/assessors/unallocated-applications/{userId}")
                .buildAndExpand(asMap("competitionId", competitionId, "userId", userId))
                .toUriString();
    }

    private String redirectToSend(long competitionId, long userId) {
        return  "redirect:" + UriComponentsBuilder
                .fromPath("/assessment/interview/competition/{competitionId}/assessors/allocate-applications/{userId}")
                .buildAndExpand(asMap("competitionId", competitionId, "userId", userId))
                .toUriString();
    }

    @PostMapping(value = "/unallocated-applications/{userId}", params = {"addAll"})
    public @ResponseBody JsonNode addAllAssessorsToInviteList(Model model,
                                                              @PathVariable("competitionId") long competitionId,
                                                              @PathVariable("userId") long userId,
                                                              @RequestParam("addAll") boolean addAll,
                                                              HttpServletRequest request,
                                                              HttpServletResponse response) {
        try {
            InterviewAllocationSelectionForm selectionForm = getSelectionFormFromCookie(request, combineIds(competitionId, userId)).orElse(new InterviewAllocationSelectionForm());

            if (addAll) {
                selectionForm.setSelectedIds(getAllSelectableApplicationIds(competitionId, userId));
                selectionForm.setAllSelected(true);
            } else {
                selectionForm.getSelectedIds().clear();
                selectionForm.setAllSelected(false);
            }

            saveFormToCookie(response, combineIds(competitionId, userId), selectionForm);

            return createSuccessfulResponseWithSelectionStatus(selectionForm.getSelectedIds().size(), selectionForm.getAllSelected(), false);
        } catch (Exception e) {
            return createFailureResponse();
        }
    }

    @PostMapping(value = "/unallocated-applications/{userId}", params = {"selectionId"})
    public @ResponseBody JsonNode selectAssessorForInviteList(
            @PathVariable("competitionId") long competitionId,
            @PathVariable("userId") long userId,
            @RequestParam("selectionId") long assessorId,
            @RequestParam("isSelected") boolean isSelected,
            HttpServletRequest request,
            HttpServletResponse response) {

        boolean limitExceeded = false;
        try {
            List<Long> assessorIds = getAllSelectableApplicationIds(competitionId, userId);
            InterviewAllocationSelectionForm selectionForm = getSelectionFormFromCookie(request, combineIds(competitionId, userId)).orElse(new InterviewAllocationSelectionForm());
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
            saveFormToCookie(response, combineIds(competitionId, userId), selectionForm);
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
                                     long userId,
                                     InterviewAllocationSelectionForm selectionForm) {
        InterviewAllocationSelectionForm storedSelectionForm = getSelectionFormFromCookie(request, combineIds(competitionId, userId)).orElse(new InterviewAllocationSelectionForm());

        selectionForm.setSelectedIds(storedSelectionForm.getSelectedIds());
        selectionForm.setAllSelected(storedSelectionForm.getAllSelected());

        saveFormToCookie(response, combineIds(competitionId, userId), selectionForm);
    }


    private String combineIds(long competitionId, long userId) {
        return String.format("%d_%d", competitionId, userId);
    }
}