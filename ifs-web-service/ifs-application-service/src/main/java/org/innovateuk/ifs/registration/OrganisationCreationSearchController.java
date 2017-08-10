package org.innovateuk.ifs.registration;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.form.AddressForm;
import org.innovateuk.ifs.registration.form.OrganisationCreationForm;
import org.innovateuk.ifs.registration.form.OrganisationTypeForm;
import org.innovateuk.ifs.registration.viewmodel.OrganisationAddressViewModel;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.innovateuk.ifs.util.ExceptionFunctions.getOrRethrow;
import static org.springframework.web.util.UriUtils.encodeQueryParam;

/**
 * TODO: Add description
 */
@Controller
@RequestMapping(AbstractOrganisationCreationController.BASE_URL)
@PreAuthorize("permitAll")
public class OrganisationCreationSearchController extends AbstractOrganisationCreationController {

    private static final String CONFIRM_SELECTED_ORGANISATION = "confirm-selected-organisation";
    private static final String ADD_ADDRESS_DETAILS = "add-address-details";
    private static final String MANUAL_ADDRESS = "manual-address";

    private static final String SELECTED_ORGANISATION = "selected-organisation";

    private static final String SAVE_ORGANISATION_DETAILS = "save-organisation-details";
    private static final String REFERER = "referer";
    private static final String SEARCH_ADDRESS = "search-address";
    private static final String SELECT_ADDRESS = "select-address";
    private static final String ORGANISATION_NAME = "organisationName";
    private static final String MODEL = "model";

    @Autowired
    private MessageSource messageSource;

    private static final String SEARCH_ORGANISATION = "search-organisation";
    private static final String NOT_IN_COMPANY_HOUSE = "not-in-company-house";

    @GetMapping(value = {"/" + FIND_ORGANISATION,"/" + FIND_ORGANISATION + "/**"})
    public String createOrganisation(@ModelAttribute(name = ORGANISATION_FORM, binding = false) OrganisationCreationForm organisationForm,
                                     Model model,
                                     HttpServletRequest request,
                                     HttpServletResponse response) {
        registrationCookieService.deleteOrganisationIdCookie(response);

        organisationForm.setOrganisationSearching(false);
        organisationForm = getFormDataFromCookie(organisationForm, model, request);

        AddressForm addressForm = organisationForm.getAddressForm();
        addAddressOptions(addressForm);
        addSelectedAddress(addressForm);


        registrationCookieService.saveToOrganisationCreationCookie(organisationForm, response);
        model.addAttribute(ORGANISATION_FORM, organisationForm);

        model.addAttribute("isLeadApplicant", checkOrganisationIsLead(request));
        model.addAttribute("searchLabel",getMessageByOrganisationType(organisationForm.getOrganisationTypeEnum(), "SearchLabel",  request.getLocale()));
        model.addAttribute("searchHint", getMessageByOrganisationType(organisationForm.getOrganisationTypeEnum(), "SearchHint",  request.getLocale()));
        model.addAttribute("organisationType", organisationTypeRestService.findOne(organisationForm.getOrganisationTypeId()).getSuccessObject());

        return TEMPLATE_PATH + "/" + FIND_ORGANISATION;
    }

    @PostMapping(value = "/" + FIND_ORGANISATION + "/**", params = SEARCH_ORGANISATION)
    public String searchOrganisation(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                     HttpServletRequest request, HttpServletResponse response) {
        addOrganisationType(organisationForm, organisationTypeIdFromCookie(request));
        organisationForm.setOrganisationSearching(true);
        organisationForm.setManualEntry(false);
        registrationCookieService.saveToOrganisationCreationCookie(organisationForm, response);
        return "redirect:/organisation/create/" + FIND_ORGANISATION + "?searchTerm=" + escapePathVariable(organisationForm.getOrganisationSearchName());

    }

    @PostMapping(value = "/" + FIND_ORGANISATION + "/**", params = NOT_IN_COMPANY_HOUSE)
    public String manualOrganisationEntry(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                          HttpServletRequest request, HttpServletResponse response) {
        addOrganisationType(organisationForm, organisationTypeIdFromCookie(request));
        organisationForm.setOrganisationSearching(false);
        boolean currentManualEntryValue = organisationForm.isManualEntry();
        organisationForm.setManualEntry(!currentManualEntryValue);
        registrationCookieService.saveToOrganisationCreationCookie(organisationForm, response);
        return "redirect:/organisation/create/" + FIND_ORGANISATION;
    }

