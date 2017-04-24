package org.innovateuk.ifs.registration;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.service.AddressRestService;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.form.AddressForm;
import org.innovateuk.ifs.invite.service.InviteOrganisationRestService;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.innovateuk.ifs.registration.form.OrganisationCreationForm;
import org.innovateuk.ifs.registration.form.OrganisationTypeForm;
import org.innovateuk.ifs.registration.populator.OrganisationCreationSelectTypePopulator;
import org.innovateuk.ifs.registration.viewmodel.OrganisationAddressViewModel;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.OrganisationTypeResource;
import org.innovateuk.ifs.user.service.OrganisationSearchRestService;
import org.innovateuk.ifs.user.service.OrganisationTypeRestService;
import org.innovateuk.ifs.util.CookieUtil;
import org.innovateuk.ifs.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.innovateuk.ifs.address.resource.OrganisationAddressType.OPERATING;
import static org.innovateuk.ifs.address.resource.OrganisationAddressType.REGISTERED;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.registration.AbstractAcceptInviteController.INVITE_HASH;
import static org.innovateuk.ifs.registration.AbstractAcceptInviteController.ORGANISATION_TYPE;

/**
 * This Controller handles the users request to create an organisation. This is done when the users creates a new account. In most cases the user will first
 * choose his organisation Type in the AcceptInviteController. Pending on that choice, the related form will be rendered with this controller.
 *
 * This controller is using the OrganisationSearchRestService to provide the user a quick way to reuse information about a existing organisation.OrganisationSearchRestService
 */
@Controller
@RequestMapping("/organisation/create")
@PreAuthorize("permitAll")
public class OrganisationCreationController {
    private static final Log LOG = LogFactory.getLog(OrganisationCreationController.class);

    public static final String ORGANISATION_ID = "organisationId";
    static final String ORGANISATION_FORM = "organisationForm";
    private static final String TEMPLATE_PATH = "registration/organisation";
    private static final String CONFIRM_SELECTED_ORGANISATION = "confirm-selected-organisation";
    private static final String ADD_ADDRESS_DETAILS = "add-address-details";
    private static final String FIND_BUSINESS = "find-business";
    private static final String FIND_ORGANISATION = "find-organisation";
    private static final String SELECTED_ORGANISATION = "selected-organisation";
    private static final String LEAD_ORGANISATION_TYPE = "lead-organisation-type";
    private static final String CONFIRM_ORGANISATION = "confirm-organisation";
    private static final String BINDING_RESULT_ORGANISATION_FORM = "org.springframework.validation.BindingResult.organisationForm";
    private static final String BASE_URL = "/organisation/create";
    private static final String USE_SEARCH_RESULT_ADDRESS = "useSearchResultAddress";
    private static final String SELECTED_POSTCODE = "selectedPostcode";
    private static final String SAVE_ORGANISATION_DETAILS = "save-organisation-details";
    private static final String REFERER = "referer";
    private static final String SEARCH_ORGANISATION = "search-organisation";
    private static final String NOT_IN_COMPANY_HOUSE = "not-in-company-house";
    private static final String MANUAL_ADDRESS = "manual-address";
    private static final String SEARCH_ADDRESS = "search-address";
    private static final String SELECT_ADDRESS = "select-address";
    private static final String ORGANISATION_NAME = "organisationName";
    private static final String MODEL = "model";
    private static final String ORGANISATION_TYPE_ID = "organisationTypeId";

    @Autowired
    private InviteRestService inviteRestService;

    @Autowired
    private InviteOrganisationRestService inviteOrganisationRestService;

    @Autowired
    private AddressRestService addressRestService;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private OrganisationTypeRestService organisationTypeRestService;

    @Autowired
    private OrganisationSearchRestService organisationSearchRestService;

    @Autowired
    private OrganisationCreationSelectTypePopulator organisationCreationSelectTypePopulator;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private CookieUtil cookieUtil;

    private Validator validator;

