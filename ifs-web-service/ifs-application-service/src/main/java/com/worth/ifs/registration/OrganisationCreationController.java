package com.worth.ifs.registration;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.address.service.AddressRestService;
import com.worth.ifs.form.AddressForm;
import com.worth.ifs.application.form.Form;
import com.worth.ifs.application.service.OrganisationService;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.invite.service.InviteOrganisationRestService;
import com.worth.ifs.invite.service.InviteRestService;
import com.worth.ifs.organisation.resource.OrganisationSearchResult;
import com.worth.ifs.registration.form.OrganisationCreationForm;
import com.worth.ifs.registration.form.OrganisationTypeForm;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.OrganisationTypeEnum;
import com.worth.ifs.user.resource.OrganisationTypeResource;
import com.worth.ifs.user.service.OrganisationSearchRestService;
import com.worth.ifs.user.service.OrganisationTypeRestService;
import com.worth.ifs.util.CookieUtil;
import com.worth.ifs.util.JsonUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
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

import static com.worth.ifs.address.resource.OrganisationAddressType.OPERATING;
import static com.worth.ifs.address.resource.OrganisationAddressType.REGISTERED;
import static com.worth.ifs.commons.rest.RestResult.restFailure;
import static org.apache.commons.lang3.StringUtils.isBlank;


/**
 * This Controller handles the users request to create an organisation. This is done when the users creates a new account. In most cases the user will first
 * choose his organisation Type in the AcceptInviteController. Pending on that choice, the related form will be rendered with this controller.
 *
 * This controller is using the OrganisationSearchRestService to provide the user a quick way to reuse information about a existing organisation.OrganisationSearchRestService
 */
@Controller
@RequestMapping("/organisation/create")
public class OrganisationCreationController {
    private static final Log LOG = LogFactory.getLog(OrganisationCreationController.class);

    public static final String ORGANISATION_ID = "organisationId";
    static final String ORGANISATION_FORM = "organisationForm";
    private static final String TEMPLATE_PATH = "registration/organisation";
    private static final String CONFIRM_SELECTED_ORGANISATION = "confirm-selected-organisation";
    private static final String ADD_ADDRESS_DETAILS = "add-address-details";
    private static final String CREATE_ORGANISATION_TYPE = "create-organisation-type";
    private static final String FIND_BUSINESS = "find-business";
    private static final String FIND_ORGANISATION = "find-organisation";
    private static final String SELECTED_ORGANISATION = "selected-organisation";
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
    private MessageSource messageSource;

    private Validator validator;

    @Autowired
    public void setValidator(Validator validator) {
        this.validator = validator;
    }


    @RequestMapping("/" + CREATE_ORGANISATION_TYPE)
    public String createAccountOrganisationType(@ModelAttribute Form form, Model model) {
        model.addAttribute("form", form);
        return TEMPLATE_PATH + "/" + CREATE_ORGANISATION_TYPE;
    }

    @RequestMapping(value = {"/" + FIND_ORGANISATION, "/" + FIND_ORGANISATION + "/**"}, method = RequestMethod.GET)
    public String createOrganisation(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                     Model model,
                                     HttpServletRequest request,
                                     HttpServletResponse response) {
        CookieUtil.removeCookie(response, ORGANISATION_ID);
        organisationForm.setOrganisationSearching(false);
        organisationForm = getFormDataFromCookie(organisationForm, model, request);

        addAddressOptions(organisationForm);
        addSelectedAddress(organisationForm);

        CookieUtil.saveToCookie(response, ORGANISATION_FORM, JsonUtil.getSerializedObject(organisationForm));
        model.addAttribute(ORGANISATION_FORM, organisationForm);

        model.addAttribute("searchLabel",getMessageByOrganisationType(organisationForm.getOrganisationTypeEnum(), "SearchLabel",  request.getLocale()));
        model.addAttribute("searchHint", getMessageByOrganisationType(organisationForm.getOrganisationTypeEnum(), "SearchHint",  request.getLocale()));

        if(OrganisationTypeEnum.BUSINESS.equals(organisationForm.getOrganisationTypeEnum()) ||
                OrganisationTypeEnum.ACADEMIC.equals(organisationForm.getOrganisationTypeEnum())
                ){
            model.addAttribute("searchEnabled", true);
        }else{
            model.addAttribute("searchEnabled", false);
        }
        return TEMPLATE_PATH + "/" + FIND_ORGANISATION;
    }

