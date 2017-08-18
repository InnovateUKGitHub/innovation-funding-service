package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.populator.ApplicationPrintPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.resource.ApplicationTeamResource;
import org.innovateuk.ifs.application.resource.FormInputResponseFileEntryResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.management.form.ReinstateIneligibleApplicationForm;
import org.innovateuk.ifs.management.model.ApplicationTeamModelPopulator;
import org.innovateuk.ifs.management.model.ReinstateIneligibleApplicationModelPopulator;
import org.innovateuk.ifs.management.service.CompetitionManagementApplicationService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.user.service.ProcessRoleService;
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
import static org.innovateuk.ifs.util.HttpUtils.getQueryStringParameters;

/**
 * Handles the Competition Management Application overview page (and associated actions).
 */
@Controller
@RequestMapping("/competition/{competitionId}/application")
@PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin', 'support', 'innovation_lead')")
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
    private CompetitionManagementApplicationService competitionManagementApplicationService;
    @Autowired
    private ApplicationSummaryRestService applicationSummaryRestService;
    @Autowired
    private ApplicationTeamModelPopulator applicationTeamModelPopulator;

    @Autowired
    private ReinstateIneligibleApplicationModelPopulator reinstateIneligibleApplicationModelPopulator;

    @GetMapping("/{applicationId}")
    public String displayApplicationOverview(@PathVariable("applicationId") final Long applicationId,
                                             @PathVariable("competitionId") final Long competitionId,
                                             @ModelAttribute(name = "form", binding = false) ApplicationForm form,
                                             UserResource user,
                                             @RequestParam(value = "origin", defaultValue = "ALL_APPLICATIONS") String origin,
                                             @RequestParam(value = "assessorId", required = false) Optional<Long> assessorId,
                                             @RequestParam MultiValueMap<String, String> queryParams,
                                             Model model) {
        return competitionManagementApplicationService
                .validateApplicationAndCompetitionIds(applicationId, competitionId, (application) -> competitionManagementApplicationService
                        .displayApplicationOverview(user, competitionId, form, origin, queryParams, model, application, assessorId));
    }

    @PostMapping(value = "/{applicationId}", params = {"markAsIneligible"})
    public String markAsIneligible(@PathVariable("applicationId") final long applicationId,
                                   @PathVariable("competitionId") final long competitionId,
                                   @RequestParam(value = "origin", defaultValue = "ALL_APPLICATIONS") String origin,
                                   @RequestParam(value = "assessorId", required = false) Optional<Long> assessorId,
                                   @ModelAttribute("form") @Valid ApplicationForm applicationForm,
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

        return validationHandler.failNowOrSucceedWith(
                () -> displayApplicationOverview(applicationId, competitionId, applicationForm, user, origin, assessorId, queryParams, model),
                () -> competitionManagementApplicationService
                                .markApplicationAsIneligible(
                                        applicationId,
                                        competitionId,
                                        assessorId,
                                        origin,
                                        queryParams,
                                        applicationForm,
                                        user,
                                        model)
        );
    }

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

    @GetMapping(value = "/{applicationId}/reinstateIneligibleApplication/confirm")
    public String reinstateIneligibleApplicationConfirm(final Model model,
                                                        @ModelAttribute("form") final ReinstateIneligibleApplicationForm form,
                                                        @PathVariable("applicationId") final long applicationId) {
        return doReinstateIneligibleApplicationConfirm(model, applicationId);
    }

    @GetMapping("/{applicationId}/forminput/{formInputId}/download")
    public
    @ResponseBody
    ResponseEntity<ByteArrayResource> downloadQuestionFile(
            @PathVariable("applicationId") final Long applicationId,
            @PathVariable("formInputId") final Long formInputId,
            UserResource user) throws ExecutionException, InterruptedException {
        ProcessRoleResource processRole;
        if (isInternal(user)) {
            long processRoleId = formInputResponseRestService.getByFormInputIdAndApplication(formInputId, applicationId).getSuccessObjectOrThrowException().get(0).getUpdatedBy();
            processRole = processRoleService.getById(processRoleId).get();
        } else {
            processRole = processRoleService.findProcessRole(user.getId(), applicationId);
        }

        final ByteArrayResource resource = formInputResponseRestService.getFile(formInputId, applicationId, processRole.getId()).getSuccessObjectOrThrowException();
        final FormInputResponseFileEntryResource fileDetails = formInputResponseRestService.getFileDetails(formInputId, applicationId, processRole.getId()).getSuccessObjectOrThrowException();
        return getFileResponseEntity(resource, fileDetails.getFileEntryResource());
    }

    /**
     * Printable version of the application
     */
    @GetMapping(value = "/{applicationId}/print")
    public String printManagementApplication(@PathVariable("applicationId") Long applicationId,
                                             @PathVariable("competitionId") Long competitionId,
                                             UserResource user,
                                             Model model) {
        return competitionManagementApplicationService
                .validateApplicationAndCompetitionIds(applicationId, competitionId, (application) -> applicationPrintPopulator.print(applicationId, model, user));
    }

    @GetMapping("/{applicationId}/team")
    public String displayApplicationTeam(@PathVariable("applicationId") final Long applicationId,
                                         @PathVariable("competitionId") final Long competitionId,
                                         @ModelAttribute(name = "loggedInUser", binding = false) UserResource user,
                                         @RequestParam MultiValueMap<String, String> queryParams,
                                         Model model) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccessObjectOrThrowException();
        ApplicationTeamResource teamResource = applicationSummaryRestService.getApplicationTeam(applicationId).getSuccessObjectOrThrowException();

        String params = UriComponentsBuilder.newInstance()
                .queryParams(queryParams)
                .build()
                .encode()
                .toUriString();
        model.addAttribute("model", applicationTeamModelPopulator.populateModel(application, teamResource, params));
        return "application/team-read-only";
    }

    private String doReinstateIneligibleApplicationConfirm(final Model model, final long applicationId) {
        ApplicationResource applicationResource = applicationRestService.getApplicationById(applicationId).getSuccessObjectOrThrowException();
        model.addAttribute("model", reinstateIneligibleApplicationModelPopulator.populateModel(applicationResource));
        return "application/reinstate-ineligible-application-confirm";
    }

    // TODO: review when IFS-1370 is implemented - RB
    private boolean isInternal(UserResource user) {
        return user.hasRole(UserRoleType.COMP_ADMIN) || user.hasRole(UserRoleType.PROJECT_FINANCE) || user.hasRole(UserRoleType.SUPPORT) || user.hasRole(UserRoleType.INNOVATION_LEAD);
    }
}