    @Autowired
    @Qualifier("mvcValidator")
    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    @GetMapping(value = {"/" + FIND_ORGANISATION, "/" + FIND_ORGANISATION + "/**"})
    public String createOrganisation(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                     Model model,
                                     HttpServletRequest request,
                                     HttpServletResponse response) {
        cookieUtil.removeCookie(response, ORGANISATION_ID);
        organisationForm.setOrganisationSearching(false);
        organisationForm = getFormDataFromCookie(organisationForm, model, request);

        addAddressOptions(organisationForm);
        addSelectedAddress(organisationForm);

        cookieUtil.saveToCookie(response, ORGANISATION_FORM, JsonUtil.getSerializedObject(organisationForm));
        model.addAttribute(ORGANISATION_FORM, organisationForm);

        model.addAttribute("isLeadApplicant", checkOrganisationIsLead(request));
        model.addAttribute("searchLabel",getMessageByOrganisationType(organisationForm.getOrganisationTypeEnum(), "SearchLabel",  request.getLocale()));
        model.addAttribute("searchHint", getMessageByOrganisationType(organisationForm.getOrganisationTypeEnum(), "SearchHint",  request.getLocale()));

        return TEMPLATE_PATH + "/" + FIND_ORGANISATION;
    }

    private String getMessageByOrganisationType(OrganisationTypeEnum orgTypeEnum, String textKey, Locale locale) {
        String searchLabel;
        try{
            searchLabel = messageSource.getMessage(String.format("registration.%s.%s", orgTypeEnum.toString(), textKey), null, locale);
        }catch(NoSuchMessageException e){
            LOG.debug(e);
            searchLabel = messageSource.getMessage(String.format("registration.DEFAULT.%s", textKey), null, locale);
        }
        return searchLabel;
    }

    private OrganisationCreationForm getFormDataFromCookie(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm, Model model, HttpServletRequest request) {
        BindingResult bindingResult;// Merge information from cookie into ModelAttribute.
        String organisationFormJson = cookieUtil.getCookieValue(request, ORGANISATION_FORM);

        if (StringUtils.isNotBlank(organisationFormJson)) {
            organisationForm = JsonUtil.getObjectFromJson(organisationFormJson, OrganisationCreationForm.class);
            bindingResult = new BeanPropertyBindingResult(organisationForm, ORGANISATION_FORM);

            if(organisationForm.getAddressForm().isTriedToSearch() && isBlank(organisationForm.getAddressForm().getPostcodeInput())) {
                ValidationMessages.rejectValue(bindingResult, "addressForm.postcodeInput", "EMPTY_POSTCODE_SEARCH");
            }

            validator.validate(organisationForm, bindingResult);
            model.addAttribute(BINDING_RESULT_ORGANISATION_FORM, bindingResult);

            BindingResult addressBindingResult = new BeanPropertyBindingResult(organisationForm.getAddressForm().getSelectedPostcode(), SELECTED_POSTCODE);
            organisationFormValidate(organisationForm, bindingResult, addressBindingResult);

            searchOrganisation(organisationForm);
        }

        addOrganisationType(organisationForm, request);

        return organisationForm;
    }

    /**
     * Get the list of postcode options, with the entered postcode. Add those results to the form.
     */
    private void addAddressOptions(OrganisationCreationForm organisationForm) {
        if (StringUtils.isNotBlank(organisationForm.getAddressForm().getPostcodeInput())) {
            AddressForm addressForm = organisationForm.getAddressForm();
            addressForm.setPostcodeOptions(searchPostcode(organisationForm.getAddressForm().getPostcodeInput()));
            addressForm.setPostcodeInput(organisationForm.getAddressForm().getPostcodeInput());
            organisationForm.setAddressForm(addressForm);
        }
    }

    /**
     * if user has selected a address from the dropdown, get it from the list, and set it as selected.
     */
    private void addSelectedAddress(OrganisationCreationForm organisationForm) {
        AddressForm addressForm = organisationForm.getAddressForm();
        if (StringUtils.isNotBlank(addressForm.getSelectedPostcodeIndex()) && addressForm.getSelectedPostcode() == null) {
            addressForm.setSelectedPostcode(addressForm.getPostcodeOptions().get(Integer.parseInt(addressForm.getSelectedPostcodeIndex())));
            organisationForm.setAddressForm(addressForm);
        }
    }

