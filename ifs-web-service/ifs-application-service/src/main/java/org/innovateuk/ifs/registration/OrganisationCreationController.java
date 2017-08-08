package org.innovateuk.ifs.registration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.form.AddressForm;
import org.innovateuk.ifs.registration.form.OrganisationCreationForm;
import org.innovateuk.ifs.registration.viewmodel.OrganisationAddressViewModel;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * This Controller handles the users request to create an organisation. This is done when the users creates a new account. In most cases the user will first
 * choose his organisation Type in the AcceptInviteController. Pending on that choice, the related form will be rendered with this controller.
 *
 * This controller is using the OrganisationSearchRestService to provide the user a quick way to reuse information about a existing organisation.OrganisationSearchRestService
 */
@Controller
@RequestMapping(AbstractOrganisationCreationController.BASE_URL)
@PreAuthorize("permitAll")
public class OrganisationCreationController extends AbstractOrganisationCreationController {
    private static final Log LOG = LogFactory.getLog(OrganisationCreationController.class);

    private static final String CONFIRM_SELECTED_ORGANISATION = "confirm-selected-organisation";
    private static final String ADD_ADDRESS_DETAILS = "add-address-details";

    private static final String SELECTED_ORGANISATION = "selected-organisation";

    private static final String SAVE_ORGANISATION_DETAILS = "save-organisation-details";
    private static final String REFERER = "referer";
    private static final String SEARCH_ADDRESS = "search-address";
    private static final String SELECT_ADDRESS = "select-address";
    private static final String ORGANISATION_NAME = "organisationName";
    private static final String MODEL = "model";

    @Autowired
    private CompetitionService competitionService;

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
        model.addAttribute(MODEL, new OrganisationAddressViewModel(organisationForm.getOrganisationType(), checkOrganisationIsLead(request)));

        if (OrganisationTypeEnum.RESEARCH.getId().equals(organisationForm.getOrganisationType().getId())) {
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
        model.addAttribute(MODEL, new OrganisationAddressViewModel(organisationForm.getOrganisationType(), checkOrganisationIsLead(request)));

        if (OrganisationTypeEnum.RESEARCH.getId().equals(organisationForm.getOrganisationType().getId())) {
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
        model.addAttribute(MODEL, new OrganisationAddressViewModel(organisationForm.getOrganisationType(), checkOrganisationIsLead(request)));

        if (OrganisationTypeEnum.RESEARCH.getId().equals(organisationForm.getOrganisationType().getId())) {
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

    @PostMapping(value = {"/" + SELECTED_ORGANISATION + "/**", "/" + FIND_ORGANISATION + "**"}, params = SELECT_ADDRESS)
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
        OrganisationCreationForm organisationFormFromCookie = registrationCookieService.getOrganisationCreationCookieValue(request).get();
        organisationFormFromCookie.setAddressForm(new AddressForm());
        organisationFormFromCookie.getAddressForm().setManualAddress(true);

        registrationCookieService.saveToOrganisationCreationCookie(organisationForm, response);

        return String.format("redirect:%s/%s/%s", BASE_URL, SELECTED_ORGANISATION, organisationForm.getSearchOrganisationId());
    }

    @PostMapping(value = {"/" + SELECTED_ORGANISATION + "/**", "/" + FIND_ORGANISATION + "**"}, params = SAVE_ORGANISATION_DETAILS)
    public String saveOrganisation(@Valid @ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                   BindingResult bindingResult,
                                   Model model,
                                   HttpServletRequest request, HttpServletResponse response,
                                   @RequestHeader(value = REFERER, required = false) final String referer) {
        organisationForm = registrationCookieService.getOrganisationCreationCookieValue(request).get();

        organisationForm.setTriedToSave(true);
        addOrganisationType(organisationForm, organisationTypeIdFromCookie(request));
        addSelectedOrganisation(organisationForm, model);

        bindingResult = new BeanPropertyBindingResult(organisationForm, ORGANISATION_FORM);
        validator.validate(organisationForm, bindingResult);
        BindingResult addressBindingResult = new BeanPropertyBindingResult(organisationForm.getAddressForm().getSelectedPostcode(), SELECTED_POSTCODE);
        organisationFormAddressFormValidate(organisationForm, bindingResult, addressBindingResult);

        if (!bindingResult.hasFieldErrors(ORGANISATION_NAME) && !bindingResult.hasFieldErrors(USE_SEARCH_RESULT_ADDRESS) && !addressBindingResult.hasErrors()) {
            registrationCookieService.saveToOrganisationCreationCookie(organisationForm, response);
            return "redirect:" + BASE_URL + "/" + CONFIRM_ORGANISATION;

        } else {
            organisationForm.setTriedToSave(true);
            organisationForm.getAddressForm().setTriedToSave(true);
            registrationCookieService.saveToOrganisationCreationCookie(organisationForm, response);
            return getRedirectUrlInvalidSave(organisationForm, referer);
        }
    }
}
