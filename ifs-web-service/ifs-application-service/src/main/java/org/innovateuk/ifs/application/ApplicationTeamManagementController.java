package org.innovateuk.ifs.application;

import org.innovateuk.ifs.application.constant.ApplicationStatusConstants;
import org.innovateuk.ifs.application.form.ContributorsForm;
import org.innovateuk.ifs.application.form.InviteeForm;
import org.innovateuk.ifs.application.form.OrganisationInviteForm;
import org.innovateuk.ifs.application.form.RemoveContributorsForm;
import org.innovateuk.ifs.application.populator.ApplicationTeamManagementModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserService;
import org.innovateuk.ifs.util.CookieUtil;
import org.innovateuk.ifs.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

/**
 * This controller will handle all requests that are related to the management of application participants
 */
@Controller
@RequestMapping("/application/{applicationId}/team")
@PreAuthorize("hasAuthority('applicant')")
public class ApplicationTeamManagementController {

    @Autowired
    private InviteRestService inviteRestService;
    @Autowired
    private UserAuthenticationService userAuthenticationService;
    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrganisationService organisationService;
    @Autowired
    private CookieFlashMessageFilter cookieFlashMessageFilter;
    @Autowired
    private CookieUtil cookieUtil;

    @Autowired
    private ApplicationTeamManagementModelPopulator applicationTeamManagementModelPopulator;

    @Autowired
    @Qualifier("mvcValidator")
    private Validator validator;

    public static final String APPLICATION_CONTRIBUTORS_UPDATE_REMOVE_CONFIRM = "application-team/update-remove-confirm";
    public static final String APPLICATION_CONTRIBUTORS_UPDATE = "application-team/edit-org";
    private static final String CONTRIBUTORS_COOKIE = "contributor_invite_state";
    private static final String INVITES_SEND = "invitesSend";


    @RequestMapping(value = "update", method = RequestMethod.GET)
    public String update(@PathVariable("applicationId") Long applicationId,
                         @RequestParam(name = "organisation", required = false) Long organisationId,
                         @ModelAttribute ContributorsForm contributorsForm,
                         @ModelAttribute("loggedInUser") UserResource user,
                         BindingResult bindingResult,
                         HttpServletResponse response,
                         HttpServletRequest request,
                         Model model) {

        ApplicationResource application = applicationService.getById(applicationId);
        ProcessRoleResource leadApplicantProcessRole = userService.getLeadApplicantProcessRoleOrNull(application);
        OrganisationResource leadOrganisation = organisationService.getOrganisationById(leadApplicantProcessRole.getOrganisationId());
        UserResource leadApplicant = userService.findById(leadApplicantProcessRole.getUser());

        List<InviteOrganisationResource> savedInvites = getSavedInviteOrganisations(application);
        Long authenticatedUserOrganisationId = getAuthenticatedUserOrganisationId(user, savedInvites);
        addSavedInvitesToForm(contributorsForm, leadOrganisation, savedInvites, organisationId);
        mergeAndValidateCookieData(request, response, bindingResult, contributorsForm, applicationId, application, leadApplicant);
        long selectedOrgIndex = getSelectedOrgIndex(contributorsForm, organisationId);

        model.addAttribute("model", applicationTeamManagementModelPopulator.populateModel(applicationId, organisationId, user, authenticatedUserOrganisationId, selectedOrgIndex));

        return APPLICATION_CONTRIBUTORS_UPDATE;
    }

    @RequestMapping(value = "update/remove/{inviteId}/confirm", method = RequestMethod.GET)
    public String deleteContributorConfirmation(@PathVariable("applicationId") final Long applicationId,
                                                @PathVariable("inviteId") final Long inviteId,
                                                Model model) {
        model.addAttribute("currentApplication", applicationService.getById(applicationId));
        model.addAttribute("inviteId", inviteId);
        model.addAttribute("removeContributorForm", new RemoveContributorsForm());

        return APPLICATION_CONTRIBUTORS_UPDATE_REMOVE_CONFIRM;
    }

