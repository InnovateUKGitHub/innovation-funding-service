package com.worth.ifs.application;

import com.worth.ifs.login.LoginForm;
import com.worth.ifs.user.domain.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.util.ArrayList;
import java.util.List;

/**
 * This controller will handle all requests that are related to the application overview.
 * Application overview is the page that contains the most basic information about the current application and
 * the basic information about the competition the application is related to.
 */
@Controller
@RequestMapping("/create-application")
public class CreateApplicationController extends AbstractApplicationController {
    private final Log log = LogFactory.getLog(getClass());


    @RequestMapping("/check-eligibility/{competitionId}")
    public String checkEligibility(Form form, Model model,
                                   @PathParam(CreateApplicationConstants.COMPETITION_ID) Long competitionId,
                                   HttpServletRequest request,
                                   HttpServletResponse response){

        User user = userAuthenticationService.getAuthenticatedUser(request);
        model.addAttribute("loginForm", new LoginForm());
        model.addAttribute(CreateApplicationConstants.COMPETITION_ID, competitionId);

        log.warn("CompetitionId " + competitionId);
        this.saveToCookie(request, response, CreateApplicationConstants.COMPETITION_ID, String.valueOf(competitionId));

        return "create-application/check-eligibility";
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
        log.warn("=== Logging cookie state === ");
        for (Cookie cookie : request.getCookies()) {
            log.warn("COOKIE name: " + cookie.getName());
            log.warn("COOKIE value: " + cookie.getValue());
        }
        log.warn("=== ==================== === ");
    }


    @RequestMapping("/create-organisation-type")
    public String createAccountOrganisationType(@ModelAttribute Form form, Model model, HttpServletRequest request) {
        User user = userAuthenticationService.getAuthenticatedUser(request);

        log.warn("createOrganisationType Eligible" + form.getFormInput("eligible"));
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
            log.warn("OrganisationName "+ companyHouseForm.getOrganisationName());

            companyHouseForm.setCompanyHouseList(this.searchCompanyHouse(companyHouseForm.getOrganisationName()));

            return "create-application/find-business";
        }else{
            return "create-application/find-business";
        }
    }

    private List<CompanyHouse> searchCompanyHouse(String organisationName) {
        // TODO: implement company house api here
        List<CompanyHouse> companyHouseList = new ArrayList<>();
        companyHouseList.add(new CompanyHouse(04214477L, "Nomensa LTD", "Some description"));
        companyHouseList.add(new CompanyHouse(04214471L, "Worth Internet Systems", "Some description"));
        return companyHouseList;
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

    @RequestMapping(value="/selected-business/{companyHouseId}", method = RequestMethod.GET)
    public String selectedOrganisation(@PathParam("companyHouseId") Long companyHouseOrganisationId,
                                            HttpServletRequest request,
                                            HttpServletResponse response ) {
        User user = userAuthenticationService.getAuthenticatedUser(request);
        this.saveToCookie(request, response, CreateApplicationConstants.COMPANY_HOUSE_ORGANISATION_ID, String.valueOf(companyHouseOrganisationId));
        this.logState(request, response);
        log.warn("companyHouseOrganisationId "+ companyHouseOrganisationId);

        if(user ==  null){
            return "redirect:/login";
        }else{
            return "create-application/confirm-selected-organisation";
        }
    }
}