    /**
     * User has chosen the organisation type in the previous screen, get that from he cookie and add it to the form.
     */
    private OrganisationTypeResource addOrganisationType(OrganisationCreationForm organisationForm, HttpServletRequest request) {
        String organisationTypeJson = cookieUtil.getCookieValue(request, ORGANISATION_TYPE);
        OrganisationTypeResource organisationType = null;
        if(StringUtils.isNotBlank(organisationTypeJson)){
            OrganisationTypeForm organisationTypeForm = JsonUtil.getObjectFromJson(organisationTypeJson, OrganisationTypeForm.class);
            if(organisationTypeForm.getOrganisationType()!=null){
                organisationType = organisationTypeRestService.findOne(organisationTypeForm.getOrganisationType()).getSuccessObject();
                organisationForm.setOrganisationType(organisationType);
            }
        }
        return organisationType;
    }

    private boolean checkOrganisationIsLead(HttpServletRequest request) {
        String organisationTypeJson = cookieUtil.getCookieValue(request, ORGANISATION_TYPE);

        if(StringUtils.isNotBlank(organisationTypeJson)){
            OrganisationTypeForm organisationTypeForm = JsonUtil.getObjectFromJson(organisationTypeJson, OrganisationTypeForm.class);
            return organisationTypeForm.isLeadApplicant();
        }
        return false;
    }

    private void organisationFormValidate(@Valid @ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm, BindingResult bindingResult, BindingResult addressBindingResult) {
        if (organisationForm.isTriedToSave() && !organisationForm.isUseSearchResultAddress()) {
            if (organisationForm.getAddressForm().getSelectedPostcode() != null) {
                validator.validate(organisationForm.getAddressForm().getSelectedPostcode(), addressBindingResult);
            } else if (!organisationForm.getAddressForm().isManualAddress()) {
                bindingResult.rejectValue(USE_SEARCH_RESULT_ADDRESS, "NotEmpty", "You should either fill in your address, or use the registered address as your operating address.");
            }
        }
    }

    private void searchOrganisation(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm) {
        if (organisationForm.isOrganisationSearching()) {
            if (StringUtils.isNotBlank(organisationForm.getOrganisationSearchName())) {
                String trimmedSearchString = StringUtils.normalizeSpace(organisationForm.getOrganisationSearchName());
                List<OrganisationSearchResult> searchResults;
                searchResults = organisationSearchRestService.searchOrganisation(organisationForm.getOrganisationType().getId(), trimmedSearchString)
                        .handleSuccessOrFailure(
                                f -> new ArrayList<>(),
                                s -> s
                        );
                organisationForm.setOrganisationSearchResults(searchResults);
            } else {
                organisationForm.setOrganisationSearchResults(new ArrayList<>());
            }
        }
    }

    @PostMapping(value = "/" + FIND_ORGANISATION + "/**", params = SEARCH_ORGANISATION)
    public String searchOrganisation(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                     HttpServletRequest request, HttpServletResponse response) {
        addOrganisationType(organisationForm, request);
        organisationForm.setOrganisationSearching(true);
        organisationForm.setManualEntry(false);
        cookieUtil.saveToCookie(response, ORGANISATION_FORM, JsonUtil.getSerializedObject(organisationForm));
        return "redirect:/organisation/create/" + FIND_ORGANISATION + "?searchTerm=" + escapePathVariable(organisationForm.getOrganisationSearchName());

    }

    @PostMapping(value = "/" + FIND_ORGANISATION + "/**", params = NOT_IN_COMPANY_HOUSE)
    public String manualOrganisationEntry(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                          HttpServletRequest request, HttpServletResponse response) {
        addOrganisationType(organisationForm, request);
        organisationForm.setOrganisationSearching(false);
        boolean currentManualEntryValue = organisationForm.isManualEntry();
        organisationForm.setManualEntry(!currentManualEntryValue);
        cookieUtil.saveToCookie(response, ORGANISATION_FORM, JsonUtil.getSerializedObject(organisationForm));
        return "redirect:/organisation/create/" + FIND_ORGANISATION;
    }

    @PostMapping(value = "/" + FIND_ORGANISATION + "/**", params = MANUAL_ADDRESS)
    public String manualAddressWithCompanyHouse(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                                HttpServletRequest request, HttpServletResponse response) {
        organisationForm.setAddressForm(new AddressForm());
        organisationForm.getAddressForm().setManualAddress(true);
        organisationForm.setOrganisationSearching(false);
        organisationForm.setManualEntry(true);
        cookieUtil.saveToCookie(response, ORGANISATION_FORM, JsonUtil.getSerializedObject(organisationForm));
        return "redirect:/organisation/create/" + FIND_ORGANISATION;
    }