    @PostMapping(value = "/" + FIND_ORGANISATION + "/**", params = MANUAL_ADDRESS)
    public String manualAddressWithCompanyHouse(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                                HttpServletRequest request, HttpServletResponse response) {
        OrganisationCreationForm organisationFromCookie = registrationCookieService.getOrganisationCreationCookieValue(request).get();
        organisationFromCookie.setAddressForm(new AddressForm());
        organisationFromCookie.getAddressForm().setManualAddress(true);

        registrationCookieService.saveToOrganisationCreationCookie(organisationFromCookie, response);
        return "redirect:/organisation/create/" + FIND_ORGANISATION;
    }

    @GetMapping("/" + SELECTED_ORGANISATION + "/{searchOrganisationId}")
    public String amendOrganisationAddress(@ModelAttribute(name = ORGANISATION_FORM, binding = false) OrganisationCreationForm organisationForm,
                                           Model model,
                                           @PathVariable("searchOrganisationId") final String searchOrganisationId,
                                           HttpServletRequest request,
                                           HttpServletResponse response) {
        organisationForm = getFormDataFromCookie(organisationForm, model, request);
        organisationForm.setSearchOrganisationId(searchOrganisationId);

        addSelectedOrganisation(organisationForm, model);

        registrationCookieService.saveToOrganisationCreationCookie(organisationForm, response);

        model.addAttribute(ORGANISATION_FORM, organisationForm);
        model.addAttribute("organisationType", organisationTypeRestService.findOne(organisationForm.getOrganisationTypeId()).getSuccessObject());
        model.addAttribute(MODEL, new OrganisationAddressViewModel(organisationTypeRestService.findOne(organisationForm.getOrganisationTypeId()).getSuccessObject(), checkOrganisationIsLead(request)));

        if (OrganisationTypeEnum.RESEARCH.getId().equals(organisationForm.getOrganisationTypeId())) {
            return TEMPLATE_PATH + "/" + ADD_ADDRESS_DETAILS;
        } else {
            return TEMPLATE_PATH + "/" + CONFIRM_SELECTED_ORGANISATION;
        }
    }

    @GetMapping("/" + SELECTED_ORGANISATION + "/{searchOrganisationId}/{selectedPostcodeIndex}")
    public String amendOrganisationAddressPostCode(@ModelAttribute(name = ORGANISATION_FORM, binding = false) OrganisationCreationForm organisationForm,
                                                   Model model,
                                                   @PathVariable("searchOrganisationId") final String searchOrganisationId,
                                                   HttpServletRequest request,
                                                   HttpServletResponse response) {
        organisationForm = getFormDataFromCookie(organisationForm, model, request);
        organisationForm.setSearchOrganisationId(searchOrganisationId);

        addSelectedOrganisation(organisationForm, model);
        addOrganisationType(organisationForm, organisationTypeIdFromCookie(request));

        AddressForm addressForm = organisationForm.getAddressForm();
        addAddressOptions(addressForm);
        addSelectedAddress(addressForm);

        registrationCookieService.saveToOrganisationCreationCookie(organisationForm, response);

        model.addAttribute(ORGANISATION_FORM, organisationForm);
        model.addAttribute(MODEL, new OrganisationAddressViewModel(organisationTypeRestService.findOne(organisationForm.getOrganisationTypeId()).getSuccessObject(), checkOrganisationIsLead(request)));
        model.addAttribute("organisationType", organisationTypeRestService.findOne(organisationForm.getOrganisationTypeId()).getSuccessObject());

        if (OrganisationTypeEnum.RESEARCH.getId().equals(organisationForm.getOrganisationTypeId())) {
            return TEMPLATE_PATH + "/" + ADD_ADDRESS_DETAILS;
        } else {
            return TEMPLATE_PATH + "/" + CONFIRM_SELECTED_ORGANISATION;
        }
    }

    @GetMapping("/" + SELECTED_ORGANISATION + "{searchOrganisationId}/search-postcode")
    public String amendOrganisationAddressPostcode(@ModelAttribute(name = ORGANISATION_FORM, binding = false) OrganisationCreationForm organisationForm,
                                                   Model model,
                                                   @PathVariable("searchOrganisationId") final String searchOrganisationId,
                                                   @RequestParam(value = "searchTerm", required = false) String searchTerm,
                                                   HttpServletRequest request,
                                                   HttpServletResponse response) {
        organisationForm = getFormDataFromCookie(organisationForm, model, request);
        organisationForm.setSearchOrganisationId(searchOrganisationId);

        addSelectedOrganisation(organisationForm, model);
        addOrganisationType(organisationForm, organisationTypeIdFromCookie(request));
        addAddressOptions(organisationForm.getAddressForm());

        registrationCookieService.saveToOrganisationCreationCookie(organisationForm, response);

        model.addAttribute(ORGANISATION_FORM, organisationForm);
        model.addAttribute(MODEL, new OrganisationAddressViewModel(organisationTypeRestService.findOne(organisationForm.getOrganisationTypeId()).getSuccessObject(), checkOrganisationIsLead(request)));
        model.addAttribute("organisationType", organisationTypeRestService.findOne(organisationForm.getOrganisationTypeId()).getSuccessObject());


        if (OrganisationTypeEnum.RESEARCH.getId().equals(organisationForm.getOrganisationTypeId())) {
            return TEMPLATE_PATH + "/" + ADD_ADDRESS_DETAILS;
        } else {
            return TEMPLATE_PATH + "/" + CONFIRM_SELECTED_ORGANISATION;
        }
    }

