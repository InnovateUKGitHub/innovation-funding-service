package com.worth.ifs.organisation;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.address.service.AddressRestService;
import com.worth.ifs.application.AcceptInviteController;
import com.worth.ifs.application.form.*;
import com.worth.ifs.application.service.OrganisationService;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.invite.service.InviteOrganisationRestService;
import com.worth.ifs.invite.service.InviteRestService;
import com.worth.ifs.organisation.resource.CompanyHouseBusiness;
import com.worth.ifs.organisation.resource.OrganisationSearchResult;
import com.worth.ifs.registration.RegistrationController;
import com.worth.ifs.user.domain.AddressType;
import com.worth.ifs.user.domain.OrganisationTypeEnum;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.OrganisationTypeResource;
import com.worth.ifs.user.service.OrganisationSearchRestService;
import com.worth.ifs.user.service.OrganisationTypeRestService;
import com.worth.ifs.util.CookieUtil;
import com.worth.ifs.util.JsonUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.commons.rest.RestResult.restFailure;

@Controller
@RequestMapping("/organisation/create")
public class OrganisationCreationController {
    private final Log log = LogFactory.getLog(this.getClass());
    public static final String ORGANISATION_FORM = "organisationForm";
    public static final String COMPANY_NAME = "company_name";
    public static final String COMPANY_ADDRESS = "company_address";

    @Autowired
    protected InviteRestService inviteRestService;
    @Autowired
    protected InviteOrganisationRestService inviteOrganisationRestService;
    @Autowired
    protected AddressRestService addressRestService;
    @Autowired
    protected OrganisationService organisationService;
    @Autowired
    protected OrganisationTypeRestService organisationTypeRestService;
    @Autowired
    protected OrganisationSearchRestService organisationSearchRestService;

    Validator validator;

    @Autowired
    public void setValidator(Validator validator) {
        this.validator = validator;
    }


    @RequestMapping("/create-organisation-type")
    public String createAccountOrganisationType(@ModelAttribute Form form, Model model) {
        model.addAttribute("form", form);
        return CREATE_APPLICATION + "/" + CREATE_ORGANISATION_TYPE;
    }

    @RequestMapping(value = {"/" + FIND_ORGANISATION, "/" + FIND_ORGANISATION + "/**"}, method = RequestMethod.GET)
    public String createOrganisation(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                     BindingResult bindingResult,
                                     Model model,
                                     HttpServletRequest request,
                                     HttpServletResponse response) {
        organisationForm.setOrganisationSearching(false);
        organisationForm = getFormDataFromCookie(organisationForm, model, request);
        addAddressOptions(organisationForm);
        addSelectedAddress(organisationForm);

        CookieUtil.saveToCookie(response, ORGANISATION_FORM, JsonUtil.getSerializedObject(organisationForm));
        model.addAttribute(ORGANISATION_FORM, organisationForm);
        if (OrganisationTypeEnum.BUSINESS.getOrganisationTypeId().equals(organisationForm.getOrganisationType().getId())) {
            return CREATE_APPLICATION + "/" + FIND_BUSINESS;
        } else {
            return CREATE_APPLICATION + "/" + FIND_ORGANISATION;
        }
    }

