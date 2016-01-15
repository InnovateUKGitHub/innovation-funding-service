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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
@RequestMapping("/application/{applicationId}/contributors")
public class ApplicationContributorController extends AbstractApplicationController {
    private static final String CONTRIBUTORS_COOKIE = "contributor_invite_state";
    private final Log log = LogFactory.getLog(getClass());
    @Autowired
    private InviteRestService inviteRestService;


    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String displayContributors(@PathVariable("applicationId") final Long applicationId) {
        return "application-contributors/display";
    }


    @RequestMapping(value = "/invite", method = RequestMethod.GET)
    public String inviteContributors(@PathVariable("applicationId") final Long applicationId,
                                     @ModelAttribute ContributorsForm contributorsForm,
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

        Map<Organisation, List<User>> organisationMap = new LinkedHashMap<>();
        organisationMap.put(leadOrganisation, Arrays.asList());
        model.addAttribute("organisationMap", organisationMap);

        Map<String, String> userMap = new LinkedHashMap<>();
        contributorsForm.addOrganisation(String.valueOf(leadOrganisation.getId()), Arrays.asList());


        String json = ApplicationCreationController.getFromCookie(request, CONTRIBUTORS_COOKIE);

        if (json != null && !json.equals("")) {
            ContributorsForm contributorsForm2 = ApplicationCreationController.getObjectFromJson(json, ContributorsForm.class);
            contributorsForm.setOrganisationMap(contributorsForm2.getOrganisationMap());
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
                                     HttpServletResponse response,
                                     HttpServletRequest request) {
        ApplicationResource application = applicationService.getById(applicationId);
        ProcessRole leadApplicantProcessRole = userService.getLeadApplicantProcessRoleOrNull(application);
        if (organisationIndex != null) {
            addPersonRow(contributorsForm, organisationIndex, application);
        } else if (organisationAndPerson != null) {
            removePersonRow(contributorsForm, organisationAndPerson);
        }

        String jsonState = ApplicationCreationController.getSerializedObject(contributorsForm);
        ApplicationCreationController.saveToCookie(response, CONTRIBUTORS_COOKIE, jsonState);

        if(request.getParameterMap().containsKey("save_contributors")){
            contributorsForm.getOrganisationMap().forEach((organisationName, inviteMap) -> {
                List<InviteResource> invites = new ArrayList<>();
                Organisation existingOrganisation = organisationService.getOrganisationById(Long.valueOf(organisationName));
                if (existingOrganisation != null) {
                    organisationName = existingOrganisation.getName();
                }
                organisationName = leadApplicantProcessRole.getOrganisation().getName();
                inviteMap.stream().forEach(invite -> {
                    InviteResource inviteResource = new InviteResource(invite.getPersonName(), invite.getEmail(), applicationId);
                    if (existingOrganisation != null) {
                        inviteResource.setInviteOrganisationId(existingOrganisation.getId());
                    }
                    invites.add(inviteResource);
                });
                log.debug(String.format("Adding %s invite to organisation: %s", invites.size(), organisationName));
                if(existingOrganisation != null){
                    inviteRestService.createInvitesByOrganisation(existingOrganisation.getId(), invites);
                }else{
                    inviteRestService.createInvitesByInviteOrganisation(organisationName, invites);
                }
            });
        }


        return String.format("redirect:/application/%d/contributors/invite", applicationId);
    }

    private void removePersonRow(ContributorsForm contributorsForm, String organisationAndPerson) {
        log.debug("remove person " + organisationAndPerson);
        String organisationId = organisationAndPerson.substring(0, organisationAndPerson.indexOf("_"));
        int personIndex = Integer.parseInt(organisationAndPerson.substring(organisationAndPerson.indexOf("_") + 1, organisationAndPerson.length()));

        contributorsForm.getOrganisation(organisationId).remove(personIndex);

        log.debug("organisationId " + organisationId);
        log.debug("personIndex " + personIndex);
    }

    private void addPersonRow(ContributorsForm contributorsForm, String organisationIndex, ApplicationResource application) {
        log.debug("add person " + organisationIndex);
        if (organisationIndex.equals("0")) {
            ProcessRole leadApplicantProcessRole = userService.getLeadApplicantProcessRoleOrNull(application);
            Organisation leadOrganisation = leadApplicantProcessRole.getOrganisation();
            organisationIndex = String.valueOf(leadOrganisation.getId());
        }
        Map<String, List<InviteeForm>> orgMap = contributorsForm.getOrganisationMap();
        List<InviteeForm> orgInvitees = orgMap.getOrDefault(organisationIndex, new ArrayList<>());
        orgInvitees.add(new InviteeForm(0L, "", ""));
        orgMap.put(organisationIndex, orgInvitees);
    }

    private void getContributors(ApplicationResource application) {
        List<Long> roleIds = application.getProcessRoleIds();

        Map<Organisation, List<User>> organisationMap = new LinkedHashMap<>();
        roleIds.stream().forEach(roleId -> {
            ProcessRole role = processRoleService.getById(roleId);
        });

    }


}