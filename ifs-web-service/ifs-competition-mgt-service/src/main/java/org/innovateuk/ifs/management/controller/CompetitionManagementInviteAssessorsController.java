package org.innovateuk.ifs.management.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.innovateuk.ifs.assessment.service.CompetitionInviteRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.invite.resource.*;
import org.innovateuk.ifs.management.controller.CompetitionManagementAssessorProfileController.AssessorProfileOrigin;
import org.innovateuk.ifs.management.form.*;
import org.innovateuk.ifs.management.model.InviteAssessorsFindModelPopulator;
import org.innovateuk.ifs.management.model.InviteAssessorsInviteModelPopulator;
import org.innovateuk.ifs.management.model.InviteAssessorsOverviewModelPopulator;
import org.innovateuk.ifs.management.viewmodel.InviteAssessorsFindViewModel;
import org.innovateuk.ifs.util.CookieUtil;
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
import static java.util.Optional.of;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.innovateuk.ifs.util.BackLinkUtil.buildOriginQueryString;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.JsonUtil.getObjectFromJson;
import static org.innovateuk.ifs.util.JsonUtil.getSerializedObject;
import static org.innovateuk.ifs.util.MapFunctions.asMap;

/**
 * This controller will handle all Competition Management requests related to inviting assessors to a Competition.
 */
@Controller
@RequestMapping("/competition/{competitionId}/assessors")
@PreAuthorize("hasAnyAuthority('comp_admin','project_finance')")
public class CompetitionManagementInviteAssessorsController {

    private static final String FILTER_FORM_ATTR_NAME = "filterForm";
    private static final String FORM_ATTR_NAME = "form";
    private static final String SELECTION_FORM = "assessorSelectionForm";

    public static final int SELECTION_LIMIT = 5;

    @Autowired
    private CompetitionInviteRestService competitionInviteRestService;

    @Autowired
    private InviteAssessorsFindModelPopulator inviteAssessorsFindModelPopulator;

    @Autowired
    private InviteAssessorsInviteModelPopulator inviteAssessorsInviteModelPopulator;

    @Autowired
    private InviteAssessorsOverviewModelPopulator inviteAssessorsOverviewModelPopulator;

    @Autowired
    private CookieUtil cookieUtil;

    @GetMapping
    public String assessors(@PathVariable("competitionId") long competitionId) {
        return format("redirect:/competition/%s/assessors/find", competitionId);
    }

    @GetMapping("/find")
    public String find(Model model,
                       @Valid @ModelAttribute(FILTER_FORM_ATTR_NAME) FindAssessorsFilterForm filterForm,
                       @ModelAttribute(name = SELECTION_FORM, binding = false) AssessorSelectionForm selectionForm,
                       @SuppressWarnings("unused") BindingResult bindingResult,
                       @PathVariable("competitionId") long competitionId,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(value = "clearFilter", defaultValue = "false") boolean clearFilter,
                       @RequestParam MultiValueMap<String, String> queryParams,
                       HttpServletRequest request,
                       HttpServletResponse response) {

        String originQuery = buildOriginQueryString(AssessorProfileOrigin.ASSESSOR_FIND, queryParams);
        updateSelectionForm(request, response, competitionId, selectionForm, filterForm, clearFilter);
        InviteAssessorsFindViewModel inviteAssessorsFindViewModel = inviteAssessorsFindModelPopulator.populateModel(competitionId, page, filterForm.getInnovationArea(), originQuery);

        model.addAttribute("model", inviteAssessorsFindViewModel);
        model.addAttribute("originQuery", originQuery);

        return "assessors/find";
    }

