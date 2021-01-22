package org.innovateuk.ifs.registration.service;

import org.innovateuk.ifs.address.form.AddressForm;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.AddressTypeResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.innovateuk.ifs.organisation.service.CompaniesHouseRestService;
import org.innovateuk.ifs.project.invite.resource.SentProjectPartnerInviteResource;
import org.innovateuk.ifs.project.invite.service.ProjectPartnerInviteRestService;
import org.innovateuk.ifs.registration.form.InviteAndIdCookie;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.innovateuk.ifs.util.EncryptedCookieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

@Component
public class OrganisationJourneyEnd {

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private CompaniesHouseRestService companiesHouseRestService;

    @Autowired
    private RegistrationCookieService registrationCookieService;

    @Autowired
    private InviteRestService inviteRestService;

    @Autowired
    private UserRestService userRestService;

    @Autowired
    private EncryptedCookieService cookieUtil;

    @Autowired
    private ProjectPartnerInviteRestService projectPartnerInviteRestService;

    public String completeProcess(HttpServletRequest request, HttpServletResponse response, UserResource user, long organisationId) {
         updateExistingCompaniesHouseData(organisationId);
        if (user != null) {
            return handleExistingUser(request, response, user, organisationId);
        } else {
            registrationCookieService.saveToOrganisationIdCookie(organisationId, response);
            return "redirect:/registration/register";
        }
    }

    private void updateExistingCompaniesHouseData(final long organisationId) {
        RestResult<OrganisationResource> org = organisationRestService.getOrganisationById(organisationId);
        org.getOptionalSuccessObject().ifPresent(theOrg -> getOrganisationfromCompaniesHouse(theOrg));

    }

    private void getOrganisationfromCompaniesHouse(final OrganisationResource theOrg) {
        RestResult<OrganisationSearchResult> organisationWithNewCompaniesHouseData = companiesHouseRestService.getOrganisationById(theOrg.getCompaniesHouseNumber());
        organisationWithNewCompaniesHouseData.getOptionalSuccessObject().ifPresent(theOrgWithCHData -> updateOrganisationWithCompaniesHouseData(theOrgWithCHData,theOrg));
    }

    private void updateOrganisationWithCompaniesHouseData(OrganisationSearchResult org, OrganisationResource orgResource){
        orgResource.setSicCodes(org.getOrganisationSicCodes());
        orgResource.setExecutiveOfficers(org.getOrganisationExecutiveOfficers());
        List<OrganisationAddressResource> addressList = new ArrayList<>();
        OrganisationAddressResource organisationAddress = new OrganisationAddressResource();
        organisationAddress.setOrganisation(orgResource.getId());
        organisationAddress.setAddress(org.getOrganisationAddress());
        organisationAddress.setAddressType(new AddressTypeResource(1L, OrganisationAddressType.REGISTERED.name()));
        addressList.add(organisationAddress);
        orgResource.setAddresses(addressList);
        organisationRestService.createOrMatch(orgResource);
    }

    private String handleExistingUser(HttpServletRequest request, HttpServletResponse response, UserResource user, long organisationId) {
        if (!user.hasRole(Role.APPLICANT)) {
            userRestService.grantRole(user.getId(), Role.APPLICANT).getSuccess();
            cookieUtil.saveToCookie(response, "role", Role.APPLICANT.getName());
        }

        if (registrationCookieService.isCollaboratorJourney(request)) {
            return acceptInvite(request, response, user, organisationId);
        } else if (registrationCookieService.isLeadJourney(request)) {
            return createNewApplication(request, user, organisationId);
        } else {
            Optional<String> inviteHash = registrationCookieService.getInviteHashCookieValue(request);
            Optional<Long> competitionId = registrationCookieService.getCompetitionIdCookieValue(request);
            throw new ObjectNotFoundException("Could not create or find application",
                    Arrays.asList(String.valueOf(competitionId.orElse(null)), inviteHash.orElse(null), String.valueOf(user.getId())));
        }
    }

    private String createNewApplication(HttpServletRequest request, UserResource user, long organisationId) {
        ApplicationResource application = applicationRestService.createApplication(registrationCookieService.getCompetitionIdCookieValue(request).get(),
                user.getId(), organisationId, "").getSuccess();
        return redirectToApplicationOverview(application.getId());
    }

    private String acceptInvite(HttpServletRequest request, HttpServletResponse response, UserResource user, long organisationId) {
        Optional<String> applicationInviteHash = registrationCookieService.getInviteHashCookieValue(request);
        if (applicationInviteHash.isPresent()) {
            ApplicationInviteResource invite = inviteRestService.getInviteByHash(applicationInviteHash.get()).getSuccess();
            inviteRestService.acceptInvite(applicationInviteHash.get(), user.getId(), organisationId).getSuccess();
            registrationCookieService.deleteInviteHashCookie(response);
            return redirectToApplicationOverview(invite.getApplication());
        }
        Optional<InviteAndIdCookie> projectInvite = registrationCookieService.getProjectInviteHashCookieValue(request);
        if (projectInvite.isPresent()) {
            SentProjectPartnerInviteResource invite = projectPartnerInviteRestService.getInviteByHash(projectInvite.get().getId(), projectInvite.get().getHash()).getSuccess();
            projectPartnerInviteRestService.acceptInvite(projectInvite.get().getId(), invite.getId(), organisationId).getSuccess();
            registrationCookieService.deleteProjectInviteHashCookie(response);
            return redirectToApplicantDashboard();
        }
        throw new ObjectNotFoundException();
    }

    private String redirectToApplicantDashboard() {
        return format("redirect:/applicant/dashboard");
    }

    private String redirectToApplicationOverview(long applicationId) {
        return format("redirect:/application/%s", applicationId);
    }
}