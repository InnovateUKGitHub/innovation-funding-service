package org.innovateuk.ifs.management.application.view.controller;

import org.innovateuk.ifs.application.populator.ApplicationPrintPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.resource.FormInputResponseFileEntryResource;
import org.innovateuk.ifs.application.resource.IneligibleOutcomeResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.async.annotations.AsyncMethod;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.interview.service.InterviewAssignmentRestService;
import org.innovateuk.ifs.interview.service.InterviewResponseRestService;
import org.innovateuk.ifs.management.application.list.form.ReinstateIneligibleApplicationForm;
import org.innovateuk.ifs.management.application.view.form.IneligibleApplicationForm;
import org.innovateuk.ifs.management.application.view.populator.ManagementApplicationPopulator;
import org.innovateuk.ifs.management.application.view.populator.ReinstateIneligibleApplicationModelPopulator;
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
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

import static java.lang.String.format;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.file.controller.FileDownloadControllerUtils.getFileResponseEntity;

/**
 * Handles the Competition Management Application overview page (and associated actions).
 */
@Controller
@RequestMapping("/competition/{competitionId}/application")
public class CompetitionManagementApplicationController {

    @Autowired
    private ProcessRoleService processRoleService;
    @Autowired
    private ApplicationPrintPopulator applicationPrintPopulator;
    @Autowired
    private ApplicationRestService applicationRestService;
    @Autowired
    private FormInputResponseRestService formInputResponseRestService;
    @Autowired
    private InterviewAssignmentRestService interviewAssignmentRestService;
    @Autowired
    private InterviewResponseRestService interviewResponseRestService;
    @Autowired
    private ReinstateIneligibleApplicationModelPopulator reinstateIneligibleApplicationModelPopulator;
    @Autowired
    private ManagementApplicationPopulator managementApplicationPopulator;

    @SecuredBySpring(value = "TODO", description = "TODO")
    @PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin', 'support', 'innovation_lead', 'stakeholder', 'external_finance')")
    @GetMapping("/{applicationId}")
    @AsyncMethod
    public String newApplicationSummary(@PathVariable("applicationId") final Long applicationId,
                                        @PathVariable("competitionId") final Long competitionId,
                                        @ModelAttribute(value = "ineligibleForm", binding = false) IneligibleApplicationForm form,
                                        BindingResult bindingResult,
                                        UserResource user,
                                        Model model) {
        model.addAttribute("model", managementApplicationPopulator.populate(applicationId, user));
        return "competition-mgt-application-overview";
    }

    @SecuredBySpring(value = "TODO", description = "TODO")
    @PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin', 'innovation_lead')")
    @PostMapping(value = "/{applicationId}", params = {"markAsIneligible"})
    public String markAsIneligible(@PathVariable("applicationId") final long applicationId,
                                   @PathVariable("competitionId") final long competitionId,
                                   @ModelAttribute("ineligibleForm") @Valid IneligibleApplicationForm form,
                                   @SuppressWarnings("unused") BindingResult bindingResult,
                                   ValidationHandler validationHandler,
                                   UserResource user,
                                   Model model) {
        Supplier<String> failureVew = () -> newApplicationSummary(applicationId, competitionId, form, bindingResult, user, model);
        return validationHandler.failNowOrSucceedWith(
                failureVew,
                () -> {
                    IneligibleOutcomeResource ineligibleOutcomeResource =
                            new IneligibleOutcomeResource(form.getIneligibleReason());

                    RestResult<Void> result = applicationRestService.markAsIneligible(applicationId, ineligibleOutcomeResource);

                    return validationHandler.addAnyErrors(result.getErrors())
                            .failNowOrSucceedWith(
                                    failureVew,
                                    () -> "redirect:/competition/" + competitionId + "/applications/ineligible");
                });
    }

