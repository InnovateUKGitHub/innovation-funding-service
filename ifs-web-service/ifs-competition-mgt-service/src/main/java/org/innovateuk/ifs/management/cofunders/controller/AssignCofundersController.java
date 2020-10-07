package org.innovateuk.ifs.management.cofunders.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.cofunder.resource.AssignCofundersResource;
import org.innovateuk.ifs.cofunder.resource.CofunderUserResource;
import org.innovateuk.ifs.cofunder.resource.CofundersAvailableForApplicationPageResource;
import org.innovateuk.ifs.cofunder.service.CofunderAssignmentRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.management.cofunders.form.CofunderSelectionForm;
import org.innovateuk.ifs.management.cofunders.populator.AssignCofundersViewModelPopulator;
import org.innovateuk.ifs.management.cofunders.viewmodel.AssignCofundersViewModel;
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
@RequestMapping("/competition/{competitionId}/cofunders/assign/{applicationId}")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = AssignCofundersController.class)
@PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionCompositeId', 'COFUNDERS')")
public class AssignCofundersController extends CompetitionManagementCookieController<CofunderSelectionForm> {

    private static final Log LOG = LogFactory.getLog(AssignCofundersController.class);

    private static final String SELECTION_FORM = "cofunderSelectionForm";

    @Autowired
    private AssignCofundersViewModelPopulator assignCofundersViewModelPopulator;

    @Autowired
    private CofunderAssignmentRestService cofunderAssignmentRestService;

    @Override
    protected String getCookieName() {
        return SELECTION_FORM;
    }

    @Override
    protected Class<CofunderSelectionForm> getFormType() {
        return CofunderSelectionForm.class;
    }

    @GetMapping
    public String cofunders(@PathVariable("competitionId") long competitionId,
                            @PathVariable("applicationId") long applicationId) {
        return format("redirect:/competition/%s/cofunders/assign/%s/find", competitionId, applicationId);
    }

    @GetMapping("/find")
    public String find(Model model,
                       @ModelAttribute(name = SELECTION_FORM, binding = false) CofunderSelectionForm selectionForm,
                       @SuppressWarnings("unused") BindingResult bindingResult,
                       @PathVariable("competitionId") long competitionId,
                       @PathVariable("applicationId") long applicationId,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(value = "filter", required = false) String filter,
                       HttpServletRequest request,
                       HttpServletResponse response) {

        updateSelectionForm(request, response, applicationId, selectionForm, filter);
        AssignCofundersViewModel assignCofundersViewModel = assignCofundersViewModelPopulator.populateModel(competitionId, applicationId, filter, page);

        model.addAttribute("model", assignCofundersViewModel);

        return "cofunders/assign";
    }

    private void updateSelectionForm(HttpServletRequest request,
                                     HttpServletResponse response,
                                     long applicationId,
                                     CofunderSelectionForm selectionForm,
                                     String filter) {
        CofunderSelectionForm storedSelectionForm = getSelectionFormFromCookie(request, applicationId).orElse(new CofunderSelectionForm());

        CofunderSelectionForm trimmedCofunderForm = trimSelectionByFilteredResult(storedSelectionForm, filter, applicationId);
        selectionForm.setSelectedCofunderIds(trimmedCofunderForm.getSelectedCofunderIds());
        selectionForm.setAllSelected(trimmedCofunderForm.isAllSelected());

        saveFormToCookie(response, applicationId, selectionForm);
    }

    private CofunderSelectionForm trimSelectionByFilteredResult(CofunderSelectionForm selectionForm,
                                                                String filter,
                                                                Long applicationId) {
        List<Long> filteredResults = getAllCofunderIds(applicationId, filter);
        CofunderSelectionForm updatedSelectionForm = new CofunderSelectionForm();

        selectionForm.getSelectedCofunderIds().retainAll(filteredResults);
        updatedSelectionForm.setSelectedCofunderIds(selectionForm.getSelectedCofunderIds());

        if (updatedSelectionForm.getSelectedCofunderIds().equals(filteredResults) && !updatedSelectionForm.getSelectedCofunderIds().isEmpty()) {
            updatedSelectionForm.setAllSelected(true);
        } else {
            updatedSelectionForm.setAllSelected(false);
        }

        return updatedSelectionForm;
    }

