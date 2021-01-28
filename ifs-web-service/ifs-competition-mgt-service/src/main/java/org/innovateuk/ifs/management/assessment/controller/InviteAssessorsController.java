package org.innovateuk.ifs.management.assessment.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.assessment.service.CompetitionInviteRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.invite.resource.ExistingUserStagedInviteListResource;
import org.innovateuk.ifs.invite.resource.ExistingUserStagedInviteResource;
import org.innovateuk.ifs.invite.resource.NewUserStagedInviteListResource;
import org.innovateuk.ifs.invite.resource.NewUserStagedInviteResource;
import org.innovateuk.ifs.management.assessor.form.AssessorSelectionForm;
import org.innovateuk.ifs.management.assessor.form.InviteNewAssessorsForm;
import org.innovateuk.ifs.management.assessor.form.InviteNewAssessorsRowForm;
import org.innovateuk.ifs.management.assessor.populator.CompetitionInviteAssessorsAcceptedModelPopulator;
import org.innovateuk.ifs.management.assessor.populator.CompetitionInviteAssessorsFindModelPopulator;
import org.innovateuk.ifs.management.assessor.populator.CompetitionInviteAssessorsInviteModelPopulator;
import org.innovateuk.ifs.management.assessor.viewmodel.CompetitionInviteAssessorsFindViewModel;
import org.innovateuk.ifs.management.cookie.CompetitionManagementCookieController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.MapFunctions.asMap;

/**
 * This controller will handle all Competition Management requests related to inviting assessors to a Competition.
 */
@Controller
@RequestMapping("/competition/{competitionId}/assessors")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = InviteAssessorsController.class)
@PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionCompositeId', 'ASSESSMENT')")
public class InviteAssessorsController extends CompetitionManagementCookieController<AssessorSelectionForm> {

    private static final Log LOG = LogFactory.getLog(InviteAssessorsController.class);

    private static final String FORM_ATTR_NAME = "form";
    private static final String SELECTION_FORM = "assessorSelectionForm";

    @Autowired
    private CompetitionInviteRestService competitionInviteRestService;

    @Autowired
    private CompetitionInviteAssessorsFindModelPopulator inviteAssessorsFindModelPopulator;

    @Autowired
    private CompetitionInviteAssessorsInviteModelPopulator inviteAssessorsInviteModelPopulator;

    @Autowired
    private CompetitionInviteAssessorsAcceptedModelPopulator inviteAssessorsAcceptedModelPopulator;

    protected String getCookieName() {
        return SELECTION_FORM;
    }

    protected Class<AssessorSelectionForm> getFormType() {
        return AssessorSelectionForm.class;
    }

    @GetMapping
    public String assessors(@PathVariable("competitionId") long competitionId) {
        return format("redirect:/competition/%s/assessors/find", competitionId);
    }

    @GetMapping("/find")
    public String find(Model model,
                       @ModelAttribute(name = SELECTION_FORM, binding = false) AssessorSelectionForm selectionForm,
                       @SuppressWarnings("unused") BindingResult bindingResult,
                       @PathVariable("competitionId") long competitionId,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(value = "assessorNameFilter", required = false) String assessorNameFilter,
                       HttpServletRequest request,
                       HttpServletResponse response) {

        updateSelectionForm(request, response, competitionId, selectionForm, assessorNameFilter);
        CompetitionInviteAssessorsFindViewModel inviteAssessorsFindViewModel = inviteAssessorsFindModelPopulator.populateModel(competitionId, page, assessorNameFilter);

        model.addAttribute("model", inviteAssessorsFindViewModel);

        return "assessors/find";
    }

    private void updateSelectionForm(HttpServletRequest request,
                                     HttpServletResponse response,
                                     long competitionId,
                                     AssessorSelectionForm selectionForm,
                                     String assessorNameFilter) {
        AssessorSelectionForm storedSelectionForm = getSelectionFormFromCookie(request, competitionId).orElse(new AssessorSelectionForm());

        AssessorSelectionForm trimmedAssessorForm = trimSelectionByFilteredResult(storedSelectionForm, assessorNameFilter, competitionId);
        selectionForm.setSelectedAssessorIds(trimmedAssessorForm.getSelectedAssessorIds());
        selectionForm.setAllSelected(trimmedAssessorForm.getAllSelected());

        saveFormToCookie(response, competitionId, selectionForm);
    }

    private AssessorSelectionForm trimSelectionByFilteredResult(AssessorSelectionForm selectionForm,
                                                                String assessorNameFilter,
                                                                Long competitionId) {
        List<Long> filteredResults = getAllAssessorIds(competitionId, assessorNameFilter);
        AssessorSelectionForm updatedSelectionForm = new AssessorSelectionForm();

        selectionForm.getSelectedAssessorIds().retainAll(filteredResults);
        updatedSelectionForm.setSelectedAssessorIds(selectionForm.getSelectedAssessorIds());

        if (updatedSelectionForm.getSelectedAssessorIds().equals(filteredResults) && !updatedSelectionForm.getSelectedAssessorIds().isEmpty()) {
            updatedSelectionForm.setAllSelected(true);
        } else {
            updatedSelectionForm.setAllSelected(false);
        }

        return updatedSelectionForm;
    }

