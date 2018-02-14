package org.innovateuk.ifs.management.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.innovateuk.ifs.assessment.service.AssessmentReviewPanelInviteRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.invite.resource.ExistingUserStagedInviteListResource;
import org.innovateuk.ifs.invite.resource.ExistingUserStagedInviteResource;
import org.innovateuk.ifs.management.controller.CompetitionManagementAssessorProfileController.AssessorProfileOrigin;
import org.innovateuk.ifs.management.form.AssessmentReviewPanelSelectionForm;
import org.innovateuk.ifs.management.form.InviteNewAssessorsForm;
import org.innovateuk.ifs.management.model.AssessmentReviewPanelInviteAssessorsAcceptedModelPopulator;
import org.innovateuk.ifs.management.model.AssessmentReviewPanelInviteAssessorsFindModelPopulator;
import org.innovateuk.ifs.management.model.AssessmentReviewPanelInviteAssessorsInviteModelPopulator;
import org.innovateuk.ifs.management.viewmodel.AssessmentPanelInviteAssessorsFindViewModel;
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

import static java.lang.String.format;
import static org.innovateuk.ifs.util.BackLinkUtil.buildOriginQueryString;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.MapFunctions.asMap;

/**
 * This controller will handle all Competition Management requests related to inviting assessors to an Assessment Panel.
 */
@Controller
@RequestMapping("/assessment/panel/competition/{competitionId}/assessors")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = CompetitionManagementAssessmentReviewPanelInviteAssessorsController.class)
@PreAuthorize("hasAnyAuthority('comp_admin','project_finance')")
public class CompetitionManagementAssessmentReviewPanelInviteAssessorsController extends CompetitionManagementCookieController<AssessmentReviewPanelSelectionForm> {

    private static final String SELECTION_FORM = "assessorPanelSelectionForm";
    private static final String FORM_ATTR_NAME = "form";

    @Autowired
    private AssessmentReviewPanelInviteRestService assessmentReviewPanelInviteRestService;

    @Autowired
    private AssessmentReviewPanelInviteAssessorsFindModelPopulator panelInviteAssessorsFindModelPopulator;

    @Autowired
    private AssessmentReviewPanelInviteAssessorsInviteModelPopulator panelInviteAssessorsInviteModelPopulator;

    @Autowired
    private AssessmentReviewPanelInviteAssessorsAcceptedModelPopulator panelInviteAssessorsAcceptedModelPopulator;

    protected String getCookieName() {
        return SELECTION_FORM;
    }

    protected Class<AssessmentReviewPanelSelectionForm> getFormType() {
        return AssessmentReviewPanelSelectionForm.class;
    }

    @GetMapping
    public String assessors(@PathVariable("competitionId") long competitionId) {
        return format("redirect:/competition/%s/assessors/panel-find", competitionId);
    }

    @GetMapping("/find")
    public String find(Model model,
                       @ModelAttribute(name = SELECTION_FORM, binding = false) AssessmentReviewPanelSelectionForm selectionForm,
                       @SuppressWarnings("unused") BindingResult bindingResult,
                       @PathVariable("competitionId") long competitionId,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam MultiValueMap<String, String> queryParams,
                       HttpServletRequest request,
                       HttpServletResponse response) {

        String originQuery = buildOriginQueryString(AssessorProfileOrigin.PANEL_FIND, queryParams);
        updateSelectionForm(request, response, competitionId, selectionForm);
        AssessmentPanelInviteAssessorsFindViewModel assessmentPanelInviteAssessorsFindViewModel = panelInviteAssessorsFindModelPopulator.populateModel(competitionId, page, originQuery);

        model.addAttribute("model", assessmentPanelInviteAssessorsFindViewModel);
        model.addAttribute("originQuery", originQuery);

        return "assessors/panel-find";
    }

    private void updateSelectionForm(HttpServletRequest request,
                                     HttpServletResponse response,
                                     long competitionId,
                                     AssessmentReviewPanelSelectionForm selectionForm) {
        AssessmentReviewPanelSelectionForm storedSelectionForm = getSelectionFormFromCookie(request, competitionId).orElse(new AssessmentReviewPanelSelectionForm());

        AssessmentReviewPanelSelectionForm trimmedAssessorForm = trimSelectionByFilteredResult(storedSelectionForm, competitionId);
        selectionForm.setSelectedAssessorIds(trimmedAssessorForm.getSelectedAssessorIds());
        selectionForm.setAllSelected(trimmedAssessorForm.getAllSelected());

        saveFormToCookie(response, competitionId, selectionForm);
    }