    @RequestMapping(value = "update/remove", method = RequestMethod.POST)
    public String deleteContributor(@PathVariable("applicationId") Long applicationId,
                                    @Valid @ModelAttribute RemoveContributorsForm removeContributorsForm) {
        applicationService.removeCollaborator(removeContributorsForm.getApplicationInviteId()).getSuccessObjectOrThrowException();

        return "redirect:/application/" + applicationId + "/contributors";
    }

    /**
     * Handle form POST, manage ContributorsForm object, save to cookie, and redirect to the GET handler.
     */
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public String updateContributors(@PathVariable("applicationId") Long applicationId,
                                     @RequestParam(name = "organisation", required = false) Long organisationId,
                                     @RequestParam(name = "add_person", required = false) String organisationIndex,
                                     @RequestParam(name = "add_partner", required = false) String addPartner,
                                     @RequestParam(name = "remove_person", required = false) String organisationAndPerson,
                                     @RequestParam(name = "save_contributors", required = false) String saveContributors,
                                     @RequestParam(name = "newApplication", required = false) String newApplication,
                                     @ModelAttribute ContributorsForm contributorsForm,
                                     BindingResult bindingResult,
                                     HttpServletRequest request,
                                     HttpServletResponse response) {

        ApplicationResource application = applicationService.getById(applicationId);
        ProcessRoleResource leadApplicantProcessRole = userService.getLeadApplicantProcessRoleOrNull(application);
        OrganisationResource organisationResource = organisationService.getOrganisationById(leadApplicantProcessRole.getOrganisationId());
        // User should never be able to set the organisation name or id of the lead-organisation.
        contributorsForm.getOrganisations().get(0).setOrganisationName(organisationResource.getName());
        contributorsForm.getOrganisations().get(0).setOrganisationId(organisationResource.getId());
        contributorsForm.setTriedToSave(false);

        if (organisationIndex != null) {
            addPersonRow(contributorsForm, organisationIndex);
            saveFormValuesToCookie(response, contributorsForm, applicationId);
        } else if (organisationAndPerson != null) {
            removePersonRow(contributorsForm, organisationAndPerson);
            saveFormValuesToCookie(response, contributorsForm, applicationId);
        } else if (addPartner != null) {
            addPartnerRow(contributorsForm);
            saveFormValuesToCookie(response, contributorsForm, applicationId);
        } else if (saveContributors != null) {
            contributorsForm.setTriedToSave(true);
            validator.validate(contributorsForm, bindingResult);
            UserResource leadApplicant = userService.findById(leadApplicantProcessRole.getUser());
            validateUniqueEmailsAndApplicantNames(contributorsForm, bindingResult, application, leadApplicant);
            validatePermissionToInvite(contributorsForm, bindingResult, application, leadApplicant, request);

            if (!bindingResult.hasErrors()) {
                saveContributors(applicationId, contributorsForm, response);
                // empty cookie, since the invites are saved.
                cookieUtil.saveToCookie(response, CONTRIBUTORS_COOKIE, "");

                if (newApplication != null && ApplicationStatusConstants.CREATED.getId().equals(application.getApplicationStatus())) {
                    applicationService.updateStatus(application.getId(), ApplicationStatusConstants.OPEN.getId());
                    return ApplicationController.redirectToApplication(application);
                }
                return format("redirect:/application/%d/team", applicationId);
            } else {
                saveFormValuesToCookie(response, contributorsForm, applicationId);
            }
        } else {
            // no specific submit action, just save the data to the cookie.
            saveFormValuesToCookie(response, contributorsForm, applicationId);
        }

        if (newApplication != null) {
            return format("redirect:/application/%d/team/update/?newApplication", applicationId);
        }
        return format("redirect:/application/%d/team/update?organisation=%d", applicationId, organisationId);
    }

    private int getSelectedOrgIndex(ContributorsForm contributorsForm, Long organisationId) {
        return IntStream.range(0, contributorsForm.getOrganisations().size())
                .filter(i -> organisationId.equals(contributorsForm.getOrganisations().get(i).getOrganisationId()))
                .findFirst().getAsInt();
    }

    private void saveContributors(@PathVariable("applicationId") Long applicationId, @ModelAttribute ContributorsForm contributorsForm, HttpServletResponse response) {
        contributorsForm.getOrganisations().forEach(invite -> saveContributor(invite, applicationId, response));
    }

