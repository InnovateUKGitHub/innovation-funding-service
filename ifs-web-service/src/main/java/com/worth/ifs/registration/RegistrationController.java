package com.worth.ifs.registration;

import com.worth.ifs.application.CreateApplicationController;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.application.service.OrganisationService;
import com.worth.ifs.application.service.UserService;
import com.worth.ifs.commons.resource.ResourceEnvelopeConstants;
import com.worth.ifs.commons.security.TokenAuthenticationService;
import com.worth.ifs.commons.resource.ResourceEnvelope;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.UserRoleType;
import com.worth.ifs.user.resource.UserResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
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

    @Autowired
    private TokenAuthenticationService tokenAuthenticationService;


    private final Log log = LogFactory.getLog(getClass());

    public final static String ORGANISATION_ID_PARAMETER_NAME = "organisationId";

    @RequestMapping(value="/register", method=RequestMethod.GET)
    public String registerForm(Model model, HttpServletRequest request) {
        String destination = "registration-register";

        Organisation organisation = getOrganisation(request);

        if(getOrganisationId(request)!=null && organisation!=null) {
            addRegistrationFormToModel(model, request);
            addOrganisationNameToModel(model, organisation);
        }
        else {
            destination = "redirect:/login";
        }

        return destination;
    }

    @RequestMapping(value="/register", method=RequestMethod.POST)
    public String registerFormSubmit(@Valid @ModelAttribute RegistrationForm registrationForm, BindingResult bindingResult, HttpServletResponse response, HttpServletRequest request, Model model) {
        String destination = "registration-register";

        if(!bindingResult.hasErrors()) {
            ResourceEnvelope<UserResource> userStatusWrapper = userService.createUserForOrganisation(registrationForm.getFirstName(),
                    registrationForm.getLastName(),
                    registrationForm.getPassword(),
                    registrationForm.getEmail(),
                    registrationForm.getTitle(),
                    registrationForm.getPhoneNumber(),
                    getOrganisationId(request),
                    UserRoleType.APPLICANT.getName());

            if(userStatusWrapper.getStatus().equals(ResourceEnvelopeConstants.OK.getName()) && userStatusWrapper.getEntity() != null) {
                CreateApplicationController.saveToCookie(response, "userId", String.valueOf(userStatusWrapper.getEntity().getId()));
                // loggin user directly
                tokenAuthenticationService.addAuthentication(response, userStatusWrapper.getEntity());
                destination = "redirect:/application/create/initialize-application/";
            }
            else {
                userStatusWrapper.getErrors().forEach(
                        error -> bindingResult.addError(
                                new ObjectError(
                                        error.getName(),
                                        error.getDescription()
                                )
                        )
                );
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

    private Long getOrganisationId(HttpServletRequest request) {
        String organisationIdString = request.getParameter(ORGANISATION_ID_PARAMETER_NAME);
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

    private void setFormActionURL(RegistrationForm registrationForm, HttpServletRequest request) {
        Long organisationId = getOrganisationId(request);
        registrationForm.setActionUrl("/registration/register?"+ORGANISATION_ID_PARAMETER_NAME+"="+organisationId);
    }
}
