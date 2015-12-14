package com.worth.ifs.application;

import com.worth.ifs.application.service.OrganisationService;
import com.worth.ifs.login.LoginForm;
import com.worth.ifs.organisation.domain.Address;
import com.worth.ifs.organisation.resource.CompanyHouseBusiness;
import com.worth.ifs.user.domain.AddressType;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.resource.OrganisationResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * This controller will handle all requests that are related to the create of a application.
 * This is used when the users want create a new application and that also includes the creation of the organisation.
 * These URLs are publicly available, since there user might not have a account jet.
 */
@Controller
@RequestMapping("/application/create")
public class CreateApplicationController extends AbstractApplicationController {
    public final static String COMPETITION_ID = "competitionId";
    public final static String COMPANY_HOUSE_COMPANY_ID = "companyId";
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    OrganisationService organisationService;


    @RequestMapping("/check-eligibility/{competitionId}")
    public String checkEligibility(Form form, Model model,
                                   @PathVariable(COMPETITION_ID) Long competitionId,
                                   HttpServletRequest request,
                                   HttpServletResponse response){
        model.addAttribute("loginForm", new LoginForm());
        model.addAttribute(COMPETITION_ID, competitionId);

        this.saveToCookie(request, response, COMPETITION_ID, String.valueOf(competitionId));
        return "create-application/check-eligibility";
    }

    @RequestMapping("/create-organisation-type")
    public String createAccountOrganisationType(@ModelAttribute Form form, Model model, HttpServletRequest request) {
        model.addAttribute("form", form);
        return "create-application/create-organisation-type";
    }

    @RequestMapping(value="/find-business", method = RequestMethod.POST)
    public String createOrganisationBusinessPost(@Valid @ModelAttribute("companyHouseLookup") CompanyHouseForm companyHouseForm,
                                             BindingResult bindingResult,
                                             Model model,
                                             HttpServletRequest request,
                                             HttpServletResponse response ) {
        this.logState(request, response);

        if(!bindingResult.hasErrors()){
            List<CompanyHouseBusiness> companies = searchCompanyHouse(companyHouseForm.getOrganisationName());
            companyHouseForm.setCompanyHouseList(companies);
            return "create-application/find-business";
        }else{
            return "create-application/find-business";
        }
    }

    @RequestMapping(value="/find-business", method = RequestMethod.GET)
    public String createOrganisationBusiness(@ModelAttribute("companyHouseLookup") CompanyHouseForm companyHouseForm,
                                             BindingResult bindingResult,
                                             Model model,
                                             HttpServletRequest request,
                                             HttpServletResponse response ) {
        this.logState(request, response);
        return "create-application/find-business";
    }


    @RequestMapping(value="/selected-business/{companyId}", method = RequestMethod.GET)
    public String confirmBusiness(@ModelAttribute("confirmCompanyDetailsForm") ConfirmCompanyDetailsForm confirmCompanyDetailsForm,
                                   BindingResult bindingResult,
                                   Model model,
                                   @PathVariable("companyId") final String companyId,
                                   HttpServletRequest request,
                                   HttpServletResponse response ) {
        this.saveToCookie(request, response, COMPANY_HOUSE_COMPANY_ID, String.valueOf(companyId));
        this.logState(request, response);

        if(organisationService == null){
            log.debug("companyHouseService is null");
        }

        CompanyHouseBusiness org = organisationService.getCompanyHouseOrganisation(String.valueOf(companyId));
        model.addAttribute("business", org);
        return "create-application/confirm-selected-organisation";
    }
    @RequestMapping(value="/selected-business/{companyId}", method = RequestMethod.POST)
    public String confirmBusinessSubmit(@Valid @ModelAttribute("confirmCompanyDetailsForm") ConfirmCompanyDetailsForm confirmCompanyDetailsForm,
                                   BindingResult bindingResult,
                                   Model model,
                                   @PathVariable("companyId") final String companyId,
                                   HttpServletRequest request,
                                   HttpServletResponse response ) {
        this.saveToCookie(request, response, COMPANY_HOUSE_COMPANY_ID, String.valueOf(companyId));
        this.logState(request, response);

        if(organisationService == null){
            log.debug("companyHouseService is null");
        }
        CompanyHouseBusiness org = organisationService.getCompanyHouseOrganisation(String.valueOf(companyId));
        model.addAttribute("business", org);

        if(!bindingResult.hasErrors()){
            log.info("do postcodelookup : " + confirmCompanyDetailsForm.getPostcodeInput());
            confirmCompanyDetailsForm.setPostcodeOptions(this.searchPostcode(confirmCompanyDetailsForm.getPostcodeInput()));

            if(request.getParameter("select-address") != null){
                if(confirmCompanyDetailsForm.getPostcodeOptions() != null && confirmCompanyDetailsForm.getPostcodeOptions().size() != 0){
                    int indexInt = Integer.parseInt(confirmCompanyDetailsForm.getSelectedPostcodeIndex());
                    if(confirmCompanyDetailsForm.getPostcodeOptions().get(indexInt) != null){
                        confirmCompanyDetailsForm.setSelectedPostcode(confirmCompanyDetailsForm.getPostcodeOptions().get(indexInt));
                    }
                }
            }else if(request.getParameter("save-company-details") != null){
                log.info("Save company details ");
                log.info("c " +confirmCompanyDetailsForm.getOrganisationSize());

                String name = org.getName();
                String companyHouseNumber = org.getCompanyNumber();
                Organisation organisation = new Organisation(null, name, companyHouseNumber, confirmCompanyDetailsForm.getOrganisationSize());

                OrganisationResource organisationResource = organisationService.save(organisation);
                log.info("Organisation CREATED");
                if(confirmCompanyDetailsForm.isUseCompanyHouseAddress()){
                    organisationResource = organisationService.addAddress(organisationResource, org.getOfficeAddress(), AddressType.REGISTERED);
                }else{
                    organisationResource = organisationService.addAddress(organisationResource, org.getOfficeAddress(), AddressType.REGISTERED);
                    organisationResource = organisationService.addAddress(organisationResource, confirmCompanyDetailsForm.getSelectedPostcode(), AddressType.OPERATING);
                }
            }
        }else{
            log.info("has errors do postcodelookup : " + confirmCompanyDetailsForm.getPostcodeInput());
        }


        return "create-application/confirm-selected-organisation";
    }

    @RequestMapping("/your-details")
    public String checkEligibility(Form form, Model model,
                                   HttpServletRequest request,
                                   HttpServletResponse response){

        return "create-application/your-details";
    }

    private List<Address> searchPostcode(String postcodeInput) {
        List<Address> addresses = new ArrayList<>();
        addresses.add(new Address(
                "Montrose House 1",
                "Clayhill Park",
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

    private void saveToCookie(HttpServletRequest request, HttpServletResponse response, String fieldName, String fieldValue){
        if(fieldName != null){
            response.addCookie(new Cookie(fieldName, fieldValue));
        }
    }

    private String getFromCookie(HttpServletRequest request, String fieldName){
        for (Cookie cookie : request.getCookies()) {
            if(cookie.getName().equals(fieldName)){
                return cookie.getValue();
            }
        }
        return null;
    }

    private void logState(HttpServletRequest request, HttpServletResponse response){
        log.debug("=== Logging cookie state === ");
        if(request.getCookies()  == null || request.getCookies().length == 0 ){
            return ;
        }
        for (Cookie cookie : request.getCookies()) {
            log.debug("COOKIE name: " + cookie.getName());
            log.debug("COOKIE value: " + cookie.getValue());
        }
        log.debug("=== ==================== === ");
    }
}