    private String getMessageByOrganisationType(OrganisationTypeEnum orgTypeEnum, String textKey, Locale locale) {
        String searchLabel;
        try{
            searchLabel = messageSource.getMessage(String.format("registration.%s.%s", orgTypeEnum.toString(), textKey), null, locale);
        }catch(NoSuchMessageException e){
            LOG.error(e);
            searchLabel = messageSource.getMessage(String.format("registration.DEFAULT.%s", textKey), null, locale);
        }
        return searchLabel;
    }

    private OrganisationCreationForm getFormDataFromCookie(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm, Model model, HttpServletRequest request) {
        BindingResult bindingResult;// Merge information from cookie into ModelAttribute.
        String organisationFormJson = CookieUtil.getCookieValue(request, ORGANISATION_FORM);

        if (StringUtils.hasText(organisationFormJson)) {
            organisationForm = JsonUtil.getObjectFromJson(organisationFormJson, OrganisationCreationForm.class);
            addOrganisationType(organisationForm, request);
            bindingResult = new BeanPropertyBindingResult(organisationForm, ORGANISATION_FORM);

            if(organisationForm.getAddressForm().isTriedToSearch() && isBlank(organisationForm.getAddressForm().getPostcodeInput())) {
                bindingResult.rejectValue("addressForm.postcodeInput", "EMPTY_POSTCODE_SEARCH");
            }

            validator.validate(organisationForm, bindingResult);
            model.addAttribute(BINDING_RESULT_ORGANISATION_FORM, bindingResult);

            BindingResult addressBindingResult = new BeanPropertyBindingResult(organisationForm.getAddressForm().getSelectedPostcode(), SELECTED_POSTCODE);
            organisationFormValidate(organisationForm, bindingResult, addressBindingResult);

            searchOrganisation(organisationForm);
        } else {
            addOrganisationType(organisationForm, request);
        }
        return organisationForm;
    }

