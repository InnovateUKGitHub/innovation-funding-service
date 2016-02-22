package com.worth.ifs.organisation;

import com.worth.ifs.application.AcceptInviteController;
import com.worth.ifs.application.form.CompanyHouseForm;
import com.worth.ifs.application.form.ConfirmCompanyDetailsForm;
import com.worth.ifs.application.form.CreateApplicationForm;
import com.worth.ifs.application.form.Form;
import com.worth.ifs.application.service.OrganisationService;
import com.worth.ifs.invite.service.InviteOrganisationRestService;
import com.worth.ifs.invite.service.InviteRestService;
import com.worth.ifs.organisation.domain.Address;
import com.worth.ifs.organisation.resource.CompanyHouseBusiness;
import com.worth.ifs.user.domain.AddressType;
import com.worth.ifs.user.domain.OrganisationTypeEnum;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.util.CookieUtil;
import com.worth.ifs.util.JsonUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/organisation/create")
public class OrganisationCreationController {
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

    @RequestMapping(value = "/find-business", method = RequestMethod.GET)
    public String createOrganisationBusiness(@ModelAttribute("companyHouseForm") CompanyHouseForm companyHouseForm) {
        return "create-application/find-business";
    }// Get Handler needed to allow browser history. Otherwise user would get message about posting information (when going back to previous page)

    @RequestMapping(value = "/find-business/search/{companyHouseName}", method = RequestMethod.GET)
    public ModelAndView createOrganisationBusinessPost(@ModelAttribute("companyHouseForm") CompanyHouseForm companyHouseForm,
                                                       @PathVariable("companyHouseName") String companyHouseName,
                                                       BindingResult bindingResult,
                                                       HttpServletRequest request,
                                                       RedirectAttributes redirectAttributes) {
        validator.validate(companyHouseForm, bindingResult);
        companyHouseForm.setCompanyHouseSearching(true);
        companyHouseForm.setCompanyHouseList(searchCompanyHouse(companyHouseName));
        return new ModelAndView("create-application/find-business");
    }

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
            bindingResult.getFieldErrors().stream().forEach(e -> log.debug(e.getDefaultMessage()));

            if (!bindingResult.hasFieldErrors(ORGANISATION_NAME)) {
                // save state into cookie.
                CookieUtil.saveToCookie(response, COMPANY_NAME, String.valueOf(companyHouseForm.getOrganisationName()));
                CookieUtil.saveToCookie(response, COMPANY_ADDRESS, String.valueOf(companyHouseForm.getSelectedPostcode()));
                return "redirect:/organisation/create/confirm-company";
            } else {
                // Prepare data for displaying validation messages after redirect.
                searchPostcodes(companyHouseForm);
                selectPostcodeAddress(companyHouseForm);
                companyHouseForm.setInCompanyHouse(false);
                companyHouseForm.setManualAddress(true);
                companyHouseForm.setTriedToSave(true);

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
        CompanyHouseBusiness org = new CompanyHouseBusiness();
        org.setName(CookieUtil.getCookieValue(request, COMPANY_NAME));
        org.setOfficeAddress(address);
        model.addAttribute("business", org);

        return "create-application/confirm-company";
    }

    @RequestMapping(value = "/selected-business/{companyId}", method = RequestMethod.GET)
    public String confirmBusiness(@ModelAttribute("confirmCompanyDetailsForm") ConfirmCompanyDetailsForm confirmCompanyDetailsForm,
                                  BindingResult bindingResult,
                                  Model model,
                                  @PathVariable(COMPANY_ID) final String companyId,
                                  HttpServletResponse response) {
        CookieUtil.saveToCookie(response, COMPANY_HOUSE_COMPANY_ID, String.valueOf(companyId));
        CompanyHouseBusiness org = organisationService.getCompanyHouseOrganisation(String.valueOf(companyId));
        model.addAttribute("business", org);
        return "create-application/confirm-selected-organisation";
    }

    @RequestMapping(value = "/selected-business/{companyId}/postcode/{postcode}", method = RequestMethod.GET)
    public ModelAndView findBusinessSubmitted(@ModelAttribute("confirmCompanyDetailsForm") ConfirmCompanyDetailsForm confirmCompanyDetailsForm,
                                              @PathVariable(COMPANY_ID) final String companyId,
                                              @PathVariable(POSTCODE) final String postcode) {
        ModelAndView mav = new ModelAndView("create-application/confirm-selected-organisation");
        confirmCompanyDetailsForm.setPostcodeInput(postcode);

        CompanyHouseBusiness org = organisationService.getCompanyHouseOrganisation(String.valueOf(companyId));
        mav.addObject("business", org);
        searchPostcodes(confirmCompanyDetailsForm);
        return mav;
    }

