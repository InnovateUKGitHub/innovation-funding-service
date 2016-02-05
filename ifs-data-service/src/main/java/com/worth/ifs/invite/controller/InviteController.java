package com.worth.ifs.invite.controller;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.commons.resource.ResourceEnvelope;
import com.worth.ifs.commons.resource.ResourceEnvelopeConstants;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.invite.constant.InviteStatusConstants;
import com.worth.ifs.invite.domain.Invite;
import com.worth.ifs.invite.domain.InviteOrganisation;
import com.worth.ifs.invite.repository.InviteOrganisationRepository;
import com.worth.ifs.invite.repository.InviteRepository;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.resource.InviteResource;
import com.worth.ifs.invite.resource.InviteResultsResource;
import com.worth.ifs.invite.service.InviteRestService;
import com.worth.ifs.invite.transactional.InviteService;
import com.worth.ifs.notifications.resource.Notification;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.repository.OrganisationRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * InviteController is to handle the REST calls from the web-service and contains the handling of all call involving the Invite and InviteOrganisations.
 */

@RestController
@RequestMapping("/invite")
public class InviteController {
    private final Log log = LogFactory.getLog(getClass());
    @Value("${ifs.web.baseURL}")
    String webBaseUrl;
    @Autowired
    InviteOrganisationRepository inviteOrganisationRepository;

    @Autowired
    InviteRepository inviteRepository;

    @Autowired
    OrganisationRepository organisationRepository;

    @Autowired
    ApplicationRepository applicationRepository;

    @Autowired
    InviteRestService inviteRestService;
    @Autowired
    InviteService inviteService;

    @RequestMapping("/createApplicationInvites")
    public ResourceEnvelope<InviteResultsResource> createApplicationInvites(@RequestBody InviteOrganisationResource inviteOrganisationResource, HttpServletRequest request) {
        ResourceEnvelope<InviteResultsResource> resourceEnvelope;

        if (inviteOrganisationResourceIsValid(inviteOrganisationResource)) {
            InviteOrganisation newInviteOrganisation = assembleInviteOrganisationFromResource(inviteOrganisationResource);
            List<Invite> newInvites = assembleInvitesFromInviteOrganisationResource(inviteOrganisationResource, newInviteOrganisation);
            inviteOrganisationRepository.save(newInviteOrganisation);
            inviteRepository.save(newInvites);
            resourceEnvelope = sendInvites(newInvites);
        } else {
            resourceEnvelope = new ResourceEnvelope<>(ResourceEnvelopeConstants.ERROR.getName(), new ArrayList<>(), new InviteResultsResource());
        }

        return resourceEnvelope;
    }

    @RequestMapping("/getInviteByHash/{hash}")
    public ResourceEnvelope<InviteResource> getInviteByHash(@PathVariable("hash") String hash) {
        Optional<Invite> invite = inviteRepository.getByHash(hash);
        if (invite.isPresent()) {
            InviteResource inviteResource = new InviteResource(invite.get());
            ResourceEnvelope<InviteResource> resourceEnvelope = new ResourceEnvelope<>(ResourceEnvelopeConstants.OK.getName(), new ArrayList<>(), inviteResource);
            return resourceEnvelope;
        }

        ResourceEnvelope<InviteResource> resourceEnvelope = new ResourceEnvelope<>(ResourceEnvelopeConstants.ERROR.getName(), new ArrayList<>(), null);
        return resourceEnvelope;
    }

    @RequestMapping("/getInvitesByApplicationId/{applicationId}")
    public Collection<InviteOrganisationResource> getInvitesByApplication(@PathVariable("applicationId") Long applicationId) {
        Map<Long, InviteOrganisationResource> results = new LinkedHashMap<>();
        List<Invite> invites = inviteRepository.findByApplicationId(applicationId);
        invites.stream().forEach(i -> {
            results.put(i.getInviteOrganisation().getId(), new InviteOrganisationResource(i.getInviteOrganisation()));
        });
        return results.values();
    }