    private OrganisationCreationForm getFormDataFromCookie(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm, Model model, HttpServletRequest request) {
        BindingResult bindingResult;// Merge information from cookie into ModelAttribute.
        String organisationFormJson = CookieUtil.getCookieValue(request, ORGANISATION_FORM);

        if (StringUtils.hasText(organisationFormJson)) {
            organisationForm = JsonUtil.getObjectFromJson(organisationFormJson, OrganisationCreationForm.class);
            addOrganisationType(organisationForm, request);
            bindingResult = new BeanPropertyBindingResult(organisationForm, ORGANISATION_FORM);
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
        OrganisationTypeForm organisationTypeForm = JsonUtil.getObjectFromJson(organisationTypeJson, OrganisationTypeForm.class);
        OrganisationTypeResource organisationType = organisationTypeRestService.findOne(organisationTypeForm.getOrganisationType()).getSuccessObject();
        organisationForm.setOrganisationType(organisationType);
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
                searchResults = organisationSearchRestService.searchOrganisation(organisationForm.getOrganisationType().getId(), organisationForm.getOrganisationSearchName())
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

    public List<Address> searchPostcode(String postcodeInput) {
        List<Address> addresses = new ArrayList<Address>();
        addresses.add(new Address(
                "Montrose House 1",
                "Clayhill Park",
                "",
                "Cheshire West and Chester",
                "England",
                "Neston",
                "po_bo",
                "CH64 3RU",
                "Cheshire"
        ));
        addresses.add(new Address(
                "Montrose House",
                "Clayhill Park",
                "",
                "Cheshire West and Chester",
                "England",
                "Neston",
                "po_bo",
                "CH64 3RU",
                "Cheshire"
        ));
        return addresses;
    }

    @RequestMapping(value = "/" + FIND_ORGANISATION + "/**", params = SEARCH_ORGANISATION, method = RequestMethod.POST)
    public String searchOrganisation(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                     HttpServletRequest request, HttpServletResponse response) {
        OrganisationTypeResource organisationType = addOrganisationType(organisationForm, request);
        organisationForm.setOrganisationSearching(true);
        organisationForm.setManualEntry(false);
        CookieUtil.saveToCookie(response, ORGANISATION_FORM, JsonUtil.getSerializedObject(organisationForm));
        return "redirect:/organisation/create/" + FIND_ORGANISATION + "/" + organisationForm.getOrganisationSearchName();
    }

    @RequestMapping(value = "/" + FIND_ORGANISATION + "/**", params = NOT_IN_COMPANY_HOUSE, method = RequestMethod.POST)
    public String manualOrganisationEntry(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                          HttpServletRequest request, HttpServletResponse response) {
        OrganisationTypeResource organisationType = addOrganisationType(organisationForm, request);
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
                                           BindingResult bindingResult,
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
            return CREATE_APPLICATION + "/" + CONFIRM_SELECTED_ORGANISATION;
        } else {
            return CREATE_APPLICATION + "/" + ADD_ADDRESS_DETAILS;
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

    @RequestMapping(value = {"/" + SELECTED_ORGANISATION + "/{searchOrganisationId}/{postcode}/{selectedPostcodeIndex}"}, method = RequestMethod.GET)
    public String amendOrganisationAddress(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                           BindingResult bindingResult,
                                           Model model,
                                           @PathVariable("searchOrganisationId") final String searchOrganisationId,
                                           @PathVariable("postcode") final String postcode,
                                           @PathVariable("selectedPostcodeIndex") final Long selectedPostcodeIndex,
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
            return CREATE_APPLICATION + "/" + CONFIRM_SELECTED_ORGANISATION;
        } else {
            return CREATE_APPLICATION + "/" + ADD_ADDRESS_DETAILS;
        }
    }

    private void addSelectedAddress(OrganisationCreationForm organisationForm) {
        AddressForm addressForm = organisationForm.getAddressForm();
        if(StringUtils.hasText(addressForm.getSelectedPostcodeIndex()) && addressForm.getSelectedPostcode() == null){
            addressForm.setSelectedPostcode(addressForm.getPostcodeOptions().get(Integer.parseInt(addressForm.getSelectedPostcodeIndex())));
            organisationForm.setAddressForm(addressForm);
        }
    }

    @RequestMapping(value = {"/selected-organisation/{searchOrganisationId}/{postcode}"}, method = RequestMethod.GET)
    public String amendOrganisationAddress(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                           BindingResult bindingResult,
                                           Model model,
                                           @PathVariable("searchOrganisationId") final String searchOrganisationId,
                                           @PathVariable("postcode") final String postcode,
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
            return CREATE_APPLICATION + "/" + CONFIRM_SELECTED_ORGANISATION;
        } else {
            return CREATE_APPLICATION + "/" + ADD_ADDRESS_DETAILS;
        }
    }

    @RequestMapping(value = {"/" + SELECTED_ORGANISATION + "/**", "/" + FIND_ORGANISATION + "**"}, params = SEARCH_ADDRESS, method = RequestMethod.POST)
    public String searchAddress(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                Model model,
                                HttpServletRequest request,
                                HttpServletResponse response,
                                @RequestHeader(value = REFERER, required = false) final String referer) {
        OrganisationTypeResource organisationType = addOrganisationType(organisationForm, request);
        addSelectedOrganisation(organisationForm, model);
        organisationForm.getAddressForm().setSelectedPostcodeIndex(null);
        organisationForm.getAddressForm().setTriedToSave(true);
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
                return String.format("redirect:" + BASE_URL + "/%s/%s/%s/%s", redirectPart, organisationForm.getSearchOrganisationId(), organisationForm.getAddressForm().getPostcodeInput(), organisationForm.getAddressForm().getSelectedPostcodeIndex());
            } else if (StringUtils.hasText(organisationForm.getSearchOrganisationId()) && StringUtils.hasText(organisationForm.getAddressForm().getPostcodeInput())) {
                return String.format("redirect:" + BASE_URL + "/%s/%s/%s", redirectPart, organisationForm.getSearchOrganisationId(), organisationForm.getAddressForm().getPostcodeInput());
            } else if (StringUtils.hasText(organisationForm.getSearchOrganisationId())) {
                return String.format("redirect:" + BASE_URL + "/%s/%s", redirectPart, organisationForm.getSearchOrganisationId());
            } else {
                return String.format("redirect:" + BASE_URL + "/%s", redirectPart);
            }
        } else {
            if (organisationForm.getAddressForm().getSelectedPostcodeIndex() != null && StringUtils.hasText(organisationForm.getAddressForm().getPostcodeInput())) {
                return String.format("redirect:" + BASE_URL + "/%s/%s/%s", redirectPart, organisationForm.getAddressForm().getPostcodeInput(), organisationForm.getAddressForm().getSelectedPostcodeIndex());
            } else if (StringUtils.hasText(organisationForm.getAddressForm().getPostcodeInput())) {
                return String.format("redirect:" + BASE_URL + "/%s/%s", redirectPart, organisationForm.getAddressForm().getPostcodeInput());
            } else {
                return String.format("redirect:" + BASE_URL + "/%s", redirectPart);
            }
        }

    }

    @RequestMapping(value = {"/" + SELECTED_ORGANISATION + "/**", "/" + FIND_ORGANISATION + "**"}, params = SELECT_ADDRESS, method = RequestMethod.POST)
    public String selectAddress(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                HttpServletRequest request, HttpServletResponse response,
                                @RequestHeader(value = REFERER, required = false) final String referer) {
        OrganisationTypeResource organisationType = addOrganisationType(organisationForm, request);
        organisationForm.getAddressForm().setSelectedPostcode(null);
        CookieUtil.saveToCookie(response, ORGANISATION_FORM, JsonUtil.getSerializedObject(organisationForm));
//        return String.format("redirect:/organisation/create/selected-organisation/%s/%s/%s", organisationForm.getSearchOrganisationId(), organisationForm.getAddressForm().getPostcodeInput(), Integer.valueOf(organisationForm.getAddressForm().getSelectedPostcodeIndex()));
        return getRedirectUrlInvalidSave(organisationForm, referer);
    }

    @RequestMapping(value = "/" + SELECTED_ORGANISATION + "/**", params = MANUAL_ADDRESS, method = RequestMethod.POST)
    public String manualAddress(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                HttpServletRequest request, HttpServletResponse response) {
        organisationForm.setAddressForm(new AddressForm());
        organisationForm.getAddressForm().setManualAddress(true);
        CookieUtil.saveToCookie(response, ORGANISATION_FORM, JsonUtil.getSerializedObject(organisationForm));
        return String.format("redirect:" + BASE_URL + "/" + SELECTED_ORGANISATION + "/%s", organisationForm.getSearchOrganisationId());
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
        OrganisationTypeResource organisationType = addOrganisationType(organisationForm, request);
        addSelectedOrganisation(organisationForm, model);
        model.addAttribute(ORGANISATION_FORM, organisationForm);
        return CREATE_APPLICATION + "/" + CONFIRM_ORGANISATION;
    }

    @RequestMapping(value = "/" + FIND_BUSINESS, method = RequestMethod.GET)
    public String createOrganisationBusiness(HttpServletResponse response) {
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
        Address address = organisationForm.getAddressForm().getSelectedPostcode();

        OrganisationResource organisationResource = new OrganisationResource();
        organisationResource.setName(organisationForm.getOrganisationName());
        organisationResource.setOrganisationType(organisationForm.getOrganisationType().getId());

        if (OrganisationTypeEnum.BUSINESS.getOrganisationTypeId().equals(organisationForm.getOrganisationType().getId())) {
            organisationResource.setCompanyHouseNumber(organisationForm.getSearchOrganisationId());
        }

        organisationResource = saveNewOrganisation(organisationResource, request);
        if (address != null) {
            organisationService.addAddress(organisationResource, address, AddressType.OPERATING);
        }
        if (selectedOrganisation != null && selectedOrganisation.getOrganisationAddress() != null) {
            organisationService.addAddress(organisationResource, selectedOrganisation.getOrganisationAddress(), AddressType.REGISTERED);
        }

        CookieUtil.removeCookie(response, ORGANISATION_FORM);
        CookieUtil.removeCookie(response, AcceptInviteController.ORGANISATION_TYPE);
        return "redirect:" + RegistrationController.BASE_URL + "?organisationId=" + organisationResource.getId();
    }

    private OrganisationResource saveNewOrganisation(OrganisationResource organisationResource, HttpServletRequest request) {
        organisationResource = organisationService.save(organisationResource);
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

            RestResult<Void> inviteRestResult = inviteRestService.getInviteByHash(cookieHash).andOnSuccess(
                    s -> {
                        return inviteOrganisationRestService.findOne(s.getInviteOrganisation()).handleSuccessOrFailure(
                                f -> restFailure(HttpStatus.NOT_FOUND),
                                i -> {
                                    if (i.getOrganisation() == null) {
                                        i.setOrganisation(finalOrganisationResource.getId());
                                        // Save the created organisation Id, so the next invitee does not have to..
                                        return inviteOrganisationRestService.put(i);
                                    }
                                    return restFailure(HttpStatus.ALREADY_REPORTED);
                                }
                        );
                    }
            );
        }
    }

    @RequestMapping("/save-organisation")
    public String saveOrganisation(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm, Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
        organisationForm = getFormDataFromCookie(organisationForm, model, request);
        OrganisationSearchResult selectedOrganisation = addSelectedOrganisation(organisationForm, model);
        AddressResource address = organisationForm.getAddressForm().getSelectedPostcode();


        OrganisationResource organisationResource = new OrganisationResource();
        organisationResource.setName(organisationForm.getOrganisationName());
        organisationResource.setOrganisationType(organisationForm.getOrganisationType().getId());

        if (OrganisationTypeEnum.BUSINESS.getOrganisationTypeId().equals(organisationForm.getOrganisationType().getId())){
            organisationResource.setCompanyHouseNumber(organisationForm.getSearchOrganisationId());
        }


        organisationResource = saveNewOrganisation(organisationResource, request);


        if (address != null) {
            organisationService.addAddress(organisationResource, address, AddressType.OPERATING);
        }
        if(selectedOrganisation.getOrganisationAddress()!= null){
            organisationService.addAddress(organisationResource, selectedOrganisation.getOrganisationAddress(), AddressType.REGISTERED);
        }


        CookieUtil.removeCookie(response, ORGANISATION_FORM);
        CookieUtil.removeCookie(response, AcceptInviteController.ORGANISATION_TYPE);
        return "redirect:/registration/register?organisationId=" + organisationResource.getId();
    }

    public List<AddressResource> searchPostcode(String postcodeInput) {
        RestResult<List<AddressResource>>  addressLookupRestResult = addressRestService.doLookup(postcodeInput);
        List<AddressResource> addressResourceList = addressLookupRestResult.andOnSuccessReturn(result -> result).getSuccessObject();
        return addressResourceList;
    }

    public List<OrganisationSearchResult> searchCompanyHouse(String organisationName) {
        return organisationService.searchCompanyHouseOrganisations(organisationName);
    }

    public void searchPostcodes(CreateApplicationForm form) {
        if (StringUtils.hasText(form.getAddressForm().getPostcodeInput())) {
            form.getAddressForm().setPostcodeOptions(searchPostcode(form.getAddressForm().getPostcodeInput()));
        }
    }

    public void selectPostcodeAddress(CreateApplicationForm form) {
        if (form.getAddressForm().getPostcodeOptions() != null && form.getAddressForm().getPostcodeOptions().size() != 0) {
            int indexInt = Integer.parseInt(form.getAddressForm().getSelectedPostcodeIndex());
            if (form.getAddressForm().getPostcodeOptions().get(indexInt) != null) {
                form.getAddressForm().setSelectedPostcode(form.getAddressForm().getPostcodeOptions().get(indexInt));
            }
        }
    }
}
