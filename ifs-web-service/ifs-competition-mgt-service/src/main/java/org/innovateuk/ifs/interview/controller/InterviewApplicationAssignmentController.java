package org.innovateuk.ifs.interview.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.origin.ApplicationSummaryOrigin;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.interview.form.InterviewAssignmentSelectionForm;
import org.innovateuk.ifs.interview.model.InterviewApplicationsFindModelPopulator;
import org.innovateuk.ifs.interview.model.InterviewApplicationsInviteModelPopulator;
import org.innovateuk.ifs.interview.model.InterviewApplicationsStatusModelPopulator;
import org.innovateuk.ifs.interview.service.InterviewAssignmentRestService;
import org.innovateuk.ifs.interview.viewmodel.InterviewAssignmentApplicationsFindViewModel;
import org.innovateuk.ifs.invite.resource.StagedApplicationListResource;
import org.innovateuk.ifs.invite.resource.StagedApplicationResource;
import org.innovateuk.ifs.management.cookie.CompetitionManagementCookieController;
import org.innovateuk.ifs.management.navigation.NavigationOrigin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.innovateuk.ifs.management.navigation.NavigationOrigin.INTERVIEW_PANEL_INVITE;
import static org.innovateuk.ifs.origin.BackLinkUtil.buildOriginQueryString;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.MapFunctions.asMap;

/**
 * This controller will handle all Competition Management requests related to assigning applications to an interview Panel.
 */
@Controller
@RequestMapping("/assessment/interview/competition/{competitionId}/applications")
@SecuredBySpring(value = "Controller", description = "Only comp admin and project finance users can setup interview" +
        " panels if they competition supports them", securedType = InterviewApplicationAssignmentController.class)
@PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionCompositeId', 'INTERVIEW_APPLICATIONS')")
public class InterviewApplicationAssignmentController extends CompetitionManagementCookieController<InterviewAssignmentSelectionForm> {

    private static final Log LOG = LogFactory.getLog(InterviewApplicationAssignmentController.class);

    private static final String SELECTION_FORM = "interviewAssignmentApplicationSelectionForm";

    @Autowired
    private InterviewAssignmentRestService interviewAssignmentRestService;

    @Autowired
    private InterviewApplicationsFindModelPopulator interviewApplicationsFindModelPopulator;

    @Autowired
    private InterviewApplicationsInviteModelPopulator interviewApplicationsInviteModelPopulator;

    @Autowired
    private InterviewApplicationsStatusModelPopulator interviewApplicationsStatusModelPopulator;

    @Override
    protected String getCookieName() {
        return SELECTION_FORM;
    }

    @Override
    protected Class<InterviewAssignmentSelectionForm> getFormType() {
        return InterviewAssignmentSelectionForm.class;
    }

    @GetMapping("/find")
    public String find(Model model,
                       @ModelAttribute(name = SELECTION_FORM, binding = false) InterviewAssignmentSelectionForm selectionForm,
                       @SuppressWarnings("unused") BindingResult bindingResult,
                       @PathVariable("competitionId") long competitionId,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam MultiValueMap<String, String> queryParams,
                       HttpServletRequest request,
                       HttpServletResponse response) {

        String originQuery = buildOriginQueryString(NavigationOrigin.INTERVIEW_PANEL_FIND, queryParams);
        updateSelectionForm(request, response, competitionId, selectionForm);

        InterviewAssignmentApplicationsFindViewModel interviewPanelApplicationsFindModel =
                interviewApplicationsFindModelPopulator.populateModel(competitionId, page, originQuery);

        model.addAttribute("model", interviewPanelApplicationsFindModel);
        model.addAttribute("originQuery", originQuery);

        return "assessors/interview/application-find";
    }

    private void updateSelectionForm(HttpServletRequest request,
                                     HttpServletResponse response,
                                     long competitionId,
                                     InterviewAssignmentSelectionForm selectionForm) {
        InterviewAssignmentSelectionForm storedSelectionForm = getSelectionFormFromCookie(request, competitionId).orElse(new InterviewAssignmentSelectionForm());

        InterviewAssignmentSelectionForm trimmedApplicationsForm = trimSelectionByFilteredResult(storedSelectionForm, competitionId);
        selectionForm.setSelectedIds(trimmedApplicationsForm.getSelectedIds());
        selectionForm.setAllSelected(trimmedApplicationsForm.getAllSelected());

        saveFormToCookie(response, competitionId, selectionForm);
    }

