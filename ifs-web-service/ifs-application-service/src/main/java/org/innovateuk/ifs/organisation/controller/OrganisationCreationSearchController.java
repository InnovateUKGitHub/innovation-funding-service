package org.innovateuk.ifs.organisation.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.address.form.AddressForm;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.organisation.resource.OrganisationExecutiveOfficerResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationSicCodeResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.organisation.viewmodel.OrganisationAddressViewModel;
import org.innovateuk.ifs.registration.form.OrganisationCreationForm;
import org.innovateuk.ifs.registration.form.OrganisationTypeForm;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Locale;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.innovateuk.ifs.address.form.AddressForm.FORM_ACTION_PARAMETER;
import static org.innovateuk.ifs.util.ExceptionFunctions.getOrRethrow;
import static org.springframework.web.util.UriUtils.encodeQueryParam;

/**
 * Provides methods for both:
 * - Finding your company or research type organisation through Companies House or JES search.
 * - Verifying or amending the address attached to the organisation.
 */
@Controller
@RequestMapping(AbstractOrganisationCreationController.BASE_URL)
@SecuredBySpring(value = "Controller", description = "TODO", securedType = OrganisationCreationSearchController.class)
@PreAuthorize("permitAll")
public class OrganisationCreationSearchController extends AbstractOrganisationCreationController {

    private static final Log LOG = LogFactory.getLog(OrganisationCreationSearchController.class);

    private static final String SELECTED_ORGANISATION = "selected-organisation";
    private static final String SEARCH_RESULT_ORGANISATION = "search-organisation-results";
    private static final String SAVE_ORGANISATION_DETAILS = "save-organisation-details";
    private static final String REFERER = "referer";
    private static final String ORGANISATION_NAME = "organisationName";
    private static final String ORGANISATION_SEARCH_NAME = "organisationSearchName";
    private static final String MODEL = "model";
    private static final String SEARCH_ORGANISATION = "search-organisation";
    private static final String DEFAULT_PAGE_NUMBER = "1";
    private static final String SELECTED_ORGANISATION_MANUAL = "selected-organisation-manual";
    @Autowired
    private MessageSource messageSource;

    @GetMapping(value = {"/" + FIND_ORGANISATION, "/" + FIND_ORGANISATION + "/**"})
    public String createOrganisation(@ModelAttribute(name = ORGANISATION_FORM, binding = false) OrganisationCreationForm organisationForm,
                                     Model model,
                                     UserResource user,
                                     HttpServletRequest request,
                                     HttpServletResponse response) {
        registrationCookieService.deleteOrganisationIdCookie(response);
        organisationForm.setOrganisationSearching(false);
        organisationForm = getFormDataFromCookie(organisationForm, model, request);
        registrationCookieService.saveToOrganisationCreationCookie(organisationForm, response);

        return addAttributesAndRedirect(organisationForm, model, user, request);
    }

    private String addAttributesAndRedirect(OrganisationCreationForm organisationForm, Model model, UserResource user, HttpServletRequest request) {
        populateViewModelForSearch(organisationForm, model, request, 1);
        addPageSubtitleToModel(request, user, model);
        return TEMPLATE_PATH + "/" + FIND_ORGANISATION;
    }

    @PostMapping(value = "/" + FIND_ORGANISATION + "/**", params = SEARCH_ORGANISATION)
    public String searchOrganisation(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                     BindingResult bindingResult,
                                     HttpServletRequest request, HttpServletResponse response) {
        addOrganisationType(organisationForm, organisationTypeIdFromCookie(request));
        organisationForm.setOrganisationSearching(true);
        organisationForm.setManualEntry(false);
        registrationCookieService.saveToOrganisationCreationCookie(organisationForm, response);
        if (isNewOrganisationSearchEnabled && !organisationForm.isResearch()) {
            return displayImprovedSearchOrganisationResults(organisationForm, request, bindingResult);
        }
        return "redirect:/organisation/create/" + FIND_ORGANISATION + "?searchTerm=" + escapePathVariable(organisationForm.getOrganisationSearchName());
    }

