package com.worth.ifs.invite.controller;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.commons.resource.ResourceEnvelope;
import com.worth.ifs.commons.resource.ResourceEnvelopeConstants;
import com.worth.ifs.invite.domain.Invite;
import com.worth.ifs.invite.domain.InviteOrganisation;
import com.worth.ifs.invite.domain.InviteStatus;
import com.worth.ifs.invite.repository.InviteOrganisationRepository;
import com.worth.ifs.invite.repository.InviteRepository;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.resource.InviteResource;
import com.worth.ifs.invite.service.InviteRestServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/invite")
public class InviteController {
    @Autowired
    InviteOrganisationRepository inviteOrganisationRepository;

    @Autowired
    InviteRepository inviteRepository;

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

    private InviteOrganisation assembleInviteOrganisationFromResource(InviteOrganisationResource inviteOrganisationResource) {
        InviteOrganisation newInviteOrganisation = new InviteOrganisation(inviteOrganisationResource.getOrganisationName(), inviteOrganisationResource.getOrganisation(), null);

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
        Invite invite = new Invite(inviteResource.getName(), inviteResource.getEmail(), application, newInviteOrganisation, null, InviteStatus.CREATED);

        return invite;
    }
}