    @GetMapping("/" + SELECTED_ORGANISATION + "/{searchOrganisationId}")
    public String amendOrganisationAddress(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                           Model model,
                                           @PathVariable("searchOrganisationId") final String searchOrganisationId,
                                           HttpServletRequest request,
                                           HttpServletResponse response) {
        organisationForm = getFormDataFromCookie(organisationForm, model, request);
        organisationForm.setSearchOrganisationId(searchOrganisationId);

        addSelectedOrganisation(organisationForm, model);

        cookieUtil.saveToCookie(response, ORGANISATION_FORM, JsonUtil.getSerializedObject(organisationForm));
        model.addAttribute(ORGANISATION_FORM, organisationForm);
        model.addAttribute(MODEL, new OrganisationAddressViewModel(organisationForm.getOrganisationType(), checkOrganisationIsLead(request)));

        if (OrganisationTypeEnum.RESEARCH.getId().equals(organisationForm.getOrganisationType().getId())) {
            return TEMPLATE_PATH + "/" + ADD_ADDRESS_DETAILS;
        } else {
            return TEMPLATE_PATH + "/" + CONFIRM_SELECTED_ORGANISATION;
        }
    }

    /**
     * +
     * after user has selected a organisation, get the details and add it to the form and the model.
     */
    private OrganisationSearchResult addSelectedOrganisation(OrganisationCreationForm organisationForm, Model model) {
        if (!organisationForm.isManualEntry() && StringUtils.isNotBlank(organisationForm.getSearchOrganisationId())) {
            OrganisationSearchResult s = organisationSearchRestService.getOrganisation(organisationForm.getOrganisationType().getId(), organisationForm.getSearchOrganisationId()).getSuccessObject();
            organisationForm.setOrganisationName(s.getName());
            model.addAttribute("selectedOrganisation", s);
            return s;
        }
        return null;
    }

    @GetMapping("/" + SELECTED_ORGANISATION + "/{searchOrganisationId}/{selectedPostcodeIndex}")
    public String amendOrganisationAddressPostCode(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                                   Model model,
                                                   @PathVariable("searchOrganisationId") final String searchOrganisationId,
                                                   HttpServletRequest request,
                                                   HttpServletResponse response) {
        organisationForm = getFormDataFromCookie(organisationForm, model, request);
        organisationForm.setSearchOrganisationId(searchOrganisationId);

        addSelectedOrganisation(organisationForm, model);
        addOrganisationType(organisationForm, request);
        addAddressOptions(organisationForm);
        addSelectedAddress(organisationForm);

        cookieUtil.saveToCookie(response, ORGANISATION_FORM, JsonUtil.getSerializedObject(organisationForm));
        model.addAttribute(ORGANISATION_FORM, organisationForm);
        model.addAttribute(MODEL, new OrganisationAddressViewModel(organisationForm.getOrganisationType(), checkOrganisationIsLead(request)));

        if (OrganisationTypeEnum.RESEARCH.getId().equals(organisationForm.getOrganisationType().getId())) {
            return TEMPLATE_PATH + "/" + ADD_ADDRESS_DETAILS;
        } else {
            return TEMPLATE_PATH + "/" + CONFIRM_SELECTED_ORGANISATION;
        }
    }

    @GetMapping("/selected-organisation/{searchOrganisationId}/search-postcode")
    public String amendOrganisationAddressPostcode(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                                   Model model,
                                                   @PathVariable("searchOrganisationId") final String searchOrganisationId,
                                                   @RequestParam(value="searchTerm", required=false) String searchTerm,
                                                   HttpServletRequest request,
                                                   HttpServletResponse response) {
        organisationForm = getFormDataFromCookie(organisationForm, model, request);
        organisationForm.setSearchOrganisationId(searchOrganisationId);

        addSelectedOrganisation(organisationForm, model);
        addOrganisationType(organisationForm, request);
        addAddressOptions(organisationForm);

        cookieUtil.saveToCookie(response, ORGANISATION_FORM, JsonUtil.getSerializedObject(organisationForm));
        model.addAttribute(ORGANISATION_FORM, organisationForm);
        model.addAttribute(MODEL, new OrganisationAddressViewModel(organisationForm.getOrganisationType(), checkOrganisationIsLead(request)));

        if (OrganisationTypeEnum.RESEARCH.getId().equals(organisationForm.getOrganisationType().getId())) {
            return TEMPLATE_PATH + "/" + ADD_ADDRESS_DETAILS;
        } else {
            return TEMPLATE_PATH + "/" + CONFIRM_SELECTED_ORGANISATION;
        }
    }