    private String displayImprovedSearchOrganisationResults(OrganisationCreationForm organisationForm, HttpServletRequest request, BindingResult bindingResult) {
        boolean isSearchResultRequestURI = request.getHeader("referer").contains(SEARCH_RESULT_ORGANISATION);
        bindingResult = new BeanPropertyBindingResult(organisationForm, ORGANISATION_FORM);
        validator.validate(organisationForm, bindingResult);

        if (!isSearchResultRequestURI && bindingResult.hasFieldErrors(ORGANISATION_SEARCH_NAME)) {
            return "redirect:/organisation/create/" + FIND_ORGANISATION + "?searchTerm=" + escapePathVariable(organisationForm.getOrganisationSearchName());
        }
        return "redirect:/organisation/create/" + SEARCH_RESULT_ORGANISATION + "?searchTerm=" + escapePathVariable(organisationForm.getOrganisationSearchName());
    }

    @GetMapping("/" + EXISTING_ORGANISATION + "/{selectedExistingOrganisationId}")
    public String searchExistingOrganisation(@ModelAttribute(name = ORGANISATION_FORM, binding = false) OrganisationCreationForm organisationForm,
                                             @PathVariable("selectedExistingOrganisationId") final Long selectedOrganisationId,
                                             Model model,
                                             UserResource user,
                                             HttpServletRequest request,
                                             HttpServletResponse response) {
        OrganisationResource selectedOrganisation = organisationRestService.getOrganisationById(selectedOrganisationId).getSuccess();

        organisationForm.setSelectedExistingOrganisationId(selectedOrganisation.getId());
        organisationForm.setOrganisationTypeId(selectedOrganisation.getOrganisationType());
        organisationForm.setSelectedExistingOrganisationName(selectedOrganisation.getName());
        organisationForm.setOrganisationSearching(false);
        organisationForm.setManualEntry(false);

        OrganisationTypeForm organisationTypeForm = new OrganisationTypeForm();
        organisationTypeForm.setOrganisationType(selectedOrganisation.getOrganisationType());

        registrationCookieService.saveToOrganisationIdCookie(selectedOrganisation.getId(), response);
        registrationCookieService.saveToOrganisationTypeCookie(organisationTypeForm, response);
        registrationCookieService.saveToOrganisationCreationCookie(organisationForm, response);

        model.addAttribute("subtitle", "Your organisation");

        return addAttributesAndRedirect(organisationForm, model, user, request);
    }

