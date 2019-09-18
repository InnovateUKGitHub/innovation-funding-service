package org.innovateuk.ifs.management.assessment.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.assessment.service.CompetitionInviteRestService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.invite.resource.ParticipantStatusResource;
import org.innovateuk.ifs.management.assessor.form.OverviewAssessorsFilterForm;
import org.innovateuk.ifs.management.assessor.form.OverviewSelectionForm;
import org.innovateuk.ifs.management.assessor.populator.CompetitionInviteAssessorsOverviewModelPopulator;
import org.innovateuk.ifs.management.cookie.CompetitionManagementCookieController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.innovateuk.ifs.invite.resource.ParticipantStatusResource.PENDING;
import static org.innovateuk.ifs.invite.resource.ParticipantStatusResource.REJECTED;

/**
 * This controller handles the Overview tab for inviting assessors to a Competition.
 */
@Controller
@RequestMapping("/competition/{competitionId}/assessors")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = InviteAssessorsOverviewController.class)
@PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionCompositeId', 'ASSESSMENT')")
public class InviteAssessorsOverviewController extends CompetitionManagementCookieController<OverviewSelectionForm> {

    private static final Log LOG = LogFactory.getLog(InviteAssessorsOverviewController.class);

    private static final String FILTER_FORM_ATTR_NAME = "filterForm";
    private static final String SELECTION_FORM = "overviewSelectionForm";

    @Autowired
    private CompetitionInviteRestService competitionInviteRestService;
    
    @Autowired
    private CompetitionInviteAssessorsOverviewModelPopulator inviteAssessorsOverviewModelPopulator;

    @Override
    protected String getCookieName() {
        return SELECTION_FORM;
    }

    @Override
    protected Class<OverviewSelectionForm> getFormType() {
        return OverviewSelectionForm.class;
    }

    @GetMapping("/pending-and-declined")
    public String overview(Model model,
                           @Valid @ModelAttribute(FILTER_FORM_ATTR_NAME) OverviewAssessorsFilterForm filterForm,
                           @ModelAttribute(name = SELECTION_FORM, binding = false) OverviewSelectionForm selectionForm,
                           @SuppressWarnings("unused") BindingResult bindingResult,
                           @PathVariable("competitionId") long competitionId,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(value = "filterChanged", required = false) boolean filterChanged,
                           HttpServletRequest request,
                           HttpServletResponse response) {

        updateOverviewSelectionForm(request, response, competitionId, selectionForm, filterForm, filterChanged);

        model.addAttribute("model", inviteAssessorsOverviewModelPopulator.populateModel(
                competitionId,
                page,
                filterForm.getStatus(),
                filterForm.getCompliant(),
                filterForm.getAssessorName()
        ));

        return "assessors/overview";
    }

    private void updateOverviewSelectionForm(HttpServletRequest request,
                                             HttpServletResponse response,
                                             long competitionId,
                                             OverviewSelectionForm selectionForm,
                                             OverviewAssessorsFilterForm filterForm,
                                             boolean filterChanged) {
        OverviewSelectionForm storedSelectionForm = getSelectionFormFromCookie(request, competitionId).orElse(new OverviewSelectionForm());

        if (storedSelectionForm.anyFilterIsActive()
                && !filterForm.anyFilterIsActive()
                && !filterChanged
                && storedSelectionForm.anySelectionIsMade()) {
            updateFilterForm(filterForm, storedSelectionForm);
        }

        OverviewSelectionForm trimmedOverviewForm = trimSelectionByFilteredResult(
                storedSelectionForm,
                filterForm.getStatus(),
                filterForm.getCompliant(),
                filterForm.getAssessorName(),
                competitionId);
        selectionForm.setSelectedInviteIds(trimmedOverviewForm.getSelectedInviteIds());
        selectionForm.setAllSelected(trimmedOverviewForm.getAllSelected());
        selectionForm.setSelectedStatus(filterForm.getStatus().orElse(null));
        selectionForm.setCompliant(filterForm.getCompliant().orElse(null));
        selectionForm.setAssessorName(filterForm.getAssessorName().orElse(null));

        saveFormToCookie(response, competitionId, selectionForm);
    }

    private void updateFilterForm(OverviewAssessorsFilterForm filterForm, OverviewSelectionForm storedSelectionForm) {
        filterForm.setStatus(storedSelectionForm.getSelectedStatus() == null ? empty() :
                of(storedSelectionForm.getSelectedStatus()));

        filterForm.setCompliant(storedSelectionForm.getCompliant() == null ? empty() :
                of(storedSelectionForm.getCompliant()));

        filterForm.setAssessorName(storedSelectionForm.getAssessorName() == null ? empty() :
                of(storedSelectionForm.getAssessorName()));
    }

