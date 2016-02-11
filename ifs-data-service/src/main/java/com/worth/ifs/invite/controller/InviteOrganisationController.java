package com.worth.ifs.invite.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.transactional.InviteOrganisationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.worth.ifs.commons.rest.RestResultBuilder.newRestHandler;

@RestController
@RequestMapping("/inviteorganisation")
public class InviteOrganisationController {

    @Autowired
    private InviteOrganisationService service;

    @RequestMapping("/{id}")
    public RestResult<InviteOrganisationResource> findById(@PathVariable("id") final Long id) {
        return newRestHandler().perform(() -> service.findOne(id));
    }
}