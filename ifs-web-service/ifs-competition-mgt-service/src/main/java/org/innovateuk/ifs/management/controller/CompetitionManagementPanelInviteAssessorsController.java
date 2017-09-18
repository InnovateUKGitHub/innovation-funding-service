package org.innovateuk.ifs.management.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.innovateuk.ifs.assessment.service.CompetitionInviteRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.invite.resource.ExistingUserStagedInviteListResource;
import org.innovateuk.ifs.invite.resource.ExistingUserStagedInviteResource;
import org.innovateuk.ifs.invite.resource.NewUserStagedInviteListResource;
import org.innovateuk.ifs.invite.resource.NewUserStagedInviteResource;
import org.innovateuk.ifs.management.controller.CompetitionManagementAssessorProfileController.AssessorProfileOrigin;
import org.innovateuk.ifs.management.form.*;
import org.innovateuk.ifs.management.model.*;
import org.innovateuk.ifs.management.viewmodel.InviteAssessorsFindViewModel;
import org.innovateuk.ifs.management.viewmodel.PanelInviteAssessorsFindViewModel;
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
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.innovateuk.ifs.util.BackLinkUtil.buildOriginQueryString;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.MapFunctions.asMap;

/**
 * This controller will handle all Competition Management requests related to inviting assessors to an Assessment Panel.
 */
@Controller
@RequestMapping("/assessment/panel/competition/{competitionId}/assessors")
@PreAuthorize("hasAnyAuthority('comp_admin','project_finance')")
public class CompetitionManagementPanelInviteAssessorsController extends CompetitionManagementCookieController<AssessorPanelSelectionForm> {

    private static final String SELECTION_FORM = "assessorPanelSelectionForm";

    @Autowired
    private CompetitionInviteRestService competitionInviteRestService;

    @Autowired
    private PanelInviteAssessorsFindModelPopulator panelInviteAssessorsFindModelPopulator;

    @Autowired
    private PanelInviteAssessorsInviteModelPopulator panelInviteAssessorsInviteModelPopulator;

    protected String getCookieName() {
        return "assessorSelectionForm";
    }

    protected Class<AssessorPanelSelectionForm> getFormType() {
        return AssessorPanelSelectionForm.class;
    }

    @GetMapping
    public String assessors(@PathVariable("competitionId") long competitionId) {
        return format("redirect:/competition/%s/assessors/find", competitionId);
    }

    @GetMapping("/find")
    public String find(Model model,
                       @ModelAttribute(name = SELECTION_FORM, binding = false) AssessorPanelSelectionForm selectionForm,
                       @SuppressWarnings("unused") BindingResult bindingResult,
                       @PathVariable("competitionId") long competitionId,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam MultiValueMap<String, String> queryParams,
                       HttpServletRequest request,
                       HttpServletResponse response) {

        String originQuery = buildOriginQueryString(AssessorProfileOrigin.PANEL_FIND, queryParams);
        updateSelectionForm(request, response, competitionId, selectionForm);
        PanelInviteAssessorsFindViewModel panelInviteAssessorsFindViewModel = panelInviteAssessorsFindModelPopulator.populateModel(competitionId, page, originQuery);

        model.addAttribute("model", panelInviteAssessorsFindViewModel);
        model.addAttribute("originQuery", originQuery);

        return "assessors/panel-find";
    }

    private void updateSelectionForm(HttpServletRequest request,
                                     HttpServletResponse response,
                                     long competitionId,
                                     AssessorPanelSelectionForm selectionForm) {
        AssessorPanelSelectionForm storedSelectionForm = getSelectionFormFromCookie(request, competitionId).orElse(new AssessorPanelSelectionForm());

        AssessorPanelSelectionForm trimmedAssessorForm = trimSelectionByFilteredResult(storedSelectionForm, empty(), competitionId);
        selectionForm.setSelectedAssessorIds(trimmedAssessorForm.getSelectedAssessorIds());
        selectionForm.setAllSelected(trimmedAssessorForm.getAllSelected());

        saveFormToCookie(response, competitionId, selectionForm);
    }

    private AssessorPanelSelectionForm trimSelectionByFilteredResult(AssessorPanelSelectionForm selectionForm,
                                                                     Optional<Long> innovationArea,
                                                                     Long competitionId) {
        List<Long> filteredResults = getAllAssessorIds(competitionId, innovationArea);
        AssessorPanelSelectionForm updatedSelectionForm = new AssessorPanelSelectionForm();

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
            List<Long> assessorIds = getAllAssessorIds(competitionId, empty());
            AssessorPanelSelectionForm selectionForm = getSelectionFormFromCookie(request, competitionId).orElse(new AssessorPanelSelectionForm());
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
            AssessorPanelSelectionForm selectionForm = getSelectionFormFromCookie(request, competitionId).orElse(new AssessorPanelSelectionForm());

            if (addAll) {
                selectionForm.setSelectedAssessorIds(getAllAssessorIds(competitionId, empty()));
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

    private List<Long> getAllAssessorIds(long competitionId, Optional<Long> innovationArea) {
        return competitionInviteRestService.getAvailableAssessorIds(competitionId, innovationArea).getSuccessObjectOrThrowException();
    }

    @PostMapping(value = "/find/addSelected")
    public String addSelectedAssessorsToInviteList(Model model,
                                                   @PathVariable("competitionId") long competitionId,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam Optional<Long> innovationArea,
                                                   @ModelAttribute(SELECTION_FORM) AssessorPanelSelectionForm selectionForm,
                                                   ValidationHandler validationHandler,
                                                   HttpServletRequest request,
                                                   HttpServletResponse response) {

        AssessorPanelSelectionForm submittedSelectionForm = getSelectionFormFromCookie(request, competitionId)
                .filter(form -> !form.getSelectedAssessorIds().isEmpty())
                .orElse(selectionForm);
        Supplier<String> failureView = () -> redirectToFind(competitionId, page, innovationArea);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            RestResult<Void> restResult = competitionInviteRestService.inviteUsers(
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

        return "assessors/invite";
    }

    private String redirectToInvite(long competitionId, int page) {
        return "redirect:" + UriComponentsBuilder.fromPath("/competition/{competitionId}/assessors/invite")
                .queryParam("page", page)
                .buildAndExpand(asMap("competitionId", competitionId))
                .toUriString();
    }

    private ExistingUserStagedInviteListResource newSelectionFormToResource(AssessorPanelSelectionForm form, long competitionId) {
        return new ExistingUserStagedInviteListResource(simpleMap(
                form.getSelectedAssessorIds(), id -> new ExistingUserStagedInviteResource(id, competitionId)));
    }
}
