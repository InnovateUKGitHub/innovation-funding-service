package org.innovateuk.ifs.interview.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.interview.form.InterviewSelectionForm;
import org.innovateuk.ifs.interview.model.InterviewInviteAssessorsAcceptedModelPopulator;
import org.innovateuk.ifs.interview.model.InterviewInviteAssessorsFindModelPopulator;
import org.innovateuk.ifs.interview.model.InterviewInviteAssessorsInviteModelPopulator;
import org.innovateuk.ifs.interview.service.InterviewInviteRestService;
import org.innovateuk.ifs.interview.viewmodel.InterviewInviteAssessorsFindViewModel;
import org.innovateuk.ifs.invite.resource.ExistingUserStagedInviteListResource;
import org.innovateuk.ifs.invite.resource.ExistingUserStagedInviteResource;
import org.innovateuk.ifs.management.assessor.controller.CompetitionManagementAssessorProfileController.AssessorProfileOrigin;
import org.innovateuk.ifs.management.cookie.controller.CompetitionManagementCookieController;
import org.innovateuk.ifs.management.assessor.form.InviteNewAssessorsForm;
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
 * This controller will handle all Competition Management requests related to inviting assessors to an Interview Panel.
 */
@Controller
@RequestMapping("/assessment/interview/competition/{competitionId}/assessors")
@SecuredBySpring(value = "Controller", description = "Comp Admins and Project Finance users can invite assessors to an Interview Panel", securedType = InterviewAssessorInviteController.class)
@PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionCompositeId', 'INTERVIEW')")
public class InterviewAssessorInviteController extends CompetitionManagementCookieController<InterviewSelectionForm> {

    private static final Log LOG = LogFactory.getLog(InterviewAssessorInviteController.class);

    private static final String SELECTION_FORM = "interviewSelectionForm";
    private static final String FORM_ATTR_NAME = "form";

    private InterviewInviteRestService interviewInviteRestService;

    private InterviewInviteAssessorsFindModelPopulator interviewInviteAssessorsFindModelPopulator;

    private InterviewInviteAssessorsInviteModelPopulator interviewInviteAssessorsInviteModelPopulator;

    private InterviewInviteAssessorsAcceptedModelPopulator interviewInviteAssessorsAcceptedModelPopulator;

    @Autowired
    public InterviewAssessorInviteController(InterviewInviteRestService interviewInviteRestService,
                                             InterviewInviteAssessorsFindModelPopulator interviewInviteAssessorsFindModelPopulator,
                                             InterviewInviteAssessorsInviteModelPopulator interviewInviteAssessorsInviteModelPopulator,
                                             InterviewInviteAssessorsAcceptedModelPopulator interviewInviteAssessorsAcceptedModelPopulator) {
        this.interviewInviteRestService = interviewInviteRestService;
        this.interviewInviteAssessorsFindModelPopulator = interviewInviteAssessorsFindModelPopulator;
        this.interviewInviteAssessorsInviteModelPopulator = interviewInviteAssessorsInviteModelPopulator;
        this.interviewInviteAssessorsAcceptedModelPopulator = interviewInviteAssessorsAcceptedModelPopulator;
    }

    protected String getCookieName() {
        return SELECTION_FORM;
    }

    protected Class<InterviewSelectionForm> getFormType() {
        return InterviewSelectionForm.class;
    }

    @GetMapping
    public String assessors(@PathVariable("competitionId") long competitionId) {
        return format("redirect:/competition/%s/assessors/interview-find", competitionId);
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

        String originQuery = buildOriginQueryString(AssessorProfileOrigin.INTERVIEW_FIND, queryParams);
        updateSelectionForm(request, response, competitionId, selectionForm);
        InterviewInviteAssessorsFindViewModel interviewInviteAssessorsFindViewModel = interviewInviteAssessorsFindModelPopulator.populateModel(competitionId, page, originQuery);

        model.addAttribute("model", interviewInviteAssessorsFindViewModel);
        model.addAttribute("originQuery", originQuery);

        return "assessors/interview/assessor-find";
    }

