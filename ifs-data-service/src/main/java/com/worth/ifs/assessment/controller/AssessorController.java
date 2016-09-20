package com.worth.ifs.assessment.controller;

import com.worth.ifs.assessment.transactional.AssessorService;
import com.worth.ifs.assessment.transactional.CompetitionInviteService;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.transactional.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Exposes CRUD operations through a REST API to manage assessor related data.
 */

@RestController
@RequestMapping("/assessor")
public class AssessorController {
    @Autowired
    private AssessorService assessorService;

    @RequestMapping(value= "/register/{hash}", method = POST)
    public RestResult<UserResource> registerAssessorByHash(@PathVariable("hash") final String hash, @RequestBody UserResource userResource) {
        //TODO: Upon successful creation user should be logged in automatically

        return assessorService.registerAssessorByHash(hash, userResource).toGetResponse();
    }
}