package org.innovateuk.ifs.management.supporters.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.supporter.resource.AssignSupportersResource;
import org.innovateuk.ifs.supporter.resource.SupporterUserResource;
import org.innovateuk.ifs.supporter.resource.SupportersAvailableForApplicationPageResource;
import org.innovateuk.ifs.supporter.service.SupporterAssignmentRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.management.supporters.form.SupporterSelectionForm;
import org.innovateuk.ifs.management.supporters.populator.AssignSupportersViewModelPopulator;
import org.innovateuk.ifs.management.supporters.viewmodel.AssignSupportersViewModel;
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
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.innovateuk.ifs.util.MapFunctions.asMap;

@Controller
@RequestMapping("/competition/{competitionId}/supporters/assign/{applicationId}")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = AssignSupportersController.class)
@PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionCompositeId', 'ASSIGN_SUPPORTERS')")
public class AssignSupportersController extends CompetitionManagementCookieController<SupporterSelectionForm> {

    private static final Log LOG = LogFactory.getLog(AssignSupportersController.class);

    private static final String SELECTION_FORM = "supporterSelectionForm";

    @Autowired
    private AssignSupportersViewModelPopulator assignSupportersViewModelPopulator;

    @Autowired
    private SupporterAssignmentRestService supporterAssignmentRestService;

    @Override
    protected String getCookieName() {
        return SELECTION_FORM;
    }

    @Override
    protected Class<SupporterSelectionForm> getFormType() {
        return SupporterSelectionForm.class;
    }

    @GetMapping
    public String supporters(@PathVariable("competitionId") long competitionId,
                            @PathVariable("applicationId") long applicationId) {
        return format("redirect:/competition/%s/supporters/assign/%s/find", competitionId, applicationId);
    }

    @GetMapping("/find")
    public String find(Model model,
                       @ModelAttribute(name = SELECTION_FORM, binding = false) SupporterSelectionForm selectionForm,
                       @SuppressWarnings("unused") BindingResult bindingResult,
                       @PathVariable("competitionId") long competitionId,
                       @PathVariable("applicationId") long applicationId,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(value = "filter", required = false) String filter,
                       HttpServletRequest request,
                       HttpServletResponse response) {

        updateSelectionForm(request, response, applicationId, selectionForm, filter);
        AssignSupportersViewModel assignSupportersViewModel = assignSupportersViewModelPopulator.populateModel(competitionId, applicationId, filter, page);

        model.addAttribute("model", assignSupportersViewModel);

        return "supporters/assign";
    }

    private void updateSelectionForm(HttpServletRequest request,
                                     HttpServletResponse response,
                                     long applicationId,
                                     SupporterSelectionForm selectionForm,
                                     String filter) {
        SupporterSelectionForm storedSelectionForm = getSelectionFormFromCookie(request, applicationId).orElse(new SupporterSelectionForm());

        SupporterSelectionForm trimmedSupporterForm = trimSelectionByFilteredResult(storedSelectionForm, filter, applicationId);
        selectionForm.setSelectedSupporterIds(trimmedSupporterForm.getSelectedSupporterIds());
        selectionForm.setAllSelected(trimmedSupporterForm.isAllSelected());

        saveFormToCookie(response, applicationId, selectionForm);
    }

    private SupporterSelectionForm trimSelectionByFilteredResult(SupporterSelectionForm selectionForm,
                                                                String filter,
                                                                Long applicationId) {
        List<Long> filteredResults = getAllSupporterIds(applicationId, filter);
        SupporterSelectionForm updatedSelectionForm = new SupporterSelectionForm();

        selectionForm.getSelectedSupporterIds().retainAll(filteredResults);
        updatedSelectionForm.setSelectedSupporterIds(selectionForm.getSelectedSupporterIds());

        if (updatedSelectionForm.getSelectedSupporterIds().equals(filteredResults) && !updatedSelectionForm.getSelectedSupporterIds().isEmpty()) {
            updatedSelectionForm.setAllSelected(true);
        } else {
            updatedSelectionForm.setAllSelected(false);
        }

        return updatedSelectionForm;
    }