    /**
     * Get the list of postcode options, with the entered postcode. Add those results to the form.
     */
    private void addAddressOptions(OrganisationCreationForm organisationForm) {
        if (StringUtils.hasText(organisationForm.getAddressForm().getPostcodeInput())) {
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
        if (StringUtils.hasText(addressForm.getSelectedPostcodeIndex()) && addressForm.getSelectedPostcode() == null) {
            addressForm.setSelectedPostcode(addressForm.getPostcodeOptions().get(Integer.parseInt(addressForm.getSelectedPostcodeIndex())));
            organisationForm.setAddressForm(addressForm);
        }
    }

    /**
     * User has chosen the organisation type in the previous screen, get that from he cookie and add it to the form.
     */
    private OrganisationTypeResource addOrganisationType(OrganisationCreationForm organisationForm, HttpServletRequest request) {
        String organisationTypeJson = CookieUtil.getCookieValue(request, AcceptInviteController.ORGANISATION_TYPE);
        OrganisationTypeResource organisationType = null;
        if(StringUtils.hasText(organisationTypeJson)){
            OrganisationTypeForm organisationTypeForm = JsonUtil.getObjectFromJson(organisationTypeJson, OrganisationTypeForm.class);
            if(organisationTypeForm.getOrganisationType()!=null){
                organisationType = organisationTypeRestService.findOne(organisationTypeForm.getOrganisationType()).getSuccessObject();
                organisationForm.setOrganisationType(organisationType);
            }
        }
        return organisationType;
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
            if (StringUtils.hasText(organisationForm.getOrganisationSearchName())) {
                List<OrganisationSearchResult> searchResults;
                String encodedSearchString = escapePathVariable(organisationForm.getOrganisationSearchName());
                searchResults = organisationSearchRestService.searchOrganisation(organisationForm.getOrganisationType().getId(), encodedSearchString)
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

    @RequestMapping(value = "/" + FIND_ORGANISATION + "/**", params = SEARCH_ORGANISATION, method = RequestMethod.POST)
    public String searchOrganisation(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                     HttpServletRequest request, HttpServletResponse response) {
        addOrganisationType(organisationForm, request);
        organisationForm.setOrganisationSearching(true);
        organisationForm.setManualEntry(false);
        CookieUtil.saveToCookie(response, ORGANISATION_FORM, JsonUtil.getSerializedObject(organisationForm));
        return "redirect:/organisation/create/" + FIND_ORGANISATION + "?searchTerm=" + escapePathVariable(organisationForm.getOrganisationSearchName());

    }

    @RequestMapping(value = "/" + FIND_ORGANISATION + "/**", params = NOT_IN_COMPANY_HOUSE, method = RequestMethod.POST)
    public String manualOrganisationEntry(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                          HttpServletRequest request, HttpServletResponse response) {
        addOrganisationType(organisationForm, request);
        organisationForm.setOrganisationSearching(false);
        organisationForm.setManualEntry(true);
        CookieUtil.saveToCookie(response, ORGANISATION_FORM, JsonUtil.getSerializedObject(organisationForm));
        return "redirect:/organisation/create/" + FIND_ORGANISATION;
    }

    @RequestMapping(value = "/" + FIND_ORGANISATION + "/**", params = MANUAL_ADDRESS, method = RequestMethod.POST)
    public String manualAddressWithCompanyHouse(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                                HttpServletRequest request, HttpServletResponse response) {
        organisationForm.setAddressForm(new AddressForm());
        organisationForm.getAddressForm().setManualAddress(true);
        organisationForm.setOrganisationSearching(false);
        organisationForm.setManualEntry(true);
        CookieUtil.saveToCookie(response, ORGANISATION_FORM, JsonUtil.getSerializedObject(organisationForm));
        return "redirect:/organisation/create/" + FIND_ORGANISATION;
    }

    @RequestMapping(value = {"/" + SELECTED_ORGANISATION + "/{searchOrganisationId}"}, method = RequestMethod.GET)
    public String amendOrganisationAddress(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                           Model model,
                                           @PathVariable("searchOrganisationId") final String searchOrganisationId,
                                           HttpServletRequest request,
                                           HttpServletResponse response) {
        organisationForm = getFormDataFromCookie(organisationForm, model, request);
        organisationForm.setSearchOrganisationId(searchOrganisationId);

        addSelectedOrganisation(organisationForm, model);

        CookieUtil.saveToCookie(response, ORGANISATION_FORM, JsonUtil.getSerializedObject(organisationForm));
        model.addAttribute(ORGANISATION_FORM, organisationForm);


        if (OrganisationTypeEnum.BUSINESS.getOrganisationTypeId().equals(organisationForm.getOrganisationType().getId())) {
            return TEMPLATE_PATH + "/" + CONFIRM_SELECTED_ORGANISATION;
        } else {
            return TEMPLATE_PATH + "/" + ADD_ADDRESS_DETAILS;
        }
    }

    /**
     * +
     * after user has selected a organisation, get the details and add it to the form and the model.
     */
    private OrganisationSearchResult addSelectedOrganisation(OrganisationCreationForm organisationForm, Model model) {
        if (!organisationForm.isManualEntry() && StringUtils.hasText(organisationForm.getSearchOrganisationId())) {
            OrganisationSearchResult s = organisationSearchRestService.getOrganisation(organisationForm.getOrganisationType().getId(), organisationForm.getSearchOrganisationId()).getSuccessObject();
            organisationForm.setOrganisationName(s.getName());
            model.addAttribute("selectedOrganisation", s);
            return s;
        }
        return null;
    }

    @RequestMapping(value = {"/" + SELECTED_ORGANISATION + "/{searchOrganisationId}/{selectedPostcodeIndex}"}, method = RequestMethod.GET)
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

        CookieUtil.saveToCookie(response, ORGANISATION_FORM, JsonUtil.getSerializedObject(organisationForm));
        model.addAttribute(ORGANISATION_FORM, organisationForm);
        if (OrganisationTypeEnum.BUSINESS.getOrganisationTypeId().equals(organisationForm.getOrganisationType().getId())) {
            return TEMPLATE_PATH + "/" + CONFIRM_SELECTED_ORGANISATION;
        } else {
            return TEMPLATE_PATH + "/" + ADD_ADDRESS_DETAILS;
        }
    }

    @RequestMapping(value = {"/selected-organisation/{searchOrganisationId}/search-postcode"}, method = RequestMethod.GET)
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

        CookieUtil.saveToCookie(response, ORGANISATION_FORM, JsonUtil.getSerializedObject(organisationForm));
        model.addAttribute(ORGANISATION_FORM, organisationForm);

        if (OrganisationTypeEnum.BUSINESS.getOrganisationTypeId().equals(organisationForm.getOrganisationType().getId())) {
            return TEMPLATE_PATH + "/" + CONFIRM_SELECTED_ORGANISATION;
        } else {
            return TEMPLATE_PATH + "/" + ADD_ADDRESS_DETAILS;
        }
    }

    @RequestMapping(value = {"/" + SELECTED_ORGANISATION + "/**", "/" + FIND_ORGANISATION + "**"}, params = SEARCH_ADDRESS, method = RequestMethod.POST)
    public String searchAddress(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                Model model,
                                HttpServletRequest request,
                                HttpServletResponse response,
                                @RequestHeader(value = REFERER, required = false) final String referer) {
        addOrganisationType(organisationForm, request);
        addSelectedOrganisation(organisationForm, model);
        organisationForm.getAddressForm().setSelectedPostcodeIndex(null);
        organisationForm.getAddressForm().setTriedToSearch(true);
        CookieUtil.saveToCookie(response, ORGANISATION_FORM, JsonUtil.getSerializedObject(organisationForm));
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
            if (StringUtils.hasText(organisationForm.getSearchOrganisationId()) && organisationForm.getAddressForm().getSelectedPostcodeIndex() != null && StringUtils.hasText(organisationForm.getAddressForm().getPostcodeInput())) {
                return String.format("redirect:%s/%s/%s/%s", BASE_URL, redirectPart, organisationForm.getSearchOrganisationId(), organisationForm.getAddressForm().getSelectedPostcodeIndex());
            } else if (StringUtils.hasText(organisationForm.getSearchOrganisationId()) && StringUtils.hasText(organisationForm.getAddressForm().getPostcodeInput())) {
                return String.format("redirect:%s/%s/%s/search-postcode?searchTerm=%s", BASE_URL, redirectPart, organisationForm.getSearchOrganisationId(), escapePathVariable(organisationForm.getAddressForm().getPostcodeInput()));
            } else if (StringUtils.hasText(organisationForm.getSearchOrganisationId())) {
                return String.format("redirect:%s/%s/%s", BASE_URL, redirectPart, organisationForm.getSearchOrganisationId());
            } else {
                return String.format("redirect:%s/%s", BASE_URL, redirectPart);
            }
        } else {
            if (organisationForm.getAddressForm().getSelectedPostcodeIndex() != null && StringUtils.hasText(organisationForm.getAddressForm().getPostcodeInput())) {
                return String.format("redirect:%s/%s/%s/%s", BASE_URL, redirectPart, escapePathVariable(organisationForm.getAddressForm().getPostcodeInput()), organisationForm.getAddressForm().getSelectedPostcodeIndex());
            } else if (StringUtils.hasText(organisationForm.getAddressForm().getPostcodeInput())) {
                return String.format("redirect:%s/%s?searchTerm=%s", BASE_URL, redirectPart, escapePathVariable(organisationForm.getAddressForm().getPostcodeInput()));
            } else {
                return String.format("redirect:%s/%s", BASE_URL, redirectPart);
            }
        }
    }