    private void saveContributor(OrganisationInviteForm organisationInvite, Long applicationId, HttpServletResponse response) {
        List<ApplicationInviteResource> invites = new ArrayList<>();
        OrganisationResource existingOrganisation = null;
        if (organisationInvite.getOrganisationId() != null) {
            // check if there is a organisation with this ID, just to make sure the user has not entered a non-existing organisation id.
            existingOrganisation = organisationService.getOrganisationById(organisationInvite.getOrganisationId());
        }

        organisationInvite.getInvites().stream().forEach(invite -> {
            ApplicationInviteResource inviteResource = new ApplicationInviteResource(invite.getPersonName(), invite.getEmail(), applicationId);
            if (organisationInvite.getOrganisationInviteId() != null && !organisationInvite.getOrganisationInviteId().equals(Long.valueOf(0))) {
                inviteResource.setInviteOrganisation(organisationInvite.getOrganisationInviteId());
            }
            invites.add(inviteResource);
        });

        if (!invites.isEmpty()) {
            // save new invites, to InviteOrganisation that already is saved.
            if (organisationInvite.getOrganisationInviteId() != null && !organisationInvite.getOrganisationInviteId().equals(Long.valueOf(0))) {
                inviteRestService.saveInvites(invites);
                cookieFlashMessageFilter.setFlashMessage(response, INVITES_SEND);
            } else if (existingOrganisation != null) {
                // Save invites, and link to existing organisation.
                inviteRestService.createInvitesByOrganisation(existingOrganisation.getId(), invites);
                cookieFlashMessageFilter.setFlashMessage(response, INVITES_SEND);
            } else {
                // Save invites, and create new InviteOrganisation
                inviteRestService.createInvitesByInviteOrganisation(organisationInvite.getOrganisationName(), invites);
                cookieFlashMessageFilter.setFlashMessage(response, INVITES_SEND);
            }
        }
    }

    private void saveFormValuesToCookie(HttpServletResponse response, ContributorsForm contributorsForm, Long applicationId) {
        contributorsForm.setApplicationId(applicationId);
        String jsonState = JsonUtil.getSerializedObject(contributorsForm);
        cookieUtil.saveToCookie(response, CONTRIBUTORS_COOKIE, jsonState);
    }

    private void removePersonRow(ContributorsForm contributorsForm, String organisationAndPerson) {
        int organisationIndex = Integer.parseInt(organisationAndPerson.substring(0, organisationAndPerson.indexOf("_")));
        int personIndex = Integer.parseInt(organisationAndPerson.substring(organisationAndPerson.indexOf("_") + 1, organisationAndPerson.length()));

        // Removing the last person from a organisation will also remove the organisation itself.
        contributorsForm.getOrganisations().get(organisationIndex).getInvites().remove(personIndex);
        if (organisationIndex != 0 && contributorsForm.getOrganisations().get(organisationIndex).getInvites().isEmpty()) {
            contributorsForm.getOrganisations().remove(organisationIndex);
        }
    }

    private void validatePermissionToInvite(ContributorsForm contributorsForm, BindingResult bindingResult,
                                            ApplicationResource application, UserResource leadApplicant, HttpServletRequest request) {

        UserResource authenticatedUser = userAuthenticationService.getAuthenticatedUser(request);

        if (leadApplicant != null && leadApplicant.getId().equals(authenticatedUser.getId())) {
            return;
        }

        Long authenticatedUserOrganisationId = getAuthenticatedUserOrganisationId(authenticatedUser, getSavedInviteOrganisations(application));

        contributorsForm.getOrganisations().forEach(invite -> {
            if (invite.getInvites() != null && !invite.getInvites().isEmpty() && (invite.getOrganisationId() == null || !invite.getOrganisationId().equals(authenticatedUserOrganisationId))) {
                // Could not add the element, so its a duplicate.
                FieldError fieldError = new FieldError("contributorsForm", format("organisations[%d].organisationName", contributorsForm.getOrganisations().indexOf(invite), 0), null, false, new String[]{"CannotInviteOrganisation"}, null, "As you are not the lead applicant, you cannot invite people from organisations other than your own.");
                bindingResult.addError(fieldError);
            }
        });
    }


