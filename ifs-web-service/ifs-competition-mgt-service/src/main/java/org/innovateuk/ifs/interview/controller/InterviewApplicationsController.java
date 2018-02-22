package org.innovateuk.ifs.interview.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.innovateuk.ifs.assessment.service.InterviewPanelRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.interview.form.InterviewSelectionForm;
import org.innovateuk.ifs.invite.resource.ExistingUserStagedInviteListResource;
import org.innovateuk.ifs.invite.resource.ExistingUserStagedInviteResource;
import org.innovateuk.ifs.management.controller.CompetitionManagementCookieController;
import org.innovateuk.ifs.management.model.InterviewPanelApplicationsFindModelPopulator;
import org.innovateuk.ifs.management.model.InterviewPanelApplicationsInviteModelPopulator;
import org.innovateuk.ifs.management.service.CompetitionManagementApplicationServiceImpl.ApplicationOverviewOrigin;
import org.innovateuk.ifs.management.viewmodel.InterviewPanelApplicationsFindViewModel;
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

import static org.innovateuk.ifs.management.service.CompetitionManagementApplicationServiceImpl.ApplicationOverviewOrigin.INTERVIEW_PANEL_INVITE;
import static org.innovateuk.ifs.util.BackLinkUtil.buildOriginQueryString;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.MapFunctions.asMap;

/**
 * This controller will handle all Competition Management requests related to assigning applications to an interview Panel.
 */
@Controller
@RequestMapping("/assessment/interview-panel/competition/{competitionId}/applications")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = InterviewApplicationsController.class)
@PreAuthorize("hasAnyAuthority('comp_admin','project_finance')")
public class InterviewApplicationsController extends CompetitionManagementCookieController<InterviewSelectionForm> {

    private static final String SELECTION_FORM = "interviewPanelApplicationSelectionForm";

    @Autowired
    private InterviewPanelRestService interviewPanelRestService;

    @Autowired
    private InterviewPanelApplicationsFindModelPopulator interviewPanelApplicationsFindModelPopulator;

    @Autowired
    private InterviewPanelApplicationsInviteModelPopulator interviewPanelApplicationsInviteModelPopulator;

    @Override
    protected String getCookieName() {
        return SELECTION_FORM;
    }

    @Override
    protected Class<InterviewSelectionForm> getFormType() {
        return InterviewSelectionForm.class;
    }

    @GetMapping("/find")
    public String find(Model model,
                       @ModelAttribute(name = SELECTION_FORM, binding = false) InterviewSelectionForm selectionForm,
                       @SuppressWarnings("unused") BindingResult bindingResult,
                       @PathVariable("competitionId") long competitionId,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam MultiValueMap<String, String> queryParams,
                       HttpServletRequest request,
                       HttpServletResponse response) {

        String originQuery = buildOriginQueryString(ApplicationOverviewOrigin.INTERVIEW_PANEL_FIND, queryParams);
        updateSelectionForm(request, response, competitionId, selectionForm);

        InterviewPanelApplicationsFindViewModel interviewPanelApplicationsFindModel =
                interviewPanelApplicationsFindModelPopulator.populateModel(competitionId, page, originQuery);

        model.addAttribute("model", interviewPanelApplicationsFindModel);
        model.addAttribute("originQuery", originQuery);

        return "assessors/interview-panel-find";
    }

    private void updateSelectionForm(HttpServletRequest request,
                                     HttpServletResponse response,
                                     long competitionId,
                                     InterviewSelectionForm selectionForm) {
        InterviewSelectionForm storedSelectionForm = getSelectionFormFromCookie(request, competitionId).orElse(new InterviewSelectionForm());

        InterviewSelectionForm trimmedApplicationsForm = trimSelectionByFilteredResult(storedSelectionForm, competitionId);
        selectionForm.setSelectedAssessorIds(trimmedApplicationsForm.getSelectedAssessorIds());
        selectionForm.setAllSelected(trimmedApplicationsForm.getAllSelected());

        saveFormToCookie(response, competitionId, selectionForm);
    }