    @PostMapping(value = {"/" + SELECTED_ORGANISATION + "/**", "/" + FIND_ORGANISATION + "**"}, params = SEARCH_ADDRESS)
    public String searchAddress(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                Model model,
                                HttpServletRequest request,
                                HttpServletResponse response,
                                @RequestHeader(value = REFERER, required = false) final String referer) {
        addOrganisationType(organisationForm, request);
        addSelectedOrganisation(organisationForm, model);
        organisationForm.getAddressForm().setSelectedPostcodeIndex(null);
        organisationForm.getAddressForm().setTriedToSearch(true);
        cookieUtil.saveToCookie(response, ORGANISATION_FORM, JsonUtil.getSerializedObject(organisationForm));
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

        if (!referer.contains(FIND_ORGANISATION)) {
            if (StringUtils.isNotBlank(organisationForm.getSearchOrganisationId()) && organisationForm.getAddressForm().getSelectedPostcodeIndex() != null && StringUtils.isNotBlank(organisationForm.getAddressForm().getPostcodeInput())) {
                return String.format("redirect:%s/%s/%s/%s", BASE_URL, redirectPart, organisationForm.getSearchOrganisationId(), organisationForm.getAddressForm().getSelectedPostcodeIndex());
            } else if (StringUtils.isNotBlank(organisationForm.getSearchOrganisationId()) && StringUtils.isNotBlank(organisationForm.getAddressForm().getPostcodeInput())) {
                return String.format("redirect:%s/%s/%s/search-postcode?searchTerm=%s", BASE_URL, redirectPart, organisationForm.getSearchOrganisationId(), escapePathVariable(organisationForm.getAddressForm().getPostcodeInput()));
            } else if (StringUtils.isNotBlank(organisationForm.getSearchOrganisationId())) {
                return String.format("redirect:%s/%s/%s", BASE_URL, redirectPart, organisationForm.getSearchOrganisationId());
            } else {
                return String.format("redirect:%s/%s", BASE_URL, redirectPart);
            }
        } else {
            if (organisationForm.getAddressForm().getSelectedPostcodeIndex() != null && StringUtils.isNotBlank(organisationForm.getAddressForm().getPostcodeInput())) {
                return String.format("redirect:%s/%s/%s/%s", BASE_URL, redirectPart, escapePathVariable(organisationForm.getAddressForm().getPostcodeInput()), organisationForm.getAddressForm().getSelectedPostcodeIndex());
            } else if (StringUtils.isNotBlank(organisationForm.getAddressForm().getPostcodeInput())) {
                return String.format("redirect:%s/%s?searchTerm=%s", BASE_URL, redirectPart, escapePathVariable(organisationForm.getAddressForm().getPostcodeInput()));
            } else {
                return String.format("redirect:%s/%s", BASE_URL, redirectPart);
            }
        }
    }

    @PostMapping(value = {"/" + SELECTED_ORGANISATION + "/**", "/" + FIND_ORGANISATION + "**"}, params = SELECT_ADDRESS)
    public String selectAddress(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                HttpServletRequest request, HttpServletResponse response,
                                @RequestHeader(value = REFERER, required = false) final String referer) {
        addOrganisationType(organisationForm, request);
        organisationForm.getAddressForm().setSelectedPostcode(null);
        cookieUtil.saveToCookie(response, ORGANISATION_FORM, JsonUtil.getSerializedObject(organisationForm));
        return getRedirectUrlInvalidSave(organisationForm, referer);
    }

    @PostMapping(value = "/" + SELECTED_ORGANISATION + "/**", params = MANUAL_ADDRESS)
    public String manualAddress(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                HttpServletRequest request, HttpServletResponse response) {
        organisationForm.setAddressForm(new AddressForm());
        organisationForm.getAddressForm().setManualAddress(true);
        cookieUtil.saveToCookie(response, ORGANISATION_FORM, JsonUtil.getSerializedObject(organisationForm));
        return String.format("redirect:%s/%s/%s", BASE_URL, SELECTED_ORGANISATION, organisationForm.getSearchOrganisationId());
    }

