package com.worth.ifs.registration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/registration")
public class RegistrationController {
    private final Log log = LogFactory.getLog(getClass());

    @RequestMapping(value="/register", method= RequestMethod.GET)
    public String login(Model model, HttpServletRequest request) {
        String destination = "registration-register";

        if(getOrganisationId(request)!=null) {
        RegistrationForm registrationForm = new RegistrationForm();
        model.addAttribute("registrationForm", registrationForm);
        }
        else {
            destination = "redirect:/login";
        }

        return destination;
    }

    @RequestMapping(value="/register", method=RequestMethod.POST)
    public String register(@Valid @ModelAttribute RegistrationForm registrationForm, BindingResult bindingResult, HttpServletResponse response, HttpServletRequest request) {


        return "registration-register";
    }

    private Long getOrganisationId(HttpServletRequest request) {
        String organisationIdString = request.getParameter("organisationId");
        Long organisationId = null;

        try {
            if(Long.parseLong(organisationIdString)>=0) {
                organisationId = Long.parseLong(organisationIdString);
            }
        }
        catch (NumberFormatException e) {
            log.info("Invalid organisationId number format:" + e);
        }

        return organisationId;
    }
}
