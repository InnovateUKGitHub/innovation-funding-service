package com.worth.ifs.registration;

import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.application.service.OrganisationService;
import com.worth.ifs.application.service.UserService;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.login.LoginForm;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.domain.UserRoleType;
import com.worth.ifs.user.dto.UserDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private UserService userService;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private CompetitionService competitionService;

    private final Log log = LogFactory.getLog(getClass());

    @RequestMapping(value="/register", method= RequestMethod.GET)
    public String register(Model model, HttpServletRequest request) {
        String destination = "registration-register";

        Organisation organisation = getOrganisation(request);
        Long competitionId = getCompetitionId(request);

        if(getOrganisationId(request)!=null && organisation!=null && competitionId!=null) {
            addRegistrationFormToModel(model, request);
            addOrganisationNameToModel(model, organisation);
        }
        else {
            destination = "redirect:/login";
        }

        return destination;
    }

    @RequestMapping(value="/register", method=RequestMethod.POST)
    public String register(@Valid @ModelAttribute RegistrationForm registrationForm, BindingResult bindingResult, HttpServletResponse response, HttpServletRequest request, Model model) {
        String destination = "registration-register";

        if(!bindingResult.hasErrors()) {
            UserDto user = userService.createUserForOrganisation(registrationForm.getFirstName(),
                    registrationForm.getLastName(),
                    registrationForm.getPassword(),
                    registrationForm.getEmail(),
                    registrationForm.getTitle(),
                    registrationForm.getPhoneNumber(),
                    getOrganisationId(request),
                    UserRoleType.APPLICANT.getName());
            if(user!=null) {
                destination = "redirect:/login?competitionId="+getCompetitionId(request);
            }
        }
        else {
            Organisation organisation = getOrganisation(request);
            addOrganisationNameToModel(model, organisation);
        }
        return destination;
    }

    private void addRegistrationFormToModel(Model model, HttpServletRequest request) {
        RegistrationForm registrationForm = new RegistrationForm();
        setFormActionURL(registrationForm, request);
        model.addAttribute("registrationForm", registrationForm);
    }

    private void addOrganisationNameToModel(Model model, Organisation organisation) {
        model.addAttribute("organisationName",organisation.getName());
    }

    private Organisation getOrganisation(HttpServletRequest request) {
        return organisationService.getOrganisationById(getOrganisationId(request));
    }

    private Boolean addOrganisationName() {
        return true;
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

    private Long getCompetitionId(HttpServletRequest request) {
        String competitionIdString = request.getParameter("competitionId");
        Long competitionId = null;

        try {
            if(Long.parseLong(competitionIdString)>=0) {
                competitionId = Long.parseLong(competitionIdString);
            }
        }
        catch (NumberFormatException e) {
            log.info("Invalid competitionId number format:" + e);
        }

        return competitionId;
    }

    private void setFormActionURL(RegistrationForm registrationForm, HttpServletRequest request) {
        Long organisationId = getOrganisationId(request);
        Long competitionId = getCompetitionId(request);
        registrationForm.setActionUrl("/registration/register?organisationId="+organisationId+"&competitionId="+competitionId);
    }
}
