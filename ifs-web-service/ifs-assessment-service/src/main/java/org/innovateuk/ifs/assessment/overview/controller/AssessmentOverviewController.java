package org.innovateuk.ifs.assessment.overview.controller;

import org.innovateuk.ifs.application.resource.FormInputResponseFileEntryResource;
import org.innovateuk.ifs.assessment.assignment.populator.RejectAssessmentModelPopulator;
import org.innovateuk.ifs.assessment.common.service.AssessmentService;
import org.innovateuk.ifs.assessment.overview.form.AssessmentOverviewForm;
import org.innovateuk.ifs.assessment.overview.populator.AssessmentFinancesSummaryModelPopulator;
import org.innovateuk.ifs.assessment.overview.populator.AssessmentOverviewModelPopulator;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.form.service.FormInputResponseService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.function.Supplier;

import static java.lang.String.format;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;
import static org.innovateuk.ifs.file.controller.FileDownloadControllerUtils.getFileResponseEntity;

@Controller
@RequestMapping(value = "/{assessmentId}")
@PreAuthorize("hasAuthority('assessor')")
public class AssessmentOverviewController {

    private static final String FORM_ATTR_NAME = "form";

    @Autowired
    private AssessmentOverviewModelPopulator assessmentOverviewModelPopulator;

    @Autowired
    private AssessmentFinancesSummaryModelPopulator assessmentFinancesSummaryModelPopulator;

    @Autowired
    private RejectAssessmentModelPopulator rejectAssessmentModelPopulator;

    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    private FormInputResponseService formInputResponseService;

    @Autowired
    private ProcessRoleService processRoleService;


    @GetMapping
    public String getOverview(Model model,
                              @ModelAttribute(FORM_ATTR_NAME) AssessmentOverviewForm form,
                              @PathVariable("assessmentId") Long assessmentId,
                              @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        model.addAttribute("model", assessmentOverviewModelPopulator.populateModel(assessmentId));
        return "assessment/application-overview";
    }

    @GetMapping("/finances")
    public String getFinancesSummary(Model model, @PathVariable("assessmentId") Long assessmentId) {
        model.addAttribute("model", assessmentFinancesSummaryModelPopulator.populateModel(assessmentId, model));
        return "assessment/application-finances-summary";
    }

    @GetMapping("/application/{applicationId}/formInput/{formInputId}/download")
    public @ResponseBody ResponseEntity<ByteArrayResource> downloadAppendix(
            @PathVariable("applicationId") Long applicationId,
            @PathVariable("formInputId") Long formInputId,
            @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        ProcessRoleResource processRole = processRoleService.findProcessRole(loggedInUser.getId(), applicationId);

        final ByteArrayResource resource = formInputResponseService
                .getFile(formInputId, applicationId, processRole.getId()).getSuccessObjectOrThrowException();

        final FormInputResponseFileEntryResource fileDetails = formInputResponseService
                .getFileDetails(formInputId, applicationId, processRole.getId()).getSuccessObjectOrThrowException();

        return getFileResponseEntity(resource, fileDetails.getFileEntryResource());
    }

    @PostMapping("/reject")
    public String rejectInvitation(
            Model model,
            @Valid @ModelAttribute(FORM_ATTR_NAME) AssessmentOverviewForm form,
            @SuppressWarnings("unused") BindingResult bindingResult,
            ValidationHandler validationHandler,
            @PathVariable("assessmentId") Long assessmentId) {
        Supplier<String> failureView = () -> doViewRejectInvitationConfirm(model, assessmentId);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            AssessmentResource assessment = assessmentService.getRejectableById(assessmentId);
            ServiceResult<Void> updateResult = assessmentService.rejectInvitation(assessment.getId(), form.getRejectReason(), form.getRejectComment());

            return validationHandler.addAnyErrors(updateResult, fieldErrorsToFieldErrors(), asGlobalErrors()).
                    failNowOrSucceedWith(failureView, () -> redirectToAssessorCompetitionDashboard(assessment.getCompetition()));
        });
    }

    @GetMapping("/reject/confirm")
    public String rejectInvitationConfirm(
            Model model,
            @ModelAttribute(FORM_ATTR_NAME) AssessmentOverviewForm form,
            @PathVariable("assessmentId") Long assessmentId) {
        return doViewRejectInvitationConfirm(model, assessmentId);
    }

    private String doViewRejectInvitationConfirm(Model model, Long assessmentId) {
        model.addAttribute("model", rejectAssessmentModelPopulator.populateModel(assessmentId));
        return "assessment/reject-invitation-confirm";
    }

    private String redirectToAssessorCompetitionDashboard(Long competitionId) {
        return format("redirect:/assessor/dashboard/competition/%s", competitionId);
    }
}
