package org.innovateuk.ifs.application.terms.controller;

import org.innovateuk.ifs.application.common.populator.ApplicationTermsModelPopulator;
import org.innovateuk.ifs.application.common.populator.ApplicationTermsPartnerModelPopulator;
import org.innovateuk.ifs.application.common.viewmodel.ApplicationTermsViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.application.terms.form.ApplicationTermsForm;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.exception.ForbiddenActionException;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.origin.ApplicationSummaryOrigin;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Supplier;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;
import static org.innovateuk.ifs.origin.BackLinkUtil.buildBackUrl;

@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form/question/{questionId}/terms-and-conditions")
@PreAuthorize("hasAnyAuthority('applicant', 'project_finance', 'ifs_administrator', 'comp_admin', 'support', 'innovation_lead', 'monitoring_officer', 'interview_assessor', 'panel_assessor', 'assessor')")
@SecuredBySpring(value = "Controller",
        description = "Only applicants are allowed to view the application terms",
        securedType = ApplicationTermsController.class)
public class ApplicationTermsController {

    private UserRestService userRestService;
    private ApplicationTermsModelPopulator applicationTermsModelPopulator;
    private QuestionStatusRestService questionStatusRestService;
    private ApplicationRestService applicationRestService;
    private ApplicationTermsPartnerModelPopulator applicationTermsPartnerModelPopulator;


    public ApplicationTermsController(UserRestService userRestService,
                                      QuestionStatusRestService questionStatusRestService,
                                      ApplicationRestService applicationRestService,
                                      ApplicationTermsPartnerModelPopulator applicationTermsPartnerModelPopulator,
                                      ApplicationTermsModelPopulator applicationTermsModelPopulator) {
        this.userRestService = userRestService;
        this.questionStatusRestService = questionStatusRestService;
        this.applicationRestService = applicationRestService;
        this.applicationTermsModelPopulator = applicationTermsModelPopulator;
        this.applicationTermsPartnerModelPopulator = applicationTermsPartnerModelPopulator;
    }

    @GetMapping
    public String getTerms(@PathVariable long applicationId,
                           @PathVariable long questionId,
                           UserResource user,
                           Model model,
                           @ModelAttribute(name = "form", binding = false) ApplicationTermsForm form,
                           @RequestParam(value = "origin", defaultValue = "APPLICATION") String origin,
                           @RequestParam(value = "readonly", defaultValue = "false") Boolean readOnly,
                           @RequestParam MultiValueMap<String, String> queryParams) {

        ApplicationTermsViewModel viewModel = applicationTermsModelPopulator.populate(user, applicationId, questionId, readOnly);
        model.addAttribute("model", viewModel);

        model.addAttribute("backUrl", backUrlFromOrigin(applicationId, viewModel.getCompetitionId(), origin, queryParams));
        model.addAttribute("backLabel", backLabelFromOrigin(origin));

        return "application/terms-and-conditions";
    }

    private static String backUrlFromOrigin(long applicationId, long competitionId, String origin, MultiValueMap<String, String> queryParams) {
        queryParams.put("applicationId", singletonList(String.valueOf(applicationId)));
        queryParams.put("competitionId", singletonList(String.valueOf(competitionId)));
        return buildBackUrl(ApplicationSummaryOrigin.valueOf(origin), queryParams, "applicationId", "competitionId", "projectId");
    }

    private static String backLabelFromOrigin(String origin) {
        return ApplicationSummaryOrigin.valueOf(origin).getTitle();
    }

    @PostMapping
    public String acceptTerms(@PathVariable long applicationId,
                              @PathVariable long questionId,
                              UserResource user,
                              Model model,
                              @ModelAttribute(name = "form", binding = false) ApplicationTermsForm form,
                              @RequestParam(value = "origin", defaultValue = "APPLICATION") String origin,
                              @RequestParam MultiValueMap<String, String> queryParams,
                              @SuppressWarnings("unused") BindingResult bindingResult,
                              ValidationHandler validationHandler) {
        Supplier<String> failureView = () -> getTerms(applicationId, questionId, user, model, form, origin, false, queryParams);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {

            ProcessRoleResource processRole = userRestService.findProcessRole(user.getId(), applicationId).getSuccess();
            RestResult<List<ValidationMessages>> result = questionStatusRestService.markAsComplete(questionId, applicationId, processRole.getId());

            return validationHandler.addAnyErrors(result, fieldErrorsToFieldErrors(), asGlobalErrors())
                    .failNowOrSucceedWith(
                            failureView,
                            () -> format("redirect:%s%d/form/question/%d/terms-and-conditions#terms-accepted", APPLICATION_BASE_URL, applicationId, questionId));
        });
    }

    @GetMapping("/partner-status")
    public String getPartnerStatus(@PathVariable long applicationId, @PathVariable long questionId, Model model) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        if (!application.isOpen()) {
            throw new ForbiddenActionException("Cannot view partners on a non-open application");
        }
        if (!application.isCollaborativeProject()) {
            throw new ForbiddenActionException("Cannot view partners on a non-collaborative application");
        }

        model.addAttribute("model", applicationTermsPartnerModelPopulator.populate(application, questionId));
        return "application/terms-and-conditions-partner-status";
    }
}