    private OverviewSelectionForm trimSelectionByFilteredResult(OverviewSelectionForm selectionForm,
                                                                Optional<ParticipantStatusResource> status,
                                                                Optional<Boolean> compliant,
                                                                Optional<String> assessorName,
                                                                Long competitionId) {
        List<Long> filteredResults = getAllInviteIds(competitionId, status, compliant, assessorName);
        OverviewSelectionForm updatedSelectionForm = new OverviewSelectionForm();

        selectionForm.getSelectedInviteIds().retainAll(filteredResults);
        updatedSelectionForm.setSelectedInviteIds(selectionForm.getSelectedInviteIds());

        if (updatedSelectionForm.getSelectedInviteIds().equals(filteredResults)  && !updatedSelectionForm.getSelectedInviteIds().isEmpty()) {
            updatedSelectionForm.setAllSelected(true);
        } else {
            updatedSelectionForm.setAllSelected(false);
        }

        return updatedSelectionForm;
    }

    @PostMapping(value = "/pending-and-declined", params = {"selectionId"})
    public @ResponseBody JsonNode selectAssessorForResendList(
            @PathVariable("competitionId") long competitionId,
            @RequestParam("selectionId") long assessorId,
            @RequestParam("isSelected") boolean isSelected,
            @RequestParam Optional<ParticipantStatusResource> status,
            @RequestParam Optional<Boolean> compliant,
            @RequestParam Optional<String> assessorName,
            HttpServletRequest request,
            HttpServletResponse response) {

        boolean limitExceeded = false;
        try {
            List<Long> InviteIds = getAllInviteIds(competitionId, status, compliant, assessorName);
            OverviewSelectionForm selectionForm = getSelectionFormFromCookie(request, competitionId).orElse(new OverviewSelectionForm());
            if (isSelected) {
                int predictedSize = selectionForm.getSelectedInviteIds().size() + 1;
                if(limitIsExceeded(predictedSize)){
                    limitExceeded = true;
                } else {
                    selectionForm.getSelectedInviteIds().add(assessorId);
                    if (selectionForm.getSelectedInviteIds().containsAll(InviteIds)) {
                        selectionForm.setAllSelected(true);
                    }
                }
            } else {
                selectionForm.getSelectedInviteIds().remove(assessorId);
                selectionForm.setAllSelected(false);
            }
            saveFormToCookie(response, competitionId, selectionForm);
            return createJsonObjectNode(selectionForm.getSelectedInviteIds().size(), selectionForm.getAllSelected(), limitExceeded);
        } catch (Exception e) {
            LOG.error("exception thrown selecting assessor for resend list", e);
            return createFailureResponse();
        }
    }

    @PostMapping(value = "/pending-and-declined", params = {"addAll"})
    public @ResponseBody JsonNode addAllAssessorsToResendList(Model model,
                                                              @PathVariable("competitionId") long competitionId,
                                                              @RequestParam("addAll") boolean addAll,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam Optional<ParticipantStatusResource> status,
                                                              @RequestParam Optional<Boolean> compliant,
                                                              @RequestParam Optional<String> assessorName,
                                                              HttpServletRequest request,
                                                              HttpServletResponse response) {
        try {
            OverviewSelectionForm selectionForm = getSelectionFormFromCookie(request, competitionId).orElse(new OverviewSelectionForm());

            if (addAll) {
                selectionForm.setSelectedInviteIds(getAllInviteIds(competitionId, status, compliant, assessorName));
                selectionForm.setAllSelected(true);
            } else {
                selectionForm.getSelectedInviteIds().clear();
                selectionForm.setAllSelected(false);
            }

            saveFormToCookie(response, competitionId, selectionForm);

            return createSuccessfulResponseWithSelectionStatus(selectionForm.getSelectedInviteIds().size(), selectionForm.getAllSelected(), false);
        } catch (Exception e) {
            LOG.error("exception thrown adding assessors to resend list", e);

            return createFailureResponse();
        }
    }

    private List<Long> getAllInviteIds(long competitionId,
                                       Optional<ParticipantStatusResource> status,
                                       Optional<Boolean> compliant,
                                       Optional<String> assessorName) {
        List<ParticipantStatusResource> statuses = status.map(Collections::singletonList)
                .orElseGet(() -> asList(REJECTED, PENDING));
        return competitionInviteRestService.getAssessorsNotAcceptedInviteIds(competitionId, statuses, compliant, assessorName).getSuccess();
    }
}