    @PostMapping(value = {"/" + SELECTED_ORGANISATION + "/**", "/" + FIND_ORGANISATION + "**"}, params = SAVE_ORGANISATION_DETAILS)
    public String saveOrganisation(@Valid @ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                   BindingResult bindingResult,
                                   Model model,
                                   HttpServletRequest request, HttpServletResponse response,
                                   @RequestHeader(value = REFERER, required = false) final String referer
    ) {
        organisationForm.setTriedToSave(true);
        addOrganisationType(organisationForm, request);
        addSelectedOrganisation(organisationForm, model);

        bindingResult = new BeanPropertyBindingResult(organisationForm, ORGANISATION_FORM);
        validator.validate(organisationForm, bindingResult);
        BindingResult addressBindingResult = new BeanPropertyBindingResult(organisationForm.getAddressForm().getSelectedPostcode(), SELECTED_POSTCODE);
        organisationFormValidate(organisationForm, bindingResult, addressBindingResult);

        boolean isLead = checkOrganisationIsLead(request);

        if (!bindingResult.hasFieldErrors(ORGANISATION_NAME) && !bindingResult.hasFieldErrors(USE_SEARCH_RESULT_ADDRESS) && !addressBindingResult.hasErrors()) {
            cookieUtil.saveToCookie(response, ORGANISATION_FORM, JsonUtil.getSerializedObject(organisationForm));
            if (isLead) {
                return "redirect:" + BASE_URL + "/" + LEAD_ORGANISATION_TYPE;
            } else {
                return "redirect:" + BASE_URL + "/" + CONFIRM_ORGANISATION;
            }

        } else {
            organisationForm.setTriedToSave(true);
            organisationForm.getAddressForm().setTriedToSave(true);
            cookieUtil.saveToCookie(response, ORGANISATION_FORM, JsonUtil.getSerializedObject(organisationForm));
            return getRedirectUrlInvalidSave(organisationForm, referer);
        }
    }

    @GetMapping("/" + LEAD_ORGANISATION_TYPE)
    public String selectOrganisationType(Model model,
                                         HttpServletRequest request) {

        model.addAttribute(MODEL, organisationCreationSelectTypePopulator.populate());

        String organisationFormJson = cookieUtil.getCookieValue(request, ORGANISATION_FORM);
        OrganisationCreationForm organisationCreationForm = JsonUtil.getObjectFromJson(organisationFormJson, OrganisationCreationForm.class);

        model.addAttribute(ORGANISATION_FORM, organisationCreationForm);
        return TEMPLATE_PATH + "/" + LEAD_ORGANISATION_TYPE;
    }

    @PostMapping("/" + LEAD_ORGANISATION_TYPE)
    public String confirmSelectOrganisationType(@Valid @ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                                BindingResult bindingResult,
                                                Model model,
                                                HttpServletRequest request, HttpServletResponse response) {
        if (!bindingResult.hasFieldErrors(ORGANISATION_TYPE_ID)) {
            OrganisationTypeForm organisationTypeForm = new OrganisationTypeForm();
            organisationTypeForm.setOrganisationType(OrganisationTypeEnum.getFromId(organisationForm.getOrganisationTypeId()).getId());
            organisationTypeForm.setLeadApplicant(true);
            String orgTypeForm = JsonUtil.getSerializedObject(organisationTypeForm);

            cookieUtil.saveToCookie(response, ORGANISATION_TYPE, orgTypeForm);

            return "redirect:" + BASE_URL + "/" + CONFIRM_ORGANISATION;
        } else {
            organisationForm.setTriedToSave(true);
            model.addAttribute(MODEL, organisationCreationSelectTypePopulator.populate());

            return TEMPLATE_PATH + "/" + LEAD_ORGANISATION_TYPE;
        }
    }


    /**
     * Confirm the company details (user input, not from company-house)
     */
    @GetMapping("/" + CONFIRM_ORGANISATION)
    public String confirmCompany(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                 Model model,
                                 HttpServletRequest request) throws IOException {
        organisationForm = getFormDataFromCookie(organisationForm, model, request);
        addOrganisationType(organisationForm, request);
        addSelectedOrganisation(organisationForm, model);
        model.addAttribute(ORGANISATION_FORM, organisationForm);
        return TEMPLATE_PATH + "/" + CONFIRM_ORGANISATION;
    }

