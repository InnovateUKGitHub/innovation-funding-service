package org.innovateuk.ifs.management.assessment.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.resource.ApplicationAvailableAssessorResource.Sort;
import org.innovateuk.ifs.application.service.ApplicationAssessmentSummaryRestService;
import org.innovateuk.ifs.assessment.resource.AssessmentCreateResource;
import org.innovateuk.ifs.assessment.service.AssessmentRestService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.AvailableAssessorsSortFieldType;
import org.innovateuk.ifs.management.assessment.form.AvailableAssessorForm;
import org.innovateuk.ifs.management.assessment.populator.ApplicationAssessmentProgressModelPopulator;
import org.innovateuk.ifs.management.assessment.viewmodel.ApplicationAssessmentProgressRemoveViewModel;
import org.innovateuk.ifs.management.cookie.CompetitionManagementCookieController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * This controller will handle all Competition Management requests related to allocating assessors to an Application.
 */
@Controller
@RequestMapping("/assessment/competition/{competitionId}/application/{applicationId}/assessors")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = AssessmentApplicationProgressController.class)
@PreAuthorize("hasAnyAuthority('comp_admin','project_finance')")
public class AssessmentApplicationProgressController extends CompetitionManagementCookieController<AvailableAssessorForm> {
    private static final String SELECTION_FORM = "availableAssessorsSelectionForm";
    private static final Log LOG = LogFactory.getLog(AssessmentApplicationProgressController.class);

    @Autowired
    private ApplicationAssessmentProgressModelPopulator applicationAssessmentProgressModelPopulator;

    @Autowired
    private AssessmentRestService assessmentRestService;

    @Autowired
    private ApplicationAssessmentSummaryRestService applicationAssessmentSummaryRestService;

    @Override
    protected String getCookieName() {
        return SELECTION_FORM;
    }

    @Override
    protected Class<AvailableAssessorForm> getFormType() {
        return AvailableAssessorForm.class;
    }

    @GetMapping
    public String applicationProgress(Model model,
                                      @ModelAttribute(name = SELECTION_FORM, binding = false) AvailableAssessorForm selectionForm,
                                      @PathVariable long applicationId,
                                      @PathVariable long competitionId,
                                      @RequestParam(value = "page", defaultValue = "1") int page,
                                      @RequestParam(value = "assessorNameFilter", defaultValue = "") String assessorNameFilter,
                                      @RequestParam(value = "sort", defaultValue = "ASSESSOR") Sort sort,
                                      HttpServletResponse response) {
        updateSelectionForm(response, competitionId, selectionForm);
        return doProgressView(model, applicationId, assessorNameFilter, page - 1, sort);
    }

    @PostMapping
    public String assignAssessor(@PathVariable("competitionId") Long competitionId,
                                 @PathVariable("applicationId") Long applicationId,
                                 @ModelAttribute(SELECTION_FORM) AvailableAssessorForm selectionForm,
                                 HttpServletRequest request) {
        AvailableAssessorForm submittedSelectionForm = getSelectionFormFromCookie(request, competitionId)
                .filter(form -> !form.getSelectedAssessors().isEmpty())
                .orElse(selectionForm);

        List<AssessmentCreateResource> assessments = submittedSelectionForm.getSelectedAssessors().stream()
                .map(assessorId -> new AssessmentCreateResource(applicationId, assessorId))
                .collect(Collectors.toList());
        assessmentRestService.createAssessments(assessments).getSuccess();
        return format("redirect:/assessment/competition/%s/application/%s/assessors", competitionId, applicationId);
    }

    @PostMapping("/withdraw/{assessmentId}")
    public String withdrawAssessment(@PathVariable("competitionId") Long competitionId,
                                     @PathVariable("applicationId") Long applicationId,
                                     @PathVariable("assessmentId") Long assessmentId) {
        assessmentRestService.withdrawAssessment(assessmentId).getSuccess();
        return redirectToProgress(competitionId, applicationId);
    }

