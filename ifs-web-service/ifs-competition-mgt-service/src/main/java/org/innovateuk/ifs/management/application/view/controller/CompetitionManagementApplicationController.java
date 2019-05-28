package org.innovateuk.ifs.management.application.view.controller;

import org.innovateuk.ifs.application.populator.ApplicationPrintPopulator;
import org.innovateuk.ifs.application.resource.*;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.async.annotations.AsyncMethod;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.management.application.list.form.ReinstateIneligibleApplicationForm;
import org.innovateuk.ifs.management.application.view.form.IneligibleApplicationForm;
import org.innovateuk.ifs.management.application.view.populator.ApplicationTeamModelManagementPopulator;
import org.innovateuk.ifs.management.application.view.populator.ManagementApplicationPopulator;
import org.innovateuk.ifs.management.application.view.populator.ReinstateIneligibleApplicationModelPopulator;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

import static java.lang.String.format;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.file.controller.FileDownloadControllerUtils.getFileResponseEntity;
import static org.innovateuk.ifs.user.resource.Role.STAKEHOLDER;
import static org.innovateuk.ifs.util.HttpUtils.getQueryStringParameters;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isInternal;

/**
 * Handles the Competition Management Application overview page (and associated actions).
 */
@Controller
@RequestMapping("/competition/{competitionId}/application")
public class CompetitionManagementApplicationController {
    public static String PROJECT_SETUP_MANAGEMENT_SERVICE = "project-setup-management";

    @Autowired
    private ProcessRoleService processRoleService;
    @Autowired
    private UserRestService userRestService;
    @Autowired
    private ApplicationPrintPopulator applicationPrintPopulator;
    @Autowired
    private ApplicationRestService applicationRestService;
    @Autowired
    private FormInputResponseRestService formInputResponseRestService;
    @Autowired
    private ApplicationSummaryRestService applicationSummaryRestService;
    @Autowired
    private ApplicationTeamModelManagementPopulator applicationTeamModelPopulator;
    @Autowired
    private ReinstateIneligibleApplicationModelPopulator reinstateIneligibleApplicationModelPopulator;
    @Autowired
    private ManagementApplicationPopulator managementApplicationPopulator;

    @SecuredBySpring(value = "TODO", description = "TODO")
    @PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin', 'support', 'innovation_lead', 'stakeholder')")
    @GetMapping("/{applicationId}")
    @AsyncMethod
    public String newApplicationSummary(@PathVariable("applicationId") final Long applicationId,
                                        @PathVariable("competitionId") final Long competitionId,
                                        @ModelAttribute(value = "ineligibleForm", binding = false) IneligibleApplicationForm form,
                                        BindingResult bindingResult,
                                        @RequestParam(value = "origin", defaultValue = "ALL_APPLICATIONS") String origin,
                                        @RequestParam MultiValueMap<String, String> queryParams,
                                        UserResource user,
                                        Model model) {
        model.addAttribute("model", managementApplicationPopulator.populate(applicationId, user, origin, queryParams));
        return "competition-mgt-application-overview";
    }

    @SecuredBySpring(value = "TODO", description = "TODO")
    @PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin', 'innovation_lead')")
    @PostMapping(value = "/{applicationId}", params = {"markAsIneligible"})
    public String markAsIneligible(@PathVariable("applicationId") final long applicationId,
                                   @PathVariable("competitionId") final long competitionId,
                                   @RequestParam(value = "origin", defaultValue = "ALL_APPLICATIONS") String origin,
                                   @RequestParam(value = "assessorId", required = false) Optional<Long> assessorId,
                                   @ModelAttribute("ineligibleForm") @Valid IneligibleApplicationForm form,
                                   @SuppressWarnings("unused") BindingResult bindingResult,
                                   ValidationHandler validationHandler,
                                   HttpServletRequest request,
                                   UserResource user,
                                   Model model) {
        // This is nasty, but we have to map the query parameters manually as Spring
        // will try to automatically map the POST request body to MultiValueMap
        // (causing issues with back links).
        // TODO: IFS-253 bind query parameters to maps properly
        MultiValueMap<String, String> queryParams = getQueryStringParameters(request);
        Supplier<String> failureVew = () -> newApplicationSummary(applicationId, competitionId, form, bindingResult, origin, queryParams, user, model);
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
    @PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin', 'support', 'innovation_lead', 'stakeholder')")
    @GetMapping("/{applicationId}/forminput/{formInputId}/download/**")
    public @ResponseBody
    ResponseEntity<ByteArrayResource> downloadQuestionFile(
            @PathVariable("applicationId") final Long applicationId,
            @PathVariable("formInputId") final Long formInputId,
            @PathVariable(value = "fileName", required = false) final String fileName,
            UserResource user) throws ExecutionException, InterruptedException {
        ProcessRoleResource processRole;
        if (hasProcessRole(user)) {
            processRole = userRestService.findProcessRole(user.getId(), applicationId).getSuccess();
        } else {
            long processRoleId = formInputResponseRestService.getByFormInputIdAndApplication(formInputId, applicationId).getSuccess().get(0).getUpdatedBy();
            processRole = processRoleService.getById(processRoleId).get();
        }

        final ByteArrayResource resource = formInputResponseRestService.getFile(formInputId, applicationId, processRole.getId()).getSuccess();
        final FormInputResponseFileEntryResource fileDetails = formInputResponseRestService.getFileDetails(formInputId, applicationId, processRole.getId()).getSuccess();
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

    @SecuredBySpring(value = "TODO", description = "TODO")
    @PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin', 'support', 'innovation_lead', 'stakeholder')")
    @GetMapping("/{applicationId}/team")
    public String displayApplicationTeam(@PathVariable("applicationId") final Long applicationId,
                                         @PathVariable("competitionId") final Long competitionId,
                                         @ModelAttribute(name = "loggedInUser", binding = false) UserResource user,
                                         @RequestParam MultiValueMap<String, String> queryParams,
                                         Model model) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        ApplicationTeamResource teamResource = applicationSummaryRestService.getApplicationTeam(applicationId).getSuccess();

        String params = UriComponentsBuilder.newInstance()
                .queryParams(queryParams)
                .build()
                .encode()
                .toUriString();
        model.addAttribute("model", applicationTeamModelPopulator.populateModel(application, teamResource, params));
        return "application/team-read-only";
    }

    private String doReinstateIneligibleApplicationConfirm(final Model model, final long applicationId) {
        ApplicationResource applicationResource = applicationRestService.getApplicationById(applicationId).getSuccess();
        model.addAttribute("model", reinstateIneligibleApplicationModelPopulator.populateModel(applicationResource));
        return "application/reinstate-ineligible-application-confirm";
    }

    private boolean hasProcessRole(UserResource user) {
        return !(isInternal(user) || user.hasRole(STAKEHOLDER));
    }
}
