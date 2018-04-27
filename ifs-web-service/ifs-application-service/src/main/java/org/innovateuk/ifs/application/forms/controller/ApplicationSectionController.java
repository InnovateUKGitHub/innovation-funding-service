package org.innovateuk.ifs.application.forms.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.user.viewmodel.UserApplicationRole;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.forms.saver.ApplicationSectionSaver;
import org.innovateuk.ifs.application.forms.service.ApplicationRedirectionService;
import org.innovateuk.ifs.application.overheads.OverheadFileSaver;
import org.innovateuk.ifs.application.populator.ApplicationNavigationPopulator;
import org.innovateuk.ifs.application.populator.section.AbstractSectionPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.viewmodel.section.AbstractSectionViewModel;
import org.innovateuk.ifs.commons.error.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    private OrganisationService organisationService;

    @Autowired
    private ApplicationRedirectionService applicationRedirectionService;

    @Autowired
    private ApplicationSectionSaver applicationSaver;

    @Autowired
    private ProcessRoleService processRoleService;

    @Autowired
    private ApplicationService applicationService;

    private Map<SectionType, AbstractSectionPopulator> sectionPopulators;

    @Autowired
    private void setPopulators(List<AbstractSectionPopulator> populators) {
        sectionPopulators = populators.stream().collect(toMap(AbstractSectionPopulator::getSectionType, Function.identity()));
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
        populateSection(model, form, bindingResult, applicantSection, false, Optional.empty(), false);
        return APPLICATION_FORM;
    }

    @SecuredBySpring(value = "TODO", description = "TODO")
    @PreAuthorize("hasAnyAuthority('support', 'innovation_lead')")
    @GetMapping(SECTION_URL + "{sectionId}/{applicantOrganisationId}")
    public String applicationFormWithOpenSectionForApplicant(@Valid @ModelAttribute(name = MODEL_ATTRIBUTE_FORM, binding = false) ApplicationForm form, BindingResult bindingResult, Model model,
                                                             @PathVariable(APPLICATION_ID) final Long applicationId,
                                                             @PathVariable("sectionId") final Long sectionId,
                                                             @PathVariable("applicantOrganisationId") final Long applicantOrganisationId,
                                                             UserResource user) {

        ApplicationResource application = applicationService.getById(applicationId);

        ProcessRoleResource applicantUser = processRoleService.getByApplicationId(application.getId()).stream().filter(pr -> pr.getOrganisationId().equals(applicantOrganisationId) && Arrays.asList(UserApplicationRole.LEAD_APPLICANT.getRoleName(), UserApplicationRole.COLLABORATOR.getRoleName()).contains(pr.getRoleName())).findFirst().orElseThrow(() -> new ObjectNotFoundException());

        ApplicantSectionResource applicantSection = applicantRestService.getSection(applicantUser.getUser(), applicationId, sectionId);
        populateSection(model, form, bindingResult, applicantSection, true, Optional.of(applicantOrganisationId), true);
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

        boolean validFinanceTerms = validFinanceTermsForMarkAsComplete(
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

        if (saveApplicationErrors.hasErrors() || !validFinanceTerms || overheadFileSaver.isOverheadFileRequest(request)) {
            validationHandler.addAnyErrors(saveApplicationErrors);
            populateSection(model, form, bindingResult, applicantSection, false, Optional.empty(), false);
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
                                 boolean readOnlyAllApplicantApplicationFinances) {
        AbstractSectionViewModel sectionViewModel = sectionPopulators.get(applicantSection.getSection().getType()).populate(applicantSection, form, model, bindingResult, readOnly, applicantOrganisationId, readOnlyAllApplicantApplicationFinances);
        applicationNavigationPopulator.addAppropriateBackURLToModel(applicantSection.getApplication().getId(), model, applicantSection.getSection(), applicantOrganisationId);
        model.addAttribute("model", sectionViewModel);
        model.addAttribute("form", form);
    }


    private void logSaveApplicationBindingErrors(ValidationHandler validationHandler) {
        if (LOG.isDebugEnabled())
            validationHandler.getAllErrors().forEach(e -> LOG.debug("Validations on application : " + e.getObjectName() + " v: " + e.getDefaultMessage()));
    }

    private boolean validFinanceTermsForMarkAsComplete(
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

            case FUNDING_FINANCES:
                return validateOtherFundingSelectionMade(params, bindingResult)
                        && validateTermsAndConditionsAgreement(form, bindingResult);

            case PROJECT_COST_FINANCES:
                return userIsResearch(userId) ?
                        validateTermsAndConditionsAgreement(form, bindingResult) :
                        validateStateAidAgreement(form, bindingResult);

            case ORGANISATION_FINANCES:
                return validateOrganisationSizeSelected(params, userId, bindingResult);

            default:
                return true;
        }
    }

    private boolean userIsResearch(Long userId) {
        return organisationService.getOrganisationForUser(userId).getOrganisationType().equals(OrganisationTypeEnum.RESEARCH.getId());
    }

    private boolean validateTermsAndConditionsAgreement(ApplicationForm form, BindingResult bindingResult) {
        if (form.isTermsAgreed()) {
            return true;
        }
        bindingResult.rejectValue(TERMS_AGREED_KEY, "APPLICATION_AGREE_TERMS_AND_CONDITIONS");
        return false;
    }

    private boolean validateOtherFundingSelectionMade(Map<String, String[]> params, BindingResult bindingResult) {

        List<String> publicFundingKeys = simpleFilter(params.keySet(), k -> k.contains("-otherPublicFunding"));
        if (!publicFundingKeys.isEmpty()) {
            return true;
        }

        bindingResult.rejectValue("formInput[cost-otherPublicFunding]", "validation.finance.other.funding.required");
        return false;
    }

    private boolean validateStateAidAgreement(ApplicationForm form, BindingResult bindingResult) {
        if (form.isStateAidAgreed()) {
            return true;
        }
        bindingResult.rejectValue(STATE_AID_AGREED_KEY, "APPLICATION_AGREE_STATE_AID_CONDITIONS");
        return false;
    }

    private boolean validateOrganisationSizeSelected(
            Map<String, String[]> params,
            Long userId,
            BindingResult bindingResult
    ) {
        List<String> financePositionKeys = simpleFilter(params.keySet(), k -> k.contains("financePosition-"));
        if (!financePositionKeys.isEmpty() || userIsResearch(userId)) {
            return true;
        }
        bindingResult.rejectValue(ORGANISATION_SIZE_KEY, "APPLICATION_ORGANISATION_SIZE_REQUIRED");
        return false;
    }
}