    private void updateSelectionForm(HttpServletRequest request,
                                     HttpServletResponse response,
                                     long competitionId,
                                     AssessorSelectionForm selectionForm,
                                     FindAssessorsFilterForm filterForm,
                                     boolean clearFilter) {
        AssessorSelectionForm storedSelectionForm = getAssessorSelectionFormFromCookie(request, competitionId).orElse(new AssessorSelectionForm());
        selectionForm.setAllSelected(storedSelectionForm.getAllSelected());
        selectionForm.setSelectedAssessorIds(storedSelectionForm.getSelectedAssessorIds());
        if (storedSelectionForm.getSelectedInnovationArea() != null && !clearFilter) {
            filterForm.setInnovationArea(of(storedSelectionForm.getSelectedInnovationArea()));
        }

        if (selectionForm.getAllSelected() && !clearFilter) {
            selectionForm.setSelectedAssessorIds(getAllAssessorIds(competitionId, filterForm.getInnovationArea()));
        } else {
            selectionForm.getSelectedAssessorIds().retainAll(getAllAssessorIds(competitionId, filterForm.getInnovationArea()));
            selectionForm.setAllSelected(false);
        }
        filterForm.getInnovationArea().ifPresent(selectionForm::setSelectedInnovationArea);
        cookieUtil.saveToCompressedCookie(response, format("%s_comp_%s", SELECTION_FORM, competitionId), getSerializedObject(selectionForm));
    }

    @PostMapping(value = "/find", params = {"selectionId"})
    public @ResponseBody JsonNode selectAssessorForInviteList(
            @PathVariable("competitionId") long competitionId,
            @RequestParam("selectionId") long assessorId,
            @RequestParam("isSelected") boolean isSelected,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam Optional<Long> innovationArea,
            HttpServletRequest request,
            HttpServletResponse response) {

        boolean limitExceeded = false;
        try {
            List<Long> assessorIds = getAllAssessorIds(competitionId, innovationArea);
            AssessorSelectionForm selectionForm = getAssessorSelectionFormFromCookie(request, competitionId).orElse(new AssessorSelectionForm());
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
            cookieUtil.saveToCompressedCookie(response, format("%s_comp_%s", SELECTION_FORM, competitionId), getSerializedObject(selectionForm));
            return createJsonObjectNode(selectionForm.getSelectedAssessorIds().size(), selectionForm.getAllSelected(), limitExceeded);
        } catch (Exception e) {
            return createFailureResponse();
        }
    }

    @PostMapping(value = "/find", params = {"addAll"})
    public @ResponseBody JsonNode addAllAssessorsToInviteList(Model model,
                                              @PathVariable("competitionId") long competitionId,
                                              @RequestParam("addAll") boolean addAll,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam Optional<Long> innovationArea,
                                              HttpServletRequest request,
                                              HttpServletResponse response) {
        try {
            AssessorSelectionForm selectionForm = getAssessorSelectionFormFromCookie(request, competitionId).orElse(new AssessorSelectionForm());

            if (addAll) {
                selectionForm.setSelectedAssessorIds(getAllAssessorIds(competitionId, innovationArea));
                selectionForm.setAllSelected(true);
            } else {
                selectionForm.getSelectedAssessorIds().clear();
                selectionForm.setAllSelected(false);
            }

            cookieUtil.saveToCompressedCookie(response, format("%s_comp_%s", SELECTION_FORM, competitionId), getSerializedObject(selectionForm));
            return createSuccessfulResponseWithSelectionStatus(selectionForm.getSelectedAssessorIds().size(), selectionForm.getAllSelected(), false);
        } catch (Exception e) {
            return createFailureResponse();
        }
    }

    private List<Long> getAllAssessorIds(long competitionId, Optional<Long> innovationArea) {
        List<AvailableAssessorResource> resources =
                competitionInviteRestService.getAvailableAssessors(competitionId, innovationArea).getSuccessObjectOrThrowException();
        return simpleMap(resources, AvailableAssessorResource::getId);
    }