    @PostMapping(value = "/find", params = {"selectionId"})
    public @ResponseBody
    JsonNode selectSupporterForInviteList(
            @PathVariable("competitionId") long competitionId,
            @PathVariable("applicationId") long applicationId,
            @RequestParam("selectionId") long supporterId,
            @RequestParam("isSelected") boolean isSelected,
            @RequestParam(defaultValue = "", required = false) String filter,
            HttpServletRequest request,
            HttpServletResponse response) {

        boolean limitExceeded = false;
        try {
            List<Long> supporterIds = getAllSupporterIds(applicationId, filter);
            SupporterSelectionForm selectionForm = getSelectionFormFromCookie(request, applicationId).orElse(new SupporterSelectionForm());
            if (isSelected) {
                int predictedSize = selectionForm.getSelectedSupporterIds().size() + 1;
                if (limitIsExceeded(predictedSize)) {
                    limitExceeded = true;
                } else {
                    selectionForm.getSelectedSupporterIds().add(supporterId);
                    if (selectionForm.getSelectedSupporterIds().containsAll(supporterIds)) {
                        selectionForm.setAllSelected(true);
                    }
                }
            } else {
                selectionForm.getSelectedSupporterIds().remove(supporterId);
                selectionForm.setAllSelected(false);
            }
            saveFormToCookie(response, applicationId, selectionForm);
            return createJsonObjectNode(selectionForm.getSelectedSupporterIds().size(), selectionForm.isAllSelected(), limitExceeded);
        } catch (Exception e) {
            LOG.error("exception thrown selecting supporters for list", e);
            return createFailureResponse();
        }
    }

    @PostMapping(value = "/find", params = {"addAll"})
    public @ResponseBody
    JsonNode addAllSupportersToInviteList(Model model,
                                         @PathVariable("competitionId") long competitionId,
                                         @PathVariable("applicationId") long applicationId,
                                         @RequestParam("addAll") boolean addAll,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "") String filter,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {
        try {
            SupporterSelectionForm selectionForm = getSelectionFormFromCookie(request, applicationId).orElse(new SupporterSelectionForm());

            if (addAll) {
                selectionForm.setSelectedSupporterIds(getAllSupporterIds(applicationId, filter));
                selectionForm.setAllSelected(true);
            } else {
                selectionForm.getSelectedSupporterIds().clear();
                selectionForm.setAllSelected(false);
            }

            saveFormToCookie(response, applicationId, selectionForm);

            return createSuccessfulResponseWithSelectionStatus(selectionForm.getSelectedSupporterIds().size(), selectionForm.isAllSelected(), false);
        } catch (Exception e) {
            LOG.error("exception thrown adding supporters to list", e);

            return createFailureResponse();
        }
    }

    private List<Long> getAllSupporterIds(long applicationId, String filter) {
        return supporterAssignmentRestService.findAllAvailableSupporterUserIdsForApplication(applicationId, filter).getSuccess();
    }

    @PostMapping(value = "/find/addSelected")
    public String addSelectedSupportersToInviteList(Model model,
                                                   @PathVariable("competitionId") long competitionId,
                                                   @PathVariable("applicationId") long applicationId,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "", required = false) String filter,
                                                   @ModelAttribute(SELECTION_FORM) SupporterSelectionForm selectionForm,
                                                   ValidationHandler validationHandler,
                                                   HttpServletRequest request,
                                                   HttpServletResponse response) {

        SupporterSelectionForm submittedSelectionForm = getSelectionFormFromCookie(request, applicationId)
                .filter(form -> !form.getSelectedSupporterIds().isEmpty())
                .orElse(selectionForm);
        Supplier<String> failureView = () -> redirectToFind(competitionId, applicationId, page);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            RestResult<Void> restResult = supporterAssignmentRestService.assign(
                    newSelectionFormToResource(submittedSelectionForm, applicationId));

            return validationHandler.addAnyErrors(restResult)
                    .failNowOrSucceedWith(failureView, () -> {
                        removeCookie(response, competitionId);
                        return redirectToFind(competitionId, applicationId, 0);
                    });
        });
    }

    private AssignSupportersResource newSelectionFormToResource(SupporterSelectionForm form, long applicationId) {
        AssignSupportersResource resource = new AssignSupportersResource();
        resource.setApplicationId(applicationId);
        resource.setSupporterIds(form.getSelectedSupporterIds());
        return resource;
    }

    private String redirectToFind(long competitionId, long applicationId, int page) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/competition/{competitionId}/supporters/assign/{applicationId}/find")
                .queryParam("page", page);

        return "redirect:" + builder.buildAndExpand(asMap("competitionId", competitionId, "applicationId", applicationId))
                .toUriString();
    }

    @PostMapping("/find/remove")
    public String removeSupporter(@RequestParam("userId") long userId, @PathVariable("competitionId") long competitionId,
                                 @PathVariable("applicationId") long applicationId) {
        supporterAssignmentRestService.removeAssignment(userId, applicationId);
        return String.format("redirect:/competition/%d/supporters/assign/%d/find", competitionId, applicationId);
    }

}
