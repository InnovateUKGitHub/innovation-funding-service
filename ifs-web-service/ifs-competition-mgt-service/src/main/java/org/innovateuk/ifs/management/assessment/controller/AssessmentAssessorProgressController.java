package org.innovateuk.ifs.management.assessment.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource.Sort;
import org.innovateuk.ifs.application.service.ApplicationCountSummaryRestService;
import org.innovateuk.ifs.assessment.resource.AssessmentCreateResource;
import org.innovateuk.ifs.assessment.service.AssessmentRestService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.AssessmentPeriodResource;
import org.innovateuk.ifs.competition.service.AssessmentPeriodRestService;
import org.innovateuk.ifs.management.assessment.form.ApplicationSelectionForm;
import org.innovateuk.ifs.management.assessment.populator.AssessorAssessmentProgressModelPopulator;
import org.innovateuk.ifs.management.assessment.viewmodel.AssessorAssessmentProgressRemoveViewModel;
import org.innovateuk.ifs.management.assessment.viewmodel.AssessorAssessmentProgressUnsubmitViewModel;
import org.innovateuk.ifs.management.cookie.CompetitionManagementCookieController;
import org.innovateuk.ifs.user.resource.Authority;
import org.innovateuk.ifs.user.resource.UserResource;
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

@Controller
@RequestMapping("/assessment/competition/{competitionId}/assessors/{assessorId}")
@SecuredBySpring(value = "Controller", description = "Comp Admins and Project Finance users can manage assessments", securedType = AssessmentAssessorProgressController.class)
@PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionCompositeId', 'ASSESSMENT')")
public class AssessmentAssessorProgressController extends CompetitionManagementCookieController<ApplicationSelectionForm> {
    private static final String SELECTION_FORM = "applicationSelectionForm";
    private static final Log LOG = LogFactory.getLog(AssessmentApplicationProgressController.class);

    @Autowired
    private AssessorAssessmentProgressModelPopulator assessorAssessmentProgressModelPopulator;

    @Autowired
    private AssessmentRestService assessmentRestService;

    @Autowired
    private ApplicationCountSummaryRestService applicationCountSummaryRestService;

    @Autowired
    private AssessmentPeriodRestService assessmentPeriodRestService;

    @Override
    protected String getCookieName() {
        return SELECTION_FORM;
    }

    @Override
    protected Class<ApplicationSelectionForm> getFormType() {
        return ApplicationSelectionForm.class;
    }

    @GetMapping("/period/{assessmentPeriodId}")
    public String assessorProgressForPeriod(@PathVariable long competitionId,
                                   @PathVariable long assessorId,
                                   @PathVariable long assessmentPeriodId,
                                   @ModelAttribute(name = SELECTION_FORM, binding = false) ApplicationSelectionForm selectionForm,
                                   @RequestParam(value = "page", defaultValue = "1") int page,
                                   @RequestParam(value = "sort", defaultValue = "APPLICATION_NUMBER") Sort sort,
                                   @RequestParam(value = "filterSearch", defaultValue = "") String filter,
                                   Model model,
                                   HttpServletRequest request,
                                   HttpServletResponse response,
                                   UserResource loggedInUser) {
        updateSelectionForm(request, response, competitionId, assessorId, selectionForm, filter);
        model.addAttribute("model", assessorAssessmentProgressModelPopulator.populateModel(
                competitionId,
                assessorId,
                assessmentPeriodId,
                page - 1,
                sort,
                filter,
                loggedInUser.hasAuthority(Authority.SUPER_ADMIN_USER)));
        return "competition/assessor-progress";
    }

    @GetMapping
    public String assessorProgressForOnlyPeriod(@PathVariable long competitionId,
                                            @PathVariable long assessorId) {

        List<AssessmentPeriodResource> assessmentPeriods = assessmentPeriodRestService.getAssessmentPeriodByCompetitionId(competitionId).getSuccess();

        if (assessmentPeriods.size() == 1) {
            long assessmentPeriodId = assessmentPeriods.get(0).getId();
            return format("redirect:/assessment/competition/%s/assessors/%s/period/%s", competitionId, assessorId, assessmentPeriodId);
        }

        return format("redirect:/assessment/competition/{competitionId}/assessors", competitionId);
    }

