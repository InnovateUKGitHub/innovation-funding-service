package org.innovateuk.ifs.application.forms.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.forms.saver.ApplicationSectionSaver;
import org.innovateuk.ifs.application.forms.service.ApplicationRedirectionService;
import org.innovateuk.ifs.application.overheads.OverheadFileSaver;
import org.innovateuk.ifs.application.populator.ApplicationNavigationPopulator;
import org.innovateuk.ifs.application.populator.section.AbstractSectionPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.viewmodel.section.AbstractSectionViewModel;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.form.ApplicationForm;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.origin.ApplicationSummaryOrigin;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.*;
import static org.innovateuk.ifs.origin.BackLinkUtil.buildOriginQueryString;
import static org.innovateuk.ifs.user.resource.Role.SUPPORT;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;

/**
 * This controller will handle all submit requests that are related to the application form.
 */
@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form")
public class ApplicationSectionController {

    private static final Log LOG = LogFactory.getLog(ApplicationSectionController.class);

    @Autowired
    private ApplicantRestService applicantRestService;

    @Autowired
    private ApplicationNavigationPopulator applicationNavigationPopulator;

    @Autowired
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    @Autowired
    private OverheadFileSaver overheadFileSaver;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private ApplicationRedirectionService applicationRedirectionService;

    @Autowired
    private ApplicationSectionSaver applicationSaver;

    @Autowired
    private UserRestService userRestService;

    @Autowired
    private ApplicationService applicationService;

    private Map<SectionType, AbstractSectionPopulator> sectionPopulators;

    @Autowired
    private void setPopulators(List<AbstractSectionPopulator> populators) {
        sectionPopulators = populators.stream().collect(toMap(AbstractSectionPopulator::getSectionType, Function.identity()));
    }

    @SecuredBySpring(value = "TODO", description = "TODO")
    @PreAuthorize("hasAnyAuthority('support', 'innovation_lead', 'ifs_administrator', 'comp_admin', 'project_finance', 'stakeholder')")
    @GetMapping("/{sectionType}/{applicantOrganisationId}")
    public String redirectToSectionManagement(@PathVariable("sectionType") SectionType type,
                                              @PathVariable(APPLICATION_ID) Long applicationId,
                                              @PathVariable long applicantOrganisationId,
                                              @RequestParam(value = "origin", defaultValue = "APPLICANT_DASHBOARD") String origin,
                                              @RequestParam MultiValueMap<String, String> queryParams) {

        String originQuery = buildOriginQueryString(ApplicationSummaryOrigin.valueOf(origin), queryParams);
        return applicationRedirectionService.redirectToSection(type, applicationId) + "/" + applicantOrganisationId + originQuery;
    }


    @SecuredBySpring(value = "TODO", description = "TODO")
    @PreAuthorize("hasAuthority('applicant')")
    @GetMapping("/{sectionType}")
    public String redirectToSection(@PathVariable("sectionType") SectionType type,
                                    @PathVariable(APPLICATION_ID) Long applicationId) {
        return applicationRedirectionService.redirectToSection(type, applicationId);
    }

    @SecuredBySpring(value = "TODO", description = "TODO")
    @PreAuthorize("hasAuthority('applicant')")
    @GetMapping(SECTION_URL + "{sectionId}")
    public String applicationFormWithOpenSection(@Valid @ModelAttribute(name = MODEL_ATTRIBUTE_FORM, binding = false) ApplicationForm form, BindingResult bindingResult, Model model,
                                                 @PathVariable(APPLICATION_ID) final Long applicationId,
                                                 @PathVariable("sectionId") final Long sectionId,
                                                 UserResource user) {

        ApplicantSectionResource applicantSection = applicantRestService.getSection(user.getId(), applicationId, sectionId);

        switch (applicantSection.getSection().getType()) {
            case FUNDING_FINANCES: {
                return String.format("redirect:/application/%d/form/your-funding/%d", applicationId, sectionId);
            }
            case PROJECT_LOCATION: {
                return String.format("redirect:/application/%d/form/your-project-location/%d", applicationId, sectionId);
            }
            default: {
                boolean isSupport = user.hasRole(SUPPORT);
                populateSection(model, form, bindingResult, applicantSection, false, Optional.empty(), false, Optional.empty(), isSupport);
                return APPLICATION_FORM;
            }
        }
    }

