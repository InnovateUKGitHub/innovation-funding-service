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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;
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
    private static final String SELECTION_FORM = "selectionForm";

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
                       @RequestParam MultiValueMap<String, String> queryParams,
                       HttpServletRequest request,
                       HttpServletResponse response) {

        String originQuery = buildOriginQueryString(AssessorProfileOrigin.ASSESSOR_FIND, queryParams);
        InviteAssessorsFindViewModel inviteAssessorsFindViewModel = inviteAssessorsFindModelPopulator.populateModel(competitionId, page, filterForm.getInnovationArea(), originQuery);

        model.addAttribute("model", inviteAssessorsFindViewModel);
        model.addAttribute("originQuery", originQuery);

        AssessorSelectionForm storedSelectionForm = getAssessorSelectionFormFromCookie(request).orElse(new AssessorSelectionForm());
        selectionForm.setAllSelected(storedSelectionForm.getAllSelected());
        selectionForm.setAssessorEmails(storedSelectionForm.getAssessorEmails());
        if (selectionForm.getAllSelected()) {
            selectionForm.setAssessorEmails(getAllAssessorEmails(competitionId, page, filterForm.getInnovationArea()));
        } else {
            selectionForm.getAssessorEmails().retainAll(getAllAssessorEmails(competitionId, page, filterForm.getInnovationArea()));
        }
        cookieUtil.saveToCookie(response, SELECTION_FORM, getSerializedObject(selectionForm));

        return "assessors/find";
    }

    // add/remove assessors via AJAX call
    @PostMapping(value = "/find", params = {"assessor"})
    public @ResponseBody JsonNode selectAssessorForInviteList(
            @PathVariable("competitionId") long competitionId,
            @RequestParam("assessor") String email,
            @RequestParam("isSelected") boolean isSelected,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam Optional<Long> innovationArea,
            HttpServletRequest request,
            HttpServletResponse response) {
        try {
            AssessorSelectionForm selectionForm = getAssessorSelectionFormFromCookie(request).orElse(new AssessorSelectionForm());
            if (isSelected) {
                selectionForm.getAssessorEmails().add(email);
            } else {
                selectionForm.getAssessorEmails().remove(email);
                selectionForm.setAllSelected(false);
            }
            cookieUtil.saveToCookie(response, SELECTION_FORM, getSerializedObject(selectionForm));
            return createJsonObjectNode(true);
        } catch (Exception e) {
            return createJsonObjectNode(false);
        }
    }

    // add/remove all assessors via AJAX call
    @PostMapping(value = "/find", params = {"addAll"})
    public @ResponseBody JsonNode addAllAssessorsToInviteList(Model model,
                                              @PathVariable("competitionId") long competitionId,
                                              @RequestParam("addAll") boolean addAll,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam Optional<Long> innovationArea,
                                              HttpServletRequest request,
                                              HttpServletResponse response) {

        try {
            AssessorSelectionForm selectionForm = getAssessorSelectionFormFromCookie(request).orElse(new AssessorSelectionForm());

            if (addAll) {
                selectionForm.setAssessorEmails(getAllAssessorEmails(competitionId, innovationArea));
                selectionForm.setAllSelected(true);
            } else {
                selectionForm.getAssessorEmails().clear();
                selectionForm.setAllSelected(false);
            }

            cookieUtil.saveToCookie(response, SELECTION_FORM, getSerializedObject(selectionForm));
            return createJsonObjectNode(true);
        } catch (Exception e) {
            return createJsonObjectNode(false);
        }
    }

    private List<String> getAllAssessorEmails(long competitionId, int currentPage, Optional<Long> innovationArea) {
        AvailableAssessorPageResource pageResource = competitionInviteRestService.getAvailableAssessors(competitionId, currentPage, innovationArea)
                .getSuccessObjectOrThrowException();
        List<String> allAssessors = new ArrayList<>();

        IntStream.range(0, pageResource.getTotalPages()).forEach( page -> {
            AvailableAssessorPageResource currentResource = competitionInviteRestService.getAvailableAssessors(competitionId, page, innovationArea)
                    .getSuccessObjectOrThrowException();
            allAssessors.addAll(simpleMap(currentResource.getContent(), AvailableAssessorResource::getEmail));
        });

        return allAssessors;
    }

    private List<String> getAllAssessorEmails(long competitionId, Optional<Long> innovationArea) {
        List<AvailableAssessorResource> pageResource =
                competitionInviteRestService.getAvailableAssessors(competitionId, innovationArea).getSuccessObjectOrThrowException();
        return simpleMap(pageResource, AvailableAssessorResource::getEmail);
    }

    // proposed for non-js call from 'add all' checkbox
    @PostMapping(value = "/find/addAll")
    public String addAllAssessorsFromCurrentPageToInviteList(Model model,
                                        @PathVariable("competitionId") long competitionId,
                                        @RequestParam("addAll") boolean email,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam Optional<Long> innovationArea,
                                        HttpServletRequest request,
                                        HttpServletResponse response) {

        AssessorSelectionForm selectionForm = getAssessorSelectionFormFromCookie(request).orElse(new AssessorSelectionForm());
        AvailableAssessorPageResource pageResource = competitionInviteRestService.getAvailableAssessors(competitionId, page, innovationArea)
                .getSuccessObjectOrThrowException();

        List<String> assessorEmails = simpleMap(pageResource.getContent(), AvailableAssessorResource::getEmail);
        selectionForm.setAssessorEmails(assessorEmails);
        cookieUtil.saveToCookie(response, SELECTION_FORM, getSerializedObject(selectionForm));
        return redirectToFind(competitionId, page, innovationArea);
    }

    // proposed for non-js call from 'add all' checkbox
    @PostMapping(value = "/find/removeAll")
    public String removeAllAssessorsFromFromCurrentPageInviteList(Model model,
                                             @PathVariable("competitionId") long competitionId,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam Optional<Long> innovationArea,
                                             HttpServletRequest request,
                                             HttpServletResponse response) {

        AssessorSelectionForm selectionForm = getAssessorSelectionFormFromCookie(request).orElse(new AssessorSelectionForm());
        selectionForm.getAssessorEmails().clear();
        cookieUtil.saveToCookie(response, SELECTION_FORM, getSerializedObject(selectionForm));
        return redirectToFind(competitionId, page, innovationArea);
    }

    // Invite all selected users
    @PostMapping(value = "/find/addSelected")
    public String addSelectedAssessorsToInviteList(Model model,
                                                   @PathVariable("competitionId") long competitionId,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam Optional<Long> innovationArea,
                                                   @ModelAttribute(SELECTION_FORM) AssessorSelectionForm selectionForm,
                                                   ValidationHandler validationHandler,
                                                   HttpServletRequest request,
                                                   HttpServletResponse response) {

        AssessorSelectionForm submittedSelectionForm = getAssessorSelectionFormFromCookie(request)
                .filter(form -> !form.getAssessorEmails().isEmpty())
                .orElse(selectionForm);
        Supplier<String> failureView = () -> redirectToFind(competitionId, page, innovationArea);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            // Could be possibly be optimised in similar fashion to INFUND-4105
            submittedSelectionForm.getAssessorEmails().stream().forEach(email -> {
                ServiceResult<CompetitionInviteResource> updateResult = competitionInviteRestService.inviteUser(new ExistingUserStagedInviteResource(email, competitionId)).toServiceResult();
                validationHandler.addAnyErrors(updateResult, fieldErrorsToFieldErrors(), asGlobalErrors());
            });

            return validationHandler.failNowOrSucceedWith(failureView, () -> {
                cookieUtil.removeCookie(response, SELECTION_FORM);
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
                                             @ModelAttribute(FORM_ATTR_NAME) InviteNewAssessorsForm form) {
        deleteInvite(email, competitionId);
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
    public String inviteNewsUsersFromInviteView(Model model,
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

    private ServiceResult<CompetitionInviteResource> inviteUser(String email, long competitionId) {
        return competitionInviteRestService.inviteUser(new ExistingUserStagedInviteResource(email, competitionId)).toServiceResult();
    }

    private ServiceResult<Void> deleteInvite(String email, long competitionId) {
        return competitionInviteRestService.deleteInvite(email, competitionId).toServiceResult();
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

    private ObjectNode createJsonObjectNode(boolean success) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("success", success ? "true" : "false");

        return node;
    }

    private Optional<AssessorSelectionForm> getAssessorSelectionFormFromCookie(HttpServletRequest request) {
        String organisationFormJson = cookieUtil.getCookieValue(request, SELECTION_FORM);
        if (isNotBlank(organisationFormJson)) {
            return Optional.ofNullable(getObjectFromJson(organisationFormJson, AssessorSelectionForm.class));
        } else {
            return Optional.empty();
        }
    }
}
