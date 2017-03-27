package org.innovateuk.ifs.invite.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.transactional.InviteOrganisationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inviteorganisation")
public class InviteOrganisationController {

    @Autowired
    private InviteOrganisationService service;

    @GetMapping(value = "/{id}")
    public RestResult<InviteOrganisationResource> getById(@PathVariable("id") long id) {
        return service.getById(id).toGetResponse();
    }

    @GetMapping(value = "/organisation/{organisationId}/application/{applicationId}")
    public RestResult<InviteOrganisationResource> getByOrganisationIdWithInvitesForApplication(@PathVariable("organisationId") long organisationId,
                                                                                               @PathVariable("applicationId") long applicationId) {
        return service.getByOrganisationIdWithInvitesForApplication(organisationId, applicationId).toGetResponse();
    }

    @PutMapping(value = "/save")
    public RestResult<Void> put(@RequestBody InviteOrganisationResource inviteOrganisationResource) {
        if (service.getById(inviteOrganisationResource.getId()).isSuccess()) {
            return service.save(inviteOrganisationResource).toPutResponse();
        } else {
            return RestResult.restFailure(HttpStatus.NOT_ACCEPTABLE);
        }
    }
}