    @SecuredBySpring(value = "ApplicationSectionController", description = "Internal users can access the sections in the 'Your Finances'")
    @PreAuthorize("hasAnyAuthority('support', 'innovation_lead', 'ifs_administrator', 'comp_admin', 'project_finance', 'stakeholder')")
    @GetMapping(SECTION_URL + "{sectionId}/{applicantOrganisationId}")
    public String applicationFormWithOpenSectionForApplicant(@Valid @ModelAttribute(name = MODEL_ATTRIBUTE_FORM, binding = false) ApplicationForm form,
                                                             BindingResult bindingResult,
                                                             Model model,
                                                             @PathVariable(APPLICATION_ID) final Long applicationId,
                                                             @PathVariable("sectionId") final Long sectionId,
                                                             @PathVariable("applicantOrganisationId") final Long applicantOrganisationId,
                                                             UserResource user,
                                                             @RequestParam(value = "origin", defaultValue = "APPLICANT_DASHBOARD") String origin,
                                                             @RequestParam MultiValueMap<String, String> queryParams) {

        String originQuery = buildOriginQueryString(ApplicationSummaryOrigin.valueOf(origin), queryParams);
        model.addAttribute("originQuery", originQuery);
        boolean isSupport = user.hasRole(SUPPORT);

        ApplicationResource application = applicationService.getById(applicationId);
        List<ProcessRoleResource> processRoles = userRestService.findProcessRole(application.getId()).getSuccess();

        ProcessRoleResource applicantUser = processRoles.stream()
                .filter(pr -> pr.getOrganisationId().equals(applicantOrganisationId) && Arrays.asList(Role.LEADAPPLICANT.getName(),
                        Role.COLLABORATOR.getName()).contains(pr.getRoleName()))
                .findFirst()
                .orElseThrow(ObjectNotFoundException::new);

        ApplicantSectionResource applicantSection = applicantRestService.getSection(applicantUser.getUser(), applicationId, sectionId);
        if (applicantSection.getSection().getType() == SectionType.FUNDING_FINANCES) {
            return String.format("redirect:/application/%d/form/your-funding/%d/%d%s", applicationId, sectionId, applicantOrganisationId, originQuery);
        }
        populateSection(model, form, bindingResult, applicantSection, true, Optional.of(applicantOrganisationId), true, Optional.of(originQuery), isSupport);
        return APPLICATION_FORM;
    }

    @SecuredBySpring(value = "TODO", description = "TODO")
    @PreAuthorize("hasAuthority('applicant')")
    @PostMapping(SECTION_URL + "{sectionId}")
    public String applicationFormSubmit(@Valid @ModelAttribute(MODEL_ATTRIBUTE_FORM) ApplicationForm form,
                                        BindingResult bindingResult, ValidationHandler validationHandler,
                                        Model model,
                                        @PathVariable(APPLICATION_ID) final Long applicationId,
                                        @PathVariable("sectionId") final Long sectionId,
                                        UserResource user,
                                        HttpServletRequest request,
                                        HttpServletResponse response) {

        logSaveApplicationBindingErrors(validationHandler);

        ApplicantSectionResource applicantSection = applicantRestService.getSection(user.getId(), applicationId, sectionId);

        model.addAttribute("form", form);

        Map<String, String[]> params = request.getParameterMap();

        boolean isSupport = user.hasRole(SUPPORT);

        boolean validFinanceTerms = validFinanceTermsForMarkAsComplete(
                applicationId,
                form, bindingResult,
                applicantSection.getSection(),
                params,
                user.getId());

        ValidationMessages saveApplicationErrors = applicationSaver.saveApplicationForm(
                applicantSection.getApplication(),
                applicantSection.getCompetition().getId(),
                form,
                sectionId,
                user.getId(),
                request,
                response,
                validFinanceTerms);

        if (params.containsKey(ASSIGN_QUESTION_PARAM)) {
            questionService.assignQuestion(applicationId, user, request);
            cookieFlashMessageFilter.setFlashMessage(response, "assignedQuestion");
        }

        if (!isSaveAndReturnRequest(params) && (saveApplicationErrors.hasErrors() || !validFinanceTerms || overheadFileSaver.isOverheadFileRequest(request))) {
            validationHandler.addAnyErrors(saveApplicationErrors);
            populateSection(model, form, bindingResult, applicantSection, false, Optional.empty(), false, Optional.empty(), isSupport);
            return APPLICATION_FORM;
        } else {
            return applicationRedirectionService.getRedirectUrl(request, applicationId, Optional.of(applicantSection.getSection().getType()));
        }
    }