    @RequestMapping(value = {"/" + SELECTED_ORGANISATION + "/**", "/" + FIND_ORGANISATION + "**"}, params = SELECT_ADDRESS, method = RequestMethod.POST)
    public String selectAddress(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                HttpServletRequest request, HttpServletResponse response,
                                @RequestHeader(value = REFERER, required = false) final String referer) {
        addOrganisationType(organisationForm, request);
        organisationForm.getAddressForm().setSelectedPostcode(null);
        CookieUtil.saveToCookie(response, ORGANISATION_FORM, JsonUtil.getSerializedObject(organisationForm));
        return getRedirectUrlInvalidSave(organisationForm, referer);
    }

    @RequestMapping(value = "/" + SELECTED_ORGANISATION + "/**", params = MANUAL_ADDRESS, method = RequestMethod.POST)
    public String manualAddress(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                HttpServletRequest request, HttpServletResponse response) {
        organisationForm.setAddressForm(new AddressForm());
        organisationForm.getAddressForm().setManualAddress(true);
        CookieUtil.saveToCookie(response, ORGANISATION_FORM, JsonUtil.getSerializedObject(organisationForm));
        return String.format("redirect:%s/%s/%s", BASE_URL, SELECTED_ORGANISATION, organisationForm.getSearchOrganisationId());
    }