    private void updateSelectionForm(HttpServletRequest request,
                                     HttpServletResponse response,
                                     long competitionId,
                                     InterviewSelectionForm selectionForm) {
        InterviewSelectionForm storedSelectionForm = getSelectionFormFromCookie(request, competitionId).orElse(new InterviewSelectionForm());

        InterviewSelectionForm trimmedAssessorForm = trimSelectionByFilteredResult(storedSelectionForm, competitionId);
        selectionForm.setSelectedAssessorIds(trimmedAssessorForm.getSelectedAssessorIds());
        selectionForm.setAllSelected(trimmedAssessorForm.getAllSelected());

        saveFormToCookie(response, competitionId, selectionForm);
    }

    private InterviewSelectionForm trimSelectionByFilteredResult(InterviewSelectionForm selectionForm,
                                                                 long competitionId) {
        List<Long> filteredResults = getAllAssessorIds(competitionId);
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
    public @ResponseBody JsonNode selectAssessorForInviteList(
            @PathVariable("competitionId") long competitionId,
            @RequestParam("selectionId") long assessorId,
            @RequestParam("isSelected") boolean isSelected,
            HttpServletRequest request,
            HttpServletResponse response) {

        boolean limitExceeded = false;
        try {
            List<Long> assessorIds = getAllAssessorIds(competitionId);
            InterviewSelectionForm selectionForm = getSelectionFormFromCookie(request, competitionId).orElse(new InterviewSelectionForm());
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
            LOG.error("exception thrown selecting assessor for invite list", e);
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
            InterviewSelectionForm selectionForm = getSelectionFormFromCookie(request, competitionId).orElse(new InterviewSelectionForm());

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
            LOG.error("exception thrown adding assessors to invite list", e);
            return createFailureResponse();
        }
    }

    private List<Long> getAllAssessorIds(long competitionId) {
        return interviewInviteRestService.getAvailableAssessorIds(competitionId).getSuccess();
    }

    @PostMapping(value = "/find/addSelected")
    public String addSelectedAssessorsToInviteList(Model model,
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
            RestResult<Void> restResult = interviewInviteRestService.inviteUsers(
                    newSelectionFormToResource(submittedSelectionForm, competitionId));

            return validationHandler.addAnyErrors(restResult)
                    .failNowOrSucceedWith(failureView, () -> {
                        removeCookie(response, competitionId);
                        return redirectToInvite(competitionId, 0);
                    });
        });
    }

    private String redirectToFind(long competitionId, int page, Optional<Long> innovationArea) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/assessment/interview/competition/{competitionId}/assessors/find")
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

        String originQuery = buildOriginQueryString(AssessorProfileOrigin.INTERVIEW_INVITE, queryParams);

        model.addAttribute("model", interviewInviteAssessorsInviteModelPopulator.populateModel(competitionId, page, originQuery));
        model.addAttribute("originQuery", originQuery);

        return "assessors/interview/assessor-invite";
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

    @GetMapping("/accepted")
    public String accepted(Model model,
                           @PathVariable("competitionId") long competitionId,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam MultiValueMap<String, String> queryParams) {
        String originQuery = buildOriginQueryString(AssessorProfileOrigin.INTERVIEW_ACCEPTED, queryParams);

        model.addAttribute("model", interviewInviteAssessorsAcceptedModelPopulator.populateModel(
                competitionId,
                page,
                originQuery
        ));

        return "assessors/interview/assessor-accepted";
    }

    private ServiceResult<Void> deleteInvite(String email, long competitionId) {
        return interviewInviteRestService.deleteInvite(email, competitionId).toServiceResult();
    }

    private ServiceResult<Void> deleteAllInvites(long competitionId) {
        return interviewInviteRestService.deleteAllInvites(competitionId).toServiceResult();
    }

    private String redirectToInvite(long competitionId, int page) {
        return "redirect:" + UriComponentsBuilder.fromPath("/assessment/interview/competition/{competitionId}/assessors/invite")
                .queryParam("page", page)
                .buildAndExpand(asMap("competitionId", competitionId))
                .toUriString();
    }

    private ExistingUserStagedInviteListResource newSelectionFormToResource(InterviewSelectionForm form, long competitionId) {
        return new ExistingUserStagedInviteListResource(simpleMap(
                form.getSelectedAssessorIds(), id -> new ExistingUserStagedInviteResource(id, competitionId)));
    }
}