    private InterviewAssignmentSelectionForm trimSelectionByFilteredResult(InterviewAssignmentSelectionForm selectionForm,
                                                             long competitionId) {
        List<Long> filteredResults = getAvailableApplicationIds(competitionId);
        InterviewAssignmentSelectionForm updatedSelectionForm = new InterviewAssignmentSelectionForm();

        selectionForm.getSelectedIds().retainAll(filteredResults);
        updatedSelectionForm.setSelectedIds(selectionForm.getSelectedIds());

        if (updatedSelectionForm.getSelectedIds().equals(filteredResults)  && !updatedSelectionForm.getSelectedIds().isEmpty()) {
            updatedSelectionForm.setAllSelected(true);
        } else {
            updatedSelectionForm.setAllSelected(false);
        }

        return updatedSelectionForm;
    }

    @PostMapping(value = "/find", params = {"selectionId"})
    public @ResponseBody JsonNode selectApplicationForInviteList(
            @PathVariable("competitionId") long competitionId,
            @RequestParam("selectionId") long applicationId,
            @RequestParam("isSelected") boolean isSelected,
            HttpServletRequest request,
            HttpServletResponse response) {

        boolean limitExceeded = false;
        try {
            List<Long> applicationIds = getAvailableApplicationIds(competitionId);
            InterviewAssignmentSelectionForm selectionForm = getSelectionFormFromCookie(request, competitionId).orElse(new InterviewAssignmentSelectionForm());
            if (isSelected) {
                int predictedSize = selectionForm.getSelectedIds().size() + 1;
                if(limitIsExceeded(predictedSize)){
                    limitExceeded = true;
                } else {
                    selectionForm.getSelectedIds().add(applicationId);
                    if (selectionForm.getSelectedIds().containsAll(applicationIds)) {
                        selectionForm.setAllSelected(true);
                    }
                }
            } else {
                selectionForm.getSelectedIds().remove(applicationId);
                selectionForm.setAllSelected(false);
            }
            saveFormToCookie(response, competitionId, selectionForm);
            return createJsonObjectNode(selectionForm.getSelectedIds().size(), selectionForm.getAllSelected(), limitExceeded);
        } catch (Exception e) {
            LOG.error("exception thrown selecting application for invite list", e);
            return createFailureResponse();
        }
    }

    @PostMapping(value = "/find", params = {"addAll"})
    public @ResponseBody JsonNode addAllApplicationsToInviteList(Model model,
                                                                 @PathVariable("competitionId") long competitionId,
                                                                 @RequestParam("addAll") boolean addAll,
                                                                 HttpServletRequest request,
                                                                 HttpServletResponse response) {
        try {
            InterviewAssignmentSelectionForm selectionForm = getSelectionFormFromCookie(request, competitionId)
                    .orElse(new InterviewAssignmentSelectionForm());

            if (addAll) {
                selectionForm.setSelectedIds(getAvailableApplicationIds(competitionId));
                selectionForm.setAllSelected(true);
            } else {
                selectionForm.getSelectedIds().clear();
                selectionForm.setAllSelected(false);
            }

            saveFormToCookie(response, competitionId, selectionForm);

            return createSuccessfulResponseWithSelectionStatus(selectionForm.getSelectedIds().size(), selectionForm.getAllSelected(), false);
        } catch (Exception e) {
            LOG.error("exception thrown adding applications to invite list", e);
            return createFailureResponse();
        }
    }

    private List<Long> getAvailableApplicationIds(long competitionId) {
        return interviewAssignmentRestService.getAvailableApplicationIds(competitionId).getSuccess();
    }

