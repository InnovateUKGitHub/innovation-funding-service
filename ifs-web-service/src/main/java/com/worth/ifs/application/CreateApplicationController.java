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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
 *
 * The user input is stored in cookies, so we can use the data after a page refresh / redirect.
 * For user-account creation, have a look at {@link com.worth.ifs.registration.RegistrationController}
 */
@Controller
@RequestMapping("/application/create")
public class CreateApplicationController extends AbstractApplicationController {
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

    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    Validator validator;

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
    public String initializeApplication(HttpServletRequest request) {
        Long competitionId = Long.valueOf(getFromCookie(request, COMPETITION_ID));
        Long userId = Long.valueOf(getFromCookie(request, USER_ID));

        ApplicationResource application = applicationService.createApplication(competitionId, userId, "");
        if (application == null || application.getId() == null) {
            log.error("Application not created with competitionID: " + competitionId);
            log.error("Application not created with userId: " + userId);
        }
        return ApplicationController.redirectToApplication(application);
    }

    @RequestMapping("/create-organisation-type")
    public String createAccountOrganisationType(@ModelAttribute Form form, Model model) {
        model.addAttribute("form", form);
        return "create-application/create-organisation-type";
    }

    @RequestMapping(value = "/find-business", method = RequestMethod.GET)
    public String createOrganisationBusiness(@ModelAttribute("companyHouseLookup") CompanyHouseForm companyHouseForm) {
        return "create-application/find-business";
    }

    @RequestMapping(value = "/find-business", method = RequestMethod.POST)
    public String createOrganisationBusinessPost(@ModelAttribute("companyHouseLookup") CompanyHouseForm companyHouseForm,
                                                 BindingResult bindingResult,
                                                 Model model,
                                                 HttpServletRequest request,
                                                 HttpServletResponse response) {
        if (request.getParameter(NOT_IN_COMPANY_HOUSE) != null) {
            companyHouseForm.setInCompanyHouse(false);
        } else if (request.getParameter(MANUAL_ADDRESS) != null) {
            companyHouseForm.setInCompanyHouse(false);
            companyHouseForm.setManualAddress(true);
            companyHouseForm.setSelectedPostcodeIndex(null);
            companyHouseForm.setSelectedPostcode(new Address());
        } else if (request.getParameter(SEARCH_ADDRESS) != null) {
            validator.validate(companyHouseForm, bindingResult);
            companyHouseForm.setInCompanyHouse(false);
            searchPostcodes(companyHouseForm);
        } else if (request.getParameter(SELECT_ADDRESS) != null) {
            companyHouseForm.setInCompanyHouse(false);
            searchPostcodes(companyHouseForm);
            selectPostcodeAddress(companyHouseForm);
        } else if (request.getParameter(SEARCH_COMPANY_HOUSE) != null) {
            companyHouseForm.setCompanyHouseSearching(true);
            validator.validate(companyHouseForm, bindingResult);
            if (!bindingResult.hasFieldErrors(COMPANY_HOUSE_NAME)) {
                searchCompanyHouse(companyHouseForm);
            }else{
                bindingResult.getFieldErrors().forEach(e -> log.info("Validation error: " + e.getField() + "__" + e.getDefaultMessage()));
            }
        } else if (request.getParameter(CONFIRM_COMPANY_DETAILS) != null) {
            companyHouseForm.setInCompanyHouse(false);
            companyHouseForm.setManualAddress(true);
            validator.validate(companyHouseForm, bindingResult);
            if (!bindingResult.hasFieldErrors(ORGANISATION_NAME) && !bindingResult.hasFieldErrors(ORGANISATION_SIZE1)) {
                // save state into cookie.
                ObjectMapper mapper = new ObjectMapper();
                String jsonAddress = "";
                try {
                    jsonAddress = mapper.writeValueAsString(companyHouseForm.getSelectedPostcode());
                } catch (JsonProcessingException e) {
                    log.error(e);
                }

                saveToCookie(response, COMPANY_NAME, String.valueOf(companyHouseForm.getOrganisationName()));
                saveToCookie(response, COMPANY_ADDRESS, jsonAddress);
                saveToCookie(response, ORGANISATION_SIZE, companyHouseForm.getOrganisationSize().name());

                return "redirect:/application/create/confirm-company";
            } else {
                companyHouseForm.setTriedToSave(true);
                bindingResult.getFieldErrors().forEach(e -> log.info("Validation error: " + e.getField() + "__" + e.getDefaultMessage()));
            }
        }

            //

        return "create-application/find-business";

    }

    /**
     * Confirm the company details (user input, not from company-house)
     */
    @RequestMapping(value = "/confirm-company", method = RequestMethod.GET)
    public String confirmCompany(Model model,
                                 HttpServletRequest request) throws IOException {
        // Get data form cookie, convert json to Address object
        ObjectMapper mapper = new ObjectMapper();
        String jsonAddress = getFromCookie(request, COMPANY_ADDRESS);
        Address address = mapper.readValue(jsonAddress, Address.class);

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
        model.addAttribute("business", org);
        if (StringUtils.hasText(confirmCompanyDetailsForm.getPostcodeInput())) {
            confirmCompanyDetailsForm.setPostcodeOptions(searchPostcode(confirmCompanyDetailsForm.getPostcodeInput()));
        }

        if (request.getParameter(MANUAL_ADDRESS) != null) {
            confirmCompanyDetailsForm.setManualAddress(true);
        } else if (request.getParameter(SEARCH_ADDRESS) != null) {
            searchPostcodes(confirmCompanyDetailsForm);
        } else if (request.getParameter(SELECT_ADDRESS) != null) {
            searchPostcodes(confirmCompanyDetailsForm);
            selectPostcodeAddress(confirmCompanyDetailsForm);
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

                return "redirect:/registration/register?organisationId=" + organisationResource.getId();
            }else{
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
        ObjectMapper mapper = new ObjectMapper();
        String jsonAddress = getFromCookie(request, COMPANY_ADDRESS);
        Address address = mapper.readValue(jsonAddress, Address.class);

        Organisation organisation = new Organisation(null, getFromCookie(request, COMPANY_NAME), null, OrganisationSize.valueOf(getFromCookie(request, ORGANISATION_SIZE)));

        OrganisationResource organisationResource = organisationService.save(organisation);
        if(address != null){
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

    private String getFromCookie(HttpServletRequest request, String fieldName) {
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(fieldName)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public static void saveToCookie(HttpServletResponse response, String fieldName, String fieldValue) {
        if (fieldName != null) {
            Cookie cookie = new Cookie(fieldName, fieldValue);
            cookie.setPath("/");
            cookie.setMaxAge(3600);
            response.addCookie(cookie);
        }
    }

    private void searchPostcodes(CreateApplicationForm form) {
        if (StringUtils.hasText(form.getPostcodeInput())) {
            form.setPostcodeOptions(searchPostcode(form.getPostcodeInput()));
        }
    }

    private void searchCompanyHouse(CompanyHouseForm form) {
        if (StringUtils.hasText(form.getCompanyHouseName())) {
            List<CompanyHouseBusiness> companies = searchCompanyHouse(form.getCompanyHouseName());
            form.setCompanyHouseList(companies);
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