    @RequestMapping(value = "/saveInvites", method = RequestMethod.POST)
    public ResourceEnvelope<InviteResultsResource> saveInvites(@RequestBody List<InviteResource> inviteResources, HttpServletRequest request) {
        List<Invite> invites = new ArrayList<>();
        inviteResources.stream().forEach(iR -> invites.add(mapInviteResourceToInvite(iR, null)));
        inviteRepository.save(invites);
        return sendInvites(invites);

    }

    private ResourceEnvelope<InviteResultsResource> sendInvites(List<Invite> invites) {
        List<ServiceResult<Notification>> results = inviteService.inviteCollaborators(webBaseUrl, invites);

        long failures = results.stream().filter(r -> r.isFailure()).count();
        long successes = results.stream().filter(r -> r.isSuccess()).count();
        log.info(String.format("Invite sending requests %s Success: %s Failures: %s", invites.size(), successes, failures));

        InviteResultsResource resource = new InviteResultsResource();
        resource.setInvitesSendFailure((int) failures);
        resource.setInvitesSendSuccess((int) successes);
        ResourceEnvelope<InviteResultsResource> resourceEnvelope = new ResourceEnvelope(ResourceEnvelopeConstants.OK.getName(), new ArrayList<>(), resource);
        return resourceEnvelope;
    }

    private InviteOrganisation assembleInviteOrganisationFromResource(InviteOrganisationResource inviteOrganisationResource) {
        Organisation organisation = null;
        if (inviteOrganisationResource.getOrganisation() != null) {
            organisation = organisationRepository.findOne(inviteOrganisationResource.getOrganisation());
        } else {
            log.error("organisationId = null");
        }
        InviteOrganisation newInviteOrganisation = new InviteOrganisation(
                inviteOrganisationResource.getOrganisationName(),
                organisation,
                null
        );

        return newInviteOrganisation;
    }

    private List<Invite> assembleInvitesFromInviteOrganisationResource(InviteOrganisationResource inviteOrganisationResource, InviteOrganisation newInviteOrganisation) {
        List<Invite> invites = new ArrayList<>();
        inviteOrganisationResource.getInviteResources().forEach(inviteResource ->
                        invites.add(mapInviteResourceToInvite(inviteResource, newInviteOrganisation))
        );

        return invites;
    }

    private Invite mapInviteResourceToInvite(InviteResource inviteResource, InviteOrganisation newInviteOrganisation) {
        Application application = applicationRepository.findOne(inviteResource.getApplication());
        if (newInviteOrganisation == null && inviteResource.getInviteOrganisation() != null) {
            newInviteOrganisation = inviteOrganisationRepository.findOne(inviteResource.getInviteOrganisation());
        }
        Invite invite = new Invite(inviteResource.getName(), inviteResource.getEmail(), application, newInviteOrganisation, null, InviteStatusConstants.CREATED);

        return invite;
    }

    private boolean inviteOrganisationResourceIsValid(InviteOrganisationResource inviteOrganisationResource) {
        if (!inviteOrganisationResourceNameAndIdAreValid(inviteOrganisationResource)) {
            return false;
        }

        if (!allInviteResourcesAreValid(inviteOrganisationResource)) {
            return false;
        }

        return true;
    }

    private boolean inviteOrganisationResourceNameAndIdAreValid(InviteOrganisationResource inviteOrganisationResource) {
        if ((inviteOrganisationResource.getOrganisationName() == null ||
                inviteOrganisationResource.getOrganisationName().isEmpty())
                &&
                inviteOrganisationResource.getOrganisation() == null) {
            return false;
        } else {
            return true;
        }
    }

    private boolean allInviteResourcesAreValid(InviteOrganisationResource inviteOrganisationResource) {
        if (inviteOrganisationResource.getInviteResources()
                .stream()
                .filter(inviteResource -> !inviteResourceIsValid(inviteResource))
                .count() > 0) {
            return false;
        } else {
            return true;
        }
    }

    private boolean inviteResourceIsValid(InviteResource inviteResource) {

        if (StringUtils.isEmpty(inviteResource.getEmail()) || StringUtils.isEmpty(inviteResource.getName()) || inviteResource.getApplication() == null) {
            return false;
        }

        return true;
    }
}