    @RequestMapping(value = {"/selected-organisation/**", "/" + FIND_ORGANISATION + "**"}, params = SAVE_ORGANISATION_DETAILS, method = RequestMethod.POST)
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

        if (!bindingResult.hasFieldErrors(ORGANISATION_NAME) && !bindingResult.hasFieldErrors(USE_SEARCH_RESULT_ADDRESS) && !addressBindingResult.hasErrors()) {
            CookieUtil.saveToCookie(response, ORGANISATION_FORM, JsonUtil.getSerializedObject(organisationForm));
            return "redirect:" + BASE_URL + "/" + CONFIRM_ORGANISATION;
        } else {
            organisationForm.setTriedToSave(true);
            organisationForm.getAddressForm().setTriedToSave(true);
            CookieUtil.saveToCookie(response, ORGANISATION_FORM, JsonUtil.getSerializedObject(organisationForm));
            return getRedirectUrlInvalidSave(organisationForm, referer);
        }
    }


    /**
     * Confirm the company details (user input, not from company-house)
     */
    @RequestMapping(value = "/" + CONFIRM_ORGANISATION, method = RequestMethod.GET)
    public String confirmCompany(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                 Model model,
                                 HttpServletRequest request) throws IOException {
        organisationForm = getFormDataFromCookie(organisationForm, model, request);
        addOrganisationType(organisationForm, request);
        addSelectedOrganisation(organisationForm, model);
        model.addAttribute(ORGANISATION_FORM, organisationForm);
        return TEMPLATE_PATH + "/" + CONFIRM_ORGANISATION;
    }

    @RequestMapping(value = "/" + FIND_BUSINESS, method = RequestMethod.GET)
    public String createOrganisationBusiness(HttpServletRequest request, HttpServletResponse response) {
        // when user comes to this page, set the organisationTypeForm, and redirect.
        OrganisationTypeForm organisationTypeForm = new OrganisationTypeForm();
        organisationTypeForm.setOrganisationType(OrganisationTypeEnum.BUSINESS.getOrganisationTypeId());
        String orgTypeForm = JsonUtil.getSerializedObject(organisationTypeForm);
        CookieUtil.saveToCookie(response, AcceptInviteController.ORGANISATION_TYPE, orgTypeForm);
        return "redirect:" + BASE_URL + "/" + FIND_ORGANISATION;
    }

    @RequestMapping("/save-organisation")
    public String saveOrganisation(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm, Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
        organisationForm = getFormDataFromCookie(organisationForm, model, request);
        OrganisationSearchResult selectedOrganisation = addSelectedOrganisation(organisationForm, model);
        AddressResource address = organisationForm.getAddressForm().getSelectedPostcode();

        OrganisationResource organisationResource = new OrganisationResource();
        organisationResource.setName(organisationForm.getOrganisationName());
        organisationResource.setOrganisationType(organisationForm.getOrganisationType().getId());

        if (OrganisationTypeEnum.BUSINESS.getOrganisationTypeId().equals(organisationForm.getOrganisationType().getId())) {
            organisationResource.setCompanyHouseNumber(organisationForm.getSearchOrganisationId());
        }

        organisationResource = saveNewOrganisation(organisationResource, request);
        if (address != null && !organisationForm.isUseSearchResultAddress()) {
            organisationService.addAddress(organisationResource, address, OPERATING);
        }
        if (selectedOrganisation != null && selectedOrganisation.getOrganisationAddress() != null) {
            organisationService.addAddress(organisationResource, selectedOrganisation.getOrganisationAddress(), REGISTERED);
        }
        CookieUtil.saveToCookie(response, ORGANISATION_ID, String.valueOf(organisationResource.getId()));
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
        String cookieHash = CookieUtil.getCookieValue(request, AcceptInviteController.INVITE_HASH);
        if (StringUtils.hasText(cookieHash)) {
            final OrganisationResource finalOrganisationResource = organisationResource;

            inviteRestService.getInviteByHash(cookieHash).andOnSuccess(
                    s ->
                            inviteOrganisationRestService.findOne(s.getInviteOrganisation()).handleSuccessOrFailure(
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