    @RequestMapping(value = "/selected-business/{companyId}/postcode/", method = RequestMethod.GET)
    public ModelAndView findBusinessSubmitted(@Valid @ModelAttribute("confirmCompanyDetailsForm") ConfirmCompanyDetailsForm confirmCompanyDetailsForm,
                                              BindingResult bindingResult,
                                              @PathVariable(COMPANY_ID) final String companyId) {
        ModelAndView mav = new ModelAndView("create-application/confirm-selected-organisation");
        CompanyHouseBusiness org = organisationService.getCompanyHouseOrganisation(String.valueOf(companyId));
        mav.addObject("business", org);

        confirmCompanyDetailsForm.setPostcodeInput("");
        return mav;
    }

    @RequestMapping(value = "/selected-business/{companyId}/postcode/{postcode}/use-address/{postcodeIndex}", method = RequestMethod.GET)
    public ModelAndView confirmBusinessSubmitted(@ModelAttribute("confirmCompanyDetailsForm") ConfirmCompanyDetailsForm confirmCompanyDetailsForm,
                                                 @PathVariable(COMPANY_ID) final String companyId,
                                                 @PathVariable(POSTCODE) final String postcode,
                                                 @PathVariable("postcodeIndex") final String postcodeIndex) {
        ModelAndView mav = new ModelAndView("create-application/confirm-selected-organisation");
        confirmCompanyDetailsForm.setPostcodeInput(postcode);
        confirmCompanyDetailsForm.setSelectedPostcodeIndex(postcodeIndex);
        CompanyHouseBusiness org = organisationService.getCompanyHouseOrganisation(String.valueOf(companyId));
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
        CompanyHouseBusiness org = organisationService.getCompanyHouseOrganisation(String.valueOf(companyId));
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
            String companyHouseNumber = org.getCompanyNumber();
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
            organisationService.addAddress(organisationResource, org.getOfficeAddress(), AddressType.REGISTERED);

            return String.format("redirect:/registration/register?organisationId=%d", organisationResource.getId());
        }
        return "create-application/confirm-selected-organisation";
    }

    private OrganisationResource saveNewOrganisation(OrganisationResource organisationResource, HttpServletRequest request) {
        log.error("saveNewOrganisation");
        organisationResource = organisationService.save(organisationResource);

        String cookieHash = CookieUtil.getCookieValue(request, AcceptInviteController.INVITE_HASH);
        if(StringUtils.hasText(cookieHash)){
            final OrganisationResource finalOrganisationResource = organisationResource;
            inviteRestService.getInviteByHash(cookieHash).handleSuccessOrFailure(
                f -> {
                    log.error(String.format("Did not find the invite.. %s", cookieHash));
                    return false;
                },
                s -> {
                    log.error(String.format("found the invite.. %s", cookieHash));
                    return inviteOrganisationRestService.findOne(s.getInviteOrganisation()).handleSuccessOrFailure(
                        f -> {
                            log.error(String.format("Did not find the invite organisation.. %s", s.getInviteOrganisation()));
                            return false;
                        },
                        i -> {
                            if (i.getOrganisation() == null) {
                                i.setOrganisation(finalOrganisationResource.getId());
                                // Save the created organisation Id, so the next invitee does not have to..
                                log.error("saving newly create organisation to inviteOrganisation");
                                inviteOrganisationRestService.put(i);
                            }else{
                                log.error("invite organisation is already connected to a organisation.");
                            }
                            return true;
                        }
                    );
                }
            );
        }

        return organisationResource;
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

    public List<CompanyHouseBusiness> searchCompanyHouse(String organisationName) {
        return organisationService.searchCompanyHouseOrganisations(organisationName);
    }

    public void searchPostcodes(CreateApplicationForm form) {
        if (StringUtils.hasText(form.getPostcodeInput())) {
            form.setPostcodeOptions(searchPostcode(form.getPostcodeInput()));
        }
    }

    public void selectPostcodeAddress(CreateApplicationForm form) {
        if (form.getPostcodeOptions() != null && form.getPostcodeOptions().size() != 0) {
            int indexInt = Integer.parseInt(form.getSelectedPostcodeIndex());
            if (form.getPostcodeOptions().get(indexInt) != null) {
                form.setSelectedPostcode(form.getPostcodeOptions().get(indexInt));
            }
        }
    }
}