    @GetMapping(value = "/withdraw/{assessmentId}/confirm")
    public String withdrawAssessmentConfirm(
            Model model,
            @PathVariable("competitionId") Long competitionId,
            @PathVariable("applicationId") Long applicationId,
            @PathVariable("assessmentId") Long assessmentId,
            @RequestParam(value = "sortField", defaultValue = "TITLE") String sortField) {
        model.addAttribute("model", new ApplicationAssessmentProgressRemoveViewModel(
                competitionId,
                applicationId,
                assessmentId,
                AvailableAssessorsSortFieldType.valueOf(sortField)
        ));
        return "competition/application-progress-remove-confirm";
    }

    private String doProgressView(Model model, Long applicationId, String assessorNameFilter, int page, Sort sort) {
        model.addAttribute("model", applicationAssessmentProgressModelPopulator.populateModel(applicationId, assessorNameFilter, page, sort));

        return "competition/application-progress";
    }

    private String redirectToProgress(long competitionId, long applicationId) {
        return format("redirect:/assessment/competition/%s/application/%s/assessors", competitionId, applicationId);
    }

    @PostMapping(params = {"selectionId"})
    public @ResponseBody
    JsonNode selectAssessorForResendList(
            @PathVariable long competitionId,
            @PathVariable long applicationId,
            @RequestParam("selectionId") long assessorId,
            @RequestParam("isSelected") boolean isSelected,
            @RequestParam(value = "assessorNameFilter", defaultValue = "") String assessorNameFilter,
            HttpServletRequest request,
            HttpServletResponse response) {

        boolean limitExceeded = false;
        try {
            List<Long> InviteIds = getAllAssessorIds(applicationId, assessorNameFilter);
            AvailableAssessorForm selectionForm = getSelectionFormFromCookie(request, competitionId).orElse(new AvailableAssessorForm());
            if (isSelected) {
                int predictedSize = selectionForm.getSelectedAssessors().size() + 1;
                if(limitIsExceeded(predictedSize)){
                    limitExceeded = true;
                } else {
                    selectionForm.getSelectedAssessors().add(assessorId);
                    if (selectionForm.getSelectedAssessors().containsAll(InviteIds)) {
                        selectionForm.setAllSelected(true);
                    }
                }
            } else {
                selectionForm.getSelectedAssessors().remove(assessorId);
                selectionForm.setAllSelected(false);
            }
            saveFormToCookie(response, competitionId, selectionForm);
            return createJsonObjectNode(selectionForm.getSelectedAssessors().size(), selectionForm.isAllSelected(), limitExceeded);
        } catch (Exception e) {
            LOG.error("exception thrown selecting assessor for resend list", e);
            return createFailureResponse();
        }
    }

    @PostMapping(params = {"addAll"})
    public @ResponseBody JsonNode addAllAssessorsToResendList(@PathVariable long competitionId,
                                                              @PathVariable long applicationId,
                                                              @RequestParam("addAll") boolean addAll,
                                                              @RequestParam(value = "assessorNameFilter", defaultValue = "") String assessorNameFilter,
                                                              HttpServletRequest request,
                                                              HttpServletResponse response) {
        try {
            AvailableAssessorForm selectionForm = getSelectionFormFromCookie(request, competitionId).orElse(new AvailableAssessorForm());

            if (addAll) {
                selectionForm.setSelectedAssessors(getAllAssessorIds(applicationId, assessorNameFilter));
                selectionForm.setAllSelected(true);
            } else {
                selectionForm.getSelectedAssessors().clear();
                selectionForm.setAllSelected(false);
            }

            saveFormToCookie(response, competitionId, selectionForm);

            return createSuccessfulResponseWithSelectionStatus(selectionForm.getSelectedAssessors().size(), selectionForm.isAllSelected(), false);
        } catch (Exception e) {
            LOG.error("exception thrown adding assessors to list", e);
            return createFailureResponse();
        }
    }

    private List<Long> getAllAssessorIds(long applicationId, String assessorName) {
        return applicationAssessmentSummaryRestService.getAvailableAssessorsIds(applicationId, assessorName).getSuccess();
    }

    private void updateSelectionForm(HttpServletResponse response,
                                     long competitionId,
                                     AvailableAssessorForm selectionForm) {
        saveFormToCookie(response, competitionId, selectionForm);
    }
}
