package org.innovateuk.ifs.invite.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.RejectionReasonResource;
import org.innovateuk.ifs.invite.transactional.RejectionReasonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Exposes CRUD operations through a REST API to manage {@link org.innovateuk.ifs.invite.domain.RejectionReason} related data.
 */
@RestController
@RequestMapping("/rejectionReason")
public class RejectionReasonController {

    @Autowired
    private RejectionReasonService rejectionReasonService;

    @GetMapping("/findAllActive")
    public RestResult<List<RejectionReasonResource>> findAllActive() {
        return rejectionReasonService.findAllActive().toGetResponse();
    }
}