    @PostMapping("/period/{assessmentPeriodId}")
    public String assessorAssignForPeriod(@PathVariable long competitionId,
                                 @PathVariable long assessorId,
                                 @PathVariable long assessmentPeriodId,
                                 @ModelAttribute(SELECTION_FORM) ApplicationSelectionForm selectionForm,
                                 HttpServletRequest request) {

        ApplicationSelectionForm submittedSelectionForm = getSelectionFormFromCookie(request, competitionId)
                .filter(form -> !form.getSelectedApplications().isEmpty())
                .orElse(selectionForm);

        List<AssessmentCreateResource> assessments = submittedSelectionForm.getSelectedApplications().stream()
                .map(applicationId -> {
                    AssessmentCreateResource a = new AssessmentCreateResource(applicationId, assessorId, assessmentPeriodId);
                    a.setAssessmentPeriodId(assessmentPeriodId);
                    return a;
                })
                .collect(Collectors.toList());
        assessmentRestService.createAssessments(assessments).getSuccess();
        return format("redirect:/assessment/competition/%s/assessors/%s/period/%s", competitionId, assessorId, assessmentPeriodId);
    }

    @PostMapping("/withdraw/{assessmentId}/period/{assessmentPeriodId}")
    public String withdrawAssessmentFromPeriod(@PathVariable long competitionId,
                                     @PathVariable long assessorId,
                                     @PathVariable long assessmentId,
                                     @PathVariable long assessmentPeriodId) {
        assessmentRestService.withdrawAssessment(assessmentId).getSuccess();
        return format("redirect:/assessment/competition/%s/assessors/%s/period/%s", competitionId, assessorId, assessmentPeriodId);
    }

    @GetMapping(value = "/withdraw/{assessmentId}/period/{assessmentPeriodId}/confirm")
    public String withdrawAssessmentConfirm(
            Model model,
            @PathVariable long competitionId,
            @PathVariable long assessorId,
            @PathVariable long assessmentId,
            @PathVariable long assessmentPeriodId) {
        model.addAttribute("model", new AssessorAssessmentProgressRemoveViewModel(
                competitionId,
                assessorId,
                assessmentId,
                assessmentPeriodId
        ));
        return "competition/assessor-progress-remove-confirm";
    }

    @PostMapping("/unsubmit/{assessmentId}")
    public String unsubmitAssessment(@PathVariable long competitionId,
                                     @PathVariable long assessorId,
                                     @PathVariable long assessmentId) {
        assessmentRestService.unsubmitAssessment(assessmentId).getSuccess();
        return format("redirect:/assessment/competition/%s/assessors/%s", competitionId, assessorId);
    }

    @GetMapping(value = "/unsubmit/{assessmentId}/confirm")
    public String unsubmitAssessmentConfirm(
            Model model,
            @PathVariable long competitionId,
            @PathVariable long assessorId,
            @PathVariable long assessmentId) {
        model.addAttribute("model", new AssessorAssessmentProgressUnsubmitViewModel(
                competitionId,
                assessorId,
                assessmentId
        ));
        return "competition/assessor-progress-unsubmit-assessment-confirm";
    }

    @PostMapping(value = "/period/{assessmentPeriodId}", params = {"selectionId"})
    @ResponseBody
    public JsonNode selectAssessorForResendListForPeriod(
            @PathVariable long competitionId,
            @PathVariable long assessorId,
            @RequestParam("selectionId") long applicationId,
            @RequestParam("isSelected") boolean isSelected,
            @RequestParam(value = "filterSearch", defaultValue = "") String filter,
            HttpServletRequest request,
            HttpServletResponse response) {
        return selectAssessorForResendListJsonNode(
                competitionId,
                assessorId,
                applicationId,
                isSelected,
                filter,
                request,
                response
        );
    }

