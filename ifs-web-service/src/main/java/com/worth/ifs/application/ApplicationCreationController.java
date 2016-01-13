package com.worth.ifs.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.application.form.CompanyHouseForm;
import com.worth.ifs.application.form.ConfirmCompanyDetailsForm;
import com.worth.ifs.application.form.CreateApplicationForm;
import com.worth.ifs.application.form.Form;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.login.LoginForm;
import com.worth.ifs.organisation.domain.Address;
import com.worth.ifs.organisation.resource.CompanyHouseBusiness;
import com.worth.ifs.user.domain.AddressType;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.OrganisationSize;
import com.worth.ifs.user.resource.OrganisationResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This controller will handle all requests that are related to the create of a application.
 * This is used when the users want create a new application and that also includes the creation of the organisation.
 * These URLs are publicly available, since there user might not have a account yet.
 * <p>
 * The user input is stored in cookies, so we can use the data after a page refresh / redirect.
 * For user-account creation, have a look at {@link com.worth.ifs.registration.RegistrationController}
 */
@Controller
@RequestMapping("/application/create")
public class ApplicationCreationController extends AbstractApplicationController {
    public static final String COMPETITION_ID = "competitionId";
    public static final String USER_ID = "userId";
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
    public static final String ORGANISATION_SIZE = "organisation_size";
    public static final String ORGANISATION_NAME = "organisationName";
    public static final String ORGANISATION_SIZE1 = "organisationSize";
    public static final String COMPANY_HOUSE_NAME = "companyHouseName";
    private static final String POSTCODE = "postcode";
    private static final String APPLICATION_ID = "applicationId";
    @Value("${server.session.cookie.secure}")
    private static boolean cookieSecure;
    @Value("${server.session.cookie.http-only}")
    private static boolean cookieHttpOnly;
    private final Log log = LogFactory.getLog(getClass());
    Validator validator;

    public static void saveToCookie(HttpServletResponse response, String fieldName, String fieldValue) {
        if (fieldName != null) {
            Cookie cookie = new Cookie(fieldName, fieldValue);
            cookie.setSecure(cookieSecure);
            cookie.setHttpOnly(cookieHttpOnly);
            cookie.setPath("/");
            cookie.setMaxAge(3600);
            response.addCookie(cookie);
        }
    }