    @PostMapping(value = "/find/addSelected")
    public String addSelectedAssessorsToInviteList(Model model,
                                                   @PathVariable("competitionId") long competitionId,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam Optional<Long> innovationArea,
                                                   @ModelAttribute(SELECTION_FORM) AssessorSelectionForm selectionForm,
                                                   ValidationHandler validationHandler,
                                                   HttpServletRequest request,
                                                   HttpServletResponse response) {

        AssessorSelectionForm submittedSelectionForm = getAssessorSelectionFormFromCookie(request, competitionId)
                .filter(form -> !form.getSelectedAssessorIds().isEmpty())
                .orElse(selectionForm);
        Supplier<String> failureView = () -> redirectToFind(competitionId, page, innovationArea);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            RestResult<Void> restResult = competitionInviteRestService.inviteUsers(
                    newSelectionFormToResource(submittedSelectionForm, competitionId));

            return validationHandler.addAnyErrors(restResult)
                                    .failNowOrSucceedWith(failureView, () -> {
                cookieUtil.removeCookie(response, format("%s_comp_%s", SELECTION_FORM, competitionId));
                return redirectToInvite(competitionId, 0);
            });
        });
    }

    private String redirectToFind(long competitionId, int page, Optional<Long> innovationArea) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/competition/{competitionId}/assessors/find")
                .queryParam("page", page);

        innovationArea.ifPresent(innovationAreaId -> builder.queryParam("innovationArea", innovationAreaId));

        return "redirect:" + builder.buildAndExpand(asMap("competitionId", competitionId))
                .toUriString();
    }

    @GetMapping("/invite")
    public String invite(Model model,
                         @PathVariable("competitionId") long competitionId,
                         @ModelAttribute(name = FORM_ATTR_NAME, binding = false) InviteNewAssessorsForm form,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam MultiValueMap<String, String> queryParams) {
        if (form.getInvites().isEmpty()) {
            form.getInvites().add(new InviteNewAssessorsRowForm());
        }

        String originQuery = buildOriginQueryString(AssessorProfileOrigin.ASSESSOR_INVITE, queryParams);

        model.addAttribute("model", inviteAssessorsInviteModelPopulator.populateModel(competitionId, page, originQuery));
        model.addAttribute("originQuery", originQuery);

        return "assessors/invite";
    }

    @PostMapping(value = "/invite", params = {"remove"})
    public String removeInviteFromInviteView(Model model,
                                             @PathVariable("competitionId") long competitionId,
                                             @RequestParam(name = "remove") String email,
                                             @RequestParam(defaultValue = "0") int page,
                                             @SuppressWarnings("unused") @ModelAttribute(FORM_ATTR_NAME) InviteNewAssessorsForm form) {
        deleteInvite(email, competitionId).getSuccessObjectOrThrowException();
        return redirectToInvite(competitionId, page);
    }

    @PostMapping(value = "/invite", params = {"removeAll"})
    public String removeAllInvitesFromInviteView(Model model,
                                                 @PathVariable("competitionId") long competitionId,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @SuppressWarnings("unused") @ModelAttribute(FORM_ATTR_NAME) InviteNewAssessorsForm form) {
        deleteAllInvites(competitionId).getSuccessObjectOrThrowException();
        return redirectToInvite(competitionId, page);
    }

    @PostMapping(value = "/invite", params = {"addNewUser"})
    public String addNewUserToInviteView(Model model,
                                         @PathVariable("competitionId") long competitionId,
                                         @RequestParam(defaultValue = "0") int page,
                                         @ModelAttribute(FORM_ATTR_NAME) InviteNewAssessorsForm form,
                                         @RequestParam MultiValueMap<String, String> queryParams) {
        form.getInvites().add(new InviteNewAssessorsRowForm());
        form.setVisible(true);

        return invite(model, competitionId, form, page, queryParams);
    }

    @PostMapping(value = "/invite", params = {"removeNewUser"})
    public String removeNewUserFromInviteView(Model model,
                                              @PathVariable("competitionId") long competitionId,
                                              @ModelAttribute(FORM_ATTR_NAME) InviteNewAssessorsForm form,
                                              @RequestParam(name = "removeNewUser") int position,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam MultiValueMap<String, String> queryParams) {
        form.getInvites().remove(position);
        form.setVisible(true);

        return invite(model, competitionId, form, page, queryParams);
    }

    @PostMapping(value = "/invite", params = {"inviteNewUsers"})
    public String inviteNewUsersFromInviteView(Model model,
                                               @PathVariable("competitionId") long competitionId,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam MultiValueMap<String, String> queryParams,
                                               @Valid @ModelAttribute(FORM_ATTR_NAME) InviteNewAssessorsForm form,
                                               @SuppressWarnings("unused") BindingResult bindingResult,
                                               ValidationHandler validationHandler) {
        form.setVisible(true);

        return validationHandler.failNowOrSucceedWith(
                () -> invite(model, competitionId, form, page, queryParams),
                () -> {
                    RestResult<Void> restResult = competitionInviteRestService.inviteNewUsers(
                            newInviteFormToResource(form, competitionId), competitionId
                    );

                    return validationHandler.addAnyErrors(restResult)
                            .failNowOrSucceedWith(
                                    () -> invite(model, competitionId, form, page, queryParams),
                                    () -> redirectToInvite(competitionId, page)
                            );
                }
        );
    }

    private String redirectToInvite(long competitionId, int page) {
        return "redirect:" + UriComponentsBuilder.fromPath("/competition/{competitionId}/assessors/invite")
                .queryParam("page", page)
                .buildAndExpand(asMap("competitionId", competitionId))
                .toUriString();
    }

    @GetMapping("/overview")
    public String overview(Model model,
                           @Valid @ModelAttribute(FILTER_FORM_ATTR_NAME) OverviewAssessorsFilterForm filterForm,
                           @PathVariable("competitionId") long competitionId,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam MultiValueMap<String, String> queryParams) {
        String originQuery = buildOriginQueryString(AssessorProfileOrigin.ASSESSOR_OVERVIEW, queryParams);

        model.addAttribute("model", inviteAssessorsOverviewModelPopulator.populateModel(
                competitionId,
                page,
                filterForm.getInnovationArea(),
                filterForm.getStatus(),
                filterForm.getCompliant(),
                originQuery
        ));
        model.addAttribute("originQuery", originQuery);

        return "assessors/overview";
    }

    private ServiceResult<Void> deleteInvite(String email, long competitionId) {
        return competitionInviteRestService.deleteInvite(email, competitionId).toServiceResult();
    }

    private ServiceResult<Void> deleteAllInvites(long competitionId) {
        return competitionInviteRestService.deleteAllInvites(competitionId).toServiceResult();
    }

    private NewUserStagedInviteListResource newInviteFormToResource(InviteNewAssessorsForm form, long competitionId) {
        List<NewUserStagedInviteResource> invites = form.getInvites().stream()
                .map(newUserInvite -> new NewUserStagedInviteResource(
                        newUserInvite.getEmail(),
                        competitionId,
                        newUserInvite.getName(),
                        form.getSelectedInnovationArea()
                ))
                .collect(Collectors.toList());

        return new NewUserStagedInviteListResource(invites);
    }

    private ExistingUserStagedInviteListResource newSelectionFormToResource(AssessorSelectionForm form, long competitionId) {
        List<ExistingUserStagedInviteResource> invites = form.getSelectedAssessorIds().stream()
                .map(id -> new ExistingUserStagedInviteResource(
                        id,
                        competitionId
                ))
                .collect(Collectors.toList());

        return new ExistingUserStagedInviteListResource(invites);
    }

    private ObjectNode createFailureResponse() {
        return createJsonObjectNode(-1, false, false);
    }

    private ObjectNode createSuccessfulResponseWithSelectionStatus(int selectionCount, boolean allSelected, boolean limitExceeded) {
        return createJsonObjectNode(selectionCount, allSelected, limitExceeded);
    }

    private ObjectNode createJsonObjectNode(int selectionCount, boolean allSelected, boolean limitExceeded) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();

        node.put("selectionCount", selectionCount);
        node.put("allSelected", allSelected);
        node.put("limitExceeded", limitExceeded);

        return node;
    }

    private Optional<AssessorSelectionForm> getAssessorSelectionFormFromCookie(HttpServletRequest request, long competitionId) {
        String assessorFormJson = cookieUtil.getCompressedCookieValue(request, format("%s_comp_%s", SELECTION_FORM, competitionId));
        if (isNotBlank(assessorFormJson)) {
            return Optional.ofNullable(getObjectFromJson(assessorFormJson, AssessorSelectionForm.class));
        } else {
            return Optional.empty();
        }
    }

    private List<Long> limitList(List<Long> allIds) {
        return allIds.subList(0, SELECTION_LIMIT);
    }

    private boolean limitIsExceeded(long amountOfIds) {
        return amountOfIds > SELECTION_LIMIT;
    }
}