    @PostMapping(value = "/find", params = {"selectionId"})
    public @ResponseBody
    JsonNode selectCofunderForInviteList(
            @PathVariable("competitionId") long competitionId,
            @PathVariable("applicationId") long applicationId,
            @RequestParam("selectionId") long cofunderId,
            @RequestParam("isSelected") boolean isSelected,
            @RequestParam(defaultValue = "", required = false) String filter,
            HttpServletRequest request,
            HttpServletResponse response) {

        boolean limitExceeded = false;
        try {
            List<Long> cofunderIds = getAllCofunderIds(applicationId, filter);
            CofunderSelectionForm selectionForm = getSelectionFormFromCookie(request, applicationId).orElse(new CofunderSelectionForm());
            if (isSelected) {
                int predictedSize = selectionForm.getSelectedCofunderIds().size() + 1;
                if (limitIsExceeded(predictedSize)) {
                    limitExceeded = true;
                } else {
                    selectionForm.getSelectedCofunderIds().add(cofunderId);
                    if (selectionForm.getSelectedCofunderIds().containsAll(cofunderIds)) {
                        selectionForm.setAllSelected(true);
                    }
                }
            } else {
                selectionForm.getSelectedCofunderIds().remove(cofunderId);
                selectionForm.setAllSelected(false);
            }
            saveFormToCookie(response, applicationId, selectionForm);
            return createJsonObjectNode(selectionForm.getSelectedCofunderIds().size(), selectionForm.isAllSelected(), limitExceeded);
        } catch (Exception e) {
            LOG.error("exception thrown selecting cofunders for list", e);
            return createFailureResponse();
        }
    }

    @PostMapping(value = "/find", params = {"addAll"})
    public @ResponseBody
    JsonNode addAllCofundersToInviteList(Model model,
                                         @PathVariable("competitionId") long competitionId,
                                         @PathVariable("applicationId") long applicationId,
                                         @RequestParam("addAll") boolean addAll,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "") String filter,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {
        try {
            CofunderSelectionForm selectionForm = getSelectionFormFromCookie(request, applicationId).orElse(new CofunderSelectionForm());

            if (addAll) {
                selectionForm.setSelectedCofunderIds(getAllCofunderIds(applicationId, filter));
                selectionForm.setAllSelected(true);
            } else {
                selectionForm.getSelectedCofunderIds().clear();
                selectionForm.setAllSelected(false);
            }

            saveFormToCookie(response, applicationId, selectionForm);

            return createSuccessfulResponseWithSelectionStatus(selectionForm.getSelectedCofunderIds().size(), selectionForm.isAllSelected(), false);
        } catch (Exception e) {
            LOG.error("exception thrown adding cofunders to list", e);

            return createFailureResponse();
        }
    }

    private List<Long> getAllCofunderIds(long applicationId, String filter) {

        List<Long> allCofunderIds = new ArrayList<>();

        int page = 0;
        CofundersAvailableForApplicationPageResource result;

        do {
            result = availableCofunders(applicationId, filter, page++);
            allCofunderIds.addAll(userIds(result));
        } while (moreResultsExist(result));

        return allCofunderIds;
    }

    private boolean moreResultsExist(CofundersAvailableForApplicationPageResource result) {
        return result.getTotalPages() > result.getNumber();
    }

    private CofundersAvailableForApplicationPageResource availableCofunders(long applicationId, String filter, int page) {
        return cofunderAssignmentRestService.findAvailableCofundersForApplication(applicationId, filter, page).getSuccess();
    }
    private List<Long> userIds(CofundersAvailableForApplicationPageResource results) {
        return results.getContent().stream().map(CofunderUserResource::getUserId).collect(Collectors.toList());
    }

    @PostMapping(value = "/find/addSelected")
    public String addSelectedCofundersToInviteList(Model model,
                                                   @PathVariable("competitionId") long competitionId,
                                                   @PathVariable("applicationId") long applicationId,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "", required = false) String filter,
                                                   @ModelAttribute(SELECTION_FORM) CofunderSelectionForm selectionForm,
                                                   ValidationHandler validationHandler,
                                                   HttpServletRequest request,
                                                   HttpServletResponse response) {

        CofunderSelectionForm submittedSelectionForm = getSelectionFormFromCookie(request, applicationId)
                .filter(form -> !form.getSelectedCofunderIds().isEmpty())
                .orElse(selectionForm);
        Supplier<String> failureView = () -> redirectToFind(competitionId, applicationId, page);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            RestResult<Void> restResult = cofunderAssignmentRestService.assign(
                    newSelectionFormToResource(submittedSelectionForm, applicationId));

            return validationHandler.addAnyErrors(restResult)
                    .failNowOrSucceedWith(failureView, () -> {
                        removeCookie(response, competitionId);
                        return redirectToFind(competitionId, applicationId, 0);
                    });
        });
    }

    private AssignCofundersResource newSelectionFormToResource(CofunderSelectionForm form, long applicationId) {
        AssignCofundersResource resource = new AssignCofundersResource();
        resource.setApplicationId(applicationId);
        resource.setCofunderIds(form.getSelectedCofunderIds());
        return resource;
    }

    private String redirectToFind(long competitionId, long applicationId, int page) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/competition/{competitionId}/cofunders/assign/{applicationId}/find")
                .queryParam("page", page);

        return "redirect:" + builder.buildAndExpand(asMap("competitionId", competitionId, "applicationId", applicationId))
                .toUriString();
    }

    @PostMapping("/find/remove")
    public String removeCofunder(@RequestParam("userId") long userId, @PathVariable("competitionId") long competitionId,
                                 @PathVariable("applicationId") long applicationId) {
        cofunderAssignmentRestService.removeAssignment(userId, applicationId);
        return String.format("redirect:/competition/%d/cofunders/assign/%d/find", competitionId, applicationId);
    }

}
