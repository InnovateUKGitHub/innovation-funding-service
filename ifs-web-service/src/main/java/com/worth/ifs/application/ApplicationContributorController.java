package com.worth.ifs.application;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.invite.resource.InviteResource;
import com.worth.ifs.invite.service.InviteRestService;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/application/{applicationId}/contributors")
public class ApplicationContributorController extends AbstractApplicationController {
    private static final String CONTRIBUTORS_COOKIE = "contributor_invite_state";
    private final Log log = LogFactory.getLog(getClass());
    @Autowired
    private InviteRestService inviteRestService;
    //
    @Autowired
    private LocalValidatorFactoryBean validator;


    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String displayContributors(@PathVariable("applicationId") final Long applicationId) {
        return "application-contributors/display";
    }


    @RequestMapping(value = "/invite", method = RequestMethod.GET)
    public String inviteContributors(@PathVariable("applicationId") final Long applicationId,
                                     @ModelAttribute ContributorsForm contributorsForm,
                                     BindingResult bindingResult,
                                     HttpServletRequest request,
                                     Model model) {


        User user = userAuthenticationService.getAuthenticatedUser(request);
        model.addAttribute("authenticatedUser", user);

        ApplicationResource application = applicationService.getById(applicationId);
        model.addAttribute("currentApplication", application);

        ProcessRole leadApplicantProcessRole = userService.getLeadApplicantProcessRoleOrNull(application);
        Organisation leadOrganisation = leadApplicantProcessRole.getOrganisation();
        User leadApplicant = leadApplicantProcessRole.getUser();
        model.addAttribute("leadApplicant", leadApplicant);
        model.addAttribute("leadOrganisation", leadOrganisation);


        OrganisationInvite leadOrganisationInvite = new OrganisationInvite();
        leadOrganisationInvite.organisationName = leadOrganisation.getName();
        leadOrganisationInvite.organisationId = leadOrganisation.getId();
        contributorsForm.getOrganisations().add(leadOrganisationInvite);

        String json = ApplicationCreationController.getFromCookie(request, CONTRIBUTORS_COOKIE);

        if (json != null && !json.equals("")) {
            ContributorsForm contributorsForm2 = ApplicationCreationController.getObjectFromJson(json, ContributorsForm.class);
            contributorsForm.setOrganisations(contributorsForm2.getOrganisations());

            // got result from submit? validate
            validator.validate(contributorsForm, bindingResult);
        }

        return "application-contributors/invite";
    }

    /**
     * Handle form POST, manage ContributorsForm object, save to cookie, and redirect to the GET handler.
     */
    @RequestMapping(value = "/invite", method = RequestMethod.POST)
    public String inviteContributors(@PathVariable("applicationId") final Long applicationId,
                                     @RequestParam(name = "add_person", required = false) String organisationIndex,
                                     @RequestParam(name = "remove_person", required = false) String organisationAndPerson,
                                     @ModelAttribute ContributorsForm contributorsForm,
                                     BindingResult bindingResult,
                                     HttpServletResponse response,
                                     HttpServletRequest request) {
        ApplicationResource application = applicationService.getById(applicationId);
        ProcessRole leadApplicantProcessRole = userService.getLeadApplicantProcessRoleOrNull(application);
        if (organisationIndex != null) {
            addPersonRow(contributorsForm, organisationIndex, application);
        } else if (organisationAndPerson != null) {
            removePersonRow(contributorsForm, organisationAndPerson);
        }
        // User should never be able to set the organisation name or id of the lead-organisation.
        contributorsForm.getOrganisations().get(0).setOrganisationName(leadApplicantProcessRole.getOrganisation().getName());
        contributorsForm.getOrganisations().get(0).setOrganisationId(leadApplicantProcessRole.getOrganisation().getId());

        validator.validate(contributorsForm, bindingResult);

        if (request.getParameterMap().containsKey("save_contributors") && !bindingResult.hasErrors()) {
            contributorsForm.getOrganisations().forEach((organisationInvite) -> {
                List<InviteResource> invites = new ArrayList<>();
                Organisation existingOrganisation = organisationService.getOrganisationById(organisationInvite.getOrganisationId());

                organisationInvite.getInvites().stream().forEach(invite -> {
                    InviteResource inviteResource = new InviteResource(invite.getPersonName(), invite.getEmail(), applicationId);
                    if (existingOrganisation != null) {
                        inviteResource.setInviteOrganisationId(existingOrganisation.getId());
                    }
                    invites.add(inviteResource);
                });
                log.debug(String.format("Adding %s invite to organisation: %s", invites.size(), organisationInvite.getOrganisationName()));
                if (existingOrganisation != null) {
                    inviteRestService.createInvitesByOrganisation(existingOrganisation.getId(), invites);
                } else {
                    inviteRestService.createInvitesByInviteOrganisation(organisationInvite.getOrganisationName(), invites);
                }
            });

            // empty cookie, since the invites are saved.
            ApplicationCreationController.saveToCookie(response, CONTRIBUTORS_COOKIE, "");
            return ApplicationController.redirectToApplication(application);
        } else {
            String jsonState = ApplicationCreationController.getSerializedObject(contributorsForm);
            ApplicationCreationController.saveToCookie(response, CONTRIBUTORS_COOKIE, jsonState);
        }


        return String.format("redirect:/application/%d/contributors/invite", applicationId);
    }

    private void removePersonRow(ContributorsForm contributorsForm, String organisationAndPerson) {
        log.debug("remove person " + organisationAndPerson);
        int organisationIndex = Integer.parseInt(organisationAndPerson.substring(0, organisationAndPerson.indexOf("_")));
        int personIndex = Integer.parseInt(organisationAndPerson.substring(organisationAndPerson.indexOf("_") + 1, organisationAndPerson.length()));

        contributorsForm.getOrganisations().get(organisationIndex).getInvites().remove(personIndex);

        log.debug("organisationId " + organisationIndex);
        log.debug("personIndex " + personIndex);
    }

    private void addPersonRow(ContributorsForm contributorsForm, String organisationIndex, ApplicationResource application) {
        log.debug("add person " + organisationIndex);
//        if (organisationIndex.equals("0")) {
//            ProcessRole leadApplicantProcessRole = userService.getLeadApplicantProcessRoleOrNull(application);
//            Organisation leadOrganisation = leadApplicantProcessRole.getOrganisation();
//            organisationIndex = String.valueOf(leadOrganisation.getId());
//        }
        OrganisationInvite organisationInvite = contributorsForm.getOrganisations().get(Integer.parseInt(organisationIndex));
        if (organisationInvite == null) {
            log.error("organisationInvite is null");
        }
        if (organisationInvite.getInvites() == null) {
            log.error("organisationInvite.getInvites is null");
        }

        log.error(String.format("Add person to organisation : %s / %s", organisationIndex, organisationInvite.getOrganisationName()));
        List<InviteeForm> invites = organisationInvite.getInvites();
        invites.add(new InviteeForm());
    }

    private void getContributors(ApplicationResource application) {
        List<Long> roleIds = application.getProcessRoleIds();

        Map<Organisation, List<User>> organisationMap = new LinkedHashMap<>();
        roleIds.stream().forEach(roleId -> {
            ProcessRole role = processRoleService.getById(roleId);
        });

    }


}