    @PostMapping(value = {"/" + SELECTED_ORGANISATION + "/**", "/" + FIND_ORGANISATION + "/**"}, params = SEARCH_ADDRESS)
    public String searchAddress(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                Model model,
                                HttpServletRequest request,
                                HttpServletResponse response,
                                @RequestHeader(value = REFERER, required = false) final String referer) {
        addOrganisationType(organisationForm, organisationTypeIdFromCookie(request));
        addSelectedOrganisation(organisationForm, model);
        organisationForm.getAddressForm().setSelectedPostcodeIndex(null);
        organisationForm.getAddressForm().setTriedToSearch(true);
        registrationCookieService.saveToOrganisationCreationCookie(organisationForm, response);

        return getRedirectUrlInvalidSave(organisationForm, referer);
    }

    @PostMapping(value = {"/" + SELECTED_ORGANISATION + "/**", "/" + FIND_ORGANISATION + "/**"}, params = SELECT_ADDRESS)
    public String selectAddress(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                HttpServletRequest request, HttpServletResponse response,
                                @RequestHeader(value = REFERER, required = false) final String referer) {
        addOrganisationType(organisationForm, organisationTypeIdFromCookie(request));
        organisationForm.getAddressForm().setSelectedPostcode(null);
        registrationCookieService.saveToOrganisationCreationCookie(organisationForm, response);

        return getRedirectUrlInvalidSave(organisationForm, referer);
    }

    @PostMapping(value = "/" + SELECTED_ORGANISATION + "/**", params = MANUAL_ADDRESS)
    public String manualAddress(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                HttpServletResponse response, HttpServletRequest request) {
        OrganisationCreationForm organisationFromCookie = registrationCookieService.getOrganisationCreationCookieValue(request).get();
        organisationFromCookie.setAddressForm(new AddressForm());
        organisationFromCookie.getAddressForm().setManualAddress(true);

        registrationCookieService.saveToOrganisationCreationCookie(organisationFromCookie, response);

        return String.format("redirect:%s/%s/%s", BASE_URL, SELECTED_ORGANISATION, organisationForm.getSearchOrganisationId());
    }

    @PostMapping(value = {"/" + SELECTED_ORGANISATION + "/**", "/" + FIND_ORGANISATION + "/**"}, params = SAVE_ORGANISATION_DETAILS)
    public String saveOrganisation(@Valid @ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                   BindingResult bindingResult,
                                   Model model,
                                   HttpServletRequest request, HttpServletResponse response,
                                   @RequestHeader(value = REFERER, required = false) final String referer) {
        OrganisationCreationForm organisationCreationForm = registrationCookieService.getOrganisationCreationCookieValue(request).get();
        organisationCreationForm.setOrganisationName(organisationForm.getOrganisationName());
        organisationCreationForm.setSearchOrganisationId(organisationForm.getSearchOrganisationId());
        organisationCreationForm.setUseSearchResultAddress(organisationForm.isUseSearchResultAddress());
        organisationCreationForm.setAddressForm(organisationForm.getAddressForm());
        organisationForm.setTriedToSave(true);
        organisationForm.getAddressForm().setTriedToSave(true);

        addOrganisationType(organisationCreationForm, organisationTypeIdFromCookie(request));
        addSelectedOrganisation(organisationCreationForm, model);

        bindingResult = new BeanPropertyBindingResult(organisationCreationForm, ORGANISATION_FORM);
        validator.validate(organisationCreationForm, bindingResult);
        BindingResult addressBindingResult = new BeanPropertyBindingResult(organisationForm.getAddressForm().getSelectedPostcode(), SELECTED_POSTCODE);
        organisationFormAddressFormValidate(organisationForm, bindingResult, addressBindingResult);

        if (!bindingResult.hasFieldErrors(ORGANISATION_NAME) && !bindingResult.hasFieldErrors(USE_SEARCH_RESULT_ADDRESS) && !addressBindingResult.hasErrors()) {
            registrationCookieService.saveToOrganisationCreationCookie(organisationCreationForm, response);
            return "redirect:" + BASE_URL + "/" + CONFIRM_ORGANISATION;

        } else {
            organisationForm.setTriedToSave(true);
            organisationForm.getAddressForm().setTriedToSave(true);
            registrationCookieService.saveToOrganisationCreationCookie(organisationCreationForm, response);
            return getRedirectUrlInvalidSave(organisationForm, referer);
        }
    }