    public static String getSerializedObject(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        String json = "";
        try {
            json = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static <T> T getObjectFromJson(String json, Class<T> type) {
        ObjectMapper mapper = new ObjectMapper();
        T obj = null;
        try {
            obj = mapper.readValue(json, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static String getFromCookie(HttpServletRequest request, String fieldName) {
        if(request != null && request.getCookies() != null){
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(fieldName)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    @Autowired
    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    @RequestMapping("/check-eligibility/{competitionId}")
    public String checkEligibility(Model model,
                                   @PathVariable(COMPETITION_ID) Long competitionId,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
        model.addAttribute("loginForm", new LoginForm());
        model.addAttribute(COMPETITION_ID, competitionId);

        saveToCookie(response, COMPETITION_ID, String.valueOf(competitionId));
        return "create-application/check-eligibility";
    }

    @RequestMapping("/initialize-application")
    public String initializeApplication(HttpServletRequest request,
                                        HttpServletResponse response) {
        Long competitionId = Long.valueOf(getFromCookie(request, COMPETITION_ID));
        Long userId = Long.valueOf(getFromCookie(request, USER_ID));

        ApplicationResource application = applicationService.createApplication(competitionId, userId, "");
        if (application == null || application.getId() == null) {
            log.error("Application not created with competitionID: " + competitionId);
            log.error("Application not created with userId: " + userId);
        } else {
            saveToCookie(response, APPLICATION_ID, String.valueOf(application.getId()));
            return String.format("redirect:/application/%s/contributors/invite", String.valueOf(application.getId()));
            //return ApplicationController.redirectToApplication(application);
        }
        return null;
    }

    @RequestMapping("/create-organisation-type")
    public String createAccountOrganisationType(@ModelAttribute Form form, Model model) {
        model.addAttribute("form", form);
        return "create-application/create-organisation-type";
    }

    @RequestMapping(value = "/find-business", method = RequestMethod.GET)
    public String createOrganisationBusiness(@ModelAttribute("companyHouseForm") CompanyHouseForm companyHouseForm) {
        return "create-application/find-business";
    }

    // Get Handler needed to allow browser history. Otherwise user would get message about posting information (when going back to previous page)
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
        String companyHouseFormJson = getFromCookie(request, "companyHouseForm");
        companyHouseForm = getObjectFromJson(companyHouseFormJson, CompanyHouseForm.class);
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
            return String.format("redirect:/application/create/find-business/not-in-company-house");
        } else if (request.getParameter(MANUAL_ADDRESS) != null) {
            companyHouseForm.setInCompanyHouse(false);
            companyHouseForm.setManualAddress(true);
            companyHouseForm.setSelectedPostcodeIndex(null);
            companyHouseForm.setSelectedPostcode(new Address());
        } else if (request.getParameter(SEARCH_ADDRESS) != null) {
            return String.format("redirect:/application/create/find-business/postcode/%s", companyHouseForm.getPostcodeInput());
        } else if (request.getParameter(SELECT_ADDRESS) != null) {
            return String.format("redirect:/application/create/find-business/postcode/%s/use-address/%s", companyHouseForm.getPostcodeInput(), companyHouseForm.getSelectedPostcodeIndex());
        } else if (request.getParameter(SEARCH_COMPANY_HOUSE) != null) {
            return String.format("redirect:/application/create/find-business/search/%s", companyHouseForm.getCompanyHouseName());
        } else if (request.getParameter(CONFIRM_COMPANY_DETAILS) != null) {
            companyHouseForm.setInCompanyHouse(false);
            companyHouseForm.setManualAddress(true);
            bindingResult.getFieldErrors().stream().forEach(e -> log.debug(e.getDefaultMessage()));

            if (!bindingResult.hasFieldErrors(ORGANISATION_NAME) && !bindingResult.hasFieldErrors(ORGANISATION_SIZE1)) {
                // save state into cookie.
                saveToCookie(response, COMPANY_NAME, String.valueOf(companyHouseForm.getOrganisationName()));
                saveToCookie(response, COMPANY_ADDRESS, getSerializedObject(companyHouseForm.getSelectedPostcode()));
                saveToCookie(response, ORGANISATION_SIZE, companyHouseForm.getOrganisationSize().name());

                return "redirect:/application/create/confirm-company";
            } else {
                // Prepare data for displaying validation messages after redirect.
                searchPostcodes(companyHouseForm);
                selectPostcodeAddress(companyHouseForm);
                companyHouseForm.setInCompanyHouse(false);
                companyHouseForm.setManualAddress(true);
                companyHouseForm.setTriedToSave(true);

                saveToCookie(response, "companyHouseForm", getSerializedObject(companyHouseForm));
                return "redirect:/application/create/find-business/invalid-entry";
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
        String jsonAddress = getFromCookie(request, COMPANY_ADDRESS);
        Address address = getObjectFromJson(jsonAddress, Address.class);

        // For displaying information only!
        CompanyHouseBusiness org = new CompanyHouseBusiness();
        org.setName(getFromCookie(request, COMPANY_NAME));
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
        saveToCookie(response, COMPANY_HOUSE_COMPANY_ID, String.valueOf(companyId));

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
        saveToCookie(response, COMPANY_HOUSE_COMPANY_ID, String.valueOf(companyId));

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
            return String.format("redirect:/application/create/selected-business/%s/postcode/%s", companyId, confirmCompanyDetailsForm.getPostcodeInput());
        } else if (request.getParameter(SELECT_ADDRESS) != null) {
            return String.format("redirect:/application/create/selected-business/%s/postcode/%s/use-address/%s", companyId, confirmCompanyDetailsForm.getPostcodeInput(), confirmCompanyDetailsForm.getSelectedPostcodeIndex());
        } else if (request.getParameter(SAVE_COMPANY_DETAILS) != null) {
            if (!bindingResult.hasFieldErrors(ORGANISATION_SIZE1)) {
                String name = org.getName();
                String companyHouseNumber = org.getCompanyNumber();
                Organisation organisation = new Organisation(null, name, companyHouseNumber, confirmCompanyDetailsForm.getOrganisationSize());

                OrganisationResource organisationResource = organisationService.save(organisation);
                if (!confirmCompanyDetailsForm.isUseCompanyHouseAddress()) {
                    //Save address manually entered.
                    organisationService.addAddress(organisationResource, confirmCompanyDetailsForm.getSelectedPostcode(), AddressType.OPERATING);
                }
                // Save address from company house api
                organisationService.addAddress(organisationResource, org.getOfficeAddress(), AddressType.REGISTERED);

                return String.format("redirect:/registration/register?organisationId=%d", organisationResource.getId());
            } else {
                log.warn("Could not save, validation message organisation size.");
                confirmCompanyDetailsForm.setTriedToSave(true);
            }
        }
        return "create-application/confirm-selected-organisation";
    }

    /**
     * Save company from user input ( without company house ). The data should be in the cookies.
     */
    @RequestMapping("/save-company")
    public String saveCompany(HttpServletRequest request) throws IOException {
        // Get data form cookie, convert json to Address object
        String jsonAddress = getFromCookie(request, COMPANY_ADDRESS);
        Address address = getObjectFromJson(jsonAddress, Address.class);


        Organisation organisation = new Organisation(null, getFromCookie(request, COMPANY_NAME), null, OrganisationSize.valueOf(getFromCookie(request, ORGANISATION_SIZE)));

        OrganisationResource organisationResource = organisationService.save(organisation);
        if (address != null) {
            organisationService.addAddress(organisationResource, address, AddressType.OPERATING);
        }
        return "redirect:/registration/register?organisationId=" + organisationResource.getId();
    }

    @RequestMapping("/your-details")
    public String checkEligibility() {
        return "create-application/your-details";
    }

    private List<Address> searchPostcode(String postcodeInput) {
        List<Address> addresses = new ArrayList<>();
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

    private List<CompanyHouseBusiness> searchCompanyHouse(String organisationName) {
        return organisationService.searchCompanyHouseOrganisations(organisationName);
    }

    private void searchPostcodes(CreateApplicationForm form) {
        if (StringUtils.hasText(form.getPostcodeInput())) {
            form.setPostcodeOptions(searchPostcode(form.getPostcodeInput()));
        }
    }

    private void selectPostcodeAddress(CreateApplicationForm form) {
        if (form.getPostcodeOptions() != null && form.getPostcodeOptions().size() != 0) {
            int indexInt = Integer.parseInt(form.getSelectedPostcodeIndex());
            if (form.getPostcodeOptions().get(indexInt) != null) {
                form.setSelectedPostcode(form.getPostcodeOptions().get(indexInt));
            }
        }
    }
}