    private List<InviteOrganisationResource> getSavedInviteOrganisations(ApplicationResource application) {
        return inviteRestService.getInvitesByApplication(application.getId()).handleSuccessOrFailure(
                failure -> Collections.<InviteOrganisationResource>emptyList(),
                success -> success);
    }

    private Long getAuthenticatedUserOrganisationId(UserResource user, List<InviteOrganisationResource> savedInvites) {
        Optional<InviteOrganisationResource> matchingOrganisationResource = savedInvites.stream()
                .filter(inviteOrg -> inviteOrg.getInviteResources().stream()
                        .anyMatch(inv -> user.getEmail().equals(inv.getEmail())))
                .findFirst();
        return matchingOrganisationResource.map((invOrgRes -> invOrgRes.getOrganisation())).orElse(null);
    }

    /**
     * Add the invites from the database, to the ContributorsForm object.
     */
    private void addSavedInvitesToForm(ContributorsForm contributorsForm, OrganisationResource leadOrganisation, List<InviteOrganisationResource> savedInvites, Long organisationId) {
        OrganisationInviteForm leadOrganisationInviteForm = new OrganisationInviteForm();
        leadOrganisationInviteForm.setOrganisationName(leadOrganisation.getName());
        leadOrganisationInviteForm.setOrganisationId(leadOrganisation.getId());
        Optional<InviteOrganisationResource> hasInvites = savedInvites.stream()
                .filter(i -> leadOrganisation.getId().equals(i.getOrganisation()))
                .findAny();
        if (hasInvites.isPresent()) {
            leadOrganisationInviteForm.setOrganisationInviteId(hasInvites.get().getId());
        }
        contributorsForm.getOrganisations().add(leadOrganisationInviteForm);

        savedInvites.stream()
                .filter(inviteOrg -> inviteOrg.getOrganisation() == null || !inviteOrg.getOrganisation().equals(leadOrganisation.getId()))
                .forEach(inviteOrg -> {
                    OrganisationInviteForm invitedOrgForm = new OrganisationInviteForm();
                    invitedOrgForm.setOrganisationName(inviteOrg.getOrganisationName());
                    invitedOrgForm.setOrganisationNameConfirmed(inviteOrg.getOrganisationNameConfirmed());
                    invitedOrgForm.setOrganisationId(inviteOrg.getOrganisation());
                    invitedOrgForm.setOrganisationInviteId(inviteOrg.getId());
                    contributorsForm.getOrganisations().add(invitedOrgForm);
                });

        // Reduce the contributors form org list to selected org
//        List<OrganisationInviteForm> selectedOrganisation = contributorsForm.getOrganisations()
//                .stream().filter(org -> org.getOrganisationId().equals(organisationId)).collect(toList());
//        contributorsForm.setOrganisations(selectedOrganisation);
    }

    private void addPersonRow(ContributorsForm contributorsForm, String organisationIndex) {
        OrganisationInviteForm organisationInviteForm = contributorsForm.getOrganisations().get(Integer.parseInt(organisationIndex));
        List<InviteeForm> invites = organisationInviteForm.getInvites();
        invites.add(new InviteeForm());
    }

    private void addPartnerRow(ContributorsForm contributorsForm) {
        OrganisationInviteForm organisationInviteForm = new OrganisationInviteForm();
        InviteeForm invite = new InviteeForm();
        List<InviteeForm> invites = organisationInviteForm.getInvites();
        invites.add(invite);
        organisationInviteForm.setInvites(invites);
        contributorsForm.getOrganisations().add(organisationInviteForm);
    }