    @PostMapping(value = "/find", params = {"selectionId"})
    public @ResponseBody
    JsonNode selectAssessorForInviteList(
            @PathVariable("competitionId") long competitionId,
            @RequestParam("selectionId") long assessorId,
            @RequestParam("isSelected") boolean isSelected,
            @RequestParam(defaultValue = "", required = false) String assessorNameFilter,
            HttpServletRequest request,
            HttpServletResponse response) {

        boolean limitExceeded = false;
        try {
            List<Long> assessorIds = getAllAssessorIds(competitionId, assessorNameFilter);
            AssessorSelectionForm selectionForm = getSelectionFormFromCookie(request, competitionId).orElse(new AssessorSelectionForm());
            if (isSelected) {
                int predictedSize = selectionForm.getSelectedAssessorIds().size() + 1;
                if (limitIsExceeded(predictedSize)) {
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
    public @ResponseBody
    JsonNode addAllAssessorsToInviteList(Model model,
                                         @PathVariable("competitionId") long competitionId,
                                         @RequestParam("addAll") boolean addAll,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "") String assessorNameFilter,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {
        try {
            AssessorSelectionForm selectionForm = getSelectionFormFromCookie(request, competitionId).orElse(new AssessorSelectionForm());

            if (addAll) {
                selectionForm.setSelectedAssessorIds(getAllAssessorIds(competitionId, assessorNameFilter));
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

    private List<Long> getAllAssessorIds(long competitionId, String assessorNameFilter) {
        return competitionInviteRestService.getAvailableAssessorIds(competitionId, assessorNameFilter).getSuccess();
    }

    @PostMapping(value = "/find/addSelected")
    public String addSelectedAssessorsToInviteList(Model model,
                                                   @PathVariable("competitionId") long competitionId,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "", required = false) String assessorNameFilter,
                                                   @ModelAttribute(SELECTION_FORM) AssessorSelectionForm selectionForm,
                                                   ValidationHandler validationHandler,
                                                   HttpServletRequest request,
                                                   HttpServletResponse response) {

        AssessorSelectionForm submittedSelectionForm = getSelectionFormFromCookie(request, competitionId)
                .filter(form -> !form.getSelectedAssessorIds().isEmpty())
                .orElse(selectionForm);
        Supplier<String> failureView = () -> redirectToFind(competitionId, page);

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

    private String redirectToFind(long competitionId, int page) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/competition/{competitionId}/assessors/find")
                .queryParam("page", page);

        return "redirect:" + builder.buildAndExpand(asMap("competitionId", competitionId))
                .toUriString();
    }

    @GetMapping("/invite")
    public String invite(Model model,
                         @PathVariable("competitionId") long competitionId,
                         @ModelAttribute(name = FORM_ATTR_NAME, binding = false) InviteNewAssessorsForm form,
                         @RequestParam(defaultValue = "0") int page) {
        if (form.getInvites().isEmpty()) {
            form.getInvites().add(new InviteNewAssessorsRowForm());
        }

        model.addAttribute("model", inviteAssessorsInviteModelPopulator.populateModel(competitionId, page));

        return "assessors/invite";
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

    @PostMapping(value = "/invite", params = {"addNewUser"})
    public String addNewUserToInviteView(Model model,
                                         @PathVariable("competitionId") long competitionId,
                                         @RequestParam(defaultValue = "0") int page,
                                         @ModelAttribute(FORM_ATTR_NAME) InviteNewAssessorsForm form) {
        form.getInvites().add(new InviteNewAssessorsRowForm());
        form.setVisible(true);

        return invite(model, competitionId, form, page);
    }

    @PostMapping(value = "/invite", params = {"removeNewUser"})
    public String removeNewUserFromInviteView(Model model,
                                              @PathVariable("competitionId") long competitionId,
                                              @ModelAttribute(FORM_ATTR_NAME) InviteNewAssessorsForm form,
                                              @RequestParam(name = "removeNewUser") int position,
                                              @RequestParam(defaultValue = "0") int page) {
        form.getInvites().remove(position);
        form.setVisible(true);

        return invite(model, competitionId, form, page);
    }

    @PostMapping(value = "/invite", params = {"inviteNewUsers"})
    public String inviteNewUsersFromInviteView(Model model,
                                               @PathVariable("competitionId") long competitionId,
                                               @RequestParam(defaultValue = "0") int page,
                                               @Valid @ModelAttribute(FORM_ATTR_NAME) InviteNewAssessorsForm form,
                                               @SuppressWarnings("unused") BindingResult bindingResult,
                                               ValidationHandler validationHandler) {
        form.setVisible(true);

        return validationHandler.failNowOrSucceedWith(
                () -> invite(model, competitionId, form, page),
                () -> {
                    RestResult<Void> restResult = competitionInviteRestService.inviteNewUsers(
                            newInviteFormToResource(form, competitionId), competitionId
                    );

                    return validationHandler.addAnyErrors(restResult)
                            .failNowOrSucceedWith(
                                    () -> invite(model, competitionId, form, page),
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

    @GetMapping("/accepted")
    public String accepted(Model model,
                           @PathVariable("competitionId") long competitionId,
                           @RequestParam(defaultValue = "0") int page) {

        model.addAttribute("model", inviteAssessorsAcceptedModelPopulator.populateModel(
                competitionId,
                page
        ));

        return "assessors/accepted";
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
        return new ExistingUserStagedInviteListResource(simpleMap(
                form.getSelectedAssessorIds(), id -> new ExistingUserStagedInviteResource(id, competitionId)));
    }
}