    private JsonNode selectAssessorForResendListJsonNode(
            long competitionId,
            long assessorId,
            long applicationId,
            boolean isSelected,
            String filter,
            HttpServletRequest request,
            HttpServletResponse response) {
        boolean limitExceeded = false;
        try {
            List<Long> InviteIds = getAllApplicationIds(competitionId, assessorId, filter);
            ApplicationSelectionForm selectionForm = getSelectionFormFromCookie(request, competitionId).orElse(new ApplicationSelectionForm());
            if (isSelected) {
                int predictedSize = selectionForm.getSelectedApplications().size() + 1;
                if(limitIsExceeded(predictedSize)){
                    limitExceeded = true;
                } else {
                    selectionForm.getSelectedApplications().add(applicationId);
                    if (selectionForm.getSelectedApplications().containsAll(InviteIds)) {
                        selectionForm.setAllSelected(true);
                    }
                }
            } else {
                selectionForm.getSelectedApplications().remove(applicationId);
                selectionForm.setAllSelected(false);
            }
            saveFormToCookie(response, competitionId, selectionForm);
            return createJsonObjectNode(selectionForm.getSelectedApplications().size(), selectionForm.isAllSelected(), limitExceeded);
        } catch (Exception e) {
            LOG.error("exception thrown selecting assessor for resend list", e);
            return createFailureResponse();
        }
    }

    @PostMapping(value = "/period/{assessmentPeriodId}", params = {"addAll"})
    public @ResponseBody JsonNode addAllAssessorsToResendList(@PathVariable long competitionId,
                                                              @PathVariable long assessorId,
                                                              @RequestParam("addAll") boolean addAll,
                                                              @RequestParam(value = "filterSearch", defaultValue = "") String filter,
                                                              HttpServletRequest request,
                                                              HttpServletResponse response) {
        try {
            ApplicationSelectionForm selectionForm = getSelectionFormFromCookie(request, competitionId).orElse(new ApplicationSelectionForm());

            if (addAll) {
                selectionForm.setSelectedApplications(getAllApplicationIds(competitionId, assessorId, filter));
                selectionForm.setAllSelected(true);
            } else {
                selectionForm.getSelectedApplications().clear();
                selectionForm.setAllSelected(false);
            }

            saveFormToCookie(response, competitionId, selectionForm);

            return createSuccessfulResponseWithSelectionStatus(selectionForm.getSelectedApplications().size(), selectionForm.isAllSelected(), false);
        } catch (Exception e) {
            LOG.error("exception thrown adding assessors to list", e);
            return createFailureResponse();
        }
    }

    private List<Long> getAllApplicationIds(long competitionId, long assessorId, String filterSearch) {
        return applicationCountSummaryRestService.getApplicationIdsByCompetitionIdAndAssessorId(competitionId, assessorId, filterSearch).getSuccess();
    }

    private void updateSelectionForm(HttpServletRequest request,
                                     HttpServletResponse response,
                                     long competitionId,
                                     long assessorId,
                                     ApplicationSelectionForm selectionForm,
                                     String filter) {
        ApplicationSelectionForm storedSelectionForm = getSelectionFormFromCookie(request, competitionId).orElse(new ApplicationSelectionForm());

        ApplicationSelectionForm trimmedAssessorForm = trimSelectionByFilteredResult(storedSelectionForm, filter, competitionId, assessorId);
        selectionForm.setSelectedApplications(trimmedAssessorForm.getSelectedApplications());
        selectionForm.setAllSelected(trimmedAssessorForm.isAllSelected());

        saveFormToCookie(response, competitionId, selectionForm);
    }

    private ApplicationSelectionForm trimSelectionByFilteredResult(ApplicationSelectionForm selectionForm,
                                                                String filter,
                                                                long competitionId, long assessorId) {
        List<Long> filteredResults = getAllApplicationIds(competitionId, assessorId, filter);
        ApplicationSelectionForm updatedSelectionForm = new ApplicationSelectionForm();

        selectionForm.getSelectedApplications().retainAll(filteredResults);
        updatedSelectionForm.setSelectedApplications(selectionForm.getSelectedApplications());

        if (updatedSelectionForm.getSelectedApplications().equals(filteredResults) && !updatedSelectionForm.getSelectedApplications().isEmpty()) {
            updatedSelectionForm.setAllSelected(true);
        } else {
            updatedSelectionForm.setAllSelected(false);
        }

        return updatedSelectionForm;
    }
}
