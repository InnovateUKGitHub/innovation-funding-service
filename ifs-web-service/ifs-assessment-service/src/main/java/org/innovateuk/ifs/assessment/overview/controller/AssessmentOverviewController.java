package org.innovateuk.ifs.assessment.overview.controller;

import org.innovateuk.ifs.application.resource.FormInputResponseFileEntryResource;
import org.innovateuk.ifs.assessment.common.service.AssessmentService;
import org.innovateuk.ifs.assessment.overview.form.AssessmentOverviewForm;
import org.innovateuk.ifs.assessment.overview.populator.AssessmentDetailedFinancesModelPopulator;
import org.innovateuk.ifs.assessment.overview.populator.AssessmentFinancesSummaryModelPopulator;
import org.innovateuk.ifs.assessment.overview.populator.AssessmentOverviewModelPopulator;
import org.innovateuk.ifs.assessment.overview.populator.AssessmentTermsAndConditionsModelPopulator;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.service.TermsAndConditionsRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
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
@SecuredBySpring(value = "Controller", description = "TODO", securedType = AssessmentOverviewController.class)
@PreAuthorize("hasAuthority('assessor')")
public class AssessmentOverviewController {

    private static final String FORM_ATTR_NAME = "form";

    @Autowired
    private AssessmentOverviewModelPopulator assessmentOverviewModelPopulator;

    @Autowired
    private AssessmentFinancesSummaryModelPopulator assessmentFinancesSummaryModelPopulator;

    @Autowired
    private AssessmentDetailedFinancesModelPopulator assessmentDetailedFinancesModelPopulator;

    @Autowired
    private AssessmentTermsAndConditionsModelPopulator assessmentTermsAndConditionsModelPopulator;

    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    private FormInputResponseRestService formInputResponseRestService;

    @Autowired
    private UserRestService userRestService;

    @Autowired
    private TermsAndConditionsRestService termsAndConditionsRestService;

    @GetMapping("/{assessmentId}")
    public String getOverview(Model model,
                              @ModelAttribute(name = FORM_ATTR_NAME, binding = false) AssessmentOverviewForm form,
                              @PathVariable("assessmentId") long assessmentId) {
        model.addAttribute("model", assessmentOverviewModelPopulator.populateModel(assessmentId));
        return "assessment/application-overview";
    }

    @GetMapping("/{assessmentId}/finances")
    public String getFinancesSummary(Model model, @PathVariable("assessmentId") long assessmentId, UserResource user) {
        model.addAttribute("model", assessmentFinancesSummaryModelPopulator.populateModel(assessmentId, user));
        return "assessment/application-finances-summary";
    }

    @GetMapping("/{assessmentId}/terms-and-conditions")
    public String getTermsAndConditions(Model model, @PathVariable("assessmentId") long assessmentId) {
        model.addAttribute("model", assessmentTermsAndConditionsModelPopulator.populate(assessmentId));
        return "assessment/application-terms-and-conditions";
    }

    @GetMapping("/application/{applicationId}/detailed-finances/organisation/{organisationId}")
    public String getDetailedFinances(Model model,
                                      @PathVariable long applicationId,
                                      @PathVariable long organisationId,
                                      UserResource user) {
        model.addAttribute("model", assessmentDetailedFinancesModelPopulator.populateModel(applicationId, organisationId, model, user));
        return "assessment/application-detailed-finances";
    }

    @GetMapping("/{assessmentId}/application/{applicationId}/formInput/{formInputId}/file/{fileEntryId}/download")
    public @ResponseBody ResponseEntity<ByteArrayResource> downloadAppendix(
            @PathVariable("applicationId") Long applicationId,
            @PathVariable("formInputId") Long formInputId,
            @PathVariable("fileEntryId") Long fileEntryId,
            UserResource loggedInUser) {
        ProcessRoleResource processRole = userRestService.findProcessRole(applicationId).getSuccess().stream()
                .filter(role -> loggedInUser.getId().equals(role.getUser()))
                .findAny()
                .orElseThrow(ObjectNotFoundException::new);

        final ByteArrayResource resource = formInputResponseRestService
                .getFile(formInputId, applicationId, processRole.getId(), fileEntryId).getSuccess();

        final FormInputResponseFileEntryResource fileDetails = formInputResponseRestService
                .getFileDetails(formInputId, applicationId, processRole.getId(), fileEntryId).getSuccess();

        return getFileResponseEntity(resource, fileDetails.getFileEntryResource());
    }

    @PostMapping("/{assessmentId}/reject")
    public String rejectInvitation(
            Model model,
            @Valid @ModelAttribute(FORM_ATTR_NAME) AssessmentOverviewForm form,
            @SuppressWarnings("unused") BindingResult bindingResult,
            ValidationHandler validationHandler,
            @PathVariable("assessmentId") Long assessmentId) {
        Supplier<String> failureView = () -> getOverview(model, form, assessmentId);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            AssessmentResource assessment = assessmentService.getRejectableById(assessmentId);
            ServiceResult<Void> updateResult = assessmentService.rejectInvitation(assessment.getId(), form.getRejectReason(), form.getRejectComment());

            return validationHandler.addAnyErrors(updateResult, fieldErrorsToFieldErrors(), asGlobalErrors()).
                    failNowOrSucceedWith(failureView, () -> redirectToAssessorCompetitionDashboard(assessment.getCompetition()));
        });
    }

    private String redirectToAssessorCompetitionDashboard(Long competitionId) {
        return format("redirect:/assessor/dashboard/competition/%s", competitionId);
    }
}