    private InterviewSelectionForm trimSelectionByFilteredResult(InterviewSelectionForm selectionForm,
                                                             long competitionId) {
        List<Long> filteredResults = getAvailableApplicationIds(competitionId);
        InterviewSelectionForm updatedSelectionForm = new InterviewSelectionForm();

        selectionForm.getSelectedAssessorIds().retainAll(filteredResults);
        updatedSelectionForm.setSelectedAssessorIds(selectionForm.getSelectedAssessorIds());

        if (updatedSelectionForm.getSelectedAssessorIds().equals(filteredResults)  && !updatedSelectionForm.getSelectedAssessorIds().isEmpty()) {
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
            InterviewSelectionForm selectionForm = getSelectionFormFromCookie(request, competitionId).orElse(new InterviewSelectionForm());
            if (isSelected) {
                int predictedSize = selectionForm.getSelectedAssessorIds().size() + 1;
                if(limitIsExceeded(predictedSize)){
                    limitExceeded = true;
                } else {
                    selectionForm.getSelectedAssessorIds().add(applicationId);
                    if (selectionForm.getSelectedAssessorIds().containsAll(applicationIds)) {
                        selectionForm.setAllSelected(true);
                    }
                }
            } else {
                selectionForm.getSelectedAssessorIds().remove(applicationId);
                selectionForm.setAllSelected(false);
            }
            saveFormToCookie(response, competitionId, selectionForm);
            return createJsonObjectNode(selectionForm.getSelectedAssessorIds().size(), selectionForm.getAllSelected(), limitExceeded);
        } catch (Exception e) {
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
    InterviewSelectionForm selectionForm = getSelectionFormFromCookie(request, competitionId).orElse(new InterviewSelectionForm());

            if (addAll) {
        selectionForm.setSelectedAssessorIds(getAvailableApplicationIds(competitionId));
        selectionForm.setAllSelected(true);
    } else {
        selectionForm.getSelectedAssessorIds().clear();
        selectionForm.setAllSelected(false);
    }

    saveFormToCookie(response, competitionId, selectionForm);

            return createSuccessfulResponseWithSelectionStatus(selectionForm.getSelectedAssessorIds().size(), selectionForm.getAllSelected(), false);
} catch (Exception e) {
        return createFailureResponse();
        }
        }

    private List<Long> getAvailableApplicationIds(long competitionId) {
        return interviewPanelRestService.getAvailableApplicationIds(competitionId).getSuccess();
    }

    @PostMapping(value = "/find/addSelected")
    public String addSelectedApplicationsToInviteList(Model model,
                                                      @PathVariable("competitionId") long competitionId,
                                                      @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam Optional<Long> innovationArea,
                                                      @ModelAttribute(SELECTION_FORM) InterviewSelectionForm selectionForm,
                                                      ValidationHandler validationHandler,
                                                      HttpServletRequest request,
                                                      HttpServletResponse response) {

        InterviewSelectionForm submittedSelectionForm = getSelectionFormFromCookie(request, competitionId)
                .filter(form -> !form.getSelectedAssessorIds().isEmpty())
                .orElse(selectionForm);
        Supplier<String> failureView = () -> redirectToFind(competitionId, page, innovationArea);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            RestResult<Void> restResult = interviewPanelRestService.assignApplications(
                    newSelectionFormToResource(submittedSelectionForm, competitionId));

            return validationHandler.addAnyErrors(restResult)
                    .failNowOrSucceedWith(failureView, () -> {
                        removeCookie(response, competitionId);
                        return redirectToInvite(competitionId, 0);
                    });
        });
    }

    private String redirectToFind(long competitionId, int page, Optional<Long> innovationArea) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/assessment/interview-panel/competition/{competitionId}/applications/find")
                .queryParam("page", page);

        innovationArea.ifPresent(innovationAreaId -> builder.queryParam("innovationArea", innovationAreaId));

        return "redirect:" + builder.buildAndExpand(asMap("competitionId", competitionId))
                .toUriString();
    }

    @GetMapping("/invite")
    public String invite(Model model,
                         @PathVariable("competitionId") long competitionId,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam MultiValueMap<String, String> queryParams) {

        String originQuery = buildOriginQueryString(INTERVIEW_PANEL_INVITE, queryParams);

        model.addAttribute("model", interviewPanelApplicationsInviteModelPopulator
                .populateModel(competitionId, page, originQuery));
        model.addAttribute("originQuery", originQuery);

        return "assessors/interview-panel-invite";
    }

    private String redirectToInvite(long competitionId, int page) {
        return "redirect:" + UriComponentsBuilder.fromPath("/assessment/interview-panel/competition/{competitionId}/applications/invite")
                .queryParam("page", page)
                .buildAndExpand(asMap("competitionId", competitionId))
                .toUriString();
    }

    private ExistingUserStagedInviteListResource newSelectionFormToResource(InterviewSelectionForm form, long competitionId) {
        return new ExistingUserStagedInviteListResource(simpleMap(
                form.getSelectedAssessorIds(), id -> new ExistingUserStagedInviteResource(id, competitionId)));
    }
}
