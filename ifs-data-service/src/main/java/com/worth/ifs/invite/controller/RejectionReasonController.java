package com.worth.ifs.invite.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.invite.resource.RejectionReasonResource;
import com.worth.ifs.invite.transactional.RejectionReasonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Exposes CRUD operations through a REST API to manage {@link com.worth.ifs.invite.domain.RejectionReason} related data.
 */
@RestController
@RequestMapping("/rejectionReason")
public class RejectionReasonController {

    @Autowired
    private RejectionReasonService rejectionReasonService;

    @RequestMapping(value = "/findAllActive", method = RequestMethod.GET)
    public RestResult<List<RejectionReasonResource>> findAllActive() {
        return rejectionReasonService.findAllActive().toGetResponse();
    }
}