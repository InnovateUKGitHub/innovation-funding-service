package com.worth.ifs.invite.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.transactional.InviteOrganisationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inviteorganisation")
public class InviteOrganisationController {

    @Autowired
    private InviteOrganisationService service;

    @RequestMapping("/{id}")
    public RestResult<InviteOrganisationResource> findById(@PathVariable("id") final Long id) {
        return service.findOne(id).toGetResponse();
    }

    @RequestMapping(value= "/save", method = RequestMethod.PUT)
    public RestResult<Void> put(@RequestBody final InviteOrganisationResource inviteOrganisationResource) {
        if(service.findOne(inviteOrganisationResource.getId()).isSuccess()){
            return service.save(inviteOrganisationResource).toPutResponse();
        }else{
            return RestResult.restFailure(HttpStatus.NOT_ACCEPTABLE);
        }
    }
}