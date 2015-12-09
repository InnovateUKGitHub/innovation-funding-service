package com.worth.ifs.application;

import com.worth.ifs.application.service.OrganisationService;
import com.worth.ifs.login.LoginForm;
import com.worth.ifs.organisation.resource.CompanyHouseBusiness;
import com.worth.ifs.user.domain.User;
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
import java.util.List;

/**
 * This controller will handle all requests that are related to the application overview.
 * Application overview is the page that contains the most basic information about the current application and
 * the basic information about the competition the application is related to.
 */
@Controller
@RequestMapping("/application/create")
public class CreateApplicationController extends AbstractApplicationController {
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    OrganisationService organisationService;


    @RequestMapping("/check-eligibility/{competitionId}")
    public String checkEligibility(Form form, Model model,
                                   @PathVariable(CreateApplicationConstants.COMPETITION_ID) Long competitionId,
                                   HttpServletRequest request,
                                   HttpServletResponse response){

        User user = userAuthenticationService.getAuthenticatedUser(request);
        model.addAttribute("loginForm", new LoginForm());
        model.addAttribute(CreateApplicationConstants.COMPETITION_ID, competitionId);

        this.saveToCookie(request, response, CreateApplicationConstants.COMPETITION_ID, String.valueOf(competitionId));

        return "create-application/check-eligibility";
    }




    @RequestMapping("/create-organisation-type")
    public String createAccountOrganisationType(@ModelAttribute Form form, Model model, HttpServletRequest request) {
        User user = userAuthenticationService.getAuthenticatedUser(request);

        model.addAttribute("form", form);
        if(user ==  null){
            return "redirect:/login";
        }else{
            return "create-application/create-organisation-type";
        }


    }
    @RequestMapping(value="/find-business", method = RequestMethod.POST)
    public String createOrganisationBusinessPost(@Valid @ModelAttribute("companyHouseLookup") CompanyHouseForm companyHouseForm,
                                             BindingResult bindingResult,
                                             Model model,
                                             HttpServletRequest request,
                                             HttpServletResponse response ) {
        User user = userAuthenticationService.getAuthenticatedUser(request);
        this.logState(request, response);

        if(user ==  null){
            return "redirect:/login";
        }else if(!bindingResult.hasErrors()){
            log.debug("OrganisationName " + companyHouseForm.getOrganisationName());
            List<CompanyHouseBusiness> companies = searchCompanyHouse(companyHouseForm.getOrganisationName());
            companies.forEach(c -> log.debug("Addresss: " + c.getLocation()));
            companies.forEach(c -> log.debug("line 1 : " + c.getOfficeAddress().getAddressLine1()));
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
        User user = userAuthenticationService.getAuthenticatedUser(request);
        this.logState(request, response);

        if(user ==  null){
            return "redirect:/login";
        }else{
            return "create-application/find-business";
        }
    }

    @RequestMapping("/selected-business/{companyId}")
    public String selectedBusiness(Form form, Model model,
                                      @PathVariable("companyId") final String companyId,
                                      HttpServletRequest request,
                                      HttpServletResponse response ) {
        User user = userAuthenticationService.getAuthenticatedUser(request);
        log.debug("companyHouseOrganisationId a " + companyId);

        this.saveToCookie(request, response, CreateApplicationConstants.COMPANY_HOUSE_COMPANY_ID, String.valueOf(companyId));
        this.logState(request, response);

        if(organisationService == null){
            log.debug("companyHouseService is null");
        }

        CompanyHouseBusiness org = organisationService.getCompanyHouseOrganisation(String.valueOf(companyId));
        model.addAttribute("business", org);

        if(user ==  null){
            return "redirect:/login";
        }else{
            return "create-application/confirm-selected-organisation";
        }
    }




    private List<CompanyHouseBusiness> searchCompanyHouse(String organisationName) {
        // TODO: implement company house api here
        return organisationService.searchCompanyHouseOrganisations(organisationName);
    }

    private void saveToCookie(HttpServletRequest request, HttpServletResponse response, String fieldName, String fieldValue){
        if(fieldName != null && fieldName != null){
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
        for (Cookie cookie : request.getCookies()) {
            log.debug("COOKIE name: " + cookie.getName());
            log.debug("COOKIE value: " + cookie.getValue());
        }
        log.debug("=== ==================== === ");
    }
}