    @SecuredBySpring(value = "TODO", description = "TODO")
    @PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin')")
    @PostMapping(value = "/{applicationId}/reinstateIneligibleApplication")
    public String reinstateIneligibleApplication(Model model,
                                                 @PathVariable("competitionId") final long competitionId,
                                                 @PathVariable("applicationId") final long applicationId,
                                                 @ModelAttribute("form") final ReinstateIneligibleApplicationForm form,
                                                 @SuppressWarnings("unused") BindingResult bindingResult,
                                                 ValidationHandler validationHandler) {

        Supplier<String> failureView = () -> doReinstateIneligibleApplicationConfirm(model, applicationId);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ServiceResult<Void> updateResult = applicationRestService.updateApplicationState(applicationId,
                    ApplicationState.SUBMITTED).toServiceResult();
            return validationHandler.addAnyErrors(updateResult, asGlobalErrors()).
                    failNowOrSucceedWith(failureView, () -> format("redirect:/competition/%s/applications/ineligible", competitionId));
        });
    }

    @SecuredBySpring(value = "TODO", description = "TODO")
    @PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin')")
    @GetMapping(value = "/{applicationId}/reinstateIneligibleApplication/confirm")
    public String reinstateIneligibleApplicationConfirm(final Model model,
                                                        @ModelAttribute("form") final ReinstateIneligibleApplicationForm form,
                                                        @PathVariable("applicationId") final long applicationId) {
        return doReinstateIneligibleApplicationConfirm(model, applicationId);
    }

    @SecuredBySpring(value = "TODO", description = "TODO")
    @PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin', 'support', 'innovation_lead', 'stakeholder', 'external_finance')")
    @GetMapping("/{applicationId}/forminput/{formInputId}/file/{fileEntryId}/download")
    public @ResponseBody
    ResponseEntity<ByteArrayResource> downloadQuestionFile(
            @PathVariable("applicationId") final Long applicationId,
            @PathVariable("formInputId") final Long formInputId,
            @PathVariable("fileEntryId") final Long fileEntryId,
            UserResource user) throws ExecutionException, InterruptedException {

        long applicantProcessRoleId = formInputResponseRestService.getByFormInputIdAndApplication(formInputId, applicationId).getSuccess().get(0).getUpdatedBy();
        final ByteArrayResource resource = formInputResponseRestService.getFile(formInputId, applicationId, applicantProcessRoleId, fileEntryId).getSuccess();
        final FormInputResponseFileEntryResource fileDetails = formInputResponseRestService.getFileDetails(formInputId, applicationId, applicantProcessRoleId, fileEntryId).getSuccess();
        return getFileResponseEntity(resource, fileDetails.getFileEntryResource());
    }

    /**
     * Printable version of the application
     */
    @SecuredBySpring(value = "TODO", description = "TODO")
    @PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin', 'support', 'innovation_lead', 'stakeholder')")
    @GetMapping(value = "/{applicationId}/print")
    public String printManagementApplication(@PathVariable("applicationId") Long applicationId,
                                             @PathVariable("competitionId") Long competitionId,
                                             UserResource user,
                                             Model model) {
        return applicationPrintPopulator.print(applicationId, model, user);
    }

    private String doReinstateIneligibleApplicationConfirm(final Model model, final long applicationId) {
        ApplicationResource applicationResource = applicationRestService.getApplicationById(applicationId).getSuccess();
        model.addAttribute("model", reinstateIneligibleApplicationModelPopulator.populateModel(applicationResource));
        return "application/reinstate-ineligible-application-confirm";
    }

    @GetMapping("/{applicationId}/download-response")
    @SecuredBySpring(value = "READ", description = "Applicants, support staff, innovation leads, stakeholders, comp admins and project finance users have permission to view uploaded interview feedback.")
    @PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin', 'support', 'innovation_lead', 'stakeholder')")
    public @ResponseBody
    ResponseEntity<ByteArrayResource> downloadResponse(Model model,
                                                       @PathVariable("applicationId") long applicationId) {

        return getFileResponseEntity(interviewResponseRestService.downloadResponse(applicationId).getSuccess(),
                interviewResponseRestService.findResponse(applicationId).getSuccess());
    }

    @GetMapping("/{applicationId}/download-feedback")
    @SecuredBySpring(value = "READ", description = "Applicants, support staff, innovation leads, stakeholders, comp admins and project finance users have permission to view uploaded interview feedback.")
    @PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin', 'innovation_lead')")
    public @ResponseBody
    ResponseEntity<ByteArrayResource> downloadFeedback(Model model,
                                                       @PathVariable("applicationId") long applicationId) {
        return getFileResponseEntity(interviewAssignmentRestService.downloadFeedback(applicationId).getSuccess(),
                interviewAssignmentRestService.findFeedback(applicationId).getSuccess());
    }
}
