package com.worth.ifs.invite.controller;

import com.worth.ifs.invite.mapper.InviteOrganisationMapper;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.transactional.InviteOrganisationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inviteorganisation")
public class InviteOrganisationController {
    @Autowired
    private InviteOrganisationService service;

    @Autowired
    private InviteOrganisationMapper mapper;

    @RequestMapping("/{id}")
    public InviteOrganisationResource findById(@PathVariable("id") final Long id) {
        return mapper.mapInviteOrganisationToResource(service.findOne(id));
    }
}