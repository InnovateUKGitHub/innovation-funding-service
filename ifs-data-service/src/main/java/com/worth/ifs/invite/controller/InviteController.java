package com.worth.ifs.invite.controller;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.commons.resource.ResourceEnvelope;
import com.worth.ifs.commons.resource.ResourceEnvelopeConstants;
import com.worth.ifs.invite.domain.Invite;
import com.worth.ifs.invite.domain.InviteOrganisation;
import com.worth.ifs.invite.constant.InviteStatusConstants;
import com.worth.ifs.invite.repository.InviteOrganisationRepository;
import com.worth.ifs.invite.repository.InviteRepository;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.resource.InviteResource;
import com.worth.ifs.invite.service.InviteRestServiceImpl;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.repository.OrganisationRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * InviteController is to handle the REST calls from the web-service and contains the handling of all call involving the Invite and InviteOrganisations.
 */

@RestController
@RequestMapping("/invite")
public class InviteController {
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    InviteOrganisationRepository inviteOrganisationRepository;

    @Autowired
    InviteRepository inviteRepository;

    @Autowired
    OrganisationRepository organisationRepository;

    @Autowired
    ApplicationRepository applicationRepository;

    @Autowired
    InviteRestServiceImpl inviteRestService;

    @RequestMapping("/createApplicationInvites")
    public ResourceEnvelope<InviteOrganisationResource> createApplicationInvites(@RequestBody InviteOrganisationResource inviteOrganisationResource) {
        InviteOrganisation newInviteOrganisation = assembleInviteOrganisationFromResource(inviteOrganisationResource);
        List<Invite> newInvites = assembleInvitesFromInviteOrganisationResource(inviteOrganisationResource, newInviteOrganisation);
        InviteOrganisation createdInviteOrganisation = inviteOrganisationRepository.save(newInviteOrganisation);
        inviteRepository.save(newInvites);

        ResourceEnvelope<InviteOrganisationResource> resourceEnvelope = new ResourceEnvelope<>(ResourceEnvelopeConstants.OK.getName(), new ArrayList<>(), new InviteOrganisationResource());

        return resourceEnvelope;
    }

    @RequestMapping("/getInvitesByApplicationId/{applicationId}")
    public Set<InviteOrganisationResource> getInvitesByApplication(@PathVariable("applicationId") Long applicationId) {
        HashSet<InviteOrganisationResource> results = new HashSet<>();
        List<Invite> invites = inviteRepository.findByApplicationId(applicationId);
        invites.stream().forEach(i -> {
            results.add(new InviteOrganisationResource(i.getInviteOrganisation()));
        });
        return results;
    }

    private InviteOrganisation assembleInviteOrganisationFromResource(InviteOrganisationResource inviteOrganisationResource) {
        Organisation organisation = null;
        if (inviteOrganisationResource.getOrganisationId() != null) {
            organisation = organisationRepository.findOne(inviteOrganisationResource.getOrganisationId());
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
        Application application = applicationRepository.findOne(inviteResource.getApplicationId());
        Invite invite = new Invite(inviteResource.getName(), inviteResource.getEmail(), application, newInviteOrganisation, null, InviteStatusConstants.CREATED);

        return invite;
    }
}