    private AssessmentReviewPanelSelectionForm trimSelectionByFilteredResult(AssessmentReviewPanelSelectionForm selectionForm,
                                                                             long competitionId) {
        List<Long> filteredResults = getAllAssessorIds(competitionId);
        AssessmentReviewPanelSelectionForm updatedSelectionForm = new AssessmentReviewPanelSelectionForm();

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
    public @ResponseBody JsonNode selectAssessorForInviteList(
            @PathVariable("competitionId") long competitionId,
            @RequestParam("selectionId") long assessorId,
            @RequestParam("isSelected") boolean isSelected,
            HttpServletRequest request,
            HttpServletResponse response) {

        boolean limitExceeded = false;
        try {
            List<Long> assessorIds = getAllAssessorIds(competitionId);
            AssessmentReviewPanelSelectionForm selectionForm = getSelectionFormFromCookie(request, competitionId).orElse(new AssessmentReviewPanelSelectionForm());
            if (isSelected) {
                int predictedSize = selectionForm.getSelectedAssessorIds().size() + 1;
                if(limitIsExceeded(predictedSize)){
                    limitExceeded = true;
                } else {
                    selectionForm.getSelectedAssessorIds().add(assessorId);
                    if (selectionForm.getSelectedAssessorIds().containsAll(assessorIds)) {
                        selectionForm.setAllSelected(true);
                    }
                }
            } else {
                selectionForm.getSelectedAssessorIds().remove(assessorId);
                selectionForm.setAllSelected(false);
            }
            saveFormToCookie(response, competitionId, selectionForm);
            return createJsonObjectNode(selectionForm.getSelectedAssessorIds().size(), selectionForm.getAllSelected(), limitExceeded);
        } catch (Exception e) {
            return createFailureResponse();
        }
    }

    @PostMapping(value = "/find", params = {"addAll"})
    public @ResponseBody JsonNode addAllAssessorsToInviteList(Model model,
                                              @PathVariable("competitionId") long competitionId,
                                              @RequestParam("addAll") boolean addAll,
                                              HttpServletRequest request,
                                              HttpServletResponse response) {
        try {
            AssessmentReviewPanelSelectionForm selectionForm = getSelectionFormFromCookie(request, competitionId).orElse(new AssessmentReviewPanelSelectionForm());

            if (addAll) {
                selectionForm.setSelectedAssessorIds(getAllAssessorIds(competitionId));
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

    private List<Long> getAllAssessorIds(long competitionId) {
        return assessmentReviewPanelInviteRestService.getAvailableAssessorIds(competitionId).getSuccess();
    }

    @PostMapping(value = "/find/addSelected")
    public String addSelectedAssessorsToInviteList(Model model,
                                                   @PathVariable("competitionId") long competitionId,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam Optional<Long> innovationArea,
                                                   @ModelAttribute(SELECTION_FORM) AssessmentReviewPanelSelectionForm selectionForm,
                                                   ValidationHandler validationHandler,
                                                   HttpServletRequest request,
                                                   HttpServletResponse response) {

        AssessmentReviewPanelSelectionForm submittedSelectionForm = getSelectionFormFromCookie(request, competitionId)
                .filter(form -> !form.getSelectedAssessorIds().isEmpty())
                .orElse(selectionForm);
        Supplier<String> failureView = () -> redirectToFind(competitionId, page, innovationArea);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            RestResult<Void> restResult = assessmentReviewPanelInviteRestService.inviteUsers(
                    newSelectionFormToResource(submittedSelectionForm, competitionId));

            return validationHandler.addAnyErrors(restResult)
                    .failNowOrSucceedWith(failureView, () -> {
                        removeCookie(response, competitionId);
                        return redirectToInvite(competitionId, 0);
                    });
        });
    }

    private String redirectToFind(long competitionId, int page, Optional<Long> innovationArea) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/assessment/panel/competition/{competitionId}/assessors/find")
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

        String originQuery = buildOriginQueryString(AssessorProfileOrigin.PANEL_INVITE, queryParams);

        model.addAttribute("model", panelInviteAssessorsInviteModelPopulator.populateModel(competitionId, page, originQuery));
        model.addAttribute("originQuery", originQuery);

        return "assessors/panel-invite";
    }

    @GetMapping("/accepted")
    public String accepted(Model model,
                           @PathVariable("competitionId") long competitionId,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam MultiValueMap<String, String> queryParams) {
        String originQuery = buildOriginQueryString(AssessorProfileOrigin.PANEL_ACCEPTED, queryParams);

        model.addAttribute("model", panelInviteAssessorsAcceptedModelPopulator.populateModel(
                competitionId,
                page,
                originQuery
        ));

        return "assessors/panel-accepted";
    }

    @PostMapping(value = "/invite", params = {"remove"})
    public String removeInviteFromInviteView(Model model,
                                             @PathVariable("competitionId") long competitionId,
                                             @RequestParam(name = "remove") String email,
                                             @RequestParam(defaultValue = "0") int page,
                                             @SuppressWarnings("unused") @ModelAttribute(FORM_ATTR_NAME) InviteNewAssessorsForm form) {
        deleteInvite(email, competitionId).getSuccess();
        return redirectToInvite(competitionId, page);
    }

    @PostMapping(value = "/invite", params = {"removeAll"})
    public String removeAllInvitesFromInviteView(Model model,
                                                 @PathVariable("competitionId") long competitionId,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @SuppressWarnings("unused") @ModelAttribute(FORM_ATTR_NAME) InviteNewAssessorsForm form) {
        deleteAllInvites(competitionId).getSuccess();
        return redirectToInvite(competitionId, page);
    }

    private ServiceResult<Void> deleteInvite(String email, long competitionId) {
        return assessmentReviewPanelInviteRestService.deleteInvite(email, competitionId).toServiceResult();
    }

    private ServiceResult<Void> deleteAllInvites(long competitionId) {
        return assessmentReviewPanelInviteRestService.deleteAllInvites(competitionId).toServiceResult();
    }

    private String redirectToInvite(long competitionId, int page) {
        return "redirect:" + UriComponentsBuilder.fromPath("/assessment/panel/competition/{competitionId}/assessors/invite")
                .queryParam("page", page)
                .buildAndExpand(asMap("competitionId", competitionId))
                .toUriString();
    }

    private ExistingUserStagedInviteListResource newSelectionFormToResource(AssessmentReviewPanelSelectionForm form, long competitionId) {
        return new ExistingUserStagedInviteListResource(simpleMap(
                form.getSelectedAssessorIds(), id -> new ExistingUserStagedInviteResource(id, competitionId)));
    }
}