    @GetMapping(value = {"/" + SEARCH_RESULT_ORGANISATION + "/**"})
    public String searchOrganisation(@ModelAttribute(name = ORGANISATION_FORM, binding = false) OrganisationCreationForm organisationForm,
                                     Model model,
                                     UserResource user,
                                     HttpServletRequest request,
                                     HttpServletResponse response,
                                     @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER) int pageNumber) {

        registrationCookieService.deleteOrganisationIdCookie(response);
        organisationForm = getImprovedSearchFormDataFromCookie(organisationForm, model, request, pageNumber, true);
        registrationCookieService.saveToOrganisationCreationCookie(organisationForm, response);

        populateViewModelForSearch(organisationForm, model, request, pageNumber);
        addPageSubtitleToModel(request, user, model);
        addPageResourceToModel(organisationForm, model, pageNumber);
        return TEMPLATE_PATH + "/" + SEARCH_RESULT_ORGANISATION;
    }

    @GetMapping("/" + SELECTED_ORGANISATION + "/{searchOrganisationId}")
    public String selectOrganisationForConfiramtion(@ModelAttribute(name = ORGANISATION_FORM, binding = false) OrganisationCreationForm organisationForm,
                                                    Model model,
                                                    @PathVariable("searchOrganisationId") final String searchOrganisationId,
                                                    HttpServletRequest request,
                                                    HttpServletResponse response,
                                                    UserResource user) {

        addOrganisationType(organisationForm, organisationTypeIdFromCookie(request));
        organisationForm.setSearchOrganisationId(searchOrganisationId);
        addOrganisationSearchName(organisationForm, request);
        addSelectedOrganisation(organisationForm, model);
        registrationCookieService.saveToOrganisationCreationCookie(organisationForm, response);
        populateViewModelForSelectedOrgConfirmation(organisationForm, model, request);
        addPageSubtitleToModel(request, user, model);
        return TEMPLATE_PATH + "/" + CONFIRM_ORGANISATION; // here go to save
    }

    @PostMapping("/" + SELECTED_ORGANISATION_MANUAL)
    public String selectManualOrganisationForConfirmation(@Valid @ModelAttribute(name = ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                                          BindingResult bindingResult,
                                                          ValidationHandler validationHandler,
                                                          Model model,
                                                          HttpServletRequest request,
                                                          HttpServletResponse response,
                                                          UserResource user) {
        removeEmptyJsFormFields(organisationForm);

        if (bindingResult.hasFieldErrors()) {
            return TEMPLATE_PATH + "/" + MANUALLY_ENTER_ORGANISATION_DETAILS;
        }

        OrganisationCreationForm organisationFormFromCookie = getFormDataForManualEntryFromCookie(request);
        addManualOrganisation(organisationForm, model, request, organisationFormFromCookie);
        registrationCookieService.saveToOrganisationCreationCookie(organisationForm, response);

        populateViewModelForSelectedOrgConfirmation(organisationForm, model, request);
        addPageSubtitleToModel(request, user, model);
        return TEMPLATE_PATH + "/" + CONFIRM_ORGANISATION; // here go to save
    }

    @GetMapping("/" + SELECTED_ORGANISATION_MANUAL)
    public String displayMauallyEnteredOrgForConfirmation(@Valid @ModelAttribute(name = ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                                          BindingResult bindingResult,
                                                          Model model,
                                                          HttpServletRequest request,
                                                          UserResource user) {
        OrganisationCreationForm organisationFormFromCookie = getFormDataOfSavedManualEntryFromCookie(organisationForm, request);
        populateManualEntryFormData(organisationFormFromCookie, model, request);
        populateViewModelForSelectedOrgConfirmation(organisationFormFromCookie, model, request);
        addPageSubtitleToModel(request, user, model);

        return TEMPLATE_PATH + "/" + CONFIRM_ORGANISATION; // here go to save
    }

    @PostMapping(value = {"organisation-type/" + MANUALLY_ENTER_ORGANISATION_DETAILS, "/" + SELECTED_ORGANISATION_MANUAL}, params = FORM_ACTION_PARAMETER)
    public String addressFormAction(Model model,
                                    @ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                    BindingResult bindingResult,
                                    ValidationHandler validationHandler,
                                    UserResource loggedInUser) {
        removeEmptyJsFormFields(organisationForm);

        organisationForm.getAddressForm().validateAction(bindingResult);
        if (validationHandler.hasErrors()) {
            return TEMPLATE_PATH + "/" + MANUALLY_ENTER_ORGANISATION_DETAILS;
        }

        AddressForm addressForm = organisationForm.getAddressForm();
        addressForm.handleAction(this::searchPostcode);

        return TEMPLATE_PATH + "/" + MANUALLY_ENTER_ORGANISATION_DETAILS;
    }

    @PostMapping(value = {"/" + SELECTED_ORGANISATION + "/**", "/" + FIND_ORGANISATION + "/**"}, params = SAVE_ORGANISATION_DETAILS)
    public String manualOrganisationSave(@Valid @ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                         BindingResult bindingResult,
                                         Model model,
                                         HttpServletRequest request, HttpServletResponse response,
                                         @RequestHeader(value = REFERER, required = false) final String referer) {
        removeEmptyJsFormFields(organisationForm);

        OrganisationCreationForm organisationCreationForm = registrationCookieService.getOrganisationCreationCookieValue(request).get();
        organisationCreationForm.setOrganisationName(organisationForm.getOrganisationName());
        organisationCreationForm.setSearchOrganisationId(organisationForm.getSearchOrganisationId());
        organisationCreationForm.setTriedToSave(true);
        organisationCreationForm.setManualEntry(true);
        organisationForm.setTriedToSave(true);

        addOrganisationType(organisationCreationForm, organisationTypeIdFromCookie(request));
        addSelectedOrganisation(organisationCreationForm, model);

        bindingResult = new BeanPropertyBindingResult(organisationCreationForm, ORGANISATION_FORM);
        validator.validate(organisationCreationForm, bindingResult);

        if (!bindingResult.hasFieldErrors(ORGANISATION_NAME)) {
            registrationCookieService.saveToOrganisationCreationCookie(organisationCreationForm, response);
            return "redirect:" + BASE_URL + "/" + CONFIRM_ORGANISATION;
        } else {
            organisationForm.setTriedToSave(true);
            registrationCookieService.saveToOrganisationCreationCookie(organisationCreationForm, response);
            return getRedirectUrlInvalidSave(organisationForm, referer);
        }
    }

    private void removeEmptyJsFormFields(OrganisationCreationForm organisationForm) {
        organisationForm.setExecutiveOfficers(organisationForm.getExecutiveOfficers()
                .stream().filter(execOfficer -> execOfficer.getName() != null)
                .collect(Collectors.toList()));
        organisationForm.setSicCodes(organisationForm.getSicCodes()
                .stream().filter(sicCode -> sicCode.getSicCode() != null)
                .collect(Collectors.toList()));
    }

    private void populateViewModelForSearch(OrganisationCreationForm organisationForm, Model model, HttpServletRequest request, int pageNumber) {
        model.addAttribute(ORGANISATION_FORM, organisationForm);
        model.addAttribute("isLeadApplicant", checkOrganisationIsLead(request));
        model.addAttribute("searchLabel", getMessageByOrganisationType(organisationForm.getOrganisationTypeEnum(), "SearchLabel", request.getLocale()));
        model.addAttribute("additionalLabel", getMessageByOrganisationType(organisationForm.getOrganisationTypeEnum(), "AdditionalLabel", request.getLocale()));
        model.addAttribute("searchHint", getMessageByOrganisationType(organisationForm.getOrganisationTypeEnum(), "SearchHint", request.getLocale()));
        model.addAttribute("organisationType", organisationTypeRestService.findOne(organisationForm.getOrganisationTypeId()).getSuccess());
        model.addAttribute("improvedSearchEnabled", isNewOrganisationSearchEnabled);
        model.addAttribute("currentPage", pageNumber);
    }

    private void populateViewModelForSelectedOrgConfirmation(OrganisationCreationForm organisationForm, Model model, HttpServletRequest request) {
        model.addAttribute("isLeadApplicant", checkOrganisationIsLead(request));
        model.addAttribute("isApplicantJourney", registrationCookieService.isApplicantJourney(request));
        model.addAttribute(ORGANISATION_FORM, organisationForm);
        model.addAttribute("organisationType", organisationTypeRestService.findOne(organisationForm.getOrganisationTypeId()).getSuccess());
        model.addAttribute("includeInternationalQuestion", registrationCookieService.getOrganisationInternationalCookieValue(request).isPresent());
        model.addAttribute(MODEL, new OrganisationAddressViewModel(organisationTypeRestService.findOne(organisationForm.getOrganisationTypeId()).getSuccess(), checkOrganisationIsLead(request)));
        model.addAttribute("improvedSearchEnabled", isNewOrganisationSearchEnabled);
    }

    private void populateViewModel(@ModelAttribute(ORGANISATION_FORM) @Valid OrganisationCreationForm organisationForm, Model model, HttpServletRequest request) {
        model.addAttribute("isLeadApplicant", checkOrganisationIsLead(request));
        model.addAttribute("isApplicantJourney", registrationCookieService.isApplicantJourney(request));
        model.addAttribute("organisationType", organisationTypeRestService.findOne(organisationForm.getOrganisationTypeId()).getSuccess());
        model.addAttribute("includeInternationalQuestion", registrationCookieService.getOrganisationInternationalCookieValue(request).isPresent());
        model.addAttribute(MODEL, new OrganisationAddressViewModel(organisationTypeRestService.findOne(organisationForm.getOrganisationTypeId()).getSuccess(), checkOrganisationIsLead(request)));
        model.addAttribute("improvedSearchEnabled", isNewOrganisationSearchEnabled);
    }

    private String getRedirectUrlInvalidSave(OrganisationCreationForm organisationForm, String referer) {
        String redirectPart;
        if (referer.contains(FIND_ORGANISATION)) {
            redirectPart = FIND_ORGANISATION;
            organisationForm.setSearchOrganisationId("");
        } else {
            redirectPart = SELECTED_ORGANISATION;
        }

        if (!referer.contains(FIND_ORGANISATION)) {
            if (isNotBlank(organisationForm.getSearchOrganisationId())) {
                return String.format("redirect:%s/%s/%s", BASE_URL, redirectPart, organisationForm.getSearchOrganisationId());
            } else {
                return String.format("redirect:%s/%s", BASE_URL, redirectPart);
            }
        } else {
            return String.format("redirect:%s/%s", BASE_URL, redirectPart);
        }
    }

    private String getMessageByOrganisationType(OrganisationTypeEnum orgTypeEnum, String textKey, Locale locale) {
        boolean improvedSearchEnabled = orgTypeEnum != null
                && orgTypeEnum != OrganisationTypeEnum.RESEARCH
                && isNewOrganisationSearchEnabled;

        String key = improvedSearchEnabled ? String.format("improved.registration.%s", textKey)
                : String.format("registration.%s.%s", orgTypeEnum.toString(), textKey);
        try {
            return messageSource.getMessage(key, null, locale);
        } catch (NoSuchMessageException e) {
            LOG.error("unable to get message for key: " + key + " and local: " + locale);
            return messageSource.getMessage(improvedSearchEnabled ? String.format("improved.registration.DEFAULT.%s", textKey)
                            : String.format("registration.DEFAULT.%s", textKey),
                    null, locale);
        }
    }

    private String escapePathVariable(final String input) {
        return getOrRethrow(() -> encodeQueryParam(input, "UTF-8"));
    }

    private boolean checkOrganisationIsLead(HttpServletRequest request) {
        return registrationCookieService.isLeadJourney(request);
    }

    @PostMapping(value = "/organisation-type/" + MANUALLY_ENTER_ORGANISATION_DETAILS, params = "add-sic-code")
    public String addSicCode(@Valid @ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                             BindingResult bindingResult,
                             Model model,
                             HttpServletRequest request, HttpServletResponse response,
                             @RequestHeader(value = REFERER, required = false) final String referer) {
        if (organisationForm.getSicCodes().size() > 3) {
            return TEMPLATE_PATH + "/" + MANUALLY_ENTER_ORGANISATION_DETAILS;
        }
        populateViewModel(organisationForm, model, request);

        organisationForm.getSicCodes().add(new OrganisationSicCodeResource());
        model.addAttribute(ORGANISATION_FORM, organisationForm);
        return TEMPLATE_PATH + "/" + MANUALLY_ENTER_ORGANISATION_DETAILS;
    }

    @PostMapping(value = "/organisation-type/" + MANUALLY_ENTER_ORGANISATION_DETAILS, params = "remove-sic-code")
    public String removeSicCode(@Valid @ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                BindingResult bindingResult,
                                Model model,
                                HttpServletRequest request, HttpServletResponse response,
                                @RequestParam("remove-sic-code") int index,
                                @RequestHeader(value = REFERER, required = false) final String referer) {

        populateViewModel(organisationForm, model, request);

        organisationForm.getSicCodes().remove(index);
        model.addAttribute(ORGANISATION_FORM, organisationForm);
        return TEMPLATE_PATH + "/" + MANUALLY_ENTER_ORGANISATION_DETAILS;
    }

    @PostMapping(value = "/organisation-type/" + MANUALLY_ENTER_ORGANISATION_DETAILS, params = "add-exec-officer")
    public String addExecutiveOfficer(@Valid @ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                      BindingResult bindingResult,
                                      Model model,
                                      HttpServletRequest request, HttpServletResponse response,
                                      @RequestHeader(value = REFERER, required = false) final String referer) {

        populateViewModel(organisationForm, model, request);

        organisationForm.getExecutiveOfficers().add(new OrganisationExecutiveOfficerResource());
        model.addAttribute(ORGANISATION_FORM, organisationForm);
        return TEMPLATE_PATH + "/" + MANUALLY_ENTER_ORGANISATION_DETAILS;
    }

    @PostMapping(value = "/organisation-type/" + MANUALLY_ENTER_ORGANISATION_DETAILS, params = "remove-exec-officer")
    public String removeExecutiveOfficer(@Valid @ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                         BindingResult bindingResult,
                                         Model model,
                                         HttpServletRequest request, HttpServletResponse response,
                                         @RequestParam("remove-exec-officer") int index,
                                         @RequestHeader(value = REFERER, required = false) final String referer) {

        populateViewModel(organisationForm, model, request);

        organisationForm.getExecutiveOfficers().remove(index);
        model.addAttribute(ORGANISATION_FORM, organisationForm);
        return TEMPLATE_PATH + "/" + MANUALLY_ENTER_ORGANISATION_DETAILS;
    }
}