    private void populateSection(Model model,
                                 ApplicationForm form,
                                 BindingResult bindingResult,
                                 ApplicantSectionResource applicantSection,
                                 boolean readOnly,
                                 Optional<Long> applicantOrganisationId,
                                 boolean readOnlyAllApplicantApplicationFinances,
                                 Optional<String> originQuery,
                                 boolean isSupport) {
        AbstractSectionViewModel sectionViewModel = sectionPopulators.get(applicantSection.getSection().getType()).populate(applicantSection, form, model, bindingResult, readOnly, applicantOrganisationId, readOnlyAllApplicantApplicationFinances);
        applicationNavigationPopulator.addAppropriateBackURLToModel(applicantSection.getApplication().getId(), model, applicantSection.getSection(), applicantOrganisationId, originQuery, isSupport);
        model.addAttribute("model", sectionViewModel);
        model.addAttribute("form", form);
    }


    private void logSaveApplicationBindingErrors(ValidationHandler validationHandler) {
        if (LOG.isDebugEnabled())
            validationHandler.getAllErrors().forEach(e -> LOG.debug("Validations on application : " + e.getObjectName() + " v: " + e.getDefaultMessage()));
    }

    private boolean validFinanceTermsForMarkAsComplete(
            long applicationId,
            ApplicationForm form,
            BindingResult bindingResult,
            SectionResource section,
            Map<String, String[]> params,
            Long userId
    ) {

        if (!isMarkSectionAsCompleteRequest(params)) {
            return true;
        }

        switch (section.getType()) {
            case PROJECT_COST_FINANCES:
                return userIsResearch(userId, applicationId) ||
                        validateStateAidAgreement(form, bindingResult);

            case ORGANISATION_FINANCES:
                return validateOrganisationSizeSelected(applicationId, params, userId, bindingResult);

            default:
                return true;
        }
    }

    private boolean userIsResearch(long userId, long applicationId) {
        return organisationRestService.getByUserAndApplicationId(userId, applicationId).getSuccess().getOrganisationType().equals(OrganisationTypeEnum.RESEARCH.getId());
    }

    private boolean validateStateAidAgreement(ApplicationForm form, BindingResult bindingResult) {
        if (form.isStateAidAgreed()) {
            return true;
        }
        bindingResult.rejectValue(STATE_AID_AGREED_KEY, "APPLICATION_AGREE_STATE_AID_CONDITIONS");
        return false;
    }

    private boolean validateOrganisationSizeSelected(
            long applicationId,
            Map<String, String[]> params,
            Long userId,
            BindingResult bindingResult
    ) {
        List<String> financePositionKeys = simpleFilter(params.keySet(), k -> k.contains("financePosition-"));
        if (!financePositionKeys.isEmpty() || userIsResearch(userId, applicationId)) {
            return true;
        }
        bindingResult.rejectValue(ORGANISATION_SIZE_KEY, "APPLICATION_ORGANISATION_SIZE_REQUIRED");
        return false;
    }
}