    private String getRedirectUrlInvalidSave(OrganisationCreationForm organisationForm, String referer) {
        String redirectPart;
        if (referer.contains(FIND_ORGANISATION)) {
            redirectPart = FIND_ORGANISATION;
            organisationForm.setSearchOrganisationId("");
        } else {
            redirectPart = SELECTED_ORGANISATION;
        }

        AddressForm addressForm = organisationForm.getAddressForm();
        if (!referer.contains(FIND_ORGANISATION)) {
            if (isNotBlank(organisationForm.getSearchOrganisationId()) && addressForm.getSelectedPostcodeIndex() != null && isNotBlank(addressForm.getPostcodeInput())) {
                return String.format("redirect:%s/%s/%s/%s", BASE_URL, redirectPart, organisationForm.getSearchOrganisationId(), addressForm.getSelectedPostcodeIndex());
            } else if (isNotBlank(organisationForm.getSearchOrganisationId()) && isNotBlank(addressForm.getPostcodeInput())) {
                return String.format("redirect:%s/%s/%s/search-postcode?searchTerm=%s", BASE_URL, redirectPart, organisationForm.getSearchOrganisationId(), escapePathVariable(addressForm.getPostcodeInput()));
            } else if (isNotBlank(organisationForm.getSearchOrganisationId())) {
                return String.format("redirect:%s/%s/%s", BASE_URL, redirectPart, organisationForm.getSearchOrganisationId());
            } else {
                return String.format("redirect:%s/%s", BASE_URL, redirectPart);
            }
        } else {
            if (addressForm.getSelectedPostcodeIndex() != null && isNotBlank(addressForm.getPostcodeInput())) {
                return String.format("redirect:%s/%s/%s/%s", BASE_URL, redirectPart, escapePathVariable(addressForm.getPostcodeInput()), addressForm.getSelectedPostcodeIndex());
            } else if (isNotBlank(addressForm.getPostcodeInput())) {
                return String.format("redirect:%s/%s?searchTerm=%s", BASE_URL, redirectPart, escapePathVariable(addressForm.getPostcodeInput()));
            } else {
                return String.format("redirect:%s/%s", BASE_URL, redirectPart);
            }
        }
    }

    private String getMessageByOrganisationType(OrganisationTypeEnum orgTypeEnum, String textKey, Locale locale) {
        try {
            return messageSource.getMessage(String.format("registration.%s.%s", orgTypeEnum.toString(), textKey), null, locale);
        } catch (NoSuchMessageException e) {
            return messageSource.getMessage(String.format("registration.DEFAULT.%s", textKey), null, locale);
        }
    }

    /**
     * Get the list of postcode options, with the entered postcode. Add those results to the form.
     */
    private void addAddressOptions(AddressForm addressForm) {
        if (isNotBlank(addressForm.getPostcodeInput())) {
            addressForm.setPostcodeOptions(searchPostcode(addressForm.getPostcodeInput()));
        }
    }

    /**
     * If user has selected a address from the dropdown, get it from the list, and set it as selected.
     */
    private void addSelectedAddress(AddressForm addressForm) {
        if (isNotBlank(addressForm.getSelectedPostcodeIndex()) && addressForm.getSelectedPostcode() == null) {
            addressForm.setSelectedPostcode(addressForm.getPostcodeOptions().get(Integer.parseInt(addressForm.getSelectedPostcodeIndex())));
        }
    }

    private List<AddressResource> searchPostcode(String postcodeInput) {
        return addressRestService.doLookup(postcodeInput).getOrElse(new ArrayList<>());
    }

    private String escapePathVariable(final String input) {
        return getOrRethrow(() -> encodeQueryParam(input, "UTF-8"));
    }

    private boolean checkOrganisationIsLead(HttpServletRequest request) {
        Optional<OrganisationTypeForm> organisationTypeForm = registrationCookieService.getOrganisationTypeCookieValue(request);
        if(organisationTypeForm.isPresent()){
            return organisationTypeForm.get().isLeadApplicant();

        }

        return false;
    }
}