    private void mergeAndValidateCookieData(HttpServletRequest request, HttpServletResponse response, BindingResult bindingResult, ContributorsForm contributorsForm, Long applicationId, ApplicationResource application, UserResource leadApplicant) {

        String json = cookieUtil.getCookieValue(request, CONTRIBUTORS_COOKIE);

        if (json != null && !"".equals(json)) {
            ContributorsForm contributorsFormCookie = JsonUtil.getObjectFromJson(json, ContributorsForm.class);
            if (contributorsFormCookie.getApplicationId().equals(applicationId)) {
                if (contributorsFormCookie.isTriedToSave()) {
                    // if the form was saved, validate and update cookie.
                    contributorsFormCookie.setTriedToSave(false);
                    String jsonState = JsonUtil.getSerializedObject(contributorsFormCookie);
                    cookieUtil.saveToCookie(response, CONTRIBUTORS_COOKIE, jsonState);

                    contributorsForm.merge(contributorsFormCookie);
                    validator.validate(contributorsForm, bindingResult);
                    validateUniqueEmailsAndApplicantNames(contributorsForm, bindingResult, application, leadApplicant);
                } else {
                    contributorsForm.merge(contributorsFormCookie);
                }
            }
        }
    }

    /**
     * Check if e-mail addresses and applicant name entered, are unique within this application's invites.
     */
    private void validateUniqueEmailsAndApplicantNames(@ModelAttribute ContributorsForm contributorsForm, BindingResult bindingResult, ApplicationResource application, UserResource leadApplicant) {
        Set<String> savedEmails = getSavedEmailAddresses(application, leadApplicant);
        Set<String> savedNames = getSavedApplicantNames(application, leadApplicant);

        contributorsForm.getOrganisations().forEach(organisation -> {
            Integer organisationIndex = contributorsForm.getOrganisations().indexOf(organisation);

            checkInvitesForUniques(savedEmails, savedNames, organisation, organisationIndex, bindingResult);
        });
    }

    private Set<String> getSavedEmailAddresses(ApplicationResource application, UserResource leadApplicant) {
        Set<String> savedEmails = new TreeSet<>();
        List<InviteOrganisationResource> savedInvites = getSavedInviteOrganisations(application);
        savedInvites.forEach(s -> s.getInviteResources().stream().forEach(i -> savedEmails.add(i.getEmail())));
        savedEmails.add(leadApplicant.getEmail());
        return savedEmails;
    }

    private Set<String> getSavedApplicantNames(ApplicationResource application, UserResource leadApplicant) {
        Set<String> savedNames = new TreeSet<>();
        List<InviteOrganisationResource> savedInvites = getSavedInviteOrganisations(application);
        savedInvites.forEach(s -> s.getInviteResources().stream().forEach(i -> savedNames.add(i.getName())));
        savedNames.add(leadApplicant.getName());
        return savedNames;
    }

    private void checkInvitesForUniques(Set<String> savedEmails, Set<String> savedNames, OrganisationInviteForm organisation, Integer organisationIndex, BindingResult bindingResult) {
        organisation.getInvites().forEach(invitee -> {
            Integer inviteIndex = organisation.getInvites().indexOf(invitee);

            checkInviteForUniqueEmails(savedEmails, inviteIndex, invitee, organisationIndex, organisation, bindingResult);
            checkInviteForUniqueApplicantNames(savedNames, inviteIndex, invitee, organisationIndex, organisation, bindingResult);
        });
    }

    private void checkInviteForUniqueApplicantNames(Set<String> savedNames, Integer inviteIndex, InviteeForm invitee, Integer organisationIndex, OrganisationInviteForm organisation, BindingResult bindingResult) {
        if (!savedNames.add(invitee.getPersonName())) {
            bindingResult.addError(
                    createNotUniqueFieldError(
                            format("organisations[%d].invites[%d].personName", organisationIndex, inviteIndex),
                            invitee.getPersonName(),
                            "You have already added this applicant name.")
            );
        }
    }

    private void checkInviteForUniqueEmails(Set<String> savedEmails, Integer inviteIndex, InviteeForm invitee, Integer organisationIndex, OrganisationInviteForm organisation, BindingResult bindingResult) {
        if (!savedEmails.add(invitee.getEmail())) {
            bindingResult.addError(
                    createNotUniqueFieldError(
                            format("organisations[%d].invites[%d].email", organisationIndex, inviteIndex),
                            invitee.getEmail(),
                            "You have already added this email address.")
            );
        }
    }

    private FieldError createNotUniqueFieldError(String field, String value, String message) {
        FieldError fieldError = new FieldError("contributorsForm",
                field,
                value,
                false,
                new String[]{"NotUnique"},
                null,
                message);

        return fieldError;
    }
}