    @PostMapping(value = "/find/addSelected")
    public String addSelectedApplicationsToInviteList(Model model,
                                                      @PathVariable("competitionId") long competitionId,
                                                      @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam Optional<Long> innovationArea,
                                                      @ModelAttribute(SELECTION_FORM) InterviewAssignmentSelectionForm selectionForm,
                                                      ValidationHandler validationHandler,
                                                      HttpServletRequest request,
                                                      HttpServletResponse response) {

        InterviewAssignmentSelectionForm submittedSelectionForm = getSelectionFormFromCookie(request, competitionId)
                .filter(form -> !form.getSelectedIds().isEmpty())
                .orElse(selectionForm);
        Supplier<String> failureView = () -> redirectToFind(competitionId, page, innovationArea);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            RestResult<Void> restResult = interviewAssignmentRestService.assignApplications(
                    newSelectionFormToResource(submittedSelectionForm, competitionId));

            return validationHandler.addAnyErrors(restResult)
                    .failNowOrSucceedWith(failureView, () -> {
                        removeCookie(response, competitionId);
                        return redirectToInvite(competitionId, 0);
                    });
        });
    }

    private String redirectToFind(long competitionId, int page, Optional<Long> innovationArea) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/assessment/interview/competition/{competitionId}/applications/find")
                .queryParam("page", page);

        innovationArea.ifPresent(innovationAreaId -> builder.queryParam("innovationArea", innovationAreaId));

        return "redirect:" + builder.buildAndExpand(asMap("competitionId", competitionId))
                .toUriString();
    }

    @GetMapping("/invite")
    public String invite(Model model,
                         @PathVariable("competitionId") long competitionId,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam MultiValueMap<String, String> queryParams,
                         @ModelAttribute(name = "form", binding = false) InterviewAssignmentSelectionForm selectionForm,
                         @SuppressWarnings("unused") BindingResult bindingResult) {

        String originQuery = buildOriginQueryString(INTERVIEW_PANEL_INVITE, queryParams);

        model.addAttribute("model", interviewApplicationsInviteModelPopulator
                .populateModel(competitionId, page, originQuery));
        model.addAttribute("form", selectionForm);
        model.addAttribute("originQuery", originQuery);

        return "assessors/interview/application-invite";
    }

    @PostMapping(value = "/invite", params = "remove")
    public String remove(Model model,
                         @PathVariable("competitionId") long competitionId,
                         @RequestParam("remove") long applicationId,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam MultiValueMap<String, String> queryParams,
                         @ModelAttribute(name = "form") InterviewAssignmentSelectionForm selectionForm,
                         @SuppressWarnings("unused") BindingResult bindingResult,
                         ValidationHandler validationHandler) {

        Supplier<String> failureAndSuccess = () -> invite(model, competitionId, page, queryParams, selectionForm, bindingResult);

        RestResult<Void> result = interviewAssignmentRestService.unstageApplication(applicationId);
        return validationHandler.addAnyErrors(result)
                .failNowOrSucceedWith(failureAndSuccess, failureAndSuccess);
    }

    @PostMapping(value = "/invite", params = "removeAll")
    public String removeAll(Model model,
                         @PathVariable("competitionId") long competitionId,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam MultiValueMap<String, String> queryParams,
                         @ModelAttribute(name = "form") InterviewAssignmentSelectionForm selectionForm,
                         @SuppressWarnings("unused") BindingResult bindingResult,
                         ValidationHandler validationHandler) {

        Supplier<String> successView = () -> redirectToFind(competitionId, 0, Optional.empty());
        Supplier<String> failureView = () -> invite(model, competitionId, page, queryParams, selectionForm, bindingResult);

        RestResult<Void> result = interviewAssignmentRestService.unstageApplications(competitionId);
        return validationHandler.addAnyErrors(result)
                .failNowOrSucceedWith(failureView, successView);
    }

    @GetMapping("/view-status")
    public String viewStatus(Model model,
                             @PathVariable("competitionId") long competitionId,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam MultiValueMap<String, String> queryParams) {

        String originQuery = buildOriginQueryString(ApplicationSummaryOrigin.COMP_EXEC_INTERVIEW, queryParams);

        model.addAttribute("model", interviewApplicationsStatusModelPopulator
                .populateModel(competitionId, page, originQuery));
        model.addAttribute("originQuery", originQuery);

        return "assessors/interview/application-view-status";
    }

    private String redirectToInvite(long competitionId, int page) {
        return "redirect:" + UriComponentsBuilder.fromPath("/assessment/interview/competition/{competitionId}/applications/invite")
                .queryParam("page", page)
                .buildAndExpand(asMap("competitionId", competitionId))
                .toUriString();
    }

    private StagedApplicationListResource newSelectionFormToResource(InterviewAssignmentSelectionForm form, long competitionId) {
        return new StagedApplicationListResource(simpleMap(
                form.getSelectedIds(), applicationId -> new StagedApplicationResource(applicationId, competitionId)));
    }
}