    @GetMapping("/" + FIND_BUSINESS)
    public String createOrganisationAsLeadApplicant(HttpServletRequest request, HttpServletResponse response) {
        //This is the first endpoint when creating a new account as lead applicant.
        OrganisationTypeForm organisationTypeForm = new OrganisationTypeForm();
        organisationTypeForm.setOrganisationType(OrganisationTypeEnum.BUSINESS.getId());
        organisationTypeForm.setLeadApplicant(true);
        String orgTypeForm = JsonUtil.getSerializedObject(organisationTypeForm);
        cookieUtil.saveToCookie(response, ORGANISATION_TYPE, orgTypeForm);
        return "redirect:" + BASE_URL + "/" + FIND_ORGANISATION;
    }

    @GetMapping("/save-organisation")
    public String saveOrganisation(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm, Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
        organisationForm = getFormDataFromCookie(organisationForm, model, request);
        OrganisationSearchResult selectedOrganisation = addSelectedOrganisation(organisationForm, model);
        AddressResource address = organisationForm.getAddressForm().getSelectedPostcode();

        OrganisationResource organisationResource = new OrganisationResource();
        organisationResource.setName(organisationForm.getOrganisationName());
        organisationResource.setOrganisationType(organisationForm.getOrganisationType().getId());

        if (!OrganisationTypeEnum.RESEARCH.getId().equals(organisationForm.getOrganisationType().getId())) {
            organisationResource.setCompanyHouseNumber(organisationForm.getSearchOrganisationId());
        }

        organisationResource = saveNewOrganisation(organisationResource, request);
        if (address != null && !organisationForm.isUseSearchResultAddress()) {
            organisationService.addAddress(organisationResource, address, OPERATING);
        }
        if (selectedOrganisation != null && selectedOrganisation.getOrganisationAddress() != null) {
            organisationService.addAddress(organisationResource, selectedOrganisation.getOrganisationAddress(), REGISTERED);
        }
        cookieUtil.saveToCookie(response, ORGANISATION_ID, String.valueOf(organisationResource.getId()));
        return "redirect:" + RegistrationController.BASE_URL;
    }

    private OrganisationResource saveNewOrganisation(OrganisationResource organisationResource, HttpServletRequest request) {
        organisationResource = organisationService.saveForAnonymousUserFlow(organisationResource);
        linkOrganisationToInvite(organisationResource, request);
        return organisationResource;
    }

    /**
     * If current user is a invitee, then link the organisation that is created, to the InviteOrganisation.
     */
    private void linkOrganisationToInvite(OrganisationResource organisationResource, HttpServletRequest request) {
        String cookieHash = cookieUtil.getCookieValue(request, INVITE_HASH);
        if (StringUtils.isNotBlank(cookieHash)) {
            final OrganisationResource finalOrganisationResource = organisationResource;

            inviteRestService.getInviteByHash(cookieHash).andOnSuccess(
                    s ->
                            inviteOrganisationRestService.getByIdForAnonymousUserFlow(s.getInviteOrganisation()).handleSuccessOrFailure(
                                    f -> restFailure(HttpStatus.NOT_FOUND),
                                    i -> {
                                        if (i.getOrganisation() == null) {
                                            i.setOrganisation(finalOrganisationResource.getId());
                                            // Save the created organisation Id, so the next invitee does not have to..
                                            return inviteOrganisationRestService.put(i);
                                        }
                                        return restFailure(HttpStatus.ALREADY_REPORTED);
                                    }
                            )
            );
        }
    }


    private List<AddressResource> searchPostcode(String postcodeInput) {
        RestResult<List<AddressResource>>  addressLookupRestResult =
                addressRestService.doLookup(postcodeInput);
        List<AddressResource> addressResourceList = addressLookupRestResult.handleSuccessOrFailure(
                failure -> new ArrayList<>(),
                addresses -> addresses);
        return addressResourceList;
    }

    private String escapePathVariable(final String input){
        try {
            return UriUtils.encodeQueryParam(input,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOG.error("Unable to encode search string " + input, e);
        }
        return input;
    }

}
