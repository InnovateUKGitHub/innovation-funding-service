package com.worth.ifs.organisation;

import com.worth.ifs.application.AcceptInviteController;
import com.worth.ifs.application.form.*;
import com.worth.ifs.application.service.OrganisationService;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.invite.service.InviteOrganisationRestService;
import com.worth.ifs.invite.service.InviteRestService;
import com.worth.ifs.organisation.domain.Address;
import com.worth.ifs.organisation.resource.OrganisationSearchResult;
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
import org.springframework.web.servlet.ModelAndView;

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
    public static final String ORGANISATION_FORM = "organisationForm";
    @Autowired
    private InviteRestService inviteRestService;
    @Autowired
    private InviteOrganisationRestService inviteOrganisationRestService;

    public static final String COMPANY_HOUSE_COMPANY_ID = "companyId";
    public static final String NOT_IN_COMPANY_HOUSE = "not-in-company-house";
    public static final String MANUAL_ADDRESS = "manual-address";
    public static final String SEARCH_ADDRESS = "search-address";
    public static final String SEARCH_COMPANY_HOUSE = "search-company-house";
    public static final String SELECT_ADDRESS = "select-address";
    public static final String SAVE_COMPANY_DETAILS = "save-company-details";
    public static final String COMPANY_ID = "companyId";
    public static final String COMPANY_NAME = "company_name";
    public static final String CONFIRM_COMPANY_DETAILS = "confirm-company-details";
    public static final String COMPANY_ADDRESS = "company_address";
    public static final String ORGANISATION_NAME = "organisationName";
    public static final String COMPANY_HOUSE_NAME = "companyHouseName";
    public static final String POSTCODE = "postcode";

    private final Log log = LogFactory.getLog(this.getClass());
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
        return "create-application/create-organisation-type";
    }

    @RequestMapping(value = { "/find-organisation", "/find-organisation/**"}, method = RequestMethod.GET)
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
        if(OrganisationTypeEnum.BUSINESS.getOrganisationTypeId().equals(organisationForm.getOrganisationType().getId())){
            return "create-application/find-business";
        }else{
            return "create-application/find-organisation";
        }
    }

    private OrganisationCreationForm getFormDataFromCookie(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm, Model model, HttpServletRequest request) {
        BindingResult bindingResult;// Merge information from cookie into ModelAttribute.
        String organisationFormJson = CookieUtil.getCookieValue(request, ORGANISATION_FORM);


        if(StringUtils.hasText(organisationFormJson)){
            organisationForm = JsonUtil.getObjectFromJson(organisationFormJson, OrganisationCreationForm.class);
            addOrganisationType(organisationForm, request);
            bindingResult = new BeanPropertyBindingResult(organisationForm, ORGANISATION_FORM);
            validator.validate(organisationForm, bindingResult);
            model.addAttribute("org.springframework.validation.BindingResult.organisationForm", bindingResult);

            BindingResult addressBindingResult = new BeanPropertyBindingResult(organisationForm.getAddressForm().getSelectedPostcode(), "selectedPostcode");
            organisationFormValidate(organisationForm, bindingResult, addressBindingResult);

            if(organisationForm.isOrganisationSearching()){
                if(StringUtils.hasText(organisationForm.getOrganisationSearchName())){
                    List<OrganisationSearchResult> searchResults = organisationSearchRestService.searchOrganisation(organisationForm.getOrganisationType().getId(), organisationForm.getOrganisationSearchName()).getSuccessObject();
                    log.warn(String.format("Got search results count: %s ", searchResults.size()));
                    organisationForm.setOrganisationSearchResults(searchResults);
                }else{
                    organisationForm.setOrganisationSearchResults(new ArrayList<>());
                }
            }
        }else{
            addOrganisationType(organisationForm, request);
        }
        return organisationForm;
    }

    private OrganisationTypeResource addOrganisationType(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm, HttpServletRequest request) {
        String organisationTypeJson = CookieUtil.getCookieValue(request, AcceptInviteController.ORGANISATION_TYPE);
        OrganisationTypeForm organisationTypeForm = JsonUtil.getObjectFromJson(organisationTypeJson, OrganisationTypeForm.class);
        log.warn("find organisation type: "+ organisationTypeForm.getOrganisationType());
        OrganisationTypeResource organisationType = organisationTypeRestService.findOne(organisationTypeForm.getOrganisationType()).getSuccessObject();
        organisationForm.setOrganisationType(organisationType);
        return organisationType;
    }

    @RequestMapping(value = "/find-organisation/**", params = "search-organisation", method = RequestMethod.POST)
    public String searchOrganisation(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                     HttpServletRequest request, HttpServletResponse response) {
        OrganisationTypeResource organisationType = addOrganisationType(organisationForm, request);
        organisationForm.setOrganisationSearching(true);
        organisationForm.setManualEntry(false);
        CookieUtil.saveToCookie(response, ORGANISATION_FORM, JsonUtil.getSerializedObject(organisationForm));
        return "redirect:/organisation/create/find-organisation/"+organisationForm.getOrganisationSearchName();
    }
    @RequestMapping(value = "/find-organisation/**", params = "not-in-company-house", method = RequestMethod.POST)
    public String manualOrganisationEntry(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                     HttpServletRequest request, HttpServletResponse response) {
        OrganisationTypeResource organisationType = addOrganisationType(organisationForm, request);
        organisationForm.setOrganisationSearching(false);
        organisationForm.setManualEntry(true);
        CookieUtil.saveToCookie(response, ORGANISATION_FORM, JsonUtil.getSerializedObject(organisationForm));
        return "redirect:/organisation/create/find-organisation";
    }

    @RequestMapping(value = "/find-organisation/**", params = "manual-address", method = RequestMethod.POST)
    public String manualAddressWithCompanyHouse(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                HttpServletRequest request, HttpServletResponse response) {
        organisationForm.setAddressForm(new AddressForm());
        organisationForm.getAddressForm().setManualAddress(true);
        organisationForm.setOrganisationSearching(false);
        organisationForm.setManualEntry(true);
        CookieUtil.saveToCookie(response, ORGANISATION_FORM, JsonUtil.getSerializedObject(organisationForm));
        return "redirect:/organisation/create/find-organisation";
    }


    @RequestMapping(value = {"/selected-organisation/{searchOrganisationId}"}, method = RequestMethod.GET)
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


        if(OrganisationTypeEnum.BUSINESS.getOrganisationTypeId().equals(organisationForm.getOrganisationType().getId())){
            return "create-application/confirm-selected-organisation";
        }else{
            return "create-application/add-address-details";
        }
    }
    @RequestMapping(value = {"/selected-organisation/{searchOrganisationId}/{postcode}/{selectedPostcodeIndex}"}, method = RequestMethod.GET)
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
        if(OrganisationTypeEnum.BUSINESS.getOrganisationTypeId().equals(organisationForm.getOrganisationType().getId())){
            return "create-application/confirm-selected-organisation";
        }else{
            return "create-application/add-address-details";
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

        if(OrganisationTypeEnum.BUSINESS.getOrganisationTypeId().equals(organisationForm.getOrganisationType().getId())){
            return "create-application/confirm-selected-organisation";
        }else{
            return "create-application/add-address-details";
        }
    }

    private OrganisationSearchResult addSelectedOrganisation(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm, Model model) {
        if(!organisationForm.isManualEntry() && StringUtils.hasText(organisationForm.getSearchOrganisationId())){
            OrganisationSearchResult s = organisationSearchRestService.getOrganisation(organisationForm.getOrganisationType().getId(), organisationForm.getSearchOrganisationId()).getSuccessObject();
            organisationForm.setOrganisationName(s.getName());
            model.addAttribute("selectedOrganisation", s);
            return s;
        }
        return null;
    }

    private void addAddressOptions(OrganisationCreationForm organisationForm) {
        if(StringUtils.hasText(organisationForm.getAddressForm().getPostcodeInput())){
            log.warn(String.format("Search for postcode %s", organisationForm.getAddressForm().getPostcodeInput()));
            AddressForm addressForm = organisationForm.getAddressForm();
            addressForm.setPostcodeOptions(searchPostcode(organisationForm.getAddressForm().getPostcodeInput()));
            log.warn("set postcode options");
            addressForm.setPostcodeInput(organisationForm.getAddressForm().getPostcodeInput());
            organisationForm.setAddressForm(addressForm);
        }
    }


    @RequestMapping(value = {"/selected-organisation/**", "/find-organisation**"}, params = "search-address", method = RequestMethod.POST)
    public String searchAddress(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                Model model,
                                 HttpServletRequest request,
                                HttpServletResponse response,
                                @RequestHeader(value = "referer", required = false) final String referer)  {
        OrganisationTypeResource organisationType = addOrganisationType(organisationForm, request);
        addSelectedOrganisation(organisationForm, model);
        organisationForm.getAddressForm().setSelectedPostcodeIndex(null);
        organisationForm.getAddressForm().setTriedToSave(true);
        CookieUtil.saveToCookie(response, ORGANISATION_FORM, JsonUtil.getSerializedObject(organisationForm));
        log.warn("redirect:" + getRedirectUrlInvalidSave(organisationForm, referer));
        return getRedirectUrlInvalidSave(organisationForm, referer);
//        return String.format("redirect:/organisation/create/selected-organisation/%s/%s", organisationForm.getSearchOrganisationId(), organisationForm.getAddressForm().getPostcodeInput());
    }

    @RequestMapping(value = {"/selected-organisation/**", "/find-organisation**"}, params = "select-address", method = RequestMethod.POST)
    public String selectAddress(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                HttpServletRequest request, HttpServletResponse response,
                                @RequestHeader(value = "referer", required = false) final String referer) {
        OrganisationTypeResource organisationType = addOrganisationType(organisationForm, request);
        organisationForm.getAddressForm().setSelectedPostcode(null);
        CookieUtil.saveToCookie(response, ORGANISATION_FORM, JsonUtil.getSerializedObject(organisationForm));
//        return String.format("redirect:/organisation/create/selected-organisation/%s/%s/%s", organisationForm.getSearchOrganisationId(), organisationForm.getAddressForm().getPostcodeInput(), Integer.valueOf(organisationForm.getAddressForm().getSelectedPostcodeIndex()));
        return getRedirectUrlInvalidSave(organisationForm, referer);
    }

    @RequestMapping(value = "/selected-organisation/**", params = "manual-address", method = RequestMethod.POST)
    public String manualAddress(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                HttpServletRequest request, HttpServletResponse response) {
        organisationForm.setAddressForm(new AddressForm());
        organisationForm.getAddressForm().setManualAddress(true);
        CookieUtil.saveToCookie(response, ORGANISATION_FORM, JsonUtil.getSerializedObject(organisationForm));
        return String.format("redirect:/organisation/create/selected-organisation/%s", organisationForm.getSearchOrganisationId());
    }
    @RequestMapping(value = {"/selected-organisation/**", "/find-organisation**"}, params = "save-organisation-details", method = RequestMethod.POST)
    public String saveOrganisation(@Valid @ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                   BindingResult bindingResult,
                                   Model model,
                                HttpServletRequest request, HttpServletResponse response,
                                   @RequestHeader(value = "referer", required = false) final String referer
    ) {
        log.warn("initial binding result error count: "+ bindingResult.getAllErrors().size());
//        organisationForm = getFormDataFromCookie(organisationForm, model, request);
        organisationForm.setTriedToSave(true);
        OrganisationTypeResource organisationType = addOrganisationType(organisationForm, request);
        addSelectedOrganisation(organisationForm, model);

        bindingResult = new BeanPropertyBindingResult(organisationForm, ORGANISATION_FORM);
        validator.validate(organisationForm, bindingResult);
        BindingResult addressBindingResult = new BeanPropertyBindingResult(organisationForm.getAddressForm().getSelectedPostcode(), "selectedPostcode");
        organisationFormValidate(organisationForm, bindingResult, addressBindingResult);

        if(!bindingResult.hasFieldErrors("organisationName") && !bindingResult.hasFieldErrors("useSearchResultAddress") && !addressBindingResult.hasErrors()){
            CookieUtil.saveToCookie(response, ORGANISATION_FORM, JsonUtil.getSerializedObject(organisationForm));
            return "redirect:/organisation/create/confirm-organisation";
        }else{
            if(bindingResult.hasFieldErrors("organisationName")){
                log.warn("organisation name has errors");
            }else if(addressBindingResult.hasFieldErrors()){
                log.warn("addressBindingResult name has errors");
            }else{
                log.warn("?? has errors");
                bindingResult.getAllErrors().stream().forEach(e -> log.warn("Error: "+ e.getObjectName() +" => "+ e.getCode()));

            }
            log.warn("FOUND ERRORS");
            organisationForm.setTriedToSave(true);
            organisationForm.getAddressForm().setTriedToSave(true);
            CookieUtil.saveToCookie(response, ORGANISATION_FORM, JsonUtil.getSerializedObject(organisationForm));


            return getRedirectUrlInvalidSave(organisationForm, referer);
        }
    }

    private String getRedirectUrlInvalidSave(OrganisationCreationForm organisationForm, String referer) {
        String redirectPart;
        if(referer.contains("find-organisation")){
            redirectPart= "find-organisation";
            organisationForm.setSearchOrganisationId("");
        }else{
            redirectPart= "selected-organisation";
        }

        if(!referer.contains("find-organisation")){
            if(StringUtils.hasText(organisationForm.getSearchOrganisationId()) && organisationForm.getAddressForm().getSelectedPostcodeIndex() != null && StringUtils.hasText(organisationForm.getAddressForm().getPostcodeInput())){
                return String.format("redirect:/organisation/create/%s/%s/%s/%s", redirectPart, organisationForm.getSearchOrganisationId(), organisationForm.getAddressForm().getPostcodeInput(), organisationForm.getAddressForm().getSelectedPostcodeIndex());
            }else if(StringUtils.hasText(organisationForm.getSearchOrganisationId()) && StringUtils.hasText(organisationForm.getAddressForm().getPostcodeInput())){
                return String.format("redirect:/organisation/create/%s/%s/%s", redirectPart, organisationForm.getSearchOrganisationId(), organisationForm.getAddressForm().getPostcodeInput());
            }else if(StringUtils.hasText(organisationForm.getSearchOrganisationId())){
                return String.format("redirect:/organisation/create/%s/%s", redirectPart, organisationForm.getSearchOrganisationId());
            }else{
                return String.format("redirect:/organisation/create/%s", redirectPart);
            }
        }else{
            if(organisationForm.getAddressForm().getSelectedPostcodeIndex() != null && StringUtils.hasText(organisationForm.getAddressForm().getPostcodeInput())){
                return String.format("redirect:/organisation/create/%s/%s/%s", redirectPart, organisationForm.getAddressForm().getPostcodeInput(), organisationForm.getAddressForm().getSelectedPostcodeIndex());
            }else if(StringUtils.hasText(organisationForm.getAddressForm().getPostcodeInput())){
                return String.format("redirect:/organisation/create/%s/%s", redirectPart, organisationForm.getAddressForm().getPostcodeInput());
            }else{
                return String.format("redirect:/organisation/create/%s", redirectPart);
            }
        }

    }

    private void organisationFormValidate(@Valid @ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm, BindingResult bindingResult, BindingResult addressBindingResult) {
        if(organisationForm.isTriedToSave() && !organisationForm.isUseSearchResultAddress()){
            if(organisationForm.getAddressForm().getSelectedPostcode() != null){
                validator.validate(organisationForm.getAddressForm().getSelectedPostcode(), addressBindingResult);
            }else if(!organisationForm.getAddressForm().isManualAddress()){
                bindingResult.rejectValue("useSearchResultAddress", "NotEmpty", "You should either fill in your address, or use the registered address as your operating address.");
            }
        }
    }

    /**
     * Confirm the company details (user input, not from company-house)
     */
    @RequestMapping(value = "/confirm-organisation", method = RequestMethod.GET)
    public String confirmCompany(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                 Model model,
                                 HttpServletRequest request) throws IOException {
        organisationForm = getFormDataFromCookie(organisationForm, model, request);
        OrganisationTypeResource organisationType = addOrganisationType(organisationForm, request);
        addSelectedOrganisation(organisationForm, model);
        model.addAttribute("organisationForm", organisationForm);
        return "create-application/confirm-organisation";
    }


    @RequestMapping(value = "/find-business", method = RequestMethod.GET)
    public String createOrganisationBusiness(@ModelAttribute("companyHouseForm") CompanyHouseForm companyHouseForm, HttpServletResponse response) {
        OrganisationTypeForm organisationTypeForm = new OrganisationTypeForm();
        organisationTypeForm.setOrganisationType(OrganisationTypeEnum.BUSINESS.getOrganisationTypeId());
        String orgTypeForm = JsonUtil.getSerializedObject(organisationTypeForm);
        CookieUtil.saveToCookie(response, AcceptInviteController.ORGANISATION_TYPE, orgTypeForm);
        return "redirect:/organisation/create/find-organisation";
    }// Get Handler needed to allow browser history. Otherwise user would get message about posting information (when going back to previous page)


    @RequestMapping(value = "/find-business/not-in-company-house", method = RequestMethod.GET)
    public ModelAndView findBusinessNotInCompanyHouse(@ModelAttribute("companyHouseForm") CompanyHouseForm companyHouseForm,
                                                      BindingResult bindingResult) {
        ModelAndView mav = new ModelAndView("create-application/find-business");
        companyHouseForm.setInCompanyHouse(false);
        return mav;
    }

    /**
     * CompanyHouseForm saved in cookie, get data and display validation messages.
     */
    @RequestMapping(value = "/find-business/invalid-entry", method = RequestMethod.GET)
    public String findBusinessInvalidEntry(@ModelAttribute("companyHouseForm") CompanyHouseForm companyHouseForm,
                                           BindingResult bindingResult,
                                           Model model,
                                           HttpServletRequest request,
                                           HttpServletResponse response) throws IOException {

        String companyHouseFormJson = CookieUtil.getCookieValue(request, "companyHouseForm");
        companyHouseForm = JsonUtil.getObjectFromJson(companyHouseFormJson, CompanyHouseForm.class);
        companyHouseForm.bindingResult = bindingResult;
        companyHouseForm.objectErrors = bindingResult.getAllErrors();

        bindingResult = new BeanPropertyBindingResult(companyHouseForm, "companyHouseForm");
        validator.validate(companyHouseForm, bindingResult);
        model.addAttribute("companyHouseForm", companyHouseForm);
        model.addAttribute("org.springframework.validation.BindingResult.companyHouseForm", bindingResult);
        return "create-application/find-business";
    }

    @RequestMapping(value = "/find-business/postcode/", method = RequestMethod.GET)
    public ModelAndView findBusinessSearchPostcode(@ModelAttribute("companyHouseForm") CompanyHouseForm companyHouseForm,
                                                   BindingResult bindingResult) {
        ModelAndView mav = new ModelAndView("create-application/find-business");
        companyHouseForm.setPostcodeInput("");

        validator.validate(companyHouseForm, bindingResult);
        companyHouseForm.setInCompanyHouse(false);
        return mav;
    }

    @RequestMapping(value = "/find-business/postcode/{postcode}", method = RequestMethod.GET)
    public ModelAndView findBusinessSearchPostcode(@ModelAttribute("companyHouseForm") CompanyHouseForm companyHouseForm,
                                                   BindingResult bindingResult,
                                                   @PathVariable(POSTCODE) final String postcode) {
        ModelAndView mav = new ModelAndView("create-application/find-business");
        companyHouseForm.setPostcodeInput(postcode);

        validator.validate(companyHouseForm, bindingResult);
        companyHouseForm.setInCompanyHouse(false);
        searchPostcodes(companyHouseForm);

        return mav;
    }

    @RequestMapping(value = "/find-business/postcode/{postcode}/use-address/{postcodeIndex}", method = RequestMethod.GET)
    public ModelAndView findBusinessUseSelectedPostcode(@ModelAttribute("companyHouseForm") CompanyHouseForm companyHouseForm,
                                                        BindingResult bindingResult,
                                                        @PathVariable(POSTCODE) final String postcode,
                                                        @PathVariable("postcodeIndex") final String postcodeIndex) {
        companyHouseForm.setPostcodeInput(postcode);
        companyHouseForm.setSelectedPostcodeIndex(postcodeIndex);

        validator.validate(companyHouseForm, bindingResult);
        companyHouseForm.setInCompanyHouse(false);
        searchPostcodes(companyHouseForm);
        selectPostcodeAddress(companyHouseForm);
        ModelAndView mav = new ModelAndView("create-application/find-business");
        return mav;
    }

    @RequestMapping(value = "/find-business", method = RequestMethod.POST)
    public String createOrganisationBusinessPost(@Valid @ModelAttribute("companyHouseForm") CompanyHouseForm companyHouseForm,
                                                 BindingResult bindingResult,
                                                 HttpServletRequest request,
                                                 HttpServletResponse response) {

        if (request.getParameter(NOT_IN_COMPANY_HOUSE) != null) {
            return "redirect:/organisation/create/find-business/not-in-company-house";
        } else if (request.getParameter(MANUAL_ADDRESS) != null) {
            companyHouseForm.setInCompanyHouse(false);
            companyHouseForm.setManualAddress(true);
            companyHouseForm.setSelectedPostcodeIndex(null);
            companyHouseForm.setSelectedPostcode(new Address());
        } else if (request.getParameter(SEARCH_ADDRESS) != null) {
            return String.format("redirect:/organisation/create/find-business/postcode/%s", companyHouseForm.getPostcodeInput());
        } else if (request.getParameter(SELECT_ADDRESS) != null) {
            return String.format("redirect:/organisation/create/find-business/postcode/%s/use-address/%s", companyHouseForm.getPostcodeInput(), companyHouseForm.getSelectedPostcodeIndex());
        } else if (request.getParameter(SEARCH_COMPANY_HOUSE) != null) {
            return String.format("redirect:/organisation/create/find-business/search/%s", companyHouseForm.getCompanyHouseName());
        } else if (request.getParameter(CONFIRM_COMPANY_DETAILS) != null) {
            companyHouseForm.setInCompanyHouse(false);
            companyHouseForm.setManualAddress(true);
            BindingResult selectedPostcodeBindingResult =new BeanPropertyBindingResult(companyHouseForm.getSelectedPostcode(), "selectedPostcode");
            validator.validate(companyHouseForm.getSelectedPostcode(), selectedPostcodeBindingResult);

            if (!bindingResult.hasFieldErrors(ORGANISATION_NAME) && !selectedPostcodeBindingResult.hasFieldErrors()) {
                // save state into cookie.
                CookieUtil.saveToCookie(response, COMPANY_NAME, String.valueOf(companyHouseForm.getOrganisationName()));
                CookieUtil.saveToCookie(response, COMPANY_ADDRESS, JsonUtil.getSerializedObject(companyHouseForm.getSelectedPostcode()));
                return "redirect:/organisation/create/confirm-company";
            } else {
                // Prepare data for displaying validation messages after redirect.
                searchPostcodes(companyHouseForm);
                selectPostcodeAddress(companyHouseForm);
                companyHouseForm.setInCompanyHouse(false);
                companyHouseForm.setManualAddress(true);
                companyHouseForm.setTriedToSave(true);

                CookieUtil.saveToCookie(response, COMPANY_ADDRESS, JsonUtil.getSerializedObject(companyHouseForm.getSelectedPostcode()));
                CookieUtil.saveToCookie(response, "companyHouseForm", JsonUtil.getSerializedObject(companyHouseForm));
                return "redirect:/organisation/create/find-business/invalid-entry";
            }
        }

        return "create-application/find-business";
    }



    /**
     * Confirm the company details (user input, not from company-house)
     */
    @RequestMapping(value = "/confirm-company", method = RequestMethod.GET)
    public String confirmCompany(Model model,
                                 HttpServletRequest request) throws IOException {
        // Get data form cookie, convert json to Address object
        String jsonAddress = CookieUtil.getCookieValue(request, COMPANY_ADDRESS);
        Address address = JsonUtil.getObjectFromJson(jsonAddress, Address.class);

        // For displaying information only!
        OrganisationSearchResult org = new OrganisationSearchResult();
        org.setName(CookieUtil.getCookieValue(request, COMPANY_NAME));
        org.setOrganisationAddress(address);
        model.addAttribute("business", org);

        return "create-application/confirm-company";
    }

    @RequestMapping(value = "/selected-business/{companyId}", method = RequestMethod.GET)
    public String confirmBusiness(@ModelAttribute("confirmCompanyDetailsForm") ConfirmCompanyDetailsForm confirmCompanyDetailsForm,
                                  @ModelAttribute("organisationForm") OrganisationCreationForm organisationForm,
                                  BindingResult bindingResult,
                                  Model model,
                                  @PathVariable(COMPANY_ID) final String companyId,
                                  HttpServletResponse response, HttpServletRequest request) {
        addOrganisationType(organisationForm, request);
        CookieUtil.saveToCookie(response, COMPANY_HOUSE_COMPANY_ID, companyId);
        OrganisationSearchResult org = organisationService.getCompanyHouseOrganisation(companyId);
        organisationForm.setOrganisationName(org.getName());
        organisationForm.getAddressForm().setSelectedPostcode(org.getOrganisationAddress());
        model.addAttribute("business", org);
        return "create-application/confirm-selected-organisation";
    }

    @RequestMapping(value = "/selected-business/{companyId}/postcode/{postcode}", method = RequestMethod.GET)
    public ModelAndView findBusinessSubmitted(@ModelAttribute("confirmCompanyDetailsForm") ConfirmCompanyDetailsForm confirmCompanyDetailsForm,
                                              @ModelAttribute("organisationForm") OrganisationCreationForm organisationForm,
                                              @PathVariable(COMPANY_ID) final String companyId,
                                              @PathVariable(POSTCODE) final String postcode,
                                              HttpServletRequest request) {
        addOrganisationType(organisationForm, request);
        ModelAndView mav = new ModelAndView("create-application/confirm-selected-organisation");
        confirmCompanyDetailsForm.setPostcodeInput(postcode);

        OrganisationSearchResult org = organisationService.getCompanyHouseOrganisation(String.valueOf(companyId));
        mav.addObject("business", org);
        searchPostcodes(confirmCompanyDetailsForm);
        return mav;
    }

    @RequestMapping(value = "/selected-business/{companyId}/postcode/", method = RequestMethod.GET)
    public ModelAndView findBusinessSubmitted(@Valid @ModelAttribute("confirmCompanyDetailsForm") ConfirmCompanyDetailsForm confirmCompanyDetailsForm,
                                              @ModelAttribute("organisationForm") OrganisationCreationForm organisationForm,
                                              BindingResult bindingResult,
                                              @PathVariable(COMPANY_ID) final String companyId,
                                              HttpServletRequest request) {
        addOrganisationType(organisationForm, request);
        ModelAndView mav = new ModelAndView("create-application/confirm-selected-organisation");
        OrganisationSearchResult org = organisationService.getCompanyHouseOrganisation(String.valueOf(companyId));
        mav.addObject("business", org);

        confirmCompanyDetailsForm.setPostcodeInput("");
        return mav;
    }

    @RequestMapping(value = "/selected-business/{companyId}/postcode/{postcode}/use-address/{postcodeIndex}", method = RequestMethod.GET)
    public ModelAndView confirmBusinessSubmitted(@ModelAttribute("confirmCompanyDetailsForm") ConfirmCompanyDetailsForm confirmCompanyDetailsForm,
                                                 @ModelAttribute("organisationForm") OrganisationCreationForm organisationForm,
                                                 @PathVariable(COMPANY_ID) final String companyId,
                                                 @PathVariable(POSTCODE) final String postcode,
                                                 @PathVariable("postcodeIndex") final String postcodeIndex,
                                                 HttpServletRequest request) {
        addOrganisationType(organisationForm, request);
        ModelAndView mav = new ModelAndView("create-application/confirm-selected-organisation");
        confirmCompanyDetailsForm.setPostcodeInput(postcode);
        confirmCompanyDetailsForm.setSelectedPostcodeIndex(postcodeIndex);
        OrganisationSearchResult org = organisationService.getCompanyHouseOrganisation(String.valueOf(companyId));
        mav.addObject("business", org);

        searchPostcodes(confirmCompanyDetailsForm);
        selectPostcodeAddress(confirmCompanyDetailsForm);

        return mav;
    }

    @RequestMapping(value = "/selected-business/{companyId}", method = RequestMethod.POST)
    public String confirmBusinessSubmit(@Valid @ModelAttribute("confirmCompanyDetailsForm") ConfirmCompanyDetailsForm confirmCompanyDetailsForm,
                                        BindingResult bindingResult,
                                        Model model,
                                        @PathVariable(COMPANY_ID) final String companyId,
                                        HttpServletRequest request,
                                        HttpServletResponse response) {
        CookieUtil.saveToCookie(response, COMPANY_HOUSE_COMPANY_ID, String.valueOf(companyId));

        if (organisationService == null) {
            log.error("companyHouseService is null");
        }
        OrganisationSearchResult org = organisationService.getCompanyHouseOrganisation(String.valueOf(companyId));
        if (org == null) {
            log.error("org is null");
        }
        model.addAttribute("business", org);
        model.addAttribute(COMPANY_ID, companyId);
        if (StringUtils.hasText(confirmCompanyDetailsForm.getPostcodeInput())) {
            confirmCompanyDetailsForm.setPostcodeOptions(searchPostcode(confirmCompanyDetailsForm.getPostcodeInput()));
        }

        if (request.getParameter(MANUAL_ADDRESS) != null) {
            confirmCompanyDetailsForm.setManualAddress(true);
        } else if (request.getParameter(SEARCH_ADDRESS) != null) {
            return String.format("redirect:/organisation/create/selected-business/%s/postcode/%s", companyId, confirmCompanyDetailsForm.getPostcodeInput());
        } else if (request.getParameter(SELECT_ADDRESS) != null) {
            return String.format("redirect:/organisation/create/selected-business/%s/postcode/%s/use-address/%s", companyId, confirmCompanyDetailsForm.getPostcodeInput(), confirmCompanyDetailsForm.getSelectedPostcodeIndex());
        } else if (request.getParameter(SAVE_COMPANY_DETAILS) != null) {
            String name = org.getName();
            String companyHouseNumber = org.getOrganisationSearchId();
            // NOTE: Setting organisation size to null as this will eventually be moved to finance details section.
            OrganisationResource organisationResource = new OrganisationResource();
            organisationResource.setName(name);
            organisationResource.setCompanyHouseNumber(companyHouseNumber);
            organisationResource.setOrganisationType(OrganisationTypeEnum.BUSINESS.getOrganisationTypeId());

            organisationResource = saveNewOrganisation(organisationResource, request);
            if (!confirmCompanyDetailsForm.isUseCompanyHouseAddress()) {
                //Save address manually entered.
                organisationService.addAddress(organisationResource, confirmCompanyDetailsForm.getSelectedPostcode(), AddressType.OPERATING);
            }
            // Save address from company house api
            organisationService.addAddress(organisationResource, org.getOrganisationAddress(), AddressType.REGISTERED);

            return String.format("redirect:/registration/register?organisationId=%d", organisationResource.getId());
        }
        return "create-application/confirm-selected-organisation";
    }

    private OrganisationResource saveNewOrganisation(OrganisationResource organisationResource, HttpServletRequest request) {
        log.error("saveNewOrganisation");
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
                    log.error(String.format("found the invite.. %s", cookieHash));
                    return inviteOrganisationRestService.findOne(s.getInviteOrganisation()).handleSuccessOrFailure(
                            f -> {
                                log.info(String.format("Did not find the invite organisation.. %s", s.getInviteOrganisation()));
                                return restFailure(HttpStatus.NOT_FOUND);
                            },
                            i -> {
                                if (i.getOrganisation() == null) {
                                    i.setOrganisation(finalOrganisationResource.getId());
                                    // Save the created organisation Id, so the next invitee does not have to..
                                    log.debug("saving newly create organisation to inviteOrganisation");
                                    return inviteOrganisationRestService.put(i);
                                }
                                log.info("invite organisation is already connected to a organisation.");
                                return restFailure(HttpStatus.ALREADY_REPORTED);
                            }
                    );
                }
            );
        }
    }

    @RequestMapping("/save-organisation")
    public String saveOrganisation(@ModelAttribute("organisationForm") OrganisationCreationForm organisationForm, Model model, HttpServletRequest request) throws IOException {
        organisationForm = getFormDataFromCookie(organisationForm, model, request);
        OrganisationSearchResult selectedOrganisation = addSelectedOrganisation(organisationForm, model);
        Address address = organisationForm.getAddressForm().getSelectedPostcode();


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
        return "redirect:/registration/register?organisationId=" + organisationResource.getId();
    }

    /**
     * Save company from user input ( without company house ). The data should be in the cookies.
     */
    @RequestMapping("/save-company")
    public String saveCompany(HttpServletRequest request) throws IOException {
        // Get data form cookie, convert json to Address object

        String jsonAddress = CookieUtil.getCookieValue(request, COMPANY_ADDRESS);
        Address address = JsonUtil.getObjectFromJson(jsonAddress, Address.class);


        OrganisationResource organisationResource = new OrganisationResource();
        organisationResource.setName(CookieUtil
                .getCookieValue(request, COMPANY_NAME));

        organisationResource = saveNewOrganisation(organisationResource, request);
        if (address != null) {
            organisationService.addAddress(organisationResource, address, AddressType.OPERATING);
        }
        return "redirect:/registration/register?organisationId=" + organisationResource.getId();
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